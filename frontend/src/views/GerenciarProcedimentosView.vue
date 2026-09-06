<template>
  <main class="procedimentos-admin">
    <div class="pagina-cabecalho">
      <div>
        <h1>Gerenciar Procedimentos</h1>
        <p>
          Visualize e gerencie os procedimentos oferecidos pela clínica.
        </p>
      </div>

      <button class="btn-novo" @click="abrirModalNovo">
        + Novo procedimento
      </button>
    </div>

    <section class="tabela-container">
      <table>
        <thead>
          <tr>
            <th>Procedimento</th>
            <th>Duração</th>
            <th>Valor</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>

        <tbody>
        <tr v-if="carregando">
            <td colspan="5" class="mensagem-tabela">
            Carregando procedimentos...
            </td>
        </tr>

        <tr v-else-if="erro">
            <td colspan="5" class="mensagem-tabela erro">
            {{ erro }}
            </td>
        </tr>

        <tr v-else-if="procedimentos.length === 0">
            <td colspan="5" class="mensagem-tabela">
            Nenhum procedimento cadastrado.
            </td>
        </tr>

        <tr
            v-else
            v-for="procedimento in procedimentos"
            :key="procedimento.id"
        >
            <td>{{ procedimento.nome }}</td>

            <td>{{ procedimento.duracao }}</td>

            <td>
            R$ {{ Number(procedimento.valor).toFixed(2).replace(".", ",") }}
            </td>

            <td>
            <span
                class="status"
                :class="procedimento.ativo ? 'ativo' : 'inativo'"
            >
                {{ procedimento.ativo ? "Ativo" : "Inativo" }}
            </span>
            </td>

            <td>
            <div class="acoes">
                <button class="btn-editar" @click="abrirModalEditar(procedimento)">
                Editar
                </button>

                <button v-if="procedimento.ativo" class="btn-excluir" @click="confirmarExclusao(procedimento)">
                Excluir
                </button>

                <button v-else class="btn-ativar" @click="ativarProcedimento(procedimento)">
                Ativar
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
          <h2>{{ editando ? "Editar procedimento" : "Novo procedimento" }}</h2>

          <button class="btn-fechar" @click="fecharModal">
            ×
          </button>
        </div>

        <div class="formulario">

          <div class="campo">
            <label for="nome">Nome</label>

            <input
              id="nome"
              v-model="novoProcedimento.nome"
              type="text"
              placeholder="Digite o nome do procedimento"
            />
          </div>

          <div class="campo">
            <label for="descricao">Descrição</label>

            <textarea
              id="descricao"
              v-model="novoProcedimento.descricao"
              placeholder="Digite a descrição do procedimento"
              rows="4"
            ></textarea>
          </div>

          <div class="linha-campos">

            <div class="campo">
              <label for="duracao">Duração</label>

              <input
                id="duracao"
                v-model="novoProcedimento.duracao"
                type="text"
                placeholder="Ex.: 60 min"
              />
            </div>

            <div class="campo">
              <label for="valor">Valor</label>

              <input
                id="valor"
                v-model="novoProcedimento.valor"
                type="number"
                min="0"
                step="0.01"
                placeholder="Ex.: 120.00"
              />
            </div>

          </div>

        </div>

        <div class="modal-acoes">
          <button class="btn-cancelar" @click="fecharModal">
            Cancelar
          </button>

          <button
            class="btn-salvar"
            @click="salvarProcedimento"
            :disabled="salvando"
            >
            {{
                salvando
                ? "Salvando..."
                : editando
                    ? "Salvar alterações"
                    : "Salvar"
            }}
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
        <h2>Excluir procedimento</h2>

        <button
            class="btn-fechar"
            @click="cancelarExclusao"
        >
            ×
        </button>
        </div>

        <div class="confirmacao-conteudo">

        <p>
            Tem certeza que deseja excluir o procedimento
            <strong>
            {{ procedimentoParaExcluir?.nome }}
            </strong>?
        </p>

        <p class="texto-aviso">
            O procedimento deixará de ser disponibilizado
            para novos agendamentos.
        </p>

        </div>

        <div class="modal-acoes">

        <button
            class="btn-cancelar"
            @click="cancelarExclusao"
            :disabled="excluindo"
        >
            Cancelar
        </button>

        <button
            class="btn-excluir-confirmar"
            @click="excluirProcedimento"
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
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useToast } from "vue-toastification";
const toast = useToast();

const procedimentos = ref([]);
const carregando = ref(false);
const erro = ref("");
const mostrarModal = ref(false);
const salvando = ref(false);

const novoProcedimento = ref({
  nome: "",
  descricao: "",
  duracao: "",
  valor: ""
});

const editando = ref(false);
const procedimentoEditandoId = ref(null);
const mostrarConfirmacaoExclusao = ref(false);
const procedimentoParaExcluir = ref(null);
const excluindo = ref(false);

function validarFormulario() {
  const nome = novoProcedimento.value.nome.trim();
  const valor = Number(novoProcedimento.value.valor);

  if (!nome) {
    toast.error("O nome do procedimento é obrigatório.");
    return false;
  }

  if (
    novoProcedimento.value.valor === "" ||
    isNaN(valor) ||
    valor <= 0
  ) {
    toast.error("O valor do procedimento deve ser maior que zero.");
    return false;
  }

  return true;
}

