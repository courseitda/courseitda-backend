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

variable "s3_bucket_name" {
  type        = string
  description = "S3 bucket name for storing private key"
}
