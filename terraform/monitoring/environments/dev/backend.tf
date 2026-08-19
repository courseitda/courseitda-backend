terraform {
  backend "s3" {                                                # 상태 파일을 S3에 저장
    bucket       = "courseitda-monitoring-dev-terraform-state" # 상태 파일을 저장할 S3 버킷 이름
    key          = "monitoring.tfstate"                        # 파일 경로 (예: courseitda-monitoring-dev-terraform-state/monitoring.tfstate)
    region       = "ap-northeast-2"                            # S3 버킷이 위치한 리전
    use_lockfile = true                                        # 동시에 여러 사용자가 terraform apply 실행 시 충돌 방지
    encrypt      = true                                        # 서버 측 암호화 적용
  }
}
