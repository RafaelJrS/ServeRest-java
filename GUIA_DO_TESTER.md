# 📘 Guia Passo a Passo do Tester (Automação de API em BDD)

Bem-vindo(a) à estrutura base de automação de API!  
Este projeto foi construído para que **você não precise programar em Java** para criar novos testes automatizados. Todo o motor de requisições (`GET`, `POST`, `PUT`, `DELETE`), controle de sessão e geração de massa dinâmica já está pronto.

Seu trabalho para automatizar novos cenários consiste apenas em **montar o passo a passo em português (`Dado que` / `Quando` / `Então`)**.

---

## 🗺️ 1. Onde você vai trabalhar? (As 3 Pastas do Tester)

Você **nunca** precisa alterar arquivos dentro de `src/main/java` ou `src/test/java`. Tudo o que você precisa fica em **`src/test/resources/`**:

```text
📁 src/test/resources/
├── 📁 config/
│   └── 📄 application.properties     👉 1. Onde ficam as URLs das APIs (Variáveis)
├── 📁 datapool/
│   ├── 📄 login-datapool.yml         👉 2. Onde você cadastra os JSONs/Massas de envio
│   └── 📄 usuarios-datapool.yml
├── 📁 features/
│   ├── 📄 login.feature              👉 3. Onde você escreve os cenários BDD (Dado/Quando/Então)
│   └── 📄 usuarios_smoke.feature
└── 📁 schemas/
    ├── 📄 login-200-schema.json      👉 (Opcional) Onde ficam os contratos JSON Schema
    └── 📄 login-401-schema.json
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

Se o seu teste for um **`POST`** ou **`PUT`**, abra ou crie um arquivo `.yml` dentro de `src/test/resources/datapool/` (exemplo: `usuarios-datapool.yml`).

Dê um **nome (apelido)** para a sua massa e coloque os campos abaixo dele com 2 espaços de recuo:

```yaml
# Exemplo 1: Massa para cadastro com sucesso (usando dados dinâmicos)
meu_usuario_valido:
  nome: "{{DYNAMIC_NAME}}"
  email: "{{DYNAMIC_EMAIL}}"
  password: "{{DYNAMIC_PASSWORD}}"
  administrador: "true"

# Exemplo 2: Massa para testar erro 400 ou 401 (com dado inválido proposital)
login_senha_errada:
  email: "{{DYNAMIC_EMAIL}}"
  password: ""
```

#### 🪄 Tabela de Tags Mágicas (Gerador de Massa Dinâmica)
Para evitar que seu teste falhe na segunda execução com erro de *"E-mail já cadastrado"*, use estas tags entre aspas no seu arquivo `.yml`. O framework troca cada tag por um dado novo automaticamente na hora do teste:

| Tag no arquivo `.yml` | O que o sistema gera automaticamente |
| :--- | :--- |
| `"{{DYNAMIC_NAME}}"` | Nome e sobrenome aleatórios em português (ex: *Lucas Almeida*) |
| `"{{DYNAMIC_EMAIL}}"` | E-mail único que nunca se repete (ex: *qa.a1b2c3d4@serverest.dev*) |
| `"{{DYNAMIC_PASSWORD}}"` | Senha forte aleatória |
| `"{{DYNAMIC_PRODUCT}}"` | Nome de produto único (ex: *Cadeira Gamer 9f8a1*) |

---

### Passo 3: Escreva o seu cenário BDD na pasta `features/`
Abra um arquivo `.feature` existente ou crie um novo dentro de `src/test/resources/features/` (ex: `meus_testes.feature`).

Todo arquivo `.feature` começa com `# language: pt` na primeira linha e segue sempre a estrutura **`Dado que`** -> **`Quando`** -> **`Entao`**:

```gherkin
# language: pt
@meus_testes @api
Funcionalidade: Validação dos Endpoints da API

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  @cenario_post_201
  Cenário: Cadastrar usuário com sucesso (Status 201)
    Dado que carrego o payload dinamico "meu_usuario_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/usuarios"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"

  @cenario_get_200
  Cenário: Consultar lista de usuários (Status 200)
    Quando envio uma requisicao "GET" para a rota "/usuarios"
    Entao o status code da resposta deve ser 200
```

---

### Passo 4: Execute o seu teste e veja o Relatório
Abra o terminal na pasta raiz do projeto (`Serverest-java`) e rode:

```powershell
# Para rodar APENAS o cenário ou funcionalidade com a sua @tag:
.\mvnw.cmd test "-Dcucumber.filter.tags=@meus_testes"

# Para rodar TODOS os testes do projeto:
.\mvnw.cmd test
```

Assim que terminar:
1. Abra a pasta `target/cucumber-reports/`.
2. Dê dois cliques no arquivo **`cucumber.html`** para abrir o relatório visual no seu navegador!

---

## 📋 3. "Cola Rápida": Todos os Passos Prontos para Copiar e Colar

