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

      <div v-else class="table-container">
        <table class="price-table">
          <thead>
            <tr>
              <th>Procedimento</th>
              <th>Descrição</th>
              <th>Tempo médio</th>
              <th>Preço</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="procedimento in procedimentos"
              :key="procedimento.id"
            >
              <td class="procedure-name">
                {{ procedimento.nome }}
              </td>

              <td class="procedure-description">
                <div
                    class="description-content"
                    :class="{ expanded: descricoesExpandidas.has(procedimento.id) }"
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
                    :class="{ rotated: descricoesExpandidas.has(procedimento.id) }"
                    />
                </button>
                </td>

              <td>
                {{ procedimento.duracao }}
              </td>

              <td class="procedure-price">
                {{ formatarValor(procedimento.valor) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </ClienteLayout>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import ClienteLayout from "@/components/client/ClienteLayout.vue";
import api from "@/services/api";
import { ChevronDown } from "lucide-vue-next";

const procedimentos = ref([]);
const carregando = ref(true);
const erro = ref("");
const descricoesExpandidas = reactive(new Set());

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
}

.page-header {
  margin-bottom: 28px;
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

.table-container {
  width: 100%;
  overflow-x: auto;
  border-radius: 26px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.22);
}

.price-table {
  width: 100%;
  border-collapse: collapse;
  overflow: hidden;
  table-layout: fixed;
}

.price-table th,
.price-table td {
  padding: 18px 20px;
  text-align: left;
}

.price-table thead {
  background-color: #ffeeee;
}

.price-table th {
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

.price-table tbody tr:last-child td {
  border-bottom: none;
}

.procedure-name {
  width: 22%;
  font-weight: 600;
  color: #000 !important;
}

.procedure-description {
  width: 43%;
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
  padding: 0;

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

.price-table th:nth-child(3),
.price-table td:nth-child(3) {
  width: 17%;
  text-align: center;
}

.price-table th:nth-child(4),
.price-table td:nth-child(4) {
  width: 18%;
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
    min-width: 700px;
  }

  .table-container {
    overflow-x: auto;
  }
}

@media (max-width: 600px) {
  .price-table {
    min-width: 650px;
  }

  .price-table th,
  .price-table td {
    padding: 14px 16px;
  }
}

</style>