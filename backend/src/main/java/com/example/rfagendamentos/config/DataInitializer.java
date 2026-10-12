package com.example.rfagendamentos.config;

import com.example.rfagendamentos.model.Endereco;
import com.example.rfagendamentos.model.Estado;
import com.example.rfagendamentos.model.TipoUsuario;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner criarProprietaria(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail("amoragns2406@gmail.com").isPresent()) {
                return;
            }
            if (usuarioRepository.findByCpf("00000000000").isPresent()) {
                return;
            }

            Usuario proprietaria = new Usuario();
            Endereco endereco = new Endereco();

            proprietaria.setNome("Rosangela Fernandes");
            proprietaria.setEmail("amoragns2406@gmail.com");
            proprietaria.setSenha(passwordEncoder.encode("Admin23&"));
            proprietaria.setTelefone("(43) 99149-4105");
            proprietaria.setCpf("00000000000");

            endereco.setCep("86170-000");
            endereco.setLogradouro("Rua Minas Gerais");
            endereco.setNumero("141 B");
            endereco.setBairro("Centro");
            endereco.setCidade("Sertanópolis");
            endereco.setEstado(Estado.PR);

            proprietaria.setEndereco(endereco);
            proprietaria.setTipoUsuario(TipoUsuario.PROPRIETARIA);
            proprietaria.setAtivo(true);

            usuarioRepository.save(proprietaria);

            System.out.println(">>> Proprietária criada com sucesso!");
        };
    }
}
