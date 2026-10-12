<template>
  <main class="horarios-admin">
    <div class="pagina-cabecalho">
      <div>
        <h1>Gerenciar Horários</h1>
        <p>
          Visualize e gerencie os horários de atendimento da clínica.
        </p>
      </div>

      <button class="btn-novo" @click="abrirModal">
        + Novo horário
      </button>
    </div>

    <section class="tabela-container">
      <table>
        <thead>
          <tr>
            <th>Horário</th>
            <th>Ações</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="carregando">
            <td colspan="2" class="mensagem-tabela">
              Carregando horários...
            </td>
          </tr>

          <tr v-else-if="erro">
            <td colspan="2" class="mensagem-tabela erro">
              {{ erro }}
            </td>
          </tr>

          <tr v-else-if="horarios.length === 0">
            <td colspan="2" class="mensagem-tabela">
              Nenhum horário cadastrado.
            </td>
          </tr>

          <tr
            v-else
            v-for="horario in horarios"
            :key="horario.id"
          >
            <td>{{ horario.hora }}</td>

            <td>
              <div class="acoes">
                <button
                  v-if="horario.ativo"
                  class="btn-excluir"
                  @click="confirmarExclusao(horario)"
                >
                  Excluir
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <div v-if="mostrarModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-cabecalho">
          <h2>Novo horário</h2>

          <button class="btn-fechar" @click="fecharModal">
            ×
          </button>
        </div>

        <div class="formulario">
          <div class="campo">
            <label for="horario">Horário</label>

            <input
              id="horario"
              v-model="novoHorario"
              type="time"
            />
          </div>
        </div>

        <div class="modal-acoes">
          <button
            class="btn-cancelar"
            @click="fecharModal"
          >
            Cancelar
          </button>

          <button
            class="btn-salvar"
            @click="salvarHorario"
            :disabled="salvando"
          >
            {{ salvando ? "Salvando..." : "Salvar" }}
          </button>
        </div>
      </div>
    </div>

    <div
      v-if="mostrarConfirmacaoExclusao"
      class="modal-overlay"
    >
      <div class="modal modal-confirmacao">
        <div class="modal-cabecalho">
          <h2>Excluir horário</h2>

          <button
            class="btn-fechar"
            @click="fecharConfirmacao"
          >
            ×
          </button>
        </div>

        <div class="confirmacao-conteudo">
          <p>
            Tem certeza que deseja excluir o horário
            <strong>
              {{ horarioParaExcluir?.hora }}
            </strong>?
          </p>

          <p class="texto-aviso">
            O horário deixará de ser disponibilizado
            para novos agendamentos.
          </p>
        </div>

        <div class="modal-acoes">
          <button
            class="btn-cancelar"
            @click="fecharConfirmacao"
            :disabled="excluindo"
          >
            Cancelar
          </button>

          <button
            class="btn-excluir-confirmar"
            @click="excluirHorario"
            :disabled="excluindo"
          >
            {{ excluindo ? "Excluindo..." : "Excluir" }}
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { onMounted, ref } from "vue";
import api from "@/services/api";
import { useToast } from "vue-toastification";

const toast = useToast();

const horarios = ref([]);
const carregando = ref(false);
const erro = ref("");

const mostrarModal = ref(false);
const novoHorario = ref("");
const salvando = ref(false);

const mostrarConfirmacaoExclusao = ref(false);
const horarioParaExcluir = ref(null);
const excluindo = ref(false);

async function carregarHorarios() {
  carregando.value = true;
  erro.value = "";

  try {
    const response = await api.get("/horarios");
    horarios.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar horários:", error);
    erro.value = "Não foi possível carregar os horários.";
  } finally {
    carregando.value = false;
  }
}

function abrirModal() {
  novoHorario.value = "";
  mostrarModal.value = true;
}

function fecharModal() {
  mostrarModal.value = false;
  novoHorario.value = "";
}

async function salvarHorario() {
  if (!novoHorario.value) {
    toast.error("Informe um horário.");
    return;
  }

  salvando.value = true;

  try {
    await api.post("/horarios", {
      hora: novoHorario.value
    });

    toast.success("Horário cadastrado com sucesso.");

    fecharModal();
    await carregarHorarios();
  } catch (error) {
    console.error("Erro ao salvar horário:", error);

    toast.error(
      error.response?.data?.message ||
      "Não foi possível cadastrar o horário."
    );
  } finally {
    salvando.value = false;
  }
}

