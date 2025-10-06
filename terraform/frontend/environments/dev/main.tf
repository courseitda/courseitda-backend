# 1. S3 버킷 생성
resource "aws_s3_bucket" "bucket" {
  bucket = "${var.project_name}-${var.area}-${var.environment}" # e.g. courseitda-frontend-dev

  tags = local.tags
}

# S3 버킷 생명 주기 정책 추가
resource "aws_s3_bucket_lifecycle_configuration" "bucket_lifecycle_config" {
  bucket = aws_s3_bucket.bucket.id

  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"

    # 만료 기간 후에 이전 버전 삭제
    noncurrent_version_expiration {
      noncurrent_days = 7
    }

    # 불완전한 멀티파트 업로드 파일 정리
    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}

# CloudFront로만 접근 가능해야 하므로, public access 전면 차단
resource "aws_s3_bucket_public_access_block" "public_access_block" {
  bucket                  = aws_s3_bucket.bucket.id
  block_public_acls       = true
  block_public_policy     = true
  restrict_public_buckets = true
  ignore_public_acls      = true
}

# 롤백을 위한 버전 관리
resource "aws_s3_bucket_versioning" "versioning" {
  bucket = aws_s3_bucket.bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "encryption_config" {
  bucket = aws_s3_bucket.bucket.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# 2. ACM 인증서 발급
resource "aws_acm_certificate" "acm_certificate" {
  provider          = aws.use1
  domain_name       = local.fqdn
  validation_method = "DNS"

  tags = local.tags
}

# CloudFront OAC 설정
# OAC: CloudFront와 S3 버킷 간의 안전한 연결을 설정하는 보안 기능
# S3 버킷에 외부 사용자가 public으로 직접 접근하는 것을 차단하고, CloudFront를 통해서만 콘텐츠에 접근할 수 있도록 제어
resource "aws_cloudfront_origin_access_control" "oac" {
  name                              = "${var.project_name}-${var.environment}-oac"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always" # 모든 요청에 서명 추가 요구
  signing_protocol                  = "sigv4"  # 서명 프로토콜 버전
}

# 외부 DNS에 CNAME 추가 후, 인증서가 'ISSUED' 상태가 되면 배포 진행
data "aws_acm_certificate" "issued_certificate" {
  provider   = aws.use1
  domain     = local.fqdn
  statuses   = ["ISSUED"]
  depends_on = [aws_acm_certificate.acm_certificate]
}

# 3. CloudFront 배포
resource "aws_cloudfront_distribution" "cdn" {
  enabled             = true                                     # 배포를 활성화 상태로 생성
  comment             = "${var.project_name}-${var.environment}" # (선택) 배포에 대한 설명
  default_root_object = "index.html"                             # 루트 URL 접근 시 자동으로 반환할 기본 파일. 설정하지 않으면 403 상태 코드 응답.
  aliases             = [local.fqdn]                             # CloudFront에서 사용할 커스텀 도메인(CNAME) 목록

  origin {
    domain_name              = aws_s3_bucket.bucket.bucket_regional_domain_name # CloudFront가 가져올 정적 리소스의 원본 주소
    origin_id                = "s3Origin"                                       # 이 origin을 식별하는 고유 ID (자유롭게 설정)
    origin_access_control_id = aws_cloudfront_origin_access_control.oac.id      # CloudFront가 S3에 서명된 요청을 보내도록 함
  }

  default_cache_behavior {
    target_origin_id       = "s3Origin"                             # 캐시에서 사용할 origin 지정. 위의 origin 블록의 origin_id와 동일해야 함.
    viewer_protocol_policy = "redirect-to-https"                    # HTTP 요청을 HTTPS로 리다이렉트
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]             # CORS preflight 지원
    cached_methods         = ["GET", "HEAD"]                        # 캐싱할 HTTP 메서드 목록. GET과 HEAD 요청에 대한 응답만 캐싱.
    cache_policy_id        = "658327ea-f89d-4fab-a63d-7e88639e58f6" # AWS 기본 관리형 캐시 정책(CachingOptimized)의 id. 최대 캐시 효율성을 보여주는 정책.
    compress               = true                                   # CloudFront가 자동으로 압축 (gzip, brotli). 텍스트 기반 파일(HTML, CSS, JS)에 효과적.
  }

  # SPA 라우팅 문제를 해결하기 위한 설정
  # 1. S3에 없는 경로 요청 시 발생하는 403, 404 에러 응답을 가로챔
  # 2. 403, 404 응답 대신 index.html 반환
  # 3. 브라우저에게는 200 OK로 위장
  # 4. React Router가 URL을 보고, 올바른 컴포넌트 렌더링
  custom_error_response {
    error_code            = 403
    response_code         = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 0 # 에러 응답을 캐싱하는 최소 시간. 0: 캐싱하지 않음.
  }

  custom_error_response {
    error_code            = 404
    response_code         = 200
    response_page_path    = "/index.html"
    error_caching_min_ttl = 0
  }

  restrictions {
    geo_restriction {
      restriction_type = "none" # 모든 국가에서 접근 허용
    }
  }

  viewer_certificate {
    acm_certificate_arn      = data.aws_acm_certificate.issued_certificate.arn # 사용할 ACM 인증서의 ARN
    ssl_support_method       = "sni-only"                                      # SNI(Server Name Indication) 방식으로 SSL 처리
    minimum_protocol_version = "TLSv1.2_2021"                                  # 지원할 최소 TLS 버전. TLSv1.3은 일부 구형 브라우저 미지원.
  }

  depends_on = [data.aws_acm_certificate.issued_certificate] # depends_on 으로 ACM 인증서 리소스 생성 후, 인증서 정보 조회를 보장

  tags = local.tags
}

# S3 버킷 정책: CloudFront OAC만 읽기 허용
data "aws_iam_policy_document" "bucket_policy_document" {
  statement {
    sid       = "AllowCloudFrontRead"
    actions   = ["s3:GetObject"]
    resources = ["${aws_s3_bucket.bucket.arn}/*"]
    principals {
      type        = "Service"
      identifiers = ["cloudfront.amazonaws.com"]
    }
    condition {
      test     = "StringEquals"
      variable = "AWS:SourceArn"
      values   = [aws_cloudfront_distribution.cdn.arn]
    }
  }
}

resource "aws_s3_bucket_policy" "bucket_policy" {
  bucket = aws_s3_bucket.bucket.id
  policy = data.aws_iam_policy_document.bucket_policy_document.json
}

