# Terraform Frontend

프론트엔드 정적 웹사이트 배포를 위한 인프라 자동화 코드입니다.

## 패키지 구조

```
.
├── environments/             # 환경별 설정
│   └── dev/                  # 개발 환경
│       ├── backend.tf        # 백엔드(테라폼 상태 저장소) 설정
│       ├── main.tf           # 생성할 리소스 정의
│       ├── outputs.tf
│       ├── provider.tf       # 프로바이더 및 Terraform 버전 설정
│       └── variables.tf
└── modules/                  # 재사용 가능한 모듈 정의
    └── static-website/       # 정적 웹사이트 배포 모듈
        ├── main.tf           # S3, CloudFront, ACM 리소스 정의
        ├── variables.tf      # 모듈에 필요한 입력 변수 정의
        └── outputs.tf        # 생성된 리소스 정보 출력
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

### 4. ACM 인증서 검증

출력되는 `acm_dns_validation_records` 정보를 확인하여 외부 DNS 제공자(가비아 등)에 CNAME 레코드를 추가합니다.

```bash
terraform output acm_dns_validation_records
```

### 5. 인증서 발급 완료 후 재배포

DNS 레코드 추가 후 ACM 인증서가 발급되면 다시 apply를 실행하여 CloudFront 배포를 완료합니다.

```bash
terraform apply
```

### 6. 배포 확인

인증서가 발급된 후 CloudFront 배포가 완료되면 웹사이트 URL로 접속 가능합니다.

```bash
terraform output website_url
```

## 생성되는 리소스

- S3 버킷: `courseitda-frontend-dev`
- CloudFront: CDN
- ACM 인증서: SSL/TLS 인증서
- CloudFront OAC(Origin Access Control): S3 버킷은 외부에서 직접 접근하지 못 하게 하고, CloudFront를 통해서만 접근하게 하기 위한 접근 제어
- S3 버킷 정책: CloudFront만 읽기 허용

## 주의사항

- ACM 인증서는 CloudFront 사용을 위해 반드시 us-east-1 리전에서 발급해야 합니다
- DNS 검증 레코드는 외부 DNS 제공자(e.g. Gabia)에 수동으로 등록해야 합니다
- CloudFront 배포 변경 사항은 전파되는데 시간이 소요될 수 있습니다 (15-30분)
- S3 버킷은 CloudFront를 통해서만 접근 가능하며, 외부에서의 직접 접근은 차단됩니다
