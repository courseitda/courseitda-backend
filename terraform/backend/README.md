# Terraform Backend

백엔드 애플리케이션 배포를 위한 인프라 자동화 코드입니다.

## 패키지 구조

```
.
├── environments/         # 환경별 설정
│   └── dev/
│       ├── backend.tf    # 백엔드(테라폼 상태 저장소) 설정
│       ├── main.tf       # 모듈 호출 및 연결
│       ├── provider.tf
│       └── variables.tf
└── modules/              # 재사용 가능한 모듈 정의
    ├── application/      # EC2 모듈
    │   ├── main.tf       # EC2 인스턴스, 보안 그룹, EIP 정의
    │   ├── outputs.tf
    │   ├── user_data.tpl
    │   └── variables.tf
    ├── compute/
    │   ├── main.tf       # RSA 키 생성 -> S3 업로드, 로컬 저장
    │   ├── outputs.tf
    │   └── variables.tf
    ├── database/         # RDS 데이터베이스 모듈
    │   ├── main.tf       # RDS 인스턴스, 보안 그룹, 파라미터 그룹 정의
    │   ├── outputs.tf
    │   └── variables.tf
    ├── network/          # VPC 네트워크 모듈
    │   ├── main.tf       # VPC, Subnet, IGW, Route Table 정의
    │   ├── outputs.tf
    │   └── variables.tf
    └── secret/           # 시크릿 모듈
```

## 사용 방법

### 1. 초기 설정

```bash
cd environments/dev
terraform init
```

### 2. 실행 계획 확인

```bash
terraform plan
```

### 3. 인프라 프로비저닝

```bash
terraform apply
```

### 4. SSH 접속

#### 방법 1: 로컬에 저장된 키 사용

```bash
chmod 600 keys/private_key.pem
ssh -i keys/private_key.pem ubuntu@<EC2_PUBLIC_IP>
```

#### 방법 2: S3에서 키 다운로드 후 사용

```bash
# S3에서 개인 키 다운로드
aws s3 cp s3://courseitda-dev-key-storage/keys/courseitda-dev-private-key.pem ./private_key.pem

# 권한 설정 후 SSH 접속
chmod 600 private_key.pem
ssh -i private_key.pem ubuntu@<EC2_PUBLIC_IP>
```

### 5. 리소스 정보 확인

```bash
terraform output
```

## 생성되는 리소스

- VPC: `courseitda-vpc` (10.0.0.0/16)
- Public Subnet: 2개 (AZ-a, AZ-b)
- Private Subnet: 2개 (AZ-a, AZ-b)
- Internet Gateway: VPC용 IGW
- EC2 인스턴스: Ubuntu 24.04 ARM64 (t4g.micro)
- Elastic IP: EC2용 고정 공인 IP
- RDS 인스턴스: MySQL 8.4.5 (db.t3.micro)
- Security Group: EC2용, RDS용
- SSH Key Pair: EC2 접근용
- S3 버킷: 개인 키 저장용 (`courseitda-dev-key-storage`)

## 주의사항

- RDS는 private subnet에 배포되어 외부에서 직접 접근이 불가능합니다
- EC2에서만 RDS에 접근 가능하도록 보안 그룹이 설정되어 있습니다
- SSH 개인 키는 로컬과 S3 양쪽에 저장됩니다
    - 로컬 파일: `environments/dev/keys/private_key.pem`
    - S3: `s3://courseitda-dev-key-storage/keys/courseitda-dev-private-key.pem`
    - GitHub Actions 환경에서는 workflow 종료 후 로컬 키가 삭제되므로, S3에서 다운로드하여 사용
- S3 버킷은 암호화, 버전 관리, 공개 접근 차단이 적용되어 있습니다
- IMDSv2가 활성화되어 SSRF 공격으로부터 보호됩니다
- 모든 스토리지(EBS, RDS, S3)는 암호화되어 있습니다
