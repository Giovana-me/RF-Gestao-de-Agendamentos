<template>
  <div class="admin-page">

    <AdminHeader />

    <div class="admin-layout">

      <AdminSidebar />

      <main class="admin-content">
        <slot />
      </main>

    </div>

  </div>
</template>

<script setup lang="ts">
import AdminHeader from "./AdminHeader.vue";
import AdminSidebar from "./AdminSidebar.vue";

import "@/assets/admin-layout.css";
</script>