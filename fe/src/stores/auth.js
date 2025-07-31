import { defineStore } from 'pinia';
import axios from 'axios';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(sessionStorage.getItem('auth_user')) || null,
    isAuthenticated: JSON.parse(sessionStorage.getItem('auth_isAuthenticated')) || false,
  }),

  actions: {
    async login(userData) {
      console.log('authStore.login: Logging in with userData=' + JSON.stringify(userData));
      if (!userData.taiKhoan || !userData.chucVu) {
        console.error('authStore.login: Missing taiKhoan or chucVu in userData');
        throw new Error('Dữ liệu người dùng không đầy đủ: Thiếu taiKhoan hoặc chucVu');
      }

      this.user = userData;
      this.isAuthenticated = true;
      sessionStorage.setItem('auth_user', JSON.stringify(userData));
      sessionStorage.setItem('auth_isAuthenticated', JSON.stringify(true));

      // Nếu thiếu idTK, gọi API /api/auth/user để lấy thông tin đầy đủ
      if (!userData.idTK) {
        console.warn('authStore.login: Missing idTK, fetching from /api/auth/user');
        try {
          const response = await axios.get('http://localhost:8080/api/auth/user', {
            withCredentials: true
          });
          console.log('authStore.login: Fetched userData=' + JSON.stringify(response.data));
          this.user = response.data;
          sessionStorage.setItem('auth_user', JSON.stringify(this.user));
        } catch (error) {
          console.error('authStore.login: Failed to fetch idTK -', error);
        }
      }
    },

    async logout() {
      console.log('authStore.logout: Starting');
      try {
        await axios.post('http://localhost:8080/api/auth/logout', {}, {
          withCredentials: true
        });
        console.log('authStore.logout: Success');
        this.user = null;
        this.isAuthenticated = false;
        sessionStorage.removeItem('auth_user');
        sessionStorage.removeItem('auth_isAuthenticated');
      } catch (error) {
        console.error('authStore.logout: Error -', error);
        this.user = null;
        this.isAuthenticated = false;
        sessionStorage.removeItem('auth_user');
        sessionStorage.removeItem('auth_isAuthenticated');
      }
    },

    async checkAuth() {
      console.log('authStore.checkAuth: Starting');
      try {
        const response = await axios.get('http://localhost:8080/api/auth/user', {
          withCredentials: true
        });
        console.log('authStore.checkAuth: Success, userData=' + JSON.stringify(response.data));
        if (!response.data.taiKhoan || !response.data.chucVu) {
          console.error('authStore.checkAuth: Missing taiKhoan or chucVu in userData');
          throw new Error('Dữ liệu người dùng không đầy đủ: Thiếu taiKhoan hoặc chucVu');
        }
        this.user = response.data;
        this.isAuthenticated = true;
        sessionStorage.setItem('auth_user', JSON.stringify(this.user));
        sessionStorage.setItem('auth_isAuthenticated', JSON.stringify(true));
      } catch (error) {
        console.error('authStore.checkAuth: Error -', error);
        if (error.response) {
          console.error('authStore.checkAuth: Response error - status=' + error.response.status + ', data=' + JSON.stringify(error.response.data));
        } else if (error.request) {
          console.error('authStore.checkAuth: No response received, possible CORS or network issue');
        }
        this.user = null;
        this.isAuthenticated = false;
        sessionStorage.removeItem('auth_user');
        sessionStorage.removeItem('auth_isAuthenticated');
      }
    },

    isAdmin() {
      return this.isAuthenticated && this.user && this.user.chucVu === 'ADMIN';
    },

    isCustomer() {
      return this.isAuthenticated && this.user && this.user.chucVu === 'KHACH_HANG';
    }
  }
});