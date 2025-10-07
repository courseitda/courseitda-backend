module "secret" {
  source = "../../modules/secret"
}

module "compute" {
  source = "../../modules/compute"

  project_name   = var.project_name
  environment    = var.environment
  s3_bucket_name = "${var.project_name}-${var.environment}-key-storage"
}

module "network" {
  source = "../../modules/network"

  project_name = var.project_name
  region       = var.region
  environment  = var.environment
}

module "application" {
  source = "../../modules/application"

  project_name = var.project_name
  environment  = var.environment

  instance_type = "t4g.small"
  volume_type   = "gp2"
  volume_size   = 20

  vpc_id = module.network.vpc_id
  # AZ-a의 public subnet에서 실행
  public_subnet_id = module.network.public_subnet_a_id
  key_pair_name    = module.compute.key_pair_name
}

module "database" {
  source = "../../modules/database"

  region       = var.region
  project_name = var.project_name
  environment  = var.environment

  db_name                    = "courseitda_dev_db"
  db_instance_class          = "db.t3.micro"
  db_allocated_storage       = 20
  db_backup_retention_period = 7

  vpc_id = module.network.vpc_id
  # # AZ-a의 private subnet에서 실행
  availability_zone = module.network.private_subnet_a_availability_zone
  private_subnet_ids = [
    module.network.private_subnet_a_id,
    module.network.private_subnet_b_id
  ]
  ingress_security_group_ids = [module.application.app_sg_id] # EC2(app) -> RDS로 통신 가능

  db_username = module.secret.db_username
  db_password = module.secret.db_password
}
