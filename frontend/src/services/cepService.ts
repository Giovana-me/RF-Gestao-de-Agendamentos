import axios from "axios";

export interface ViaCepResponse {
    cep: string;
    logradouro: string;
    bairro: string;
    localidade: string;
    uf: string;
    erro?: boolean;
}

export async function buscarCep(cep: string): Promise<ViaCepResponse> {

    const cepLimpo = cep.replace(/\D/g, "");

    const response = await axios.get<ViaCepResponse>(
        `https://viacep.com.br/ws/${cepLimpo}/json/`
    );

    return response.data;
}