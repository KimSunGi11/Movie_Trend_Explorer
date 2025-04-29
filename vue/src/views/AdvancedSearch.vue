<template>
  <div class="advanced-search">
    <div class="container">
      <div class="row">
        <!-- 필터 사이드바 -->
        <div class="col-md-3">
          <div class="filters-sidebar">
            <h3>필터</h3>
            
            <!-- 정렬 옵션 -->
            <div class="filter-section">
              <h4>정렬</h4>
              <select v-model="filters.sortBy" class="form-select">
                <option value="popularity.desc">인기도 내림차순</option>
                <option value="popularity.asc">인기도 오름차순</option>
                <option value="vote_average.desc">평점 내림차순</option>
                <option value="vote_average.asc">평점 오름차순</option>
                <option value="release_date.desc">개봉일 내림차순</option>
                <option value="release_date.asc">개봉일 오름차순</option>
                <option value="title.asc">제목 (A-Z)</option>
                <option value="title.desc">제목 (Z-A)</option>
              </select>
            </div>

            <!-- 장르 필터 -->
            <div class="filter-section">
              <h4>장르</h4>
              <div class="genre-list">
                <div v-for="genre in genres" :key="genre.id" class="form-check">
                  <input 
                    type="checkbox" 
                    :id="'genre-' + genre.id"
                    v-model="filters.genres"
                    :value="genre.id"
                    class="form-check-input"
                  >
                  <label :for="'genre-' + genre.id" class="form-check-label">
                    {{ genre.name }}
                  </label>
                </div>
              </div>
            </div>

            <!-- 언어 필터 -->
            <div class="filter-section">
              <h4>언어</h4>
              <select v-model="filters.language" class="form-select">
                <option value="">모든 언어</option>
                <option value="ko">한국어</option>
                <option value="en">영어</option>
                <option value="ja">일본어</option>
                <option value="zh">중국어</option>
                <option value="fr">프랑스어</option>
                <option value="de">독일어</option>
                <option value="es">스페인어</option>
                <option value="it">이탈리아어</option>
                <option value="ru">러시아어</option>
              </select>
            </div>

            <!-- 사용자 점수 필터 -->
            <div class="filter-section">
              <h4>최소 사용자 점수</h4>
              <input 
                type="range" 
                v-model="filters.minUserScore" 
                class="form-range" 
                min="0" 
                max="10" 
                step="0.1"
              >
              <div class="text-center">{{ filters.minUserScore }}</div>
            </div>

            <!-- 최소 즐겨찾기 수 필터 -->
            <div class="filter-section">
              <h4>최소 즐겨찾기 수</h4>
              <input 
                type="number" 
                v-model="filters.minFavoriteCount" 
                class="form-control" 
                min="0"
                placeholder="최소 즐겨찾기 수"
              >
            </div>

            <!-- 상영시간 필터 -->
            <div class="filter-section">
              <h4>상영시간</h4>
              <div class="row">
                <div class="col">
                  <input 
                    type="number" 
                    v-model="filters.minRuntime" 
                    class="form-control" 
                    placeholder="최소"
                    min="0"
                  >
                </div>
                <div class="col">
                  <input 
                    type="number" 
                    v-model="filters.maxRuntime" 
                    class="form-control" 
                    placeholder="최대"
                    min="0"
                  >
                </div>
              </div>
            </div>

            <!-- 키워드 필터 -->
            <div class="filter-section">
              <h4>키워드</h4>
              <input 
                type="text" 
                v-model="filters.keyword" 
                class="form-control" 
                placeholder="키워드 입력"
              >
            </div>

            <!-- 검색 버튼 -->
            <button 
              @click="search" 
              class="btn btn-primary w-100 mt-3"
              :disabled="isLoading"
            >
              <i class="fas fa-search"></i> 검색
            </button>
          </div>
        </div>

        <!-- 검색 결과 -->
        <div class="col-md-9">
          <div v-if="isLoading" class="text-center">
            <div class="spinner-border text-primary" role="status">
              <span class="sr-only">로딩중...</span>
            </div>
          </div>

          <div v-else-if="error" class="alert alert-danger">
            {{ error }}
          </div>

          <div v-else-if="movies.length === 0" class="text-center text-muted">
            검색 결과가 없습니다.
          </div>

          <div v-else class="row">
            <div v-for="movie in movies" :key="movie.id" class="col-md-4 mb-4">
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

          <!-- 페이지네이션 -->
          <div v-if="totalPages > 1" class="d-flex justify-content-center mt-4">
            <nav aria-label="Page navigation">
              <ul class="pagination">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">이전</a>
                </li>
                <li 
                  v-for="page in displayedPages" 
                  :key="page"
                  class="page-item"
                  :class="{ active: currentPage === page }"
                >
                  <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                  <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">다음</a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import NoPoster from '@/assets/no-poster.svg'

