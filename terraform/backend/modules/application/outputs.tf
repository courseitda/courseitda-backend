output "ec2_id" {
  value = aws_instance.app_instance.id
}

output "app_sg_id" {
  description = "Security Group ID for the application instances"
  value       = aws_security_group.app_sg.id
}

output "ami_id" {
  description = "ID of the AMI used in the Launch Template"
  value       = data.aws_ami.app_ami.id
}
