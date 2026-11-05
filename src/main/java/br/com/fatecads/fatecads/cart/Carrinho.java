package br.com.fatecads.fatecads.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.fatecads.fatecads.entity.Produto;

public class Carrinho {

    private final Map<Integer, ItemCarrinho> itens = new LinkedHashMap<>();

    public void adicionar(Produto produto, int quantidade) {
        ItemCarrinho item = itens.get(produto.getIdProduto());
        if (item == null) {
            itens.put(produto.getIdProduto(), new ItemCarrinho(produto, quantidade));
            return;
        }
        item.setQuantidade(item.getQuantidade() + quantidade);
    }

    public void atualizar(Integer idProduto, int quantidade) {
        if (quantidade <= 0) {
            remover(idProduto);
            return;
        }
        ItemCarrinho item = itens.get(idProduto);
        if (item != null) {
            item.setQuantidade(quantidade);
        }
    }

    public void remover(Integer idProduto) {
        itens.remove(idProduto);
    }

    public void limpar() {
        itens.clear();
    }

    public List<ItemCarrinho> getItens() {
        return new ArrayList<>(itens.values());
    }

    public int getQuantidadeTotal() {
        return itens.values().stream().mapToInt(ItemCarrinho::getQuantidade).sum();
    }

    public double getTotal() {
        return itens.values().stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    public boolean isVazio() {
        return itens.isEmpty();
    }
}
