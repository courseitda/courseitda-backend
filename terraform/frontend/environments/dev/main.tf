module "static_website" {
  source = "../../modules/static-website"

  project_name = var.project_name
  area         = var.area
  environment  = var.environment
  fqdn         = "dev.courseitda.me"
  tags = {
    Project     = var.project_name
    Environment = var.environment
  }

  providers = {
    aws.use1 = aws.use1
  }
}
