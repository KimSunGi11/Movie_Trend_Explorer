import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    isLoggedIn: false,
    user: null,
    token: localStorage.getItem('token') || null,
    movies: [],
    watchlist: [],
    currentMovie: null
  },
  mutations: {
    setLoggedIn(state, value) {
      state.isLoggedIn = value
    },
    setUser(state, user) {
      state.user = user
    },
    setToken(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    logout(state) {
      state.isLoggedIn = false
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
    async initializeAuth({ commit, state }) {
      const token = state.token
      if (token) {
        try {
          const response = await fetch('http://localhost:8080/api/auth/user', {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })
          if (response.ok) {
            const user = await response.json()
            commit('setUser', user)
            commit('setLoggedIn', true)
          } else {
            commit('logout')
          }
        } catch (error) {
          commit('logout')
        }
      }
    },
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
      commit('logout')
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
        commit('logout')
      }
    }
  },
  getters: {
    isAuthenticated: state => state.isLoggedIn,
    currentUser: state => state.user,
    isAdmin: state => state.user && state.user.role === 'ADMIN',
    movies: state => state.movies,
    currentMovie: state => state.currentMovie,
    watchlist: state => state.watchlist
  }
}) 