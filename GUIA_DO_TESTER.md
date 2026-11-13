# 📘 Guia Passo a Passo do Tester (Automação de API em BDD)

Bem-vindo(a) à estrutura de automação de API!  
Este projeto foi construído para que **você não precise programar em Java** para criar novos testes automatizados. Todo o motor de requisições (`GET`, `POST`, `PUT`, `DELETE`), controle de sessão isolada, autenticação RBAC (Admin vs Usuário Comum) e geração de massa dinâmica já está pronto.

Seu trabalho para automatizar novos cenários consiste apenas em **montar o passo a passo em português (`Dado que` / `Quando` / `Então`)**.

---

## 🗺️ 1. Onde você vai trabalhar? (As 4 Pastas do Tester)

Você **nunca** precisa alterar arquivos dentro de `src/main/java` ou `src/test/java`. Tudo o que você precisa fica em **`src/test/resources/`**:

```text
📁 src/test/resources/
├── 📁 config/
│   └── 📄 application.properties     👉 1. Onde ficam as URLs das APIs (Variáveis Multi-API)
├── 📁 datapool/
│   ├── 📄 login-datapool.yml         👉 2. Onde você cadastra os JSONs/Massas de envio
│   ├── 📄 usuarios-datapool.yml
│   ├── 📄 produtos-datapool.yml
│   └── 📄 carrinhos-datapool.yml
├── 📁 features/
│   ├── 📄 login.feature              👉 3. Onde você escreve os cenários BDD (Dado/Quando/Então)
│   ├── 📄 usuarios_smoke.feature
│   ├── 📄 produtos.feature
│   └── 📄 carrinhos.feature
└── 📁 schemas/
    └── 📄 *-schema.json              👉 4. (Opcional) Onde ficam os contratos JSON Schema
```

---

## 🛠️ 2. Passo a Passo: Como criar um novo teste do zero

Siga sempre esta receita de **4 passos**:

### Passo 1: Verifique a variável da URL da API
Abra o arquivo `src/test/resources/config/application.properties`.
* A URL padrão já está configurada em `API_SERVEREST=https://serverest.dev`.
* Se você precisar testar **outra API**, basta adicionar uma nova variável nesse arquivo:
  ```properties
  API_PAGAMENTOS=https://minha-outra-api.com.br
  ```

---

### Passo 2: Cadastre a massa de dados no DataPool (`.yml`)
> *Nota: Se o seu teste for apenas um `GET` ou `DELETE` (que não envia body JSON), você pode pular para o Passo 3!*

Se o seu teste for um **`POST`** ou **`PUT`**, abra ou crie um arquivo `.yml` dentro de `src/test/resources/datapool/` (ex: `produtos-datapool.yml` ou `carrinhos-datapool.yml`).

```yaml
# Exemplo 1: Produto com nome dinâmico e números inteiros (sem aspas nos números)
produto_valido:
  nome: "{{DYNAMIC_PRODUCT}}"
  preco: 470
  descricao: "Mouse Gamer RGB Sensor Optico"
  quantidade: 150

# Exemplo 2: Carrinho com lista de produtos usando variável salva na memória ({{ID_PRODUTO}})
carrinho_valido:
  produtos:
    - idProduto: "{{ID_PRODUTO}}"
      quantidade: 2
```

#### 🪄 Tabela de Tags Mágicas e Variáveis de Contexto (`.yml`)
Para evitar que seu teste falhe na segunda execução com erro de *"Registro já cadastrado"*, use estas tags entre aspas no seu arquivo `.yml`:

| Tag no arquivo `.yml` | O que o sistema injeta automaticamente na hora do teste |
| :--- | :--- |
| `"{{DYNAMIC_NAME}}"` | Nome e sobrenome aleatórios em português (ex: *Lucas Almeida*) |
| `"{{DYNAMIC_EMAIL}}"` | E-mail único que nunca se repete (ex: *qa.a1b2c3d4@serverest.dev*) |
| `"{{DYNAMIC_PASSWORD}}"` | Senha forte aleatória |
| `"{{DYNAMIC_PRODUCT}}"` | Nome de produto único (ex: *Cadeira Gamer 9f8a1*) |
| `"{{ID_PRODUTO}}"` *(ou qualquer variável do contexto)* | O valor salvo na memória do cenário (ex: o `_id` de um produto recém-criado!) |

---

### Passo 3: Escreva o seu cenário BDD na pasta `features/`
Abra um arquivo `.feature` existente ou crie um novo dentro de `src/test/resources/features/`.

Todo arquivo `.feature` começa com `# language: pt` na primeira linha e segue sempre a estrutura **`Dado que`** -> **`Quando`** -> **`Entao`**:

```gherkin
# language: pt
@meus_testes @api
Funcionalidade: Validação dos Endpoints da API

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  @cenario_post_produto_201
  Cenário: Cadastrar produto autenticado como administrador (Status 201)
    Dado que estou autenticado com um usuario da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que carrego o payload dinamico "produto_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/produtos"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
```

---

### Passo 4: Execute o seu teste e veja o Relatório
Abra o terminal na pasta raiz do projeto (`Serverest-java`) e rode:

```powershell
# Para rodar APENAS o cenário ou funcionalidade com a sua @tag:
.\mvnw.cmd test "-Dcucumber.filter.tags=@meus_testes"

# Para rodar por domínio (@login, @usuarios, @produtos, @carrinhos):
.\mvnw.cmd test "-Dcucumber.filter.tags=@produtos"

# Para rodar TODOS os 45 cenários do projeto:
.\mvnw.cmd test
```

Assim que terminar:
1. Abra a pasta `target/cucumber-reports/`.
2. Dê dois cliques no arquivo **`cucumber.html`** para abrir o relatório visual no seu navegador!

