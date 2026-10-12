<template>
  <main class="clientes-admin">
    <div class="pagina-cabecalho">
      <div>
        <h1>Visualizar Clientes</h1>
        <p>Consulte os clientes cadastrados na clínica.</p>
      </div>
    </div>

    <section class="tabela-container">
      <table>
        <thead>
          <tr>
            <th>
              <button
                type="button"
                class="ordenacao-nome"
                @click="alternarOrdenacao"
                :title="ordemCrescente ? 'Ordenar de Z a A' : 'Ordenar de A a Z'"
              >
                Nome
                <span>{{ ordemCrescente ? "↑" : "↓" }}</span>
              </button>
            </th>
            <th>E-mail</th>
            <th>Telefone</th>
            <th>Endereço</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="carregando">
            <td colspan="4" class="mensagem-tabela">
              Carregando clientes...
            </td>
          </tr>

          <tr v-else-if="erro">
            <td colspan="4" class="mensagem-tabela erro">
              {{ erro }}
            </td>
          </tr>

          <tr v-else-if="clientes.length === 0">
            <td colspan="4" class="mensagem-tabela">
              Nenhum cliente cadastrado.
            </td>
          </tr>

          <tr
            v-else
            v-for="cliente in clientesPaginados"
            :key="cliente.id"
          >
            <td>
              <button
                type="button"
                class="nome-cliente"
                @click="abrirAgendamentos(cliente)"
                :title="`Ver os últimos agendamentos de ${cliente.nome}`"
              >
                {{ cliente.nome }}
              </button>
            </td>

            <td>{{ cliente.email }}</td>
            <td>{{ cliente.telefone || "-" }}</td>
            <td>{{ formatarEndereco(cliente.endereco) }}</td>
          </tr>
        </tbody>
      </table>
    </section>
    <div
      v-if="!carregando && !erro && clientes.length > 0"
      class="paginacao"
    >
      <span class="paginacao-info">
        Mostrando {{ indiceInicial + 1 }}–{{ indiceFinal }}
        de {{ clientes.length }} clientes
      </span>

      <div class="paginacao-controles">
        <button
          type="button"
          class="paginacao-btn"
          :disabled="paginaAtual === 1"
          @click="paginaAtual--"
        >
          Anterior
        </button>

        <span class="paginacao-pagina">
          Página {{ paginaAtual }} de {{ totalPaginas }}
        </span>

        <button
          type="button"
          class="paginacao-btn"
          :disabled="paginaAtual >= totalPaginas"
          @click="paginaAtual++"
        >
          Próxima
        </button>
      </div>
    </div>
    <div
      v-if="modalAberto"
      class="agenda-modal-overlay"
      @click.self="fecharModal"
    >
      <section
        class="agenda-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-titulo"
      >
        <header class="agenda-modal-header">
          <div>
            <h2 id="modal-titulo">Últimos agendamentos</h2>
            <p class="modal-subtitulo">
              {{ clienteSelecionado?.nome }}
            </p>
          </div>

          <button
            type="button"
            class="agenda-modal-close"
            aria-label="Fechar modal"
            @click="fecharModal"
          >
            &times;
          </button>
        </header>

        <div class="agenda-modal-content">
          <p v-if="carregandoAgendamentos" class="modal-mensagem">
            Carregando agendamentos...
          </p>

          <p
            v-else-if="erroAgendamentos"
            class="modal-mensagem erro"
          >
            {{ erroAgendamentos }}
          </p>

          <p
            v-else-if="agendamentos.length === 0"
            class="modal-mensagem"
          >
            Este cliente ainda não possui agendamentos.
          </p>

          <div v-else class="lista-agendamentos">
            <article
              v-for="agendamento in agendamentos"
              :key="agendamento.id"
              class="agendamento-card"
            >
              <div class="agendamento-card-header">
                <h3>
                  {{ agendamento.procedimento?.nome || "Procedimento" }}
                </h3>

                <span
                  class="status-agendamento"
                  :class="classeStatus(agendamento.status)"
                >
                  {{ formatarStatus(agendamento.status) }}
                </span>
              </div>

              <div class="agendamento-detalhes">
                <p>
                  <strong>Data:</strong>
                  {{ formatarData(agendamento.data) }}
                </p>

                <p>
                  <strong>Horário:</strong>
                  {{ formatarHora(agendamento.hora) }}
                </p>
              </div>
              <div class="observacao-pessoal">
              <div class="observacao-titulo">
                <icon name="notebook-pen" size="lg" color="#94335c" />
                <strong>Observação pessoal</strong>
              </div>

              <template
                v-if="agendamentoEmEdicao === String(agendamento.id)"
              >
                <textarea
                  v-model="textoObservacao"
                  class="observacao-textarea"
                  rows="3"
                  maxlength="2000"
                  placeholder="Escreva um lembrete sobre esta sessão..."
                ></textarea>

                <div class="observacao-acoes">
                  <button
                    type="button"
                    class="btn-observacao cancelar"
                    @click="cancelarEdicaoObservacao"
                  >
                    Cancelar
                  </button>
                  <button
                    type="button"
                    class="btn-observacao salvar"
                    :disabled="salvandoObservacao"
                    @click="salvarObservacao(agendamento.id)"
                  >
                    {{ salvandoObservacao ? "Salvando..." : "Salvar" }}
                  </button>

                </div>
              </template>

              <template v-else>
                <p
                  v-if="observacoesPessoais[String(agendamento.id)]"
                  class="observacao-texto"
                >
                  {{ observacoesPessoais[String(agendamento.id)] }}
                </p>

                <p v-else class="observacao-vazia">
                  Nenhuma observação registrada para esta sessão.
                </p>

                <div class="observacao-acoes">
                  <button
                    type="button"
                    class="btn-observacao salvar"
                    @click="abrirEditorObservacao(agendamento)"
                  >
                    {{
                      observacoesPessoais[String(agendamento.id)]
                        ? "Editar observação"
                        : "Adicionar observação"
                    }}
                  </button>
                </div>
              </template>
            </div>
            </article>
          </div>
        </div>
      </section>
    </div>
  </main>
