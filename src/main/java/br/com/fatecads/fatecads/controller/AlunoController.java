package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.CursoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/alunos")
public class AlunoController {
    //injeção de dependêencia da service de alunos
    @Autowired
    private AlunoService alunoService;

    //injeção de dependêcia da service de cursos
    @Autowired
    private CursoService cursoService;


    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno){
        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }


    @GetMapping("/listar")
    public String listar(Model model){
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/listarAlunos";
    }

    // Método para criar um novo aluno e abrir o formulário

    @GetMapping("/criar")
    public String criar(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("cursos", cursoService.findAll());
        return "aluno/formularioAluno";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        alunoService.deleteById(id);
        return "redirect:/alunos/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Aluno aluno = alunoService.findById(id);
        model.addAttribute("aluno", aluno);
        model.addAttribute("cursos", cursoService.findAll());
        return "aluno/formularioAluno";    
            
        
    }


}
