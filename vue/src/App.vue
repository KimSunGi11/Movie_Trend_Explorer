<template>
  <div id="app">
    <Header />
    <router-view></router-view>
  </div>
</template>

<script>
import Header from '@/components/Header.vue'
import { mapState, mapActions } from 'vuex'

export default {
  name: 'App',
  components: {
    Header
  },
  computed: {
    ...mapState(['user', 'token'])
  },
  async created() {
    await this.$store.dispatch('initializeAuth')
  },
  methods: {
    ...mapActions(['logout']),
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      if (token) {
        // 토큰이 있으면 사용자 정보 가져오기
        this.$store.dispatch('fetchUserInfo')
      }
    }
  }
}
</script>

<style>
#app {
  font-family: 'Source Sans Pro', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #1A1A1A;
  min-height: 100vh;
  background-color: #F5F5F5;
}

body {
  margin: 0;
  padding: 0;
  background-color: #F5F5F5;
}

.navbar {
  background: linear-gradient(to right, #1A1A1A, #2C3E50);
  padding: 1.2rem 0;
  margin-bottom: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-brand {
  font-weight: 700;
  font-size: 1.6rem;
  color: #C4A77D !important;
  font-family: 'Playfair Display', serif;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9) !important;
  transition: all 0.3s ease;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.nav-link:hover {
  color: #C4A77D !important;
}

.btn-outline-light {
  border-width: 2px;
  border-color: #C4A77D;
  color: #C4A77D;
  transition: all 0.3s ease;
}

.btn-outline-light:hover {
  background-color: #C4A77D;
  color: #1A1A1A;
}

.btn-light {
  background-color: #C4A77D;
  color: #1A1A1A;
  border: none;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.btn-light:hover {
  background-color: #8B7355;
  color: #FFFFFF;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 추가적인 럭셔리 스타일 */
.card {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.movie-card {
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
  border: 1px solid rgba(196, 167, 125, 0.1);
}

.movie-card:hover {
  border-color: #C4A77D;
}

.movie-info {
  background: linear-gradient(to bottom, #ffffff, #f8f8f8);
}

.movie-title {
  color: #1A1A1A;
  font-weight: 600;
}

.movie-date {
  color: #8B7355;
}

.rating {
  color: #C4A77D;
}

.vote-count {
  color: #8B7355;
}

.search-container :deep(.search-input) {
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
  border: 1px solid rgba(196, 167, 125, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-container :deep(.search-input):focus {
  border-color: #C4A77D;
  box-shadow: 0 0 0 0.2rem rgba(196, 167, 125, 0.25);
}

.search-container :deep(.search-button) {
  background: linear-gradient(to right, #1A1A1A, #2C3E50);
  border: none;
  color: #C4A77D;
}

.search-container :deep(.search-button):hover {
  background: linear-gradient(to right, #2C3E50, #1A1A1A);
}

.category-tabs {
  background: linear-gradient(to right, #f8f8f8, #ffffff);
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
}

.nav-tabs .nav-link {
  color: #8B7355;
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
  border: 1px solid rgba(196, 167, 125, 0.1);
}

.nav-tabs .nav-link:hover {
  color: #C4A77D;
  background: linear-gradient(145deg, #f5f5f5, #ffffff);
}

.nav-tabs .nav-link.active {
  background: linear-gradient(to right, #1A1A1A, #2C3E50);
  color: #C4A77D;
  border: none;
}

.comment-section {
  background: linear-gradient(145deg, #ffffff, #f8f8f8);
  border: 1px solid rgba(196, 167, 125, 0.1);
  border-radius: 8px;
  padding: 1.5rem;
}

.comment-item {
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
  padding: 1rem 0;
}

.comment-header {
  color: #1A1A1A;
}

.comment-date {
  color: #8B7355;
}

.comment-content {
  color: #2C3E50;
}

.form-control {
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
  border: 1px solid rgba(196, 167, 125, 0.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-control:focus {
  border-color: #C4A77D;
  box-shadow: 0 0 0 0.2rem rgba(196, 167, 125, 0.25);
}

.pagination .page-link {
  color: #8B7355;
  background: linear-gradient(145deg, #ffffff, #f5f5f5);
  border: 1px solid rgba(196, 167, 125, 0.1);
}

.pagination .page-link:hover {
  color: #C4A77D;
  background: linear-gradient(145deg, #f5f5f5, #ffffff);
}

.pagination .page-item.active .page-link {
  background: linear-gradient(to right, #1A1A1A, #2C3E50);
  border-color: #C4A77D;
  color: #C4A77D;
}

.autocomplete-suggestions {
  background: linear-gradient(145deg, #ffffff, #f8f8f8);
  border: 1px solid rgba(196, 167, 125, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.suggestion-item {
  color: #1A1A1A;
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
}

.suggestion-item:hover {
  background: linear-gradient(145deg, #f5f5f5, #ffffff);
  color: #C4A77D;
}
</style> 