# AWS CodePipeline 설정 가이드

이 가이드는 GitHub Actions 대신 AWS CodePipeline을 사용하여 EC2에 배포하는 방법을 설명합니다.

## 🎯 CodePipeline의 장점

1. **AWS 네이티브 통합**: EC2 연결이 더 안정적
2. **IAM 역할 사용**: SSH 키 없이 권한 관리
3. **자동 스케일링**: CodeBuild 환경 자동 관리
4. **비용 효율**: 사용한 만큼만 과금 (프리티어 가능)
5. **무중단 배포**: CodeDeploy로 롤링 배포 가능

## 📋 사전 준비

### 1. IAM 역할 생성

#### CodePipeline 역할
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "codecommit:GetBranch",
        "codecommit:GetCommit",
        "codecommit:GetRepository",
        "codecommit:ListBranches",
        "codecommit:ListRepositories",
        "codebuild:BatchGetBuilds",
        "codebuild:StartBuild",
        "codedeploy:CreateDeployment",
        "codedeploy:GetApplication",
        "codedeploy:GetApplicationRevision",
        "codedeploy:GetDeployment",
        "codedeploy:GetDeploymentConfig",
        "codedeploy:RegisterApplicationRevision",
        "s3:GetObject",
        "s3:PutObject",
        "s3:GetObjectVersion"
      ],
      "Resource": "*"
    }
  ]
}
```

#### CodeBuild 역할
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents",
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": "*"
    }
  ]
}
```

#### CodeDeploy 역할 (EC2에 연결)
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:GetObjectVersion"
      ],
      "Resource": "*"
    }
  ]
}
```

### 2. S3 버킷 생성

```bash
aws s3 mb s3://bulletin-deploy-artifacts --region ap-northeast-2
```

## 🚀 CodePipeline 설정 단계

### 방법 1: AWS Console에서 설정 (권장)

1. **CodePipeline 생성**
   ```
   CodePipeline → 파이프라인 생성
   → 파이프라인 이름: bulletin-deploy
   → 서비스 역할: 새 역할 생성 (자동)
   ```

2. **소스 스테이지 설정**
   ```
   소스 제공자: GitHub (버전 2)
   → GitHub 연결 생성
   → 리포지토리: Ukkim1230/bulletin
   → 브랜치: main
   ```

3. **빌드 스테이지 설정**
   ```
   빌드 제공자: AWS CodeBuild
   → 프로젝트 생성
   → 환경: Managed Image
   → 운영 체제: Amazon Linux 2
   → 런타임: Standard
   → 이미지: aws/codebuild/amazonlinux2-x86_64-standard:7.0
   → 빌드 사양: buildspec.yml 사용
   ```
   
   **또는 Amazon Linux 2023 사용:**
   ```
   → 운영 체제: Amazon Linux 2023
   → 이미지: aws/codebuild/amazonlinux2023-x86_64-standard:7.0
   ```

4. **배포 스테이지 설정 (옵션 A: CodeDeploy 사용)**
   ```
   배포 제공자: AWS CodeDeploy
   → 애플리케이션 생성
   → 배포 그룹 생성
   → EC2 인스턴스 선택
   → 배포 구성: CodeDeployDefault.AllAtOnce
   ```

5. **배포 스테이지 설정 (옵션 B: Lambda 사용 - 더 간단)**
   ```
   배포 제공자: AWS Lambda
   → Lambda 함수 생성하여 EC2에 직접 배포
   ```

### 방법 2: CloudFormation/Terraform 사용

자세한 내용은 `codepipeline-cloudformation.yml` 참조

## 🔧 CodeBuild 설정

`buildspec.yml` 파일이 프로젝트 루트에 있습니다:

```yaml
version: 0.2

phases:
  build:
    commands:
      - ./gradlew clean build -x test --no-daemon

artifacts:
  files:
    - 'build/libs/*.jar'
    - 'bulletin.service'
```

## 📦 CodeDeploy 설정

`appspec.yml` 파일이 프로젝트 루트에 있습니다:

```yaml
version: 0.0
os: linux

files:
  - source: /
    destination: /opt/bulletin

hooks:
  BeforeInstall:
    - location: scripts/before-install.sh
  AfterInstall:
    - location: scripts/after-install.sh
  ApplicationStart:
    - location: scripts/start.sh
```

## 🎯 간단한 방법: CodeBuild만 사용

CodeDeploy 없이 CodeBuild에서 직접 EC2에 배포:

### buildspec.yml 수정

```yaml
version: 0.2

phases:
  build:
    commands:
      - ./gradlew clean build -x test --no-daemon
      - |
        # EC2에 직접 배포 (SSH 사용)
        ./deploy-ec2.sh

artifacts:
  files:
    - 'build/libs/*.jar'
```

### 환경 변수 설정

CodeBuild 프로젝트 → 환경 변수:
- `EC2_HOST`: EC2 퍼블릭 IP
- `EC2_USERNAME`: ec2-user 또는 ubuntu
- `EC2_SSH_KEY`: SSH 키 (Secrets Manager에 저장)

## 📝 CodePipeline vs GitHub Actions 비교

| 항목 | GitHub Actions | CodePipeline |
|------|----------------|--------------|
| **비용** | 무료 (공개 리포지토리) | 사용량 기반 |
| **EC2 연결** | SSH 키 필요 | IAM 역할 사용 가능 |
| **안정성** | 높음 | 매우 높음 |
| **설정 난이도** | 쉬움 | 중간 |
| **AWS 통합** | 제한적 | 완전 통합 |

## 🚀 빠른 시작 (CodeBuild만 사용)

1. **CodeBuild 프로젝트 생성**
   ```
   CodeBuild → 프로젝트 생성
   → 소스: GitHub
   → 빌드 사양: buildspec.yml 파일 사용
   → 환경: 
     - 운영 체제: Amazon Linux 2
     - 런타임: Standard
     - 이미지: aws/codebuild/amazonlinux2-x86_64-standard:7.0
     (또는 Amazon Linux 2023: aws/codebuild/amazonlinux2023-x86_64-standard:7.0)
   ```

2. **환경 변수 설정**
   ```
   EC2_HOST: EC2 퍼블릭 IP
   EC2_USERNAME: ec2-user
   EC2_SSH_KEY: SSH 키 (Secrets Manager 또는 환경 변수)
   ```

3. **빌드 실행**
   ```
   CodeBuild → 프로젝트 선택 → 빌드 시작
   ```

## ⚠️ 주의사항

1. **비용**: CodePipeline은 사용량 기반 과금
2. **SSH 키**: CodeBuild에서 EC2 접근 시 SSH 키 필요 (IAM만으로는 불가)
3. **보안**: SSH 키는 AWS Secrets Manager에 저장 권장

## 🔐 보안 권장사항

1. **SSH 키를 Secrets Manager에 저장**
   ```bash
   aws secretsmanager create-secret \
     --name bulletin/ec2-ssh-key \
     --secret-string file://ec2-key.pem
   ```

2. **CodeBuild에서 Secrets Manager 사용**
   ```yaml
   env:
     secrets-manager:
       EC2_SSH_KEY: bulletin/ec2-ssh-key
   ```

## 📚 참고 자료

- [AWS CodePipeline 문서](https://docs.aws.amazon.com/codepipeline/)
- [AWS CodeBuild 문서](https://docs.aws.amazon.com/codebuild/)
- [AWS CodeDeploy 문서](https://docs.aws.amazon.com/codedeploy/)

