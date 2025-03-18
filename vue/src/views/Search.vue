<template>
  <div class="search">
    <div class="container">
      <h1 class="mb-4">검색 결과: "{{ $route.query.query }}"</h1>
      <div class="row">
        <div v-for="movie in movies" :key="movie.id" class="col-md-3 mb-4">
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
  name: 'Search',
  data() {
    return {
      movies: [],
      defaultPoster: NoPoster
    }
  },
  watch: {
    '$route.query.query': {
      immediate: true,
      handler(newQuery) {
        if (newQuery) {
          this.searchMovies(newQuery)
        }
      }
    }
  },
  methods: {
    async searchMovies(query) {
      try {
        const response = await axios.get(`http://localhost:8080/api/movies/search?query=${encodeURIComponent(query)}`)
        if (response.data && Array.isArray(response.data.results)) {
          this.movies = response.data.results
        }
      } catch (error) {
        console.error('Error searching movies:', error)
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
    }
  }
}
</script>

<style scoped>
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