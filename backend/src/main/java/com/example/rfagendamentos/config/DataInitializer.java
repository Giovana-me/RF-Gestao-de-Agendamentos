package com.example.rfagendamentos.config;

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
            if (usuarioRepository.findByEmail("adminrf@gmail.com").isPresent()) {
                return;
            }
            Usuario proprietaria = new Usuario();
            proprietaria.setNome("Rosangela Fernandes");
            proprietaria.setEmail("adminrf@gmail.com");
            proprietaria.setSenha(passwordEncoder.encode("186519"));
            proprietaria.setTelefone("(43) 99149-4105");
            proprietaria.setCpf("00000000000");
            proprietaria.setEndereco("Rua Minas Gerais 141 B, Sertanópolis");
            proprietaria.setTipoUsuario(TipoUsuario.PROPRIETARIA);
            proprietaria.setAtivo(true);

            usuarioRepository.save(proprietaria);

            System.out.println(">>> Proprietária criada com sucesso!");
        };
    }
}
