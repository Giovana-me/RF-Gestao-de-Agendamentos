<template>
  <ClienteLayout>
    <h2>Minhas solicitações</h2>

    <p class="subtitle">
      Acompanhe suas solicitações de agendamento e seus respectivos status.
    </p>

    <p v-if="carregando" class="message">
      Carregando solicitações...
    </p>

    <p v-else-if="erro" class="message error">
      {{ erro }}
    </p>

    <p v-else-if="solicitacoes.length === 0" class="message">
      Você ainda não possui solicitações cadastradas.
    </p>

    <div v-else>

  <section v-if="solicitacoesAtivas.length > 0">
    <h3 class="section-title">
      Solicitações Ativas
    </h3>

    <div class="cards">
      <article
        v-for="solicitacao in solicitacoesAtivas"
        :key="solicitacao.id"
        class="request-card"
      >
        <div class="card-top">
          <h3>{{ solicitacao.procedimento?.nome }}</h3>

          <span class="status" :class="classeStatus(solicitacao.status)">
            {{ formatarStatus(solicitacao.status) }}
          </span>
        </div>

        <div class="info-list">
          <p>
            <strong>Data:</strong>
            {{ formatarData(solicitacao.dataDesejada) }}
          </p>

          <p>
            <strong>Horário:</strong>
            {{ solicitacao.horaDesejada }}
          </p>

          <p>
            <strong>Duração:</strong>
            {{ solicitacao.procedimento?.duracao }}
          </p>

          <p>
            <strong>Local:</strong>
            Rua Minas Gerais, 141B - Sertanópolis/PR
          </p>

          <p v-if="solicitacao.observacao">
            <strong>Observação:</strong>
            {{ solicitacao.observacao }}
          </p>
        </div>

        <div class="divider"></div>

        <div class="actions">
          <button
            class="secondary-button"
            :disabled="solicitacao.status !== 'PENDENTE'"
            @click="abrirRemarcacao(solicitacao)"
          >
            Remarcar
          </button>

          <button
            class="danger-button"
            :disabled="solicitacao.status !== 'PENDENTE'"
            @click="cancelarSolicitacao(solicitacao.id)"
          >
            Cancelar
          </button>
        </div>
      </article>
    </div>
  </section>

  <section
    v-if="solicitacoesCanceladas.length > 0"
    class="canceladas-section"
  >
    <h3 class="section-title canceladas-title">
      Solicitações Canceladas
    </h3>

    <div class="cards">
      <article
        v-for="solicitacao in solicitacoesCanceladas"
        :key="solicitacao.id"
        class="request-card cancelado-card"
      >
        <div class="card-top">
          <h3>{{ solicitacao.procedimento?.nome }}</h3>

          <span class="status" :class="classeStatus(solicitacao.status)">
            {{ formatarStatus(solicitacao.status) }}
          </span>
        </div>

        <div class="info-list">
          <p>
            <strong>Data:</strong>
            {{ formatarData(solicitacao.dataDesejada) }}
          </p>

          <p>
            <strong>Horário:</strong>
            {{ solicitacao.horaDesejada }}
          </p>

          <p>
            <strong>Duração:</strong>
            {{ solicitacao.procedimento?.duracao }}
          </p>

          <p>
            <strong>Local:</strong>
            Rua Minas Gerais, 141B - Sertanópolis/PR
          </p>

          <p v-if="solicitacao.observacao">
            <strong>Observação:</strong>
            {{ solicitacao.observacao }}
          </p>
        </div>

        <div class="divider"></div>

        <div class="actions">
          <button
            class="secondary-button"
            disabled
          >
            Remarcar
          </button>

          <button
            class="danger-button"
            disabled
          >
            Cancelado
          </button>
        </div>
      </article>
    </div>
  </section>

</div>
    <ModalData
  v-if="mostrarModalData"
  @fechar="mostrarModalData = false"
  @continuar="selecionarData"
/>
<ModalHora
  v-if="mostrarModalHora"
  :data="dataSelecionada"
  @fechar="mostrarModalHora = false"
  @voltar="voltarParaData"
  @continuar="selecionarHora"
/>
<ModalDetalhes
  v-if="mostrarModalDetalhes"
  :procedimento="solicitacaoSelecionada?.procedimento"
  :data="dataSelecionada"
  :hora="horaSelecionada"
  @fechar="mostrarModalDetalhes = false"
  @voltar="voltarParaHora"
  @finalizar="finalizarRemarcacao"
/>
  </ClienteLayout>
</template>

<script setup>
import ClienteLayout from "@/components/ClienteLayout.vue";
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import ModalData from "@/components/modals/ModalData.vue";
import ModalHora from "@/components/modals/ModalHora.vue";
import ModalDetalhes from "@/components/modals/ModalDetalhes.vue";
import { useToast } from "vue-toastification";
import { getUsuarioLogado } from "@/services/authService";
import { getUsuarioAutenticadoOuErro } from "@/services/authService";

const solicitacoes = ref([]);
const solicitacoesAtivas = computed(() =>
  solicitacoes.value.filter(
    s => !["RECUSADA", "CANCELADA"].includes(s.status)
  )
);

