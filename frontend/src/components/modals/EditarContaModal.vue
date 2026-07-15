<template>
<div
    v-if="visivel"
    class="modal-overlay"
>
    <div class="modal">
        <h2>Minha Conta</h2>
        <div class="campo">
            <label>Nome</label>
            <input
                type="text"
                v-model="nome"
            >
        </div>
        <div class="campo">
            <label>Telefone</label>
            <input
                type="text"
                v-model="telefone"
            >
        </div>
        <div class="campo">
            <label>Endereço</label>
            <input
                type="text"
                v-model="endereco"
            >
        </div>
        <div class="campo">

            <label>CPF</label>

            <input
                type="text"
                v-model="cpf"
                disabled
            >
        </div>
        <div class="campo">

            <label>Email</label>

            <input
                type="email"
                v-model="email"
                disabled
            >
        </div>
        <p
            v-if="erro"
            class="mensagem-erro"
        >
        {{ erro }}
        </p>
        <div class="buttons">

            <button
                class="cancelar"
                @click="fechar"
            >
                Cancelar
            </button>

            <button
                class="salvar"
                @click="salvar"
            >
                Salvar
            </button>
        </div>
    </div>
</div>
</template>

<script setup lang="ts">

import { ref } from "vue";
import { watch } from "vue";
import { getUsuarioLogado } from "../../services/authService";
import { buscarUsuarioPorId } from "../../services/usuarioService";

const props = defineProps<{
    visivel: boolean
}>();

const emit = defineEmits([
    "fechar",
    "salvar"
]);

const erro = ref("");
const nome = ref("");
const telefone = ref("");
const endereco = ref("");
const cpf = ref("");
const email = ref("");

async function carregarUsuario() {
    const usuarioLogado = getUsuarioLogado();

    if (!usuarioLogado) {
        return;
    }
    const usuario = await buscarUsuarioPorId(usuarioLogado.id);

    nome.value = usuario.nome;
    telefone.value = usuario.telefone;
    endereco.value = usuario.endereco;
    cpf.value = usuario.cpf;
    email.value = usuario.email;
}

function fechar(){
    emit("fechar");
}

function salvar() {

    erro.value = "";

    if (!nome.value.trim()) {
        erro.value = "O nome é obrigatório.";
        return;
    }

    if (!telefone.value.trim()) {
        erro.value = "O telefone é obrigatório.";
        return;
    }

    if (!endereco.value.trim()) {
        erro.value = "O endereço é obrigatório.";
        return;
    }

    emit("salvar", {
        nome: nome.value,
        telefone: telefone.value,
        endereco: endereco.value
    });

}

watch(
    () => props.visivel,
    (abriu) => {
        if (abriu) {
            carregarUsuario();
        }
    }
);
</script>

<style scoped>

.modal-overlay{
    position:fixed;
    inset:0;
    background:rgba(0,0,0,.5);
    display:flex;
    justify-content:center;
    align-items:center;
    z-index:999;
}

.modal{
    width: 800px;
    height: 800px;
    background:white;
    border-radius:30px;
    padding:30px;
}

.modal h2 {
    margin: 0;
    font-size: 28px;
    font-weight: 700;
    color: #000;
    padding: 20px;
}

.campo{
    font-size: 18px;
    color: rgb(63, 63, 63);
    display:flex;
    flex-direction:column;
    margin-bottom:15px;
    padding: 5px;
}

input{
    padding:10px;
    border-radius:26px;
    height: 60px;
    border:1px solid #ccc;
    font-size: 18px;
}

.mensagem-erro{
    color:#dc3545;
    font-size:14px;
    margin-top:10px;
    margin-bottom:10px;
}

.buttons {
    display:flex;
    justify-content:flex;
    gap:10px;
    margin-top:25px;
    height: 50px;
}

.salvar{
    background:#dd81b5;
    color:white;
    border-radius: 26px;
    border: none;
    width: 100%;
    font-size: 16px;
}

.salvar:hover {
    background:#94335c;
    color:white;
}

.cancelar{
    background:#ffeeee;
    border-radius: 26px;
    border: none;
    width: 100%;
    font-size: 16px;
}

.cancelar:hover {
    background:#94335c;
    color:white;
}

</style>