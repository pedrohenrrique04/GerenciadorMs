package view;

import Dao.ProdutoDAO;
import Model.ProdutoModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe de Visão (View) para o Catálogo de Produtos.
 * Funciona como um componente a ser inserido em um BorderPane (Dashboard).
 */
public class TelaProdutos {

    // 🚨 CORREÇÃO 1: Objeto DAO para interagir com o MySQL
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
    // 🚨 DATA_FILE REMOVIDO: Não usaremos mais persistência em arquivo
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Cores do Tema
    private static final String COR_TOPO = "#0F172A";
    private static final String COR_PRIMARIA = "#498090";
    private static final String COR_DESTAQUE = "#D8B167";


    // =================================================================
    // MÉTODO PRINCIPAL DE INTEGRAÇÃO (getTela)
    // =================================================================
    public BorderPane getTela() {
        criarDiretorioImagens();

        // 🚨 CORREÇÃO 2: Carrega a lista do MySQL
        carregarProdutosDoBanco();

        if (produtos.isEmpty()) {
            // Se o banco estiver vazio, carrega exemplos (e os salva no banco)
            carregarProdutosExemplo();
            salvarProdutosExemploNoBanco();
        }

        produtosFiltrados.addAll(produtos);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        VBox topo = criarTopo();
        root.setTop(topo);

        SplitPane centro = new SplitPane();
        centro.setDividerPositions(0.22);

        VBox barraLateralConteudo = criarBarraLateralFiltros();
        ScrollPane scrollFiltros = new ScrollPane(barraLateralConteudo);
        scrollFiltros.setFitToWidth(true);
        scrollFiltros.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollFiltros.setStyle("-fx-background: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0; -fx-border-color: " + COR_PRIMARIA + ";");

        scrollGrade = criarGradeProdutos();

        centro.getItems().addAll(scrollFiltros, scrollGrade);
        root.setCenter(centro);

        return root;
    }
    // =================================================================

    // --- Métodos de Persistência e Inicialização (Corrigidos) ---

    private void criarDiretorioImagens() {
        File diretorio = new File(IMAGES_DIR);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }

    // 🚨 NOVO: Método para carregar do Banco (substitui carregarProdutosSalvos)
    private void carregarProdutosDoBanco() {
        produtos = produtoDAO.listarTodos();
        System.out.println("✅ " + produtos.size() + " produtos carregados do MySQL.");
    }

    // 🚨 NOVO: Método para persistir os exemplos iniciais no Banco
    private void salvarProdutosExemploNoBanco() {
        for (ProdutoModel produto : produtos) {
            // Cria os exemplos apenas se o produto ainda não tiver um ID válido (o DAO verifica e insere)
            if (produto.getId() == 0) {
                produtoDAO.criar(produto);
            }
        }
    }


    // 🚨 MÉTODOS DE SERIALIZAÇÃO EM ARQUIVO FORAM REMOVIDOS COMPLETAMENTE

    // --- Métodos de Criação de UI (Topo, Lateral, Grade) ---

    private VBox criarTopo() {
        VBox topo = new VBox(15);
        topo.setPadding(new Insets(20));
        topo.setStyle("-fx-background-color: " + COR_TOPO + "; -fx-border-color: #3a6775; -fx-border-width: 0 0 2 0;");

        HBox linhaPrincipal = new HBox(15);
        linhaPrincipal.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("Produtos Ms");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);

        Button btnAdicionarProduto = new Button("+ Adicionar Produto");
        btnAdicionarProduto.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8;");
        btnAdicionarProduto.setOnAction(e -> mostrarJanelaNovoProduto());

        HBox acoesDireita = new HBox(15, btnAdicionarProduto);
        acoesDireita.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(titulo, Priority.ALWAYS);
        linhaPrincipal.getChildren().addAll(titulo, acoesDireita);

