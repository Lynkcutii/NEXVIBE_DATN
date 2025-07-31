// src/main.js

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

import 'bootstrap/dist/css/bootstrap.min.css'

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import './assets/style.css'
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
// app.use(createPinia());

app.mount('#app')