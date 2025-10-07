variable "project_name" {
  type        = string
  description = "Project name"
}

variable "area" {
  type        = string
  description = "Defines the work area of the project (e.g., frontend, backend)"
}

variable "environment" {
  type        = string
  description = "Environment name"
}

variable "fqdn" {
  type        = string
  description = "Fully qualified domain name for the website"
}

variable "tags" {
  type        = map(string)
  description = "Tags to apply to all resources"
  default     = {}
}
