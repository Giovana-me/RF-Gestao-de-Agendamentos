interface UsuarioLogado {
    id: number;
    token: string;
    nome: string;
    email: string;
    telefone: string;
    endereco: string;
    tipoUsuario: string;
}

import api from "./api";

export function atualizarSessao(usuario: UsuarioLogado): void {
    sessionStorage.setItem("id", usuario.id.toString());
    sessionStorage.setItem("nome", usuario.nome);
    sessionStorage.setItem("email", usuario.email);
    sessionStorage.setItem("tipoUsuario", usuario.tipoUsuario);
    sessionStorage.setItem("token", usuario.token);
}

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
    sessionStorage.setItem("telefone", usuario.telefone);
    sessionStorage.setItem("endereco", usuario.endereco);
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
        nome: sessionStorage.getItem("nome")?? "",
        email: sessionStorage.getItem("email")?? "",
        tipoUsuario: sessionStorage.getItem("tipoUsuario")?? "",
        telefone: sessionStorage.getItem("telefone")?? "",
        endereco: sessionStorage.getItem("endereco")?? ""
    }
}

export function getUsuarioAutenticadoOuErro(): UsuarioLogado {
    const usuario = getUsuarioLogado();
    if (!usuario) {
        throw new Error("Usuário não autenticado.");
    }
    return usuario;
}