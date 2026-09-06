<template>
  <main class="clientes-admin">

    <div class="pagina-cabecalho">
      <div>
        <h1>Visualizar Clientes</h1>
        <p>
          Consulte os clientes cadastrados na clínica.
        </p>
      </div>
    </div>

    <section class="tabela-container">
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>E-mail</th>
            <th>Telefone</th>
            <th>CPF</th>
            <th>Endereço</th>
          </tr>
        </thead>

        <tbody>

          <tr v-if="carregando">
            <td colspan="5" class="mensagem-tabela">
              Carregando clientes...
            </td>
          </tr>

          <tr v-else-if="erro">
            <td colspan="5" class="mensagem-tabela erro">
              {{ erro }}
            </td>
          </tr>

          <tr v-else-if="clientes.length === 0">
            <td colspan="5" class="mensagem-tabela">
              Nenhum cliente cadastrado.
            </td>
          </tr>

          <tr
            v-else
            v-for="cliente in clientes"
            :key="cliente.id"
          >
            <td>{{ cliente.nome }}</td>

            <td>{{ cliente.email }}</td>

            <td>{{ cliente.telefone || "-" }}</td>

            <td>{{ cliente.cpf || "-" }}</td>

            <td>
              {{ formatarEndereco(cliente.endereco) }}
            </td>
          </tr>

        </tbody>
      </table>
    </section>

  </main>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";

const clientes = ref([]);
const carregando = ref(false);
const erro = ref("");

function formatarEndereco(endereco) {
  if (!endereco) {
    return "-";
  }

  const partes = [];

  if (endereco.logradouro) {
    partes.push(endereco.logradouro);
  }

  if (endereco.numero) {
    partes.push(endereco.numero);
  }

  if (endereco.bairro) {
    partes.push(endereco.bairro);
  }

  if (endereco.cidade) {
    partes.push(endereco.cidade);
  }

  if (endereco.estado) {
    partes.push(endereco.estado);
  }

  return partes.length > 0 ? partes.join(", ") : "-";
}

async function carregarClientes() {
  carregando.value = true;
  erro.value = "";

  try {
    const response = await api.get("/usuarios/clientes");
    clientes.value = response.data;
  } catch (error) {
    console.error("Erro ao carregar clientes:", error);
    erro.value = "Não foi possível carregar os clientes.";
  } finally {
    carregando.value = false;
  }
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
  width: 20%;
  font-weight: 600;
  color: #000;
}

.tabela-container th:nth-child(2),
.tabela-container td:nth-child(2) {
  width: 20%;
}

.tabela-container th:nth-child(3),
.tabela-container td:nth-child(3) {
  width: 15%;
}

.tabela-container th:nth-child(4),
.tabela-container td:nth-child(4) {
  width: 15%;
}

.tabela-container th:nth-child(5),
.tabela-container td:nth-child(5) {
  width: 30%;
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

@media (max-width: 900px) {
  .tabela-container table {
    min-width: 900px;
  }

  .tabela-container {
    overflow-x: auto;
  }
}

@media (max-width: 600px) {
  .tabela-container table {
    min-width: 850px;
  }

  .tabela-container th,
  .tabela-container td {
    padding: 14px 16px;
  }
}

</style>