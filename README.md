# Fatec ADS Store

## Tema 15 — Teste de regressão da edição de produtos

A classe `ProdutoRegressaoTest` usa JUnit e Playwright para exercitar três fluxos no Chromium.

| Cenário | O que o teste verifica |
| --- | --- |
| Cadastro | O administrador preenche o formulário e envia uma imagem. Após recarregar a listagem, a nova linha tem os dados esperados e o servidor devolve a imagem enviada. |
| Consulta | O cliente entra na loja e encontra o produto com descrição, marca, unidade, preço em reais e imagem. |
| Edição | O administrador abre o formulário com os dados atuais, altera os campos e salva sem anexar um arquivo. Após recarregar, a listagem mostra uma única linha com os dados novos e o mesmo ID. O servidor devolve a imagem original com os mesmos bytes. |

Os testes sobem a aplicação em uma porta local disponível, usam contas fictícias e persistem os dados em um banco H2 em memória, no perfil `test`. O perfil também é usado pelos testes de contexto já existentes.

### Executar no Windows

Use Java 17 e execute os comandos a partir da raiz do projeto. Na primeira execução, instale o Chromium usado pelo Playwright.

```powershell
$env:PLAYWRIGHT_SKIP_BROWSER_GC = "1"
.\mvnw.cmd "-Dexec.classpathScope=test" "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium" org.codehaus.mojo:exec-maven-plugin:3.6.3:java
$env:PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD = "1"
.\mvnw.cmd "-Dtest=ProdutoRegressaoTest" test
```

Para a demonstração em sala, abra o Chromium durante o cenário de edição.

```powershell
.\mvnw.cmd "-Dtest=ProdutoRegressaoTest#edicaoSemNovoArquivoMantemProdutoEImagem" "-Dplaywright.headed=true" test
```

O Maven grava os resultados em `target/surefire-reports`. Os testes aprovados salvam capturas em `target/evidencias-regressao`. Para executar toda a suíte do projeto, use `.\mvnw.cmd test`.

### Reproduzir a regressão

Em uma cópia temporária do projeto, abra `ProdutoController.salvar` e desative o bloco que copia a imagem existente alterando a condição para `false && produto.getIdProduto() != null`. Rode `ProdutoRegressaoTest` na cópia. Cadastro e consulta devem continuar passando. O cenário de edição deve falhar porque o servidor retorna HTTP 404 ao buscar a imagem que deveria ter sido preservada. Rode a suíte na versão íntegra para confirmar a recuperação.
