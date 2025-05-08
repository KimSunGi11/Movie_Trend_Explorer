<template>
  <div class="login-container">
    <div class="login-box">
      <h2>로그인</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">아이디</label>
          <input
            type="text"
            id="username"
            v-model="username"
            required
            class="form-control"
            placeholder="아이디를 입력하세요"
          >
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input
            type="password"
            id="password"
            v-model="password"
            required
            class="form-control"
            placeholder="비밀번호를 입력하세요"
          >
        </div>
        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        <button type="submit" class="btn btn-primary btn-block">로그인</button>
      </form>
      <div class="mt-3 text-center">
        <p>계정이 없으신가요? <router-link to="/signup">회원가입</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import axios from 'axios'

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      error: null
    }
  },
  methods: {
    ...mapActions(['login']),
    async handleLogin() {
      try {
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          username: this.username,
          password: this.password
        });
        
        console.log('Login response:', response.data); // 로그인 응답 확인
        
        const { token } = response.data;
        localStorage.setItem('token', token);
        
        // 사용자 정보 가져오기
        const userResponse = await axios.get('http://localhost:8080/api/auth/user', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        console.log('User info response:', userResponse.data); // 사용자 정보 응답 확인
        
        // Vuex store에 사용자 정보 저장
        this.$store.commit('setUser', userResponse.data);
        this.$store.commit('setLoggedIn', true);
        this.$store.commit('setToken', token);
        
        console.log('Store state after login:', this.$store.state); // store 상태 확인
        
        this.$router.push('/');
      } catch (error) {
        this.error = '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.'
        console.error('Login error:', error)
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
}

.login-box {
  background: rgba(255, 255, 255, 0.1);
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #C4A77D;
  font-weight: 600;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #C4A77D;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid rgba(196, 167, 125, 0.3);
  border-radius: 8px;
  font-size: 1rem;
  background: rgba(255, 255, 255, 0.1);
  color: #FFFFFF;
  transition: all 0.3s ease;
}

.form-control:focus {
  background: rgba(255, 255, 255, 0.15);
  border-color: #C4A77D;
  box-shadow: 0 0 0 3px rgba(196, 167, 125, 0.2);
  outline: none;
}

.form-control::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.btn-block {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  background: linear-gradient(135deg, #C4A77D, #8B7355);
  border: none;
  border-radius: 8px;
  color: #FFFFFF;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.btn-block:hover {
  background: linear-gradient(135deg, #8B7355, #C4A77D);
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.alert {
  padding: 0.75rem;
  margin-bottom: 1rem;
  border-radius: 8px;
}

.alert-danger {
  background: rgba(220, 53, 69, 0.1);
  border: 1px solid rgba(220, 53, 69, 0.2);
  color: #ff6b6b;
}

.mt-3 {
  margin-top: 1.5rem;
}

.text-center {
  text-align: center;
}

.text-center p {
  color: rgba(255, 255, 255, 0.7);
}

.text-center a {
  color: #C4A77D;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.text-center a:hover {
  color: #8B7355;
  text-decoration: underline;
}
</style> 