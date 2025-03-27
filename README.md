# Movie Trend Explorer

영화 트렌드를 탐색하고 즐겨찾기할 수 있는 웹 애플리케이션입니다.

## 주요 기능

### 1. 영화 정보 조회
- TMDB API를 활용한 최신 영화 정보 제공
- 영화 상세 정보 조회 (제목, 개봉일, 평점, 인기도 등)
- 영화 포스터 및 배경 이미지 표시

### 2. 사용자 기능
- 회원가입 및 로그인
- JWT 기반 인증 시스템
- 사용자별 프로필 관리

### 3. 즐겨찾기 기능
- 영화 즐겨찾기 추가/제거
- 즐겨찾기한 영화 목록 조회
- 영화별 즐겨찾기 수 표시

### 4. 댓글 기능
- 영화별 댓글 작성
- 댓글 목록 조회
- 댓글 작성자 정보 표시

## 기술 스택

### Frontend
- Vue.js 3
- Vuex (상태 관리)
- Vue Router
- Axios (HTTP 클라이언트)
- Font Awesome (아이콘)

### Backend
- Spring Boot 3
- Spring Security (JWT 인증)
- Spring Data JPA
- MySQL
- Lombok

## 설치 및 실행 방법

### 사전 요구사항
- Docker
- Docker Compose
- Node.js 16 이상
- Java 17 이상

### 실행 방법

1. 프로젝트 클론
```bash
git clone [repository-url]
cd Movie_Trend_Explorer
```

2. 환경 변수 설정
- `boot/src/main/resources/application.yml` 파일에서 데이터베이스 설정 확인
- `vue/.env` 파일에서 API 엔드포인트 설정 확인

3. Docker Compose로 실행
```bash
docker-compose up -d --build
```

4. 프론트엔드 개발 서버 실행
```bash
cd vue
npm install
npm run serve
```

## API 엔드포인트

### 인증
- POST `/api/auth/signup` - 회원가입
- POST `/api/auth/login` - 로그인
- GET `/api/auth/user` - 사용자 정보 조회

### 영화
- GET `/api/movies` - 영화 목록 조회
- GET `/api/movies/{id}` - 영화 상세 정보 조회
- GET `/api/movies/trending` - 트렌딩 영화 조회

### 즐겨찾기
- GET `/api/favorites` - 사용자의 즐겨찾기 목록 조회
- POST `/api/favorites/{movieId}/toggle` - 즐겨찾기 토글
- GET `/api/favorites/{movieId}/count` - 영화별 즐겨찾기 수 조회
- GET `/api/favorites/{movieId}/check` - 즐겨찾기 상태 확인

### 댓글
- GET `/api/comments/movie/{movieId}` - 영화별 댓글 목록 조회
- POST `/api/comments` - 댓글 작성
- DELETE `/api/comments/{id}` - 댓글 삭제

## 보안
- JWT 기반 인증 시스템
- Spring Security를 통한 API 보안
- CORS 설정을 통한 안전한 통신
- 비밀번호 암호화 (BCrypt)

## 기여 방법
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스
이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요. 