</template>


<script setup>
import { ref, onMounted, computed } from "vue";
import { useToast } from "vue-toastification";
import api from "@/services/api";

const toast = useToast();

const clientes = ref([]);
const carregando = ref(false);
const erro = ref("");
const paginaAtual = ref(1);
const tamanhoPagina = 8;
const ordemCrescente = ref(true);

const observacoesPessoais = ref({});
const agendamentoEmEdicao = ref(null);
const textoObservacao = ref("");
const salvandoObservacao = ref(false);

const clientesOrdenados = computed(() => {
  return [...clientes.value].sort((a, b) => {
    const comparacao = (a.nome || "").localeCompare(
      b.nome || "",
      "pt-BR",
      { sensitivity: "base" }
    );
    return ordemCrescente.value ? comparacao : -comparacao;
  });
});

const totalPaginas = computed(() => {
  return Math.max(
    1,
    Math.ceil(clientesOrdenados.value.length / tamanhoPagina)
  );
});

const indiceInicial = computed(() => {
  return (paginaAtual.value - 1) * tamanhoPagina;
});

const indiceFinal = computed(() => {
  return Math.min(
    indiceInicial.value + tamanhoPagina,
    clientesOrdenados.value.length
  );
});

const clientesPaginados = computed(() => {
  return clientesOrdenados.value.slice(
    indiceInicial.value,
    indiceFinal.value
  );
});

function alternarOrdenacao() {
  ordemCrescente.value = !ordemCrescente.value;
  paginaAtual.value = 1;
}

const modalAberto = ref(false);
const clienteSelecionado = ref(null);
const agendamentos = ref([]);
const carregandoAgendamentos = ref(false);
const erroAgendamentos = ref("");

