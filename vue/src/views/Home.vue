<template>
  <div class="home">
    <!-- Hero Section with Search -->
    <section class="hero">
      <div class="container">
        <div class="hero-content text-center">
          <h1>환영합니다</h1>
          <h2>여러분이 원하는 영화를 찾아보세요.</h2>
          <div class="search-container">
            <SearchBar />
          </div>
        </div>
      </div>
    </section>

    <!-- 영화 카테고리 탭 -->
    <div class="category-tabs">
      <div class="container">
        <ul class="nav nav-tabs" id="movieTabs" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link active" id="trending-tab" data-bs-toggle="tab" data-bs-target="#trending" type="button" role="tab" aria-controls="trending" aria-selected="true">트렌드</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="popular-tab" data-bs-toggle="tab" data-bs-target="#popular" type="button" role="tab" aria-controls="popular" aria-selected="false">인기 급상승</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="korean-tab" data-bs-toggle="tab" data-bs-target="#korean" type="button" role="tab" aria-controls="korean" aria-selected="false">국내 영화</button>
          </li>
          <li class="nav-item" role="presentation" v-if="isLoggedIn">
            <button class="nav-link" id="recommended-tab" data-bs-toggle="tab" data-bs-target="#recommended" type="button" role="tab" aria-controls="recommended" aria-selected="false">맞춤 추천</button>
          </li>
        </ul>
      </div>
    </div>

    <!-- 영화 카테고리 콘텐츠 -->
    <div class="tab-content" id="movieTabsContent">
      <!-- 트렌드 영화 섹션 -->
      <div class="tab-pane fade show active" id="trending" role="tabpanel" aria-labelledby="trending-tab">
        <div class="container">
        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2">Loading movies...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="alert alert-danger" role="alert">
          {{ error }}
        </div>

        <!-- Movies Grid -->
        <div v-else class="row">
          <div v-for="movie in movies" :key="movie.id" class="col-md-3 mb-4">
            <router-link :to="{ name: 'MovieDetail', params: { id: movie.id }}" class="movie-card" @click="goToMovieDetail(movie.id)">
              <img 
                :src="getImageUrl(movie.poster_path)" 
                :alt="movie.title"
                class="movie-poster"
                @error="handleImageError"
              >
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="movie-date">{{ formatDate(movie.release_date) }}</p>
                <div class="movie-rating">
                  <span class="rating">★ {{ movie.vote_average?.toFixed(1) || 'N/A' }}</span>
                  <span class="vote-count">({{ movie.vote_count || 0 }})</span>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
      </div>

    <!-- 인기 급상승 영화 섹션 -->
      <div class="tab-pane fade" id="popular" role="tabpanel" aria-labelledby="popular-tab">
      <div class="container">
        <!-- Loading State -->
        <div v-if="isTrendingLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2">Loading trending movies...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="trendingError" class="alert alert-danger" role="alert">
          {{ trendingError }}
        </div>

        <!-- Movies Grid -->
        <div v-else class="row">
          <div v-for="movie in trendingMovies" :key="movie.id" class="col-md-3 mb-4">
            <router-link :to="{ name: 'MovieDetail', params: { id: movie.id }}" class="movie-card" @click="goToMovieDetail(movie.id)">
              <img 
                :src="getImageUrl(movie.poster_path)" 
                :alt="movie.title"
                class="movie-poster"
                @error="handleImageError"
              >
              <div class="movie-info">
                <h3 class="movie-title">{{ movie.title }}</h3>
                <p class="movie-date">{{ formatDate(movie.release_date) }}</p>
                <div class="movie-rating">
                  <span class="rating">★ {{ movie.vote_average?.toFixed(1) || 'N/A' }}</span>
                  <span class="vote-count">({{ movie.vote_count || 0 }})</span>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </div>
      </div>

      <!-- 국내 영화 섹션 -->
      <div class="tab-pane fade" id="korean" role="tabpanel" aria-labelledby="korean-tab">
        <div class="container">
          <!-- Loading State -->
          <div v-if="isKoreanLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">Loading Korean movies...</p>
          </div>

          <!-- Error State -->
          <div v-else-if="koreanError" class="alert alert-danger" role="alert">
            {{ koreanError }}
          </div>

          <!-- Movies Grid -->
          <div v-else class="row">
            <div v-for="movie in koreanMovies" :key="movie.id" class="col-md-3 mb-4">
              <router-link :to="{ name: 'MovieDetail', params: { id: movie.id }}" class="movie-card" @click="goToMovieDetail(movie.id)">
                <img 
                  :src="getImageUrl(movie.poster_path)" 
                  :alt="movie.title"
                  class="movie-poster"
                  @error="handleImageError"
                >
                <div class="movie-info">
                  <h3 class="movie-title">{{ movie.title }}</h3>
                  <p class="movie-date">{{ formatDate(movie.release_date) }}</p>
                  <div class="movie-rating">
                    <span class="rating">★ {{ movie.vote_average?.toFixed(1) || 'N/A' }}</span>
                    <span class="vote-count">({{ movie.vote_count || 0 }})</span>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- 맞춤 추천 영화 섹션 -->
      <div class="tab-pane fade" id="recommended" role="tabpanel" aria-labelledby="recommended-tab">
        <div class="container">
          <!-- Loading State -->
          <div v-if="isRecommendedLoading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">맞춤 영화를 불러오는 중...</p>
          </div>

          <!-- Error State -->
          <div v-else-if="recommendedError" class="alert alert-danger" role="alert">
            {{ recommendedError }}
          </div>

          <!-- Empty State -->
          <div v-else-if="recommendedMovies.length === 0" class="text-center py-5">
            <p class="text-muted">아직 추천할 영화가 없습니다. 영화를 더 둘러보세요!</p>
          </div>

          <!-- Movies Grid -->
          <div v-else class="row">
            <div v-for="movie in recommendedMovies" :key="movie.id" class="col-md-3 mb-4">
              <router-link :to="{ name: 'MovieDetail', params: { id: movie.id }}" class="movie-card" @click="goToMovieDetail(movie.id)">
                <img 
                  :src="getImageUrl(movie.poster_path)" 
                  :alt="movie.title"
                  class="movie-poster"
                  @error="handleImageError"
                >
                <div class="movie-info">
                  <h3 class="movie-title">{{ movie.title }}</h3>
                  <p class="movie-date">{{ formatDate(movie.release_date) }}</p>
                  <div class="movie-rating">
                    <span class="rating">★ {{ movie.vote_average?.toFixed(1) || 'N/A' }}</span>
                    <span class="vote-count">({{ movie.vote_count || 0 }})</span>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import NoPoster from '@/assets/no-poster.svg'
