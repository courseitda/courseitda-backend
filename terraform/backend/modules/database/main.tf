resource "aws_security_group" "db_sg" {
  name   = "${var.project_name}-${var.environment}-db-sg"
  vpc_id = var.vpc_id

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = var.ingress_security_group_ids
  }

  egress {
    from_port   = 0
    to_port     = 0             # port range 0-0: 모든 포트를 의미
    protocol    = "-1"          # -1: 모든 프로토콜을 의미
    cidr_blocks = ["0.0.0.0/0"] # 모든 ip 허용
  }

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-db-sg"
  }
}

# RDS Subnet Group
resource "aws_db_subnet_group" "db_subnet_group" {
  name       = "${var.project_name}-${var.environment}-db-subnet-group"
  subnet_ids = var.private_subnet_ids

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-db-subnet"
  }
}

# RDS 파라미터 그룹 (문자셋, 타임존 설정)
resource "aws_db_parameter_group" "db_param_group" {
  name   = "${var.project_name}-${var.environment}-db-params"
  family = "mysql8.4"

  # 한글 및 이모지 지원
  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_client"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_connection"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_database"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_results"
    value = "utf8mb4"
  }

  parameter {
    name  = "collation_server"
    value = "utf8mb4_unicode_ci"
  }

  # 한국 시간대 설정
  parameter {
    name  = "time_zone"
    value = "Asia/Seoul"
  }

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-${var.environment}-db-params"
  }
}

# RDS 인스턴스
resource "aws_db_instance" "db" {
  db_name        = var.db_name
  identifier     = "${var.project_name}-${var.environment}-db-instance"
  engine         = "mysql"
  engine_version = "8.4.5"
  instance_class = var.db_instance_class

  storage_type      = "gp2"
  allocated_storage = var.db_allocated_storage

  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.db_subnet_group.name
  parameter_group_name   = aws_db_parameter_group.db_param_group.name
  availability_zone      = "${var.region}a" # AZ-a 고정
  vpc_security_group_ids = [aws_security_group.db_sg.id]

  multi_az                = false                          # Multi-AZ 불가 (비용 절감)
  backup_retention_period = var.db_backup_retention_period # 무료 자동 백업 7일
  deletion_protection     = false
  skip_final_snapshot     = true
  publicly_accessible     = false # 보안상 내부 전용
  storage_encrypted       = true

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-rds"
  }
}
