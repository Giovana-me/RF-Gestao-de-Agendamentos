<template>
    <div
        v-if="visivel"
        class="modal-overlay"
    >
        <div class="modal">

            <h2>Alterar e-mail</h2>

            <div class="campo">
                <label>Novo e-mail</label>

                <input
                    type="email"
                    v-model="novoEmail"
                >
            </div>

            <div class="campo">
                <label>Senha atual</label>

                <PasswordInput
                    v-model="senha"
                    placeholder="Digite sua senha"
                />
            </div>

            <div class="buttons">

                <button
                    class="cancelar"
                    @click="$emit('fechar')"
                >
                    Cancelar
                </button>

                <button
                    class="salvar"
                    @click="confirmar"
                >
                    Confirmar
                </button>

            </div>

        </div>
    </div>
</template>

<script setup lang="ts">

import { ref } from "vue";
import PasswordInput from "../inputs/PasswordInput.vue";

const props = defineProps<{
    visivel: boolean;
}>();

const emit = defineEmits([
    "fechar",
    "confirmar"
]);

const novoEmail = ref("");
const senha = ref("");

function confirmar() {

    emit("confirmar", {
        email: novoEmail.value,
        senha: senha.value
    });

}

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

.campo > input {
    padding: 10px;
    border-radius: 26px;
    height: 60px;
    border: 1px solid #ccc;
    font-size: 18px;
    width: 100%;
    box-sizing: border-box;
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

</style>