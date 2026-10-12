<template>
    <div v-if="visivel" class="modal-overlay">
        <div class="modal">
            <template v-if="etapa === 1">
                <h2>Alterar e-mail</h2>

                <div class="campo">
                    <label>E-mail atual</label>
                    <input
                        type="email"
                        :value="emailAtual"
                        readonly
                    >
                </div>

                <div class="campo">
                    <label>Novo e-mail</label>
                    <input
                        type="email"
                        v-model="novoEmail"
                        placeholder="Digite seu novo e-mail"
                    >
                </div>

                <div class="campo">
                    <label>Senha atual</label>
                    <PasswordInput
                        v-model="senha"
                        placeholder="Digite sua senha"
                    />
                </div>

                <p v-if="mensagemErro" class="mensagem-erro">
                    {{ mensagemErro }}
                </p>

                <div class="buttons">
                    <button class="cancelar" @click="fechar">
                        Cancelar
                    </button>

                    <button
                        class="salvar"
                        @click="solicitarCodigo"
                        :disabled="carregando"
                    >
                        {{ carregando ? "Enviando..." : "Enviar código" }}
                    </button>
                </div>
            </template>

            <template v-else>
                <h2>Confirme seu novo e-mail</h2>

                <p class="descricao">
                    Enviamos um código de confirmação para:
                </p>

                <p class="email-destino">
                    {{ novoEmail }}
                </p>

                <div class="campo">
                    <label>Código de confirmação</label>
                    <input
                        type="text"
                        v-model="codigo"
                        maxlength="6"
                        inputmode="numeric"
                        placeholder="Digite o código de 6 dígitos"
                    >
                </div>

                <p v-if="mensagemErro" class="mensagem-erro">
                    {{ mensagemErro }}
                </p>

                <div class="reenviar">
                    <button
                        v-if="tempoRestante === 0"
                        class="link-reenviar"
                        @click="reenviarCodigo"
                        :disabled="carregando"
                    >
                        Reenviar código
                    </button>

                    <span v-else>
                        Você poderá reenviar o código em
                        <strong>{{ tempoRestante }}s</strong>
                    </span>
                </div>

                <div class="buttons">
                    <button class="cancelar" @click="voltar">
                        Voltar
                    </button>

                    <button
                        class="salvar"
                        @click="confirmarCodigo"
                        :disabled="carregando"
                    >
                        {{ carregando ? "Confirmando..." : "Confirmar" }}
                    </button>
                </div>
            </template>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from "vue";
import PasswordInput from "../inputs/PasswordInput.vue";

defineProps<{
    visivel: boolean;
    emailAtual: string;
}>();

const emit = defineEmits([
    "fechar",
    "solicitar",
    "confirmar"
]);

const etapa = ref(1);
const novoEmail = ref("");
const senha = ref("");
const codigo = ref("");
const mensagemErro = ref("");
const carregando = ref(false);
const tempoRestante = ref(0);

let intervalo: number | null = null;

function solicitarCodigo() {
    mensagemErro.value = "";

    if (!novoEmail.value.trim()) {
        mensagemErro.value = "Informe o novo e-mail.";
        return;
    }

    if (!senha.value) {
        mensagemErro.value = "Informe sua senha atual.";
        return;
    }

    carregando.value = true;

    emit("solicitar", {
        novoEmail: novoEmail.value.trim(),
        senhaAtual: senha.value
    });
}

function confirmarCodigo() {
    mensagemErro.value = "";

    if (!codigo.value.trim()) {
        mensagemErro.value = "Informe o código de confirmação.";
        return;
    }

    if (codigo.value.length !== 6) {
        mensagemErro.value = "O código deve possuir 6 dígitos.";
        return;
    }

    carregando.value = true;

    emit("confirmar", {
        codigo: codigo.value.trim()
    });
}

function iniciarContagem() {
    tempoRestante.value = 60;

    if (intervalo !== null) {
        clearInterval(intervalo);
    }

    intervalo = window.setInterval(() => {
        tempoRestante.value--;

        if (tempoRestante.value <= 0) {
            tempoRestante.value = 0;

            if (intervalo !== null) {
                clearInterval(intervalo);
                intervalo = null;
            }
        }
    }, 1000);
}

function reenviarCodigo() {
    mensagemErro.value = "";
    carregando.value = true;

    emit("solicitar", {
        novoEmail: novoEmail.value.trim(),
        senhaAtual: senha.value
    });
}

function voltar() {
    etapa.value = 1;
    codigo.value = "";
    mensagemErro.value = "";
    carregando.value = false;
}

function fechar() {
    etapa.value = 1;
    novoEmail.value = "";
    senha.value = "";
    codigo.value = "";
    mensagemErro.value = "";
    carregando.value = false;
    tempoRestante.value = 0;

    if (intervalo !== null) {
        clearInterval(intervalo);
        intervalo = null;
    }

    emit("fechar");
}

function codigoEnviado() {
    etapa.value = 2;
    codigo.value = "";
    mensagemErro.value = "";
    carregando.value = false;
    iniciarContagem();
}

function alteracaoConfirmada() {
    fechar();
}

defineExpose({
    codigoEnviado,
    alteracaoConfirmada
});

onUnmounted(() => {
    if (intervalo !== null) {
        clearInterval(intervalo);
    }
});
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, .5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 99999;
}

.modal {
    width: 800px;
    background: white;
    border-radius: 30px;
    padding: 30px;
    max-height: 90vh;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
}

.modal h2 {
    font-size: 20px;
    font-weight: 600;
    color: #000;
    padding-bottom: 20px;
    margin: 0;
}

.campo {
    font-size: 16px;
    color: rgb(63, 63, 63);
    display: flex;
    flex-direction: column;
    margin-bottom: 15px;
    padding: 10px;
}

.campo > input {
    padding: 10px;
    border-radius: 26px;
    height: 60px;
    border: 1px solid #ccc;
    font-size: 16px;
    width: 100%;
    box-sizing: border-box;
}

.campo > input:focus {
    outline: none;
    border-color: #dd81b5;
}

input[readonly] {
    background-color: #f6f6f6;
    color: #555;
    cursor: default;
}

.mensagem-erro {
    color: #dc3545;
    font-size: 14px;
    margin: 5px 10px 10px;
}

.descricao {
    color: #555;
    font-size: 15px;
    margin: 0 10px 5px;
}

.email-destino {
    color: #94335c;
    font-size: 16px;
    font-weight: 600;
    margin: 0 10px 20px;
    word-break: break-word;
}

.reenviar {
    text-align: center;
    font-size: 14px;
    color: #666;
    margin: 5px 0 10px;
}

.link-reenviar {
    background: none;
    border: none;
    color: #94335c;
    font-size: 14px;
    cursor: pointer;
    text-decoration: underline;
}

.link-reenviar:hover {
    color: #dd81b5;
}

.link-reenviar:disabled {
    cursor: default;
    opacity: 0.6;
}

.buttons {
    display: flex;
    gap: 16px;
    margin-top: 25px;
    height: 50px;
    flex-shrink: 0;
}

.salvar {
    background: #dd81b5;
    color: white;
    border-radius: 26px;
    border: none;
    width: 100%;
    font-size: 16px;
    cursor: pointer;
}

.salvar:hover {
    background: #94335c;
    color: white;
}

.salvar:disabled {
    opacity: 0.6;
    cursor: default;
}

.cancelar {
    background: #ffeeee;
    border-radius: 26px;
    border: none;
    width: 100%;
    font-size: 16px;
    cursor: pointer;
}

.cancelar:hover {
    background: #94335c;
    color: white;
}
</style>