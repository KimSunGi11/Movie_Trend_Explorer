<template>
  <div class="admin-page">
    <h1>관리자 페이지</h1>
    <div class="admin-content">
      <div class="users-section">
        <h2>사용자 목록</h2>
        <div v-if="loading" class="loading">로딩 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else class="users-table">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>사용자명</th>
                <th>이름</th>
                <th>권한</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id">
                <td>{{ user.id }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.name }}</td>
                <td>{{ user.role }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Admin',
  data() {
    return {
      users: [],
      loading: true,
      error: null
    };
  },
  async created() {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8080/api/users', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      this.users = response.data;
    } catch (error) {
      if (error.response && error.response.status === 403) {
        this.error = '관리자 권한이 필요합니다.';
        this.$router.push('/');
      } else {
        this.error = '사용자 목록을 불러오는데 실패했습니다.';
      }
    } finally {
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.admin-page {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  color: #2c3e50;
  margin-bottom: 2rem;
}

h2 {
  color: #34495e;
  margin-bottom: 1rem;
}

.admin-content {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.users-section {
  margin-bottom: 2rem;
}

.loading {
  text-align: center;
  color: #666;
  padding: 2rem;
}

.error {
  color: #e74c3c;
  text-align: center;
  padding: 1rem;
}

.users-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

tr:hover {
  background-color: #f8f9fa;
}
</style> 