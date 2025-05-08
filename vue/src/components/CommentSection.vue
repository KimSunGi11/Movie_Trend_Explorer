<template>
  <div class="comments-section">
    <h3>댓글</h3>
    
    <div v-if="isAuthenticated" class="comment-form">
      <textarea
        v-model="newComment"
        placeholder="댓글을 입력하세요..."
        class="form-control"
        rows="3"
      ></textarea>
      <button
        @click="submitComment"
        class="btn btn-primary mt-2"
        :disabled="!newComment.trim()"
      >
        댓글 작성
      </button>
    </div>
    
    <div v-else class="login-prompt">
      <p>댓글을 작성하려면 <router-link to="/login">로그인</router-link>이 필요합니다.</p>
    </div>

    <div v-if="isLoading" class="text-center my-4">
      <div class="spinner-border text-primary" role="status">
        <span class="sr-only">로딩중...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger mt-3">
      {{ error }}
    </div>

    <div v-else class="comments-list mt-4">
      <div v-if="comments.length === 0" class="text-center text-muted">
        아직 댓글이 없습니다. 첫 댓글을 작성해보세요!
      </div>

      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <span class="user-name">{{ comment.name }}</span>
          <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
          <button 
            v-if="isAdmin || comment.username === currentUser?.username"
            @click="deleteComment(comment.id)"
            class="delete-btn"
          >
            <i class="fas fa-trash"></i>
          </button>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import axios from 'axios'

export default {
  name: 'CommentSection',
  props: {
    movieId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      comments: [],
      newComment: '',
      loading: true,
      error: null
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'currentUser', 'isAdmin'])
  },
  created() {
    this.fetchComments()
  },
  methods: {
    async fetchComments() {
      this.loading = true
      this.error = null
      
      try {
        const response = await axios.get(`http://localhost:8080/api/movies/${this.movieId}/comments`)
        this.comments = response.data
      } catch (error) {
        console.error('Error fetching comments:', error)
        this.error = '댓글을 불러오는데 실패했습니다.'
      } finally {
        this.loading = false
      }
    },
    async submitComment() {
      if (!this.newComment.trim()) return
      
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.$router.push('/login')
          return
        }

        await axios.post(
          `http://localhost:8080/api/movies/${this.movieId}/comments`,
          { content: this.newComment.trim() },
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        )
        
        this.newComment = ''
        await this.fetchComments()
      } catch (error) {
        console.error('Error submitting comment:', error)
        if (error.response?.status === 401) {
          localStorage.removeItem('token')
          this.$router.push('/login')
        } else {
          this.error = '댓글 작성에 실패했습니다.'
        }
      }
    },
    async deleteComment(commentId) {
      if (!confirm('댓글을 삭제하시겠습니까?')) return;
      
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`http://localhost:8080/api/comments/${commentId}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        this.comments = this.comments.filter(comment => comment.id !== commentId);
      } catch (error) {
        console.error('댓글 삭제 실패:', error);
        alert('댓글 삭제에 실패했습니다.');
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.comments-section {
  background: rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(196, 167, 125, 0.2);
  margin-top: 2rem;
}

.comments-section h3 {
  color: #C4A77D;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.login-prompt {
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.login-prompt p {
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.login-prompt a {
  color: #C4A77D;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-prompt a:hover {
  color: #8B7355;
  text-decoration: underline;
}

.comment-item {
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.comment-author {
  color: #C4A77D;
  font-weight: 600;
  font-size: 1.1rem;
}

.comment-date {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  margin-right: auto;
  background: rgba(0, 0, 0, 0.3);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
}

.comment-content {
  white-space: pre-wrap;
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.6;
  margin-top: 0.5rem;
}

.comment-actions {
  display: flex;
  gap: 1rem;
}

.comment-action-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.comment-action-btn:hover {
  color: #C4A77D;
}

.comment-form {
  background: rgba(0, 0, 0, 0.2);
  padding: 1.5rem;
  border-radius: 8px;
  margin-top: 2rem;
  border: 1px solid rgba(196, 167, 125, 0.2);
}

.comment-form textarea {
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(196, 167, 125, 0.3);
  border-radius: 8px;
  padding: 1rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 1rem;
  resize: vertical;
  min-height: 100px;
}

.comment-form textarea:focus {
  outline: none;
  border-color: #C4A77D;
  box-shadow: 0 0 0 2px rgba(196, 167, 125, 0.2);
}

.comment-form button {
  background: linear-gradient(135deg, #C4A77D, #8B7355);
  border: none;
  color: #FFFFFF;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.comment-form button:hover {
  background: linear-gradient(135deg, #8B7355, #C4A77D);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.user-name {
  font-weight: bold;
  margin-right: 1rem;
  color: #C4A77D;
}

.comment-date {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  margin-right: auto;
  background: rgba(0, 0, 0, 0.3);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
}

.comment-content {
  white-space: pre-wrap;
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.6;
  margin-top: 0.5rem;
}

.btn-danger {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.delete-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  padding: 0.25rem;
  margin-left: 0.5rem;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  color: #ff6b6b;
  transform: scale(1.1);
}
</style> 