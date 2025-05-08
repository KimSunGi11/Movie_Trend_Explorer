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
      favoriteCount: 0,
      viewStartTime: null,
      viewEndTime: null
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
    this.startViewTimer()
  },
  beforeDestroy() {
    this.stopViewTimer()
    this.recordViewDuration()
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
    },
    startViewTimer() {
      this.viewStartTime = Date.now()
    },
    stopViewTimer() {
      this.viewEndTime = Date.now()
    },
    recordViewDuration() {
      const token = localStorage.getItem('token')
      if (!token) return

      const viewDuration = Math.floor((this.viewEndTime - this.viewStartTime) / 1000)
      const genres = this.movie.genres?.map(g => g.name) || []
      const keywords = this.movie.keywordList?.map(k => k.name) || []

      axios.post('http://localhost:8080/api/movies/record-view', null, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        params: {
          movieId: this.movie.id,
          movieTitle: this.movie.title,
          genres: genres.join(','),
          keywords: keywords.join(','),
          viewDuration
        }
      }).catch(error => {
        console.error('Error recording view duration:', error)
      })
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
  padding: 0;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  min-height: 100vh;
  position: relative;
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
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 500px;
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.backdrop-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 500px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.7), rgba(0,0,0,0.9));
  z-index: 1;
}

.container {
  position: relative;
  z-index: 2;
  padding-top: 2rem;
}

.movie-header {
  position: relative;
  margin-bottom: 2rem;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.backdrop-image {
  width: 100%;
  height: 500px;
  object-fit: cover;
  filter: brightness(0.4);
}

.movie-info-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 2rem;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.9), transparent);
  color: #FFFFFF;
}

.movie-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #C4A77D;
}

.movie-meta {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  color: rgba(255, 255, 255, 0.7);
}

.movie-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #C4A77D;
  font-weight: 600;
}

.movie-content {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 2rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(196, 167, 125, 0.2);
  margin-bottom: 2rem;
}

.movie-overview {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  margin-bottom: 2rem;
}

.movie-genres {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
}

.genre-tag {
  background: rgba(196, 167, 125, 0.2);
  color: #C4A77D;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  border: 1px solid rgba(196, 167, 125, 0.3);
}

.movie-cast {
  margin-top: 2rem;
}

.cast-title {
  color: #C4A77D;
  font-weight: 600;
  margin-bottom: 1.5rem;
}

.cast-list {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
  padding-bottom: 1rem;
  scrollbar-width: thin;
  scrollbar-color: #C4A77D rgba(255, 255, 255, 0.1);
}

.cast-list::-webkit-scrollbar {
  height: 8px;
}

.cast-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.cast-list::-webkit-scrollbar-thumb {
  background: #C4A77D;
  border-radius: 4px;
}

.cast-list::-webkit-scrollbar-thumb:hover {
  background: #8B7355;
}

.cast-card {
  flex: 0 0 150px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid rgba(196, 167, 125, 0.1);
}

.cast-card:hover {
  transform: translateY(-5px);
  border-color: #C4A77D;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.cast-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.cast-info {
  padding: 1rem;
}

.cast-name {
  color: #C4A77D;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.cast-character {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.movie-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-favorite {
  background: linear-gradient(135deg, #C4A77D, #8B7355);
  border: none;
  color: #FFFFFF;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-favorite:hover {
  background: linear-gradient(135deg, #8B7355, #C4A77D);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.btn-favorite.active {
  background: linear-gradient(135deg, #8B7355, #C4A77D);
}

.loading-spinner {
  color: #C4A77D !important;
}

.error-message {
  background: rgba(220, 53, 69, 0.1);
  border: 1px solid rgba(220, 53, 69, 0.2);
  color: #ff6b6b;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
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
  background: rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(196, 167, 125, 0.2);
}

h1 {
  margin: 0 0 0.5rem;
  font-size: 2.5rem;
  color: #C4A77D;
  font-weight: 700;
}

.original-title {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 1rem;
}

.meta-info {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  color: rgba(255, 255, 255, 0.9);
  flex-wrap: wrap;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(196, 167, 125, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.meta-info i {
  color: #C4A77D;
}

.overview {
  margin-top: 2rem;
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.overview h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #C4A77D;
}

.overview p {
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.9);
}

.genres, .keywords {
  margin: 1.5rem 0;
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.genres h3, .keywords h3 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #C4A77D;
}

.genre-tags, .keyword-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.genre-tag, .keyword-tag {
  background: rgba(196, 167, 125, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(196, 167, 125, 0.2);
  transition: all 0.3s ease;
}

.genre-tag:hover, .keyword-tag:hover {
  background: rgba(196, 167, 125, 0.2);
  transform: translateY(-2px);
}

.comments-section {
  background: rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(196, 167, 125, 0.2);
  margin-top: 2rem;
}

.comments-section h3 {
  color: #C4A77D;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.login-prompt {
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.login-prompt p {
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.login-prompt a {
  color: #C4A77D;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-prompt a:hover {
  color: #8B7355;
  text-decoration: underline;
}

.text-muted {
  color: rgba(255, 255, 255, 0.7) !important;
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
  padding: 0.5rem 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.favorite-btn:hover {
  transform: scale(1.05);
}

.favorite-btn i {
  font-size: 1.2rem;
  color: #C4A77D;
}

.favorite-btn i.text-warning {
  color: #C4A77D;
}

.favorite-count {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(0, 0, 0, 0.2);
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  margin-left: 0.5rem;
}

.comment-item {
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.comment-author {
  color: #C4A77D;
  font-weight: 600;
  font-size: 1.1rem;
}

.comment-date {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
  background: rgba(0, 0, 0, 0.3);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
}

.comment-content {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  margin-bottom: 1rem;
}

.comment-actions {
  display: flex;
  gap: 1rem;
}

.comment-action-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.comment-action-btn:hover {
  color: #C4A77D;
}

.comment-form {
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  margin-top: 2rem;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.comment-form textarea {
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(196, 167, 125, 0.3);
  border-radius: 8px;
  padding: 1rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 1rem;
  resize: vertical;
  min-height: 100px;
}

.comment-form textarea:focus {
  outline: none;
  border-color: #C4A77D;
  box-shadow: 0 0 0 2px rgba(196, 167, 125, 0.2);
}

.comment-form button {
  background: linear-gradient(135deg, #C4A77D, #8B7355);
  border: none;
  color: #FFFFFF;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.comment-form button:hover {
  background: linear-gradient(135deg, #8B7355, #C4A77D);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}
</style> 