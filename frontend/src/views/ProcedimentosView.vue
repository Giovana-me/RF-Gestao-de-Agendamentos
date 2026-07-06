<template>
  <ClienteLayout>
    <Stepper 
    v-if="exibirStepper"
    :etapaAtual="1" />

    <section class="procedures-section">
      <h2>Escolha um procedimento</h2>

      <p v-if="carregando" class="message">Carregando procedimentos...</p>

      <p v-else-if="erro" class="message error">
        {{ erro }}
      </p>

      <div v-else class="procedures-grid">
        <button
          v-for="procedimento in procedimentos"
          :key="procedimento.id"
          class="procedure-card"
          @click="selecionarProcedimento(procedimento)"
        >
          <div class="card-header">
            <h3>{{ procedimento.nome }}</h3>

            <span class="info-icon">
              i

              <span class="tooltip">
                <strong>{{ procedimento.nome }}</strong>
                <br />
                {{ procedimento.descricao }}
                <br /><br />
                <strong>Duração:</strong> {{ procedimento.duracao }}
                <br />
                <strong>Valor:</strong>
                R$ {{ formatarValor(procedimento.valor) }}
              </span>
            </span>
          </div>

          <p class="duration">⏱ {{ procedimento.duracao }}</p>
        </button>
      </div>
    </section>
    <ModalData
      v-if="mostrarModalData"
      @fechar="fecharModalData"
      @continuar="continuarParaHora"
    />
    <ModalHora
      v-if="mostrarModalHora"
      @fechar="fecharModalHora"
      @voltar="voltarParaData"
      @continuar="continuarParaDetalhes"
    />
    <ModalDetalhes
      v-if="mostrarModalDetalhes"
      :procedimento="procedimentoSelecionado"
      :data="dataSelecionada"
      :hora="horaSelecionada"
      @fechar="fecharModalDetalhes"
      @voltar="voltarParaHora"
      @finalizar="finalizarSolicitacao"
    />
  </ClienteLayout>
</template>

<script setup>
import ClienteLayout from "@/components/ClienteLayout.vue";
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import ModalData from "@/components/ModalData.vue";
import ModalHora from "@/components/ModalHora.vue";
import ModalDetalhes from "@/components/ModalDetalhes.vue";
import Stepper from "@/components/Stepper.vue";

const mostrarModalData = ref(false);
const procedimentoSelecionado = ref(null);
const dataSelecionada = ref("");

const mostrarModalHora = ref(false);
const horaSelecionada = ref("");

const mostrarModalDetalhes = ref(false);
const clienteId = ref(1);

const procedimentos = ref([]);
const carregando = ref(true);
const erro = ref("");

const exibirStepper = computed(() => {
  return (
    !mostrarModalData.value &&
    !mostrarModalHora.value &&
    !mostrarModalDetalhes.value
  );
});

async function carregarProcedimentos() {
  try {
    carregando.value = true;

    const response = await api.get("/procedimentos");
    procedimentos.value = response.data;
  } catch (error) {
    console.error(error);
    erro.value = "Não foi possível carregar os procedimentos.";
  } finally {
    carregando.value = false;
  }
}

function selecionarProcedimento(procedimento) {
  procedimentoSelecionado.value = procedimento;
  mostrarModalData.value = true;
}

function fecharModalData() {
  mostrarModalData.value = false;
}

function continuarParaHora(data) {
  dataSelecionada.value = data;
  mostrarModalData.value = false;
  mostrarModalHora.value = true;
}

  function fecharModalHora() {
  mostrarModalHora.value = false;
}

function voltarParaData() {
  mostrarModalHora.value = false;
  mostrarModalData.value = true;
}

function continuarParaDetalhes(hora) {
  horaSelecionada.value = hora;
  mostrarModalHora.value = false;
  mostrarModalDetalhes.value = true;

  console.log("Procedimento:", procedimentoSelecionado.value);
  console.log("Data:", dataSelecionada.value);
  console.log("Hora:", horaSelecionada.value);
}