import SearchBar from '@/components/SearchBar.vue'

export default {
  name: 'Home',
  components: {
    SearchBar
  },
  data() {
    return {
      movies: [],
      trendingMovies: [],
      koreanMovies: [],
      recommendedMovies: [],
      isLoading: false,
      isTrendingLoading: false,
      isKoreanLoading: false,
      isRecommendedLoading: false,
      error: null,
      trendingError: null,
      koreanError: null,
      recommendedError: null,
      defaultPoster: NoPoster,
      isLoggedIn: false,
      username: null
    }
  },
  created() {
    this.checkLoginStatus()
    this.fetchPopularMovies()
    this.fetchTrendingMovies()
    this.fetchKoreanMovies()
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      if (token) {
        this.isLoggedIn = true
        this.username = localStorage.getItem('username')
        this.fetchRecommendedMovies()
      }
    },
    async fetchRecommendedMovies() {
      try {
        this.isRecommendedLoading = true;
        this.recommendedError = null;
        const token = localStorage.getItem('token');
        if (!token) {
          throw new Error('로그인이 필요합니다.');
        }
        const response = await axios.get('http://localhost:8080/api/movies/recommended', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        this.recommendedMovies = response.data;
      } catch (error) {
        console.error('Error fetching recommended movies:', error);
        this.recommendedError = '맞춤 영화를 불러오는데 실패했습니다.';
      } finally {
        this.isRecommendedLoading = false;
      }
    },
    async fetchPopularMovies() {
      this.isLoading = true
      this.error = null
      
      try {
        const response = await axios.get('http://localhost:8080/api/movies/trending')
        console.log('API Response:', response.data)
        console.log('First movie:', response.data.results[0])
        console.log('First movie poster path:', response.data.results[0]?.poster_path)
        
        if (response.data && Array.isArray(response.data.results)) {
          this.movies = response.data.results.map(movie => {
            console.log('Movie poster path:', movie.poster_path)
            return movie
          })
        } else {
          this.error = '영화 데이터를 불러오는데 실패했습니다.'
        }
      } catch (error) {
        console.error('Error fetching popular movies:', error)
        this.error = '영화 데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.'
      } finally {
        this.isLoading = false
      }
    },
    
    async fetchTrendingMovies() {
      this.isTrendingLoading = true
      this.trendingError = null
      
      try {
        const response = await axios.get('http://localhost:8080/api/movies/trending/daily')
        
        if (response.data && Array.isArray(response.data.results)) {
          this.trendingMovies = response.data.results.map(movie => {
            return movie
          })
        } else {
          this.trendingError = '인기 급상승 영화 데이터를 불러오는데 실패했습니다.'
        }
      } catch (error) {
        console.error('Error fetching trending movies:', error)
        this.trendingError = '인기 급상승 영화 데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.'
      } finally {
        this.isTrendingLoading = false
      }
    },

    async fetchKoreanMovies() {
      this.isKoreanLoading = true
      this.koreanError = null
      
      try {
        const response = await axios.get('http://localhost:8080/api/movies/korean/popular')
        
        if (response.data && Array.isArray(response.data.results)) {
          this.koreanMovies = response.data.results.map(movie => {
            return movie
          })
        } else {
          this.koreanError = '국내 영화 데이터를 불러오는데 실패했습니다.'
        }
      } catch (error) {
        console.error('Error fetching Korean movies:', error)
        this.koreanError = '국내 영화 데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.'
      } finally {
        this.isKoreanLoading = false
      }
    },
    getImageUrl(path) {
      if (!path) return this.defaultPoster
      if (path.startsWith('http')) return path
      return `https://image.tmdb.org/t/p/original${path}`
    },
    handleImageError(e) {
      e.target.src = this.defaultPoster
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    goToMovieDetail(movieId) {
      this.$router.push(`/movie/${movieId}`)
    }
  }
}
</script>

<style scoped>
.home {
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  min-height: 100vh;
}

.hero {
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  color: #C4A77D;
  padding: 6rem 0;
  margin-bottom: 0;
  position: relative;
  overflow: visible;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(196, 167, 125, 0.1), rgba(43, 62, 80, 0.1));
  z-index: 1;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.hero h1 {
  font-size: 3.5rem;
  margin-bottom: 1rem;
  color: #C4A77D;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  font-weight: 700;
  letter-spacing: 1px;
}

.hero h2 {
  font-size: 2rem;
  margin-bottom: 2.5rem;
  font-weight: 300;
  color: rgba(255, 255, 255, 0.9);
  letter-spacing: 0.5px;
}

.search-container {
  display: flex;
  justify-content: center;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 3;
}

.search-container :deep(.search-bar) {
  width: 100%;
  max-width: 800px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  padding: 0.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  position: relative;
}

.search-container :deep(.search-input) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(196, 167, 125, 0.3);
  color: #1A1A1A;
  font-size: 1.1rem;
  padding: 1rem 1.5rem;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.search-container :deep(.search-input):focus {
  background: #FFFFFF;
  border-color: #C4A77D;
  box-shadow: 0 0 0 3px rgba(196, 167, 125, 0.2);
}

.search-container :deep(.search-button) {
  background: linear-gradient(135deg, #C4A77D, #8B7355);
  border: none;
  color: #FFFFFF;
  padding: 0.8rem 1.5rem;
  border-radius: 50px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.search-container :deep(.search-button):hover {
  background: linear-gradient(135deg, #8B7355, #C4A77D);
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.search-container :deep(.suggestions) {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: linear-gradient(145deg, #ffffff, #f8f8f8);
  border: 1px solid rgba(196, 167, 125, 0.2);
  border-radius: 8px;
  margin-top: 0.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
}

.search-container :deep(.suggestion-item) {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.search-container :deep(.suggestion-item:hover) {
  background: linear-gradient(145deg, #f5f5f5, #ffffff);
}

.search-container :deep(.suggestion-poster) {
  width: 45px;
  height: 68px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 1rem;
}

.search-container :deep(.suggestion-info) {
  flex: 1;
}

.search-container :deep(.suggestion-title) {
  color: #1A1A1A;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.search-container :deep(.suggestion-year) {
  color: #8B7355;
  font-size: 0.9rem;
}

.category-tabs {
  margin-bottom: 0;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  padding: 2rem 0;
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
}

.nav-tabs {
  border-bottom: none;
  justify-content: center;
}

.nav-tabs .nav-link {
  font-size: 1.2rem;
  font-weight: 600;
  color: #C4A77D;
  border: none;
  padding: 0.75rem 1.5rem;
  margin-right: 1rem;
  border-radius: 30px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.nav-tabs .nav-link:hover {
  color: #FFFFFF;
  background: rgba(196, 167, 125, 0.2);
}

.nav-tabs .nav-link.active {
  color: #FFFFFF;
  background: linear-gradient(135deg, #C4A77D, #8B7355);
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.tab-content {
  padding: 2rem 0;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
}

.movie-card {
  display: block;
  text-decoration: none;
  color: inherit;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  height: 100%;
  border: 1px solid rgba(196, 167, 125, 0.1);
  backdrop-filter: blur(10px);
}

.movie-card:hover {
  transform: translateY(-5px);
  border-color: #C4A77D;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.movie-poster {
  width: 100%;
  height: 360px;
  object-fit: cover;
}

.movie-info {
  padding: 1rem;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
}

.movie-title {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #C4A77D;
}

.movie-date {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.movie-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.rating {
  color: #C4A77D;
  font-weight: 600;
}

.vote-count {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

/* 로딩 스피너 스타일 수정 */
.spinner-border {
  color: #C4A77D !important;
}

/* 에러 메시지 스타일 수정 */
.alert-danger {
  background: rgba(220, 53, 69, 0.1);
  border-color: rgba(220, 53, 69, 0.2);
  color: #ff6b6b;
}

/* 빈 상태 메시지 스타일 수정 */
.text-muted {
  color: rgba(255, 255, 255, 0.7) !important;
}
</style> 