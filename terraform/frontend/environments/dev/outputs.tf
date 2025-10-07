# DNS 검증 레코드(CNAME) 정보
# Route53에서 발급 받은 것이 아닌, 외부 DNS이므로 레코드 수동 등록 필요
# 아래 output 정보를 토대로 외부 DNS에 레코드 추가
output "acm_dns_validation_records" {
  value       = module.static_website.acm_dns_validation_records
  description = "Add the following CNAME records to your external DNS provider (e.g., Gabia). Continue after the certificate is issued."
}

output "bucket_name" {
  value = module.static_website.bucket_name
}

output "cloudfront_domain" {
  value = module.static_website.cloudfront_domain
}

output "cloudfront_id" {
  value = module.static_website.cloudfront_id
}

output "acm_certificate_arn" {
  value       = module.static_website.acm_certificate_arn
  description = "ACM Certificate ARN"
}

# ACM 발급 여부 확인을 위한 output
output "acm_certificate_status" {
  value       = module.static_website.acm_certificate_status
  description = "ACM Certificate Status"
}

# 외부 사용자 접속 URL
output "website_url" {
  value       = module.static_website.website_url
  description = "Web site URL"
}
