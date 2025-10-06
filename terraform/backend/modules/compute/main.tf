# RSA 개인 키 생성
resource "tls_private_key" "private_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# AWS Key Pair 생성 (SSH 용도)
resource "aws_key_pair" "key_pair" {
  key_name   = "${var.project_name}-key-pair"
  public_key = tls_private_key.private_key.public_key_openssh
}

# 생성된 개인 키를 로컬에 .pem 파일로 저장
resource "local_file" "private_key" {
  filename        = "${path.root}/keys/private_key.pem"
  content         = tls_private_key.private_key.private_key_pem
  file_permission = "0600"
}
