output "ec2_id" {
  value = aws_instance.monitoring_instance.id
}

output "monitoring_sg_id" {
  description = "Security Group ID for the monitoring instance"
  value       = aws_security_group.monitoring_sg.id
}

output "ami_id" {
  description = "ID of the AMI used for the monitoring instance"
  value       = data.aws_ami.monitoring_ami.id
}

output "eip_public_ip" {
  description = "Public IP (Elastic IP) associated with the monitoring instance"
  value       = aws_eip.monitoring_eip.public_ip
}