Use exatamente a escrita abaixo nos seus arquivos `.feature` (só troque os valores que estão entre aspas `" "` ou números):

### 🟢 Passos de Preparação (`Dado que`)

| O que você quer fazer | Passo exato para colocar no `.feature` |
| :--- | :--- |
| **Definir qual API vai testar** | `Dado que utilizo a url da API "API_SERVEREST"` |
| **Limpar a sessão antes da chamada** | `Dado que a sessao da requisicao esta limpa e inicializada` |
| **Carregar massa do arquivo YAML** | `Dado que carrego o payload dinamico "nome_da_massa" do arquivo "nome-arquivo.yml"` |
| **Criar usuário válido e preparar Login (200)** | `Dado que possuo um usuario cadastrado a partir da massa "usuario_base_para_login" do arquivo "login-datapool.yml"` |
| **Passar um ID na rota (ex: `/usuarios/{id}`)** | `Dado que defino o parametro de rota "id" com o valor "ID_SALVO_OU_FIXO"` |
| **Passar filtros na URL (`?chave=valor`)** | `Dado que defino os parametros de consulta:` *(seguido de tabela `\| chave \| valor \|`)* |
| **Enviar um JSON escrito direto na feature** | `Dado que informo o payload JSON diretamente:` *(seguido de `"""` com o JSON)* |

---

### 🟡 Passos de Ação (`Quando`) — Onde você escolhe o Verbo e a Rota

O passo é **sempre o mesmo**, mudando apenas o método (`GET`, `POST`, `PUT`, `DELETE`) e a rota:

* **POST:**
  ```gherkin
  Quando envio uma requisicao "POST" para a rota "/login"
  ```
* **GET (Listagem):**
  ```gherkin
  Quando envio uma requisicao "GET" para a rota "/usuarios"
  ```
* **GET por ID:**
  ```gherkin
  Quando envio uma requisicao "GET" para a rota "/usuarios/{id}"
  ```
* **PUT (Edição):**
  ```gherkin
  Quando envio uma requisicao "PUT" para a rota "/usuarios/{id}"
  ```
* **DELETE (Exclusão):**
  ```gherkin
  Quando envio uma requisicao "DELETE" para a rota "/usuarios/{id}"
  ```

---

### 🔵 Passos de Validação (`Entao`) — Onde você valida o Status e o Response

| O que você quer validar | Passo exato para colocar no `.feature` |
| :--- | :--- |
| **Validar o Status Code (200, 201, 400, 401...)** | `Entao o status code da resposta deve ser 200` |
| **Validar mensagem ou campo do JSON** | `E o campo "message" na resposta JSON deve conter "Login realizado com sucesso"` |
| **Confirmar que o Token Bearer foi gerado** | `E o token Bearer deve ser gerado e salvo no contexto` |
| **Validar Contrato JSON Schema** | `E o contrato da resposta deve respeitar o schema "schemas/login-200-schema.json"` |
| **Salvar o `_id` retornado para usar depois** | `E armazeno o valor do campo "_id" da resposta na variavel de contexto "ID_USUARIO"` |

---

## 💡 4. Exemplos Prontos de Cenários Comuns

### Exemplo A: Login 200 (Sucesso) e Login 401 (Falha)
```gherkin
  @login_200
  Cenário: Realizar login com sucesso na API (Status 200)
    Dado que possuo um usuario cadastrado a partir da massa "usuario_base_para_login" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Login realizado com sucesso"
    E o token Bearer deve ser gerado e salvo no contexto

  @login_401
  Cenário: Impedir login com credenciais invalidas na API (Status 401)
    Dado que carrego o payload dinamico "login_credenciais_invalidas" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Email e/ou senha inválidos"
```

### Exemplo B: Criar um registro no `POST`, capturar o `_id` e deletar no `DELETE`
```gherkin
  @criar_e_deletar
  Cenário: Criar um usuário e excluí-lo em seguida usando o ID gerado
    Dado que carrego o payload dinamico "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/usuarios"
    Entao o status code da resposta deve ser 201
    E armazeno o valor do campo "_id" da resposta na variavel de contexto "ID_CRIADO"

    Dado que a sessao da requisicao esta limpa e inicializada
    E que defino o parametro de rota "id" com o valor "ID_CRIADO"
    Quando envio uma requisicao "DELETE" para a rota "/usuarios/{id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro excluído com sucesso"
```

---

## ✅ 5. Checklist Rápido antes de abrir seu Pull Request / Commit

1. Colocou `# language: pt` na primeira linha do arquivo `.feature`?
2. Usou as tags dinâmicas (`{{DYNAMIC_EMAIL}}`, `{{DYNAMIC_NAME}}`) no arquivo `.yml` em vez de chumbarem e-mails fixos?
3. Colocou uma `@tag` em cima do seu cenário e testou rodando `.\mvnw.cmd test "-Dcucumber.filter.tags=@sua_tag"`?
4. Todos os passos ficaram verdes (`BUILD SUCCESS`)? Então está pronto para subir! 🚀

