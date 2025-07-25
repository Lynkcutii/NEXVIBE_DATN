<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Bài viết</h1>
    <router-link :to="{ name: 'admin.posts.create' }" class="btn btn-primary"><i class="fas fa-plus"></i> Viết bài mới</router-link>
  </div>
  <div class="card">
    <div class="card-body">
      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th>Tiêu đề</th>
            <th>Tác giả</th>
            <th>Ngày đăng</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <!-- Dùng v-for để lặp qua danh sách bài viết -->
          <tr v-for="post in posts" :key="post.id">
            <td>{{ post.title }}</td>
            <td>{{ post.author }}</td>
            <td>{{ post.date }}</td>
            <td>
              <span class="badge" :class="post.isPublished ? 'bg-success' : 'bg-warning'">
                {{ post.isPublished ? 'Đã xuất bản' : 'Bản nháp' }}
              </span>
            </td>
            <td>
              <router-link :to="{ name: 'admin.posts.edit', params: { id: post.id } }" class="btn btn-link text-primary p-1" title="Sửa"><i class="fas fa-edit"></i></router-link>
              <!-- Nút Xóa sẽ gọi hàm mở modal -->
              <button @click="openDeleteModal(post)" class="btn btn-link text-danger p-1" title="Xóa"><i class="fas fa-trash-alt"></i></button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal xác nhận xóa -->
  <div class="modal fade" id="deletePostModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteModalLabel">Xác nhận Xóa</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          Bạn có chắc chắn muốn xóa bài viết "<strong>{{ postToDelete?.title }}</strong>"?<br>
          Hành động này không thể hoàn tác.
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="button" class="btn btn-danger" @click="confirmDelete">Xóa</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';

// Dữ liệu mẫu
const posts = ref([
  { id: 1, title: 'Top 5 mẫu áo thun thể thao hot nhất 2024', author: 'Trần Minh Tuấn', date: '28/06/2024', isPublished: true },
  { id: 2, title: 'Cách phối đồ với quần jogger cho nam', author: 'Lê Thị Hoa', date: '25/06/2024', isPublished: false },
]);

// Logic cho Modal Xóa
let deleteModalInstance = null;
const postToDelete = ref(null);

// Hàm mở modal và lưu thông tin bài viết cần xóa
const openDeleteModal = (post) => {
  postToDelete.value = post;
  if (deleteModalInstance) {
    deleteModalInstance.show();
  }
};

// Hàm xác nhận xóa
const confirmDelete = () => {
  if (!postToDelete.value) return;
  
  console.log(`Đang thực hiện xóa bài viết có ID: ${postToDelete.value.id}`);
  
  // NƠI BẠN SẼ GỌI API ĐỂ XÓA DỮ LIỆU TRÊN SERVER
  // Ví dụ:
  // await api.delete(`/posts/${postToDelete.value.id}`);
  
  // Sau khi API chạy xong, ẩn modal đi
  if (deleteModalInstance) {
    deleteModalInstance.hide();
  }

  // Cập nhật lại danh sách trên UI bằng cách lọc bỏ bài viết đã xóa
  posts.value = posts.value.filter(p => p.id !== postToDelete.value.id);
};

// Khởi tạo đối tượng Modal khi component được gắn vào DOM
onMounted(() => {
  const modalElement = document.getElementById('deletePostModal');
  if (modalElement) {
    deleteModalInstance = new Modal(modalElement);
  }
});

// Hủy đối tượng Modal khi component bị gỡ khỏi DOM
onUnmounted(() => {
    if (deleteModalInstance) {
        deleteModalInstance.dispose();
    }
});
</script>