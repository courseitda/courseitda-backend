variable "project_name" {
  type        = string
  description = "Project name"
}

variable "region" {
  type        = string
  description = "AWS region"
}

variable "area" {
  type        = string
  description = "Defines the work area of the project (e.g., frontend or backend)"
}

variable "environments" {
  type        = list(string)
  description = "List of deployment environments"
}
