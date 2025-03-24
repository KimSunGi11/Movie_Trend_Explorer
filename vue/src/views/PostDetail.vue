<template>
  <div class="post-detail">
    <div class="container">
      <div v-if="isLoading" class="text-center">
        <div class="spinner-border text-primary" role="status">
          <span class="sr-only">로딩중...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else class="post-content">
        <div class="post-header">
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="post-meta">
            <span class="author">{{ post.name }}</span>
            <span class="date">{{ formatDate(post.createdAt) }}</span>
            <span class="views">조회 {{ post.viewCount }}</span>
          </div>
        </div>

        <div class="post-body">
          {{ post.content }}
        </div>

        <div class="post-actions" v-if="isAuthenticated && currentUsername === post.username">
          <button class="btn btn-danger" @click="deletePost">삭제</button>
        </div>

        <div class="comments-section mt-4">
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

          <div v-if="post.comments.length === 0" class="text-center text-muted mt-3">
            아직 댓글이 없습니다. 첫 댓글을 작성해보세요!
          </div>

          <div v-else class="comments-list mt-3">
            <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
              <div class="comment-header">
                <span class="user-name">{{ comment.name }}</span>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                <button
                  v-if="isAuthenticated && currentUsername === comment.username"
                  @click="deleteComment(comment.id)"
                  class="btn btn-sm btn-danger"
                >
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
</template>

<script>
import axios from 'axios'

export default {
  name: 'PostDetail',
  data() {
    return {
      post: null,
      isLoading: false,
      error: null,
      newComment: ''
    }
  },
  computed: {
    isAuthenticated() {
      return !!localStorage.getItem('token')
    },
    currentUsername() {
      return localStorage.getItem('username')
    }
  },
  created() {
    this.fetchPost()
  },
  methods: {
    async fetchPost() {
      this.isLoading = true
      this.error = null
      
      try {
        const response = await axios.get(`http://localhost:8080/api/posts/${this.$route.params.id}`)
        this.post = response.data
      } catch (error) {
        console.error('Error fetching post:', error)
        this.error = '게시글을 불러오는데 실패했습니다.'
      } finally {
        this.isLoading = false
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
          `http://localhost:8080/api/posts/${this.post.id}/comments`,
          { content: this.newComment.trim() },
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        )
        
        this.newComment = ''
        await this.fetchPost()
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
      if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) return
      
      try {
        const token = localStorage.getItem('token')
        await axios.delete(
          `http://localhost:8080/api/posts/${this.post.id}/comments/${commentId}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )
        
        await this.fetchPost()
      } catch (error) {
        console.error('Error deleting comment:', error)
        this.error = '댓글 삭제에 실패했습니다.'
      }
    },
    async deletePost() {
      if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) return
      
      try {
        const token = localStorage.getItem('token')
        await axios.delete(
          `http://localhost:8080/api/posts/${this.post.id}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )
        
        this.$router.push('/community')
      } catch (error) {
        console.error('Error deleting post:', error)
        this.error = '게시글 삭제에 실패했습니다.'
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
.post-detail {
  padding: 2rem 0;
}

.post-header {
  margin-bottom: 2rem;
}

.post-title {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #212529;
}

.post-meta {
  font-size: 0.875rem;
  color: #6c757d;
}

.post-meta span {
  margin-right: 1rem;
}

.post-body {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #212529;
  white-space: pre-wrap;
  margin-bottom: 2rem;
}

.post-actions {
  margin-bottom: 2rem;
  text-align: right;
}

.comments-section {
  border-top: 1px solid #dee2e6;
  padding-top: 2rem;
}

.comment-form {
  margin-bottom: 2rem;
}

.login-prompt {
  text-align: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 2rem;
}

.comment-item {
  padding: 1rem;
  border-bottom: 1px solid #dee2e6;
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
}

.comment-date {
  color: #6c757d;
  font-size: 0.9rem;
  margin-right: auto;
}

.comment-content {
  white-space: pre-wrap;
  color: #212529;
}

.btn-danger {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
</style> 