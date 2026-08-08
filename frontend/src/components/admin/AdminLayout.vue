<template>
  <AdminHeader />

  <div class="admin-layout">

    <AdminSidebar />

    <main class="admin-content">
      <RouterView />
    </main>

  </div>
</template>

<script setup lang="ts">
import AdminHeader from "./AdminHeader.vue";
import AdminSidebar from "./AdminSidebar.vue";

import "@/assets/admin-layout.css";
</script>