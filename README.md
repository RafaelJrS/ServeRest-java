# 🚀 ServeRest API Automation — Arquitetura Modular em Java

Estrutura completa de automação de testes de API construída sob os princípios de **Shift-Left Testing**, **Clean Architecture** e **BDD (Behavior-Driven Development)**, utilizando **Java 17+**, **REST-Assured**, **Cucumber** e **PicoContainer**.

Atualmente o projeto cobre **100% dos domínios e regras avançadas da API ServeRest (59 cenários automatizados)**: **Login**, **Usuários**, **Produtos** e **Carrinhos**, incluindo **Integridade Referencial entre Domínios**, **Prova Matemática de Estoque (E2E)**, **Análise de Valor Limite**, **Segurança de Token JWT** e **SLA de Performance**.

---

## 🎯 1. Objetivo e Função do Projeto

O principal objetivo deste projeto é fornecer um **motor universal de testes de API** onde a camada técnica (gerenciamento de sessão HTTP, autenticação RBAC, leitura de dados dinâmicos, interpolação de variáveis de contexto e validação de contratos) está **100% desacoplada** da escrita dos cenários de negócio.

### Qual problema esta arquitetura resolve?
Em projetos tradicionais de automação, cada novo endpoint exige que o QA escreva novas classes Java e novos *Step Definitions*, gerando código duplicado, acoplamento e fragilidade na manutenção.

Nesta arquitetura:
1. **Passos Padronizados e Universais (`Dado que` / `Quando` / `Então`):** Os *Step Definitions* são modulares e genéricos. O QA testa qualquer endpoint (`GET`, `POST`, `PUT`, `DELETE`) reutilizando sempre os mesmos passos e alterando apenas a variável da URL (se mudar de API), a rota, a chave da massa YAML e o Status Code esperado.
2. **Isolamento Total de Sessão (`TestContext` + PicoContainer):** Nenhuma variável global estática do REST-Assured é utilizada. O **PicoContainer** injeta uma instância exclusiva de `TestContext` por cenário, garantindo segurança para execução em paralelo (*Thread-Safe*) e encadeamento limpo de requisições (ex: capturar o Token Bearer no `/login` ou o `_id` de um produto e utilizá-lo automaticamente no carrinho).
3. **Zero Dados Fixos no Código (`DataPool YAML` + `Datafaker` + Interpolação de Contexto):** Os payloads de envio residem em arquivos YAML externos (`src/test/resources/datapool/*.yml`) com suporte a *placeholders* dinâmicos (`{{DYNAMIC_NAME}}`, `{{DYNAMIC_EMAIL}}`, `{{DYNAMIC_PASSWORD}}`, `{{DYNAMIC_PRODUCT}}`) e variáveis salvas na sessão (`{{ID_PRODUTO}}`), eliminando falhas intermitentes (*Flaky Tests*) por colisão de massa.
4. **Validação de Contrato Nativa (JSON Schema) e SLA de Tempo de Resposta:** Garante na esteira de desenvolvimento que a estrutura, tipagem e campos obrigatórios do JSON de resposta respeitam os 11 contratos oficiais mapeados em `src/test/resources/schemas/` e que o tempo de resposta atende ao SLA definido.

---

## 📊 2. Cobertura Atual de Testes (59 Cenários)

| Domínio | Arquivo Feature | Tag Principal | Endpoints e Regras Cobertas | Cenários |
| :--- | :--- | :---: | :--- | :---: |
| **Autenticação** | `login.feature` | `@login` | `POST /login` (`200`, `401`, formato de e-mail inválido `400`, campos em branco `400`, SLA e JSON Schema) | **4** |
| **Usuários** | `usuarios_smoke.feature` | `@usuarios` | `GET`, `POST`, `GET/{_id}`, `PUT/{_id}`, `DELETE/{_id}` (`200`, `201`, `400`, flag admin inválida, ID fora de 16 caracteres, edição com e-mail duplicado e bloqueio de exclusão com carrinho ativo) | **14** |
| **Produtos** | `produtos.feature` | `@produtos` | `GET`, `POST`, `GET/{_id}`, `PUT/{_id}`, `DELETE/{_id}` (`200`, `201`, `400`, `401`, `403`, preço zero, quantidade negativa, token JWT adulterado e bloqueio de exclusão de produto em carrinho) | **21** |
| **Carrinhos** | `carrinhos.feature` | `@carrinhos` | `GET`, `POST`, `GET/{_id}`, `DELETE concluir-compra`, `DELETE cancelar-compra` (`200`, `201`, `400`, `401`, quantidade zero, token adulterado e **Prova Matemática de Estoque E2E**) | **20** |
| **TOTAL** | **4 Features** | `@api` | **Todos os 16 endpoints da API ServeRest + 5 Frentes Avançadas de Qualidade** | **59** |

---

## 🛠️ 3. Stack Tecnológica

| Camada | Tecnologia | Versão | Função no Projeto |
| :--- | :--- | :---: | :--- |
| **Linguagem Base** | Java LTS | 17+ | Tipagem forte e orientação a objetos |
| **Cliente HTTP / DSL** | REST-Assured | 5.4.0 | Construção e envio das requisições REST |
| **Validação de Contrato** | JSON Schema Validator | 5.4.0 | Validação estrutural de contratos JSON |
| **Motor BDD** | Cucumber Java | 7.18.0 | Escrita de cenários executáveis em Gherkin (PT-BR) |
| **Injeção de Dependência** | Cucumber PicoContainer | 7.18.0 | Isolamento do `TestContext` e `ApiClient` por cenário |
| **Test Runner** | JUnit 5 Platform Suite | 5.10.2 | Orquestração e execução da suíte de testes |
| **Leitura de DataPool** | Jackson Databind & YAML | 2.17.1 | Desserialização recursiva de massas YAML e serialização JSON |
| **Massa Dinâmica** | Datafaker | 2.2.2 | Geração em memória de nomes, e-mails, senhas e produtos |
| **Asserções** | AssertJ Core | 3.26.0 | Asserções fluentes e mensagens de falha detalhadas |

