# Terraform Bootstrap

Terraform 상태 파일을 저장하기 위한 S3 버킷을 생성하는 부트스트랩 코드입니다.

## 패키지 구조

```
.
├── area/                  # 작업 영역별 설정
│   ├── backend/           # 백엔드 영역
│   │   ├── main.tf        # 주요 리소스 정의
│   │   └── variables.tf   # 백엔드 영역 변수 정의
│   └── frontend/          # 프론트엔드 영역
│       ├── main.tf        # 주요 리소스 정의
│       └── variables.tf   # 프론트엔드 영역 변수 정의
└── modules/               # 재사용 가능한 모듈 정의
    └── terraform-state/   # Terraform 상태 관리 모듈
        ├── main.tf        # S3 버킷 및 보안 설정 리소스 정의
        ├── outputs.tf     # 생성된 S3 버킷 이름 출력
        ├── provider.tf    # AWS Provider 및 Terraform 버전 설정
        └── variables.tf   # 모듈 입력 변수 정의
```

## 사용 방법

### 1. 백엔드 상태 저장소 생성

```bash
cd area/backend
terraform init
terraform plan
terraform apply
```

### 2. 프론트엔드 상태 저장소 생성

```bash
cd area/frontend
terraform init
terraform plan
terraform apply
```

## 생성되는 리소스

각 영역(area)과 환경(environment) 조합마다 다음 리소스가 생성됩니다:

- S3 버킷: `courseitda-{area}-{environment}-terraform-state`

## 주의사항

- Object Lock이 활성화된 버킷은 삭제 전 모든 객체를 먼저 삭제해야 합니다
- GOVERNANCE 모드에서 객체를 즉시 삭제하려면 관리자 권한이 필요합니다
- 상태 파일은 민감한 정보를 포함할 수 있으므로 접근 권한을 엄격히 관리해야 합니다
