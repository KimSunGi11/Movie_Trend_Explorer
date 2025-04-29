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
      isLoading: false,
      isTrendingLoading: false,
      isKoreanLoading: false,
      error: null,
      trendingError: null,
      koreanError: null,
      defaultPoster: NoPoster
    }
  },
  created() {
    this.fetchPopularMovies()
    this.fetchTrendingMovies()
    this.fetchKoreanMovies()
  },
  methods: {
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
.hero {
  background: linear-gradient(to right, #01b4e4, #0d253f);
  color: white;
  padding: 4rem 0;
  margin-bottom: 2rem;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero h2 {
  font-size: 2rem;
  margin-bottom: 2rem;
  font-weight: 300;
}

.search-container {
  display: flex;
  justify-content: center;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.search-container :deep(.search-bar) {
  width: 100%;
  max-width: 800px;
}

.search-container :deep(.search-input) {
  font-size: 1.2rem;
  padding: 1rem;
  border-radius: 30px;
  border: none;
  width: 100%;
}

.search-container :deep(.search-button) {
  padding: 0 2rem;
  border-radius: 30px;
  font-size: 1.2rem;
}

.category-tabs {
  margin-bottom: 2rem;
  background-color: #f8f9fa;
  padding: 1rem 0;
  border-bottom: 1px solid #dee2e6;
}

.nav-tabs {
  border-bottom: none;
  justify-content: center;
}

.nav-tabs .nav-link {
  font-size: 1.2rem;
  font-weight: 600;
  color: #343a40;
  border: none;
  padding: 0.75rem 1.5rem;
  margin-right: 1rem;
  border-radius: 30px;
  transition: all 0.3s ease;
  background-color: #e9ecef;
}

.nav-tabs .nav-link:hover {
  color: #01b4e4;
  background-color: #dde2e6;
}

.nav-tabs .nav-link.active {
  color: white;
  background-color: #01b4e4;
  border: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.tab-content {
  padding: 2rem 0;
}

.movie-card {
  display: block;
  text-decoration: none;
  color: inherit;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  height: 100%;
}

.movie-card:hover {
  transform: translateY(-5px);
}

.movie-poster {
  width: 100%;
  height: 360px;
  object-fit: cover;
}

.movie-info {
  padding: 1rem;
}

.movie-title {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.movie-date {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.movie-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.rating {
  color: #f5c518;
  font-weight: 600;
}

.vote-count {
  color: #666;
  font-size: 0.9rem;
}
</style> 