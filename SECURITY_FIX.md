# Git 히스토리에서 민감한 정보 제거 가이드

## ⚠️ 긴급 보안 조치

공개 저장소에 민감한 정보가 노출되었습니다. 즉시 다음 조치를 취하세요:

# Git 히스토리에서 민감한 정보 제거 가이드

## ⚠️ 긴급 보안 조치

공개 저장소에 민감한 정보가 노출되었습니다. 즉시 다음 조치를 취하세요:

## 1단계: 즉시 비밀번호 변경 (가장 중요!)

### Cloudinary API 키 변경
1. Cloudinary 대시보드 접속: https://cloudinary.com/console
2. Settings → Security
3. **API 키 재생성** (기존 키는 비활성화)

### EC2 데이터베이스 비밀번호 변경
1. EC2 SSH 접속
2. PostgreSQL 접속 후 비밀번호 변경
3. `.env` 파일 업데이트

## 2단계: Git 히스토리에서 민감한 정보 제거

### 방법 A: git filter-branch 사용 (권장)

```bash
# 현재 위치 확인
git remote -v

# 민감한 정보가 포함된 파일에서 비밀번호 제거
# (이미 application.yml에서 제거됨)

# Git 히스토리에서 제거
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch src/main/resources/application.yml" \
  --prune-empty --tag-name-filter cat -- --all

# 강제 푸시 (주의: 히스토리 재작성)
git push origin --force --all
git push origin --force --tags
```

### 방법 B: BFG Repo-Cleaner 사용 (더 빠름)

```bash
# BFG 다운로드 (Windows)
# https://rtyley.github.io/bfg-repo-cleaner/

# 특정 파일 제거
java -jar bfg.jar --delete-files application.yml

# 리팩토링
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# 강제 푸시
git push origin --force --all
```

## 3단계: 올바른 저장소로 변경

```bash
# 현재 remote 확인
git remote -v

# 올바른 저장소로 변경 (비공개 저장소)
git remote set-url origin https://github.com/Ukkim1230/bulletin.git

# 확인
git remote -v
```

## 4단계: 공개 저장소 삭제 또는 비공개로 전환

### 옵션 1: 저장소 비공개로 전환
1. GitHub → bulletin-showcase 저장소 → Settings
2. 맨 아래로 스크롤 → "Danger Zone"
3. "Change visibility" → "Make private"

### 옵션 2: 저장소 삭제
1. GitHub → bulletin-showcase 저장소 → Settings
2. 맨 아래로 스크롤 → "Danger Zone"
3. "Delete this repository" (신중하게!)

## 5단계: 재배포

```bash
# EC2에서 환경 변수 업데이트
# .env 파일에 새로운 비밀번호 설정
```

## 보안 체크리스트

- [ ] Cloudinary API 키 재생성 완료
- [ ] EC2 DB 비밀번호 변경 완료
- [ ] Git 히스토리에서 민감한 정보 제거
- [ ] 올바른 저장소로 변경
- [ ] 공개 저장소 비공개 또는 삭제
- [ ] EC2 환경 변수 업데이트

## 주의사항

⚠️ **Git 히스토리 재작성은 강제 푸시가 필요합니다.**
- 다른 사람이 저장소를 사용 중이라면 협의 필요
- 히스토리가 변경되므로 주의 필요

⚠️ **비밀번호 변경은 즉시 해야 합니다!**
- 이미 노출되었으므로 악용될 수 있습니다.