const solicitacoesCanceladas = computed(() =>
  solicitacoes.value.filter(
    s => s.status === "RECUSADA" || s.status === "CANCELADA"
  )
);
const carregando = ref(true);
const erro = ref("");
const mostrarModalData = ref(false);
const mostrarModalHora = ref(false);
const mostrarModalDetalhes = ref(false);
const solicitacaoSelecionada = ref(null);
const dataSelecionada = ref("");
const horaSelecionada = ref("");
const toast = useToast();

async function carregarSolicitacoes() {
  const usuarioLogado = getUsuarioAutenticadoOuErro();
  try {
    carregando.value = true;

    const response = await api.get(`/solicitacoes/cliente/${usuarioLogado.id}`);
    solicitacoes.value = response.data;
  } catch (error) {
    console.error(error);
    erro.value = "Não foi possível carregar suas solicitações.";
  } finally {
    carregando.value = false;
  }
}

async function cancelarSolicitacao(id) {
  const confirmar = confirm("Deseja realmente cancelar esta solicitação?");

  if (!confirmar) {
    return;
  }

  try {
    await api.patch(`/solicitacoes/${id}/cancelar`);
    await carregarSolicitacoes();
  } catch (error) {
    console.error(error);
    toast.error("Não foi possível cancelar a solicitação.");
  }
}

function formatarStatus(status) {
  const statusFormatado = {
    PENDENTE: "Solicitado",
    APROVADA: "Aprovado",
    RECUSADA: "Cancelado"
  };

  return statusFormatado[status] || status;
}

function classeStatus(status) {
  return {
    PENDENTE: "status-pendente",
    APROVADA: "status-aprovada",
    RECUSADA: "status-recusada",
    CANCELADA: "status-cancelada"
  }[status];
}

function formatarData(data) {
  if (!data) return "";

  const partes = data.split("-");

  if (partes.length !== 3) {
    return data;
  }

  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function abrirRemarcacao(solicitacao) {
  solicitacaoSelecionada.value = solicitacao;
  mostrarModalData.value = true;
}

function selecionarData(data) {
  dataSelecionada.value = data;
  mostrarModalData.value = false;
  mostrarModalHora.value = true;
}

function selecionarHora(hora) {
  horaSelecionada.value = hora;

  mostrarModalHora.value = false;
  mostrarModalDetalhes.value = true;
}

function voltarParaData() {
  mostrarModalHora.value = false;
  mostrarModalData.value = true;
}

function voltarParaHora() {
  mostrarModalDetalhes.value = false;
  mostrarModalHora.value = true;
}

async function finalizarRemarcacao(observacao) {
  try {

    await api.put(
      `/solicitacoes/${solicitacaoSelecionada.value.id}/remarcar`,
      {
        dataDesejada: dataSelecionada.value,
        horaDesejada: horaSelecionada.value,
        observacao
      }
    );

    mostrarModalDetalhes.value = false;
    await carregarSolicitacoes();
    toast.success("Solicitação remarcada com sucesso!");

  } catch (error) {
    console.error(error);
    toast.error("Não foi possível remarcar.");

  }
}

onMounted(() => {
  carregarSolicitacoes();
});
</script>

<style scoped>

h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #000;
}

h3.section-title {
  font-size: 20px;
}

.subtitle {
  margin: 10px 0 32px;
  font-size: 16px;
  color: #000000;
}

.cards {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, minmax(300px, 1fr));
  gap: 28px;
  flex-grow: 1;
  align-content: start;
}

.request-card {
  
  border-radius: 30px;
  padding: 26px;
  box-sizing: border-box;
  min-height: 310px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.22);
  background-color: #fff;
}

.card-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 26px;
  color: #000;
}

.card-top h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
}

.status {
  border-radius: 30px;
  padding: 10px 24px;
  font-size: 15px;
  color: #fff;
  white-space: nowrap;
}

.status-pendente {
  background-color: #81d1dd;
}

.status-aprovada {
  background-color: #2f9e44;
}

.status-recusada {
  background-color: #c23030;
}

.status-cancelada {
  background-color: #c23030;
}

.info-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  color: #000;
}

.info-list p {
  margin: 0;
  font-size: 16px;
  line-height: 1.4;
  color: #000;
}

.divider {
  height: 1px;
  background-color: rgba(0, 0, 0, 0.5);
  margin: 28px 0 18px;
}

.actions {
  margin-top: auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.secondary-button,
.danger-button {
  border: none;
  border-radius: 30px;
  padding: 16px;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 4px 4px rgba(0, 0, 0, 0.2);
}

.secondary-button {
  background-color: #ffeeee;
  color: #000;
}

.danger-button {
  background-color: #bb5151;
  color: #fff;
}

.secondary-button:disabled,
.danger-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.message {
  margin-top: 32px;
  font-size: 18px;
}

.error {
  color: #94335c;
}

.section-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 700;
  color: #000;
}

.canceladas-section {
  margin-top: 50px;
}

.canceladas-title {
  color: #c23030;
}

.cancelado-card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.22);
  border: 1px solid #c23030;
  opacity: 0.75;
}

@media (max-width: 1100px) {
  .cards {
    grid-template-columns: 1fr;
  }
}
</style>