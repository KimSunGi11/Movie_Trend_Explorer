<template>
  <div class="signup-container">
    <div class="signup-box">
      <h2>회원가입</h2>
      <form @submit.prevent="handleSignup" class="signup-form">
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
        <div class="form-group">
          <label for="email">이메일</label>
          <input
            type="email"
            id="email"
            v-model="email"
            required
            class="form-control"
            placeholder="이메일을 입력하세요"
          >
        </div>
        <div class="form-group">
          <label for="name">이름</label>
          <input
            type="text"
            id="name"
            v-model="name"
            required
            class="form-control"
            placeholder="이름을 입력하세요"
          >
        </div>
        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        <button type="submit" class="btn btn-primary btn-block">회원가입</button>
      </form>
      <div class="mt-3 text-center">
        <p>이미 계정이 있으신가요? <router-link to="/login">로그인</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Signup',
  data() {
    return {
      username: '',
      password: '',
      email: '',
      name: '',
      error: null
    }
  },
  methods: {
    async handleSignup() {
      try {
        await axios.post('http://localhost:8080/api/auth/signup', {
          username: this.username,
          password: this.password,
          email: this.email,
          name: this.name
        })

        // 회원가입 성공 후 로그인 페이지로 이동
        this.$router.push('/login')
      } catch (error) {
        this.error = '회원가입에 실패했습니다. 다시 시도해주세요.'
        console.error('Signup error:', error)
      }
    }
  }
}
</script>

<style scoped>
.signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(to right, #01b4e4, #0d253f);
}

.signup-box {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.signup-box h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #0d253f;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.btn-block {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  background: #01b4e4;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-block:hover {
  background: #0d253f;
}

.alert {
  padding: 0.75rem;
  margin-bottom: 1rem;
  border-radius: 4px;
}

.alert-danger {
  background-color: #f8d7da;
  border-color: #f5c6cb;
  color: #721c24;
}
</style> 