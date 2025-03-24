<template>
  <div class="create-post">
    <div class="container">
      <h2 class="mb-4">게시글 작성</h2>

      <div v-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <form @submit.prevent="submitPost">
        <div class="form-group">
          <label for="title">제목</label>
          <input
            type="text"
            class="form-control"
            id="title"
            v-model="title"
            required
          >
        </div>

        <div class="form-group">
          <label for="content">내용</label>
          <textarea
            class="form-control"
            id="content"
            v-model="content"
            rows="10"
            required
          ></textarea>
        </div>

        <div class="d-flex justify-content-between">
          <button type="button" class="btn btn-secondary" @click="goBack">
            취소
          </button>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? '작성 중...' : '작성하기' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CreatePost',
  data() {
    return {
      title: '',
      content: '',
      isSubmitting: false,
      error: null
    }
  },
  methods: {
    async submitPost() {
      this.isSubmitting = true
      this.error = null

      try {
        const token = localStorage.getItem('token')
        if (!token) {
          this.$router.push('/login')
          return
        }

        await axios.post(
          'http://localhost:8080/api/posts',
          {
            title: this.title.trim(),
            content: this.content.trim()
          },
          {
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        )

        this.$router.push('/community')
      } catch (error) {
        console.error('Error creating post:', error)
        if (error.response?.status === 401) {
          localStorage.removeItem('token')
          this.$router.push('/login')
        } else {
          this.error = '게시글 작성에 실패했습니다.'
        }
      } finally {
        this.isSubmitting = false
      }
    },
    goBack() {
      this.$router.push('/community')
    }
  }
}
</script>

<style scoped>
.create-post {
  padding: 2rem 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-control {
  border-radius: 4px;
}

textarea.form-control {
  resize: vertical;
  min-height: 300px;
}

.btn {
  padding: 0.5rem 1.5rem;
}

.btn:disabled {
  cursor: not-allowed;
}
</style> 