function fecharModalDetalhes() {
  mostrarModalDetalhes.value = false;
}

function voltarParaHora() {
  mostrarModalDetalhes.value = false;
  mostrarModalHora.value = true;
}

async function finalizarSolicitacao(observacao) {
  try {
    const novaSolicitacao = {
      dataDesejada: dataSelecionada.value,
      horaDesejada: horaSelecionada.value,
      observacao: observacao,
      tipoSolicitacao: "NOVO_AGENDAMENTO",
      cliente: {
        id: clienteId.value
      },
      procedimento: {
        id: procedimentoSelecionado.value.id
      }
    };

    await api.post("/solicitacoes", novaSolicitacao);

    alert("Solicitação enviada com sucesso!");

    mostrarModalDetalhes.value = false;
    procedimentoSelecionado.value = null;
    dataSelecionada.value = "";
    horaSelecionada.value = "";
  } catch (error) {
    console.error(error);
    alert("Não foi possível enviar a solicitação.");
  }
}

function formatarValor(valor) {
  if (valor === null || valor === undefined) {
    return "0,00";
  }

  return Number(valor).toFixed(2).replace(".", ",");
}

onMounted(() => {
  carregarProcedimentos();
});
</script>

<style scoped>

.steps {
  color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 34px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.step span {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: #ffeeee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.step.active span {
  background: #dd81b5;
  color: #fff;
}

.step p {
  margin: 0;
  font-size: 16px;
}

.line {
  width: 150px;
  height: 1px;
  background-color: #000;
  margin: 0 16px 30px;
}

.procedures-section {
  position: relative;
  z-index: 10;
  overflow: visible;
}

.procedures-section h2 {
  margin: 0 0 24px;
  font-size: 20px;
  font-weight: 700;
  color:#000;
}

.procedures-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(200px, 1fr));
  grid-template-rows: repeat(3, 145px);
  gap: 28px;
  position: relative;
  z-index: 10;
  overflow: visible;
}

.procedure-card {
  border: none;
  border-radius: 26px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.22);
  padding: 22px;
  text-align: left;
  cursor: pointer;
  position: relative;
  overflow: visible;
  z-index: 1;
  transition: transform 0.2s, box-shadow 0.2s;
}

.procedure-card:hover {
  transform: translateY(-3px);
  z-index: 999;
  box-shadow:
    0 8px 14px rgba(0, 0, 0, 0.24),
    inset 0 3px 5px rgba(0, 0, 0, 0.12);
}

.procedure-card:nth-child(n + 5) .tooltip {
  top: auto;
  bottom: 38px;
}

.procedure-card:nth-child(4n) .tooltip {
  right: 0;
  transform: translateX(0);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 19px;
  font-weight: 500;
  line-height: 1.2;
  word-break: normal;
  overflow-wrap: break-word;
}

.info-icon {
  width: 28px;
  height: 28px;
  min-width: 28px;
  border-radius: 50%;
  background-color: #dd81b5;
  color: #fff;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1000;
}

.tooltip {
  display: none;
  position: absolute;
  top: 38px;
  right: 0;
  width: 360px;
  max-width: 360px;
  background-color: #fff;
  color: #000;
  border: 1px solid #ddd;
  border-radius: 14px;
  padding: 14px;
  font-size: 14px;
  line-height: 1.4;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.22);
  z-index: 9999;
}

.info-icon:hover .tooltip {
  display: block;
}

.duration {
  margin: 26px 0 0;
  color: #dd81b5;
  font-size: 16px;
}

.message {
  font-size: 18px;
  margin-top: 32px;
}

.error {
  color: #94335c;
}

@media (max-width: 1200px) {
  .procedures-grid {
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: auto;
  }
}

@media (max-width: 900px) {
  .procedures-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .line {
    width: 60px;
  }
}
</style>
