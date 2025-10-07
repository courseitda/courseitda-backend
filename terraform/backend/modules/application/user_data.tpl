#!/bin/bash
# user_data는 EC2 최초 부팅 시점에 실행됨
# OS 초기화 완료 직후, root 권한으로 실행되는 스크립트
apt-get update -y

# Docker 설치
apt-get install -y ca-certificates curl
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Nginx 설치
apt-get install -y nginx

# Swapfile 생성 (메모리 부족 문제 해결용)
fallocate -l 4GiB /swapfile

chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile

# 확인
free -h
swapon -s

# 재부팅 후에도 유지
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
