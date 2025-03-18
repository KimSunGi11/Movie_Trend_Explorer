<template>
  <div class="movies">
    <h1>Trending Movies</h1>
    <div class="row">
      <div v-for="movie in movies" :key="movie.id" class="col-md-4 mb-4">
        <div class="card">
          <img :src="getImageUrl(movie.posterPath)" class="card-img-top" :alt="movie.title">
          <div class="card-body">
            <h5 class="card-title">{{ movie.title }}</h5>
            <p class="card-text">{{ movie.overview.substring(0, 150) }}...</p>
            <router-link :to="'/movie/' + movie.id" class="btn btn-primary">View Details</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'MovieList',
  data() {
    return {
      movies: []
    }
  },
  created() {
    this.fetchMovies()
  },
  methods: {
    async fetchMovies() {
      try {
        const response = await axios.get('http://localhost:8080/api/movies/trending')
        if (response.data && Array.isArray(response.data.results)) {
          this.movies = response.data.results.map(movie => ({
            id: movie.id,
            title: movie.title,
            overview: movie.overview || '',
            posterPath: movie.posterPath,
            releaseDate: movie.releaseDate,
            voteAverage: movie.voteAverage,
            voteCount: movie.voteCount,
            popularity: movie.popularity
          }))
        } else {
          console.error('Invalid response format:', response.data)
        }
      } catch (error) {
        console.error('Error fetching movies:', error)
      }
    },
    getImageUrl(path) {
      return `https://image.tmdb.org/t/p/w500${path}`
    }
  }
}
</script>

<style scoped>
.card-img-top {
  height: 400px;
  object-fit: cover;
}
</style> 