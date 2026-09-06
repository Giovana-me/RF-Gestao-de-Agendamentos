```vue
<template>
  <div class="cadastro-container">
    <div class="image-container">
      <img src="../assets/images/login-image.png" alt="" />
    </div>

    <div class="cadastro-card">
      <h1>RF Agendamentos</h1>

      <p class="subtitle">Crie sua conta na RF Estética</p>

      <div class="form-scroll">
        <form @submit.prevent="realizarCadastro">
          <div class="form-group">
            <label>Nome</label>

            <input type="text" v-model="nome" placeholder="Digite seu nome" required />
          </div>

          <div class="form-group">
            <label>E-mail</label>

            <input
              type="email"
              v-model="email"
              placeholder="Digite seu e-mail"
              required
              @input="limparErroEmail"
            />

            <span v-if="erroEmail" class="campo-erro">
              {{ erroEmail }}
            </span>
          </div>

          <div class="form-group">
            <label>Senha</label>

            <input type="password" v-model="senha" placeholder="Digite sua senha" required />
          </div>

          <div class="form-group">
            <label>Telefone</label>

            <input
              type="text"
              v-model="telefone"
              placeholder="Digite apenas números"
              inputmode="numeric"
              maxlength="11"
              required
              @input="formatarTelefone"
            />

            <span class="campo-ajuda"> Digite apenas números. </span>

            <span v-if="erroTelefone" class="campo-erro">
              {{ erroTelefone }}
            </span>
          </div>

          <div class="form-group">
            <label>CPF</label>

            <input
              type="text"
              v-model="cpf"
              placeholder="Digite apenas números"
              inputmode="numeric"
              maxlength="11"
              required
              @input="formatarCpf"
            />

            <span class="campo-ajuda"> Digite apenas números. </span>

            <span v-if="erroCpf" class="campo-erro">
              {{ erroCpf }}
            </span>
          </div>

          <p v-if="erro" class="erro">
            {{ erro }}
          </p>

          <button type="submit" :disabled="carregando">
            {{ carregando ? 'Cadastrando...' : 'Cadastrar' }}
          </button>
        </form>

        <p class="login-link">
          Já possui uma conta?

          <button type="button" class="link-button" @click="irParaLogin">Entrar</button>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'
import { useToast } from 'vue-toastification'

const router = useRouter()
const toast = useToast()

const nome = ref('')
const email = ref('')
const senha = ref('')
const telefone = ref('')
const cpf = ref('')

const erro = ref('')
const erroEmail = ref('')
const erroTelefone = ref('')
const erroCpf = ref('')

const carregando = ref(false)

function formatarTelefone(event: Event) {
  const input = event.target as HTMLInputElement

  telefone.value = input.value.replace(/\D/g, '')

  if (telefone.value.length > 11) {
    telefone.value = telefone.value.substring(0, 11)
  }

  validarTelefone()
}

function formatarCpf(event: Event) {
  const input = event.target as HTMLInputElement

  cpf.value = input.value.replace(/\D/g, '')

  if (cpf.value.length > 11) {
    cpf.value = cpf.value.substring(0, 11)
  }

  validarCpf()
}

function validarTelefone(): boolean {
  if (!telefone.value) {
    erroTelefone.value = 'O telefone é obrigatório.'
    return false
  }

  if (!/^\d+$/.test(telefone.value)) {
    erroTelefone.value = 'Digite apenas números.'
    return false
  }

  if (telefone.value.length !== 10 && telefone.value.length !== 11) {
    erroTelefone.value = 'Informe um telefone válido.'
    return false
  }

  erroTelefone.value = ''
  return true
}

function validarCpf(): boolean {
  if (!cpf.value) {
    erroCpf.value = 'O CPF é obrigatório.'
    return false
  }

  if (!/^\d+$/.test(cpf.value)) {
    erroCpf.value = 'Digite apenas números.'
    return false
  }

  if (cpf.value.length !== 11) {
    erroCpf.value = 'Informe um CPF válido com 11 números.'
    return false
  }

  erroCpf.value = ''
  return true
}

function validarEmail(): boolean {
  if (!email.value.trim()) {
    erroEmail.value = 'O e-mail é obrigatório.'
    return false
  }

  const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)

  if (!emailValido) {
    erroEmail.value = 'Informe um e-mail válido.'
    return false
  }

  erroEmail.value = ''
  return true
}

function limparErroEmail() {
  if (erroEmail.value) {
    validarEmail()
  }
}

async function realizarCadastro() {
  erro.value = ''

  const emailValido = validarEmail()
  const telefoneValido = validarTelefone()
  const cpfValido = validarCpf()

  if (!emailValido || !telefoneValido || !cpfValido) {
    return
  }

  if (!nome.value.trim()) {
    erro.value = 'O nome é obrigatório.'
    return
  }

  if (!senha.value.trim()) {
    erro.value = 'A senha é obrigatória.'
    return
  }

  carregando.value = true

  try {
    await api.post('/auth/cadastro', {
      nome: nome.value.trim(),
      email: email.value.trim(),
      senha: senha.value,
      telefone: telefone.value,
      cpf: cpf.value,
    })

    toast.success('Cadastro realizado com sucesso!')

    setTimeout(() => {
      router.push('/login')
    }, 1000)
  } catch (e: any) {
    console.error(e)

    if (e.response?.data) {
      const dados = e.response.data

      if (typeof dados === 'object') {
        if (dados.email) {
          erroEmail.value = dados.email
        }

        if (dados.telefone) {
          erroTelefone.value = dados.telefone
        }

        if (dados.cpf) {
          erroCpf.value = dados.cpf
        }

        if (dados.nome) {
          erro.value = dados.nome
        }

        if (dados.senha) {
          erro.value = dados.senha
        }

        if (dados.mensagem) {
          erro.value = dados.mensagem
        }
      }
    }

    if (!erro.value && !erroEmail.value && !erroTelefone.value && !erroCpf.value) {
      erro.value = 'Não foi possível realizar o cadastro.'
    }
  } finally {
    carregando.value = false
  }
}

function irParaLogin() {
  router.push('/login')
}
</script>

<style scoped>
.cadastro-container {
  width: 100%;
  height: 100vh;
  display: flex;
  background: #ffeeee;
  overflow: hidden;
}
.image-container {
  width: 50%;
  height: 100vh;
  overflow: hidden;
}
.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cadastro-card {
  width: 50%;
  height: 100vh;
  background: #ffeeee;
  padding: 60px 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}
.cadastro-card h1 {
  text-align: center;
  margin-bottom: 10px;
  color: black;
}
.subtitle {
  text-align: center;
  margin-bottom: 30px;
  color: black;
}
.form-scroll {
  width: 100%;
  max-height: 75vh;
  overflow-y: auto;
  padding: 10px 15px 10px 5px;
}
.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}
.form-group label {
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 18px;
  color: black;
}
.form-group input {
  height: 55px;
  padding: 12px 18px;
  border: 1px solid #ccc;
  border-radius: 26px;
  font-size: 16px;
  background: white;
}
.form-group input:focus {
  outline: none;
  border-color: #dd81b5;
}
.campo-ajuda {
  margin-top: 5px;
  margin-left: 10px;
  font-size: 13px;
  color: #666;
}
.campo-erro {
  margin-top: 5px;
  margin-left: 10px;
  font-size: 13px;
  color: red;
}
.erro {
  color: red;
  margin: 10px 0 15px;
  text-align: center;
}
button[type='submit'] {
  width: 100%;
  height: 50px;
  padding: 12px;
  border: none;
  border-radius: 26px;
  cursor: pointer;
  color: rgb(0, 0, 0);
  background: #dd81b5;
  font-size: 16px;
}
button[type='submit']:hover {
  background: #94335c;
  color: white;
}
button[type='submit']:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.login-link {
  margin-top: 20px;
  text-align: center;
  color: black;
}
.link-button {
  border: none;
  background: none;
  color: #94335c;
  font-size: inherit;
  font-weight: 600;
  cursor: pointer;
}
.link-button:hover {
  text-decoration: underline;
}
.form-scroll::-webkit-scrollbar {
  width: 8px;
}
.form-scroll::-webkit-scrollbar-thumb {
  background: #d8a0bd;
  border-radius: 10px;
}
.form-scroll::-webkit-scrollbar-track {
  background: transparent;
}
</style>