---

## 🏗️ 4. Arquitetura de Pastas

```text
📁 Serverest-java/
├── 📁 src/main/java/br/com/serverest/            # ⚙️ MOTOR CORE (Não precisa ser alterado para novos testes)
│   ├── 📁 client/
│   │   └── 📄 ApiClient.java                     # Executor HTTP modular, captura de Token Bearer e setups atômicos
│   ├── 📁 config/
│   │   ├── 📄 ConfigurationManager.java          # Leitor de application.properties e variáveis de ambiente
│   │   └── 📄 RestAssuredConfigManager.java      # Fábrica de RequestSpecification, timeouts e logs de falha
│   ├── 📁 context/
│   │   └── 📄 TestContext.java                   # Sessão isolada do REST-Assured injetada via PicoContainer
│   └── 📁 utils/
│       └── 📄 DataPoolReader.java                # Leitor recursivo de YAML + Interpolador Datafaker e TestContext
│
├── 📁 src/test/java/br/com/serverest/            # 🧪 CAMADA DE ORQUESTRAÇÃO BDD
│   ├── 📁 hooks/
│   │   └── 📄 CucumberHooks.java                 # Setup/Teardown por cenário e anexo de JSON no relatório em falhas
│   ├── 📁 runners/
│   │   └── 📄 TestRunner.java                    # Runner JUnit 5 Platform Suite
│   └── 📁 steps/
│       └── 📄 GenericApiSteps.java               # Step Definitions universais (Dado que / Quando / Então)
│
├── 📁 src/test/resources/                        # 📝 ÁREA DE TRABALHO DO QA / TESTER
│   ├── 📁 config/
│   │   └── 📄 application.properties             # Variáveis de URL das APIs (Suporte Multi-API) e Timeouts
│   ├── 📁 datapool/
│   │   ├── 📄 login-datapool.yml                 # Massa dinâmica e limites do domínio de Login
│   │   ├── 📄 usuarios-datapool.yml              # Massa dinâmica e limites do domínio de Usuários
│   │   ├── 📄 produtos-datapool.yml              # Massa dinâmica e limites do domínio de Produtos
│   │   └── 📄 carrinhos-datapool.yml             # Massa dinâmica do domínio de Carrinhos (com listas e {{ID_PRODUTO}})
│   ├── 📁 features/
│   │   ├── 📄 login.feature                      # 4 cenários BDD de Autenticação (200, 401 e 400)
│   │   ├── 📄 usuarios_smoke.feature             # 14 cenários BDD de Usuários
│   │   ├── 📄 produtos.feature                   # 21 cenários BDD de Produtos (incluindo RBAC 401/403 e limites)
│   │   └── 📄 carrinhos.feature                  # 20 cenários BDD de Carrinhos (incluindo E2E de Estoque)
│   └── 📁 schemas/
│       └── 📄 *-schema.json                      # 11 contratos JSON Schema (Login, Usuários, Produtos e Carrinhos)
│
├── 📄 GUIA_DO_TESTER.md                          # 📘 Manual passo a passo para criação de novos cenários BDD
├── 📄 mvnw / mvnw.cmd                            # Maven Wrapper embutido (dispensa instalação manual do Maven)
└── 📄 pom.xml                                    # Gerenciamento de dependências e plugins Maven
```

---

## ▶️ 5. Como Executar os Testes

O projeto já inclui o **Maven Wrapper** (`mvnw` / `mvnw.cmd`), exigindo apenas o **JDK 17+** configurado no ambiente.

### Executar todos os 59 cenários da suíte
```powershell
.\mvnw.cmd clean test
```

### Executar por domínio ou cenário específico via `@tag`
```powershell
# Domínio de Login (4 cenários):
.\mvnw.cmd test "-Dcucumber.filter.tags=@login"

# Domínio de Usuários (14 cenários):
.\mvnw.cmd test "-Dcucumber.filter.tags=@usuarios"

# Domínio de Produtos (21 cenários):
.\mvnw.cmd test "-Dcucumber.filter.tags=@produtos"

# Domínio de Carrinhos (20 cenários):
.\mvnw.cmd test "-Dcucumber.filter.tags=@carrinhos"
```

### Sobrescrever a URL base via linha de comando (ex: CI/CD ou ambiente local)
```powershell
.\mvnw.cmd test "-DAPI_SERVEREST=http://localhost:3000"
```

---

## 📊 6. Relatórios de Execução

Após qualquer execução, os relatórios são gerados automaticamente em:
* **Relatório Visual HTML:** `target/cucumber-reports/cucumber.html` *(basta abrir no navegador)*
* **Relatório JSON (para integração em CI/CD):** `target/cucumber-reports/cucumber.json`

> 💡 Em caso de falha em qualquer cenário, o `CucumberHooks` anexa automaticamente o payload JSON completo retornado pela API dentro do relatório HTML para facilitar o diagnóstico.

---

## 📘 7. Como Criar Novos Cenários de Teste?

Para manter a padronização da escrita em Gherkin (`Dado que` / `Quando` / `Então`) e criar novos testes sem precisar programar em Java, consulte o manual dedicado do projeto:

👉 **[Acesse aqui o GUIA_DO_TESTER.md](./GUIA_DO_TESTER.md)**
