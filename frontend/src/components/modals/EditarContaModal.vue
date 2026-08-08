<template>
<div
    v-if="visivel"
    class="modal-overlay"
>
    <div class="modal">
    <h2>Minha Conta</h2>
        <div class="modal-content">
            <div class="campo">
                <label>Nome</label>
                <input
                    type="text"
                    v-model="usuario.nome"
                >
            </div>
            <div class="campo">
                <label>Telefone</label>
                <input
                    type="text"
                    v-model="usuario.telefone"
                >
            </div>
            <div class="campo">

                <label>CPF</label>

                <input
                    type="text"
                    v-model="usuario.cpf"
                >
            </div>
            <div class="campo">
                <label>CEP</label>

                <input
                    type="text"
                    v-model="usuario.endereco.cep"
                    maxlength="9"
                    @input="formatarCep"
                    @blur="consultarCep"
                >
            </div>

            <div class="campo">
                <label>Rua</label>

                <input
                    type="text"
                    v-model="usuario.endereco.logradouro"
                    readonly
                >
            </div>

            <div class="campo">
                <label>Número</label>

                <input
                    type="text"
                    v-model="usuario.endereco.numero"
                >
            </div>

            <div class="campo">
                <label>Bairro</label>

                <input
                    type="text"
                    v-model="usuario.endereco.bairro"
                    readonly
                >
            </div>

            <div class="campo">
                <label>Cidade</label>

                <input
                    type="text"
                    v-model="usuario.endereco.cidade"
                    readonly
                >
            </div>

            <div class="campo">
                <label>Estado</label>

                <input
                    type="text"
                    v-model="usuario.endereco.estado"
                    readonly
                >
            </div>
            <div class="campo-acoes">
            <label>E-mail</label>
            <div class="linha-acao">
                <span>{{ usuario.email }}</span>
                <button
                    class="link-acao"
                    @click="$emit('alterarEmail')"
                >
                    Alterar e-mail
                </button>
            </div>
            </div>
            <div class="campo-acoes">
                <label>Senha</label>
                <div class="linha-acao">
                    <span>••••••••</span>
                    <button
                        class="link-acao"
                        @click="$emit('alterarSenha')"
                    >
                        Alterar senha
                    </button>
                </div>
            </div>
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
import { buscarCep } from "@/services/cepService";
import { useToast } from "vue-toastification";
import type { Usuario } from "../../services/usuarioService";

const toast = useToast();

const props = defineProps<{
    visivel: boolean
}>();

const emit = defineEmits([
    "fechar",
    "salvar",
    "alterarEmail",
    "alterarSenha"
]);

const erro = ref("");

const usuario = ref<Usuario>({
    id: 0,
    nome: "",
    email: "",
    telefone: "",
    cpf: "",
    endereco: {
        cep: "",
        logradouro: "",
        numero: "",
        bairro: "",
        cidade: "",
        estado: ""
    },
    tipoUsuario: "",
    ativo: true
});

async function carregarUsuario() {
    const usuarioLogado = getUsuarioLogado();

    if (!usuarioLogado) {
        return;
    }
    usuario.value = await buscarUsuarioPorId(usuarioLogado.id);
}

async function consultarCep() {
    const cep = usuario.value.endereco.cep.replace(/\D/g, "");
    if (cep.length !== 8) {
        limparEndereco();
        return;
    }
    try {
        const resultado = await buscarCep(cep);

        if (resultado.erro) {
            toast.error("CEP não encontrado.");
            return;
        }
        usuario.value.endereco.logradouro = resultado.logradouro;
        usuario.value.endereco.bairro = resultado.bairro;
        usuario.value.endereco.cidade = resultado.localidade;
        usuario.value.endereco.estado = resultado.uf;

    } catch (error) {
        console.error(error);
        toast.error("Erro ao consultar o CEP.");
    }
}

function fechar(){
    emit("fechar");
}

function salvar() {
    erro.value = "";
    if (!usuario.value.nome.trim()) {
        erro.value = "O nome é obrigatório.";
        return;
    }
    if (!usuario.value.telefone.trim()) {
        erro.value = "O telefone é obrigatório.";
        return;
    }
    if (!usuario.value.cpf.trim()) {
        erro.value = "O CPF é obrigatório.";
        return;
    }
    if (!usuario.value.endereco.cep.trim()) {
        erro.value = "O CEP é obrigatório.";
        return;
    }
    if (!usuario.value.endereco.numero.trim()) {
        erro.value = "O número é obrigatório.";
        return;
    }
    emit("salvar", {
        nome: usuario.value.nome,
        telefone: usuario.value.telefone,
        cpf: usuario.value.cpf,
        endereco: usuario.value.endereco
    });
}

function formatarCep() {
    let cep = usuario.value.endereco.cep.replace(/\D/g, "");
    if (cep.length > 8) {
        cep = cep.substring(0, 8);
    }

    if (cep.length > 5) {
        cep = cep.replace(/^(\d{5})(\d+)/, "$1-$2");
    }
    usuario.value.endereco.cep = cep;
}

function limparEndereco() {
    usuario.value.endereco.logradouro = "";
    usuario.value.endereco.bairro = "";
    usuario.value.endereco.cidade = "";
    usuario.value.endereco.estado = "";
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

.modal-content{
    flex: 1;
    overflow-y: auto;
    padding-right: 8px;
}

.modal{
    width: 800px;
    background:white;
    border-radius:30px;
    padding:30px;
    max-height: 90vh; 
    box-sizing: border-box;
    display: flex; 
    flex-direction: column;
}

.modal h2 {
    font-size: 26px;
    font-weight: 600;
    color: #000;
    padding-bottom: 20px;
}

.campo{
    font-size: 18px;
    color: rgb(63, 63, 63);
    display:flex;
    flex-direction:column;
    margin-bottom:15px;
    padding: 10px;
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
    display: flex;
    gap: 16px;
    margin-top: 25px;
    height: 50px;
    flex-shrink: 0;
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

.modal-content::-webkit-scrollbar{
    width:8px;
}

.modal-content::-webkit-scrollbar-track{
    background:#f3f3f3;
    border-radius:10px;
}

.modal-content::-webkit-scrollbar-thumb{
    background:#dd81b5;
    border-radius:10px;
}

.modal-content::-webkit-scrollbar-thumb:hover{
    background:#94335c;
}

input[readonly] {
    background-color: #f6f6f6;
    color: #555;
    cursor: default;
}

.campo-acoes {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;
    padding: 10px;
}

.campo-acoes label {
    font-size: 18px;
    color: rgb(63, 63, 63);
    margin-bottom: 10px;
}

.linha-acao {
    display: flex;
    justify-content: space-between;
    align-items: center;

    border: 1px solid #ccc;
    border-radius: 26px;

    padding: 0 20px;

    min-height: 60px;
}

.linha-acao span {
    font-size: 18px;
    color: #333;
}

.link-acao {
    background: transparent;
    border: none;

    color: #94335c;
    font-size: 16px;
    font-weight: 600;

    cursor: pointer;
    transition: .2s;
}

.link-acao:hover {
    color: #dd81b5;
    text-decoration: underline;
}

</style>

/*width: 800px;
    background:white;
    border-radius:30px;
    padding:30px;
    max-height: 90vh; 
    box-sizing: border-box;
    display: flex; 
    flex-direction: column;*/
