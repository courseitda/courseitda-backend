# Terraform Frontend

프론트엔드 정적 웹사이트 배포를 위한 인프라 자동화 코드입니다.

## 패키지 구조

```
.
├── environments/
│   └── dev/
│       ├── backend.tf
│       ├── main.tf
│       ├── outputs.tf
│       ├── provider.tf
│       └── variables.tf
└── modules/
    └── static-website/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

## 사용 방법

### 1. Terraform 코드 실행

```bash
cd environments/dev
terraform init
terraform plan
terraform apply
```

### 2. ACM 인증서 검증

`acm_dns_validation_records` output을 확인해 외부 DNS 제공자(예: Gabia)에 검증 레코드를 추가합니다.

```bash
terraform output acm_dns_validation_records
```

CloudFront에 연결해서 사용할 도메인 레코드도 추가합니다.

CloudFront 도메인 주소는 `cloudfront_domain` output으로 확인할 수 있습니다.

```bash
terraform output cloudfront_domain
```

### 3. Terraform 코드 재실행

검증 레코드 추가 후 ACM 인증서 발급이 완료되면, 다시 `terraform apply`를 실행해 리소스 생성을 마무리합니다.

```bash
terraform apply
```

리소스 생성이 완료되면, 연결한 커스텀 도메인으로 접속 가능합니다.

`website_url` output으로 연결한 커스텀 도메인 주소를 확인할 수 있습니다.

```bash
terraform output website_url
```

## 주요 리소스

- S3 버킷: 빌드된 프론트엔드 코드 배포 용도
- CloudFront: CDN
- ACM 인증서: SSL/TLS 인증서
- CloudFront OAC(Origin Access Control): S3 버킷은 외부에서 직접 접근하지 못 하게 하고, CloudFront를 통해서만 접근하게 하기 위한 접근 제어
- S3 버킷 정책: CloudFront만 읽기 허용

## 주의사항

- ACM 인증서는 CloudFront 사용을 위해 반드시 us-east-1 리전에서 발급해야 합니다
- DNS 검증 레코드는 외부 DNS 제공자(e.g. Gabia)에 수동으로 등록해야 합니다
- CloudFront 배포 변경 사항은 전파되는데 시간이 소요될 수 있습니다 (15-30분)
- S3 버킷은 CloudFront를 통해서만 접근 가능하며, 외부에서의 직접 접근은 차단됩니다
