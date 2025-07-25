<template>
  <h1 class="h3 mb-4 text-gray-800">{{ isEditing ? 'Chỉnh sửa Bài viết' : 'Viết bài mới' }}</h1>
  <form @submit.prevent="savePost">
    <div class="card shadow">
      <div class="card-body">
        <div class="mb-3">
          <label for="postTitle" class="form-label">Tiêu đề</label>
          <input type="text" class="form-control" id="postTitle" v-model="post.title" required>
        </div>
        <div class="mb-3">
          <label for="postContent" class="form-label">Nội dung</label>
          <textarea class="form-control" id="postContent" rows="10" v-model="post.content"></textarea>
        </div>
         <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" role="switch" id="isPublished" v-model="post.isPublished">
            <label class="form-check-label" for="isPublished">Xuất bản ngay</label>
        </div>
      </div>
    </div>
    <div class="d-flex justify-content-end gap-2 mt-4">
      <router-link :to="{ name: 'admin.posts.list' }" class="btn btn-secondary">Hủy</router-link>
      <button type="submit" class="btn btn-primary">Lưu bài viết</button>
    </div>
  </form>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
const route = useRoute();
const router = useRouter();
const isEditing = computed(() => !!route.params.id);
const post = ref({ title: '', content: '', isPublished: true });
onMounted(() => {
  if (isEditing.value) {
    console.log(`Lấy dữ liệu cho bài viết ID: ${route.params.id}`);
    post.value = { title: 'Top 5 mẫu áo thun', content: 'Nội dung bài viết...', isPublished: true };
  }
});
const savePost = () => {
  console.log('Đang lưu bài viết:', post.value);
  router.push({ name: 'admin.posts.list' });
};
</script>