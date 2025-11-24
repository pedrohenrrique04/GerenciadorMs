package view;

import model.ProdutoModel;
import javafx.application.Application;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays; // <-- NOVO IMPORT ADICIONADO PARA Arrays.asList()
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe principal da aplicação de Catálogo de Produtos.
 * Usa ProdutoModel como modelo de dados.
 * Código compatível com Java 7.
 */
public class TelaProdutos extends Application {

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
    private static final String DATA_FILE = "produtos.dat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Catálogo de Produtos - Loja Elegance");

        criarDiretorioImagens();
        carregarProdutosSalvos();

        if (produtos.isEmpty()) {
            carregarProdutosExemplo();
        }

        produtosFiltrados.addAll(produtos);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        VBox topo = criarTopo();
        root.setTop(topo);

        SplitPane centro = new SplitPane();
        centro.setDividerPositions(0.22);

        VBox barraLateral = criarBarraLateralFiltros();
        scrollGrade = criarGradeProdutos();

        centro.getItems().addAll(barraLateral, scrollGrade);
        root.setCenter(centro);

        Scene scene = new Scene(root, 1400, 900);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> salvarProdutos());
    }

    // --- Métodos de Persistência e Inicialização ---

    private void criarDiretorioImagens() {
        File diretorio = new File(IMAGES_DIR);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarProdutosSalvos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            produtos = (List<ProdutoModel>) ois.readObject();
            System.out.println("✅ " + produtos.size() + " produtos carregados do arquivo.");
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo de dados não encontrado. Iniciando com produtos de exemplo.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar produtos salvos: " + e.getMessage());
        }
    }

    private void salvarProdutos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(produtos);
            System.out.println("💾 Produtos salvos com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao salvar produtos: " + e.getMessage());
        }
    }

    // --- Métodos de Criação de UI (Topo, Lateral, Grade) ---

    private VBox criarTopo() {
        VBox topo = new VBox(15);
        topo.setPadding(new Insets(20));
        topo.setStyle("-fx-background-color: #498090; -fx-border-color: #3a6775; -fx-border-width: 0 0 2 0;");

        HBox linhaPrincipal = new HBox(15);
        linhaPrincipal.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("LOJA ELEGANCE");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 32));
        titulo.setTextFill(Color.WHITE);

        Button btnAdicionarProduto = new Button("+ Adicionar Produto");
        btnAdicionarProduto.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8;");
        btnAdicionarProduto.setOnAction(e -> mostrarJanelaNovoProduto());

        // HBox para ações (Adicionar)
        HBox acoesDireita = new HBox(15, btnAdicionarProduto);
        acoesDireita.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(titulo, Priority.ALWAYS);
        linhaPrincipal.getChildren().addAll(titulo, acoesDireita);

        HBox barraPesquisa = criarBarraPesquisa();

        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER);

        // Atualiza o total de produtos e estoque no topo
        Label totalProdutos = new Label(produtos.size() + " produtos no total");
        Label categorias = new Label("3 categorias disponíveis");
        Label emEstoque = new Label(calcularTotalEstoque() + " unidades em estoque");

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
        campoPesquisa.setPromptText("🔍 Pesquisar produtos por nome, categoria, cor...");
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

    private VBox criarBarraLateralFiltros() {
        VBox barraLateral = new VBox(20);
        barraLateral.setPadding(new Insets(25));
        barraLateral.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 2 0 0; -fx-border-color: #498090;");
        barraLateral.setPrefWidth(280);

        Label tituloFiltros = new Label("FILTROS");
        tituloFiltros.setFont(Font.font("System", FontWeight.BOLD, 20));
        tituloFiltros.setTextFill(Color.web("#498090"));

        Label contadorResultados = new Label(produtosFiltrados.size() + " produtos encontrados");
        contadorResultados.setFont(Font.font("System", FontWeight.BOLD, 12));
        contadorResultados.setTextFill(Color.web("#498090"));

        // CORRIGIDO: Usando Arrays.asList() para compatibilidade com Java 7
        VBox categoriaBox = criarSecaoFiltroRadio("CATEGORIA", Arrays.asList("Roupas", "Calçados", "Acessórios"));
        // Atribuições para acesso posterior
        VBox catOpcoes = (VBox) categoriaBox.getChildren().get(1);
        roupasRadio = (RadioButton) catOpcoes.getChildren().get(0);
        calcadosRadio = (RadioButton) catOpcoes.getChildren().get(1);
        acessoriosRadio = (RadioButton) catOpcoes.getChildren().get(2);

        // CORRIGIDO: Usando Arrays.asList() para compatibilidade com Java 7
        VBox generoBox = criarSecaoFiltroRadio("GÊNERO", Arrays.asList("Masculino", "Feminino", "Unissex"));
        // Atribuições para acesso posterior
        VBox genOpcoes = (VBox) generoBox.getChildren().get(1);
        masculinoRadio = (RadioButton) genOpcoes.getChildren().get(0);
        femininoRadio = (RadioButton) genOpcoes.getChildren().get(1);
        unissexRadio = (RadioButton) genOpcoes.getChildren().get(2);

        // CORRIGIDO: Usando Arrays.asList() para compatibilidade com Java 7
        VBox corBox = criarSecaoFiltroRadio("COR", Arrays.asList("Branco", "Preto", "Verde", "Azul", "Vermelho", "Amarelo", "Marrom", "Cinza"));
        // Atribuições para acesso posterior
        VBox corOpcoes = (VBox) corBox.getChildren().get(1);
        brancoRadio = (RadioButton) corOpcoes.getChildren().get(0);
        pretoRadio = (RadioButton) corOpcoes.getChildren().get(1);
        verdeRadio = (RadioButton) corOpcoes.getChildren().get(2); // Os demais serão acessados via grupo

        btnLimparFiltros = new Button("🧹 Limpar Todos os Filtros");
        btnLimparFiltros.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8;");
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
        badgeCategoria.setStyle("-fx-background-color: #498090; -fx-padding: 4 8; -fx-background-radius: 10;");

        badgeContainer.getChildren().add(badgeCategoria);

        StackPane imagemContainer = new StackPane();
        imagemContainer.setPrefSize(220, 160);
        imagemContainer.setStyle("-fx-background-color: #D8B167; -fx-background-radius: 8;");

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
        precoLabel.setTextFill(Color.web("#498090"));

        Label estoqueLabel = new Label(produto.getQuantidade() + " em estoque");
        estoqueLabel.setFont(Font.font("System", 11));
        estoqueLabel.setTextFill(produto.getQuantidade() > 0 ? Color.web("#498090") : Color.web("#e74c3c"));

        precoEstoque.getChildren().addAll(precoLabel, estoqueLabel);

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);

        Button btnDetalhes = new Button("Ver Detalhes");
        btnDetalhes.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnDetalhes.setPrefWidth(120);
        btnDetalhes.setOnAction(e -> mostrarJanelaDetalhesProduto(produto));

        Button btnComprar = new Button("Comprar");
        btnComprar.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-size: 12; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnComprar.setPrefWidth(80);
        btnComprar.setOnAction(e -> comprarProduto(produto));

        botoes.getChildren().addAll(btnDetalhes, btnComprar);

        infoBox.getChildren().addAll(nomeLabel, detalhes, precoEstoque, botoes);
        card.getChildren().addAll(badgeContainer, imagemContainer, infoBox);

        return card;
    }

    // --- Métodos de Lógica e Filtro ---

    private void aplicarPesquisa() {
        // Reutiliza o filtro base e aplica o termo de pesquisa no resultado
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
        // Aplica apenas os filtros de rádio
        produtosFiltrados.clear();
        produtosFiltrados.addAll(aplicarFiltrosBase());

        // Garante que o termo de pesquisa também seja aplicado após o filtro de rádio
        aplicarPesquisa();
    }

    private List<ProdutoModel> aplicarFiltrosBase() {
        // Filtra a lista principal 'produtos' com base nos RadioButtons selecionados
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

        // Chama aplicarFiltros para recarregar a grade
        aplicarFiltros();
    }

    private void atualizarGradeProdutos() {
        gradeProdutos.getChildren().clear();

        if (produtosFiltrados.isEmpty()) {
            VBox mensagemVazia = new VBox(15);
            mensagemVazia.setAlignment(Pos.CENTER);
            mensagemVazia.setPadding(new Insets(50));

            Label icone = new Label("🔍");
            icone.setFont(Font.font("System", 48));

            Label texto = new Label("Nenhum produto encontrado");
            texto.setFont(Font.font("System", FontWeight.BOLD, 18));
            texto.setTextFill(Color.web("#498090"));

            Label subtexto = new Label("Tente ajustar os filtros ou termos da pesquisa");
            subtexto.setFont(Font.font("System", 14));
            subtexto.setTextFill(Color.web("#7f8c8d"));

            Button btnLimparTudo = new Button("Limpar Todos os Filtros");
            btnLimparTudo.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
            btnLimparTudo.setOnAction(e -> limparTodosFiltros());

            mensagemVazia.getChildren().addAll(icone, texto, subtexto, btnLimparTudo);
            // Adiciona a mensagem ao centro da grade (precisa de um wrapper para centralizar no FlowPane)
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

    // --- Métodos Auxiliares ---

    /**
     * CORREÇÃO PARA JAVA 7: Substitui o switch expression por um switch statement tradicional.
     */
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
        // Uso do construtor ProdutoModel(int id, String nome, int quantidade, double precoCusto, double precoVenda,
        //                   LocalDate dataEntrada, LocalDate dataReposicao, String categoria, String genero, String cor)
        produtos.add(new ProdutoModel(1, "Tênis Nike Air Force 1", 50, 299.99, 799.99,
                LocalDate.now().minusDays(10), null, "Calçados", "Masculino", "Branco"));
        produtos.add(new ProdutoModel(2, "Camiseta Basic Cotton", 100, 29.99, 79.99,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(15), "Roupas", "Feminino", "Preto"));
        produtos.add(new ProdutoModel(3, "Bolsa Couro Legítimo", 15, 199.99, 459.99,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(30), "Acessórios", "Feminino", "Marrom"));
        produtos.add(new ProdutoModel(4, "Jaqueta Jeans Masculina", 25, 89.99, 199.99,
                LocalDate.now().minusDays(7), null, "Roupas", "Masculino", "Azul"));
        produtos.add(new ProdutoModel(5, "Relógio Smartwatch", 20, 199.99, 399.99,
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
        labelCampo.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(labelCampo, 0, linha);

        campo.setPrefWidth(300);
        campo.setStyle("-fx-font-size: 14; -fx-border-color: #D8B167; -fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-radius: 5;");
        GridPane.setConstraints(campo, 1, linha);

        grid.getChildren().addAll(labelCampo, campo);
    }

    private void adicionarCampoFormularioCombo(GridPane grid, String label, ComboBox<String> combo, int linha) {
        Label labelCampo = new Label(label);
        labelCampo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelCampo.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(labelCampo, 0, linha);

        combo.setPrefWidth(300);
        combo.setStyle("-fx-font-size: 14; -fx-border-color: #D8B167; -fx-background-radius: 5; -fx-border-radius: 5;");
        GridPane.setConstraints(combo, 1, linha);

        grid.getChildren().addAll(labelCampo, combo);
    }

    // --- Janelas Modais ---

    private void mostrarJanelaNovoProduto() {
        Stage janelaNovo = new Stage();
        janelaNovo.setTitle("Adicionar Novo Produto");
        janelaNovo.initModality(Modality.APPLICATION_MODAL);

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: white;");

        Label titulo = new Label("ADICIONAR NOVO PRODUTO");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.web("#498090"));

        GridPane formulario = new GridPane();
        formulario.setVgap(15);
        formulario.setHgap(20);
        formulario.setPadding(new Insets(20, 0, 20, 0));

        // Campos de entrada
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

        // ComboBoxes
        ComboBox<String> categoriaCombo = new ComboBox<>();
        categoriaCombo.getItems().addAll("Roupas", "Calçados", "Acessórios");
        categoriaCombo.setValue("Roupas");

        ComboBox<String> generoCombo = new ComboBox<>();
        generoCombo.getItems().addAll("Masculino", "Feminino", "Unissex");
        generoCombo.setValue("Unissex");

        ComboBox<String> corCombo = new ComboBox<>();
        corCombo.getItems().addAll("Branco", "Preto", "Verde", "Azul", "Vermelho", "Amarelo", "Marrom", "Cinza");
        corCombo.setValue("Branco");

        // Preenche o formulário
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
        descricaoLabel.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(descricaoLabel, 0, 9);
        GridPane.setConstraints(descricaoArea, 1, 9);
        descricaoArea.setStyle("-fx-border-color: #D8B167; -fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-radius: 5;");
        formulario.getChildren().addAll(descricaoLabel, descricaoArea);

        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER);

        Button btnSalvar = new Button("Salvar Novo Produto");
        btnSalvar.setStyle("-fx-background-color: #D8B167; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 200; -fx-background-radius: 8;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-pref-width: 100; -fx-background-radius: 8;");

        botoes.getChildren().addAll(btnSalvar, btnCancelar);

        btnSalvar.setOnAction(e -> {
            try {
                String nome = nomeField.getText();
                if (nome.trim().isEmpty()) {
                    throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
                }

                // Validações básicas (Prevenção de NumberFormatException)
                int quantidade = Integer.parseInt(quantidadeField.getText());
                double precoCusto = Double.parseDouble(precoCustoField.getText().replace(',', '.'));
                double precoVenda = Double.parseDouble(precoVendaField.getText().replace(',', '.'));

                LocalDate dataReposicao = null;
                if (!dataReposicaoField.getText().isEmpty()) {
                    dataReposicao = LocalDate.parse(dataReposicaoField.getText(), DATE_FORMATTER);
                }

                // Lógica de criação do ID inteiro e uso do ProdutoModel
                ProdutoModel novoProduto = new ProdutoModel(
                        produtos.size() + 1, // ID sequencial
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

                novoProduto.setDescricao(descricaoArea.getText()); // Define a descrição

                produtos.add(novoProduto);
                salvarProdutos();
                aplicarFiltros(); // Atualiza a grade (que chama atualizarGradeProdutos)
                janelaNovo.close();

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Novo produto '" + nome + "' adicionado com sucesso!");
                sucesso.showAndWait();

            } catch (NumberFormatException ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro de Formato");
                erro.setHeaderText("Erro ao adicionar produto");
                erro.setContentText("Valores de Quantidade, Preço de Custo e Preço de Venda devem ser numéricos. Verifique se usou ponto (.) ou vírgula (,) corretamente.");
                erro.showAndWait();
            }
            catch (Exception ex) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro Inesperado");
                erro.setContentText("Ocorreu um erro ao processar o produto: " + ex.getMessage());
                erro.showAndWait();
                System.err.println("Erro inesperado: " + ex.getMessage());
            }
        });

        btnCancelar.setOnAction(e -> janelaNovo.close());

        layoutPrincipal.getChildren().addAll(titulo, new Separator(), formulario, botoes);
        janelaNovo.setScene(new Scene(layoutPrincipal));
        janelaNovo.showAndWait();
    }

    private void mostrarJanelaDetalhesProduto(ProdutoModel produto) {
        Stage janelaDetalhes = new Stage();
        janelaDetalhes.setTitle("Detalhes: " + produto.getNome());
        janelaDetalhes.initModality(Modality.APPLICATION_MODAL);

        VBox layoutPrincipal = new VBox(20);
        layoutPrincipal.setPadding(new Insets(25));
        layoutPrincipal.setStyle("-fx-background-color: #f8f9fa;");

        // Título e ID
        Label titulo = new Label(produto.getNome());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web("#498090"));

        Label idLabel = new Label("ID: " + produto.getId());
        idLabel.setFont(Font.font("System", 14));
        idLabel.setTextFill(Color.web("#7f8c8d"));

        // Preço e Estoque
        HBox precoEstoqueBox = new HBox(30);
        precoEstoqueBox.setAlignment(Pos.CENTER_LEFT);

        Label precoLabel = new Label(String.format("R$ %.2f", produto.getPrecoVenda()));
        precoLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));
        precoLabel.setTextFill(Color.web("#D8B167")); // Cor de destaque

        Label estoqueLabel = new Label("Estoque: " + produto.getQuantidade());
        estoqueLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        estoqueLabel.setTextFill(produto.getQuantidade() > 0 ? Color.web("#498090") : Color.web("#e74c3c"));

        precoEstoqueBox.getChildren().addAll(precoLabel, estoqueLabel);

        // Grid de Informações
        GridPane infoGrid = new GridPane();
        infoGrid.setVgap(10);
        infoGrid.setHgap(15);

        int row = 0;
        adicionarDetalheGrid(infoGrid, "Categoria:", produto.getCategoria(), row++);
        adicionarDetalheGrid(infoGrid, "Gênero:", produto.getGenero(), row++);
        adicionarDetalheGrid(infoGrid, "Cor:", produto.getCor(), row++);
        adicionarDetalheGrid(infoGrid, "Preço de Custo:", String.format("R$ %.2f", produto.getPrecoCusto()), row++);
        adicionarDetalheGrid(infoGrid, "Data de Entrada:", produto.getDataEntrada().format(DATE_FORMATTER), row++);

        String reposicao = produto.getDataReposicao() != null ? produto.getDataReposicao().format(DATE_FORMATTER) : "N/A";
        adicionarDetalheGrid(infoGrid, "Próxima Reposição:", reposicao, row++);

        // Descrição
        Label descTitulo = new Label("Descrição do Produto");
        descTitulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        descTitulo.setTextFill(Color.web("#498090"));

        TextArea descArea = new TextArea(produto.getDescricao() != null ? produto.getDescricao() : "Nenhuma descrição fornecida.");
        descArea.setEditable(false);
        descArea.setWrapText(true);
        descArea.setPrefHeight(100);
        descArea.setStyle("-fx-control-inner-background: white; -fx-background-radius: 5;");

        // Botões de Ação
        HBox botoes = new HBox(15);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        Button btnFechar = new Button("Fechar");
        btnFechar.setStyle("-fx-background-color: #498090; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnFechar.setOnAction(e -> janelaDetalhes.close());

        Button btnEditar = new Button("Editar Produto");
        btnEditar.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 8;");
        // Em um app real, este botão abriria a janela de edição
        btnEditar.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Simulação de Edição: Aqui você implementaria a lógica para abrir a tela de edição FXML/Controller.");
            a.showAndWait();
        });

        botoes.getChildren().addAll(btnEditar, btnFechar);

        layoutPrincipal.getChildren().addAll(
                titulo, idLabel, new Separator(),
                precoEstoqueBox, new Separator(),
                infoGrid, new Separator(),
                descTitulo, descArea,
                botoes
        );

        janelaDetalhes.setScene(new Scene(layoutPrincipal, 600, 600));
        janelaDetalhes.showAndWait();
    }

    private void adicionarDetalheGrid(GridPane grid, String label, String valor, int linha) {
        Label labelTitulo = new Label(label);
        labelTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelTitulo.setTextFill(Color.web("#498090"));
        GridPane.setConstraints(labelTitulo, 0, linha);

        Label labelValor = new Label(valor);
        labelValor.setFont(Font.font("System", 14));
        labelValor.setTextFill(Color.web("#2c3e50"));
        GridPane.setConstraints(labelValor, 1, linha);

        grid.getChildren().addAll(labelTitulo, labelValor);
    }
}