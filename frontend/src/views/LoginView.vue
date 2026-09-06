<template>
  <div class="login-container">
    <div class="image-container">
        <img src="../assets/images/login-image.png" alt="">
    </div>
    <div class="login-card">

      <h1>RF Agendamentos</h1>

      <p class="subtitle">
        Sistema de Gestão da Clínica RF Estética
      </p>

      <form @submit.prevent="realizarLogin">

        <div class="form-group">
          <label>E-mail</label>

          <input
            type="email"
            v-model="email"
            placeholder="Digite seu e-mail"
            required
          />
        </div>

        <div class="form-group">
          <label>Senha</label>

          <input
            type="password"
            v-model="senha"
            placeholder="Digite sua senha"
            required
          />
        </div>

        <button type="submit">
          Entrar
        </button>

      </form>

      <p
        v-if="erro"
        class="erro"
      >
        {{ erro }}
      </p>
      <p class="cadastro-link">
        Ainda não possui uma conta?
        <span @click="irParaCadastro">Cadastre-se</span>
      </p>

    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login, salvarSessao } from "../services/authService";

const email = ref("");
const senha = ref("");
const erro = ref("");
const router = useRouter();

function irParaCadastro() {
  router.push("/cadastro");
}

async function realizarLogin() {
  erro.value = "";
    try {
      const usuario = await login(
          email.value,
          senha.value
      );
      salvarSessao(usuario);
      if (usuario.tipoUsuario === "PROPRIETARIA") {
        router.push("/agenda");
      } else {
        router.push("/procedimentos");
      }
    } catch (e) {
        erro.value = "E-mail ou senha inválidos.";
    }
}
</script>

<style scoped>

.login-container{
    width: 100%;
    height: 100%;
    display:flex;
    background:#ffeeee;
    overflow: hidden;
}

.image-container {
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-card{
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 50%;
  height: 100%;
  box-sizing: border-box;
  background: #ffeeee;
  padding: 60px 100px;
  overflow: hidden;
}

h1{
    text-align:center;
    margin-bottom:10px;
    color: black;
}

.cadastro-link {
  text-align: center;
  margin-top: 20px;
  color: black;
}

.cadastro-link span {
  color: #94335c;
  font-weight: 600;
  cursor: pointer;
}

.cadastro-link span:hover {
  text-decoration: underline;
}

.subtitle{
    text-align:center;
    margin-bottom:30px;
    color:black;
}

.form-group{
    display:flex;
    flex-direction:column;
    margin-bottom:30px;
}

label{
    margin-bottom:8px;
    font-weight:600;
    font-size: 18px;
    color: black;
}

input{
    height: 60px;
    padding:12px;
    border:1px solid #ccc;
    border-radius:26px;
    font-size: 16px;
}

button{
    width:100%;
    height: 50px;
    padding:12px;
    border:none;
    border-radius:26px;
    cursor:pointer;
    color:rgb(0, 0, 0);
    background:#dd81b5;
    font-size:16px;
}

button:hover{
    background:#94335c;
    color: white;
}

.erro{
    color:red;
    margin-top:15px;
    text-align:center;
}
</style>