import api from "./api";
import type { Endereco } from "../interfaces/Endereco";

export interface AtualizarContaDTO {
    nome: string;
    telefone: string;
    cpf: string;
    endereco: Endereco;
}

export interface Usuario {
    id: number;
    nome: string;
    email: string;
    telefone: string;
    cpf: string;
    endereco: Endereco;
    tipoUsuario: string;
    ativo: boolean;
}

export async function buscarUsuarioPorId(id: number): Promise<Usuario> {
    const response = await api.get<Usuario>(`/usuarios/${id}`);
    return response.data;
}

export async function atualizarUsuario(
    id: number,
    dados: AtualizarContaDTO
): Promise<Usuario> {
    const response = await api.put<Usuario>(
        `/usuarios/${id}`,
        dados
    );
    return response.data;
}

export interface SolicitarAlteracaoEmailDTO {
    novoEmail: string;
    senhaAtual: string;
}

export async function solicitarAlteracaoEmail(
    id: number,
    dados: SolicitarAlteracaoEmailDTO
): Promise<void> {
    await api.post(
        `/usuarios/${id}/email/solicitar`,
        dados
    );
}

export interface ConfirmarAlteracaoEmailDTO {
    codigo: string;
}

export interface ConfirmarAlteracaoEmailResponse {
    id: number;
    nome: string;
    email: string;
    telefone: string;
    endereco: Endereco;
    tipoUsuario: string;
    token: string;
}

export async function confirmarAlteracaoEmail(
    id: number,
    dados: ConfirmarAlteracaoEmailDTO
): Promise<ConfirmarAlteracaoEmailResponse> {
    const response = await api.post<ConfirmarAlteracaoEmailResponse>(
        `/usuarios/${id}/email/confirmar`,
        dados
    );

    return response.data;
}

export interface SolicitarAlteracaoSenhaDTO {
    senhaAtual: string;
    novaSenha: string;
}

export async function solicitarAlteracaoSenha(
    id: number,
    dados: SolicitarAlteracaoSenhaDTO
): Promise<void> {
    await api.post(
        `/usuarios/${id}/senha/solicitar`,
        dados
    );
}

export interface ConfirmarAlteracaoSenhaDTO {
    codigo: string;
}

export async function confirmarAlteracaoSenha(
    id: number,
    dados: ConfirmarAlteracaoSenhaDTO
): Promise<void> {
    await api.post(
        `/usuarios/${id}/senha/confirmar`,
        dados
    );
}