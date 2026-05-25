package br.com.fatecads.fatecads.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final long MINUTOS_EXPIRACAO_TOKEN = 30;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario save(Usuario usuario) {
        usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Optional<Usuario> findByLoginAndMatchingEmail(String loginUsuario, String emailUsuario) {
        if (loginUsuario == null || emailUsuario == null) {
            return Optional.empty();
        }

        String emailInformado = emailUsuario.trim();
        return usuarioRepository.findByLoginUsuario(loginUsuario.trim())
                .filter(usuario -> usuario.getEmailUsuario().equalsIgnoreCase(emailInformado));
    }

    public String createPasswordResetToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        usuario.setResetTokenSenha(token);
        usuario.setResetTokenExpiracao(LocalDateTime.now().plusMinutes(MINUTOS_EXPIRACAO_TOKEN));
        usuarioRepository.save(usuario);
        return token;
    }

    public Optional<Usuario> findByValidResetToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usuarioRepository.findByResetTokenSenha(token)
                .filter(usuario -> usuario.getResetTokenExpiracao() != null)
                .filter(usuario -> usuario.getResetTokenExpiracao().isAfter(LocalDateTime.now()));
    }

    public void updatePassword(Integer idUsuario, String novaSenha) {
        Usuario usuario = findById(idUsuario);
        if (usuario != null) {
            usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
            usuario.setResetTokenSenha(null);
            usuario.setResetTokenExpiracao(null);
            usuarioRepository.save(usuario);
        }
    }

    public boolean updatePasswordByResetToken(String token, String novaSenha) {
        Optional<Usuario> usuarioEncontrado = findByValidResetToken(token);
        if (usuarioEncontrado.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioEncontrado.get();
        usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
        usuario.setResetTokenSenha(null);
        usuario.setResetTokenExpiracao(null);
        usuarioRepository.save(usuario);
        return true;
    }
}
