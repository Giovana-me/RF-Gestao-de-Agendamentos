import api from "./api";

export async function login(email, senha) {
    const response = await api.post("/auth/login", {
        email,
        senha
    });

    return response.data;
}

export function salvarSessao(usuario) {
    sessionStorage.setItem("token", usuario.token);
    sessionStorage.setItem("nome", usuario.nome);
    sessionStorage.setItem("email", usuario.email);
    sessionStorage.setItem("tipoUsuario", usuario.tipoUsuario);
}

export function logout() {
    sessionStorage.clear();
}

export function getToken() {
    return sessionStorage.getItem("token");
}

export function estaAutenticado() {
    return !!getToken();
}

export function isCliente() {
    return sessionStorage.getItem("tipoUsuario") === "CLIENTE";
}

export function isProprietaria() {
    return sessionStorage.getItem("tipoUsuario") === "PROPRIETARIA";
}

export function getUsuarioLogado() {
    return {
        nome: sessionStorage.getItem("nome"),
        email: sessionStorage.getItem("email"),
        tipoUsuario: sessionStorage.getItem("tipoUsuario"),
        token: sessionStorage.getItem("token")
    };
}