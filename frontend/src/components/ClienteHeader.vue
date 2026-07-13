<template>
  <header class="cliente-header">
    <div class="cliente-user-area">
      <div class="cliente-avatar">👤</div>

      <div>
        <h1>{{ nomeUsuario }}</h1>
        <button class="cliente-edit-button">editar</button>
      </div>
    </div>

    <button
      class="cliente-logout-button"
      @click="realizarLogout"
    >
      Sair
    </button>
  </header>
</template>

<script setup lang="ts">

import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getUsuarioLogado, logout } from "../services/authService";

const router = useRouter();
const nomeUsuario = ref("");

onMounted(() => {
    const usuario = getUsuarioLogado();
    if (usuario) {
        nomeUsuario.value = usuario.nome;
    }
});

function realizarLogout() {
    logout();
    router.push("/login");
}
</script>