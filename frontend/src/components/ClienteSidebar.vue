<template>
  <aside class="cliente-sidebar">
    <button
      class="cliente-menu-button"
      :class="{ active: rotaAtual === '/procedimentos' }"
      @click="ir('/procedimentos')"
    >
      Agendar
    </button>

    <button
      class="cliente-menu-button"
      :class="{ active: rotaAtual === '/meus-agendamentos' }"
      @click="ir('/meus-agendamentos')"
    >
      Meus agendamentos
    </button>

    <div class="cliente-brand">
      RF Estética
    </div>
  </aside>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const rotaAtual = route.path;

function ir(caminho) {
  router.push(caminho);
}
</script>