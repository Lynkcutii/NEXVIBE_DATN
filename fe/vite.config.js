import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 1. Import module 'path' của Node.js

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  // 2. Thêm vào khối 'resolve'
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
})