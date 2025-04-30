<template>
  <div class="movie-detail">
    <div v-if="isLoading" class="loading">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">로딩중...</span>
      </div>
    </div>
    
    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>
    
    <div v-else-if="movie" class="movie-content">
      <div class="backdrop" :style="{ backgroundImage: `url(${backdropUrl})` }">
        <div class="backdrop-overlay"></div>
      </div>
      
      <div class="container">
        <div class="movie-header">
          <div class="poster">
            <img :src="posterUrl" :alt="movie.title" @error="handleImageError">
          </div>
          
          <div class="movie-info">
            <h1>{{ movie.title }}</h1>
            <p class="original-title" v-if="movie.original_title !== movie.title">
              {{ movie.original_title }}
            </p>
            
            <div class="meta-info">
              <span class="release-date">
                <i class="fas fa-calendar"></i>
                {{ formatDate(movie.release_date) }}
              </span>
              <span class="rating">
                <i class="fas fa-star"></i>
                {{ movie.vote_average.toFixed(1) }}
                <small>({{ movie.vote_count }}명)</small>
              </span>
              <span class="popularity">
                <i class="fas fa-fire"></i>
                {{ Math.round(movie.popularity) }}
              </span>
              <span v-if="movie.runtime" class="runtime">
                <i class="fas fa-clock"></i>
                {{ movie.runtime }}분
              </span>
              <span v-if="movie.original_language" class="language">
                <i class="fas fa-language"></i>
                {{ getLanguageName(movie.original_language) }}
              </span>
              <span v-if="isAuthenticated" class="favorite">
                <button 
                  type="button" 
                  class="favorite-btn" 
                  @click="toggleFavorite"
                  :title="isFavorite ? '즐겨찾기 해제' : '즐겨찾기 추가'"
                >
                  <i :class="['fas', isFavorite ? 'fa-star' : 'fa-star', isFavorite ? 'text-warning' : 'text-muted']"></i>
                  <span class="favorite-count">{{ favoriteCount }}</span>
                </button>
              </span>
            </div>
            
            <div class="genres" v-if="movie.genres && movie.genres.length">
              <h3>장르</h3>
              <div class="genre-tags">
                <span v-for="genre in movie.genres" :key="genre.id" class="genre-tag">
                  {{ genre.name }}
                </span>
              </div>
            </div>

            <div class="keywords" v-if="movie.keywordList && movie.keywordList.length">
              <h3>키워드</h3>
              <div class="keyword-tags">
                <span v-for="keyword in movie.keywordList" :key="keyword.id" class="keyword-tag">
                  {{ keyword.name }}
                </span>
              </div>
            </div>
            
            <div class="overview">
              <h2>줄거리</h2>
              <p>{{ movie.overview || '줄거리가 없습니다.' }}</p>
            </div>
          </div>
        </div>
        <CommentSection :movieId="$route.params.id" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import NoPoster from '@/assets/no-poster.svg'
import CommentSection from '@/components/CommentSection.vue'