function formatarEndereco(endereco) {
  if (!endereco) return "-";

  const partes = [];

  if (endereco.logradouro) partes.push(endereco.logradouro);
  if (endereco.numero) partes.push(endereco.numero);
  if (endereco.bairro) partes.push(endereco.bairro);
  if (endereco.cidade) partes.push(endereco.cidade);
  if (endereco.estado) partes.push(endereco.estado);

  return partes.length > 0 ? partes.join(", ") : "-";
}

async function carregarClientes() {
  carregando.value = true;
  erro.value = "";

  try {
    const response = await api.get("/usuarios/clientes");
    clientes.value = response.data;
    paginaAtual.value = 1;
  } catch (error) {
    console.error("Erro ao carregar clientes:", error);
    erro.value = "Não foi possível carregar os clientes.";
  } finally {
    carregando.value = false;
  }
}

async function abrirAgendamentos(cliente) {
  clienteSelecionado.value = cliente;
  modalAberto.value = true;
  agendamentos.value = [];
  observacoesPessoais.value = {};
  erroAgendamentos.value = "";
  agendamentoEmEdicao.value = null;
  textoObservacao.value = "";
  carregandoAgendamentos.value = true;

  try {
    const response = await api.get(
      `/agendamentos/cliente/${cliente.id}/recentes`
    );

    const lista = Array.isArray(response.data) ? response.data : [];

    agendamentos.value = lista
      .slice()
      .sort((a, b) => {
        const dataA = `${a.data || ""} ${a.hora || ""}`;
        const dataB = `${b.data || ""} ${b.hora || ""}`;
        return dataB.localeCompare(dataA);
      })
      .slice(0, 3);

    await carregarObservacoesAgendamentos();
  } catch (error) {
    console.error("Erro ao carregar agendamentos:", error);
    erroAgendamentos.value =
      "Não foi possível carregar os agendamentos deste cliente.";
  } finally {
    carregandoAgendamentos.value = false;
  }
}

async function carregarObservacoesAgendamentos() {
  const resultados = await Promise.allSettled(
    agendamentos.value.map(async (agendamento) => {
      const response = await api.get(
        `/observacoes-agendamento/agendamento/${agendamento.id}`
      );

      return {
        id: String(agendamento.id),
        texto: response.status === 204 ? "" : response.data?.texto || ""
      };
    })
  );

  const observacoes = {};
  let houveErro = false;

  resultados.forEach((resultado) => {
    if (resultado.status === "fulfilled") {
      observacoes[resultado.value.id] = resultado.value.texto;
    } else {
      houveErro = true;
      console.error(
        "Erro ao carregar uma observação pessoal:",
        resultado.reason
      );
    }
  });

  observacoesPessoais.value = observacoes;

  if (houveErro) {
    toast.error("Não foi possível carregar algumas observações pessoais.");
  }
}

function fecharModal() {
  modalAberto.value = false;
  clienteSelecionado.value = null;
  agendamentos.value = [];
  observacoesPessoais.value = {};
  erroAgendamentos.value = "";
  agendamentoEmEdicao.value = null;
  textoObservacao.value = "";
}