function confirmarExclusao(horario) {
  horarioParaExcluir.value = horario;
  mostrarConfirmacaoExclusao.value = true;
}

function fecharConfirmacao() {
  mostrarConfirmacaoExclusao.value = false;
  horarioParaExcluir.value = null;
}

async function excluirHorario() {
  if (!horarioParaExcluir.value) {
    return;
  }

  excluindo.value = true;

  try {
    await api.patch(
      `/horarios/${horarioParaExcluir.value.id}/desativar`
    );

    fecharConfirmacao();
    await carregarHorarios();

    toast.success("Horário excluído com sucesso.");
  } catch (error) {
    console.error("Erro ao excluir horário:", error);

    toast.error(
      "Não foi possível excluir o horário."
    );
  } finally {
    excluindo.value = false;
  }
}

onMounted(() => {
  carregarHorarios();
});
</script>

<style scoped>
.horarios-admin {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.pagina-cabecalho {
  margin-bottom: 28px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
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

.btn-novo {
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
}

.btn-novo:hover {
  background-color: #c9689e;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
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
  width: 50%;
  font-weight: 600;
  color: #000;
}

.tabela-container th:nth-child(2),
.tabela-container td:nth-child(2) {
  width: 50%;
  text-align: center;
}

.acoes {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-excluir {
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color 0.2s,
    color 0.2s,
    transform 0.2s;
  background-color: #fcecec;
  color: #c62828;
}

.btn-excluir:hover {
  background-color: #c62828;
  color: #fff;
}

.mensagem-tabela {
  text-align: center !important;
  padding: 30px !important;
  color: #777 !important;
  font-size: 15px !important;
}

.mensagem-tabela.erro {
  color: #94335c !important;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99999;
}

.modal {
  width: 520px;
  max-width: 90%;
  background-color: #fff;
  border-radius: 18px;
  padding: 26px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.22);
}

.modal-cabecalho {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.modal-cabecalho h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #000;
}

.btn-fechar {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  background-color: #ffeeee;
  color: #dd81b5;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.btn-fechar:hover {
  background-color: #dd81b5;
  color: #fff;
}

.formulario {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.campo {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.campo label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.campo input {
  width: 100%;
  box-sizing: border-box;
  padding: 11px 13px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  color: #333;
  font-family: inherit;
  font-size: 14px;
  outline: none;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

.campo input:focus {
  border-color: #dd81b5;
  box-shadow: 0 0 0 2px rgba(221, 129, 181, 0.12);
}

.modal-acoes {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 26px;
}

.modal-acoes button {
  border: none;
  border-radius: 8px;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color 0.2s,
    transform 0.2s;
}

.btn-cancelar {
  background-color: #eeeeee;
  color: #555;
}

.btn-cancelar:hover {
  background-color: #ddd;
}

.btn-salvar {
  background-color: #dd81b5;
  color: #fff;
}

.btn-salvar:hover {
  background-color: #c9689e;
}

.btn-salvar:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.modal-confirmacao {
  max-width: 480px;
}

.confirmacao-conteudo {
  margin-bottom: 10px;
}

.confirmacao-conteudo p {
  margin: 0 0 12px;
  line-height: 1.5;
  color: #333;
}

.texto-aviso {
  color: #777 !important;
  font-size: 14px;
}

.btn-excluir-confirmar {
  background-color: #c62828;
  color: #fff;
}

.btn-excluir-confirmar:hover {
  background-color: #a91f1f;
}

.btn-excluir-confirmar:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .tabela-container table {
    min-width: 600px;
  }

  .tabela-container {
    overflow-x: auto;
  }

  .pagina-cabecalho {
    align-items: flex-start;
  }
}

@media (max-width: 600px) {
  .pagina-cabecalho {
    flex-direction: column;
    align-items: flex-start;
  }

  .btn-novo {
    width: 100%;
  }

  .tabela-container table {
    min-width: 550px;
  }

  .tabela-container th,
  .tabela-container td {
    padding: 14px 16px;
  }

  .modal {
    width: 90%;
    padding: 20px;
  }
}
</style>