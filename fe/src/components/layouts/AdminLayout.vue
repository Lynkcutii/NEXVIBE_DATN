<template>
  <div class="wrapper">
    <Sidebar />

    <main id="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Sidebar from '../Sidebar.vue';
</script>