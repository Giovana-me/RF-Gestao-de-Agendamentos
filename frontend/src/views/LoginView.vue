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

async function realizarLogin() {
  erro.value = "";
    try {
        const usuario = await login(
            email.value,
            senha.value
        );
        salvarSessao(usuario);
        router.push("/procedimentos");
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
}

img {
    width: 100%;
    
}

.login-card{
    display: flex;
    flex-direction: column;
    justify-content: center;
    width: 100%;
    background:#ffeeee;
    padding: 100px;
}

h1{
    text-align:center;
    margin-bottom:10px;
    color: black;
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