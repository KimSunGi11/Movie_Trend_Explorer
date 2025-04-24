<template>
  <div class="search-container">
    <div class="search-bar">
      <input
        type="text"
        v-model="searchQuery"
        @input="onInput"
        @keyup.enter="search"
        placeholder="영화 검색..."
        class="search-input"
      />
      <button @click="search" class="search-button">
        <i class="fas fa-search"></i>
      </button>
    </div>
    <!-- 자동완성 결과 -->
    <div v-if="showSuggestions && suggestions.length > 0" class="suggestions">
      <div
        v-for="movie in suggestions"
        :key="movie.id"
        class="suggestion-item"
        @click="selectSuggestion(movie)"
      >
        <img
          :src="movie.poster_path ? `https://image.tmdb.org/t/p/w92${movie.poster_path}` : require('@/assets/no-poster.svg')"
          :alt="movie.title"
          class="suggestion-poster"
        />
        <div class="suggestion-info">
          <div class="suggestion-title">{{ movie.title }}</div>
          <div class="suggestion-year">{{ movie.release_date ? movie.release_date.substring(0, 4) : '연도 미상' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { debounce } from 'lodash'

export default {
  name: 'SearchBar',
  data() {
    return {
      searchQuery: '',
      suggestions: [],
      showSuggestions: false
    }
  },
  methods: {
    onInput: debounce(async function() {
      if (this.searchQuery.length < 2) {
        this.suggestions = []
        this.showSuggestions = false
        return
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/movies/autocomplete?query=${encodeURIComponent(this.searchQuery)}`)
        this.suggestions = response.data
        this.showSuggestions = true
      } catch (error) {
        console.error('Error fetching suggestions:', error)
        this.suggestions = []
        this.showSuggestions = false
      }
    }, 300),

    selectSuggestion(movie) {
      this.searchQuery = movie.title
      this.showSuggestions = false
      this.$router.push(`/movie/${movie.id}`)
    },

    search() {
      if (this.searchQuery.trim()) {
        this.$router.push(`/search?query=${encodeURIComponent(this.searchQuery.trim())}`)
      }
    }
  },
  created() {
    // 클릭 이벤트 리스너 추가
    document.addEventListener('click', (e) => {
      if (!this.$el.contains(e.target)) {
        this.showSuggestions = false
      }
    })
  },
  beforeDestroy() {
    // 클릭 이벤트 리스너 제거
    document.removeEventListener('click', this.handleClickOutside)
  }
}
</script>

<style scoped>
.search-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 30px;
  padding: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.search-input {
  flex: 1;
  border: none;
  padding: 10px 15px;
  font-size: 16px;
  border-radius: 30px;
  outline: none;
}

.search-button {
  background: none;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  color: #666;
}

.search-button:hover {
  color: #000;
}

.suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 5px;
  max-height: 400px;
  overflow-y: auto;
  z-index: 1000;
}

.suggestion-item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.suggestion-item:hover {
  background-color: #f5f5f5;
}

.suggestion-poster {
  width: 45px;
  height: 68px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 10px;
}

.suggestion-info {
  flex: 1;
}

.suggestion-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.suggestion-year {
  font-size: 14px;
  color: #666;
}
</style> 