module "terraform-state" {
  source = "../../modules/terraform-state"

  region       = var.region
  project_name = var.project_name
  area         = var.area
  environments = ["dev"]
}
