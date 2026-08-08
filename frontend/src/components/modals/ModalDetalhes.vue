<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <button class="close-button" @click="$emit('fechar')">
        ×
      </button>

      <h2>Detalhes do agendamento</h2>
      <div class="modal-content">
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
        >
        </textarea>
      </div>

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

import Stepper from "../Stepper.vue";

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

.modal-content{
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
}

.modal-box {
  width: 800px;
  max-height: 90vh;
  background-color: white;
  border-radius: 30px;
  padding: 30px;
  box-sizing: border-box;
  position: relative;
  color: #000;
  display: flex;
  flex-direction: column;
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
  font-size: 18px;
  font-weight: 700;
}

textarea {
  width: 100%;
  height: 140px;
  border: 1px solid #94335c;
  border-radius: 18px;
  box-sizing: border-box;
  padding: 18px;
  font-size: 16px;
  font-family: Inter, Arial, sans-serif;
  resize: none;
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
  border-radius: 26px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
  gap:10px;
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
  color: #000;
}

.finish-button:hover {
  background:#94335c;
  color:white;
}

.finish-button {
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