        HBox barraPesquisa = criarBarraPesquisa();

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);

        Label totalProdutos = new Label(produtos.size() + " produtos no total");
        Label categorias = new Label("3 categorias disponíveis");
        Label emEstoque = new Label(calcularTotalEstoque() + " unidades em unidades em estoque");

        for (Label stat : new Label[]{totalProdutos, categorias, emEstoque}) {
            stat.setFont(Font.font("System", 12));
            stat.setTextFill(Color.WHITE);
        }

        stats.getChildren().addAll(totalProdutos, categorias, emEstoque);

        topo.getChildren().addAll(linhaPrincipal, barraPesquisa, stats);
        return topo;
    }

    private HBox criarBarraPesquisa() {
        HBox barraPesquisa = new HBox(10);
        barraPesquisa.setAlignment(Pos.CENTER_LEFT);
        barraPesquisa.setPadding(new Insets(10, 0, 0, 0));

        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("Pesquisar produtos por nome, categoria, cor...");
        campoPesquisa.setPrefWidth(400);
        campoPesquisa.setPrefHeight(40);
        campoPesquisa.setStyle("-fx-font-size: 14; -fx-background-radius: 20; -fx-border-radius: 20; -fx-background-color: white; -fx-border-color: " + COR_DESTAQUE + ";");

        btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-font-size: 14;");
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

    private VBox criarBarraLateralFiltros() {
        VBox barraLateral = new VBox(20);
        barraLateral.setPadding(new Insets(25));
        barraLateral.setPrefWidth(280);

        Label tituloFiltros = new Label("FILTROS");
        tituloFiltros.setFont(Font.font("System", FontWeight.BOLD, 20));
        tituloFiltros.setTextFill(Color.web(COR_PRIMARIA));

        Label contadorResultados = new Label(produtosFiltrados.size() + " produtos encontrados");
        contadorResultados.setFont(Font.font("System", FontWeight.BOLD, 12));
        contadorResultados.setTextFill(Color.web(COR_PRIMARIA));

        VBox categoriaBox = criarSecaoFiltroRadio("CATEGORIA", Arrays.asList("Roupas", "Calçados", "Acessórios"));
        VBox catOpcoes = (VBox) categoriaBox.getChildren().get(1);
        roupasRadio = (RadioButton) catOpcoes.getChildren().get(0);
        calcadosRadio = (RadioButton) catOpcoes.getChildren().get(1);
        acessoriosRadio = (RadioButton) catOpcoes.getChildren().get(2);

        VBox generoBox = criarSecaoFiltroRadio("GÊNERO", Arrays.asList("Masculino", "Feminino", "Unissex"));
        VBox genOpcoes = (VBox) generoBox.getChildren().get(1);
        masculinoRadio = (RadioButton) genOpcoes.getChildren().get(0);
        femininoRadio = (RadioButton) genOpcoes.getChildren().get(1);
        unissexRadio = (RadioButton) genOpcoes.getChildren().get(2);

        VBox corBox = criarSecaoFiltroRadio("COR", Arrays.asList("Branco", "Preto", "Verde", "Azul", "Vermelho", "Amarelo", "Marrom", "Cinza"));
        VBox corOpcoes = (VBox) corBox.getChildren().get(1);
        brancoRadio = (RadioButton) corOpcoes.getChildren().get(0);
        pretoRadio = (RadioButton) corOpcoes.getChildren().get(1);
        verdeRadio = (RadioButton) corOpcoes.getChildren().get(2);

        btnLimparFiltros = new Button("🧹 Limpar Todos os Filtros");
        btnLimparFiltros.setStyle("-fx-background-color: " + COR_PRIMARIA + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8;");
        btnLimparFiltros.setMaxWidth(Double.MAX_VALUE);
        btnLimparFiltros.setPrefHeight(40);
        btnLimparFiltros.setOnAction(e -> limparTodosFiltros());

        configurarEventosFiltros();

        barraLateral.getChildren().addAll(
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

        return barraLateral;
    }

    private VBox criarSecaoFiltroRadio(String titulo, List<String> opcoes) {
        VBox secao = new VBox(15);

        Label labelTitulo = new Label(titulo);
        labelTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        labelTitulo.setTextFill(Color.web(COR_PRIMARIA));

        VBox opcoesBox = new VBox(8);

        ToggleGroup toggleGroup = new ToggleGroup();

        for (String opcao : opcoes) {
            RadioButton radioButton = new RadioButton(opcao);
            radioButton.setFont(Font.font("System", 14));
            radioButton.setStyle("-fx-text-fill: #2c3e50;");
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
        gradeProdutos.setStyle("-fx-background-color: #f5f7fa;");

        atualizarGradeProdutos();

        ScrollPane scroll = new ScrollPane(gradeProdutos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f5f7fa; -fx-border-color: transparent;");
        return scroll;
    }

    // --- Métodos de Criação de Card, Lógica e Janelas Modais ---

    private VBox criarCardProduto(ProdutoModel produto) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setAlignment(Pos.TOP_CENTER);
        card.setCursor(Cursor.HAND);

        card.setOnMouseClicked(e -> mostrarJanelaDetalhesProduto(produto));

        HBox badgeContainer = new HBox();
        badgeContainer.setAlignment(Pos.TOP_LEFT);
        badgeContainer.setMaxWidth(Double.MAX_VALUE);

        Label badgeCategoria = new Label(produto.getCategoria().toUpperCase());
        badgeCategoria.setFont(Font.font("System", FontWeight.BOLD, 10));
        badgeCategoria.setTextFill(Color.WHITE);
        badgeCategoria.setStyle("-fx-background-color: " + COR_PRIMARIA + "; -fx-padding: 4 8; -fx-background-radius: 10;");

        badgeContainer.getChildren().add(badgeCategoria);

        StackPane imagemContainer = new StackPane();
        imagemContainer.setPrefSize(220, 160);
        imagemContainer.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-background-radius: 8;");

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
                    System.err.println("Erro ao carregar imagem para o card: " + e.getMessage());
                }
            }
        }

        if (imagemView.getImage() == null) {
            Label placeholderImg = new Label(getEmojiCategoria(produto.getCategoria()));
            placeholderImg.setFont(Font.font("System", 36));
            placeholderImg.setTextFill(Color.WHITE);
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
        nomeLabel.setTextFill(Color.web("#2c3e50"));

        HBox detalhes = new HBox(10);
        detalhes.setAlignment(Pos.CENTER_LEFT);

        Label generoLabel = new Label("👤 " + produto.getGenero());
        Label corLabel = new Label("🎨 " + produto.getCor());

        for (Label det : new Label[]{generoLabel, corLabel}) {
            det.setFont(Font.font("System", 10));
            det.setTextFill(Color.web("#7f8c8d"));
            det.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 2 6; -fx-background-radius: 8;");
        }

        detalhes.getChildren().addAll(generoLabel, corLabel);

        HBox precoEstoque = new HBox();
        precoEstoque.setAlignment(Pos.CENTER_LEFT);
        precoEstoque.setSpacing(15);

        Label precoLabel = new Label(String.format("R$ %.2f", produto.getPrecoVenda()));
        precoLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        precoLabel.setTextFill(Color.web(COR_PRIMARIA));

        Label estoqueLabel = new Label(produto.getQuantidade() + " em estoque");
        estoqueLabel.setFont(Font.font("System", 11));
        estoqueLabel.setTextFill(produto.getQuantidade() > 0 ? Color.web(COR_PRIMARIA) : Color.web("#e74c3c"));

        precoEstoque.getChildren().addAll(precoLabel, estoqueLabel);

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);

        Button btnDetalhes = new Button("Ver Detalhes");
        btnDetalhes.setStyle("-fx-background-color: " + COR_PRIMARIA + "; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnDetalhes.setPrefWidth(120);
        btnDetalhes.setOnAction(e -> mostrarJanelaDetalhesProduto(produto));

        Button btnComprar = new Button("Comprar");
        btnComprar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnComprar.setPrefWidth(80);
        btnComprar.setOnAction(e -> comprarProduto(produto));

        botoes.getChildren().addAll(btnDetalhes, btnComprar);

        infoBox.getChildren().addAll(nomeLabel, detalhes, precoEstoque, botoes);
        card.getChildren().addAll(badgeContainer, imagemContainer, infoBox);

        return card;
    }

    private void aplicarPesquisa() {
        String termoPesquisa = campoPesquisa.getText().trim().toLowerCase();
        produtosFiltrados.clear();
        List<ProdutoModel> baseFiltrada = aplicarFiltrosBase();

        List<ProdutoModel> resultado = baseFiltrada.stream()
                .filter(p -> termoPesquisa.isEmpty() ||
                        p.getNome().toLowerCase().contains(termoPesquisa) ||
                        p.getCategoria().toLowerCase().contains(termoPesquisa) ||
                        p.getCor().toLowerCase().contains(termoPesquisa) ||
                        (p.getDescricao() != null && p.getDescricao().toLowerCase().contains(termoPesquisa)))
                .collect(Collectors.toList());

        produtosFiltrados.addAll(resultado);
        atualizarGradeProdutos();
    }

    private void aplicarFiltros() {
        produtosFiltrados.clear();
        produtosFiltrados.addAll(aplicarFiltrosBase());
        aplicarPesquisa();
    }

    private List<ProdutoModel> aplicarFiltrosBase() {
        List<ProdutoModel> filtrados = new ArrayList<>(produtos);

        RadioButton categoriaSelecionada = (RadioButton) categoriaGroup.getSelectedToggle();
        RadioButton generoSelecionado = (RadioButton) generoGroup.getSelectedToggle();
        RadioButton corSelecionada = (RadioButton) corGroup.getSelectedToggle();

        if (categoriaSelecionada != null) {
            final String categoria = categoriaSelecionada.getText();
            filtrados = filtrados.stream()
                    .filter(produto -> produto.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        if (generoSelecionado != null) {
            final String genero = generoSelecionado.getText();
            filtrados = filtrados.stream()
                    .filter(produto -> produto.getGenero().equalsIgnoreCase(genero))
                    .collect(Collectors.toList());
        }

        if (corSelecionada != null) {
            final String cor = corSelecionada.getText();
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
        if (categoriaGroup.getSelectedToggle() != null) categoriaGroup.getSelectedToggle().setSelected(false);
        if (generoGroup.getSelectedToggle() != null) generoGroup.getSelectedToggle().setSelected(false);
        if (corGroup.getSelectedToggle() != null) corGroup.getSelectedToggle().setSelected(false);
        aplicarFiltros();
    }

    private void atualizarGradeProdutos() {
        gradeProdutos.getChildren().clear();

        if (produtosFiltrados.isEmpty()) {
            VBox mensagemVazia = new VBox(15);
            mensagemVazia.setAlignment(Pos.CENTER);
            mensagemVazia.setPadding(new Insets(50));

            Label icone = new Label("procurar");
            icone.setFont(Font.font("System", 48));

            Label texto = new Label("Nenhum produto encontrado");
            texto.setFont(Font.font("System", FontWeight.BOLD, 18));
            texto.setTextFill(Color.web(COR_PRIMARIA));

            Label subtexto = new Label("Tente ajustar os filtros ou termos da pesquisa");
            subtexto.setFont(Font.font("System", 14));
            subtexto.setTextFill(Color.web("#7f8c8d"));

            Button btnLimparTudo = new Button("Limpar Todos os Filtros");
            btnLimparTudo.setStyle("-fx-background-color: " + COR_PRIMARIA + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
            btnLimparTudo.setOnAction(e -> limparTodosFiltros());

            mensagemVazia.getChildren().addAll(icone, texto, subtexto, btnLimparTudo);
            VBox wrapper = new VBox(mensagemVazia);
            wrapper.setAlignment(Pos.CENTER);
            wrapper.setPrefWidth(Double.MAX_VALUE);
            gradeProdutos.getChildren().add(wrapper);
        } else {
            for (ProdutoModel produto : produtosFiltrados) {
                VBox cardProduto = criarCardProduto(produto);
                gradeProdutos.getChildren().add(cardProduto);
            }
        }
    }

    private String getEmojiCategoria(String categoria) {
        String emoji;
        String catLower = categoria.toLowerCase();

        switch (catLower) {
            case "roupas":
                emoji = "👕";
                break;
            case "calçados":
                emoji = "👟";
                break;
            case "acessórios":
                emoji = "👜";
                break;
            default:
                emoji = "📦";
                break;
        }
        return emoji;
    }

    private int calcularTotalEstoque() {
        return produtos.stream().mapToInt(ProdutoModel::getQuantidade).sum();
    }

    private void carregarProdutosExemplo() {
        // IDs zerados para forçar o salvamento no banco, caso a lista esteja vazia
        produtos.add(new ProdutoModel(0, "Tênis Nike Air Force 1", 50, 299.99, 799.99,
                LocalDate.now().minusDays(10), null, "Calçados", "Masculino", "Branco"));
        produtos.add(new ProdutoModel(0, "Camiseta Basic Cotton", 100, 29.99, 79.99,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(15), "Roupas", "Feminino", "Preto"));
        produtos.add(new ProdutoModel(0, "Bolsa Couro Legítimo", 15, 199.99, 459.99,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(30), "Acessórios", "Feminino", "Marrom"));
        produtos.add(new ProdutoModel(0, "Jaqueta Jeans Masculina", 25, 89.99, 199.99,
                LocalDate.now().minusDays(7), null, "Roupas", "Masculino", "Azul"));
        produtos.add(new ProdutoModel(0, "Relógio Smartwatch", 20, 199.99, 399.99,
                LocalDate.now().minusDays(15), LocalDate.now().plusDays(7), "Acessórios", "Unissex", "Preto"));
    }

    private void comprarProduto(ProdutoModel produto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Produto Adicionado ao Carrinho");
        alert.setHeaderText(null);
        alert.setContentText("✅ " + produto.getNome() + "\n\nFoi adicionado ao seu carrinho de compras! (Simulação)");
        alert.showAndWait();
    }

    private void adicionarCampoFormulario(GridPane grid, String label, TextField campo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web(COR_PRIMARIA));
        GridPane.setConstraints(labelCampo, 0, linha);

        campo.setPrefWidth(300);
        campo.setStyle("-fx-font-size: 14; -fx-border-color: " + COR_DESTAQUE + "; -fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-radius: 5;");
        GridPane.setConstraints(campo, 1, linha);

        grid.getChildren().addAll(labelCampo, campo);
    }

    private void adicionarCampoFormularioCombo(GridPane grid, String label, ComboBox<String> combo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web(COR_PRIMARIA));
        GridPane.setConstraints(labelCampo, 0, linha);

        combo.setPrefWidth(300);
        combo.setStyle("-fx-font-size: 14; -fx-border-color: " + COR_DESTAQUE + "; -fx-background-radius: 5; -fx-border-radius: 5;");
        GridPane.setConstraints(combo, 1, linha);

        grid.getChildren().addAll(labelCampo, combo);
    }

    private void mostrarJanelaDetalhesProduto(ProdutoModel produto) {
        Stage janelaDetalhes = new Stage();
        janelaDetalhes.setTitle("Detalhes do Produto: " + produto.getNome());
        janelaDetalhes.initModality(Modality.APPLICATION_MODAL);

        janelaDetalhes.setWidth(600);
        janelaDetalhes.setHeight(650);
        janelaDetalhes.setResizable(false);

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(30));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label(produto.getNome().toUpperCase());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web(COR_PRIMARIA));

        GridPane detalhesGrid = new GridPane();
        detalhesGrid.setVgap(15);
        detalhesGrid.setHgap(20);
        detalhesGrid.setPadding(new Insets(15, 0, 15, 0));

        int row = 0;
        row = adicionarCampoReadOnly(detalhesGrid, "ID:", String.valueOf(produto.getId()), row);
        row = adicionarCampoReadOnly(detalhesGrid, "Categoria:", produto.getCategoria(), row);
        row = adicionarCampoReadOnly(detalhesGrid, "Gênero:", produto.getGenero(), row);
        row = adicionarCampoReadOnly(detalhesGrid, "Cor:", produto.getCor(), row);
        row = adicionarCampoReadOnly(detalhesGrid, "Estoque Atual:", String.valueOf(produto.getQuantidade()), row);
        row = adicionarCampoReadOnly(detalhesGrid, "Preço de Venda:", String.format("R$ %.2f", produto.getPrecoVenda()), row);
        row = adicionarCampoReadOnly(detalhesGrid, "Preço de Custo:", String.format("R$ %.2f", produto.getPrecoCusto()), row);

        String dataEntrada = produto.getDataEntrada() != null ? produto.getDataEntrada().format(DATE_FORMATTER) : "N/A";
        row = adicionarCampoReadOnly(detalhesGrid, "Entrada (Data):", dataEntrada, row);

        String dataReposicao = produto.getDataReposicao() != null ? produto.getDataReposicao().format(DATE_FORMATTER) : "Sem previsão";
        row = adicionarCampoReadOnly(detalhesGrid, "Reposição (Previsão):", dataReposicao, row);

        Label descricaoTitulo = new Label("Descrição Completa:");
        descricaoTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        descricaoTitulo.setTextFill(Color.web(COR_PRIMARIA));
        GridPane.setConstraints(descricaoTitulo, 0, row);

        TextArea descricaoArea = new TextArea(produto.getDescricao() != null ? produto.getDescricao() : "Nenhuma descrição fornecida.");
        descricaoArea.setEditable(false);
        descricaoArea.setWrapText(true);
        descricaoArea.setPrefRowCount(4);
        descricaoArea.setPrefWidth(350);
        descricaoArea.setStyle("-fx-border-color: #e0e0e0; -fx-background-color: #f8f9fa;");
        GridPane.setConstraints(descricaoArea, 1, row);

        detalhesGrid.getChildren().addAll(descricaoTitulo, descricaoArea);


        HBox botoesAcao = new HBox(15);
        botoesAcao.setAlignment(Pos.CENTER_RIGHT);

        Button btnEditar = new Button("Editar Produto");
        btnEditar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-pref-width: 150;");

        Button btnFechar = new Button("Fechar");
        btnFechar.setStyle("-fx-background-color: " + COR_PRIMARIA + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-pref-width: 100;");
        btnFechar.setOnAction(e -> janelaDetalhes.close());

        botoesAcao.getChildren().addAll(btnEditar, btnFechar);

        layoutPrincipal.getChildren().addAll(titulo, new Separator(), detalhesGrid, new Separator(), botoesAcao);
        Scene scene = new Scene(layoutPrincipal);
        janelaDetalhes.setScene(scene);
        janelaDetalhes.showAndWait();
    }

    private int adicionarCampoReadOnly(GridPane grid, String label, String valor, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web("#2c3e50"));
        GridPane.setConstraints(labelCampo, 0, linha);

        Label valorCampo = new Label(valor);
        valorCampo.setFont(Font.font("System", 14));
        valorCampo.setTextFill(Color.web(COR_PRIMARIA));
        GridPane.setConstraints(valorCampo, 1, linha);

        grid.getChildren().addAll(labelCampo, valorCampo);
        return linha + 1;
    }


    private void mostrarJanelaNovoProduto() {
        Stage janelaNovo = new Stage();
        janelaNovo.setTitle("Adicionar Novo Produto");
        janelaNovo.initModality(Modality.APPLICATION_MODAL);

        janelaNovo.setWidth(700);
        janelaNovo.setHeight(750);
        janelaNovo.setResizable(false);

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label("ADICIONAR NOVO PRODUTO");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web(COR_PRIMARIA));

        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(20);
        formulario.setPadding(new Insets(20, 0, 20, 0));

        TextField nomeField = new TextField();
        TextField quantidadeField = new TextField("0");
        TextField precoCustoField = new TextField("0.00");
        TextField precoVendaField = new TextField("0.00");
        TextField dataEntradaField = new TextField(LocalDate.now().format(DATE_FORMATTER));
        dataEntradaField.setEditable(false);
        TextField dataReposicaoField = new TextField();

        TextArea descricaoArea = new TextArea();
        descricaoArea.setPromptText("Digite a descrição do produto...");
        descricaoArea.setPrefRowCount(4);

        ComboBox<String> categoriaCombo = new ComboBox<>();
        categoriaCombo.getItems().addAll("Roupas", "Calçados", "Acessórios");
        categoriaCombo.setValue("Roupas");

        ComboBox<String> generoCombo = new ComboBox<>();
        generoCombo.getItems().addAll("Masculino", "Feminino", "Unissex");
        generoCombo.setValue("Unissex");

        ComboBox<String> corCombo = new ComboBox<>();
        corCombo.getItems().addAll("Branco", "Preto", "Verde", "Azul", "Vermelho", "Amarelo", "Marrom", "Cinza");
        corCombo.setValue("Branco");

        // --- CONSTRU&Ccedil;&Atilde;O DO FORMUL&Aacute;RIO ---
        adicionarCampoFormulario(formulario, "Nome do Produto:", nomeField, 0);
        adicionarCampoFormularioCombo(formulario, "Categoria:", categoriaCombo, 1);
        adicionarCampoFormularioCombo(formulario, "Gênero:", generoCombo, 2);
        adicionarCampoFormularioCombo(formulario, "Cor:", corCombo, 3);
        adicionarCampoFormulario(formulario, "Quantidade:", quantidadeField, 4);
        adicionarCampoFormulario(formulario, "Preço de Custo:", precoCustoField, 5);
        adicionarCampoFormulario(formulario, "Preço de Venda:", precoVendaField, 6);
        adicionarCampoFormulario(formulario, "Data de Entrada:", dataEntradaField, 7);
        adicionarCampoFormulario(formulario, "Data de Reposição:", dataReposicaoField, 8);

        Label descricaoLabel = new Label("Descrição:");
        descricaoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        descricaoLabel.setTextFill(Color.web(COR_PRIMARIA));
        GridPane.setConstraints(descricaoLabel, 0, 9);
        GridPane.setConstraints(descricaoArea, 1, 9);
        descricaoArea.setStyle("-fx-border-color: " + COR_DESTAQUE + "; -fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-radius: 5;");
        formulario.getChildren().addAll(descricaoLabel, descricaoArea);

        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button("Salvar Novo Produto");
        btnSalvar.setStyle("-fx-background-color: " + COR_DESTAQUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200; -fx-background-radius: 8;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: " + COR_PRIMARIA + "; -fx-text-fill: white; -fx-pref-width: 100; -fx-background-radius: 8;");

        botoes.getChildren().addAll(btnSalvar, btnCancelar);

        btnSalvar.setOnAction(e -> {
            try {
                String nome = nomeField.getText();
                if (nome.trim().isEmpty()) {
                    throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
                }

                // 1. Coleta e conversão (mantida)
                int quantidade = Integer.parseInt(quantidadeField.getText());
                // Permite vírgula ou ponto, convertendo para ponto antes de Double.parseDouble
                double precoCusto = Double.parseDouble(precoCustoField.getText().replace(',', '.'));
                double precoVenda = Double.parseDouble(precoVendaField.getText().replace(',', '.'));

                if (precoVenda <= 0) {
                    throw new IllegalArgumentException("O preço de venda deve ser maior que zero.");
                }

                LocalDate dataReposicao = null;
                if (!dataReposicaoField.getText().isEmpty()) {
                    dataReposicao = LocalDate.parse(dataReposicaoField.getText(), DATE_FORMATTER);
                }

                // O ID é 0 para que o DAO saiba que é um novo produto (o banco gera o ID)
                ProdutoModel novoProduto = new ProdutoModel(
                        0, // ID 0 para novo produto
                        nome,
                        quantidade,
                        precoCusto,
                        precoVenda,
                        LocalDate.parse(dataEntradaField.getText(), DATE_FORMATTER),
                        dataReposicao,
                        categoriaCombo.getValue(),
                        generoCombo.getValue(),
                        corCombo.getValue()
                );

                novoProduto.setDescricao(descricaoArea.getText());

                //  CORRE&Ccedil;&Atilde;O CR&Iacute;TICA: USA O DAO PARA SALVAR NO BANCO DE DADOS
                ProdutoModel produtoSalvo = produtoDAO.criar(novoProduto);

                if (produtoSalvo != null && produtoSalvo.getId() > 0) {
                    // 🚨 CORREÇÃO: Atualiza a lista da UI com o objeto RETORNADO do banco (com ID correto)
                    produtos.add(produtoSalvo);
                    aplicarFiltros(); // Re-aplica filtros e atualiza a grade

                    janelaNovo.close();

                    Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                    sucesso.setTitle("Sucesso");
                    sucesso.setHeaderText(null);
                    sucesso.setContentText("✅ Novo produto '" + nome + "' adicionado ao BANCO DE DADOS com sucesso!");
                    sucesso.showAndWait();
                } else {
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro de Persistência");
                    erro.setHeaderText("Falha ao adicionar produto");
                    erro.setContentText("O produto não foi salvo no banco de dados. Verifique o console para detalhes da exceção SQL (print stack trace).");
                    erro.showAndWait();
                }

            } catch (NumberFormatException ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro de Formato");
                erro.setHeaderText("Erro ao adicionar produto");
                erro.setContentText("Certifique-se de que a Quantidade é um número inteiro e os Preços (Custo e Venda) são números válidos.");
                erro.showAndWait();
            } catch (IllegalArgumentException ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro de Validação");
                erro.setHeaderText("Erro ao adicionar produto");
                erro.setContentText(ex.getMessage());
                erro.showAndWait();
            }
        });

        btnCancelar.setOnAction(e -> janelaNovo.close());

        layoutPrincipal.getChildren().addAll(titulo, formulario, new Separator(), botoes);
        Scene scene = new Scene(layoutPrincipal);
        janelaNovo.setScene(scene);
        janelaNovo.showAndWait();
    }

    // ... (restante dos métodos inalterados) ...
}