<template>
    <div
        v-if="visivel"
        class="modal-overlay"
    >
        <div class="modal">

            <template v-if="etapa === 1">

                <h2>Alterar senha</h2>

                <div class="campo">
                    <label>Senha atual</label>

                    <PasswordInput
                        v-model="senhaAtual"
                        placeholder="Digite sua senha atual"
                    />
                </div>

                <div class="campo">
                    <label>Nova senha</label>

                    <PasswordInput
                        v-model="novaSenha"
                        :placeholder="placeholderSenha"
                    />
                    <span class="ajuda-senha">
                        {{ ajudaSenha }}
                    </span>
                </div>

                <div class="campo">
                    <label>Confirmar nova senha</label>

                    <PasswordInput
                        v-model="confirmarSenha"
                        :placeholder="placeholderSenha"
                    />
                </div>

                <div class="buttons">

                    <button
                        class="cancelar"
                        @click="fechar"
                        :disabled="carregando"
                    >
                        Cancelar
                    </button>

                    <button
                        class="salvar"
                        @click="solicitar"
                        :disabled="carregando"
                    >
                        {{ carregando ? "Enviando..." : "Enviar código" }}
                    </button>

                </div>

            </template>

            <template v-else>

                <h2>Confirmar alteração</h2>

                <p class="mensagem">
                    Enviamos um código de confirmação
                    para o seu e-mail.
                </p>

                <div class="campo">
                    <label>Código de confirmação</label>

                    <input
                        v-model="codigo"
                        type="text"
                        inputmode="numeric"
                        maxlength="6"
                        placeholder="Digite o código"
                        @input="limitarCodigo"
                    />
                </div>

                <div class="reenviar">

                    <button
                        v-if="tempoRestante === 0"
                        class="reenviar-button"
                        @click="reenviar"
                    >
                        Reenviar código
                    </button>

                    <span v-else>
                        Você poderá reenviar o código em
                        {{ tempoRestante }}s
                    </span>

                </div>

                <div class="buttons">

                    <button
                        class="cancelar"
                        @click="fechar"
                        :disabled="carregando"
                    >
                        Cancelar
                    </button>

                    <button
                        class="salvar"
                        @click="confirmar"
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
import { ref, onUnmounted, computed } from "vue";
import PasswordInput from "../inputs/PasswordInput.vue";
import { useToast } from "vue-toastification";
import { isProprietaria } from "../../services/authService";

const toast = useToast();

defineProps<{
    visivel: boolean;
}>();

const emit = defineEmits([
    "fechar",
    "solicitar",
    "confirmar"
]);

const etapa = ref(1);
const senhaAtual = ref("");
const novaSenha = ref("");
const confirmarSenha = ref("");
const codigo = ref("");
const tempoRestante = ref(0);
const carregando = ref(false);

let intervalo: number | null = null;

const proprietaria = computed(() => isProprietaria());

const tamanhoMinimoSenha = computed(() => {
    return proprietaria.value ? 8 : 6;
});

const ajudaSenha = computed(() => {
    if (proprietaria.value) {
        return "A senha deve possuir pelo menos 8 caracteres, uma letra, um número e um caractere especial.";
    }

    return "A senha deve possuir pelo menos 6 caracteres, uma letra e um caractere especial.";
});

const placeholderSenha = computed(() => {
    if (proprietaria.value) {
        return "Ex.: Senha123@";
    }

    return "Ex.: 12abc@";
});

