locals {
  base_tags = merge(var.base_tags, {
    Project     = var.project_name
    Environment = var.environment
  })
}

# 기존 backend 테라폼(terraform/backend)이 만든 VPC/서브넷/키페어를 그대로 사용합니다.
# backend 상태 파일을 참조하지 않고, 태그/이름으로 AWS 리소스를 직접 조회하는 방식이라
# terraform/backend 디렉터리는 전혀 수정하지 않습니다.
module "application" {
  source = "../../modules/application"

  project_name = var.project_name
  environment  = var.environment

  # 기존 앱 서버(t4g.small, ARM64, gp2 20GiB)와 동일한 사양
  instance_type = "t4g.small"
  volume_type   = "gp2"
  volume_size   = 20

  # 기존 backend VPC의 public subnet(AZ-a)을 태그로 조회해 재사용
  network_project_name = "courseitda"

  base_tags = local.base_tags
}
