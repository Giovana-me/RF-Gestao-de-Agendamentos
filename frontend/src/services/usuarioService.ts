import api from "./api";

export interface AtualizarContaDTO {
    nome: string;
    telefone: string;
    endereco: string;
}

export interface Usuario {
    id: number;
    nome: string;
    email: string;
    telefone: string;
    cpf: string;
    endereco: string;
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