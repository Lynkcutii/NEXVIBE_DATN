// src/stores/auth.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
    // Lấy dữ liệu từ localStorage để duy trì trạng thái đăng nhập
    const user = ref(JSON.parse(localStorage.getItem('nexvibe_user')));
    const token = ref(localStorage.getItem('nexvibe_token'));

    const isAuthenticated = computed(() => !!token.value && !!user.value);
    const userFullName = computed(() => user.value?.name || 'Tài khoản');

    function login(userData, authToken) {
        user.value = userData;
        token.value = authToken;
        localStorage.setItem('nexvibe_user', JSON.stringify(userData));
        localStorage.setItem('nexvibe_token', authToken);
    }

    function logout() {
        user.value = null;
        token.value = null;
        localStorage.removeItem('nexvibe_user');
        localStorage.removeItem('nexvibe_token');
        // Có thể cần gọi API để vô hiệu hóa token ở backend
    }
    
    return { user, token, isAuthenticated, userFullName, login, logout }
});