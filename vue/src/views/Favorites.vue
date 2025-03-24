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
        <div v-for="movie in favoriteMovies" :key="movie.id" class="col-md-4 mb-4">
          <div class="card h-100">
            <img :src="movie.posterPath" class="card-img-top" :alt="movie.title">
            <div class="card-body">
              <h5 class="card-title">{{ movie.title }}</h5>
              <p class="card-text">{{ movie.overview }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <div class="rating">
                  <i class="fas fa-star text-warning"></i>
                  <span class="ms-1">{{ movie.voteAverage }}</span>
                </div>
                <button class="btn btn-outline-danger" @click="removeFavorite(movie.id)">
                  <i class="fas fa-heart"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Favorites',
  data() {
    return {
      favoriteMovies: [],
      isLoading: false,
      error: null
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
    async removeFavorite(movieId) {
      try {
        const token = localStorage.getItem('token')
        await axios.post(
          `http://localhost:8080/api/favorites/${movieId}/toggle`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )
        
        this.favoriteMovies = this.favoriteMovies.filter(movie => movie.id !== movieId)
      } catch (error) {
        console.error('Error removing favorite:', error)
        this.error = '즐겨찾기 해제에 실패했습니다.'
      }
    }
  }
}
</script>

<style scoped>
.favorites {
  padding: 2rem 0;
}

.card {
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-5px);
}

.card-img-top {
  height: 400px;
  object-fit: cover;
}

.card-title {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}

.card-text {
  font-size: 0.9rem;
  color: #6c757d;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rating {
  font-size: 0.9rem;
  color: #ffc107;
}

.btn-outline-danger {
  padding: 0.25rem 0.5rem;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  color: white;
}
</style> 