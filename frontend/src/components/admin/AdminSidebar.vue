<template>
  <aside class="admin-sidebar">
    <button class="admin-menu-button" :class="{ active: rotaAtual === '/agenda' }" @click="ir('/agenda')">
      <CalendarDays :size="24"/>
      <span> Agenda </span>
    </button>

    <button class="admin-menu-button" :class="{ active: rotaAtual === '/horarios' }" @click="ir('/horarios')">
      <Clock3 :size="24"/>
      <span> Horários </span>
    </button>

    <button class="admin-menu-button" :class="{ active: rotaAtual === '/procedimentos-admin' }" @click="ir('/procedimentos-admin')">
      <Sparkles :size="24"/>
      <span> Procedimentos </span>
    </button>

    <div class="admin-brand">
      <div class="brand-circle">
        <img src="../../assets/images/rf-image.png" alt="">
      </div>
    </div>
  </aside>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";
import { CalendarDays, Clock3, Sparkles } from "lucide-vue-next";
import { computed } from "vue";

const rotaAtual = computed(() => route.path);
const router = useRouter();
const route = useRoute();

function ir(caminho) {
  router.push(caminho);
}
</script>