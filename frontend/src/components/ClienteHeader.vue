<template>
  <header class="cliente-header">
    <div class="cliente-user-area">
      <div class="cliente-avatar">👤</div>

      <div>
        <h1>{{ nomeUsuario }}</h1>
        <button class="cliente-edit-button">editar</button>
      </div>
    </div>

    <button class="cliente-logout-button">
      Sair
    </button>
  </header>
</template>

<script setup>
import { ref } from "vue";

const nomeUsuario = ref("Maria Silva"); // depois vem do login
</script>