---

## 📋 3. "Cola Rápida": Todos os Passos Prontos para Copiar e Colar

Use exatamente a escrita abaixo nos seus arquivos `.feature` (só troque os valores que estão entre aspas `" "` ou números):

### 🟢 Passos de Preparação e Autenticação (`Dado que`)

| O que você quer fazer | Passo exato para colocar no `.feature` |
| :--- | :--- |
| **Definir qual API vai testar** | `Dado que utilizo a url da API "API_SERVEREST"` |
| **Limpar a sessão antes da chamada** | `Dado que a sessao da requisicao esta limpa e inicializada` |
| **Autenticar como Admin (`200`/`201`)** | `Dado que estou autenticado com um usuario da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"` |
| **Autenticar como Usuário Comum (`403`)** | `Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"` |
| **Fazer chamada sem Token (`401`)** | `Dado que nao possuo token de autenticacao na sessao` |
| **Carregar massa do arquivo YAML** | `Dado que carrego o payload dinamico "nome_da_massa" do arquivo "nome-arquivo.yml"` |
| **Criar usuário válido e preparar Login (`200`)** | `Dado que possuo um usuario cadastrado a partir da massa "usuario_base_para_login" do arquivo "login-datapool.yml"` |
| **Criar usuário prévio e salvar `_id`** | `Dado que possuo um usuario recem cadastrado na variavel "ID_USUARIO" a partir da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"` |
| **Reutilizar JSON do último usuário (`400`)** | `Dado que reutilizo o mesmo payload do usuario recem cadastrado` |
| **Criar produto prévio e salvar `_id`** | `Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"` |
| **Reutilizar JSON do último produto (`400`)** | `Dado que reutilizo o mesmo payload do produto recem cadastrado` |
| **Criar carrinho prévio e salvar `_id`** | `Dado que possuo um carrinho recem cadastrado na variavel "ID_CARRINHO" a partir da massa "carrinho_valido" do arquivo "carrinhos-datapool.yml"` |
| **Passar um ID na rota (`/{_id}`)** | `Dado que defino o parametro de rota "_id" com o valor "ID_SALVO_OU_FIXO"` |
| **Passar filtros na URL (`?chave=valor`)** | `Dado que defino os parametros de consulta:` *(seguido de tabela `\| chave \| valor \|`)* |
| **Enviar um JSON escrito direto na feature** | `Dado que informo o payload JSON diretamente:` *(seguido de `"""` com o JSON)* |

---

### 🟡 Passos de Ação (`Quando`) — Onde você escolhe o Verbo e a Rota

O passo é **sempre o mesmo**, mudando apenas o método (`GET`, `POST`, `PUT`, `DELETE`) e a rota:

* **POST:** `Quando envio uma requisicao "POST" para a rota "/produtos"`
* **GET (Listagem):** `Quando envio uma requisicao "GET" para a rota "/produtos"`
* **GET por ID:** `Quando envio uma requisicao "GET" para a rota "/produtos/{_id}"`
* **PUT (Edição):** `Quando envio uma requisicao "PUT" para a rota "/produtos/{_id}"`
* **DELETE (Exclusão):** `Quando envio uma requisicao "DELETE" para a rota "/produtos/{_id}"`

---

### 🔵 Passos de Validação (`Entao`) — Onde você valida o Status e o Response

| O que você quer validar | Passo exato para colocar no `.feature` |
| :--- | :--- |
| **Validar o Status Code (`200`, `201`, `400`, `401`, `403`)** | `Entao o status code da resposta deve ser 200` |
| **Validar mensagem ou campo do JSON** | `E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"` |
| **Validar que um campo existe e não é nulo** | `E o campo "quantidade" na resposta JSON nao deve ser nulo` |
| **Confirmar que o Token Bearer foi gerado** | `E o token Bearer deve ser gerado e salvo no contexto` |
| **Validar Contrato JSON Schema** | `E o contrato da resposta deve respeitar o schema "schemas/produtos-lista-200-schema.json"` |
| **Salvar o `_id` retornado para usar depois** | `E armazeno o valor do campo "_id" da resposta na variavel de contexto "ID_CRIADO"` |

---

## ⚠️ 4. Regra Importante da API ServeRest para IDs Inexistentes (`400`)

Na API ServeRest, os endpoints de busca por ID (`GET /usuarios/{_id}`, `GET /produtos/{_id}`, `GET /carrinhos/{_id}`) validam primeiro se o parâmetro `_id` possui **exatamente 16 caracteres alfanuméricos**:
* Se você passar um ID com tamanho diferente de 16 caracteres, a API retorna `"id deve ter exatamente 16 caracteres alfanuméricos"`.
* Portanto, quando quiser testar a mensagem `"Usuário não encontrado"`, `"Produto não encontrado"` ou `"Carrinho não encontrado"`, passe sempre um ID fictício de **16 caracteres** (ex: `"idInexistente123"`, `"idProdInexist123"`, `"idCarrInexist123"`).

---

## ✅ 5. Checklist Rápido antes de abrir seu Pull Request / Commit

1. Colocou `# language: pt` na primeira linha do arquivo `.feature`?
2. Usou as tags dinâmicas (`{{DYNAMIC_EMAIL}}`, `{{DYNAMIC_NAME}}`, `{{DYNAMIC_PRODUCT}}`) no arquivo `.yml` em vez de chumbar dados fixos?
3. Colocou uma `@tag` em cima do seu cenário e testou rodando `.\mvnw.cmd test "-Dcucumber.filter.tags=@sua_tag"`?
4. Todos os passos ficaram verdes (`BUILD SUCCESS`)? Então está pronto para subir! 🚀
