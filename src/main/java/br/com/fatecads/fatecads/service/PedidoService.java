package br.com.fatecads.fatecads.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.repository.AlunoRepository;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    //Método para salvar um pedido
    @Transactional
    public Pedido salvarPedido(Pedido dadosPedido) {
        if (dadosPedido.getAluno() == null || dadosPedido.getAluno().getIdAluno() == null) {
            throw new IllegalArgumentException("Selecione um aluno para o pedido.");
        }
        if (dadosPedido.getItens() == null || dadosPedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um produto ao pedido.");
        }

        Aluno aluno = alunoRepository.findById(dadosPedido.getAluno().getIdAluno())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        Pedido pedido;
        if (dadosPedido.getIdPedido() == null) {
            pedido = new Pedido();
            pedido.setDataPedido(LocalDate.now());
            pedido.setItens(new ArrayList<>());
        } else {
            pedido = pedidoRepository.findById(dadosPedido.getIdPedido())
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
            pedido.getItens().clear();
            if (pedido.getDataPedido() == null) {
                pedido.setDataPedido(LocalDate.now());
            }
        }

        pedido.setAluno(aluno);
        for (ItemDoPedido dadosItem : dadosPedido.getItens()) {
            if (dadosItem.getProduto() == null || dadosItem.getProduto().getIdProduto() == null
                    || dadosItem.getQuantidade() == null || dadosItem.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Item do pedido inválido.");
            }

            Produto produto = produtoRepository.findById(dadosItem.getProduto().getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

            ItemDoPedido item = new ItemDoPedido();
            item.setProduto(produto);
            item.setQuantidade(dadosItem.getQuantidade());
            item.setPreco(produto.getValorProduto());
            item.atualizarSubtotal();
            item.setPedido(pedido);
            pedido.getItens().add(item);
        }
        pedido.atualizarTotal();
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Pedido findById(Integer id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        pedidoRepository.deleteById(id);
    }
}
