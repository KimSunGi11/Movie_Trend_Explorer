<template>
  <div class="movie-detail" v-if="movie">
    <div class="row">
      <div class="col-md-4">
        <img :src="getImageUrl(movie.posterPath)" class="img-fluid" :alt="movie.title">
      </div>
      <div class="col-md-8">
        <h1>{{ movie.title }}</h1>
        <p class="lead">{{ movie.overview }}</p>
        <div class="movie-info">
          <p><strong>Release Date:</strong> {{ movie.releaseDate }}</p>
          <p><strong>Rating:</strong> {{ movie.voteAverage }}/10 ({{ movie.voteCount }} votes)</p>
          <p><strong>Popularity:</strong> {{ movie.popularity }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'MovieDetail',
  data() {
    return {
      movie: null
    }
  },
  created() {
    this.fetchMovieDetails()
  },
  methods: {
    async fetchMovieDetails() {
      try {
        const response = await axios.get(`http://localhost:8080/api/movies/${this.$route.params.id}`)
        this.movie = response.data
      } catch (error) {
        console.error('Error fetching movie details:', error)
      }
    },
    getImageUrl(path) {
      return `https://image.tmdb.org/t/p/w500${path}`
    }
  }
}
</script>

<style scoped>
.movie-detail {
  margin-top: 2rem;
}
.movie-info {
  margin-top: 2rem;
}
</style> 