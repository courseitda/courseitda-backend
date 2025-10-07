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
