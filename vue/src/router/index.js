import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Movies from '../views/Movies.vue'
import MovieDetail from '../views/MovieDetail.vue'
import Login from '../views/Login.vue'
import Signup from '../views/Signup.vue'
import Community from '../views/Community.vue'
import PostDetail from '../views/PostDetail.vue'
import CreatePost from '../views/CreatePost.vue'
import Favorites from '../views/Favorites.vue'
import Admin from '../views/Admin.vue'
import AdvancedSearch from '@/views/AdvancedSearch.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/movies',
    name: 'Movies',
    component: Movies
  },
  {
    path: '/movie/:id',
    name: 'MovieDetail',
    component: MovieDetail
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/signup',
    name: 'Signup',
    component: Signup
  },
  {
    path: '/community',
    name: 'Community',
    component: Community
  },
  {
    path: '/community/post/:id',
    name: 'PostDetail',
    component: PostDetail
  },
  {
    path: '/community/create',
    name: 'CreatePost',
    component: CreatePost,
    meta: { requiresAuth: true }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/advanced-search',
    name: 'AdvancedSearch',
    component: AdvancedSearch
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/', '/login', '/signup', '/search', '/movies', '/movie']
  const path = to.path
  const isPublicPage = publicPages.some(page => path.startsWith(page))
  const isAuthenticated = localStorage.getItem('token')

  if (!isPublicPage && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router 