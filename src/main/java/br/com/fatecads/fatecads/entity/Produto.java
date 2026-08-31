package br.com.fatecads.fatecads.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProduto;

    @Column(nullable = false, length = 40)
    private String descricaoProduto;

    private Double valorProduto;

    @Column(nullable = false, length = 10)
    private String unidadeProduto;

    @Column(nullable = false, length = 30)
    private String marcaProduto; 

    @JdbcTypeCode(SqlTypes.LONG32VARBINARY)
    @Column(name = "imagem_produto", columnDefinition = "bytea")
    private byte[] imagemProduto;

    @Column(length = 100)
    private String tipoImagemProduto;

    @OneToMany(mappedBy = "produto")
    private List<ItemDoPedido> itens;
    
}
