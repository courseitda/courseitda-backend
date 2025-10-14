locals {
  base_tags = merge(var.base_tags, {
    Project     = var.project_name
    Environment = var.environment
  })
}

module "static_website" {
  source = "../../modules/static-website"

  providers = {
    aws.use1_for_acm = aws.use1
  }

  project_name = var.project_name
  area         = var.area
  environment  = var.environment
  fqdn         = "dev.courseitda.me"

  base_tags = local.base_tags
}
