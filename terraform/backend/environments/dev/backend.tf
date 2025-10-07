terraform {
  backend "s3" {                                            # Terraform 상태 파일(terraform.tfstate)을 로컬이 아닌 S3에 저장하도록 지정
    bucket       = "courseitda-backend-dev-terraform-state" # 상태 파일을 저장할 S3 버킷 이름
    key          = "backend.tfstate"                        # S3 버킷 안에서 사용할 객체 키(= 경로) ex. courseitda-terraform-state-dev/dev/terraform.tfstate
    region       = "ap-northeast-2"                         # S3 버킷이 위치한 리전
    use_lockfile = true                                     # 동시에 여러 사용자가 terraform apply 실행 시 충돌 방지
    encrypt      = true                                     # 서버 측 암호화 적용
  }
}
