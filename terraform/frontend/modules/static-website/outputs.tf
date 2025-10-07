output "bucket_name" {
  value       = aws_s3_bucket.bucket.bucket
  description = "S3 bucket name"
}

output "bucket_arn" {
  value       = aws_s3_bucket.bucket.arn
  description = "S3 bucket ARN"
}

output "cloudfront_domain" {
  value       = aws_cloudfront_distribution.cdn.domain_name
  description = "CloudFront distribution domain name"
}

output "cloudfront_id" {
  value       = aws_cloudfront_distribution.cdn.id
  description = "CloudFront distribution ID"
}

output "acm_certificate_arn" {
  value       = aws_acm_certificate.acm_certificate.arn
  description = "ACM Certificate ARN"
}

output "acm_certificate_status" {
  value       = aws_acm_certificate.acm_certificate.status
  description = "ACM Certificate Status"
}

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

output "website_url" {
  value       = "https://${var.fqdn}"
  description = "Web site URL"
}
