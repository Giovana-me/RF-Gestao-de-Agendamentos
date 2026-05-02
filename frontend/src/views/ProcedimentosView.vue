<template>
  <div>
    <h1>Procedimentos</h1>

    <div v-for="proc in procedimentos" :key="proc.id">
      <h3>{{ proc.nome }}</h3>
      <p>{{ proc.descricao }}</p>
      <p>Valor: R$ {{ proc.valor }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";

const procedimentos = ref([]);

async function carregarProcedimentos() {
  try {
    const response = await api.get("/procedimentos");
    procedimentos.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar procedimentos:", error);
  }
}

onMounted(() => {
  carregarProcedimentos();
});
</script>