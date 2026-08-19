output "ec2_id" {
  description = "The ID of the monitoring EC2 instance"
  value       = module.application.ec2_id
}

output "monitoring_sg_id" {
  description = "Security Group ID for the monitoring instance"
  value       = module.application.monitoring_sg_id
}

output "eip_public_ip" {
  description = "The Elastic IP associated with the monitoring instance"
  value       = module.application.eip_public_ip
}

output "loki_logs_bucket_name" {
  description = "S3 bucket name used as Loki's persistent storage backend"
  value       = module.application.loki_logs_bucket_name
}
