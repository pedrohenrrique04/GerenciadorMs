package Dao;

import Model.Venda;
import java.util.Optional;

/**
 * DAO mockado (Data Access Object) para a entidade Venda.
 * Nesta versão, apenas simula o registro de vendas, sem conexão real com banco de dados.
 */
public class VendaDAO {

    // Simulação de ID sequencial para novas vendas
    private static int nextId = 1000;

    /**
     * Simula o registro de uma nova venda no banco de dados.
     * Atribui um ID sequencial à venda.
     * @param venda O objeto Venda a ser registrado.
     * @return true se o registro foi bem-sucedido (sempre true no mock).
     */
    public boolean registrarVenda(Venda venda) {
        // Simula a inserção e atribuição de ID
        venda.setId(nextId++);
        System.out.println("✅ Venda registrada no DB (MOCK) com sucesso! ID: " + venda.getId());
        // Em um DAO real, aqui ocorreria a lógica JDBC (INSERT INTO VENDAS...)
        return true;
    }

    /**
     * Simula a busca de uma Venda pelo ID.
     * @param id O ID da venda a ser buscada.
     * @return Um Optional vazio (Mock).
     */
    public Optional<Venda> buscarPorId(int id) {
        // Mock: Implemente a busca real aqui, se necessário.
        return Optional.empty();
    }
}