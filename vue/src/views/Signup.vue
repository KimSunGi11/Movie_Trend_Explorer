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
  background: linear-gradient(135deg, #1A1A1A, #2C3E50);
}

.signup-box {
  background: rgba(255, 255, 255, 0.1);
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.signup-box h2 {
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