<template>
  <div class="search-container">
    <div class="search-bar">
      <input
        type="text"
        v-model="searchQuery"
        @input="onInput"
        @compositionstart="onCompositionStart"
        @compositionupdate="onCompositionUpdate"
        @compositionend="onCompositionEnd"
        @keyup.enter="search"
        @keydown.up.prevent="navigateSuggestions(-1)"
        @keydown.down.prevent="navigateSuggestions(1)"
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
        v-for="(movie, index) in suggestions"
        :key="movie.id"
        class="suggestion-item"
        :class="{ 'selected': index === selectedIndex }"
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
      showSuggestions: false,
      isComposing: false,
      composingText: '',
      accumulatedText: '',
      selectedIndex: -1
    }
  },
  methods: {
    onCompositionStart(e) {
      this.isComposing = true
      this.composingText = e.data
      this.selectedIndex = -1
    },
    onCompositionUpdate(e) {
      this.composingText = e.data
      // 이전 입력값과 현재 입력 중인 값을 합쳐서 검색
      const searchText = this.accumulatedText + this.composingText
      this.fetchSuggestions(searchText)
    },
    onCompositionEnd(e) {
      this.isComposing = false
      this.searchQuery = e.target.value
      // 입력이 완료되면 누적 텍스트 업데이트
      this.accumulatedText = this.searchQuery
      this.composingText = ''
      this.fetchSuggestions(this.searchQuery)
    },
    onInput: debounce(function(e) {
      if (!this.isComposing) {
        this.searchQuery = e.target.value
        this.accumulatedText = this.searchQuery
        this.selectedIndex = -1
        this.fetchSuggestions(this.searchQuery)
      }
    }, 300),
    navigateSuggestions(direction) {
      if (!this.showSuggestions || this.suggestions.length === 0) return

      this.selectedIndex += direction

      // 범위 체크
      if (this.selectedIndex >= this.suggestions.length) {
        this.selectedIndex = 0
      } else if (this.selectedIndex < 0) {
        this.selectedIndex = this.suggestions.length - 1
      }

      // 선택된 항목이 보이도록 스크롤 조정
      this.$nextTick(() => {
        const selectedElement = this.$el.querySelector('.suggestion-item.selected')
        if (selectedElement) {
          selectedElement.scrollIntoView({ block: 'nearest', behavior: 'smooth' })
        }
      })
    },
    fetchSuggestions(query) {
      if (query.length < 1) {
        this.suggestions = []
        this.showSuggestions = false
        this.selectedIndex = -1
        return
      }

      try {
        axios.get(`http://localhost:8080/api/movies/autocomplete?query=${encodeURIComponent(query)}`)
          .then(response => {
            this.suggestions = response.data.slice(0, 10) // 최대 10개로 제한
            this.showSuggestions = true
            this.selectedIndex = -1
          })
          .catch(error => {
            console.error('Error fetching suggestions:', error)
            this.suggestions = []
            this.showSuggestions = false
            this.selectedIndex = -1
          })
      } catch (error) {
        console.error('Error fetching suggestions:', error)
        this.suggestions = []
        this.showSuggestions = false
        this.selectedIndex = -1
      }
    },
    selectSuggestion(movie) {
      this.searchQuery = movie.title
      this.accumulatedText = ''
      this.showSuggestions = false
      this.selectedIndex = -1
      this.$router.push(`/movie/${movie.id}`)
    },
    search() {
      if (this.searchQuery.trim()) {
        // Enter 키를 눌렀을 때 선택된 항목이 있다면 해당 항목으로 이동
        if (this.selectedIndex >= 0 && this.suggestions[this.selectedIndex]) {
          this.selectSuggestion(this.suggestions[this.selectedIndex])
          return
        }
        
        this.accumulatedText = ''
        this.selectedIndex = -1
        this.$router.push(`/search?query=${encodeURIComponent(this.searchQuery.trim())}`)
      }
    }
  },
  created() {
    // 클릭 이벤트 리스너 추가
    document.addEventListener('click', (e) => {
      if (!this.$el.contains(e.target)) {
        this.showSuggestions = false
        this.selectedIndex = -1
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

.suggestion-item:hover,
.suggestion-item.selected {
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
  color: #333;
}

.suggestion-year {
  font-size: 14px;
  color: #666;
}
</style> 