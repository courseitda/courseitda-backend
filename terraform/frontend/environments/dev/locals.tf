locals {
  environment = "dev"
  fqdn        = "dev.courseitda.me"

  tags = {
    Project     = var.project_name
    Environment = var.environment
  }
}
