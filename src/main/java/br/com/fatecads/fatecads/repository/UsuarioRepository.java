package br.com.fatecads.fatecads.repository;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fatecads.fatecads.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    Optional<Usuario> findByLoginUsuario(String loginUsuario);
}
