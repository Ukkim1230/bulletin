# 기본 변수들
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-northeast-2"  # 서울 리전
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "prod"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "youth-bulletin"
}

# 데이터베이스 설정
variable "db_username" {
  description = "Database username"
  type        = string
  default     = "bulletin_admin"
  sensitive   = true
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

# 도메인 설정 (선택사항)
variable "domain_name" {
  description = "Domain name for the application"
  type        = string
  default     = ""
}

variable "certificate_arn" {
  description = "ACM certificate ARN for HTTPS"
  type        = string
  default     = ""
}

# 태그
variable "common_tags" {
  description = "Common tags to apply to all resources"
  type        = map(string)
  default = {
    Project     = "youth-bulletin"
    Environment = "prod"
    ManagedBy   = "terraform"
  }
}
