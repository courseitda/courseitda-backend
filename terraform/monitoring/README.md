# Terraform Monitoring

모니터링 서버(Grafana/Prometheus/Loki) 배포를 위한 인프라 자동화 코드입니다.

기존 `terraform/backend`가 만든 VPC/서브넷/키페어는 **data source(태그·이름 조회)** 로만 참조하며,
`terraform/backend` 디렉터리의 어떤 파일도 수정하지 않습니다.

## 패키지 구조

```
.
├── bootstrap/
│   ├── main.tf
│   ├── outputs.tf
│   ├── provider.tf
│   └── variables.tf
├── environments/
│   └── dev/
│       ├── backend.tf
│       ├── main.tf
│       ├── outputs.tf
│       ├── provider.tf
│       └── variables.tf
└── modules/
    └── application/
        ├── main.tf
        ├── outputs.tf
        ├── user_data.tpl
        └── variables.tf
```

## 사용 방법

### 1. 상태 저장소 생성 (최초 1회)

```bash
cd bootstrap
terraform init
terraform plan
terraform apply
```

`courseitda-monitoring-dev-terraform-state` S3 버킷이 생성됩니다 (backend의 상태 버킷과 분리).

### 2. 모니터링 서버 리소스 생성

```bash
cd environments/dev
terraform init
terraform plan
terraform apply
```

## 주요 리소스

- EC2 인스턴스: Ubuntu 24.04 ARM64 (t4g.small), 기존 앱 서버와 동일 사양
- Security Group: SSH(22) / Grafana(3001) / Prometheus(9090) / Loki(3100) 전체 허용 (앱 서버 SG와 동일한 정책)
- Elastic IP: 기존 앱 서버(`terraform/backend/modules/application`)와 동일한 방식으로 **Terraform이 신규 발급** (`aws_eip` 리소스로 새로 생성 후 인스턴스에 연결)

기존 backend 리소스 재사용(모두 조회 전용, backend 파일 미수정):

- VPC/Public Subnet(AZ-a): `courseitda-vpc` / `courseitda-public-a` 태그로 조회
- SSH Key Pair: `courseitda-key-pair` 이름으로 조회

## ⚠️ 마이그레이션(첫 apply) 주의사항

현재 모니터링 서버는 Terraform 밖에서 수동으로 만들어져 있고, 별도의 고정 Elastic IP를 물고 있습니다.
이번 apply는 그 기존 IP를 재사용하지 않고 **새 IP를 발급**하므로, 아래 후속 조치가 필요합니다.

1. `terraform apply` 후 `terraform output eip_public_ip`로 새로 발급된 IP를 확인하세요.
2. 기존 IP를 참조하던 곳을 새 IP로 갱신해야 합니다.
   - `courseitda-backend-config`(submodule) → `dev/docker-compose.yml`의 promtail 서비스 `LOKI_SERVER_IP`
   - Grafana 접속 URL 등 팀 내부 문서/북마크
3. Grafana 대시보드, Prometheus/Loki에 쌓인 과거 메트릭·로그는 **기존 인스턴스의 로컬 Docker volume**에 저장되어 있어
   새 인스턴스로 자동 이전되지 않습니다. 필요하다면 apply 전에 Grafana 대시보드를 export하거나 기존 EBS 볼륨을 스냅샷해두세요.
4. 권장 순서: (1) apply로 새 EC2 + 새 EIP 생성 → (2) 새 인스턴스에 `monitoring-dev` self-hosted 러너 등록 →
   (3) 위 참조처(promtail 등)를 새 IP로 갱신 → (4) `Monitoring Server Deploy (Manual)` 워크플로우 실행으로 docker compose 스택 기동 →
   (5) 정상 동작 확인 후 기존 수동 인스턴스 종료.
5. self-hosted 러너 설치는 기존 backend 앱 서버(`dev` 라벨)와 동일하게 **Terraform 범위 밖에서 수동으로 등록**하는 것으로 가정했습니다.

## 주의사항

- IMDSv2가 활성화되어 SSRF 공격으로부터 보호됩니다
- EBS 볼륨은 암호화되어 있습니다
- SSH/Grafana/Prometheus/Loki 포트가 `0.0.0.0/0`에 열려 있습니다 (기존 앱 서버 SG와 동일한 학습용 정책이며, 필요 시 특정 IP로 제한 권장)
