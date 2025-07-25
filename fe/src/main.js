// src/main.js

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

// Import CSS của các thư viện
import 'bootstrap/dist/css/bootstrap.min.css'
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';

// Import CSS tùy chỉnh của bạn
import './assets/style.css'

// Import JS của Bootstrap
import 'bootstrap'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import axios from 'axios';

axios.defaults.baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
axios.defaults.withCredentials = true;

const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia)
// app.use(store)
app.use(ElementPlus)
app.use(createPinia());

app.mount('#app')