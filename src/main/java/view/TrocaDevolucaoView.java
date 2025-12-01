package view;

import controller.TrocaDevolucaoController;
import Model.TrocaDevolucao;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Tela principal para gerenciar solicitações de Troca e Devolução.
 */
public class TrocaDevolucaoView extends JFrame {

    private final TrocaDevolucaoController controller;
    private JTable tabelaTrocaDevolucao;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbTipoBusca;
    private JTextField txtBusca;

    public TrocaDevolucaoView() {
        super("Gerenciamento de Trocas e Devoluções");
        this.controller = new TrocaDevolucaoController(this);
        initComponents();
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Não carregar a tabela aqui, pois o Controller já faz isso no construtor.
    }

    private void initComponents() {
        // --- Painel Superior (Busca) ---
        JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cmbTipoBusca = new JComboBox<>(new String[]{"ID", "Pedido", "Tipo"});
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");

        btnBuscar.addActionListener(e -> controller.buscarTrocaDevolucao(
                cmbTipoBusca.getSelectedItem().toString(), txtBusca.getText())
        );

        panelBusca.add(cmbTipoBusca);
        panelBusca.add(txtBusca);
        panelBusca.add(btnBuscar);

        // --- Tabela ---
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "ID Produto", "Pedido", "Tipo", "Status", "Data Solic.", "Quant.", "Valor"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabelaTrocaDevolucao = new JTable(tableModel);
        tabelaTrocaDevolucao.setPreferredScrollableViewportSize(new Dimension(500, 300));
        JScrollPane scrollPane = new JScrollPane(tabelaTrocaDevolucao);

        // --- Painel Inferior (Botões de Ação) ---
        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnNovo = new JButton("Nova Solicitação");
        JButton btnEditar = new JButton("Editar");
        JButton btnProcessar = new JButton("Processar");
        JButton btnDeletar = new JButton("Deletar");

        btnNovo.addActionListener(e -> controller.abrirCadastro(null));
        btnEditar.addActionListener(this::abrirEdicao);
        btnProcessar.addActionListener(this::abrirProcessamento);
        btnDeletar.addActionListener(this::deletarSolicitacao);

        panelAcoes.add(btnNovo);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnProcessar);
        panelAcoes.add(btnDeletar);

        // --- Layout Principal ---
        setLayout(new BorderLayout());
        add(panelBusca, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelAcoes, BorderLayout.SOUTH);
    }

    // Método chamado pelo Controller para preencher a tabela
    public void carregarTabela(List<TrocaDevolucao> lista) {
        tableModel.setRowCount(0); // Limpa a tabela
        for (TrocaDevolucao td : lista) {
            tableModel.addRow(new Object[]{
                    td.getId(),
                    td.getIdProduto(),
                    td.getNumeroPedido(),
                    td.getTipo(),
                    td.getStatus(),
                    td.getDataSolicitacao().toString(),
                    td.getQuantidade(),
                    String.format("R$ %.2f", td.getPrecoVenda())
            });
        }
    }

    // Método auxiliar para obter o ID da linha selecionada
    private String getSelectedId() {
        int row = tabelaTrocaDevolucao.getSelectedRow();
        if (row >= 0) {
            return tableModel.getValueAt(row, 0).toString();
        }
        return null;
    }

    private void abrirEdicao(ActionEvent e) {
        String idStr = getSelectedId();
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            controller.buscarPorId(id).ifPresentOrElse(
                    td -> controller.abrirCadastro(td),
                    () -> JOptionPane.showMessageDialog(this, "Erro: Solicitação não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE)
            );
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void abrirProcessamento(ActionEvent e) {
        String idStr = getSelectedId();
        if (idStr != null) {
            controller.abrirProcessamento(idStr);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação para processar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deletarSolicitacao(ActionEvent e) {
        String idStr = getSelectedId();
        if (idStr != null) {
            controller.deletarTrocaDevolucao(idStr);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma solicitação para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método Main para teste rápido
    public static void main(String[] args) {
        // Garanta que a conexão com o banco de dados esteja configurada antes de rodar
        // Dao.Conexao.configurar...
        new TrocaDevolucaoView().setVisible(true);
    }
}