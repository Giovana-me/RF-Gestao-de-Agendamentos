<template>
  <ClienteLayout>
    <div class="pagina-cabecalho">
      <div>
        <h2>Minhas solicitações e agendamentos</h2>
        <p class="subtitle">
          Acompanhe suas solicitações de agendamento e seus respectivos status.
        </p>
      </div>

      <button
        class="historico-button"
        @click="abrirHistorico"
        :disabled="carregandoHistorico"
      >
        {{ carregandoHistorico ? "Carregando..." : "Ver histórico" }}
      </button>
    </div>

    <p v-if="carregando" class="message">Carregando...</p>
    <p v-else-if="erro" class="message error">{{ erro }}</p>

    <div v-else class="solicitacoes-content">
      <section v-if="solicitacoesAtivas.length > 0">
        <h3 class="section-title">Em andamento</h3>

        <div class="cards-wrapper">
          <div class="cards">
            <article
              v-for="solicitacao in solicitacoesAtivas"
              :key="solicitacao.id"
              class="request-card"
            >
              <div class="card-top">
                <h3>{{ solicitacao.procedimento?.nome }}</h3>
                <span
                  class="status"
                  :class="classeStatus(solicitacao.status)"
                >
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
        </div>
      </section>

      <section v-if="agendamentos.length > 0" class="agendamentos-section">
        <h3 class="section-title">Meus agendamentos</h3>

        <div class="cards-wrapper">
          <div class="cards">
            <article
              v-for="agendamento in agendamentos"
              :key="agendamento.id"
              class="request-card"
            >
              <div class="card-top">
                <h3>{{ agendamento.procedimento?.nome }}</h3>
                <span
                  class="status"
                  :class="classeStatusAgendamento(agendamento.status)"
                >
                  {{ formatarStatusAgendamento(agendamento.status) }}
                </span>
              </div>

              <div class="info-list">
                <p>
                  <strong>Data:</strong>
                  {{ formatarData(agendamento.data) }}
                </p>
                <p>
                  <strong>Horário:</strong>
                  {{ agendamento.hora }}
                </p>
                <p>
                  <strong>Duração:</strong>
                  {{ agendamento.procedimento?.duracao }}
                </p>
                <p>
                  <strong>Local:</strong>
                  Rua Minas Gerais, 141B - Sertanópolis/PR
                </p>
                <p v-if="agendamento.observacao">
                  <strong>Observação:</strong>
                  {{ agendamento.observacao }}
                </p>
              </div>
            </article>
          </div>
        </div>
      </section>

      <section
        v-if="solicitacoesCanceladas.length > 0"
        class="canceladas-section"
      >
        <h3 class="section-title canceladas-title">Finalizadas</h3>

        <div class="cards-wrapper">
          <div class="cards">
            <article
              v-for="solicitacao in solicitacoesCanceladas"
              :key="solicitacao.id"
              class="request-card cancelado-card"
            >
              <div class="card-top">
                <h3>{{ solicitacao.procedimento?.nome }}</h3>
                <span
                  class="status"
                  :class="classeStatus(solicitacao.status)"
                >
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
                <button class="secondary-button" disabled>
                  Remarcar
                </button>
                <button
                  class="danger-button"
                  @click="inativarSolicitacao(solicitacao.id)"
                >
                  Excluir
                </button>
              </div>
            </article>
          </div>
        </div>
      </section>
    </div>
    <div v-if="mostrarHistorico" class="modal-overlay">
      <div class="modal modal-historico">
        <div class="modal-cabecalho">
          <h2>Histórico de agendamentos</h2>

          <button
            class="btn-fechar"
            @click="fecharHistorico"
          >
            ×
          </button>
        </div>

        <div class="historico-lista">
          <div
            v-if="historicoAgendamentos.length > 0"
            class="historico-cabecalho"
          >
            <span>Procedimento</span>
            <span>Data</span>
            <span>Horário</span>
          </div>

          <div
            v-for="agendamento in historicoAgendamentos"
            :key="agendamento.id"
            class="historico-item"
          >
            <span>{{ agendamento.procedimento?.nome }}</span>
            <span>{{ formatarData(agendamento.data) }}</span>
            <span>{{ agendamento.hora }}</span>
          </div>

          <p
            v-if="historicoAgendamentos.length === 0"
            class="historico-vazio"
          >
            Nenhum agendamento realizado anteriormente.
          </p>
        </div>

        <div class="modal-acoes">
          <button
            class="btn-cancelar"
            @click="fecharHistorico"
          >
            Fechar
          </button>
        </div>
      </div>
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
import ClienteLayout from "@/components/client/ClienteLayout.vue";
import { ref, onMounted, computed } from "vue";
import api from "@/services/api";
import ModalData from "@/components/modals/ModalData.vue";
import ModalHora from "@/components/modals/ModalHora.vue";
import ModalDetalhes from "@/components/modals/ModalDetalhes.vue";
import { useToast } from "vue-toastification";
import { getUsuarioAutenticadoOuErro } from "@/services/authService";

