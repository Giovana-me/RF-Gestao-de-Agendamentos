<template>
  <div class="modal-overlay">
    <div class="modal-box">
      <button class="close-button" @click="$emit('fechar')">
        ×
      </button>

      <h2>Selecione uma data</h2>
      <div class="modal-content">
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
      </div>
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
import Stepper from "../Stepper.vue";
import { useToast } from "vue-toastification";

const toast = useToast();

const hoje = new Date();
const dataMinima = hoje.toISOString().split("T")[0];
const emit = defineEmits(["fechar", "continuar"]);
const dataSelecionada = ref("");

function continuar() {
  if (!dataSelecionada.value) {
    toast.info("Selecione uma data para continuar.");
    return;
  }

  const data = new Date(dataSelecionada.value + "T00:00:00");

  if (data.getDay() === 0) {
    toast.info("Não realizamos atendimentos aos domingos.");
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
  color: black;
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

.subtitle {
  margin: 12px 0 28px;
  font-size: 18px;
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
  border-radius: 26px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
  gap:10px;
  margin-top: 25px;
  height: 50px;
  flex-shrink: 0;
}

.back-button:hover{
  background:#94335c;
  color:white;
}

.next-button:hover {
  background:#94335c;
  color:white;
}

.back-button {
  background-color: #ffeeee;
  color: black;
}

.next-button {
  background-color: #dd81b5;
  color: #fff;
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