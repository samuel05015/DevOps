package br.com.fatecads.fatecads.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.fatecads.fatecads.entity.Produto;

class CarrinhoTests {

    @Test
    void deveSomarQuantidadesECalcularTotal() {
        Carrinho carrinho = new Carrinho();
        Produto produto = produto(1, 12.50);

        carrinho.adicionar(produto, 2);
        carrinho.adicionar(produto, 1);

        assertEquals(3, carrinho.getQuantidadeTotal());
        assertEquals(37.50, carrinho.getTotal(), 0.001);
    }

    @Test
    void quantidadeZeroDeveRemoverItem() {
        Carrinho carrinho = new Carrinho();
        Produto produto = produto(1, 10.0);
        carrinho.adicionar(produto, 1);

        carrinho.atualizar(1, 0);

        assertTrue(carrinho.isVazio());
    }

    private Produto produto(Integer id, Double preco) {
        Produto produto = new Produto();
        produto.setIdProduto(id);
        produto.setDescricaoProduto("Produto teste");
        produto.setMarcaProduto("Marca");
        produto.setUnidadeProduto("UN");
        produto.setValorProduto(preco);
        return produto;
    }
}
