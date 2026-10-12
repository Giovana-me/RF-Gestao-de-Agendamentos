package com.example.rfagendamentos.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigoAlteracaoEmail(
            String emailDestino,
            String codigo) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(emailDestino);
        mensagem.setSubject("Confirmação de alteração de e-mail");

        mensagem.setText(
                "Olá!\n\n" +
                        "Recebemos uma solicitação para alterar o e-mail " +
                        "da sua conta no sistema RF Agendamentos.\n\n" +
                        "Seu código de confirmação é:\n\n" +
                        codigo +
                        "\n\n" +
                        "Esse código é válido por 15 minutos.\n\n" +
                        "Se você não solicitou essa alteração, " +
                        "ignore este e-mail.\n\n" +
                        "Atenciosamente,\n" +
                        "RF Estética."
        );

        mailSender.send(mensagem);
    }

    public void enviarCodigoAlteracaoSenha(
            String destinatario,
            String codigo
    ) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(destinatario);
        mensagem.setSubject("Confirmação de alteração de senha");

        mensagem.setText(
                "Olá!\n\n" +
                        "Recebemos uma solicitação para alterar a senha da sua conta.\n\n" +
                        "Seu código de confirmação é:\n\n" +
                        codigo +
                        "\n\n" +
                        "Este código é válido por 15 minutos.\n\n" +
                        "Se você não solicitou a alteração da senha, ignore este e-mail.\n\n" +
                        "Atenciosamente,\n" +
                        "RF Estética."
        );

        mailSender.send(mensagem);
    }
}
