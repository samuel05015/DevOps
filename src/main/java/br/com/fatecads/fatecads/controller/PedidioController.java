package br.com.fatecads.fatecads.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidioController {
    
    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/salvar")
    public ResponseEntity<Void> salvarPedido(@RequestBody Pedido pedido){
        pedidoService.salvarPedido(pedido);
        return ResponseEntity.ok().build();
    }

    //Método para abrir a tela de cadastro de pedidos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("pedido", new Pedido());
        adicionarOpcoesFormulario(model);
        return "pedido/formularioPedidio";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            return "redirect:/pedidos/listar";
        }
        model.addAttribute("pedido", pedido);
        adicionarOpcoesFormulario(model);
        return "pedido/formularioPedidio";
    }

    private void adicionarOpcoesFormulario(Model model) {
        //Alunos
        List<Aluno> alunos = alunoService.findAll();
        model.addAttribute("alunos", alunos);
        
        //Produtos
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        return "pedido/listarPedidos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        pedidoService.deleteById(id);
        return "redirect:/pedidos/listar";
    }
}
