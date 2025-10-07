variable "region" {
  type        = string
  description = "AWS region (e.g., ap-northeast-2)"
}

variable "project_name" {
  type        = string
  description = "Project name"
}

variable "environment" {
  type        = string
  description = "Environment name (e.g., dev, prod)"
}

variable "vpc_id" {
  type        = string
  description = "VPC ID where RDS will be deployed"
}

variable "availability_zone" {
  type        = string
  description = "Availability zone where RDS instance will be deployed (e.g., ap-northeast-2a)"
}

variable "private_subnet_ids" {
  type        = list(string)
  description = "List of private subnet IDs for RDS subnet group (minimum 2 subnets in different AZs)"
}

variable "db_name" {
  type        = string
  description = "Name of the database to create"
}

variable "db_instance_class" {
  type        = string
  description = "RDS instance class (e.g., db.t4g.micro)"
}

variable "db_allocated_storage" {
  type        = number
  description = "RDS storage size in GiB"
}

variable "db_backup_retention_period" {
  type        = number
  description = "Automated backup retention period in days"
}

variable "ingress_security_group_ids" {
  type        = list(string)
  description = "List of security group IDs allowed to access RDS"
}

variable "db_username" {
  type        = string
  description = "RDS master username"
  sensitive   = true
}

variable "db_password" {
  type        = string
  description = "RDS master password"
  sensitive   = true
}
