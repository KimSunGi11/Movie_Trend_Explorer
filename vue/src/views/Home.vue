<template>
  <div class="home">
    <!-- Hero Section with Search -->
    <section class="hero">
      <div class="container">
        <div class="hero-content text-center">
          <h1>Welcome to Movie Trend Explorer</h1>
          <h2>Millions of movies to discover. Explore now.</h2>
          <div class="search-container">
            <input 
              type="text" 
              class="form-control search-input" 
              placeholder="Search for a movie..."
              v-model="searchQuery"
              @keyup.enter="handleSearch"
            >
            <button class="btn btn-primary search-button" @click="handleSearch">Search</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Popular Movies Section -->
    <section class="popular-movies">
      <div class="container">
        <h2 class="section-title">Popular Movies</h2>
        
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
    </section>
  </div>
</template>

<script>
import axios from 'axios'
import NoPoster from '@/assets/no-poster.svg'

export default {
  name: 'Home',
  data() {
    return {
      movies: [],
      searchQuery: '',
      isLoading: false,
      error: null,
      defaultPoster: NoPoster
    }
  },
  created() {
    this.fetchPopularMovies()
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
          this.error = '영화 데이터 형식이 올바르지 않습니다.'
          console.error('Invalid data format:', response.data)
        }
      } catch (error) {
        console.error('Error fetching movies:', error)
        this.error = '영화 데이터를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.'
      } finally {
        this.isLoading = false
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
    handleSearch() {
      if (this.searchQuery.trim()) {
        this.$router.push(`/search?query=${encodeURIComponent(this.searchQuery.trim())}`)
      }
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
  margin-bottom: 3rem;
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
  gap: 1rem;
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  font-size: 1.2rem;
  padding: 1rem;
  border-radius: 30px;
  border: none;
}

.search-button {
  padding: 0 2rem;
  border-radius: 30px;
  font-size: 1.2rem;
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 2rem;
  font-weight: 600;
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