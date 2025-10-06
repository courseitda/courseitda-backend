# DNS 검증 레코드(CNAME) 정보
# Route53에서 발급 받은 것이 아닌, 외부 DNS이므로 레코드 수동 등록 필요
# 아래 output 정보를 토대로 외부 DNS에 레코드 추가
output "acm_dns_validation_records" {
  value = [
    for dvo in aws_acm_certificate.acm_certificate.domain_validation_options : {
      name  = dvo.resource_record_name
      type  = dvo.resource_record_type
      value = dvo.resource_record_value
    }
  ]
  description = "Add the following CNAME records to your external DNS provider (e.g., Gabia). Continue after the certificate is issued."
}

output "bucket_name" {
  value = aws_s3_bucket.bucket.bucket
}

output "cloudfront_domain" {
  value = aws_cloudfront_distribution.cdn.domain_name
}

output "cloudfront_id" {
  value = aws_cloudfront_distribution.cdn.id
}

output "acm_certificate_arn" {
  value       = aws_acm_certificate.acm_certificate.arn
  description = "ACM Certificate ARN"
}

# ACM 발급 여부 확인을 위한 output
output "acm_certificate_status" {
  value       = aws_acm_certificate.acm_certificate.status
  description = "ACM Certificate Status"
}

# 외부 사용자 접속 URL
output "website_url" {
  value       = "https://${local.fqdn}"
  description = "Web site URL"
}
