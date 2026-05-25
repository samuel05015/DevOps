package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.service.EmailService;
import br.com.fatecads.fatecads.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/esqueci-senha")
    public String esqueciSenha() {
        return "esqueciSenha";
    }

    @PostMapping("/esqueci-senha")
    public String enviarEmailRecuperacao(@RequestParam String loginUsuario, @RequestParam String emailUsuario,
            RedirectAttributes redirectAttributes) {
        return usuarioService.findByLoginAndMatchingEmail(loginUsuario, emailUsuario)
                .map(usuario -> enviarLinkRecuperacao(usuario, redirectAttributes))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Login e email nao conferem com nenhum usuario cadastrado.");
                    return "redirect:/esqueci-senha";
                });
    }

    @GetMapping("/recuperar-senha")
    public String recuperarSenha(@RequestParam(required = false) String token, Model model,
            RedirectAttributes redirectAttributes) {
        return usuarioService.findByValidResetToken(token)
                .map(usuario -> {
                    model.addAttribute("loginUsuario", usuario.getLoginUsuario());
                    model.addAttribute("token", token);
                    return "recuperarSenha";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Link de recuperacao invalido ou expirado.");
                    return "redirect:/esqueci-senha";
                });
    }

    @PostMapping("/recuperar-senha")
    public String trocarSenha(@RequestParam String token, @RequestParam String novaSenha,
            @RequestParam String confirmarSenha, RedirectAttributes redirectAttributes) {
        if (usuarioService.findByValidResetToken(token).isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Link de recuperacao invalido ou expirado.");
            return "redirect:/esqueci-senha";
        }

        if (novaSenha == null || novaSenha.isBlank()) {
            redirectAttributes.addFlashAttribute("erro", "Informe a nova senha.");
            return "redirect:/recuperar-senha?token=" + token;
        }

        if (!novaSenha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("erro", "As senhas informadas nao sao iguais.");
            return "redirect:/recuperar-senha?token=" + token;
        }

        usuarioService.updatePasswordByResetToken(token, novaSenha);
        return "redirect:/login?senhaAlterada";
    }

    private String enviarLinkRecuperacao(Usuario usuario, RedirectAttributes redirectAttributes) {
        String token = usuarioService.createPasswordResetToken(usuario);
        String linkRecuperacao = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/recuperar-senha")
                .queryParam("token", token)
                .toUriString();

        try {
            emailService.enviarEmailRecuperacaoSenha(usuario, linkRecuperacao);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Enviamos um link de recuperacao para o email cadastrado.");
            return "redirect:/esqueci-senha";
        } catch (MailException exception) {
            log.error("Erro ao enviar email de recuperacao para {}", usuario.getEmailUsuario(), exception);
            redirectAttributes.addFlashAttribute("erro",
                    "Nao foi possivel enviar o email. Confira o erro no console do sistema.");
            return "redirect:/esqueci-senha";
        }
    }
    
}
