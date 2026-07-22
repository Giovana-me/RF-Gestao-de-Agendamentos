<template>
  <div class="cliente-page">
    <ClienteHeader />

    <div class="cliente-layout">
      <ClienteSidebar />

      <main class="cliente-content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import ClienteHeader from "./ClienteHeader.vue";
import ClienteSidebar from "./ClienteSidebar.vue";
import "@/assets/cliente-layout.css";
</script>