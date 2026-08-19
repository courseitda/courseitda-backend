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
- Security Group: SSH(22) / HTTP(80) / HTTPS(443) / Grafana(3001) / Prometheus(9090) / Loki(3100) 전체 허용 (앱 서버 SG와 동일한 정책)
- Elastic IP: 기존 앱 서버(`terraform/backend/modules/application`)와 동일한 방식으로 **Terraform이 신규 발급** (`aws_eip` 리소스로 새로 생성 후 인스턴스에 연결)
- nginx: 메인 앱 서버와 동일하게 `user_data.tpl`에서 패키지만 설치 (`apt-get install -y nginx`). 리버스 프록시 설정, HTTPS 인증서 발급은 메인 앱 서버와 마찬가지로 Terraform/Git 범위 밖에서 수동으로 진행
- S3 버킷(`loki_logs`, `courseitda-dev-loki-logs`): Loki의 영속 스토리지 백엔드. public access 차단 + 기본 암호화(AES256) 적용, lifecycle 규칙은 걸지 않음(삭제는 Loki compactor가 담당)
- IAM Role + Instance Profile: 모니터링 EC2가 access key 없이 위 S3 버킷에만 접근할 수 있도록 하는 최소 권한 역할. EC2가 이 role을 자동으로 사용하도록 `iam_instance_profile`로 연결
- `metadata_options.http_put_response_hop_limit = 2`: Docker 컨테이너(Loki)가 IMDS를 통해 이 IAM Role 자격증명을 받아올 수 있도록 설정 (기본값 1이면 브리지 네트워크의 추가 홉 때문에 컨테이너에서 메타데이터 서버 접근이 막힘)

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

## 📦 Loki S3 스토리지 백엔드

Loki 청크/인덱스를 로컬 디스크 대신 S3(`loki_logs` 버킷)에 저장하도록 전환했습니다. 관련 설정은 이 terraform 리포가 아니라 `courseitda-backend-config`(submodule)에 있습니다.

- **실서버**: `courseitda-backend-config/monitoring/docker-compose.yml`이 `courseitda-backend-config/monitoring/loki/loki-config.s3.yml`을 사용 (S3 백엔드, `grafana/loki:3.6.13` 고정, `schema v13 + tsdb`, `retention_period: 30d` + compactor로 실제 삭제까지 수행)
- **로컬 개발용**(`docker-compose.monitoring.yml`, 팀원 개인 PC): 여전히 `courseitda-backend-config/monitoring/loki/loki-config.yml`을 쓰고 스토리지는 filesystem 그대로지만, **스키마/버전은 실서버와 동일하게 맞췄습니다** — `grafana/loki:latest` → `3.6.13` 고정, `schema v11+boltdb-shipper` → `v13+tsdb`로 전환, compactor 추가(기존엔 compactor가 없어서 `retention_period`가 있어도 실제로는 아무것도 안 지워지고 로컬 디스크에 무한정 쌓이는 버그가 있었음 — 이번에 같이 고쳐짐). object_store만 filesystem으로 유지 (개인 PC엔 EC2 IAM Role이 없어서 S3는 자격증명이 없어 저장이 실패하기 때문에 스토리지 백엔드만 분리)
- **AWS 자격증명**: access key/secret key를 어디에도 넣지 않았습니다. 모니터링 EC2에 붙은 IAM Instance Profile을 Loki가 AWS SDK 기본 자격증명 체인으로 자동 사용합니다 (`storage_config.aws`에 키 필드 없음)
- **적용 후 필요한 작업**: `terraform apply`로 IAM Role/S3 버킷을 먼저 만든 뒤 EC2가 떠야, 그 EC2의 self-hosted 러너가 `Monitoring Server Deploy (Manual)` 워크플로우로 새 `loki-config.s3.yml` 기반 docker compose를 띄우는 순서가 됩니다. `courseitda-backend-config` submodule 변경 사항은 그 repo에 별도로 커밋/푸시하고, 메인 repo의 submodule 포인터도 갱신해야 반영됩니다.
- 기존 로컬(filesystem) Loki에 쌓여있던 로그는 마이그레이션 대상에 포함하지 않았습니다 (위 EIP/EC2 교체와 마찬가지로 새로 시작).
- 로컬에서 이미 `docker-compose.monitoring.yml`을 띄워본 적이 있다면, 기존 `loki-data` 볼륨엔 예전 스키마(v11/boltdb-shipper)로 만든 인덱스가 들어있어 새 스키마(v13/tsdb)와 호환되지 않습니다. 다음에 로컬 스택을 띄우기 전에 `docker volume rm <프로젝트>_loki-data`로 한 번 초기화해주세요 (로컬 로그라 데이터 보존 필요 없음).

## Spring 앱 로컬 로그 보존 기간 (logback)

Loki+S3가 장기 보관을 담당하게 되면서, `src/main/resources/logback-dev.xml`의 로컬 원본 로그 보존 기간을 줄였습니다.

- request/response/app 로그: 7일 → **3일** (`maxHistory`)
- error 로그: 30일 유지 (변경 없음)

Promtail이 활성 로그 파일을 실시간으로 tail해서 Loki로 전송하는 구조라, 로컬 보존 기간을 줄여도 Loki/S3 쪽 로그 유실 위험은 낮습니다.

## 주의사항

- IMDSv2가 활성화되어 SSRF 공격으로부터 보호됩니다
- EBS 볼륨은 암호화되어 있습니다
- SSH/HTTP/HTTPS/Grafana/Prometheus/Loki 포트가 `0.0.0.0/0`에 열려 있습니다 (기존 앱 서버 SG와 동일한 학습용 정책이며, 필요 시 특정 IP로 제한 권장)
- nginx 리버스 프록시 설정과 HTTPS 인증서 발급(certbot 등)은 메인 앱 서버와 동일하게 이 리포에 포함하지 않았습니다. 도메인이 정해지면 서버에 직접 접속해 수동으로 진행해주세요.
- Loki S3 버킷은 이 terraform 스택(`environments/dev`)을 destroy해도 안에 데이터가 남아있으면 기본적으로 삭제되지 않습니다(AWS 기본 동작) — 버킷을 비우지 않으면 destroy가 실패할 수 있습니다.
