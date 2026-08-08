<template>
  <header class="cliente-header">
    <div class="cliente-user-area">
      <div class="cliente-avatar">
        <UserRound :size="36" />
      </div>

      <div>
        <h1>{{ nomeUsuario }}</h1>
        <button class="cliente-edit-button" @click="abrirModal">
          <Pencil :size="18" />
          Editar
        </button>
      </div>
    </div>

    <button class="cliente-logout-button" @click="realizarLogout">
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
    :visivel="modalAlterarEmail"
    @fechar="fecharAlteracaoEmail"
    @confirmar="confirmarAlteracaoEmail"
/>
<AlterarSenhaModal
    :visivel="modalAlterarSenha"
    @fechar="fecharAlteracaoSenha"
    @confirmar="confirmarAlteracaoSenha"
/>
</template>

<script setup lang="ts">

import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { atualizarSessao } from "../../services/authService.ts";
import { getUsuarioLogado, logout } from "../../services/authService.ts";
import { atualizarUsuario } from "../../services/usuarioService.ts";
import EditarContaModal from "../modals/EditarContaModal.vue";
import { useToast } from "vue-toastification";
import { UserRound, Pencil, LogOut } from "lucide-vue-next";
import type { AtualizarContaDTO } from "../../services/usuarioService.ts";
import AlterarEmailModal from "../modals/AlterarEmailModal.vue";
import { alterarEmail } from "../../services/usuarioService";
import AlterarSenhaModal from "../modals/AlterarSenhaModal.vue";
import { alterarSenha } from "../../services/usuarioService";

const router = useRouter();
const nomeUsuario = ref("");
const modalAberto = ref(false);
const toast = useToast();
const modalAlterarEmail = ref(false);
const modalAlterarSenha = ref(false);

onMounted(() => {
    const usuario = getUsuarioLogado();
    if (usuario) {
        nomeUsuario.value = usuario.nome;
    }
});

function abrirAlteracaoEmail() {
    modalAberto.value = false;
    modalAlterarEmail.value = true;
}

function fecharAlteracaoEmail() {
    modalAlterarEmail.value = false;
    modalAberto.value = true;
}

async function confirmarAlteracaoEmail(dados: {
    email: string;
    senha: string;
}) {
    try {
        const usuario = getUsuarioLogado();
        if (!usuario) {
            return;
        }
        const usuarioAtualizado = await alterarEmail(
          usuario.id,
          {
            novoEmail: dados.email,
            senhaAtual: dados.senha
          }
        );

        const sessaoAtualizada = {
            ...usuario,
            nome: usuarioAtualizado.nome,
            email: usuarioAtualizado.email,
            telefone: usuarioAtualizado.telefone,
            endereco: usuarioAtualizado.endereco
        };

        atualizarSessao(sessaoAtualizada);

        toast.success("E-mail alterado com sucesso!");
        fecharAlteracaoEmail();

    } catch (error) {
        console.error(error);
        toast.error("Não foi possível alterar o e-mail.");
    }
}

function abrirAlteracaoSenha() {
    modalAberto.value = false;
    modalAlterarSenha.value = true;
}

function fecharAlteracaoSenha() {
    modalAlterarSenha.value = false;
    modalAberto.value = true;
}

async function confirmarAlteracaoSenha(dados: {
    senhaAtual: string;
    novaSenha: string;
}) {
    try {

        const usuario = getUsuarioLogado();

        if (!usuario) {
            return;
        }

        await alterarSenha(
            usuario.id,
            {
                senhaAtual: dados.senhaAtual,
                novaSenha: dados.novaSenha
            }
        );

        toast.success("Senha alterada com sucesso!");

        fecharAlteracaoSenha();

    } catch (error) {
        console.error(error);
        toast.error("Não foi possível alterar a senha.");
    }
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

        toast.success("Conta atualizada com sucesso!")

      } catch (error) {
        console.error(error);
        toast.error("Não foi possível atualizar a conta.")
      }
}

function realizarLogout() {
    logout();
    router.push("/login");
}

function abrirModal(){
    modalAberto.value = true;
}

function fecharModal(){
    modalAberto.value = false;
}

</script>