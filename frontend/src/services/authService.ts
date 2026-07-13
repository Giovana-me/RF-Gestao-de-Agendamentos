interface UsuarioLogado {
    id: number;
    token: string;
    nome: string;
    email: string;
    tipoUsuario: string;
}

import api from "./api";

export async function login(email: string, senha: string): Promise<UsuarioLogado> {
    const response = await api.post<UsuarioLogado>("/auth/login", {
        email,
        senha
    });

    return response.data;
}

export function salvarSessao(usuario: UsuarioLogado): void {
    sessionStorage.setItem("id", usuario.id.toString());
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

export function getUsuarioLogado(): UsuarioLogado | null {
    const token = sessionStorage.getItem("token");
    if (!token) {
        return null;
    }
    return {
        id: Number(sessionStorage.getItem("id")),
        token,
        nome: sessionStorage.getItem("nome") ?? "",
        email: sessionStorage.getItem("email")?? "",
        tipoUsuario: sessionStorage.getItem("tipoUsuario")?? ""
    };
}