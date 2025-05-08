<template>
  <div class="favorites">
    <div class="container">
      <h2 class="mb-4">즐겨찾기 영화</h2>

      <div v-if="isLoading" class="text-center">
        <div class="spinner-border text-primary" role="status">
          <span class="sr-only">로딩중...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else-if="favoriteMovies.length === 0" class="text-center text-muted">
        아직 즐겨찾기한 영화가 없습니다.
      </div>

      <div v-else class="row">
        <div v-for="movie in favoriteMovies" :key="movie.id" class="col-md-3 mb-4">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id }}" class="movie-card">
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
</template>

<script>
import axios from 'axios'
import NoPoster from '@/assets/no-poster.svg'

export default {
  name: 'Favorites',
  data() {
    return {
      favoriteMovies: [],
      isLoading: false,
      error: null,
      defaultPoster: NoPoster
    }
  },
  created() {
    this.fetchFavoriteMovies()
  },
  methods: {
    async fetchFavoriteMovies() {
      this.isLoading = true
      this.error = null
      
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.$router.push('/login')
          return
        }

        const response = await axios.get('http://localhost:8080/api/favorites', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })

        const movieIds = response.data
        this.favoriteMovies = await Promise.all(
          movieIds.map(id => this.fetchMovieDetails(id))
        )
      } catch (error) {
        console.error('Error fetching favorites:', error)
        if (error.response?.status === 401) {
          localStorage.removeItem('token')
          this.$router.push('/login')
        } else {
          this.error = '즐겨찾기 목록을 불러오는데 실패했습니다.'
        }
      } finally {
        this.isLoading = false
      }
    },
    async fetchMovieDetails(movieId) {
      const response = await axios.get(`http://localhost:8080/api/movies/${movieId}`)
      return response.data
    },
    getImageUrl(path) {
      if (!path) return this.defaultPoster
      return `https://image.tmdb.org/t/p/w500${path}`
    },
    handleImageError(e) {
      e.target.src = this.defaultPoster
    },
    formatDate(date) {
      if (!date) return '개봉일 정보 없음'
      return new Date(date).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
  }
}
</script>

<style scoped>
.favorites {
  padding: 2rem 0;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  min-height: 100vh;
}

.favorites h2 {
  color: #C4A77D;
  font-weight: 600;
  margin-bottom: 2rem;
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

.text-muted {
  color: rgba(255, 255, 255, 0.7) !important;
}

.spinner-border {
  color: #C4A77D !important;
}

.alert-danger {
  background: rgba(220, 53, 69, 0.1);
  border: 1px solid rgba(220, 53, 69, 0.2);
  color: #ff6b6b;
}
</style> 