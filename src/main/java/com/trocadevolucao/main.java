package com.trocadevolucao;

import com.trocadevolucao.model.TrocaDevolucao;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class main extends Application {

    private List<TrocaDevolucao> trocasDevolucoes = new ArrayList<>();
    private List<TrocaDevolucao> solicitacoesFiltradas = new ArrayList<>();

    private FlowPane gradeSolicitacoes;
    private ScrollPane scrollGrade;

    private ToggleGroup statusGroup, tipoGroup;
    private RadioButton pendenteRadio, aprovadaRadio, processadaRadio;
    private RadioButton trocaRadio, devolucaoRadio;
    private Button btnLimparFiltros;

    private TextField campoPesquisa;
    private Button btnPesquisar;
    private Button btnLimparPesquisa;

    private static final String DATA_FILE = "trocas_devolucoes.dat";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Troca e Devolução - Loja Elegance");

        carregarSolicitacoesSalvas();

        if (trocasDevolucoes.isEmpty()) {
            carregarSolicitacoesExemplo();
        }

        // A lista de filtros inicia com todas as solicitações
        solicitacoesFiltradas.addAll(trocasDevolucoes);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        VBox topo = criarTopo();
        root.setTop(topo);

        SplitPane centro = new SplitPane();
        // Ajuste o divisor para a nova largura de 240px na barra lateral (aprox. 20%)
        centro.setDividerPositions(0.20);

        VBox barraLateral = criarBarraLateralFiltros();
        scrollGrade = criarGradeSolicitacoes();

        centro.getItems().addAll(barraLateral, scrollGrade);
        root.setCenter(centro);

        // --- CORREÇÃO DE PROPORÇÃO DA TELA (ANTES: 1400x900) ---
        Scene scene = new Scene(root, 1200, 750);
        // --------------------------------------------------------

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> salvarSolicitacoes());

        // Aplica a filtragem inicial para garantir que o contador na lateral esteja correto
        aplicarFiltros();
    }

    // --- MÉTODOS DE DADOS E ARQUIVO ---

    @SuppressWarnings("unchecked")
    private void carregarSolicitacoesSalvas() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            trocasDevolucoes = (List<TrocaDevolucao>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Ignora se o arquivo não existir (primeira execução)
        } catch (Exception e) {
            // Outros erros de leitura
            e.printStackTrace();
        }
    }

    private void salvarSolicitacoes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(trocasDevolucoes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para carregar dados de exemplo (necessário se o arquivo .dat não existir)
    private void carregarSolicitacoesExemplo() {
        // Exemplo de dados (baseado na imagem fornecida)
        trocasDevolucoes.add(new TrocaDevolucao(1, 101, "PED-2024-001", "TROCA", "Produto com defeito", LocalDate.of(2025, 11, 18), "PENDENTE", 1, 299.99));
        trocasDevolucoes.add(new TrocaDevolucao(2, 205, "PED-2024-015", "DEVOLUCAO", "Tamanho incorreto", LocalDate.of(2025, 11, 19), "APROVADA", 1, 159.99));
        trocasDevolucoes.add(new TrocaDevolucao(3, 312, "PED-2024-028", "TROCA", "Cor diferente do anunciado", LocalDate.of(2025, 11, 20), "PROCESSADA", 1, 459.99));
        trocasDevolucoes.add(new TrocaDevolucao(4, 118, "PED-2024-007", "DEVOLUCAO", "Arrependimento da compra", LocalDate.of(2025, 11, 17), "PENDENTE", 2, 159.98));
        trocasDevolucoes.add(new TrocaDevolucao(5, 276, "PED-2024-033", "TROCA", "Produto danificado", LocalDate.of(2025, 11, 18), "APROVADA", 1, 899.99));
    }


    // --- MÉTODOS DE LAYOUT (TOPO E PESQUISA) ---

    private VBox criarTopo() {
        VBox topo = new VBox(15);
        topo.setPadding(new Insets(20));
        topo.setStyle("-fx-background-color: #498090; -fx-border-color: #3a6775; -fx-border-width: 0 0 2 0;");

        HBox linhaTitulo = new HBox();
        linhaTitulo.setAlignment(Pos.CENTER_LEFT);
        linhaTitulo.setSpacing(15);

        Label titulo = new Label("TROCAS E DEVOLUÇÕES");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);

        // Atualiza a contagem total de solicitações
        Label badgeContador = new Label(trocasDevolucoes.size() + " solicitações");
        badgeContador.setFont(Font.font("System", FontWeight.BOLD, 14));
        badgeContador.setTextFill(Color.WHITE);
        badgeContador.setStyle("-fx-background-color: #D8B167; -fx-padding: 5 12; -fx-background-radius: 12;");

        Button btnNovaSolicitacao = new Button("+ Nova Solicitação");
        btnNovaSolicitacao.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        btnNovaSolicitacao.setOnAction(e -> mostrarJanelaNovaSolicitacao());

        linhaTitulo.getChildren().addAll(titulo, badgeContador, btnNovaSolicitacao);

        HBox barraPesquisa = criarBarraPesquisa();

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);

        // Contagem para os status
        long pendentes = trocasDevolucoes.stream().filter(TrocaDevolucao::isPendente).count();
        long processadas = trocasDevolucoes.stream().filter(TrocaDevolucao::isProcessada).count();

        // O label "5 solicitações" na imagem representa o total. O texto "2 pendentes, 1 processada" foi adaptado
        // para refletir a contagem exata dos dados de exemplo.
        Label totalSolicitacoes = new Label(trocasDevolucoes.size() + " solicitações");
        Label solicitacoesPendentes = new Label(pendentes + " pendentes");
        Label solicitacoesProcessadas = new Label(processadas + " processadas");
        // Aprovadas (2) está omitida na visualização do topo da imagem, mas pendentes(2) e processadas(1) estão lá.

        for (Label stat : new Label[]{totalSolicitacoes, solicitacoesPendentes, solicitacoesProcessadas}) {
            stat.setFont(Font.font("System", 12));
            stat.setTextFill(Color.WHITE);
        }

        stats.getChildren().addAll(totalSolicitacoes, solicitacoesPendentes, solicitacoesProcessadas);

        topo.getChildren().addAll(linhaTitulo, barraPesquisa, stats);
        return topo;
    }

    private HBox criarBarraPesquisa() {
        HBox barraPesquisa = new HBox(10);
        barraPesquisa.setAlignment(Pos.CENTER_LEFT);
        barraPesquisa.setPadding(new Insets(10, 0, 0, 0));

        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("🔍 Pesquisar por número do pedido, motivo...");
        campoPesquisa.setPrefWidth(400);
        campoPesquisa.setPrefHeight(40);
        campoPesquisa.setStyle("-fx-font-size: 14; -fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: white; -fx-border-color: #D8B167;");

        btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14;");
        btnPesquisar.setPrefHeight(40);
        btnPesquisar.setPrefWidth(100);
        btnPesquisar.setOnAction(e -> aplicarPesquisa());

        btnLimparPesquisa = new Button("Limpar");
        btnLimparPesquisa.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 14;");
        btnLimparPesquisa.setPrefHeight(40);
        btnLimparPesquisa.setPrefWidth(80);
        btnLimparPesquisa.setOnAction(e -> limparPesquisa());

        campoPesquisa.setOnAction(e -> aplicarPesquisa());

        barraPesquisa.getChildren().addAll(campoPesquisa, btnPesquisar, btnLimparPesquisa);
        return barraPesquisa;
    }


    // --- MÉTODOS DE LAYOUT (FILTROS) ---

    private VBox criarBarraLateralFiltros() {
        VBox barraLateral = new VBox(20);
        barraLateral.setPadding(new Insets(25));
        barraLateral.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0; -fx-border-color: #498090;");

        // --- CORREÇÃO DE PROPORÇÃO DA BARRA LATERAL (ANTES: 280) ---
        barraLateral.setPrefWidth(240);
        // -------------------------------------------------------------

        Label tituloFiltros = new Label("FILTROS");
        tituloFiltros.setFont(Font.font("System", FontWeight.BOLD, 20));
        tituloFiltros.setTextFill(Color.web("#498090"));

        Label contadorResultados = new Label(solicitacoesFiltradas.size() + " solicitações encontradas");
        contadorResultados.setFont(Font.font("System", FontWeight.BOLD, 12));
        contadorResultados.setTextFill(Color.web("#498090"));

        VBox tipoBox = criarSecaoFiltroRadio("TIPO", List.of("Troca", "Devolução"));
        trocaRadio = (RadioButton) ((VBox)tipoBox.getChildren().get(1)).getChildren().get(0);
        devolucaoRadio = (RadioButton) ((VBox)tipoBox.getChildren().get(1)).getChildren().get(1);

        VBox statusBox = criarSecaoFiltroRadio("STATUS", List.of("Pendente", "Aprovada", "Processada"));
        pendenteRadio = (RadioButton) ((VBox)statusBox.getChildren().get(1)).getChildren().get(0);
        aprovadaRadio = (RadioButton) ((VBox)statusBox.getChildren().get(1)).getChildren().get(1);
        processadaRadio = (RadioButton) ((VBox)statusBox.getChildren().get(1)).getChildren().get(2);

        btnLimparFiltros = new Button("🧹 Limpar Todos os Filtros");
        btnLimparFiltros.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        btnLimparFiltros.setMaxWidth(Double.MAX_VALUE);
        btnLimparFiltros.setPrefHeight(40);
        btnLimparFiltros.setOnAction(e -> limparTodosFiltros());

        configurarEventosFiltros();

        barraLateral.getChildren().addAll(
                tituloFiltros,
                contadorResultados,
                new Separator(),
                tipoBox,
                new Separator(),
                statusBox,
                new Separator(),
                btnLimparFiltros
        );

        return barraLateral;
    }

    private VBox criarSecaoFiltroRadio(String titulo, List<String> opcoes) {
        VBox secao = new VBox(15);

        Label labelTitulo = new Label(titulo);
        labelTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        labelTitulo.setTextFill(Color.web("#498090"));

        VBox opcoesBox = new VBox(8);

        ToggleGroup toggleGroup = new ToggleGroup();

        for (String opcao : opcoes) {
            RadioButton radioButton = new RadioButton(opcao);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setStyle("-fx-text-fill: #2c3e50;");
            radioButton.setToggleGroup(toggleGroup);
            opcoesBox.getChildren().add(radioButton);
        }

        if (titulo.equals("TIPO")) {
            tipoGroup = toggleGroup;
        } else if (titulo.equals("STATUS")) {
            statusGroup = toggleGroup;
        }

        secao.getChildren().addAll(labelTitulo, opcoesBox);
        return secao;
    }

    private void configurarEventosFiltros() {
        tipoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        statusGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    // --- MÉTODOS DE LAYOUT (GRADE E CARDS) ---

    private ScrollPane criarGradeSolicitacoes() {
        gradeSolicitacoes = new FlowPane();
        gradeSolicitacoes.setPadding(new Insets(25));
        gradeSolicitacoes.setHgap(20);
        gradeSolicitacoes.setVgap(20);
        gradeSolicitacoes.setStyle("-fx-background-color: #f5f7fa;");

        // A grade será atualizada após o applyFilters() no start()
        // atualizarGradeSolicitacoes();

        ScrollPane scroll = new ScrollPane(gradeSolicitacoes);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f5f7fa; -fx-border-color: transparent;");
        return scroll;
    }

    private void atualizarGradeSolicitacoes() {
        gradeSolicitacoes.getChildren().clear();

        if (solicitacoesFiltradas.isEmpty()) {
            VBox mensagemVazia = new VBox(15);
            mensagemVazia.setAlignment(Pos.CENTER);
            mensagemVazia.setPadding(new Insets(50));

            Label icone = new Label("🔍");
            icone.setFont(Font.font("System", 48));

            Label texto = new Label("Nenhuma solicitação encontrada");
            texto.setFont(Font.font("System", FontWeight.BOLD, 18));
            texto.setTextFill(Color.web("#498090"));

            Label subtexto = new Label("Tente ajustar os filtros ou termos da pesquisa");
            subtexto.setFont(Font.font("System", 14));
            subtexto.setTextFill(Color.web("#7f8c8d"));

            Button btnLimparTudo = new Button("Limpar Todos os Filtros");
            btnLimparTudo.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-weight: bold;");
            btnLimparTudo.setOnAction(e -> limparTodosFiltros());

            mensagemVazia.getChildren().addAll(icone, texto, subtexto, btnLimparTudo);
            gradeSolicitacoes.getChildren().add(mensagemVazia);
        } else {
            for (TrocaDevolucao solicitacao : solicitacoesFiltradas) {
                VBox cardSolicitacao = criarCardSolicitacao(solicitacao);
                gradeSolicitacoes.getChildren().add(cardSolicitacao);
            }
        }
    }

    private VBox criarCardSolicitacao(TrocaDevolucao solicitacao) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(300);
        card.setMaxWidth(300);
        card.setAlignment(Pos.TOP_CENTER);

        card.setOnMouseClicked(e -> mostrarJanelaDetalhesSolicitacao(solicitacao));

        HBox badgeContainer = new HBox(10);
        badgeContainer.setAlignment(Pos.TOP_LEFT);
        badgeContainer.setMaxWidth(Double.MAX_VALUE);

        Label badgeTipo = new Label(solicitacao.isTroca() ? "TROCA" : "DEVOLUÇÃO");
        badgeTipo.setFont(Font.font("System", FontWeight.BOLD, 10));
        badgeTipo.setTextFill(Color.WHITE);
        badgeTipo.setStyle("-fx-background-color: " + (solicitacao.isTroca() ? "#498090" : "#D8B167") + "; -fx-padding: 4 8; -fx-background-radius: 10;");

        Label badgeStatus = new Label(solicitacao.getStatus());
        badgeStatus.setFont(Font.font("System", FontWeight.BOLD, 10));
        badgeStatus.setTextFill(Color.WHITE);
        String corStatus = getCorStatus(solicitacao.getStatus());
        badgeStatus.setStyle("-fx-background-color: " + corStatus + "; -fx-padding: 4 8; -fx-background-radius: 10;");

        badgeContainer.getChildren().addAll(badgeTipo, badgeStatus);

        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMaxWidth(260);

        Label pedidoLabel = new Label("Pedido: " + solicitacao.getNumeroPedido());
        pedidoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        pedidoLabel.setTextFill(Color.web("#2c3e50"));

        Label produtoLabel = new Label("Produto ID: " + solicitacao.getProdutoId());
        produtoLabel.setFont(Font.font("System", 12));
        produtoLabel.setTextFill(Color.web("#7f8c8d"));

        Label motivoLabel = new Label(solicitacao.getMotivo());
        motivoLabel.setFont(Font.font("System", 12));
        motivoLabel.setWrapText(true);
        motivoLabel.setMaxWidth(260);
        motivoLabel.setTextFill(Color.web("#2c3e50"));
        motivoLabel.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8; -fx-background-radius: 8;");

        HBox detalhes = new HBox(15);
        detalhes.setAlignment(Pos.CENTER_LEFT);

        Label quantidadeLabel = new Label("Qtd: " + solicitacao.getQuantidade());
        Label valorLabel = new Label("R$ " + String.format("%.2f", solicitacao.getValorTotal()));

        for (Label det : new Label[]{quantidadeLabel, valorLabel}) {
            det.setFont(Font.font("System", 11));
            det.setTextFill(Color.web("#7f8c8d"));
            det.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 2 6; -fx-background-radius: 8;");
        }

        detalhes.getChildren().addAll(quantidadeLabel, valorLabel);

        Label dataLabel = new Label("Solicitado: " + solicitacao.getDataSolicitacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dataLabel.setFont(Font.font("System", 10));
        dataLabel.setTextFill(Color.web("#7f8c8d"));

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);

        Button btnDetalhes = new Button("Detalhes");
        btnDetalhes.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold;");
        btnDetalhes.setPrefWidth(100);
        btnDetalhes.setOnAction(e -> mostrarJanelaDetalhesSolicitacao(solicitacao));

        Button btnProcessar = new Button(solicitacao.isPendente() ? "Processar" : "Ver");
        btnProcessar.setStyle("-fx-background-color: " + (solicitacao.isPendente() ? "#D8B167" : "#6c757d") + "; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold;");
        btnProcessar.setPrefWidth(80);
        btnProcessar.setOnAction(e -> processarSolicitacao(solicitacao));

        botoes.getChildren().addAll(btnDetalhes, btnProcessar);

        infoBox.getChildren().addAll(pedidoLabel, produtoLabel, motivoLabel, detalhes, dataLabel, botoes);
        card.getChildren().addAll(badgeContainer, infoBox);

        return card;
    }

    private String getCorStatus(String status) {
        switch (status.toUpperCase()) {
            case "PENDENTE": return "#ffc107";
            case "APROVADA": return "#28a745";
            case "PROCESSADA": return "#007bff";
            case "REJEITADA": return "#dc3545";
            default: return "#6c757d";
        }
    }

    // --- MÉTODOS DE FILTRAGEM E PESQUISA ---

    private void aplicarPesquisa() {
        // Filtra a lista principal de acordo com o termo de pesquisa
        String termo = campoPesquisa.getText().toLowerCase();

        // Se a pesquisa estiver vazia, a lista filtrada é igual à lista principal
        if (termo.isEmpty()) {
            solicitacoesFiltradas.clear();
            solicitacoesFiltradas.addAll(trocasDevolucoes);
        } else {
            solicitacoesFiltradas = trocasDevolucoes.stream()
                    .filter(s -> s.getNumeroPedido().toLowerCase().contains(termo) ||
                            s.getMotivo().toLowerCase().contains(termo) ||
                            String.valueOf(s.getProdutoId()).contains(termo))
                    .collect(Collectors.toList());
        }
        // Aplica os filtros de Rádio-Button sobre o resultado da pesquisa
        aplicarFiltros(true);
    }

    private void limparPesquisa() {
        campoPesquisa.clear();
        aplicarFiltros(true); // Mantém os filtros de rádio-button
    }

    private void limparTodosFiltros() {
        if (tipoGroup.getSelectedToggle() != null) {
            tipoGroup.getSelectedToggle().setSelected(false);
        }
        if (statusGroup.getSelectedToggle() != null) {
            statusGroup.getSelectedToggle().setSelected(false);
        }
        campoPesquisa.clear();
        aplicarFiltros(false); // Limpa tudo
    }

    // Método principal de filtragem
    private void aplicarFiltros() {
        aplicarFiltros(false); // Por padrão, aplica filtros sem recalcular a lista base
    }

    private void aplicarFiltros(boolean recalcularListaBase) {

        // 1. Recalcula a lista base se vier de uma ação de Pesquisa/LimparPesquisa
        if (recalcularListaBase) {
            String termoPesquisa = campoPesquisa.getText().toLowerCase();
            solicitacoesFiltradas = trocasDevolucoes.stream()
                    .filter(s -> s.getNumeroPedido().toLowerCase().contains(termoPesquisa) ||
                            s.getMotivo().toLowerCase().contains(termoPesquisa) ||
                            String.valueOf(s.getProdutoId()).contains(termoPesquisa))
                    .collect(Collectors.toList());
        } else if (campoPesquisa.getText().isEmpty()) {
            // Se não for para recalcular e a pesquisa estiver vazia, usa a lista principal como base
            solicitacoesFiltradas.clear();
            solicitacoesFiltradas.addAll(trocasDevolucoes);
        }

        // 2. Aplica filtros de Rádio-Button
        List<TrocaDevolucao> resultadoFinal = new ArrayList<>(solicitacoesFiltradas);

        RadioButton tipoSelecionado = (RadioButton) tipoGroup.getSelectedToggle();
        if (tipoSelecionado != null) {
            String tipo = tipoSelecionado.getText().toUpperCase();
            resultadoFinal = resultadoFinal.stream()
                    .filter(s -> s.getTipoSolicitacao().equals(tipo))
                    .collect(Collectors.toList());
        }

        RadioButton statusSelecionado = (RadioButton) statusGroup.getSelectedToggle();
        if (statusSelecionado != null) {
            String status = statusSelecionado.getText().toUpperCase();
            resultadoFinal = resultadoFinal.stream()
                    .filter(s -> s.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // 3. Atualiza a lista final e o layout
        solicitacoesFiltradas.clear();
        solicitacoesFiltradas.addAll(resultadoFinal);

        atualizarGradeSolicitacoes();

        // Atualiza o contador de resultados na barra lateral (garantindo que os elementos existem)
        if (scrollGrade != null && scrollGrade.getParent() instanceof SplitPane) {
            VBox barraLateral = (VBox) ((SplitPane) scrollGrade.getParent()).getItems().get(0);
            // O contador Resultados é o segundo elemento (índice 1)
            if (barraLateral.getChildren().size() > 1) {
                Label contadorResultados = (Label) barraLateral.getChildren().get(1);
                contadorResultados.setText(solicitacoesFiltradas.size() + " solicitações encontradas");
            }
        }
    }


    // --- MÉTODOS DE JANELAS (DETALHES, NOVA) ---

    private void mostrarJanelaDetalhesSolicitacao(TrocaDevolucao solicitacao) {
        Stage janelaDetalhes = new Stage();
        janelaDetalhes.setTitle("Detalhes da Solicitação - " + solicitacao.getNumeroPedido());

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label("DETALHES DA SOLICITAÇÃO");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#498090"));

        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(20);
        formulario.setPadding(new Insets(20, 0, 20, 0));

        TextField numeroPedidoField = new TextField(solicitacao.getNumeroPedido());
        TextField produtoIdField = new TextField(String.valueOf(solicitacao.getProdutoId()));
        TextField tipoField = new TextField(solicitacao.getTipoSolicitacao());
        TextField statusField = new TextField(solicitacao.getStatus());
        TextField motivoField = new TextField(solicitacao.getMotivo());
        TextField quantidadeField = new TextField(String.valueOf(solicitacao.getQuantidade()));
        TextField valorUnitarioField = new TextField(String.format("%.2f", solicitacao.getValorUnitario()));
        TextField valorTotalField = new TextField(String.format("%.2f", solicitacao.getValorTotal()));
        TextField dataSolicitacaoField = new TextField(solicitacao.getDataSolicitacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        TextField dataProcessamentoField = new TextField(solicitacao.getDataProcessamento() != null ?
                solicitacao.getDataProcessamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        TextArea observacoesArea = new TextArea(solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "");
        observacoesArea.setPromptText("Observações da solicitação...");
        observacoesArea.setPrefRowCount(4);

        numeroPedidoField.setEditable(false);
        produtoIdField.setEditable(false);
        tipoField.setEditable(false);
        statusField.setEditable(false);
        motivoField.setEditable(false);
        quantidadeField.setEditable(false);
        valorUnitarioField.setEditable(false);
        valorTotalField.setEditable(false);
        dataSolicitacaoField.setEditable(false);
        dataProcessamentoField.setEditable(false);
        observacoesArea.setEditable(false);

        adicionarCampoFormulario(formulario, "Número do Pedido:", numeroPedidoField, 0);
        adicionarCampoFormulario(formulario, "Produto ID:", produtoIdField, 1);
        adicionarCampoFormulario(formulario, "Tipo:", tipoField, 2);
        adicionarCampoFormulario(formulario, "Status:", statusField, 3);
        adicionarCampoFormulario(formulario, "Motivo:", motivoField, 4);
        adicionarCampoFormulario(formulario, "Quantidade:", quantidadeField, 5);
        adicionarCampoFormulario(formulario, "Valor Unitário:", valorUnitarioField, 6);
        adicionarCampoFormulario(formulario, "Valor Total:", valorTotalField, 7);
        adicionarCampoFormulario(formulario, "Data Solicitação:", dataSolicitacaoField, 8);
        adicionarCampoFormulario(formulario, "Data Processamento:", dataProcessamentoField, 9);

        Label observacoesLabel = new Label("Observações:");
        observacoesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        observacoesLabel.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(observacoesLabel, 0, 10);
        GridPane.setConstraints(observacoesArea, 1, 10);
        observacoesArea.setStyle("-fx-border-color: #D8B167; -fx-background-color: #f8f9fa;");
        formulario.getChildren().addAll(observacoesLabel, observacoesArea);

        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER);

        Button btnExcluir = new Button("Excluir");
        btnExcluir.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 100;");

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 100;");

        Button btnProcessar = new Button("Processar");
        btnProcessar.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 100;");
        btnProcessar.setDisable(!solicitacao.isPendente());

        Button btnFechar = new Button("Fechar");
        btnFechar.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-pref-width: 100;");

        botoes.getChildren().addAll(btnExcluir, btnEditar, btnProcessar, btnFechar);

        btnEditar.setOnAction(e -> {
            motivoField.setEditable(true);
            quantidadeField.setEditable(true);
            valorUnitarioField.setEditable(true);
            observacoesArea.setEditable(true);
            btnProcessar.setDisable(false);
            btnEditar.setDisable(true);
        });

        btnProcessar.setOnAction(e -> {
            try {
                solicitacao.setMotivo(motivoField.getText());
                solicitacao.setQuantidade(Integer.parseInt(quantidadeField.getText()));
                solicitacao.setValorUnitario(Double.parseDouble(valorUnitarioField.getText()));
                solicitacao.setObservacoes(observacoesArea.getText());

                solicitacao.aprovar();

                motivoField.setEditable(false);
                quantidadeField.setEditable(false);
                valorUnitarioField.setEditable(false);
                observacoesArea.setEditable(false);
                btnProcessar.setDisable(true);
                btnEditar.setDisable(false);

                statusField.setText(solicitacao.getStatus());
                dataProcessamentoField.setText(solicitacao.getDataProcessamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                salvarSolicitacoes();
                aplicarFiltros(true); // Recarrega o grid
                janelaDetalhes.close();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Solicitação processada com sucesso!");
                sucesso.showAndWait();

            } catch (Exception ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao processar solicitação");
                erro.setContentText("Verifique os dados informados: " + ex.getMessage());
                erro.showAndWait();
            }
        });

        btnExcluir.setOnAction(e -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Excluir Solicitação");
            confirmacao.setContentText("Tem certeza que deseja excluir a solicitação do pedido: " + solicitacao.getNumeroPedido() + "?");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.OK) {
                    trocasDevolucoes.remove(solicitacao);
                    salvarSolicitacoes();
                    aplicarFiltros(true); // Recarrega o grid
                    janelaDetalhes.close();

                    Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                    sucesso.setTitle("Sucesso");
                    sucesso.setHeaderText(null);
                    sucesso.setContentText("Solicitação excluída com sucesso!");
                    sucesso.showAndWait();
                }
            });
        });

        btnFechar.setOnAction(e -> janelaDetalhes.close());

        layoutPrincipal.getChildren().addAll(titulo, formulario, botoes);

        Scene cena = new Scene(layoutPrincipal, 600, 700);
        janelaDetalhes.setScene(cena);
        janelaDetalhes.show();
    }

    private void mostrarJanelaNovaSolicitacao() {
        Stage janelaNova = new Stage();
        janelaNova.setTitle("Nova Solicitação de Troca/Devolução");

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label("NOVA SOLICITAÇÃO");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#498090"));

        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(20);
        formulario.setPadding(new Insets(20, 0, 20, 0));

        TextField numeroPedidoField = new TextField();
        TextField produtoIdField = new TextField();
        TextField motivoField = new TextField();
        TextField quantidadeField = new TextField("1");
        TextField valorUnitarioField = new TextField("0.00");
        DatePicker dataSolicitacaoField = new DatePicker(LocalDate.now());
        TextArea observacoesArea = new TextArea();
        observacoesArea.setPromptText("Observações adicionais...");
        observacoesArea.setPrefRowCount(3);

        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("TROCA", "DEVOLUCAO");
        tipoCombo.setValue("TROCA");
        tipoCombo.setStyle("-fx-border-color: #D8B167;");

        adicionarCampoFormulario(formulario, "Número do Pedido:", numeroPedidoField, 0);
        adicionarCampoFormulario(formulario, "Produto ID:", produtoIdField, 1);
        adicionarCampoFormularioCombo(formulario, "Tipo:", tipoCombo, 2);
        adicionarCampoFormulario(formulario, "Motivo:", motivoField, 3);
        adicionarCampoFormulario(formulario, "Quantidade:", quantidadeField, 4);
        adicionarCampoFormulario(formulario, "Valor Unitário:", valorUnitarioField, 5);

        Label dataLabel = new Label("Data Solicitação:");
        dataLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        dataLabel.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(dataLabel, 0, 6);
        GridPane.setConstraints(dataSolicitacaoField, 1, 6);
        dataSolicitacaoField.setStyle("-fx-border-color: #D8B167;");
        formulario.getChildren().addAll(dataLabel, dataSolicitacaoField);

        Label observacoesLabel = new Label("Observações:");
        observacoesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        observacoesLabel.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(observacoesLabel, 0, 7);
        GridPane.setConstraints(observacoesArea, 1, 7);
        observacoesArea.setStyle("-fx-border-color: #D8B167; -fx-background-color: #f8f9fa;");
        formulario.getChildren().addAll(observacoesLabel, observacoesArea);

        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-pref-width: 120;");

        botoes.getChildren().addAll(btnSalvar, btnCancelar);

        btnSalvar.setOnAction(e -> {
            try {
                TrocaDevolucao novaSolicitacao = new TrocaDevolucao(
                        trocasDevolucoes.size() + 1,
                        Integer.parseInt(produtoIdField.getText()),
                        numeroPedidoField.getText(),
                        tipoCombo.getValue(),
                        motivoField.getText(),
                        dataSolicitacaoField.getValue(),
                        "PENDENTE",
                        Integer.parseInt(quantidadeField.getText()),
                        Double.parseDouble(valorUnitarioField.getText())
                );

                novaSolicitacao.setObservacoes(observacoesArea.getText());

                trocasDevolucoes.add(novaSolicitacao);
                salvarSolicitacoes();
                aplicarFiltros(true); // Recarrega o grid
                janelaNova.close();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Solicitação criada com sucesso!");
                sucesso.showAndWait();

            } catch (Exception ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao criar solicitação");
                erro.setContentText("Verifique os dados informados: " + ex.getMessage());
                erro.showAndWait();
            }
        });

        btnCancelar.setOnAction(e -> janelaNova.close());

        layoutPrincipal.getChildren().addAll(titulo, formulario, botoes);

        Scene cena = new Scene(layoutPrincipal, 500, 600);
        janelaNova.setScene(cena);
        janelaNova.show();
    }

    private void processarSolicitacao(TrocaDevolucao solicitacao) {
        if (solicitacao.isPendente()) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Processar Solicitação");
            confirmacao.setHeaderText("Processar " + (solicitacao.isTroca() ? "Troca" : "Devolução"));
            confirmacao.setContentText("Deseja aprovar esta solicitação?");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.OK) {
                    solicitacao.aprovar();
                    salvarSolicitacoes();
                    aplicarFiltros(true); // Recarrega o grid

                    Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                    sucesso.setTitle("Sucesso");
                    sucesso.setHeaderText(null);
                    sucesso.setContentText("Solicitação aprovada com sucesso!");
                    sucesso.showAndWait();
                }
            });
        } else {
            mostrarJanelaDetalhesSolicitacao(solicitacao);
        }
    }

    private void adicionarCampoFormulario(GridPane grid, String label, TextField campo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(labelCampo, 0, linha);

        campo.setPrefWidth(250);
        campo.setStyle("-fx-font-size: 14; -fx-border-color: #D8B167; -fx-background-color: #f8f9fa;");
        GridPane.setConstraints(campo, 1, linha);

        grid.getChildren().addAll(labelCampo, campo);
    }

    private void adicionarCampoFormularioCombo(GridPane grid, String label, ComboBox<String> combo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(labelCampo, 0, linha);

        combo.setPrefWidth(250);
        GridPane.setConstraints(combo, 1, linha);

        grid.getChildren().addAll(labelCampo, combo);
    }
}