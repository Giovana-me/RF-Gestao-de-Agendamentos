<template>
  <div class="horarios-admin">
    <div class="pagina-cabecalho">
      <div>
        <h1>Gerenciar Horários</h1>
        <p>Cadastre e gerencie os horários de atendimento da clínica.</p>
      </div>
      <button class="btn-novo" @click="abrirModal">
        + Novo horário
      </button>
    </div>
    <div class="card-horarios">

      <div v-if="carregando" class="mensagem">
        Carregando horários...
      </div>

      <div v-else-if="horarios.length === 0" class="mensagem">
        Nenhum horário cadastrado.
      </div>

      <div v-else class="lista-horarios">

        <div
          v-for="horario in horarios"
          :key="horario.id"
          class="horario-item"
        >
          <span class="hora">
            {{ horario.hora }}
          </span>

          <button
            class="btn-excluir"
            @click="confirmarExclusao(horario)"
            title="Excluir horário"
          >
            <Trash2 :size="18" />
          </button>
        </div>

      </div>
    </div>
    <div
      v-if="mostrarModal"
      class="modal-overlay"
      @click.self="fecharModal"
    >
      <div class="modal">

        <div class="modal-header">
          <h2>Novo horário</h2>

          <button
            class="btn-fechar"
            @click="fecharModal"
          >
            ×
          </button>
        </div>

        <div class="modal-body">

          <label for="hora">
            Horário
          </label>

          <input
            id="hora"
            v-model="novoHorario"
            type="time"
          />

        </div>

        <div class="modal-footer">

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
            {{ salvando ? "Salvando..." : "Adicionar" }}
          </button>

        </div>

      </div>
    </div>
    <div
      v-if="mostrarConfirmacao"
      class="modal-overlay"
      @click.self="fecharConfirmacao"
    >
      <div class="modal modal-confirmacao">

        <div class="modal-header">
          <h2>Excluir horário</h2>
          <button
            class="btn-fechar"
            @click="fecharConfirmacao"
          >
            ×
          </button>
        </div>

        <div class="modal-body">
          <p>
            Deseja realmente excluir o horário
            <strong>{{ horarioSelecionado?.hora }}</strong>?
          </p>
        </div>

        <div class="modal-footer">
          <button
            class="btn-cancelar"
            @click="fecharConfirmacao"
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
  </div>
</template>

<script setup lang="ts">

import { onMounted, ref } from "vue";
import api from "@/services/api";
import { useToast } from "vue-toastification";
import { Trash2 } from "lucide-vue-next";

interface Horario {
  id: number;
  hora: string;
  ativo: boolean;
}

const toast = useToast();
const horarios = ref<Horario[]>([]);
const carregando = ref(false);
const salvando = ref(false);
const excluindo = ref(false);
const mostrarModal = ref(false);
const mostrarConfirmacao = ref(false);
const novoHorario = ref("");
const horarioSelecionado = ref<Horario | null>(null);

async function carregarHorarios() {
  carregando.value = true;

  try {
    const response = await api.get("/horarios");
    horarios.value = response.data;

  } catch (error) {
    console.error("Erro ao carregar horários:", error);
    toast.error(
      "Não foi possível carregar os horários."
    );
  } finally {
    carregando.value = false;

  }
}

function abrirModal() {
  novoHorario.value = "";
  mostrarModal.value = true;

}

function fecharModal() {
  if (salvando.value) {
    return;
  }
  mostrarModal.value = false;

}

async function salvarHorario() {
  if (!novoHorario.value) {
    toast.info(
      "Selecione um horário para continuar."
    );
    return;
  }
  salvando.value = true;

  try {

    await api.post("/horarios", {
      hora: novoHorario.value
    });
    toast.success(
      "Horário cadastrado com sucesso!"
    );

    fecharModal();
    await carregarHorarios();

  } catch (error: any) {
    console.error("Erro ao cadastrar horário:", error);
    const mensagem =
      error.response?.data ||
      "Não foi possível cadastrar o horário.";
    toast.error(mensagem);

  } finally {
    salvando.value = false;
  }
}

function confirmarExclusao(horario: Horario) {
  horarioSelecionado.value = horario;
  mostrarConfirmacao.value = true;
}

function fecharConfirmacao() {

  if (excluindo.value) {
    return;
  }
  mostrarConfirmacao.value = false;
  horarioSelecionado.value = null;
}


async function excluirHorario() {
  if (!horarioSelecionado.value) {
    return;
  }
  excluindo.value = true;
  try {

    await api.patch(
      `/horarios/${horarioSelecionado.value.id}/desativar`
    );
    toast.success(
      "Horário excluído com sucesso!"
    );

    fecharConfirmacao();
    await carregarHorarios();
  } catch (error: any) {

    console.error("Erro ao excluir horário:", error);

    const mensagem =
      error.response?.data ||
      "Não foi possível excluir o horário.";

    toast.error(mensagem);

  } finally {
    excluindo.value = false;

  }
}

onMounted(() => {
  carregarHorarios();
});

</script>

<style scoped>

.gerenciar-horarios {
  position: relative;
  z-index: 10;
  width: 100%;
}

.pagina-cabecalho {
  margin-bottom: 28px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
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

.card-horarios {
  width: 100%;
  box-sizing: border-box;
  padding: 26px;
  border-radius: 26px;
  background-color: #fff;
}

.lista-horarios {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.horario-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 64px;
  padding: 12px 14px;
  box-sizing: border-box;
  border: 1px solid #eee;
  border-radius: 26px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.22);
  transition:
    background-color 0.2s,
    border-color 0.2s,
    box-shadow 0.2s,
    transform 0.2s;
}

.horario-item:hover {
  background-color: white;
  box-shadow:
    0 8px 14px rgba(0, 0, 0, 0.24),
    inset 0 3px 5px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.hora {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 90px;
  padding: 9px 14px;
  border-radius: 20px;
  background-color: #ffeeee;
  color: #dd81b5;
  font-size: 16px;
  font-weight: 700;
}

.btn-excluir {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fcecec;
  color: #c62828;
  cursor: pointer;
  transition:
    background-color 0.2s,
    color 0.2s,
    transform 0.2s;
}

.btn-excluir:hover {
  background-color: #c62828;
  color: #fff;
  transform: translateY(-1px);
}

.mensagem {
  text-align: center;
  padding: 30px;
  color: #777;
  font-size: 15px;
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
  box-sizing: border-box;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.modal-header h2 {
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

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.modal-body label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.modal-body input {
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

.modal-body input:focus {
  border-color: #dd81b5;
  box-shadow: 0 0 0 2px rgba(221, 129, 181, 0.12);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 26px;
}

.modal-footer button {
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

.modal-body p {
  margin: 0 0 12px;
  font-size: 15px;
  color: black !important;
}

@media (max-width: 900px) {
  .lista-horarios {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 700px) {
  .pagina-cabecalho {
    align-items: flex-start;
  }
  .lista-horarios {
    grid-template-columns: repeat(2, 1fr);
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
  .card-horarios {
    padding: 20px;
  }
  .lista-horarios {
    grid-template-columns: 1fr;
  }
  .modal {
    width: 90%;
    padding: 20px;
  }
}

</style>