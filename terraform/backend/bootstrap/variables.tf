variable "project_name" {
  type        = string
  description = "Project name"
  default     = "courseitda"
}

variable "region" {
  type        = string
  description = "AWS region"
  default     = "ap-northeast-2"
}

variable "area" {
  type        = string
  description = "Defines the work area of the project (e.g., frontend or backend)"
  default     = "backend"
}

variable "environments" {
  type        = list(string)
  description = "List of deployment environments"
  default     = ["dev"]
}
