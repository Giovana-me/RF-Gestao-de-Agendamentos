<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <button class="close-button" @click="$emit('fechar')">
        ×
      </button>

      <h2>Detalhes do agendamento</h2>
      
      <Stepper :etapaAtual="4" />

      <div class="details">
        <p>
          <strong>Procedimento:</strong>
          {{ procedimento?.nome }}
        </p>

        <p>
          <strong>Local:</strong>
          Rua Minas Gerais, 141B - Sertanópolis/PR
        </p>

        <p>
          <strong>Profissional:</strong>
          Rosangela Fernandes
        </p>

        <p>
          <strong>Data/Hora:</strong>
          {{ dataFormatada }} - {{ hora }}
        </p>
      </div>

      <label class="label" for="observacao">
        Alguma observação adicional?
      </label>

      <textarea
        id="observacao"
        v-model="observacao"
        placeholder="Descreva sua observação..."
      ></textarea>

      <div class="actions">
        <button class="back-button" @click="$emit('voltar')">
          Voltar
        </button>

        <button class="finish-button" @click="finalizar">
          Finalizar
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";

import Stepper from "./Stepper.vue";

const props = defineProps({
  procedimento: {
    type: Object,
    required: true
  },
  data: {
    type: String,
    required: true
  },
  hora: {
    type: String,
    required: true
  }
});

const emit = defineEmits(["fechar", "voltar", "finalizar"]);

const observacao = ref("");

const dataFormatada = computed(() => {
  if (!props.data) return "";

  const partes = props.data.split("-");

  if (partes.length !== 3) {
    return props.data;
  }

  return `${partes[2]}/${partes[1]}/${partes[0]}`;
});

function finalizar() {
  emit("finalizar", observacao.value);
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
  margin: 0 0 22px;
  font-size: 26px;
  font-weight: 700;
  color: #000;
}

.details {
  margin-bottom: 28px;
}

.details p {
  margin: 10px 0;
  font-size: 18px;
  color: #000;
}

.label {
  display: block;
  margin-bottom: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #000;
}

textarea {
  width: 100%;
  height: 140px;
  border: 1px solid #000;
  border-radius: 8px;
  padding: 18px;
  font-size: 17px;
  font-family: Inter, Arial, sans-serif;
  resize: none;
  box-sizing: border-box;
  color: #000;
}

textarea::placeholder {
  color: #888;
}

.actions {
  display: grid;
  grid-template-columns: 110px 1fr;
  gap: 24px;
  margin-top: 20px;
}

.back-button,
.finish-button {
  border: none;
  border-radius: 24px;
  padding: 16px;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.18);
}

.back-button {
  background-color: #ffeeee;
  color: #000;
}

.finish-button {
  background-color: #e8b5d5;
  color: #000;
}
</style>