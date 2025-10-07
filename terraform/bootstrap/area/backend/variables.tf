variable "region" {
  type        = string
  description = "AWS region"
  default     = "ap-northeast-2"
}

variable "project_name" {
  type        = string
  description = "Project name"
  default     = "courseitda"
}

variable "area" {
  type        = string
  description = "Defines the work area of the project (e.g., frontend, backend)"
  default     = "backend"
}
