variable "project_name" {
  type        = string
  description = "Project name"
}

variable "environment" {
  type        = string
  description = "Environment name (e.g., dev, prod)"
}

variable "instance_type" {
  type        = string
  description = "EC2 instance type (e.g., t4g.micro)"
}

variable "vpc_id" {
  type        = string
  description = "VPC ID where EC2 will be deployed"
}

variable "public_subnet_id" {
  type        = string
  description = "Public subnet ID where EC2 will be deployed"
}

variable "volume_type" {
  type        = string
  description = "EBS volume type (e.g., gp2, gp3)"
}

variable "volume_size" {
  type        = number
  description = "EBS volume size in GiB"
}

variable "key_pair_name" {
  type        = string
  description = "Key pair name for SSH access to EC2"
}
