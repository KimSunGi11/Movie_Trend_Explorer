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
  color: #2c3e50;
  min-height: 100vh;
}

body {
  margin: 0;
  padding: 0;
  background-color: #f8f9fa;
}

.navbar {
  background: linear-gradient(to right, #01b4e4, #0d253f);
  padding: 1rem 0;
  margin-bottom: 2rem;
}

.navbar-brand {
  font-weight: 600;
  font-size: 1.5rem;
}

.nav-link {
  color: rgba(255, 255, 255, 0.8) !important;
  transition: color 0.2s;
}

.nav-link:hover {
  color: white !important;
}

.btn-outline-light {
  border-width: 2px;
}

.btn-light {
  background-color: white;
  color: #0d253f;
  border: none;
}

.btn-light:hover {
  background-color: #e9ecef;
  color: #0d253f;
}
</style> 