package br.com.fatecads.fatecads.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String emailRemetente;

    public void enviarEmailRecuperacaoSenha(Usuario usuario, String linkRecuperacao) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        if (emailRemetente != null && !emailRemetente.isBlank()) {
            mensagem.setFrom(emailRemetente);
        }

        mensagem.setTo(usuario.getEmailUsuario());
        mensagem.setSubject("Recuperacao de senha - Fatec ADS");
        mensagem.setText("""
                Ola, %s.

                Recebemos uma solicitacao para trocar a senha da sua conta no sistema Fatec ADS.

                Clique no link abaixo para criar uma nova senha:
                %s

                Esse link expira em 30 minutos.
                Se voce nao solicitou a troca de senha, ignore este email.
                """.formatted(usuario.getNomeUsuario(), linkRecuperacao));

        javaMailSender.send(mensagem);
    }
}
