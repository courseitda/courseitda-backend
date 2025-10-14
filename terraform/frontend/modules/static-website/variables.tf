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

variable "base_tags" {
  type        = map(string)
  description = "Base tags to apply to all resources"
}

variable "fqdn" {
  type        = string
  description = "Fully qualified domain name for the website"
}
