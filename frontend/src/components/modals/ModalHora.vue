<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <button class="close-button" @click="$emit('fechar')">
        ×
      </button>

      <h2>Selecione um horário</h2>
      <div class="modal-content">
        <Stepper :etapaAtual="3" />

        <p class="subtitle">
          Escolha o horário desejado para o atendimento.
        </p>

        <div v-if="carregando" class="mensagem-horarios">
          Carregando horários...
        </div>

        <div v-else-if="horarios.length === 0" class="mensagem-horarios">
          Nenhum horário disponível.
        </div>

        <div class="time-grid">
          <button
            v-for="horario in horarios"
          :key="horario.id"
          class="time-button"
          :class="{ selected: horaSelecionada === horario.hora }"
          @click="horaSelecionada = horario.hora"
          >
          {{ horario.hora }}
        </button>
        </div>
      </div>
      <div class="actions">
        <button class="back-button" @click="$emit('voltar')">
          Voltar
        </button>

        <button class="next-button" @click="continuar">
          Continuar
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Stepper from "../Stepper.vue";
import { useToast } from "vue-toastification";
import api from "@/services/api";
import { onMounted, ref } from "vue";

const toast = useToast();
const emit = defineEmits(["fechar", "voltar", "continuar"]);

interface Horario {
  id: number;
  hora: string;
  ativo: boolean;
}

const horarios = ref<Horario[]>([]);
const horaSelecionada = ref("");
const carregando = ref(false);

async function carregarHorarios() {
  carregando.value = true;
  try {
    const response = await api.get("/horarios");
    horarios.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar horários:", error);
    toast.error(
      "Não foi possível carregar os horários."
    );
  } finally {
    carregando.value = false;
  }
}

function continuar() {
  if (!horaSelecionada.value) {
    toast.info("Selecione um horário para continuar.");
    return;
  }

  emit("continuar", horaSelecionada.value);
}

onMounted(() => {
  carregarHorarios();
});

</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99999;
}

.modal-content{
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
}

.modal-box {
  width: 800px;
  background-color: white;
  border-radius: 30px;
  padding: 30px;
  box-sizing: border-box;
  position: relative;
  color: black;
  display: flex;
  flex-direction: column;
  max-height: 90vh; 
}

.close-button {
  position: absolute;
  top: 18px;
  right: 22px;
  border: none;
  background: transparent;
  font-size: 32px;
  cursor: pointer;
  color: #000;
}

.modal-box h2 {
  font-size: 26px;
  font-weight: 600;
  color: #000;
  padding-bottom: 20px;
}

.steps {
  padding: 30px;
}

.subtitle {
  margin: 12px 0 28px;
  font-size: 18px;
  color: #000;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.time-button {
  border: 1px solid #94335c;
  border-radius: 18px;
  box-sizing: border-box;
  background-color: #fff;
  color: #000;
  padding: 16px;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  justify-content: center;
}

.time-button.selected {
  background-color: #94335c;
  color: white;
}

.actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-top: 32px;
}

.back-button,
.next-button {
  border: none;
  border-radius: 26px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
  gap:16px;
  margin-top: 25px;
  height: 50px;
  flex-shrink: 0;
}

.back-button:hover {
  background:#94335c;
  color:white;
}

.back-button {
  background-color: #ffeeee;
  color: black;
}

.next-button:hover {
  background:#94335c;
  color:white;
}

.next-button {
  background-color: #dd81b5;
  color: white;
}

.modal-content::-webkit-scrollbar{
    width:8px;
}

.modal-content::-webkit-scrollbar-track{
    background:#f3f3f3;
    border-radius:10px;
}

.modal-content::-webkit-scrollbar-thumb{
    background:#dd81b5;
    border-radius:10px;
}

.modal-content::-webkit-scrollbar-thumb:hover{
    background:#94335c;
}

</style>