function solicitar() {
    if (!senhaAtual.value) {
        toast.error(
            "Informe sua senha atual."
        );
        return;
    }

    if (!novaSenha.value) {
        toast.error(
            "Informe a nova senha."
        );
        return;
    }

    if (novaSenha.value.length < tamanhoMinimoSenha.value) {
        toast.error(
            `A nova senha deve possuir pelo menos ${tamanhoMinimoSenha.value} caracteres.`
        );
        return;
    }

    if (!/[A-Za-z]/.test(novaSenha.value)) {
        toast.error(
            "A nova senha deve possuir pelo menos uma letra."
        );
        return;
    }

    if (proprietaria.value && !/\d/.test(novaSenha.value)) {
        toast.error(
            "A nova senha deve possuir pelo menos um número."
        );
        return;
    }

    if (!/[!@#$%^&*(),.?":{}|<>_\-+=\[\]/;'`~]/.test(novaSenha.value)) {
        toast.error(
            "A nova senha deve possuir pelo menos um caractere especial."
        );
        return;
    }

    if (!confirmarSenha.value) {
        toast.error(
            "Confirme a nova senha."
        );
        return;
    }

    if (novaSenha.value !== confirmarSenha.value) {
        toast.error(
            "As senhas não coincidem."
        );
        return;
    }

    if (senhaAtual.value === novaSenha.value) {
        toast.error(
            "A nova senha deve ser diferente da senha atual."
        );
        return;
    }

    carregando.value = true;

    emit("solicitar", {
        senhaAtual: senhaAtual.value,
        novaSenha: novaSenha.value
    });
}

function codigoEnviado() {
    etapa.value = 2;
    codigo.value = "";
    carregando.value = false;
    iniciarContador();
}

function confirmar() {
    if (!codigo.value) {
        toast.error(
            "Informe o código de confirmação."
        );
        return;
    }

    if (!/^\d{6}$/.test(codigo.value)) {
        toast.error(
            "O código deve possuir 6 dígitos."
        );
        return;
    }

    carregando.value = true;

    emit("confirmar", {
        codigo: codigo.value
    });
}

function limitarCodigo(event: Event) {
    const input = event.target as HTMLInputElement;

    codigo.value = input.value
        .replace(/\D/g, "")
        .slice(0, 6);
}

function alteracaoConfirmada() {
    carregando.value = false;
    limparFormulario();
    emit("fechar");
}

function reenviar() {
    etapa.value = 1;
    codigo.value = "";
    carregando.value = false;
    pararContador();
}

function iniciarContador() {
    pararContador();

    tempoRestante.value = 60;

    intervalo = window.setInterval(() => {
        if (tempoRestante.value > 0) {
            tempoRestante.value--;
        } else {
            pararContador();
        }
    }, 1000);
}

function pararContador() {
    if (intervalo !== null) {
        clearInterval(intervalo);
        intervalo = null;
    }
}

function fechar() {
    pararContador();
    limparFormulario();
    emit("fechar");
}

function limparFormulario() {
    etapa.value = 1;
    senhaAtual.value = "";
    novaSenha.value = "";
    confirmarSenha.value = "";
    codigo.value = "";
    tempoRestante.value = 0;
    carregando.value = false;
}

onUnmounted(() => {
    pararContador();
});

defineExpose({
    codigoEnviado,
    alteracaoConfirmada
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

    z-index: 999;

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

}


.campo {

    font-size: 16px;

    color: rgb(63, 63, 63);

    display: flex;

    flex-direction: column;

    margin-bottom: 15px;

    padding: 10px;

}


.campo input {

    padding: 10px;

    border-radius: 26px;

    height: 60px;

    border: 1px solid #ccc;

    font-size: 16px;

    box-sizing: border-box;

    outline: none;

}


.campo input:focus {

    border-color: #dd81b5;

}


.mensagem {

    font-size: 15px;

    color: #555;

    margin: 0 10px 15px;

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

    cursor: not-allowed;

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


.cancelar:disabled {

    opacity: 0.6;

    cursor: not-allowed;

}


.reenviar {

    text-align: center;

    font-size: 14px;

    color: #666;

    margin-top: 5px;

}


.reenviar-button {

    background: none;

    border: none;

    color: #94335c;

    font-size: 14px;

    cursor: pointer;

    text-decoration: underline;

}


.reenviar-button:hover {

    color: #dd81b5;

}

.ajuda-senha {
    font-size: 13px;
    color: #777;
    margin-top: 6px;
    padding-left: 4px;
}

</style>