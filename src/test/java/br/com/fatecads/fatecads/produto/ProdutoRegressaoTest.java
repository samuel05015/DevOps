package br.com.fatecads.fatecads.produto;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.ProdutoRepository;
import br.com.fatecads.fatecads.service.UsuarioService;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.FilePayload;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProdutoRegressaoTest {

    private static final String ADMIN = "admin-regressao";
    private static final String CLIENTE = "cliente-regressao";
    private static final String SENHA_TESTE = "senha-ficticia";
    private static final String LISTAGEM = "/produtos/listar";
    private static final String TIPO_IMAGEM = "image/png";
    private static final Path EVIDENCIAS = Path.of("target", "evidencias-regressao");

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @LocalServerPort
    private int porta;

    private Playwright playwright;
    private Browser navegador;
    private byte[] imagemOriginal;

    @BeforeAll
    void prepararAmbiente() throws IOException {
        imagemOriginal = new ClassPathResource("static/img/batman-logo.png").getContentAsByteArray();
        Files.createDirectories(EVIDENCIAS);
        criarUsuario(ADMIN, "ROLE_ADMIN");
        criarUsuario(CLIENTE, "ROLE_USER");

        boolean exibirNavegador = Boolean.getBoolean("playwright.headed");
        playwright = Playwright.create();
        navegador = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(!exibirNavegador)
                .setSlowMo(exibirNavegador ? 350 : 0));
    }

    @AfterAll
    void fecharNavegador() {
        if (playwright != null) {
            playwright.close();
        }
    }

    @AfterEach
    void limparProdutos() {
        produtoRepository.deleteAll();
    }

    @Test
    @DisplayName("Cadastro com imagem aparece na listagem após recarregar")
    void cadastroPersisteProdutoComImagem() {
        try (BrowserContext contexto = novoContexto()) {
            Page pagina = entrar(contexto, ADMIN, "/admin");
            abrirListagem(pagina);
            pagina.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Novo produto")).click();
            preencherProduto(pagina, "Caneca Batman", "49.95", "un", "Gotham");
            pagina.getByLabel("Imagem do produto")
                    .setInputFiles(new FilePayload("produto.png", TIPO_IMAGEM, imagemOriginal));
            salvarProduto(pagina);
            pagina.reload();

            Locator produto = linhaProduto(pagina, "Caneca Batman");
            conferirLinha(produto, "49.95", "un", "Gotham");
            Locator imagem = produto.getByRole(AriaRole.IMG);
            assertThat(imagem).isVisible();
            conferirImagem(pagina, imagem.getAttribute("src"));
            capturar(pagina, "01-cadastro.png");
        }
    }

    @Test
    @DisplayName("Consulta ao catálogo exibe o produto, seus dados e sua imagem")
    void consultaExibeProdutoNoCatalogoDoCliente() {
        Produto registrado = criarProduto("Luminária Batman", 89.95, "un", "Gotham");
        try (BrowserContext contexto = novoContexto()) {
            Page pagina = entrar(contexto, CLIENTE, "/loja");

            Locator produto = pagina.getByRole(AriaRole.ARTICLE).filter(new Locator.FilterOptions()
                    .setHas(pagina.getByRole(AriaRole.HEADING,
                            new Page.GetByRoleOptions().setName("Luminária Batman").setExact(true))));
            assertThat(produto).hasCount(1);
            assertThat(produto.getByText("Gotham • un", new Locator.GetByTextOptions().setExact(true))).isVisible();
            assertThat(produto.getByText(Pattern.compile("R\\$\\s*89,95"))).isVisible();
            assertThat(produto.getByRole(AriaRole.IMG)).hasAttribute("src", caminhoImagem(registrado));
            conferirImagem(pagina, caminhoImagem(registrado));
            capturar(pagina, "02-consulta.png");
        }
    }

    @Test
    @DisplayName("Edição mantém o ID e a imagem original quando não há novo arquivo")
    void edicaoSemNovoArquivoMantemProdutoEImagem() {
        String descricaoAnterior = "Camiseta Batman";
        String descricaoNova = "Camiseta Batman Premium";
        Produto registrado = criarProduto(descricaoAnterior, 59.95, "un", "Gotham");
        String rotaEdicao = "/produtos/editar/" + registrado.getIdProduto();

        try (BrowserContext contexto = novoContexto()) {
            Page pagina = entrar(contexto, ADMIN, "/admin");
            abrirListagem(pagina);
            linkEdicao(linhaProduto(pagina, descricaoAnterior)).click();
            assertThat(pagina).hasURL(url(rotaEdicao));
            assertThat(pagina.getByLabel("Descrição")).hasValue(descricaoAnterior);
            assertThat(pagina.getByLabel("Valor")).hasValue("59.95");
            assertThat(pagina.getByLabel("Unidade")).hasValue("un");
            assertThat(pagina.getByLabel("Marca")).hasValue("Gotham");

            preencherProduto(pagina, descricaoNova, "79.95", "cx", "Wayne");
            salvarProduto(pagina);
            pagina.reload();

            assertThat(linhaProduto(pagina, descricaoAnterior)).hasCount(0);
            Locator atualizado = linhaProduto(pagina, descricaoNova);
            conferirLinha(atualizado, "79.95", "cx", "Wayne");
            assertThat(linkEdicao(atualizado)).hasAttribute("href", rotaEdicao);
            conferirImagem(pagina, caminhoImagem(registrado));
            assertThat(atualizado.getByRole(AriaRole.IMG)).hasAttribute("src", caminhoImagem(registrado));
            capturar(pagina, "03-edicao.png");
        }
    }

    private BrowserContext novoContexto() {
        return navegador.newContext(new Browser.NewContextOptions()
                .setLocale("pt-BR")
                .setViewportSize(1440, 900));
    }

    private Page entrar(BrowserContext contexto, String login, String destino) {
        Page pagina = contexto.newPage();
        pagina.navigate(url("/login"));
        pagina.getByLabel("Login", new Page.GetByLabelOptions().setExact(true)).fill(login);
        pagina.getByLabel("Senha", new Page.GetByLabelOptions().setExact(true)).fill(SENHA_TESTE);
        pagina.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
        assertThat(pagina).hasURL(url(destino));
        return pagina;
    }

    private void abrirListagem(Page pagina) {
        pagina.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Acessar Produtos")).click();
        assertThat(pagina).hasURL(url(LISTAGEM));
    }

    private void preencherProduto(Page pagina, String descricao, String valor, String unidade, String marca) {
        pagina.getByLabel("Descrição").fill(descricao);
        pagina.getByLabel("Valor").fill(valor);
        pagina.getByLabel("Unidade").fill(unidade);
        pagina.getByLabel("Marca").fill(marca);
    }

    private void salvarProduto(Page pagina) {
        pagina.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Salvar produto")).click();
        assertThat(pagina).hasURL(url(LISTAGEM));
    }

    private Locator linhaProduto(Page pagina, String descricao) {
        return pagina.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions()
                .setHas(pagina.getByRole(AriaRole.CELL,
                        new Page.GetByRoleOptions().setName(descricao).setExact(true))));
    }

    private void conferirLinha(Locator linha, String valor, String unidade, String marca) {
        assertThat(linha).hasCount(1);
        assertThat(linha.getByRole(AriaRole.CELL, new Locator.GetByRoleOptions().setName(valor).setExact(true)))
                .isVisible();
        assertThat(linha.getByRole(AriaRole.CELL, new Locator.GetByRoleOptions().setName(unidade).setExact(true)))
                .isVisible();
        assertThat(linha.getByRole(AriaRole.CELL, new Locator.GetByRoleOptions().setName(marca).setExact(true)))
                .isVisible();
    }

    private Locator linkEdicao(Locator linha) {
        return linha.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Editar").setExact(true));
    }

    private void conferirImagem(Page pagina, String caminho) {
        var resposta = pagina.request().get(url(caminho));
        try {
            assertEquals(200, resposta.status(), "A imagem original do produto deve continuar acessível");
            assertEquals(TIPO_IMAGEM, resposta.headers().get("content-type"));
            assertArrayEquals(imagemOriginal, resposta.body(), "O conteúdo da imagem original deve ser preservado");
        } finally {
            resposta.dispose();
        }
    }

    private String caminhoImagem(Produto produto) {
        return "/produtos/imagem/" + produto.getIdProduto();
    }

    private String url(String caminho) {
        return "http://127.0.0.1:" + porta + caminho;
    }

    private void capturar(Page pagina, String nome) {
        pagina.screenshot(new Page.ScreenshotOptions().setPath(EVIDENCIAS.resolve(nome)).setFullPage(true));
    }

    private void criarUsuario(String login, String perfil) {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(login);
        usuario.setLoginUsuario(login);
        usuario.setEmailUsuario(login + "@example.test");
        usuario.setSenhaUsuario(SENHA_TESTE);
        usuario.setRole(perfil);
        usuarioService.save(usuario);
    }

    private Produto criarProduto(String descricao, double valor, String unidade, String marca) {
        Produto produto = new Produto();
        produto.setDescricaoProduto(descricao);
        produto.setValorProduto(valor);
        produto.setUnidadeProduto(unidade);
        produto.setMarcaProduto(marca);
        produto.setImagemProduto(imagemOriginal);
        produto.setTipoImagemProduto(TIPO_IMAGEM);
        return produtoRepository.save(produto);
    }
}
