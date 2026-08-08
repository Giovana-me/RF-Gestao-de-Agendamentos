import api from "./api";
import type {Endereco} from "../interfaces/Endereco";

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

    const response = await api.put<Usuario>(`/usuarios/${id}`, dados);

    return response.data;
}

export interface AlterarEmailDTO {
    novoEmail: string;
    senhaAtual: string;
}

export async function alterarEmail(
    id: number,
    dados: AlterarEmailDTO
): Promise<Usuario> {

    const response = await api.put<Usuario>(
        `/usuarios/${id}/email`,
        dados
    );

    return response.data;
}

export interface AlterarSenhaDTO {
    senhaAtual: string;
    novaSenha: string;
}

export async function alterarSenha(
    id: number,
    dados: AlterarSenhaDTO
): Promise<void> {

    await api.put(
        `/usuarios/${id}/senha`,
        dados
    );
}