const solicitacoes = ref([]);
const agendamentos = ref([]);
const historicoAgendamentos = ref([]);
const carregando = ref(true);
const carregandoHistorico = ref(false);
const erro = ref("");
const mostrarHistorico = ref(false);
const mostrarModalData = ref(false);
const mostrarModalHora = ref(false);
const mostrarModalDetalhes = ref(false);
const solicitacaoSelecionada = ref(null);
const dataSelecionada = ref("");
const horaSelecionada = ref("");
const toast = useToast();

const solicitacoesAtivas = computed(() =>
  solicitacoes.value.filter(
    s => s.status === "PENDENTE"
  )
);

const solicitacoesCanceladas = computed(() =>
  solicitacoes.value.filter(
    s => s.status === "RECUSADA" || s.status === "CANCELADA"
  )
);

function formatarStatus(status) {
  const statusFormatado = {
    PENDENTE: "Solicitado",
    APROVADA: "Aprovado",
    RECUSADA: "Recusado",
    CANCELADA: "Cancelado"
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

function formatarStatusAgendamento(status) {
  const statusFormatado = {
    PENDENTE: "Pendente",
    CONFIRMADO: "Confirmado",
    CANCELADO: "Cancelado",
    CONCLUIDO: "Concluído"
  };
  return statusFormatado[status] || status;
}

function classeStatusAgendamento(status) {
  return {
    PENDENTE: "status-pendente",
    CONFIRMADO: "status-aprovada",
    CANCELADO: "status-cancelada",
    CONCLUIDO: "status-concluido"
  }[status];
}

function formatarData(data) {
  if (!data) {
    return "";
  }

  const partes = data.split("-");

  if (partes.length !== 3) {
    return data;
  }

  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

async function carregarSolicitacoes() {
  const usuarioLogado = getUsuarioAutenticadoOuErro();

  try {
    const response = await api.get(
      `/solicitacoes/cliente/${usuarioLogado.id}/recentes`
    );
    solicitacoes.value = response.data;
  } catch (error) {
    console.error(error);
    erro.value = "Não foi possível carregar suas solicitações.";
  }
}

async function carregarAgendamentos() {
  const usuarioLogado = getUsuarioAutenticadoOuErro();

  try {
    const response = await api.get(
      `/agendamentos/cliente/${usuarioLogado.id}/recentes`
    );
    agendamentos.value = response.data;
  } catch (error) {
    console.error(error);
    erro.value = "Não foi possível carregar seus agendamentos.";
  }
}

async function abrirHistorico() {
  const usuarioLogado = getUsuarioAutenticadoOuErro();

  try {
    carregandoHistorico.value = true;

    const response = await api.get(
      `/agendamentos/cliente/${usuarioLogado.id}/historico`
    );

    historicoAgendamentos.value = response.data;
    mostrarHistorico.value = true;
  } catch (error) {
    console.error(error);
    toast.error("Não foi possível carregar o histórico.");
  } finally {
    carregandoHistorico.value = false;
  }
}

function fecharHistorico() {
  mostrarHistorico.value = false;
}

async function cancelarSolicitacao(id) {
  const confirmar = confirm(
    "Deseja realmente cancelar esta solicitação?"
  );

  if (!confirmar) {
    return;
  }

  try {
    await api.patch(`/solicitacoes/${id}/cancelar`);
    await carregarSolicitacoes();
    toast.success("Solicitação cancelada com sucesso!");
  } catch (error) {
    console.error(error);
    toast.error("Não foi possível cancelar a solicitação.");
  }
}

async function inativarSolicitacao(id) {
  const confirmar = confirm(
    "Deseja realmente excluir esta solicitação?"
  );

  if (!confirmar) {
    return;
  }

  try {
    await api.patch(`/solicitacoes/${id}/inativar`);
    await carregarSolicitacoes();
    toast.success("Solicitação excluída com sucesso!");
  } catch (error) {
    console.error(error);
    toast.error("Não foi possível excluir a solicitação.");
  }
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

onMounted(async () => {
  try {
    await Promise.all([
      carregarSolicitacoes(),
      carregarAgendamentos()
    ]);
  } finally {
    carregando.value = false;
  }
});
</script>

<style scoped>
h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #000;
}

.subtitle {
  margin: 10px 0 32px;
  font-size: 16px;
  color: #000;
}

.solicitacoes-content {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px;
}

.section-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 700;
  color: #000;
}

.cards-wrapper {
  width: 100%;
  padding: 8px 4px 18px;
  box-sizing: border-box;
}

.cards {
  display: flex;
  flex-wrap: wrap;
  gap: 28px;
  width: 100%;
}

.request-card {
  flex: 0 0 380px;
  width: 380px;
  min-height: 310px;
  border-radius: 30px;
  padding: 26px;
  box-sizing: border-box;
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

.status-recusada,
.status-cancelada {
  background-color: #c23030;
}

.status-concluido {
  background-color: #6f42c1;
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
  border-radius: 26px;
  width: 100%;
  height: 50px;
  gap: 10px;
  font-size: 16px;
  cursor: pointer;
}

.secondary-button {
  background-color: #ffeeee;
  color: #000;
}

.secondary-button:hover:not(:disabled) {
  background: #94335c;
  color: white;
}

.danger-button {
  background-color: #c23030;
  color: #fff;
}

.danger-button:hover:not(:disabled) {
  background: #610a0a;
  color: white;
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

.agendamentos-section {
  margin-top: 50px;
}

.canceladas-section {
  margin-top: 50px;
}

.canceladas-title {
  color: #c23030;
}

.cancelado-card {
  border: 1px solid #c23030;
  opacity: 0.75;
}

.pagina-cabecalho {
  margin-bottom: 28px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 10px;
  flex-shrink: 0;
}

.historico-button {
  border: none;
  border-radius: 10px;
  padding: 12px 18px;
  background-color: #dd81b5;
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition:
    background-color 0.2s,
    transform 0.2s,
    box-shadow 0.2s;
  white-space: nowrap;
}

.historico-button:hover:not(:disabled) {
  background-color: #c9689e;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.historico-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
  z-index: 99999;
}

.modal {
  position: relative;
  width: 100%;
  max-width: 650px;
  max-height: 90vh;
  background-color: #fff;
  border-radius: 20px;
  padding: 28px;
  box-sizing: border-box;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-historico {
  width: 650px;
  max-width: 90%;
}

.modal-cabecalho {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
  padding-bottom: 18px;
  border-bottom: 1px solid #eee;
}

.modal-cabecalho h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #000;
}

.btn-fechar {
  width: 36px;
  height: 36px;
  padding: 0;
  border: none;
  border-radius: 50%;
  background-color: #ffeeee;
  color: #000;
  font-size: 26px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.btn-fechar:hover {
  background-color: #94335c;
  color: #fff;
}

.historico-lista {
  width: 100%;
  max-height: 360px;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 6px;
  box-sizing: border-box;
}

.historico-cabecalho,
.historico-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 110px 90px;
  align-items: center;
  gap: 16px;
  padding: 14px 12px;
}

.historico-cabecalho {
  border: 1px solid #dd81b5;
  border-bottom: 2px solid #dd81b5;
  border-radius: 8px 8px 0 0;
  background-color: #ffeeee;
  font-size: 14px;
  font-weight: 700;
  color: #333;
}

.historico-item {
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #333;
}

.historico-item:last-child {
  border-bottom: none;
}

.historico-item span {
  min-width: 0;
  overflow-wrap: break-word;
}

.historico-item span:first-child {
  font-weight: 600;
  color: #000;
}

.historico-vazio {
  margin: 30px 0;
  text-align: center;
  color: #777;
  font-size: 15px;
}

.historico-lista::-webkit-scrollbar {
  width: 8px;
}

.historico-lista::-webkit-scrollbar-track {
  background: #eeeeee;
  border-radius: 10px;
}

.historico-lista::-webkit-scrollbar-thumb {
  background: #94335c;
  border-radius: 10px;
}

.historico-lista::-webkit-scrollbar-thumb:hover {
  background: #7d284d;
}

.modal-acoes {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid #eee;
}

.btn-cancelar {
  border: none;
  border-radius: 26px;
  min-width: 110px;
  height: 46px;
  padding: 0 22px;
  background-color: #ffeeee;
  color: #000;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.btn-cancelar:hover {
  background-color: #94335c;
  color: #fff;
}

/* Scroll da página */

.solicitacoes-content::-webkit-scrollbar {
  width: 10px;
}

.solicitacoes-content::-webkit-scrollbar-track {
  background: #eeeeee;
  border-radius: 10px;
}

.solicitacoes-content::-webkit-scrollbar-thumb {
  background: #94335c;
  border-radius: 10px;
}

.solicitacoes-content::-webkit-scrollbar-thumb:hover {
  background: #7d284d;
}

/* Responsividade */

@media (max-width: 700px) {
  .modal-overlay {
    padding: 12px;
  }

  .modal {
    max-width: 100%;
    max-height: 92vh;
    padding: 22px;
    border-radius: 18px;
  }

  .modal-historico {
    width: 100%;
    max-width: 100%;
  }

  .historico-cabecalho,
  .historico-item {
    grid-template-columns: minmax(0, 1fr) 90px 70px;
    gap: 10px;
    padding: 12px 8px;
    font-size: 13px;
  }

  .modal-cabecalho h2 {
    font-size: 18px;
  }
}

@media (max-width: 600px) {
  .request-card {
    flex-basis: 320px;
    width: 320px;
  }

  .cards {
    gap: 18px;
  }

  .pagina-cabecalho {
    align-items: flex-start;
    flex-direction: column;
  }

  .historico-button {
    align-self: flex-start;
  }
}
</style>