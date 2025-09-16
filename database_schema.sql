-- 교회 모바일 주보 데이터베이스 스키마

-- 1. 예배 정보 테이블
CREATE TABLE worship_services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_date DATE NOT NULL,
    service_type VARCHAR(50) NOT NULL, -- '주일예배', '수요예배', '금요기도회' 등
    service_time TIME NOT NULL,
    preacher VARCHAR(100) NOT NULL, -- 설교자
    sermon_title VARCHAR(200) NOT NULL, -- 설교제목
    sermon_text VARCHAR(200), -- 본문 말씀
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 찬양 정보 테이블
CREATE TABLE praise_songs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    worship_service_id BIGINT NOT NULL,
    song_order INT NOT NULL, -- 순서 (1: 전주, 2: 찬송1, 3: 찬송2 등)
    song_type VARCHAR(20) NOT NULL, -- 'HYMN', 'CCM', 'SPECIAL'
    song_title VARCHAR(200) NOT NULL,
    song_number VARCHAR(10), -- 찬송가 번호 (CCM의 경우 NULL)
    lyrics TEXT, -- 가사 (선택사항)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (worship_service_id) REFERENCES worship_services(id) ON DELETE CASCADE
);

-- 3. 광고사항 테이블
CREATE TABLE announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    announcement_date DATE NOT NULL, -- 광고 게시 날짜
    start_date DATE, -- 행사 시작일
    end_date DATE, -- 행사 종료일
    is_important BOOLEAN DEFAULT FALSE, -- 중요 공지 여부
    display_order INT DEFAULT 0, -- 표시 순서
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 성경구절/말씀 테이블
CREATE TABLE bible_verses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_date DATE NOT NULL,
    verse_reference VARCHAR(100) NOT NULL, -- 예: "요한복음 3:16"
    verse_text TEXT NOT NULL,
    verse_type VARCHAR(20) DEFAULT 'WEEKLY', -- 'WEEKLY', 'DAILY', 'SPECIAL'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 기도제목 테이블
CREATE TABLE prayer_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    request_date DATE NOT NULL,
    category VARCHAR(50), -- '교회', '성도', '선교', '국가' 등
    is_public BOOLEAN DEFAULT TRUE, -- 공개 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. 헌금 정보 테이블
CREATE TABLE offering_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_date DATE NOT NULL,
    offering_type VARCHAR(50) NOT NULL, -- '십일조', '감사헌금', '특별헌금' 등
    purpose VARCHAR(200), -- 헌금 목적
    account_info VARCHAR(200), -- 계좌 정보
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. 교회 행사/일정 테이블
CREATE TABLE church_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(200) NOT NULL,
    event_description TEXT,
    event_date DATE NOT NULL,
    event_time TIME,
    location VARCHAR(200),
    organizer VARCHAR(100), -- 주관 부서/담당자
    is_recurring BOOLEAN DEFAULT FALSE, -- 정기 행사 여부
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 8. 관리자 계정 테이블 (주보 관리용)
CREATE TABLE admin_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- 암호화된 비밀번호
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN', -- 'SUPER_ADMIN', 'ADMIN', 'EDITOR'
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_worship_services_date ON worship_services(service_date);
CREATE INDEX idx_announcements_date ON announcements(announcement_date);
CREATE INDEX idx_announcements_active ON announcements(is_active);
CREATE INDEX idx_bible_verses_date ON bible_verses(service_date);
CREATE INDEX idx_prayer_requests_date ON prayer_requests(request_date);
CREATE INDEX idx_church_events_date ON church_events(event_date);

-- 샘플 데이터
INSERT INTO worship_services (service_date, service_type, service_time, preacher, sermon_title, sermon_text) 
VALUES ('2024-09-22', '주일예배', '11:00:00', '김목사', '하나님의 사랑', '요한복음 3:16');

INSERT INTO announcements (title, content, announcement_date, is_important) 
VALUES ('추석 연휴 예배 안내', '추석 연휴 기간 예배 시간이 변경됩니다.', '2024-09-22', true);

INSERT INTO bible_verses (service_date, verse_reference, verse_text) 
VALUES ('2024-09-22', '요한복음 3:16', '하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니');
