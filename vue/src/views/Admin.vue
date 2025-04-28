<template>
  <div class="admin-page">
    <div class="container">
      <h2 class="mb-4">관리자 페이지</h2>
      
      <div class="card">
        <div class="card-header">
          <h3 class="mb-0">유저 목록</h3>
        </div>
        <div class="card-body">
          <div v-if="isLoading" class="text-center">
            <div class="spinner-border text-primary" role="status">
              <span class="sr-only">로딩중...</span>
            </div>
          </div>
          <div v-else-if="error" class="alert alert-danger">
            {{ error }}
          </div>
          <div v-else class="table-responsive">
            <table class="table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>이름</th>
                  <th>이메일</th>
                  <th>역할</th>
                  <th>작업</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id" @click="showUserComments(user)">
                  <td>{{ user.id }}</td>
                  <td>{{ user.name }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.role }}</td>
                  <td>
                    <button class="btn btn-sm btn-danger" @click.stop="deleteUser(user.id)">
                      삭제
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- 유저 댓글 모달 -->
    <div class="modal" :class="{ 'show': showModal }" tabindex="-1" v-if="showModal">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ selectedUser?.name }}님의 댓글 목록</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div v-if="isLoading" class="text-center">
              <div class="spinner-border text-primary" role="status">
                <span class="sr-only">로딩중...</span>
              </div>
            </div>
            <div v-else-if="error" class="alert alert-danger">
              {{ error }}
            </div>
            <div v-else-if="userComments.length === 0" class="text-center text-muted">
              작성한 댓글이 없습니다.
            </div>
            <div v-else class="comments-list">
              <div v-for="comment in userComments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <span class="movie-title">
                    <router-link :to="{ name: 'MovieDetail', params: { id: comment.movieId }}">
                      {{ getMovieTitle(comment.movieId) }}
                    </router-link>
                  </span>
                  <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                  <button @click="deleteComment(comment.id)" class="btn btn-sm btn-danger">
                    삭제
                  </button>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade show" v-if="showModal"></div>
  </div>
</template>

<script>
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'Admin',
  data() {
    return {
      users: [],
      showModal: false,
      selectedUser: null,
      userComments: [],
      isLoading: false,
      error: null,
      movieTitles: {}
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'isAdmin'])
  },
  created() {
    this.checkAdminAccess()
    this.fetchUsers()
  },
  methods: {
    checkAdminAccess() {
      if (!this.isAuthenticated || !this.isAdmin) {
        this.$router.push('/')
      }
    },
    async fetchUsers() {
      this.isLoading = true
      this.error = null
      
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.error = '인증이 필요합니다. 로그인해주세요.'
          return
        }
        
        const response = await axios.get('http://localhost:8080/api/admin/users', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        this.users = response.data
      } catch (error) {
        console.error('Error fetching users:', error)
        if (error.response?.status === 403) {
          this.error = '관리자 권한이 필요합니다.'
          this.$router.push('/')
        } else {
          this.error = '유저 목록을 불러오는데 실패했습니다.'
        }
      } finally {
        this.isLoading = false
      }
    },
    async showUserComments(user) {
      this.selectedUser = user
      this.showModal = true
      this.isLoading = true
      this.error = null
      
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.error = '인증이 필요합니다. 로그인해주세요.'
          return
        }
        
        const response = await axios.get(`http://localhost:8080/api/admin/users/${user.id}/comments`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        this.userComments = response.data
        
        // 영화 제목 가져오기
        const movieIds = [...new Set(this.userComments.map(comment => comment.movieId))]
        await Promise.all(movieIds.map(movieId => this.fetchMovieTitle(movieId)))
      } catch (error) {
        console.error('Error fetching user comments:', error)
        this.error = '댓글을 불러오는데 실패했습니다.'
      } finally {
        this.isLoading = false
      }
    },
    async fetchMovieTitle(movieId) {
      if (this.movieTitles[movieId]) return
      
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(`http://localhost:8080/api/movies/${movieId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        this.movieTitles[movieId] = response.data.title
      } catch (error) {
        console.error('Error fetching movie title:', error)
        this.movieTitles[movieId] = '영화 정보 없음'
      }
    },
    getMovieTitle(movieId) {
      return this.movieTitles[movieId] || '로딩중...'
    },
    formatDate(date) {
      return new Date(date).toLocaleString('ko-KR')
    },
    closeModal() {
      this.showModal = false
      this.selectedUser = null
      this.userComments = []
    },
    async deleteUser(userId) {
      if (!confirm('정말로 이 유저를 삭제하시겠습니까?')) return
      
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('인증이 필요합니다. 로그인해주세요.')
          return
        }
        
        await axios.delete(`http://localhost:8080/api/admin/users/${userId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        this.users = this.users.filter(user => user.id !== userId)
      } catch (error) {
        console.error('Error deleting user:', error)
        alert('유저 삭제에 실패했습니다.')
      }
    },
    async deleteComment(commentId) {
      if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) return
      
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('인증이 필요합니다. 로그인해주세요.')
          return
        }
        
        const comment = this.userComments.find(c => c.id === commentId)
        if (!comment) {
          alert('댓글을 찾을 수 없습니다.')
          return
        }
        
        await axios.delete(`http://localhost:8080/api/movies/${comment.movieId}/comments/${commentId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        this.userComments = this.userComments.filter(comment => comment.id !== commentId)
      } catch (error) {
        console.error('Error deleting comment:', error)
        alert('댓글 삭제에 실패했습니다.')
      }
    }
  }
}
</script>

<style scoped>
.admin-page {
  padding: 2rem 0;
}

.modal {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

.comments-list {
  max-height: 60vh;
  overflow-y: auto;
}

.comment-item {
  border-bottom: 1px solid #eee;
  padding: 1rem 0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.movie-title {
  font-weight: bold;
}

.comment-date {
  color: #666;
  font-size: 0.9rem;
}

.comment-content {
  color: #333;
  white-space: pre-wrap;
}
</style> 