function formatarData(data) {
  if (!data) return "-";

  const dataParte = String(data).split("T")[0];
  const partes = dataParte.split("-");

  if (partes.length !== 3) return data;

  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

function formatarHora(hora) {
  if (!hora) return "-";
  return String(hora).slice(0, 5);
}

function formatarStatus(status) {
  const statusFormatados = {
    PENDENTE: "Pendente",
    CONFIRMADO: "Confirmado",
    CANCELADO: "Cancelado",
    CONCLUIDO: "Concluído"
  };

  return statusFormatados[status] || status || "Indefinido";
}

function classeStatus(status) {
  const classes = {
    PENDENTE: "status-pendente",
    CONFIRMADO: "status-confirmado",
    CANCELADO: "status-cancelado",
    CONCLUIDO: "status-concluido"
  };

  return classes[status] || "";
}

function abrirEditorObservacao(agendamento) {
  agendamentoEmEdicao.value = String(agendamento.id);
  textoObservacao.value =
    observacoesPessoais.value[String(agendamento.id)] || "";
}

async function salvarObservacao(id) {
  const texto = textoObservacao.value.trim();

  if (!texto) {
    toast.warning("Digite uma observação antes de salvar.");
    return;
  }

  if (texto.length > 2000) {
    toast.warning("A observação deve ter no máximo 2000 caracteres.");
    return;
  }

  salvandoObservacao.value = true;

  try {
    const response = await api.put(
      `/observacoes-agendamento/agendamento/${id}`,
      { texto }
    );

    observacoesPessoais.value = {
      ...observacoesPessoais.value,
      [String(id)]: response.data.texto
    };

    agendamentoEmEdicao.value = null;
    textoObservacao.value = "";

    toast.success("Observação pessoal salva com sucesso!");
  } catch (error) {
    console.error("Erro ao salvar observação pessoal:", error);

    if (error.response?.status === 401 || error.response?.status === 403) {
      toast.error("Você não tem autorização para salvar esta observação.");
    } else {
      toast.error("Não foi possível salvar a observação. Tente novamente.");
    }
  } finally {
    salvandoObservacao.value = false;
  }
}

function cancelarEdicaoObservacao() {
  agendamentoEmEdicao.value = null;
  textoObservacao.value = "";
}

onMounted(() => {
  carregarClientes();
});
</script>

<style scoped>
.clientes-admin {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.pagina-cabecalho {
  margin-bottom: 28px;
  flex-shrink: 0;
}

.pagina-cabecalho h1 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 700;
  color: #000;
}

.pagina-cabecalho p {
  margin: 0;
  font-size: 16px;
  color: #555;
}

.tabela-container {
  width: 100%;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: auto;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.22);
}

.tabela-container table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.tabela-container th,
.tabela-container td {
  padding: 18px 20px;
  text-align: left;
}

.tabela-container thead {
  background-color: #ffeeee;
}

.tabela-container th {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #ffeeee;
  color: #000;
  font-size: 15px;
  font-weight: 700;
  border-bottom: 2px solid #dd81b5;
}

.tabela-container td {
  color: #333;
  font-size: 15px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
  overflow-wrap: break-word;
}

.tabela-container tbody tr {
  transition: background-color 0.2s;
}

.tabela-container tbody tr:hover {
  background-color: #fff8fb;
}

.tabela-container tbody tr:last-child td {
  border-bottom: none;
}

.tabela-container th:nth-child(1),
.tabela-container td:nth-child(1) {
  width: 25%;
}

.tabela-container th:nth-child(2),
.tabela-container td:nth-child(2) {
  width: 25%;
}

.tabela-container th:nth-child(3),
.tabela-container td:nth-child(3) {
  width: 18%;
}

.tabela-container th:nth-child(4),
.tabela-container td:nth-child(4) {
  width: 32%;
}

.nome-cliente {
  padding: 0;
  border: none;
  background: transparent;
  color: #94335c;
  font: inherit;
  font-weight: 600;
  text-align: left;
  cursor: pointer;
  transition: color 0.2s;
}

.nome-cliente:hover {
  color: #dd81b5;
  text-decoration: underline;
}

.nome-cliente:focus-visible {
  outline: 2px solid #94335c;
  outline-offset: 4px;
  border-radius: 2px;
}

.mensagem-tabela {
  text-align: center !important;
  padding: 30px !important;
  color: #777 !important;
  font-size: 15px !important;
}

.mensagem-tabela.erro,
.erro {
  color: #94335c !important;
}

/* Modal */
.agenda-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.45);
}

.agenda-modal {
  width: 520px;
  max-width: 100%;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.agenda-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.agenda-modal-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.modal-subtitulo {
  margin: 6px 0 0;
  color: #94335c;
  font-size: 14px;
}

.agenda-modal-close {
  flex-shrink: 0;
  border: none;
  background: transparent;
  font-size: 28px;
  line-height: 1;
  color: #777;
  cursor: pointer;
}

.agenda-modal-close:hover {
  color: #333;
}

.agenda-modal-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  overflow-y: auto;
}

