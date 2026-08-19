variable "project_name" {
  type        = string
  description = "Project name"
}

variable "environment" {
  type        = string
  description = "Environment name (e.g., dev, prod)"
}

variable "base_tags" {
  type        = map(string)
  description = "Base tags to apply to resources"
}

variable "instance_type" {
  type        = string
  description = "EC2 instance type (e.g., t4g.small)"
}

variable "volume_type" {
  type        = string
  description = "EBS volume type (e.g., gp2, gp3)"
}

variable "volume_size" {
  type        = number
  description = "EBS volume size in GiB"
}

variable "network_project_name" {
  type        = string
  description = "project_name used when tagging the existing backend VPC/subnet (courseitda-vpc, courseitda-public-a, courseitda-key-pair), used to look them up as data sources"
}