function abrirModalNovo() {
  editando.value = false;
  procedimentoEditandoId.value = null;

  novoProcedimento.value = {
    nome: "",
    descricao: "",
    duracao: "",
    valor: ""
  };

  mostrarModal.value = true;
}

function abrirModalEditar(procedimento) {
  editando.value = true;
  procedimentoEditandoId.value = procedimento.id;

  novoProcedimento.value = {
    nome: procedimento.nome,
    descricao: procedimento.descricao,
    duracao: procedimento.duracao,
    valor: procedimento.valor
  };

  mostrarModal.value = true;
}

function fecharModal() {
  mostrarModal.value = false;
  editando.value = false;
  procedimentoEditandoId.value = null;
}

async function salvarProcedimento() {
  if (!validarFormulario()) {
    return;
  }

  salvando.value = true;
  try {
    const dados = {
      nome: novoProcedimento.value.nome.trim(),
      descricao: novoProcedimento.value.descricao,
      duracao: novoProcedimento.value.duracao,
      valor: Number(novoProcedimento.value.valor)
    };

    if (editando.value) {
      await api.put(
        `/procedimentos/${procedimentoEditandoId.value}`,
        dados
      );
      toast.success("Procedimento atualizado com sucesso.");
    } else {
      await api.post("/procedimentos", dados);

      toast.success("Procedimento cadastrado com sucesso.");
    }
    fecharModal();
    await carregarProcedimentos();

  } catch (error) {
    console.error("Erro ao salvar procedimento:", error);

    toast.error(
      error.response?.data?.nome ||
      error.response?.data?.valor ||
      "Não foi possível salvar o procedimento."
    );

  } finally {
    salvando.value = false;
  }
}

async function carregarProcedimentos() {
  carregando.value = true;
  erro.value = "";

  try {
    const response = await api.get("/procedimentos/todos");

    procedimentos.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar procedimentos:", error);
    erro.value = "Não foi possível carregar os procedimentos.";
  } finally {
    carregando.value = false;
  }
}

function confirmarExclusao(procedimento) {
  procedimentoParaExcluir.value = procedimento;
  mostrarConfirmacaoExclusao.value = true;
}

function cancelarExclusao() {
  mostrarConfirmacaoExclusao.value = false;
  procedimentoParaExcluir.value = null;
}

async function excluirProcedimento() {
  if (!procedimentoParaExcluir.value) {
    return;
  }
  excluindo.value = true;

  try {
    await api.delete(
      `/procedimentos/${procedimentoParaExcluir.value.id}`
    );
    cancelarExclusao();
    await carregarProcedimentos();
    toast.success("Procedimento desativado com sucesso.");
  } catch (error) {
    console.error("Erro ao excluir procedimento:", error);
    toast.error(
      "Não foi possível desativar o procedimento."
    );

  } finally {
    excluindo.value = false;
  }
}

async function ativarProcedimento(procedimento) {
  try {
    await api.patch(
      `/procedimentos/${procedimento.id}/ativar`
    );
    await carregarProcedimentos();
    toast.success("Procedimento ativado com sucesso.");
  } catch (error) {
    console.error("Erro ao ativar procedimento:", error);
    toast.error(
      "Não foi possível ativar o procedimento."
    );
  }
}

onMounted(() => {
  carregarProcedimentos();
});
</script>

<style scoped>

.erro-campo {
  color: #c62828;
  font-size: 13px;
  margin-top: 2px;
}

.campo input.input-erro {
  border-color: #c62828;
}

.procedimentos-admin {
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
  margin-bottom: 28px;
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

.btn-ativar {
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  background-color: #e8f5e9;
  color: #2e7d32;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color 0.2s,
    color 0.2s,
    transform 0.2s;
}

.btn-ativar:hover {
  background-color: #2e7d32;
  color: #fff;
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
  overflow: hidden;
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
  width: 28%;
  font-weight: 600;
  color: #000;
}

.tabela-container th:nth-child(2),
.tabela-container td:nth-child(2) {
  width: 15%;
  text-align: center;
}

.tabela-container th:nth-child(3),
.tabela-container td:nth-child(3) {
  width: 17%;
  text-align: center;
}

.tabela-container th:nth-child(4),
.tabela-container td:nth-child(4) {
  width: 15%;
  text-align: center;
}

.tabela-container th:nth-child(5),
.tabela-container td:nth-child(5) {
  width: 25%;
  text-align: center;
}

.tabela-container td:nth-child(3) {
  color: #dd81b5;
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
}

.status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 70px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.status.ativo {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status.inativo {
  background-color: #eeeeee;
  color: #666;
}

.acoes {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-editar,
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
}

.btn-editar {
  background-color: #ffeeee;
  color: #dd81b5;
}

.btn-editar:hover {
  background-color: #dd81b5;
  color: #fff;
}

.btn-excluir {
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

.campo input,
.campo textarea {
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

.campo input:focus,
.campo textarea:focus {
  border-color: #dd81b5;
  box-shadow: 0 0 0 2px rgba(221, 129, 181, 0.12);
}

.campo textarea {
  resize: vertical;
  min-height: 100px;
}

.linha-campos {
  display: flex;

  gap: 16px;
}

.linha-campos .campo {
  flex: 1;
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
    min-width: 800px;
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
    min-width: 750px;
  }

  .tabela-container th,
  .tabela-container td {
    padding: 14px 16px;
  }

  .linha-campos {
    flex-direction: column;
  }

  .modal {
    width: 90%;
    padding: 20px;
  }
}
</style>