.modal-mensagem {
  margin: 0;
  padding: 20px 0;
  text-align: center;
  font-size: 14px;
  color: #777;
}

.lista-agendamentos {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.agendamento-card {
  padding: 16px;
  border: 1px solid #f0dce6;
  border-radius: 10px;
  background: #fffafb;
}

.agendamento-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.agendamento-card-header h3 {
  margin: 0;
  color: #333;
  font-size: 15px;
  font-weight: 600;
  overflow-wrap: anywhere;
}

.status-agendamento {
  flex-shrink: 0;
  padding: 5px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
}

.status-pendente {
  color: #8a6200;
  background: #fff0c2;
}

.status-confirmado {
  color: #276749;
  background: #d9f4e5;
}

.status-cancelado {
  color: #a12c38;
  background: #ffe1e4;
}

.status-concluido {
  color: #4a467d;
  background: #e8e5ff;
}

.agendamento-detalhes {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.agendamento-detalhes p {
  display: flex;
  flex-wrap: wrap;
  margin: 0;
  font-size: 13px;
  color: #555;
  gap: 4px;
  
}

.agendamento-detalhes strong {
  color: #333;
}

.ordenacao-nome {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0;
  border: none;
  background: transparent;
  color: inherit;
  font: inherit;
  font-weight: 700;
  cursor: pointer;
}

.ordenacao-nome span {
  color: #94335c;
  font-size: 18px;
}

.ordenacao-nome:hover {
  color: #94335c;
}

.paginacao {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px 4px 0;
  flex-shrink: 0;
}

.paginacao-info {
  color: #666;
  font-size: 13px;
}

.paginacao-controles {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.paginacao-pagina {
  color: #333;
  font-size: 13px;
  white-space: nowrap;
}

.paginacao-btn {
  padding: 9px 14px;
  border: 1px solid #dd81b5;
  border-radius: 6px;
  background: #fff;
  color: #94335c;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.paginacao-btn:hover:not(:disabled) {
  background: #94335c;
  color: #fff;
}

.paginacao-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.observacao-pessoal {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
  padding: 14px;
  border: 1px solid #f0dce6;
  border-radius: 10px;
  background: #fff8fb;
}

.observacao-titulo {
  display: flex;
  align-items: center;
  color: #94335c;
  font-size: 14px;
}

.observacao-texto {
  margin: 0;
  color: #444;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.observacao-vazia {
  margin: 0;
  color: #888;
  font-size: 13px;
  line-height: 1.5;
}

.observacao-textarea {
  width: 100%;
  min-height: 80px;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  color: #333;
  font-family: inherit;
  font-size: 13px;
  line-height: 1.5;
  resize: vertical;
  box-sizing: border-box;
}

.observacao-textarea:focus {
  outline: none;
  border-color: #94335c;
  box-shadow: 0 0 0 2px rgba(148, 51, 92, 0.12);
}

.observacao-acoes {
  display: flex;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 8px;
}

.btn-observacao {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-observacao.salvar {
  border: 1px solid #94335c;
  background: #94335c;
  color: #fff;
}

.btn-observacao.salvar:hover {
  background: #762648;
}

.btn-observacao.cancelar {
  border: 1px solid #ddd;
  background: #fff;
  color: #555;
}

.btn-observacao.cancelar:hover {
  background: #f3f3f3;
}

.btn-observacao:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .paginacao {
    align-items: flex-start;
    flex-direction: column;
  }

  .paginacao-controles {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 900px) {
  .tabela-container table {
    min-width: 850px;
  }
}

@media (max-width: 600px) {
  .tabela-container th,
  .tabela-container td {
    padding: 14px 16px;
  }
  .agenda-modal-header {
    padding: 18px;
  }
  .agenda-modal-content {
    padding: 18px;
  }
  .agendamento-card-header {
    flex-direction: column;
  }
}
</style>