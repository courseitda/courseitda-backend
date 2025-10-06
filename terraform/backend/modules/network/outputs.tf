output "vpc_id" {
  description = "The ID of the created VPC"
  value       = aws_vpc.vpc.id
}

output "vpc_tags" {
  description = "Tags applied to the VPC"
  value       = aws_vpc.vpc.tags
}

output "public_subnet_a_id" {
  description = "The ID of the public subnet a in availability zone A"
  value       = aws_subnet.public_a.id
}

output "private_subnet_a_id" {
  description = "The ID of the private subnet a in availability zone A"
  value       = aws_subnet.private_a.id
}

output "private_subnet_a_availability_zone" {
  description = "The availability zone of the private subnet a"
  value       = aws_subnet.private_a.availability_zone
}

output "public_subnet_b_id" {
  description = "The ID of the public subnet b in availability zone B"
  value       = aws_subnet.public_b.id
}

output "private_subnet_b_id" {
  description = "The ID of the private subnet b in availability zone B"
  value       = aws_subnet.private_b.id
}

output "private_subnet_b_availability_zone" {
  description = "The availability zone of the subnet private b"
  value       = aws_subnet.private_b.availability_zone
}