export default {
  name: 'AdvancedSearch',
  data() {
    return {
      movies: [],
      genres: [],
      isLoading: false,
      error: null,
      defaultPoster: NoPoster,
      currentPage: 1,
      totalPages: 1,
      filters: {
        sortBy: 'popularity.desc',
        genres: [],
        language: '',
        minUserScore: 0,
        minFavoriteCount: 0,
        minRuntime: null,
        maxRuntime: null,
        keyword: ''
      },
      genreMap: {
        28: '액션',
        12: '모험',
        16: '애니메이션',
        35: '코미디',
        80: '범죄',
        99: '다큐멘터리',
        18: '드라마',
        10751: '가족',
        14: '판타지',
        36: '역사',
        27: '공포',
        10402: '음악',
        9648: '미스터리',
        10749: '로맨스',
        878: 'SF',
        10770: 'TV 영화',
        53: '스릴러',
        10752: '전쟁',
        37: '서부'
      }
    }
  },
  computed: {
    displayedPages() {
      const pages = []
      const maxDisplayed = 5
      let start = Math.max(1, this.currentPage - Math.floor(maxDisplayed / 2))
      let end = Math.min(this.totalPages, start + maxDisplayed - 1)
      
      if (end - start + 1 < maxDisplayed) {
        start = Math.max(1, end - maxDisplayed + 1)
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      
      return pages
    }
  },
  created() {
    this.fetchGenres()
    this.search()
  },
  methods: {
    async fetchGenres() {
      try {
        const response = await axios.get('http://localhost:8080/api/movies/genres')
        this.genres = response.data.map(genre => ({
          ...genre,
          name: this.genreMap[genre.id] || genre.name
        }))
      } catch (error) {
        console.error('Error fetching genres:', error)
        this.error = '장르 목록을 불러오는데 실패했습니다.'
      }
    },
    async search() {
      this.isLoading = true
      this.error = null
      
      try {
        const params = {
          page: this.currentPage,
          sort_by: this.filters.sortBy,
          with_genres: this.filters.genres.join(','),
          with_original_language: this.filters.language,
          vote_average_gte: this.filters.minUserScore,
          min_favorite_count: this.filters.minFavoriteCount,
          keyword: this.filters.keyword
        }
        
        if (this.filters.minRuntime) {
          params.with_runtime_gte = this.filters.minRuntime
        }
        if (this.filters.maxRuntime) {
          params.with_runtime_lte = this.filters.maxRuntime
        }
        
        const response = await axios.get('http://localhost:8080/api/movies/discover', { params })
        this.movies = response.data.results
        this.totalPages = response.data.total_pages
      } catch (error) {
        console.error('Error searching movies:', error)
        this.error = '영화 검색에 실패했습니다.'
      } finally {
        this.isLoading = false
      }
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
        this.search()
      }
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
  },
  watch: {
    filters: {
      deep: true,
      handler() {
        this.currentPage = 1
        this.search()
      }
    }
  }
}
</script>

<style scoped>
.advanced-search {
  padding: 2rem 0;
}

.filters-sidebar {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 2rem;
}

.filter-section {
  margin-bottom: 1.5rem;
}

.filter-section h4 {
  font-size: 1rem;
  margin-bottom: 0.75rem;
  color: #2c3e50;
}

.genre-list {
  max-height: 200px;
  overflow-y: auto;
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

.pagination {
  margin-bottom: 0;
}

.page-link {
  color: #007bff;
}

.page-item.active .page-link {
  background-color: #007bff;
  border-color: #007bff;
}
</style> 