```vue
<template>
  <ClienteLayout>
    <h2>Minhas solicitações e agendamentos</h2>
    <p class="subtitle">
      Acompanhe suas solicitações de agendamento e seus respectivos status.
    </p>

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

      <div class="historico-button-container">
        <button
          class="historico-button"
          @click="carregarHistorico"
          :disabled="carregandoHistorico"
        >
          {{
            carregandoHistorico
              ? "Carregando..."
              : mostrarHistorico
                ? "Ocultar histórico"
                : "Ver histórico"
          }}
        </button>
      </div>

      <div v-if="mostrarHistorico" class="historico-content">
        <section class="historico-section">
          <h3 class="section-title">Histórico de solicitações</h3>

          <div
            v-if="historicoSolicitacoes.length > 0"
            class="cards-wrapper"
          >
            <div class="cards">
              <article
                v-for="solicitacao in historicoSolicitacoes"
                :key="solicitacao.id"
                class="request-card historico-card"
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
              </article>
            </div>
          </div>

          <p v-else class="message">
            Nenhuma solicitação antiga encontrada.
          </p>
        </section>

        <section class="historico-section">
          <h3 class="section-title">Histórico de agendamentos</h3>

          <div
            v-if="historicoAgendamentos.length > 0"
            class="cards-wrapper"
          >
            <div class="cards">
              <article
                v-for="agendamento in historicoAgendamentos"
                :key="agendamento.id"
                class="request-card historico-card"
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

          <p v-else class="message">
            Nenhum agendamento antigo encontrado.
          </p>
        </section>
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
const historicoSolicitacoes = ref([]);
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
    s => !["RECUSADA", "CANCELADA"].includes(s.status)
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

async function carregarHistorico() {
  if (mostrarHistorico.value) {
    mostrarHistorico.value = false;
    return;
  }

  const usuarioLogado = getUsuarioAutenticadoOuErro();

  try {
    carregandoHistorico.value = true;

    const [solicitacoesResponse, agendamentosResponse] = await Promise.all([
      api.get(`/solicitacoes/cliente/${usuarioLogado.id}/historico`),
      api.get(`/agendamentos/cliente/${usuarioLogado.id}/historico`)
    ]);

    historicoSolicitacoes.value = solicitacoesResponse.data;
    historicoAgendamentos.value = agendamentosResponse.data;
    mostrarHistorico.value = true;
  } catch (error) {
    console.error(error);
    toast.error("Não foi possível carregar o histórico.");
  } finally {
    carregandoHistorico.value = false;
  }
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
  overflow-x: auto;
  overflow-y: hidden;
  padding: 8px 4px 18px;
}

.cards {
  display: flex;
  flex-wrap: nowrap;
  gap: 28px;
  width: max-content;
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
  background-color: #bb5151;
  color: #fff;
}

.danger-button:hover:not(:disabled) {
  background: #933341;
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

.historico-button-container {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.historico-button {
  border: none;
  border-radius: 26px;
  min-width: 220px;
  height: 50px;
  padding: 0 28px;
  background-color: #94335c;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.historico-button:hover:not(:disabled) {
  background-color: #7d284d;
}

.historico-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.historico-content {
  margin-top: 10px;
  padding-top: 30px;
  border-top: 1px solid #ddd;
}

.historico-section {
  margin-bottom: 50px;
}

.historico-card {
  border: 1px solid #ddd;
}

.cards-wrapper::-webkit-scrollbar {
  height: 10px;
}

.cards-wrapper::-webkit-scrollbar-track {
  background: #eeeeee;
  border-radius: 10px;
}

.cards-wrapper::-webkit-scrollbar-thumb {
  background: #94335c;
  border-radius: 10px;
}

.cards-wrapper::-webkit-scrollbar-thumb:hover {
  background: #7d284d;
}

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

@media (max-width: 600px) {
  .request-card {
    flex-basis: 320px;
    width: 320px;
  }

  .cards {
    gap: 18px;
  }
}
</style>