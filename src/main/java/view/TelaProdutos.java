package view;

import Dao.ProdutoDAO; // Importando o DAO
import Model.ProdutoModel;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TelaProdutos extends Application {

    // Instância do DAO para comunicar com o Banco
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private List<ProdutoModel> produtos = new ArrayList<>();
    private List<ProdutoModel> produtosFiltrados = new ArrayList<>();

    private FlowPane gradeProdutos;
    private ScrollPane scrollGrade;

    private ToggleGroup categoriaGroup, generoGroup, corGroup;
    private RadioButton roupasRadio, calcadosRadio, acessoriosRadio;
    private RadioButton masculinoRadio, femininoRadio, unissexRadio;
    private RadioButton brancoRadio, pretoRadio, verdeRadio;
    private Button btnLimparFiltros;

    private TextField campoPesquisa;
    private Button btnPesquisar;
    private Button btnLimparPesquisa;

    private static final String IMAGES_DIR = "produto_images/";

    // Cores do Tema
    private static final String COR_TOPO = "#0F172A"; // Azul Escuro
    private static final String COR_DESTAQUE = "#D8B167"; // Dourado
    private static final String COR_TEXTO_ESCURO = "#0F172A";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gerenciamento de Estoque - Loja Elegance");

        criarDiretorioImagens();

        // Carrega do Banco de Dados ao iniciar
        carregarProdutosDoBanco();

        produtosFiltrados.addAll(produtos);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        VBox topo = criarTopo();
        root.setTop(topo);

        SplitPane centro = new SplitPane();
        centro.setDividerPositions(0.22);
        centro.setStyle("-fx-background-color: white; -fx-box-border: transparent;");

        ScrollPane barraLateral = criarBarraLateralFiltros();
        scrollGrade = criarGradeProdutos();

        centro.getItems().addAll(barraLateral, scrollGrade);
        root.setCenter(centro);

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void criarDiretorioImagens() {
        File diretorio = new File(IMAGES_DIR);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }

    // INTEGRAÇÃO: Busca os dados do MySQL
    private void carregarProdutosDoBanco() {
        produtos.clear();
        try {
            List<ProdutoModel> listaDoBanco = produtoDAO.listar();
            produtos.addAll(listaDoBanco);
            System.out.println("Produtos carregados: " + produtos.size());
        } catch (Exception e) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro de Conexão");
            erro.setHeaderText("Não foi possível carregar os produtos");
            erro.setContentText("Verifique sua conexão com a internet ou as configurações do banco.");
            erro.showAndWait();
        }
    }

    private VBox criarTopo() {
        VBox topo = new VBox(15);
        topo.setPadding(new Insets(20));
        topo.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        HBox linhaTitulo = new HBox();
        linhaTitulo.setAlignment(Pos.CENTER_LEFT);
        linhaTitulo.setSpacing(15);

        Label titulo = new Label("ESTOQUE ELEGANCE");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);

        Label badgeContador = new Label(produtos.size() + " itens cadastrados");
        badgeContador.setFont(Font.font("System", FontWeight.BOLD, 14));
        badgeContador.setTextFill(Color.WHITE);
        badgeContador.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-padding: 5 12; -fx-background-radius: 12;");

        Button btnAdicionarProduto = new Button("+ Novo Produto");
        btnAdicionarProduto.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand;");
        btnAdicionarProduto.setOnAction(e -> mostrarJanelaNovoProduto());

        linhaTitulo.getChildren().addAll(titulo, badgeContador, btnAdicionarProduto);

        HBox barraPesquisa = criarBarraPesquisa();

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);

        Label totalProdutos = new Label(produtos.size() + " itens totais");
        Label categorias = new Label("3 categorias");
        Label emEstoque = new Label(calcularTotalEstoque() + " unidades físicas");

        for (Label stat : new Label[]{totalProdutos, categorias, emEstoque}) {
            stat.setFont(Font.font("System", 12));
            stat.setTextFill(Color.web("#e2e8f0"));
        }

        stats.getChildren().addAll(totalProdutos, categorias, emEstoque);

        topo.getChildren().addAll(linhaTitulo, barraPesquisa, stats);
        return topo;
    }

    private HBox criarBarraPesquisa() {
        HBox barraPesquisa = new HBox(10);
        barraPesquisa.setAlignment(Pos.CENTER_LEFT);
        barraPesquisa.setPadding(new Insets(10, 0, 0, 0));

        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("🔍 Buscar no estoque (nome, código, cor)...");
        campoPesquisa.setPrefWidth(400);
        campoPesquisa.setPrefHeight(40);
        campoPesquisa.setStyle("-fx-font-size: 14; -fx-background-radius: 5; -fx-background-color: white;");

        btnPesquisar = new Button("Buscar");
        btnPesquisar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size: 14; -fx-cursor: hand;");
        btnPesquisar.setPrefHeight(40);
        btnPesquisar.setPrefWidth(100);
        btnPesquisar.setOnAction(e -> aplicarPesquisa());

        btnLimparPesquisa = new Button("Limpar");
        btnLimparPesquisa.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14; -fx-cursor: hand;");
        btnLimparPesquisa.setPrefHeight(40);
        btnLimparPesquisa.setPrefWidth(80);
        btnLimparPesquisa.setOnAction(e -> limparPesquisa());

        campoPesquisa.setOnAction(e -> aplicarPesquisa());

        barraPesquisa.getChildren().addAll(campoPesquisa, btnPesquisar, btnLimparPesquisa);
        return barraPesquisa;
    }

    private ScrollPane criarBarraLateralFiltros() {
        VBox conteudoFiltros = new VBox(20);
        conteudoFiltros.setPadding(new Insets(25));
        conteudoFiltros.setStyle("-fx-background-color: white;");

        Label tituloFiltros = new Label("FILTROS");
        tituloFiltros.setFont(Font.font("System", FontWeight.BOLD, 20));
        tituloFiltros.setTextFill(Color.web(COR_TEXTO_ESCURO));

        Label contadorResultados = new Label(produtosFiltrados.size() + " resultados");
        contadorResultados.setFont(Font.font("System", FontWeight.BOLD, 12));
        contadorResultados.setTextFill(Color.web("#64748b"));

        VBox categoriaBox = criarSecaoFiltroRadio("CATEGORIA", Arrays.asList("Roupas", "Calçados", "Acessórios"));
        roupasRadio = (RadioButton) ((VBox)categoriaBox.getChildren().get(1)).getChildren().get(0);
        calcadosRadio = (RadioButton) ((VBox)categoriaBox.getChildren().get(1)).getChildren().get(1);
        acessoriosRadio = (RadioButton) ((VBox)categoriaBox.getChildren().get(1)).getChildren().get(2);

        VBox generoBox = criarSecaoFiltroRadio("GÊNERO", Arrays.asList("Masculino", "Feminino", "Unissex"));
        masculinoRadio = (RadioButton) ((VBox)generoBox.getChildren().get(1)).getChildren().get(0);
        femininoRadio = (RadioButton) ((VBox)generoBox.getChildren().get(1)).getChildren().get(1);
        unissexRadio = (RadioButton) ((VBox)generoBox.getChildren().get(1)).getChildren().get(2);

        VBox corBox = criarSecaoFiltroRadio("COR", Arrays.asList("Branco", "Preto", "Verde"));
        brancoRadio = (RadioButton) ((VBox)corBox.getChildren().get(1)).getChildren().get(0);
        pretoRadio = (RadioButton) ((VBox)corBox.getChildren().get(1)).getChildren().get(1);
        verdeRadio = (RadioButton) ((VBox)corBox.getChildren().get(1)).getChildren().get(2);

        btnLimparFiltros = new Button("🧹 Limpar Filtros");
        btnLimparFiltros.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand;");
        btnLimparFiltros.setMaxWidth(Double.MAX_VALUE);
        btnLimparFiltros.setPrefHeight(40);
        btnLimparFiltros.setOnAction(e -> limparTodosFiltros());

        configurarEventosFiltros();

        conteudoFiltros.getChildren().addAll(
                tituloFiltros,
                contadorResultados,
                new Separator(),
                categoriaBox,
                new Separator(),
                generoBox,
                new Separator(),
                corBox,
                new Separator(),
                btnLimparFiltros
        );

        ScrollPane scrollLateral = new ScrollPane(conteudoFiltros);
        scrollLateral.setFitToWidth(true);
        scrollLateral.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollLateral.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollLateral.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 0 1 0 0;");
        scrollLateral.setPrefWidth(280);

        return scrollLateral;
    }

    private VBox criarSecaoFiltroRadio(String titulo, List<String> opcoes) {
        VBox secao = new VBox(15);

        Label labelTitulo = new Label(titulo);
        labelTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        labelTitulo.setTextFill(Color.web(COR_TEXTO_ESCURO));

        VBox opcoesBox = new VBox(8);

        ToggleGroup toggleGroup = new ToggleGroup();

        for (String opcao : opcoes) {
            RadioButton radioButton = new RadioButton(opcao);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setStyle("-fx-text-fill: #334155;");
            radioButton.setToggleGroup(toggleGroup);
            opcoesBox.getChildren().add(radioButton);
        }

        if (titulo.equals("CATEGORIA")) {
            categoriaGroup = toggleGroup;
        } else if (titulo.equals("GÊNERO")) {
            generoGroup = toggleGroup;
        } else if (titulo.equals("COR")) {
            corGroup = toggleGroup;
        }

        secao.getChildren().addAll(labelTitulo, opcoesBox);
        return secao;
    }

    private void configurarEventosFiltros() {
        categoriaGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        generoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        corGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private ScrollPane criarGradeProdutos() {
        gradeProdutos = new FlowPane();
        gradeProdutos.setPadding(new Insets(25));
        gradeProdutos.setHgap(20);
        gradeProdutos.setVgap(20);
        gradeProdutos.setStyle("-fx-background-color: white;");

        atualizarGradeProdutos();

        ScrollPane scroll = new ScrollPane(gradeProdutos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-border-color: transparent;");
        return scroll;
    }

    private void atualizarGradeProdutos() {
        gradeProdutos.getChildren().clear();

        if (produtosFiltrados.isEmpty()) {
            VBox mensagemVazia = new VBox(15);
            mensagemVazia.setAlignment(Pos.CENTER);
            mensagemVazia.setPadding(new Insets(50));

            Label icone = new Label("📦");
            icone.setFont(Font.font("System", 48));

            Label texto = new Label("Nenhum item encontrado");
            texto.setFont(Font.font("System", FontWeight.BOLD, 18));
            texto.setTextFill(Color.web(COR_TEXTO_ESCURO));

            Label subtexto = new Label("Tente ajustar os filtros ou pesquisar novamente");
            subtexto.setFont(Font.font("System", 14));
            subtexto.setTextFill(Color.web("#94a3b8"));

            Button btnLimparTudo = new Button("Limpar Filtros");
            btnLimparTudo.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            btnLimparTudo.setOnAction(e -> limparTodosFiltros());

            mensagemVazia.getChildren().addAll(icone, texto, subtexto, btnLimparTudo);
            gradeProdutos.getChildren().add(mensagemVazia);
        } else {
            for (ProdutoModel produto : produtosFiltrados) {
                VBox cardProduto = criarCardProduto(produto);
                gradeProdutos.getChildren().add(cardProduto);
            }
        }
    }

    private VBox criarCardProduto(ProdutoModel produto) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setAlignment(Pos.TOP_CENTER);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-border-color: " + COR_DESTAQUE + "; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 6); -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);"));

        card.setOnMouseClicked(e -> mostrarJanelaDetalhesProduto(produto));

        HBox badgeContainer = new HBox();
        badgeContainer.setAlignment(Pos.TOP_LEFT);
        badgeContainer.setMaxWidth(Double.MAX_VALUE);

        Label badgeCategoria = new Label(produto.getCategoria().toUpperCase());
        badgeCategoria.setFont(Font.font("System", FontWeight.BOLD, 10));
        badgeCategoria.setTextFill(Color.WHITE);
        badgeCategoria.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-padding: 4 8; -fx-background-radius: 4;");

        badgeContainer.getChildren().add(badgeCategoria);

        StackPane imagemContainer = new StackPane();
        imagemContainer.setPrefSize(220, 160);
        imagemContainer.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        ImageView imagemView = new ImageView();
        imagemView.setFitWidth(220);
        imagemView.setFitHeight(160);
        imagemView.setPreserveRatio(true);

        if (produto.getImagemPath() != null && !produto.getImagemPath().isEmpty()) {
            File imagemFile = new File(produto.getImagemPath());
            if (imagemFile.exists()) {
                try {
                    Image imagem = new Image(imagemFile.toURI().toString());
                    imagemView.setImage(imagem);
                } catch (Exception e) {
                }
            }
        }

        if (imagemView.getImage() == null) {
            Label placeholderImg = new Label(getEmojiCategoria(produto.getCategoria()));
            placeholderImg.setFont(Font.font("System", 36));
            placeholderImg.setTextFill(Color.web(COR_TOPO));
            imagemContainer.getChildren().add(placeholderImg);
        } else {
            imagemContainer.getChildren().add(imagemView);
        }

        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setMaxWidth(220);

        Label nomeLabel = new Label(produto.getNome());
        nomeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nomeLabel.setWrapText(true);
        nomeLabel.setMaxWidth(220);
        nomeLabel.setTextFill(Color.web(COR_TEXTO_ESCURO));

        HBox detalhes = new HBox(10);
        detalhes.setAlignment(Pos.CENTER_LEFT);

        Label generoLabel = new Label("👤 " + produto.getGenero());
        Label corLabel = new Label("🎨 " + produto.getCor());

        for (Label det : new Label[]{generoLabel, corLabel}) {
            det.setFont(Font.font("System", 10));
            det.setTextFill(Color.web("#64748b"));
            det.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 2 6; -fx-background-radius: 4;");
        }

        detalhes.getChildren().addAll(generoLabel, corLabel);

        HBox precoEstoque = new HBox();
        precoEstoque.setAlignment(Pos.CENTER_LEFT);
        precoEstoque.setSpacing(15);

        Label precoLabel = new Label(String.format("R$ %.2f", produto.getPrecoVenda()));
        precoLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        precoLabel.setTextFill(Color.web(COR_TOPO));

        Label estoqueLabel = new Label(produto.getQuantidade() + " un.");
        estoqueLabel.setFont(Font.font("System", 11));
        estoqueLabel.setTextFill(produto.getQuantidade() > 0 ? Color.web("#10b981") : Color.web("#ef4444"));

        precoEstoque.getChildren().addAll(precoLabel, estoqueLabel);

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);

        Button btnDetalhes = new Button("Editar / Detalhes");
        btnDetalhes.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold; -fx-cursor: hand;");
        btnDetalhes.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnDetalhes, Priority.ALWAYS);
        btnDetalhes.setOnAction(e -> mostrarJanelaDetalhesProduto(produto));

        botoes.getChildren().add(btnDetalhes);

        infoBox.getChildren().addAll(nomeLabel, detalhes, precoEstoque, botoes);
        card.getChildren().addAll(badgeContainer, imagemContainer, infoBox);

        return card;
    }

    private void mostrarJanelaDetalhesProduto(ProdutoModel produto) {
        Stage janelaDetalhes = new Stage();
        janelaDetalhes.setTitle("Gerenciar Produto - " + produto.getNome());

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label("EDITAR PRODUTO");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(COR_TOPO));

        VBox imagemContainer = new VBox(10);
        imagemContainer.setAlignment(Pos.CENTER);

        ImageView imagemView = new ImageView();
        imagemView.setFitWidth(200);
        imagemView.setFitHeight(150);
        imagemView.setPreserveRatio(true);

        if (produto.getImagemPath() != null && !produto.getImagemPath().isEmpty()) {
            File imagemFile = new File(produto.getImagemPath());
            if (imagemFile.exists()) {
                try {
                    Image imagem = new Image(imagemFile.toURI().toString());
                    imagemView.setImage(imagem);
                } catch (Exception e) {
                }
            }
        }

        if (imagemView.getImage() == null) {
            StackPane placeholder = new StackPane();
            placeholder.setPrefSize(200, 150);
            placeholder.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-background-radius: 8;");
            Label placeholderLabel = new Label(getEmojiCategoria(produto.getCategoria()));
            placeholderLabel.setFont(Font.font("System", 24));
            placeholderLabel.setTextFill(Color.WHITE);
            placeholder.getChildren().add(placeholderLabel);
            imagemContainer.getChildren().add(placeholder);
        } else {
            imagemContainer.getChildren().add(imagemView);
        }

        Button btnAdicionarImagem = new Button("📷 Alterar Imagem");
        btnAdicionarImagem.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand;");
        btnAdicionarImagem.setOnAction(e -> selecionarImagem(produto, imagemView, janelaDetalhes));

        imagemContainer.getChildren().add(btnAdicionarImagem);

        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(20);
        formulario.setPadding(new Insets(20, 0, 20, 0));

        TextField nomeField = new TextField(produto.getNome());
        TextField quantidadeField = new TextField(String.valueOf(produto.getQuantidade()));
        TextField precoCustoField = new TextField(String.format("%.2f", produto.getPrecoCusto()));
        TextField precoVendaField = new TextField(String.format("%.2f", produto.getPrecoVenda()));
        TextField dataEntradaField = new TextField(produto.getDataEntrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        TextField dataReposicaoField = new TextField(produto.getDataReposicao() != null ?
                produto.getDataReposicao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        TextArea descricaoArea = new TextArea(produto.getDescricao() != null ? produto.getDescricao() : "");
        descricaoArea.setPromptText("Digite a descrição do produto...");
        descricaoArea.setPrefRowCount(4);

        nomeField.setEditable(false);
        quantidadeField.setEditable(false);
        precoCustoField.setEditable(false);
        precoVendaField.setEditable(false);
        dataEntradaField.setEditable(false);
        dataReposicaoField.setEditable(false);
        descricaoArea.setEditable(false);

        adicionarCampoFormulario(formulario, "Nome do Produto:", nomeField, 0);
        adicionarCampoFormulario(formulario, "Estoque Atual:", quantidadeField, 1);
        adicionarCampoFormulario(formulario, "Preço de Custo:", precoCustoField, 2);
        adicionarCampoFormulario(formulario, "Preço de Venda:", precoVendaField, 3);
        adicionarCampoFormulario(formulario, "Data de Entrada:", dataEntradaField, 4);
        adicionarCampoFormulario(formulario, "Data de Reposição:", dataReposicaoField, 5);

        Label descricaoLabel = new Label("Descrição:");
        descricaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        descricaoLabel.setTextFill(Color.web(COR_TOPO));
        GridPane.setConstraints(descricaoLabel, 0, 6);
        GridPane.setConstraints(descricaoArea, 1, 6);
        descricaoArea.setStyle("-fx-border-color: " + COR_DESTAQUE + "; -fx-background-color: #f8f9fa;");
        formulario.getChildren().addAll(descricaoLabel, descricaoArea);

        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER);

        Button btnExcluir = new Button("Excluir Item");
        btnExcluir.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120; -fx-cursor: hand;");

        Button btnEditar = new Button("Editar Dados");
        btnEditar.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120; -fx-cursor: hand;");

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120; -fx-cursor: hand;");
        btnSalvar.setDisable(true);

        Button btnFechar = new Button("Cancelar");
        btnFechar.setStyle("-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-pref-width: 100; -fx-cursor: hand;");

        botoes.getChildren().addAll(btnExcluir, btnEditar, btnSalvar, btnFechar);

        btnEditar.setOnAction(e -> {
            nomeField.setEditable(true);
            quantidadeField.setEditable(true);
            precoCustoField.setEditable(true);
            precoVendaField.setEditable(true);
            dataEntradaField.setEditable(true);
            dataReposicaoField.setEditable(true);
            descricaoArea.setEditable(true);
            btnSalvar.setDisable(false);
            btnEditar.setDisable(true);
        });

        // INTEGRAÇÃO: Atualizar no banco
        btnSalvar.setOnAction(e -> {
            try {
                produto.setNome(nomeField.getText());
                produto.setQuantidade(Integer.parseInt(quantidadeField.getText()));
                produto.setPrecoCusto(Double.parseDouble(precoCustoField.getText()));
                produto.setPrecoVenda(Double.parseDouble(precoVendaField.getText()));
                produto.setDescricao(descricaoArea.getText());

                if (!dataReposicaoField.getText().isEmpty()) {
                    produto.setDataReposicao(LocalDate.parse(dataReposicaoField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } else {
                    produto.setDataReposicao(null);
                }

                nomeField.setEditable(false);
                quantidadeField.setEditable(false);
                precoCustoField.setEditable(false);
                precoVendaField.setEditable(false);
                dataEntradaField.setEditable(false);
                dataReposicaoField.setEditable(false);
                descricaoArea.setEditable(false);
                btnSalvar.setDisable(true);
                btnEditar.setDisable(false);

                // Chama o DAO para atualizar
                produtoDAO.atualizar(produto);
                atualizarGradeProdutos();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Dados do produto atualizados!");
                sucesso.showAndWait();

            } catch (Exception ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao salvar produto");
                erro.setContentText("Verifique os dados informados.");
                erro.showAndWait();
            }
        });

        // INTEGRAÇÃO: Excluir do banco
        btnExcluir.setOnAction(e -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Excluir Produto");
            confirmacao.setContentText("Tem certeza que deseja remover o produto: " + produto.getNome() + " do estoque?");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.OK) {
                    if (produto.getImagemPath() != null && !produto.getImagemPath().isEmpty()) {
                        File imagemFile = new File(produto.getImagemPath());
                        if (imagemFile.exists()) {
                            imagemFile.delete();
                        }
                    }

                    // Chama o DAO para excluir
                    produtoDAO.excluir(produto.getId());
                    produtos.remove(produto);
                    produtosFiltrados.remove(produto);
                    atualizarGradeProdutos();
                    janelaDetalhes.close();

                    Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                    sucesso.setTitle("Sucesso");
                    sucesso.setHeaderText(null);
                    sucesso.setContentText("Produto removido do estoque!");
                    sucesso.showAndWait();
                }
            });
        });

        btnFechar.setOnAction(e -> janelaDetalhes.close());

        layoutPrincipal.getChildren().addAll(titulo, imagemContainer, formulario, botoes);

        Scene cena = new Scene(layoutPrincipal, 650, 800);
        janelaDetalhes.setScene(cena);
        janelaDetalhes.show();
    }

    private void selecionarImagem(ProdutoModel produto, ImageView imagemView, Stage janela) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem do Produto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
        );

        File arquivoSelecionado = fileChooser.showOpenDialog(janela);
        if (arquivoSelecionado != null) {
            try {
                String extensao = getFileExtension(arquivoSelecionado.getName());
                String novoNome = "produto_" + produto.getId() + extensao;
                File destino = new File(IMAGES_DIR + novoNome);

                if (produto.getImagemPath() != null && !produto.getImagemPath().isEmpty()) {
                    File imagemAnterior = new File(produto.getImagemPath());
                    if (imagemAnterior.exists()) {
                        imagemAnterior.delete();
                    }
                }

                copyFile(arquivoSelecionado, destino);

                produto.setImagemPath(destino.getAbsolutePath());

                Image imagem = new Image(destino.toURI().toString());
                imagemView.setImage(imagem);

                // Atualiza a imagem no banco
                produtoDAO.atualizar(produto);
                atualizarGradeProdutos();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Imagem atualizada!");
                sucesso.showAndWait();

            } catch (Exception e) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao adicionar imagem");
                erro.setContentText("Não foi possível carregar a imagem.");
                erro.showAndWait();
            }
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot == -1) return ".jpg";
        return filename.substring(lastDot);
    }

    private void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    private void adicionarCampoFormulario(GridPane grid, String label, TextField campo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web(COR_TOPO));
        GridPane.setConstraints(labelCampo, 0, linha);

        campo.setPrefWidth(300);
        campo.setStyle("-fx-font-size: 14; -fx-border-color: " + COR_DESTAQUE + "; -fx-background-color: white;");
        GridPane.setConstraints(campo, 1, linha);

        grid.getChildren().addAll(labelCampo, campo);
    }

    private void mostrarJanelaNovoProduto() {
        Stage janelaNovo = new Stage();
        janelaNovo.setTitle("Cadastro de Novo Produto");

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label("CADASTRAR PRODUTO");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(COR_TOPO));

        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(20);
        formulario.setPadding(new Insets(20, 0, 20, 0));

        TextField nomeField = new TextField();
        TextField quantidadeField = new TextField("0");
        TextField precoCustoField = new TextField("0.00");
        TextField precoVendaField = new TextField("0.00");
        TextField dataEntradaField = new TextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        TextField dataReposicaoField = new TextField();
        TextArea descricaoArea = new TextArea();
        descricaoArea.setPromptText("Digite a descrição do produto...");
        descricaoArea.setPrefRowCount(4);

        ComboBox<String> categoriaCombo = new ComboBox<>();
        categoriaCombo.getItems().addAll("Roupas", "Calçados", "Acessórios");
        categoriaCombo.setValue("Roupas");
        categoriaCombo.setStyle("-fx-border-color: " + COR_DESTAQUE + ";");

        ComboBox<String> generoCombo = new ComboBox<>();
        generoCombo.getItems().addAll("Masculino", "Feminino", "Unissex");
        generoCombo.setValue("Unissex");
        generoCombo.setStyle("-fx-border-color: " + COR_DESTAQUE + ";");

        ComboBox<String> corCombo = new ComboBox<>();
        corCombo.getItems().addAll("Branco", "Preto", "Verde", "Azul", "Vermelho", "Amarelo");
        corCombo.setValue("Branco");
        corCombo.setStyle("-fx-border-color: " + COR_DESTAQUE + ";");

        adicionarCampoFormulario(formulario, "Nome do Produto:", nomeField, 0);
        adicionarCampoFormularioCombo(formulario, "Categoria:", categoriaCombo, 1);
        adicionarCampoFormularioCombo(formulario, "Gênero:", generoCombo, 2);
        adicionarCampoFormularioCombo(formulario, "Cor:", corCombo, 3);
        adicionarCampoFormulario(formulario, "Quantidade Inicial:", quantidadeField, 4);
        adicionarCampoFormulario(formulario, "Preço de Custo:", precoCustoField, 5);
        adicionarCampoFormulario(formulario, "Preço de Venda:", precoVendaField, 6);
        adicionarCampoFormulario(formulario, "Data de Entrada:", dataEntradaField, 7);
        adicionarCampoFormulario(formulario, "Data de Reposição:", dataReposicaoField, 8);

        Label descricaoLabel = new Label("Descrição:");
        descricaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        descricaoLabel.setTextFill(Color.web(COR_TOPO));
        GridPane.setConstraints(descricaoLabel, 0, 9);
        GridPane.setConstraints(descricaoArea, 1, 9);
        descricaoArea.setStyle("-fx-border-color: " + COR_DESTAQUE + "; -fx-background-color: white;");
        formulario.getChildren().addAll(descricaoLabel, descricaoArea);

        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button("Cadastrar");
        btnSalvar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120; -fx-cursor: hand;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-text-fill: white; -fx-pref-width: 120; -fx-cursor: hand;");

        botoes.getChildren().addAll(btnSalvar, btnCancelar);

        // INTEGRAÇÃO: Salvar no banco
        btnSalvar.setOnAction(e -> {
            try {
                ProdutoModel novoProduto = new ProdutoModel(
                        0, // ID 0, o banco vai gerar
                        nomeField.getText(),
                        Integer.parseInt(quantidadeField.getText()),
                        Double.parseDouble(precoCustoField.getText()),
                        Double.parseDouble(precoVendaField.getText()),
                        LocalDate.now(),
                        dataReposicaoField.getText().isEmpty() ? null : LocalDate.now().plusDays(30),
                        categoriaCombo.getValue(),
                        generoCombo.getValue(),
                        corCombo.getValue()
                );

                novoProduto.setDescricao(descricaoArea.getText());

                // Salva no banco e pega o ID
                produtoDAO.salvar(novoProduto);

                produtos.add(novoProduto);
                produtosFiltrados.add(novoProduto);
                atualizarGradeProdutos();
                janelaNovo.close();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Produto cadastrado com sucesso!");
                sucesso.showAndWait();

            } catch (Exception ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao cadastrar produto");
                erro.setContentText("Verifique os dados informados.");
                erro.showAndWait();
            }
        });

        btnCancelar.setOnAction(e -> janelaNovo.close());

        layoutPrincipal.getChildren().addAll(titulo, formulario, botoes);

        Scene cena = new Scene(layoutPrincipal, 600, 750);
        janelaNovo.setScene(cena);
        janelaNovo.show();
    }

    private void adicionarCampoFormularioCombo(GridPane grid, String label, ComboBox<String> combo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web(COR_TOPO));
        GridPane.setConstraints(labelCampo, 0, linha);

        combo.setPrefWidth(300);
        GridPane.setConstraints(combo, 1, linha);

        grid.getChildren().addAll(labelCampo, combo);
    }

    private void aplicarPesquisa() {
        String termoPesquisa = campoPesquisa.getText().trim().toLowerCase();

        if (termoPesquisa.isEmpty()) {
            aplicarFiltros();
        } else {
            List<ProdutoModel> baseFiltrada = aplicarFiltrosBase();

            produtosFiltrados = baseFiltrada.stream()
                    .filter(produto ->
                            produto.getNome().toLowerCase().contains(termoPesquisa) ||
                                    produto.getCategoria().toLowerCase().contains(termoPesquisa) ||
                                    produto.getGenero().toLowerCase().contains(termoPesquisa) ||
                                    produto.getCor().toLowerCase().contains(termoPesquisa)
                    )
                    .collect(Collectors.toList());

            atualizarGradeProdutos();
        }
    }

    private void aplicarFiltros() {
        produtosFiltrados = aplicarFiltrosBase();
        atualizarGradeProdutos();
    }

    private List<ProdutoModel> aplicarFiltrosBase() {
        List<ProdutoModel> filtrados = new ArrayList<>(produtos);

        RadioButton categoriaSelecionada = (RadioButton) categoriaGroup.getSelectedToggle();
        RadioButton generoSelecionado = (RadioButton) generoGroup.getSelectedToggle();
        RadioButton corSelecionada = (RadioButton) corGroup.getSelectedToggle();

        if (categoriaSelecionada != null) {
            String categoria = categoriaSelecionada.getText();
            filtrados = filtrados.stream()
                    .filter(produto -> produto.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        if (generoSelecionado != null) {
            String genero = generoSelecionado.getText();
            filtrados = filtrados.stream()
                    .filter(produto -> produto.getGenero().equalsIgnoreCase(genero))
                    .collect(Collectors.toList());
        }

        if (corSelecionada != null) {
            String cor = corSelecionada.getText();
            filtrados = filtrados.stream()
                    .filter(produto -> produto.getCor().equalsIgnoreCase(cor))
                    .collect(Collectors.toList());
        }

        return filtrados;
    }

    private void limparPesquisa() {
        campoPesquisa.clear();
        aplicarFiltros();
    }

    private void limparTodosFiltros() {
        limparPesquisa();
        limparFiltrosRadio();
    }

    private void limparFiltrosRadio() {
        categoriaGroup.selectToggle(null);
        generoGroup.selectToggle(null);
        corGroup.selectToggle(null);

        produtosFiltrados = new ArrayList<>(produtos);
        atualizarGradeProdutos();
    }

    private String getEmojiCategoria(String categoria) {
        switch (categoria.toLowerCase()) {
            case "roupas": return "👕";
            case "calçados": return "👟";
            case "acessórios": return "👜";
            default: return "📦";
        }
    }

    private int calcularTotalEstoque() {
        return produtos.stream().mapToInt(ProdutoModel::getQuantidade).sum();
    }

    public static void main(String[] args) {
        launch(args);
    }
}