export default {
  name: 'MovieDetail',
  components: {
    CommentSection
  },
  data() {
    return {
      movie: null,
      isLoading: true,
      error: null,
      defaultPoster: NoPoster,
      isFavorite: false,
      favoriteCount: 0
    }
  },
  computed: {
    posterUrl() {
      if (!this.movie?.poster_path) return this.defaultPoster
      return `https://image.tmdb.org/t/p/w500${this.movie.poster_path}`
    },
    backdropUrl() {
      if (!this.movie?.backdrop_path) return ''
      return `https://image.tmdb.org/t/p/original${this.movie.backdrop_path}`
    },
    isAuthenticated() {
      return !!localStorage.getItem('token')
    }
  },
  created() {
    this.fetchMovieDetails()
  },
  methods: {
    async fetchMovieDetails() {
      this.isLoading = true
      this.error = null
      
      try {
        const movieId = this.$route.params.id
        const response = await axios.get(`http://localhost:8080/api/movies/${movieId}`)
        this.movie = response.data
        // 영화 정보를 가져온 후 즐겨찾기 상태와 카운트 가져오기
        await Promise.all([
          this.getFavoriteCount(),
          this.checkFavoriteStatus()
        ])
      } catch (error) {
        console.error('Error fetching movie details:', error)
        this.error = '영화 정보를 불러오는데 실패했습니다. 잠시 후 다시 시도해주세요.'
      } finally {
        this.isLoading = false
      }
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
    },
    async checkFavoriteStatus() {
      if (!this.isAuthenticated) return
      
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          `http://localhost:8080/api/favorites/${this.movie.id}/check`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )
        this.isFavorite = response.data
      } catch (error) {
        console.error('Error checking favorite status:', error)
      }
    },
    async getFavoriteCount() {
      try {
        const response = await axios.get(`http://localhost:8080/api/favorites/${this.movie.id}/count`)
        this.favoriteCount = response.data
      } catch (error) {
        console.error('Error getting favorite count:', error)
        this.favoriteCount = 0
      }
    },
    async toggleFavorite() {
      try {
        if (!this.isAuthenticated) {
          this.$router.push('/login');
          return;
        }

        const token = localStorage.getItem('token');
        if (!token) {
          this.$router.push('/login');
          return;
        }

        const response = await axios.post(
          `http://localhost:8080/api/favorites/${this.movie.id}/toggle`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        );

        if (response.status === 200) {
          this.isFavorite = !this.isFavorite;
          // 즐겨찾기 카운트 업데이트
          if (this.isFavorite) {
            this.favoriteCount++;
          } else {
            this.favoriteCount--;
          }
        }
      } catch (error) {
        console.error('즐겨찾기 토글 중 오류 발생:', error);
        if (error.response?.status === 401) {
          this.$router.push('/login');
        } else {
          this.error = '즐겨찾기 상태 변경에 실패했습니다. 잠시 후 다시 시도해주세요.';
        }
      }
    },
    getLanguageName(code) {
      const languages = {
        'ko': '한국어',
        'en': '영어',
        'ja': '일본어',
        'zh': '중국어',
        'fr': '프랑스어',
        'de': '독일어',
        'es': '스페인어',
        'it': '이탈리아어',
        'ru': '러시아어'
      };
      return languages[code] || code;
    }
  },
  watch: {
    '$route.params.id': {
      handler(newId) {
        if (newId) {
          this.fetchMovieDetails()
        }
      },
      immediate: true
    }
  }
}
</script>

<style scoped>
.movie-detail {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.error-message {
  text-align: center;
  padding: 2rem;
  color: #dc3545;
}

.movie-content {
  position: relative;
}

.backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 500px;
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.backdrop-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.7), rgba(0,0,0,0.9));
}

.container {
  position: relative;
  z-index: 1;
  padding-top: 2rem;
}

.movie-header {
  display: flex;
  gap: 2rem;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 200px;
}

.poster {
  flex-shrink: 0;
  width: 300px;
}

.poster img {
  width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.movie-info {
  flex-grow: 1;
}

h1 {
  margin: 0 0 0.5rem;
  font-size: 2.5rem;
  color: #2c3e50;
}

.original-title {
  font-size: 1.2rem;
  color: #6c757d;
  margin-bottom: 1rem;
}

.meta-info {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  color: #495057;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.meta-info i {
  color: #01b4e4;
}

.overview {
  margin-top: 2rem;
}

.overview h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.overview p {
  line-height: 1.6;
  color: #495057;
}

@media (max-width: 768px) {
  .movie-header {
    flex-direction: column;
    padding: 1rem;
  }
  
  .poster {
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
  }
  
  .backdrop {
    height: 300px;
  }
  
  .movie-header {
    margin-top: 100px;
  }
  
  h1 {
    font-size: 2rem;
  }
  
  .meta-info {
    flex-wrap: wrap;
    gap: 1rem;
  }
}

.movie-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 0.5rem;
}

.rating, .favorite-count {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.favorite-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: transform 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.favorite-btn:hover {
  transform: scale(1.1);
}

.favorite-btn i {
  font-size: 1.2rem;
  color: #6c757d;
}

.favorite-btn i.text-warning {
  color: #ffc107;
}

.favorite-count {
  font-size: 0.9rem;
  color: #6c757d;
}

.favorite-btn:hover i {
  color: #ffc107;
}

.genres, .keywords {
  margin: 1rem 0;
}

.genres h3, .keywords h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  color: #666;
}

.genre-tags, .keyword-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.genre-tag, .keyword-tag {
  background-color: #e9ecef;
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  font-size: 0.9rem;
  color: #495057;
}

.runtime, .language {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
}

.runtime i, .language i {
  color: #666;
}
</style> 