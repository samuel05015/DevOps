package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemDoPedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idItem;

    private Integer quantidade;

    private Double preco;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "id_Pedido_fk")
    private Pedido pedido;
    
    @ManyToOne
    @JoinColumn(name = "id_Produto_fk")
    private Produto produto;

    //Método para calcular o subtotal do item do pedido
    public Double calcularSubtotal() {
        return quantidade * preco;
    }


    //Método para atualizar subtotal do item do pedido
    public void atualizarSubtotal() {
        this.subtotal = calcularSubtotal();
    }

}
