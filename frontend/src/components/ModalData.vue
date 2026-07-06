<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <button class="close-button" @click="$emit('fechar')">
        ×
      </button>

      <h2>Selecione uma data</h2>

      <Stepper :etapaAtual="2" />

      <p class="subtitle">
        Escolha a data desejada para o procedimento.
      </p>

      <input
        type="date"
        class="date-input"
        v-model="dataSelecionada"
      :min="dataMinima"
      />

      <div class="actions">
        <button class="back-button" @click="$emit('fechar')">
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

const hoje = new Date();

const dataMinima = hoje.toISOString().split("T")[0];

const emit = defineEmits(["fechar", "continuar"]);

const dataSelecionada = ref("");

function continuar() {
  if (!dataSelecionada.value) {
    alert("Selecione uma data para continuar.");
    return;
  }

  const data = new Date(dataSelecionada.value + "T00:00:00");

  if (data.getDay() === 0) {
    alert("Não realizamos atendimentos aos domingos.");
    return;
  }

  emit("continuar", dataSelecionada.value);
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

.date-input {
  width: 100%;
  border: 1px solid #94335c;
  border-radius: 18px;
  padding: 16px;
  font-size: 18px;
  box-sizing: border-box;
  color: #000;
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