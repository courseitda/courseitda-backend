# Terraform Backend

백엔드 애플리케이션 배포를 위한 인프라 자동화 코드입니다.

## 패키지 구조

```
.
├── environments/
│   └── dev/
│       ├── backend.tf
│       ├── main.tf
│       ├── provider.tf
│       └── variables.tf
└── modules/
    ├── application/
    │   ├── main.tf
    │   ├── outputs.tf
    │   ├── user_data.tpl
    │   └── variables.tf
    ├── compute/
    │   ├── main.tf
    │   ├── outputs.tf
    │   └── variables.tf
    ├── database/
    │   ├── main.tf
    │   ├── outputs.tf
    │   └── variables.tf
    ├── network/
    │   ├── main.tf
    │   ├── outputs.tf
    │   └── variables.tf
    └── secret/
```

## 사용 방법

### 1. Terraform 코드 실행

```bash
cd environments/dev
terraform init
terraform plan
terraform apply
```

## 주요 리소스

- Internet Gateway: VPC용 IGW
- VPC: `courseitda-vpc` (10.0.0.0/16)
- Public Subnet: 2개 (AZ-a, AZ-b)
- Private Subnet: 2개 (AZ-a, AZ-b)
- EC2 인스턴스: Ubuntu 24.04 ARM64 (t4g.small)
- Elastic IP: EC2 용 고정 public IP
- Security Group: EC2, RDS에서 사용
- SSH Key Pair: EC2 접근용
- S3 버킷: pem 키 저장용도
- RDS 인스턴스: MySQL 8.4.5 (db.t3.micro)

## 주의사항

- RDS는 private subnet에 배포되어 외부에서 직접 접근이 불가능합니다
- EC2에서만 RDS에 접근 가능하도록 보안 그룹이 설정되어 있습니다
- SSH 개인 키는 S3 버킷에 저장됩니다
    - S3 버킷은 암호화, 버전 관리, 공개 접근 차단이 적용되어 있습니다
- IMDSv2가 활성화되어 SSRF 공격으로부터 보호됩니다
- 모든 스토리지(EBS, RDS, S3)는 암호화되어 있습니다
