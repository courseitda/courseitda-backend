# EC2 AMI 조회
# AWS EC2 DescribeImages API를 호출해서 원하는 AMI 조회
data "aws_ami" "app_ami" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-arm64-server-*"]
  }
}

# EC2 보안 그룹
resource "aws_security_group" "app_sg" {
  name   = "${var.project_name}-${var.environment}-ec2-sg"
  vpc_id = var.vpc_id

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

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"] # 전부 허용
  }

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-ec2-sg"
  }
}

# 앱이 실행될 EC2 인스턴스
resource "aws_instance" "app_instance" {
  ami           = data.aws_ami.app_ami.id # data 블록 참조
  instance_type = var.instance_type

  subnet_id              = var.public_subnet_id
  vpc_security_group_ids = [aws_security_group.app_sg.id]

  key_name = var.key_pair_name

  root_block_device {
    volume_type           = var.volume_type # SSD 타입
    volume_size           = var.volume_size # 용량(GiB)
    delete_on_termination = true
    encrypted             = true # EBS 볼륨 암호화 (무료)
  }

  # IMDSv2 설정 (SSRF 공격 방어)
  # 메타데이터 관련 엔드포인트에 접근해 IAM Role의 인증 토큰을 탈취할 수 있는 보안 취약점 방어
  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required"
  }

  # 초기 부팅 시점에 user_data 스크립트 실행
  # templatefile()를 사용하면 ${}로 테라폼 변수를 tpl파일에 주입해서 사용 가능. 대신 쉘 스크립트에서 원래의 ${}은 $${}로 사용해야 함.
  # file()을 사용하면 tpl 파일 내부에서 테라폼 변수는 사용 불가능. 대신 쉘 스크립트에서 ${} 그대로 사용 가능.
  # filebase64()를 사용하면 Base64 인코딩 과정에서 개행 문자의 차이(\n vs \r\n)를 모두 같은 바이트 시퀀스로 처리.
  user_data_base64 = filebase64("${path.module}/user_data.tpl")

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-app"
  }
}

# EC2가 사용할 고정 public IP
resource "aws_eip" "app_eip" {
  domain = "vpc"

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-${var.environment}-app-eip"
  }
}

resource "aws_eip_association" "app_eip_association" {
  instance_id   = aws_instance.app_instance.id
  allocation_id = aws_eip.app_eip.id
}
