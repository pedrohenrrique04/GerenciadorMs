package Model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo de dados para representar uma transação de Venda finalizada.
 * Depende da classe CartItem para representar os itens da venda.
 */
public class Venda {
    private int id;
    private LocalDateTime dataVenda;
    private double valorTotal;
    private List<CartItem> itens;
    private String status;

    /**
     * Construtor para registrar uma nova venda.
     * @param itens Lista de itens (CartItem) vendidos.
     * @param valorTotal Valor total final da transação.
     */
    public Venda(List<CartItem> itens, double valorTotal) {
        this.id = 0; // ID será definido pelo DAO ao salvar
        this.dataVenda = LocalDateTime.now();
        this.valorTotal = valorTotal;
        this.itens = itens;
        this.status = "FINALIZADA";
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDataVenda() { return dataVenda; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public List<CartItem> getItens() { return itens; }
    public void setItens(List<CartItem> itens) { this.itens = itens; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}