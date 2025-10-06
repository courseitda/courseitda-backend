# RSA 개인 키 생성
resource "tls_private_key" "private_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# AWS Key Pair 생성 (SSH 용도)
resource "aws_key_pair" "key_pair" {
  key_name   = "${var.project_name}-key-pair"
  public_key = tls_private_key.private_key.public_key_openssh
}

# 생성된 개인 키를 로컬에 .pem 파일로 저장
resource "local_file" "private_key" {
  filename        = "${path.root}/keys/private_key.pem"
  content         = tls_private_key.private_key.private_key_pem
  file_permission = "0600"
}

# S3 버킷 생성 (개인 키 저장용)
resource "aws_s3_bucket" "key_storage" {
  bucket = var.s3_bucket_name

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-key-storage"
  }
}

# S3 버킷 버전 관리 활성화
resource "aws_s3_bucket_versioning" "key_storage_versioning" {
  bucket = aws_s3_bucket.key_storage.id

  versioning_configuration {
    status = "Enabled"
  }
}

# S3 버킷 공개 접근 차단
resource "aws_s3_bucket_public_access_block" "key_storage_public_access_block" {
  bucket = aws_s3_bucket.key_storage.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# S3 버킷 암호화 설정
resource "aws_s3_bucket_server_side_encryption_configuration" "key_storage_encryption" {
  bucket = aws_s3_bucket.key_storage.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# 개인 키를 S3에 업로드
resource "aws_s3_object" "private_key" {
  bucket  = aws_s3_bucket.key_storage.id
  key     = "keys/${var.project_name}-${var.environment}-private-key.pem"
  content = tls_private_key.private_key.private_key_pem

  server_side_encryption = "AES256"

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-private-key"
  }
}
