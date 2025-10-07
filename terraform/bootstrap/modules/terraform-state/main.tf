# S3 Bucket
resource "aws_s3_bucket" "bucket" {
  for_each = toset(var.environments)

  bucket              = "${var.project_name}-${var.area}-${each.key}-terraform-state"
  object_lock_enabled = true

  tags = {
    Project     = var.project_name
    Environment = each.key
  }
}

# Versioning
resource "aws_s3_bucket_versioning" "versioning" {
  for_each = aws_s3_bucket.bucket

  bucket = each.value.id
  versioning_configuration {
    # 동일한 객체 키(terraform.tfstate)가 여러 번 업로드되면, 이전 버전이 삭제되지 않고 남아있음. 상태 파일 롤백/복구를 위한 기능.
    status = "Enabled"
  }
}

resource "aws_s3_bucket_public_access_block" "public_access" {
  for_each = aws_s3_bucket.bucket

  bucket = each.value.id
  # IAM 인증만 허용하기 위한 설정
  block_public_acls       = true # 버킷이나 객체에 public ACL을 설정 추가 시도를 차단
  ignore_public_acls      = true # public ACL이 이미 설정되어 있어도 무시
  block_public_policy     = true # public 접근을 허용하는 정책 설정 추가 시도를 차단
  restrict_public_buckets = true # public 접근을 허용하는 정책이 이미 존재해도 무시
}

# Object Lock Configuration
resource "aws_s3_bucket_object_lock_configuration" "object_lock_config" {
  for_each = aws_s3_bucket.bucket

  bucket = each.value.id
  rule {
    default_retention {
      mode = "GOVERNANCE" # 관리자 권한 사용자는 retention을 무시하고 객체 삭제/수정 가능 <-> "COMPLIANCE": retention 기간이 끝날 때까지 관리자도 객체 삭제/수정 불가능
      days = 1            # 객체 생성 이후, 삭제/수정이 불가능한 기간
    }
  }
}
