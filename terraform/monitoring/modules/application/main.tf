# --- 기존 backend 테라폼(terraform/backend)이 만든 리소스를 태그/이름으로 조회 ---
# backend state를 직접 참조하지 않고 data source로만 조회하므로 terraform/backend는 전혀 건드리지 않음

data "aws_vpc" "existing" {
  filter {
    name   = "tag:Name"
    values = ["${var.network_project_name}-vpc"]
  }
}

# 기존 앱 서버와 동일하게 AZ-a public subnet 사용
data "aws_subnet" "existing_public_a" {
  vpc_id = data.aws_vpc.existing.id

  filter {
    name   = "tag:Name"
    values = ["${var.network_project_name}-public-a"]
  }
}

data "aws_key_pair" "existing" {
  key_name = "${var.network_project_name}-key-pair"
}

# EC2 AMI 조회 (backend application 모듈과 동일: Ubuntu 24.04 ARM64)
data "aws_ami" "monitoring_ami" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-arm64-server-*"]
  }
}

# Loki 로그(청크/인덱스)를 저장할 S3 버킷 (영속 스토리지 백엔드)
resource "aws_s3_bucket" "loki_logs" {
  bucket = "${var.project_name}-${var.environment}-loki-logs"

  tags = merge(var.base_tags, {
    Name = "${var.project_name}-${var.environment}-loki-logs"
  })
}

resource "aws_s3_bucket_public_access_block" "loki_logs_public_access_block" {
  bucket = aws_s3_bucket.loki_logs.id

  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "loki_logs_encryption" {
  bucket = aws_s3_bucket.loki_logs.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}
# 주의: 버킷에 수명주기(lifecycle expiration) 규칙을 걸지 않음.
# 보존 기간(30일)은 Loki compactor(retention_enabled)가 인덱스와 청크를 함께 정리하는 방식으로 관리해야 함.
# S3 lifecycle로 청크만 따로 지우면 인덱스와 어긋나서 조회 오류가 날 수 있음.

# 모니터링 EC2가 access key 없이 위 S3 버킷에 접근할 수 있도록 하는 IAM Role
resource "aws_iam_role" "monitoring_instance_role" {
  name = "${var.project_name}-${var.environment}-monitoring-instance-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = { Service = "ec2.amazonaws.com" }
        Action    = "sts:AssumeRole"
      }
    ]
  })

  tags = var.base_tags
}

# 최소 권한: 이 모니터링 서버가 만든 Loki 버킷 하나에만 접근 허용
resource "aws_iam_role_policy" "loki_s3_access" {
  name = "${var.project_name}-${var.environment}-loki-s3-access"
  role = aws_iam_role.monitoring_instance_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["s3:ListBucket"]
        Resource = [aws_s3_bucket.loki_logs.arn]
      },
      {
        Effect   = "Allow"
        Action   = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"]
        Resource = ["${aws_s3_bucket.loki_logs.arn}/*"]
      }
    ]
  })
}

resource "aws_iam_instance_profile" "monitoring_instance_profile" {
  name = "${var.project_name}-${var.environment}-monitoring-instance-profile"
  role = aws_iam_role.monitoring_instance_role.name
}

# 모니터링 EC2 보안 그룹
resource "aws_security_group" "monitoring_sg" {
  name   = "${var.project_name}-${var.environment}-monitoring-sg"
  vpc_id = data.aws_vpc.existing.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # 학습 편의를 위해 SSH 포트 허용
    description = "Allow SSH traffic"
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow HTTP traffic"
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow HTTPS traffic"
  }

  ingress {
    from_port   = 3001
    to_port     = 3001
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow Grafana traffic"
  }

  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow Prometheus traffic"
  }

  ingress {
    from_port   = 3100
    to_port     = 3100
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow Loki traffic"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"] # 전부 허용
  }

  tags = merge(var.base_tags, {
    Name = "${var.project_name}-monitoring-sg"
  })
}

# 모니터링 스택(Grafana/Prometheus/Loki)이 실행될 EC2 인스턴스
resource "aws_instance" "monitoring_instance" {
  ami           = data.aws_ami.monitoring_ami.id
  instance_type = var.instance_type

  subnet_id              = data.aws_subnet.existing_public_a.id
  vpc_security_group_ids = [aws_security_group.monitoring_sg.id]

  key_name             = data.aws_key_pair.existing.key_name
  iam_instance_profile = aws_iam_instance_profile.monitoring_instance_profile.name

  root_block_device {
    volume_type           = var.volume_type
    volume_size           = var.volume_size
    delete_on_termination = true
    encrypted             = true # EBS 볼륨 암호화 (무료)
  }

  # IMDSv2 설정 (SSRF 공격 방어)
  # http_put_response_hop_limit = 2: 기본값(1)이면 Docker 컨테이너(브리지 네트워크가 홉을 하나 더 소모)에서
  # 메타데이터 서버에 접근이 막혀 Loki 컨테이너가 이 IAM Role 자격증명을 못 받아옴
  metadata_options {
    http_endpoint               = "enabled"
    http_tokens                 = "required"
    http_put_response_hop_limit = 2
  }

  # 초기 부팅 시점에 user_data 스크립트 실행 (Docker 설치 + swap 구성)
  user_data_base64 = filebase64("${path.module}/user_data.tpl")

  tags = merge(var.base_tags, {
    Name = "${var.project_name}-${var.environment}-monitoring"
  })
}

# EC2가 사용할 고정 public IP (backend application 모듈과 동일하게 신규 발급)
resource "aws_eip" "monitoring_eip" {
  domain = "vpc"

  tags = merge(var.base_tags, {
    Name = "${var.project_name}-${var.environment}-monitoring-eip"
  })
}

resource "aws_eip_association" "monitoring_eip_association" {
  instance_id   = aws_instance.monitoring_instance.id
  allocation_id = aws_eip.monitoring_eip.id
}
