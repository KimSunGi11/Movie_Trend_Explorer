<template>
  <div class="community">
    <div class="container">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>커뮤니티</h2>
        <router-link v-if="isAuthenticated" to="/community/create" class="btn btn-primary">
          글쓰기
        </router-link>
      </div>

      <div v-if="isLoading" class="text-center">
        <div class="spinner-border text-primary" role="status">
          <span class="sr-only">로딩중...</span>
        </div>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
      </div>

      <div v-else class="posts-list">
        <div v-if="posts.length === 0" class="text-center text-muted">
          아직 게시글이 없습니다. 첫 게시글을 작성해보세요!
        </div>

        <div v-else class="post-item" v-for="post in posts" :key="post.id">
          <div class="post-header">
            <h3 class="post-title" @click="goToPost(post.id)">
              {{ post.title }}
            </h3>
            <div class="post-meta">
              <span class="author">{{ post.name }}</span>
              <span class="date">{{ formatDate(post.createdAt) }}</span>
              <span class="views">조회 {{ post.viewCount }}</span>
            </div>
          </div>
          <p class="post-preview">{{ truncateContent(post.content) }}</p>
        </div>

        <div class="d-flex justify-content-center mt-4">
          <nav aria-label="Page navigation">
            <ul class="pagination">
              <li class="page-item" :class="{ disabled: currentPage === 0 }">
                <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">이전</a>
              </li>
              <li class="page-item" :class="{ disabled: !hasNextPage }">
                <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">다음</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Community',
  data() {
    return {
      posts: [],
      isLoading: false,
      error: null,
      currentPage: 0,
      hasNextPage: false,
      pageSize: 10
    }
  },
  computed: {
    isAuthenticated() {
      return !!localStorage.getItem('token')
    }
  },
  created() {
    this.fetchPosts()
  },
  methods: {
    async fetchPosts() {
      this.isLoading = true
      this.error = null
      
      try {
        const response = await axios.get(`http://localhost:8080/api/posts`, {
          params: {
            page: this.currentPage,
            size: this.pageSize
          }
        })
        
        this.posts = response.data.content
        this.hasNextPage = response.data.hasNext
      } catch (error) {
        console.error('Error fetching posts:', error)
        this.error = '게시글을 불러오는데 실패했습니다.'
      } finally {
        this.isLoading = false
      }
    },
    goToPost(id) {
      this.$router.push(`/community/post/${id}`)
    },
    changePage(page) {
      if (page >= 0 && (page < this.currentPage || this.hasNextPage)) {
        this.currentPage = page
        this.fetchPosts()
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
    },
    truncateContent(content) {
      if (!content) return ''
      return content.length > 200 ? content.substring(0, 200) + '...' : content
    }
  }
}
</script>

<style scoped>
.community {
  padding: 2rem 0;
}

.post-item {
  padding: 1.5rem;
  border-bottom: 1px solid #dee2e6;
  cursor: pointer;
  transition: background-color 0.2s;
}

.post-item:hover {
  background-color: #f8f9fa;
}

.post-header {
  margin-bottom: 0.5rem;
}

.post-title {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  color: #212529;
}

.post-meta {
  font-size: 0.875rem;
  color: #6c757d;
}

.post-meta span {
  margin-right: 1rem;
}

.post-preview {
  color: #6c757d;
  margin-bottom: 0;
}

.pagination {
  margin-bottom: 0;
}

.page-link {
  color: #007bff;
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}
</style> 