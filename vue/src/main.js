import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import axios from 'axios'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

Vue.config.productionTip = false

// Axios 기본 설정
axios.defaults.baseURL = process.env.VUE_APP_API_URL || 'http://localhost:8080'

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app') 