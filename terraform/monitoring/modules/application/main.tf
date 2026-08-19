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

  key_name = data.aws_key_pair.existing.key_name

  root_block_device {
    volume_type           = var.volume_type
    volume_size           = var.volume_size
    delete_on_termination = true
    encrypted             = true # EBS 볼륨 암호화 (무료)
  }

  # IMDSv2 설정 (SSRF 공격 방어)
  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required"
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
