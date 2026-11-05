package br.com.fatecads.fatecads.cart;

import br.com.fatecads.fatecads.entity.Produto;

public class ItemCarrinho {

    private final Integer idProduto;
    private final String descricao;
    private final String marca;
    private final String unidade;
    private final Double preco;
    private final boolean temImagem;
    private int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        this.idProduto = produto.getIdProduto();
        this.descricao = produto.getDescricaoProduto();
        this.marca = produto.getMarcaProduto();
        this.unidade = produto.getUnidadeProduto();
        this.preco = produto.getValorProduto() == null ? 0.0 : produto.getValorProduto();
        this.temImagem = produto.getImagemProduto() != null && produto.getImagemProduto().length > 0;
        this.quantidade = quantidade;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMarca() {
        return marca;
    }

    public String getUnidade() {
        return unidade;
    }

    public Double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public boolean isTemImagem() {
        return temImagem;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return preco * quantidade;
    }
}
