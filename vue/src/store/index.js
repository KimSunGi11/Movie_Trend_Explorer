import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    movies: [],
    watchlist: [],
    currentMovie: null
  },
  mutations: {
    setUser(state, user) {
      state.user = user
    },
    setToken(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    clearAuth(state) {
      state.user = null
      state.token = null
      localStorage.removeItem('token')
    },
    setMovies(state, movies) {
      state.movies = movies
    },
    setCurrentMovie(state, movie) {
      state.currentMovie = movie
    },
    setWatchlist(state, watchlist) {
      state.watchlist = watchlist
    }
  },
  actions: {
    async login({ commit }, credentials) {
      const response = await axios.post('/api/auth/login', credentials)
      commit('setToken', response.data.token)
      commit('setUser', {
        username: response.data.username,
        name: response.data.name
      })
    },
    async register({ commit }, userData) {
      const response = await axios.post('/api/auth/register', userData)
      commit('setToken', response.data.token)
      commit('setUser', response.data.user)
    },
    logout({ commit }) {
      commit('clearAuth')
    },
    async fetchMovies({ commit }, { page = 1, query = '' }) {
      try {
        const response = await axios.get(query 
          ? `/api/movies/search?query=${query}&page=${page}`
          : '/api/movies/trending'
        )
        commit('setMovies', response.data.results)
      } catch (error) {
        console.error('Error fetching movies:', error)
        throw error
      }
    },
    async fetchMovie({ commit }, id) {
      try {
        const response = await axios.get(`/api/movies/${id}`)
        commit('setCurrentMovie', response.data)
      } catch (error) {
        console.error('Error fetching movie details:', error)
        throw error
      }
    },
    async fetchWatchlist({ commit }) {
      const response = await axios.get('/api/watchlist')
      commit('setWatchlist', response.data)
    },
    async addToWatchlist({ dispatch }, movieId) {
      await axios.post('/api/watchlist', { movieId })
      dispatch('fetchWatchlist')
    },
    async removeFromWatchlist({ dispatch }, movieId) {
      await axios.delete(`/api/watchlist/${movieId}`)
      dispatch('fetchWatchlist')
    },
    async fetchUserInfo({ commit, state }) {
      try {
        const response = await axios.get('/api/auth/user', {
          headers: {
            Authorization: `Bearer ${state.token}`
          }
        })
        commit('setUser', {
          username: response.data.username,
          name: response.data.name
        })
      } catch (error) {
        console.error('Error fetching user info:', error)
        commit('clearAuth')
      }
    }
  },
  getters: {
    isAuthenticated: state => !!state.token,
    currentUser: state => state.user,
    movies: state => state.movies,
    currentMovie: state => state.currentMovie,
    watchlist: state => state.watchlist
  }
}) 