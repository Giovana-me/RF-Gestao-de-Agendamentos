<template>
  <ClienteLayout>
    <section class="tabela-precos-section">
      <div class="page-header">
        <h2>Tabela de Preços</h2>
        <p>
          Confira nossos procedimentos, descrições, tempo médio e valores.
        </p>
      </div>
      <p v-if="carregando" class="message">
        Carregando procedimentos...
      </p>
      <p v-else-if="erro" class="message error">
        {{ erro }}
      </p>
      <div v-else-if="procedimentos.length === 0" class="message">
        Nenhum procedimento disponível no momento.
      </div>
      <div v-else>
        <div class="total-container">
          <span class="total-label">
            Total dos procedimentos selecionados:
          </span>

          <span class="total-value">
            {{ formatarValor(totalSelecionado) }}
          </span>
        </div>
        <div class="table-container">
          <table class="price-table">
            <thead>
              <tr>
                <th class="select-column">
                  Selecionar
                </th>
                <th>
                  Procedimento
                </th>
                <th>
                  Descrição
                </th>
                <th>
                  Tempo médio
                </th>
                <th>
                  Preço
                </th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="procedimento in procedimentos"
                :key="procedimento.id"
                :class="{
                  'selected-row': procedimentosSelecionados.has(procedimento.id)
                }"
              >
                <td class="select-column">
                  <input
                    type="checkbox"
                    :id="`procedimento-${procedimento.id}`"
                    :value="procedimento.id"
                    :checked="procedimentosSelecionados.has(procedimento.id)"
                    @change="alternarSelecao(procedimento)"
                    class="procedure-checkbox"
                  />
                  <label
                    :for="`procedimento-${procedimento.id}`"
                    class="checkbox-label"
                  >
                    <span class="sr-only">
                      Selecionar {{ procedimento.nome }}
                    </span>
                  </label>
                </td>
                <td class="procedure-name">
                  {{ procedimento.nome }}
                </td>
                <td class="procedure-description">
                  <div
                    class="description-content"
                    :class="{
                      expanded: descricoesExpandidas.has(procedimento.id)
                    }"
                  >
                    {{ procedimento.descricao }}
                  </div>
                  <button
                    v-if="procedimento.descricao"
                    type="button"
                    class="expand-button"
                    @click="alternarDescricao(procedimento.id)"
                    :aria-label="
                      descricoesExpandidas.has(procedimento.id)
                        ? 'Recolher descrição'
                        : 'Expandir descrição'
                    "
                  >
                    <ChevronDown
                      :size="18"
                      :class="{
                        rotated: descricoesExpandidas.has(procedimento.id)
                      }"
                    />
                  </button>
                </td>
                <td class="procedure-duration">
                  {{ procedimento.duracao }}
                </td>
                <td class="procedure-price">
                  {{ formatarValor(procedimento.valor) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </ClienteLayout>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from "vue";
import ClienteLayout from "@/components/client/ClienteLayout.vue";
import api from "@/services/api";
import { ChevronDown } from "lucide-vue-next";

const procedimentos = ref([]);
const carregando = ref(true);
const erro = ref("");
const procedimentosSelecionados = reactive(new Set());
const descricoesExpandidas = reactive(new Set());

function alternarSelecao(procedimento) {
  if (procedimentosSelecionados.has(procedimento.id)) {
    procedimentosSelecionados.delete(procedimento.id);
  } else {
    procedimentosSelecionados.add(procedimento.id);
  }
}

const totalSelecionado = computed(() => {
  return procedimentos.value
    .filter((procedimento) =>
      procedimentosSelecionados.has(procedimento.id)
    )
    .reduce((total, procedimento) => {
      return total + Number(procedimento.valor || 0);
    }, 0);
});

function alternarDescricao(id) {
  if (descricoesExpandidas.has(id)) {
    descricoesExpandidas.delete(id);
  } else {
    descricoesExpandidas.add(id);
  }
}

async function carregarProcedimentos() {
  try {
    carregando.value = true;
    erro.value = "";
    const response = await api.get("/procedimentos");
    procedimentos.value = response.data;
  } catch (error) {
    console.error(error);
    erro.value = "Não foi possível carregar a tabela de preços.";
  } finally {
    carregando.value = false;
  }
}

function formatarValor(valor) {
  if (valor === null || valor === undefined) {
    return "R$ 0,00";
  }
  return Number(valor).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

onMounted(() => {
  carregarProcedimentos();

});

</script>

<style scoped>

.tabela-precos-section {
  position: relative;
  z-index: 10;
  width: 100%;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.page-header {
  margin-bottom: 20px;
}


.page-header h2 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 700;
  color: #000;
}

.page-header p {
  margin: 0;
  font-size: 16px;
  color: #555;
}

.total-container {
  width: 100%;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 16px 20px;
  margin-bottom: 18px;
  background-color: #ffeeee;
  border: 1px solid #f3c4d9;
  border-radius: 8px;
}

.total-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.total-value {
  font-size: 20px;
  font-weight: 700;
  color: #dd81b5;
  white-space: nowrap;
}

.table-container {
  width: 100%;
  max-height: 400px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: auto;
  background-color: #fff;
}

.price-table thead {
  background-color: #ffeeee;
}

.price-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.price-table th,
.price-table td {
  padding: 18px 20px;
  text-align: left;
}

.price-table th {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #ffeeee;
  color: #000;
  font-size: 15px;
  font-weight: 700;
  border-bottom: 2px solid #dd81b5;
}

.price-table td {
  color: #333;
  font-size: 15px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.price-table tbody tr {
  transition: background-color 0.2s;
}

.price-table tbody tr:hover {
  background-color: #fff8fb;
}

.price-table tbody tr.selected-row {
  background-color: #fff4f9;
}

.price-table tbody tr.selected-row:hover {
  background-color: #ffedf6;
}

.price-table tbody tr:last-child td {
  border-bottom: none;
}

.select-column {
  width: 11% !important;
  text-align: center !important;
}

.procedure-checkbox {
  width: 18px;
  height: 18px;
  margin: 0;
  cursor: pointer;
  accent-color: #dd81b5;
}

.checkbox-label {
  cursor: pointer;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.procedure-name {
  width: 20%;
  font-weight: 600;
  color: #000 !important;
}

.procedure-description {
  width: 40%;
  line-height: 1.5;
  position: relative;
  padding-right: 42px !important;
}

.description-content {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.description-content.expanded {
  display: block;
  overflow: visible;
}

.expand-button {
  position: absolute;
  top: 50%;
  right: 14px;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 50%;
  background-color: #ffeeee;
  color: #dd81b5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    background-color 0.2s,
    transform 0.2s;
}

.expand-button:hover {
  background-color: #dd81b5;
  color: #fff;
}

.expand-button svg {
  transition: transform 0.2s;
}


.expand-button svg.rotated {
  transform: rotate(180deg);
}

.price-table th:nth-child(4),
.price-table td:nth-child(4) {
  width: 14%;
  text-align: center;
}

.price-table th:nth-child(5),
.price-table td:nth-child(5) {
  width: 15%;
  text-align: center;
}


.procedure-price {
  color: #dd81b5 !important;
  font-size: 16px !important;
  font-weight: 700;
  white-space: nowrap;
}

.message {
  font-size: 18px;
  margin-top: 32px;
}

.error {
  color: #94335c;
}

@media (max-width: 900px) {
  .price-table {
    min-width: 750px;
  }
  .table-container {
    overflow-x: auto;
    overflow-y: auto;
  }
}

@media (max-width: 600px) {
  .total-container {
    align-items: flex-start;
    flex-direction: column;
    gap: 6px;
  }

  .total-label {
    font-size: 14px;
  }

  .total-value {
    font-size: 19px;
  }

  .price-table {
    min-width: 700px;
  }

  .price-table th,
  .price-table td {
    padding: 14px 16px;
  }
}
</style>