output "rds_endpoint" {
  # ex. db-instance.abcdefghijk.ap-northeast-2.rds.amazonaws.com
  value = aws_db_instance.db.endpoint
}

output "rds_id" {
  description = "ID for RDS"
  value       = aws_db_instance.db.id
}

output "db_sg_id" {
  description = "Security Group ID for the database"
  value       = aws_security_group.db_sg.id
}
