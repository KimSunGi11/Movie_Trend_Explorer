# Movie Trend Explorer
DevContainer + Movie Review Project using TMDB API

### Architecture of Movie Trend Explorer Project

### Features
- Movie information display using TMDB API
- User reviews and ratings
- Movie search and filtering
- User authentication and authorization
- Personalized movie recommendations
- Watchlist management

### Tech Stack
- Frontend: Vue.js
- Backend: Spring Boot
- Database: PostgreSQL
- Authentication: JWT + RSA
- API: TMDB API

### Project Setup

###### Vue Project Setup
```
vue -V
vue create vue
cd vue
yarn install
yarn serve
```

###### Spring Boot Setup
```
cd boot
./gradlew bootRun
```

### API Documentation
- [TMDB API Documentation](https://developer.themoviedb.org/docs)
- [Application API Documentation](./docs/api.md)

### Environment Variables
- TMDB_API_KEY: Your TMDB API key
- JWT_SECRET: Your JWT secret key
- DATABASE_URL: Your database connection URL 