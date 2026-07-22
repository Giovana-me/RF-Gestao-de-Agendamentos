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

const router = useRouter();
const nomeUsuario = ref("");
const modalAberto = ref(false);
const toast = useToast();

onMounted(() => {
    const usuario = getUsuarioLogado();
    if (usuario) {
        nomeUsuario.value = usuario.nome;
    }
});

async function salvarConta(dados: {
    nome: string;
    telefone: string;
    endereco: string;
}) {
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
        nome: usuarioAtualizado.nome
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