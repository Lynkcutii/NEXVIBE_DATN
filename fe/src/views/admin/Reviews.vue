<template>
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 mb-0 text-gray-800">Quản lý Đánh giá</h1>
  </div>
  <div class="card">
    <div class="card-body">
      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th>Người đánh giá</th>
            <th>Sản phẩm</th>
            <th>Rating</th>
            <th style="width: 35%;">Nội dung</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="review in reviews" :key="review.id">
            <td>{{ review.customerName }}</td>
            <td>{{ review.productName }}</td>
            <td class="text-warning">
                <!-- Hiển thị sao vàng cho rating -->
                <i v-for="n in review.rating" :key="n" class="fas fa-star"></i>
                <!-- Hiển thị sao xám cho phần còn lại -->
                <i v-for="n in (5 - review.rating)" :key="n" class="far fa-star"></i>
            </td>
            <td>
                <!-- Chỉ hiển thị một phần nội dung, đầy đủ trong modal -->
                <p class="mb-0 text-truncate" style="max-width: 300px;">{{ review.content }}</p>
            </td>
            <td>
              <span class="badge" :class="review.isApproved ? 'bg-success' : 'bg-warning'">
                {{ review.isApproved ? 'Đã duyệt' : 'Chờ duyệt' }}
              </span>
            </td>
            <td>
                <button @click="openReviewModal(review)" class="btn btn-link text-primary p-1" title="Xem chi tiết & Trả lời">
                    <i class="fas fa-eye"></i>
                </button>
                <button 
                    @click="toggleApproval(review)" 
                    class="btn btn-link p-1" 
                    :class="review.isApproved ? 'text-danger' : 'text-success'"
                    :title="review.isApproved ? 'Ẩn đánh giá' : 'Duyệt đánh giá'"
                >
                    <i class="fas" :class="review.isApproved ? 'fa-eye-slash' : 'fa-check'"></i>
                </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal Chi tiết & Trả lời Đánh giá -->
  <div class="modal fade" id="reviewModal" tabindex="-1" aria-labelledby="reviewModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="reviewModalLabel">Chi tiết Đánh giá</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body" v-if="selectedReview">
          <div class="row">
            <div class="col-md-6">
                <p><strong>Người đánh giá:</strong> {{ selectedReview.customerName }}</p>
                <p><strong>Sản phẩm:</strong> {{ selectedReview.productName }}</p>
                <p><strong>Rating:</strong> 
                    <span class="text-warning">
                        <i v-for="n in selectedReview.rating" :key="n" class="fas fa-star"></i>
                        <i v-for="n in (5 - selectedReview.rating)" :key="n" class="far fa-star"></i>
                    </span>
                </p>
            </div>
          </div>
          <hr>
          <h6>Nội dung đánh giá:</h6>
          <p class="bg-light p-3 rounded">{{ selectedReview.content }}</p>
          <hr>
          <h6>Trả lời của Shop:</h6>
          <textarea class="form-control" rows="4" placeholder="Nhập câu trả lời của bạn ở đây..." v-model="selectedReview.reply"></textarea>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
          <button type="button" class="btn btn-primary" @click="submitReply">Lưu trả lời</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Modal } from 'bootstrap';

// Dữ liệu mẫu
const reviews = ref([
  { id: 1, customerName: 'Nguyễn Văn Khách', productName: 'Áo Thun Thể Thao', rating: 5, content: 'Vải rất mát, co dãn tốt, form đẹp, sẽ ủng hộ shop tiếp vào lần sau. Giao hàng cũng rất nhanh chóng.', isApproved: true, reply: '' },
  { id: 2, customerName: 'Trần Thị B', productName: 'Áo Hoodie Nỉ Bông', rating: 3, content: 'Áo hơi dày so với mình nghĩ, màu sắc cũng không được tươi như trong ảnh. Tạm ổn.', isApproved: false, reply: '' },
]);

// --- Logic cho Modal ---
let reviewModalInstance = null;
const selectedReview = ref(null);

const openReviewModal = (review) => {
  // Tạo một bản sao để tránh thay đổi trực tiếp dữ liệu gốc
  selectedReview.value = { ...review }; 
  reviewModalInstance?.show();
};

const submitReply = () => {
    if (!selectedReview.value) return;
    console.log(`Đang lưu trả lời cho đánh giá ID ${selectedReview.value.id}:`, selectedReview.value.reply);
    // Nơi bạn gọi API để lưu câu trả lời
    
    // Cập nhật lại UI (nếu cần)
    const originalReview = reviews.value.find(r => r.id === selectedReview.value.id);
    if (originalReview) {
        originalReview.reply = selectedReview.value.reply;
    }
    
    reviewModalInstance?.hide();
};

// --- Logic Duyệt/Bỏ duyệt ---
const toggleApproval = (review) => {
    const actionText = review.isApproved ? 'ẩn' : 'duyệt';
    if (confirm(`Bạn có chắc muốn ${actionText} đánh giá này không?`)) {
        console.log(`Đang ${actionText} đánh giá ID: ${review.id}`);
        // Gọi API để cập nhật trạng thái
        // Sau khi thành công, cập nhật UI
        review.isApproved = !review.isApproved;
    }
};


// --- Quản lý vòng đời Modal ---
onMounted(() => {
  reviewModalInstance = new Modal(document.getElementById('reviewModal'));
});

onUnmounted(() => {
  reviewModalInstance?.dispose();
});
</script>