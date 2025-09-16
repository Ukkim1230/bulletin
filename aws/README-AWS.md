# 🚀 AWS 배포 가이드

## 📋 사전 요구사항

### 1. 필수 도구 설치
- **AWS CLI**: [설치 가이드](https://aws.amazon.com/cli/)
- **Docker**: [설치 가이드](https://www.docker.com/products/docker-desktop)
- **Terraform**: [설치 가이드](https://www.terraform.io/downloads.html)

### 2. AWS 계정 설정
```bash
aws configure
```

## 🎯 빠른 배포

### Windows
```cmd
aws\deploy-aws.bat
```

### 수동 배포

1. **Terraform 초기화**
```bash
cd aws/terraform
terraform init
```

2. **변수 설정**
```bash
# terraform.tfvars 파일 생성
echo 'db_password = "your_secure_password"' > terraform.tfvars
echo 'aws_region = "ap-northeast-2"' >> terraform.tfvars
```

3. **인프라 배포**
```bash
terraform plan
terraform apply
```

4. **Docker 이미지 빌드 & 푸시**
```bash
# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $(terraform output -raw ecr_repository_url)

# 이미지 빌드
docker build -t youth-bulletin .

# 이미지 태깅 & 푸시
docker tag youth-bulletin:latest $(terraform output -raw ecr_repository_url):latest
docker push $(terraform output -raw ecr_repository_url):latest
```

5. **ECS 서비스 업데이트**
```bash
aws ecs update-service --cluster $(terraform output -raw ecs_cluster_name) --service $(terraform output -raw ecs_service_name) --force-new-deployment
```

## 📊 배포된 리소스

### 네트워킹
- ✅ VPC (10.0.0.0/16)
- ✅ 퍼블릭 서브넷 2개 (Multi-AZ)
- ✅ 프라이빗 서브넷 2개 (Multi-AZ)
- ✅ Internet Gateway
- ✅ NAT Gateway 2개
- ✅ Route Tables

### 컴퓨팅
- ✅ ECS Fargate 클러스터
- ✅ ECS 서비스 (2개 태스크)
- ✅ Application Load Balancer
- ✅ Auto Scaling 준비

### 데이터베이스
- ✅ RDS MySQL 8.0
- ✅ Multi-AZ 백업
- ✅ 자동 백업 (7일 보관)

### 보안
- ✅ Security Groups
- ✅ IAM Roles & Policies
- ✅ SSM Parameter Store (DB 자격증명)

### 모니터링
- ✅ CloudWatch Logs
- ✅ Health Checks
- ✅ 애플리케이션 메트릭

## 🌐 접속 정보

배포 완료 후 다음 명령어로 URL 확인:
```bash
terraform output application_urls
```

- **메인 페이지**: `http://[ALB-DNS]/`
- **모바일 주보**: `http://[ALB-DNS]/mobile`
- **API 문서**: `http://[ALB-DNS]/swagger-ui.html`

## 📱 청년부 사용법

1. **스마트폰으로 접속**: 모바일 주보 URL
2. **홈화면에 추가**: 브라우저 메뉴 → "홈 화면에 추가"
3. **오프라인 사용**: 캐시된 데이터로 마지막 주보 확인
4. **자동 업데이트**: 5분마다 자동 새로고침

## 🛠️ 관리 명령어

### 로그 확인
```bash
aws logs tail /ecs/youth-bulletin --follow
```

### 서비스 재시작
```bash
aws ecs update-service --cluster youth-bulletin-cluster --service youth-bulletin-service --force-new-deployment
```

### 스케일링
```bash
aws ecs update-service --cluster youth-bulletin-cluster --service youth-bulletin-service --desired-count 3
```

### 비용 최적화 (개발/테스트용)
```bash
# 서비스 중지
aws ecs update-service --cluster youth-bulletin-cluster --service youth-bulletin-service --desired-count 0

# RDS 중지 (최대 7일)
aws rds stop-db-instance --db-instance-identifier youth-bulletin-db
```

## 💰 예상 비용 (월간)

| 리소스 | 사양 | 예상 비용 |
|--------|------|-----------|
| ECS Fargate | 2 tasks (0.5 vCPU, 1GB) | $15-25 |
| RDS MySQL | db.t3.micro | $15-20 |
| ALB | 기본 사용량 | $20-25 |
| NAT Gateway | 2개 | $45-60 |
| 기타 (CloudWatch, ECR 등) | | $5-10 |
| **총 예상 비용** | | **$100-140** |

### 비용 절약 팁
- 개발/테스트 시 NAT Gateway 1개만 사용
- RDS를 필요시에만 실행
- CloudWatch 로그 보관 기간 단축

## 🔒 보안 고려사항

- ✅ 데이터베이스는 프라이빗 서브넷에 배치
- ✅ 보안 그룹으로 접근 제한
- ✅ IAM 역할 기반 권한 관리
- ✅ SSM Parameter Store로 민감 정보 관리
- ✅ 암호화된 RDS 스토리지

## 🚨 문제 해결

### 배포 실패
```bash
# Terraform 상태 확인
terraform show

# AWS 리소스 직접 확인
aws ecs describe-services --cluster youth-bulletin-cluster
aws rds describe-db-instances --db-instance-identifier youth-bulletin-db
```

### 애플리케이션 오류
```bash
# ECS 태스크 로그 확인
aws logs tail /ecs/youth-bulletin --follow

# 헬스체크 상태 확인
aws elbv2 describe-target-health --target-group-arn [TARGET-GROUP-ARN]
```

## 🗑️ 리소스 정리

```bash
cd aws/terraform
terraform destroy
```

---

**🎉 축하합니다!** 청년부 모바일 주보가 AWS에 성공적으로 배포되었습니다! 🙏
