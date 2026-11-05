package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fatecads.fatecads.cart.Carrinho;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.ProdutoService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/loja")
public class LojaController {

    private static final String CARRINHO_SESSION = "carrinho";

    @Autowired
    private ProdutoService produtoService;

    @ModelAttribute("quantidadeCarrinho")
    public int quantidadeCarrinho(HttpSession session) {
        return obterCarrinho(session).getQuantidadeTotal();
    }

    @GetMapping({"", "/"})
    public String catalogo(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "loja/catalogo";
    }

    @GetMapping("/carrinho")
    public String carrinho(Model model, HttpSession session) {
        model.addAttribute("carrinho", obterCarrinho(session));
        return "loja/carrinho";
    }

    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionar(@PathVariable Integer id,
            @RequestParam(defaultValue = "1") int quantidade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Produto produto = produtoService.findById(id);
        if (produto == null) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/loja";
        }
        obterCarrinho(session).adicionar(produto, Math.max(1, quantidade));
        redirectAttributes.addFlashAttribute("mensagem", "Produto adicionado ao carrinho.");
        return "redirect:/loja";
    }

    @PostMapping("/carrinho/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @RequestParam int quantidade, HttpSession session) {
        obterCarrinho(session).atualizar(id, quantidade);
        return "redirect:/loja/carrinho";
    }

    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Integer id, HttpSession session) {
        obterCarrinho(session).remover(id);
        return "redirect:/loja/carrinho";
    }

    @PostMapping("/carrinho/limpar")
    public String limpar(HttpSession session) {
        obterCarrinho(session).limpar();
        return "redirect:/loja/carrinho";
    }

    @PostMapping("/carrinho/finalizar")
    public String finalizar(HttpSession session, RedirectAttributes redirectAttributes) {
        Carrinho carrinho = obterCarrinho(session);
        if (carrinho.isVazio()) {
            redirectAttributes.addFlashAttribute("erro", "Adicione um produto antes de finalizar.");
            return "redirect:/loja/carrinho";
        }
        carrinho.limpar();
        redirectAttributes.addFlashAttribute("mensagem",
                "Pedido simulado com sucesso. Nenhum pagamento foi realizado.");
        return "redirect:/loja/carrinho";
    }

    private Carrinho obterCarrinho(HttpSession session) {
        Object carrinhoAtual = session.getAttribute(CARRINHO_SESSION);
        if (carrinhoAtual instanceof Carrinho carrinho) {
            return carrinho;
        }
        Carrinho carrinho = new Carrinho();
        session.setAttribute(CARRINHO_SESSION, carrinho);
        return carrinho;
    }
}
