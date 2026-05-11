package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.save(usuario);
        return "redirect:/usuarios/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/listarUsuarios";
    }

    @GetMapping("/criar")
    public String criar(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/formularioUsuario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("usuario", usuarioService.findById(id));
        return "usuario/formularioUsuario";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios/listar";
    }
}
