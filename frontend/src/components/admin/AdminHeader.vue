<template>
    <header class="admin-header">
        <div class="admin-user-area">
            <div class="admin-avatar">
                <UserRound :size="36" />
            </div>
            <div>
                <h1>{{ nomeUsuario }}</h1>
                <button class="admin-edit-button" @click="abrirModal">
                    <Pencil :size="18" />
                    Editar
                </button>
            </div>
        </div>

        <button class="admin-logout-button" @click="realizarLogout">
            <LogOut :size="22" />
        </button>
    </header>

    <EditarContaModal
        :visivel="modalAberto"
        @fechar="fecharModal"
        @salvar="salvarConta"
        @alterarEmail="abrirAlteracaoEmail"
        @alterarSenha="abrirAlteracaoSenha"
    />

    <AlterarEmailModal
        ref="alterarEmailModalRef"
        :visivel="modalAlterarEmail"
        :email-atual="emailUsuario"
        @fechar="fecharAlteracaoEmail"
        @solicitar="solicitarAlteracaoEmailUsuario"
        @confirmar="confirmarAlteracaoEmailUsuario"
    />
    <AlterarSenhaModal
    ref="alterarSenhaModalRef"
    :visivel="modalAlterarSenha"
    @fechar="fecharAlteracaoSenha"
    @solicitar="solicitarAlteracaoSenhaUsuario"
    @confirmar="confirmarAlteracaoSenhaUsuario"
/>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { atualizarSessao, getUsuarioLogado, logout } from "../../services/authService.ts";
import {
    atualizarUsuario,
    solicitarAlteracaoEmail,
    confirmarAlteracaoEmail,
    solicitarAlteracaoSenha,
    confirmarAlteracaoSenha
} from "../../services/usuarioService.ts";
import EditarContaModal from "../modals/EditarContaModal.vue";
import AlterarEmailModal from "../modals/AlterarEmailModal.vue";
import AlterarSenhaModal from "../modals/AlterarSenhaModal.vue";
import { useToast } from "vue-toastification";
import { UserRound, Pencil, LogOut } from "lucide-vue-next";
import type { AtualizarContaDTO } from "../../services/usuarioService.ts";

const router = useRouter();
const nomeUsuario = ref("");
const emailUsuario = ref("");
const modalAberto = ref(false);
const toast = useToast();
const modalAlterarEmail = ref(false);
const modalAlterarSenha = ref(false);

const alterarEmailModalRef = ref<InstanceType<typeof AlterarEmailModal> | null>(null);
const alterarSenhaModalRef = ref<InstanceType<typeof AlterarSenhaModal> | null>(null);

onMounted(() => {
    const usuario = getUsuarioLogado();

    if (usuario) {
        nomeUsuario.value = usuario.nome;
        emailUsuario.value = usuario.email;
    }
});

function abrirAlteracaoEmail() {
    modalAberto.value = false;
    modalAlterarEmail.value = true;

    const usuario = getUsuarioLogado();

    if (usuario) {
        emailUsuario.value = usuario.email;
    }
}

function fecharAlteracaoEmail() {
    modalAlterarEmail.value = false;
    modalAberto.value = true;
}

async function solicitarAlteracaoEmailUsuario(dados: {
    novoEmail: string;
    senhaAtual: string;
}) {
    try {
        const usuario = getUsuarioLogado();

        if (!usuario) {
            return;
        }

        await solicitarAlteracaoEmail(
            usuario.id,
            {
                novoEmail: dados.novoEmail,
                senhaAtual: dados.senhaAtual
            }
        );

        alterarEmailModalRef.value?.codigoEnviado();

        toast.success(
            "Código de confirmação enviado para seu novo e-mail!"
        );
    } catch (error) {
        console.error(error);
        toast.error(
            "Não foi possível enviar o código de confirmação."
        );
    }
}

async function confirmarAlteracaoEmailUsuario(dados: {
    codigo: string;
}) {
    try {
        const usuario = getUsuarioLogado();

        if (!usuario) {
            return;
        }

        const usuarioAtualizado = await confirmarAlteracaoEmail(
            usuario.id,
            {
                codigo: dados.codigo
            }
        );

        const sessaoAtualizada = {
            ...usuario,
            id: usuarioAtualizado.id,
            nome: usuarioAtualizado.nome,
            email: usuarioAtualizado.email,
            telefone: usuarioAtualizado.telefone,
            endereco: usuarioAtualizado.endereco,
            tipoUsuario: usuarioAtualizado.tipoUsuario,
            token: usuarioAtualizado.token
        };

        atualizarSessao(sessaoAtualizada);

        nomeUsuario.value = usuarioAtualizado.nome;
        emailUsuario.value = usuarioAtualizado.email;

        alterarEmailModalRef.value?.alteracaoConfirmada();

        toast.success("E-mail alterado com sucesso!");
    } catch (error) {
        console.error(error);
        toast.error(
            "Não foi possível confirmar a alteração do e-mail."
        );
    }
}

function abrirAlteracaoSenha() {
    modalAberto.value = false;
    modalAlterarSenha.value = true;
}

async function solicitarAlteracaoSenhaUsuario(dados: {
    senhaAtual: string;
    novaSenha: string;
}) {
    try {
        const usuario = getUsuarioLogado();

        if (!usuario) {
            return;
        }

        await solicitarAlteracaoSenha(
            usuario.id,
            {
                senhaAtual: dados.senhaAtual,
                novaSenha: dados.novaSenha
            }
        );

        alterarSenhaModalRef.value?.codigoEnviado();

        toast.success(
            "Código de confirmação enviado para seu e-mail!"
        );
    } catch (error) {
        console.error(error);

        toast.error(
            "Não foi possível enviar o código de confirmação."
        );
    }
}

async function confirmarAlteracaoSenhaUsuario(dados: {
    codigo: string;
}) {
    try {
        const usuario = getUsuarioLogado();

        if (!usuario) {
            return;
        }

        await confirmarAlteracaoSenha(
            usuario.id,
            {
                codigo: dados.codigo
            }
        );

        alterarSenhaModalRef.value?.alteracaoConfirmada();

        logout();

        toast.success(
            "Senha alterada com sucesso. Faça login novamente."
        );

        router.push("/login");
    } catch (error) {
        console.error(error);

        toast.error(
            "Não foi possível confirmar a alteração da senha."
        );
    }
}

function fecharAlteracaoSenha() {
    modalAlterarSenha.value = false;
    modalAberto.value = true;
}

async function salvarConta(dados: AtualizarContaDTO) {
    try {
        const usuarioLogado = getUsuarioLogado();

        if (!usuarioLogado) {
            return;
        }

        const usuarioAtualizado = await atualizarUsuario(
            usuarioLogado.id,
            dados
        );

        const sessaoAtualizada = {
            ...usuarioLogado,
            nome: usuarioAtualizado.nome,
            telefone: usuarioAtualizado.telefone,
            endereco: usuarioAtualizado.endereco
        };

        atualizarSessao(sessaoAtualizada);

        nomeUsuario.value = usuarioAtualizado.nome;

        fecharModal();

        toast.success("Conta atualizada com sucesso!");
    } catch (error) {
        console.error(error);
        toast.error("Não foi possível atualizar a conta.");
    }
}

function realizarLogout() {
    logout();
    router.push("/login");
}

function abrirModal() {
    modalAberto.value = true;
}

function fecharModal() {
    modalAberto.value = false;
}
</script>