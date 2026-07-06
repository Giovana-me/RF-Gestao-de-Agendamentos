<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <button class="close-button" @click="$emit('fechar')">
        ×
      </button>

      <h2>Selecione um horário</h2>

      <Stepper :etapaAtual="3" />

      <p class="subtitle">
        Escolha o horário desejado para o atendimento.
      </p>

      <div class="time-grid">
        <button
          v-for="hora in horarios"
          :key="hora"
          class="time-button"
          :class="{ selected: horaSelecionada === hora }"
          @click="horaSelecionada = hora"
        >
          ⏱ {{ hora }}
        </button>
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

<script setup>
import { ref } from "vue";

import Stepper from "./Stepper.vue";

const emit = defineEmits(["fechar", "voltar", "continuar"]);

const horarios = [
  "08:00",
  "09:00",
  "10:00",
  "11:00",
  "14:00",
  "15:00",
  "16:00",
  "17:00",
  "18:00",
  "19:00"
];

const horaSelecionada = ref("");

function continuar() {
  if (!horaSelecionada.value) {
    alert("Selecione um horário para continuar.");
    return;
  }

  emit("continuar", horaSelecionada.value);
}
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

.modal-box {
  width: 760px;
  background-color: #fff;
  border-radius: 30px;
  padding: 36px;
  box-sizing: border-box;
  position: relative;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.3);
  color: #000;
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
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #000;
}

.subtitle {
  margin: 12px 0 28px;
  font-size: 18px;
  color: #000;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.time-button {
  border: none;
  background-color: #fff;
  color: #000;
  border-radius: 12px;
  padding: 22px;
  font-size: 22px;
  cursor: pointer;
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.22),
    inset 0 3px 5px rgba(0, 0, 0, 0.12);
}

.time-button.selected {
  background-color: #dd81b5;
  color: #fff;
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
  border-radius: 24px;
  padding: 16px;
  font-size: 16px;
  cursor: pointer;
}

.back-button {
  background-color: #ffeeee;
  color: #000;
}

.next-button {
  background-color: #dd81b5;
  color: #fff;
}
</style>