# VPC
resource "aws_vpc" "vpc" {
  cidr_block                       = "10.0.0.0/16"
  assign_generated_ipv6_cidr_block = false
  enable_dns_support               = true # DNS 이름 사용 가능
  enable_dns_hostnames             = true # 인스턴스에 퍼블릭 DNS 이름 부여 가능
  instance_tenancy                 = "default"

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-vpc"
  }
}

# public Subnet in AZ-a
resource "aws_subnet" "public_a" {
  vpc_id                          = aws_vpc.vpc.id
  cidr_block                      = "10.0.0.0/24"
  assign_ipv6_address_on_creation = false
  availability_zone               = "${var.region}a"
  map_public_ip_on_launch         = false # 인스턴스 자동 퍼블릭 IP 할당 x

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-public-a"
  }
}

# private Subnet in AZ-a
resource "aws_subnet" "private_a" {
  vpc_id                          = aws_vpc.vpc.id
  cidr_block                      = "10.0.10.0/24"
  assign_ipv6_address_on_creation = false
  availability_zone               = "${var.region}a"
  map_public_ip_on_launch         = false

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-private-a"
  }
}

# public Subnet in AZ-b
resource "aws_subnet" "public_b" {
  vpc_id                          = aws_vpc.vpc.id
  cidr_block                      = "10.0.1.0/24"
  assign_ipv6_address_on_creation = false
  availability_zone               = "${var.region}b"
  map_public_ip_on_launch         = false # 인스턴스 자동 퍼블릭 IP 할당 x

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-public-b"
  }
}

# private Subnet in AZ-b
resource "aws_subnet" "private_b" {
  vpc_id                          = aws_vpc.vpc.id
  cidr_block                      = "10.0.11.0/24"
  assign_ipv6_address_on_creation = false
  availability_zone               = "${var.region}b"
  map_public_ip_on_launch         = false

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-private-b"
  }
}

# IGW
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-igw"
  }
}

# public Route Table
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Project     = var.project_name
    Environment = var.environment
    Name        = "${var.project_name}-public-rt"
  }
}

# Subnet Associations
resource "aws_route_table_association" "public_rt_a" {
  subnet_id      = aws_subnet.public_a.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table_association" "public_rt_b" {
  subnet_id      = aws_subnet.public_b.id
  route_table_id = aws_route_table.public_rt.id
}
