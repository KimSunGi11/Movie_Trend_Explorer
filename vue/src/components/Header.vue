<template>
  <header class="header">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <router-link class="navbar-brand" to="/">MTE</router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <router-link class="nav-link" to="/">홈</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/community">커뮤니티</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/advanced-search">
                <i class="fas fa-search"></i> 영화찾기
              </router-link>
            </li>
            <li class="nav-item" v-if="isAuthenticated">
              <router-link to="/favorites" class="nav-link">
                <i class="fas fa-heart"></i> 즐겨찾기
              </router-link>
            </li>
            <li class="nav-item" v-if="isAdmin">
              <router-link to="/admin" class="nav-link admin-link">
                <i class="fas fa-user-shield"></i> 관리자 페이지
              </router-link>
            </li>
          </ul>
          <ul class="navbar-nav">
            <template v-if="isAuthenticated">
              <li class="nav-item">
                <span class="nav-link">{{ currentUser?.name || '사용자' }}</span>
              </li>
              <li class="nav-item">
                <a class="nav-link" href="#" @click.prevent="handleLogout">로그아웃</a>
              </li>
            </template>
            <template v-else>
              <li class="nav-item">
                <router-link class="nav-link" to="/login">로그인</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/signup">회원가입</router-link>
              </li>
            </template>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Header',
  computed: {
    ...mapGetters(['isAuthenticated', 'currentUser', 'isAdmin'])
  },
  watch: {
    isAuthenticated(newVal) {
      console.log('isAuthenticated changed:', newVal);
      console.log('currentUser:', this.currentUser);
      console.log('isAdmin:', this.isAdmin);
    }
  },
  methods: {
    ...mapActions(['logout']),
    async handleLogout() {
      await this.logout()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.header {
  margin-bottom: 0;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
}

.navbar {
  margin-bottom: 0;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
  border-bottom: 1px solid rgba(196, 167, 125, 0.1);
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-link {
  cursor: pointer;
}

.admin-link {
  color: #ffd700 !important;
  font-weight: bold;
}

.admin-link:hover {
  color: #ffed4a !important;
}
</style> 