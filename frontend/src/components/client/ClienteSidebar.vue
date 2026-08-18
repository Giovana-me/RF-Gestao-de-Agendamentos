<template>
  <aside class="cliente-sidebar">
    <button class="cliente-menu-button" :class="{ active: rotaAtual === '/procedimentos' }" @click="ir('/procedimentos')">
    <CalendarPlus :size="24"/>
      <span> Agendar </span>
    </button>

    <button class="cliente-menu-button" :class="{ active: rotaAtual === '/meus-agendamentos' }" @click="ir('/meus-agendamentos')">
      <CalendarCheck :size="24"/>
      <span> Agendamentos </span>
    </button>

    <button class="cliente-menu-button" :class="{ active: rotaAtual === '/tabela-precos' }" @click="ir('/tabela-precos')">
      <BadgeDollarSign :size="24"/>
      <span> Preços </span>
    </button>

    <div class="cliente-brand">
      <div class="brand-circle">
        <img src="../../assets/images/rf-image.png" alt="">
      </div>
    </div>
  </aside>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";
import { CalendarPlus, CalendarCheck, BadgeDollarSign } from "lucide-vue-next";
import { computed } from "vue";

const router = useRouter();
const route = useRoute();

const rotaAtual = computed(() => route.path);

function ir(caminho) {
  router.push(caminho);
}
</script>