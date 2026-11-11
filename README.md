# 🚀 ServeRest API Automation — Arquitetura Modular em Java

Estrutura base de automação de testes de API construída sob os princípios de **Shift-Left Testing**, **Clean Architecture** e **BDD (Behavior-Driven Development)**, utilizando **Java 17+**, **REST-Assured**, **Cucumber** e **PicoContainer**.

---

## 🎯 1. Objetivo e Função do Projeto

O principal objetivo deste projeto é fornecer um **motor universal de testes de API** onde a camada técnica (gerenciamento de sessão HTTP, autenticação, leitura de dados dinâmicos e validação de contratos) está **100% desacoplada** da escrita dos cenários de negócio.

### Qual problema esta arquitetura resolve?
Em projetos tradicionais de automação, cada novo endpoint exige que o QA escreva novas classes Java e novos *Step Definitions*, gerando código duplicado, acoplamento e fragilidade na manutenção.

Nesta arquitetura:
1. **Passos Padronizados e Universais (`Dado que` / `Quando` / `Então`):** Os *Step Definitions* são modulares e genéricos. O QA testa qualquer endpoint (`GET`, `POST`, `PUT`, `DELETE`) reutilizando sempre os mesmos passos e alterando apenas a variável da URL (se mudar de API), a rota, a chave da massa YAML e o Status Code esperado.
2. **Isolamento Total de Sessão (`TestContext` + PicoContainer):** Nenhuma variável global estática do REST-Assured é utilizada. O **PicoContainer** injeta uma instância exclusiva de `TestContext` por cenário, garantindo segurança para execução em paralelo (*Thread-Safe*) e encadeamento limpo de requisições (ex: capturar o Token Bearer no `/login` ou o `_id` no `POST` e utilizá-lo automaticamente no `PUT`/`DELETE`).
3. **Zero Dados Fixos no Código (`DataPool YAML` + `Datafaker`):** Os payloads de envio residem em arquivos YAML externos (`src/test/resources/datapool/*.yml`) com suporte a *placeholders* dinâmicos (`{{DYNAMIC_NAME}}`, `{{DYNAMIC_EMAIL}}`, `{{DYNAMIC_PASSWORD}}`), eliminando falhas intermitentes (*Flaky Tests*) por colisão de massa de dados.
4. **Validação de Contrato Nativa (JSON Schema):** Garante na esteira de desenvolvimento que a estrutura, tipagem e campos obrigatórios do JSON de resposta respeitam o contrato esperado da API.

---

## 🛠️ 2. Stack Tecnológica

| Camada | Tecnologia | Versão | Função no Projeto |
| :--- | :--- | :--- | :--- |
| **Linguagem Base** | Java LTS | 17+ | Tipagem forte e orientação a objetos |
| **Cliente HTTP / DSL** | REST-Assured | 5.4.0 | Construção e envio das requisições REST |
| **Validação de Contrato** | JSON Schema Validator | 5.4.0 | Validação estrutural de contratos JSON |
| **Motor BDD** | Cucumber Java | 7.18.0 | Escrita de cenários executáveis em Gherkin (PT-BR) |
| **Injeção de Dependência** | Cucumber PicoContainer | 7.18.0 | Isolamento do `TestContext` e `ApiClient` por cenário |
| **Test Runner** | JUnit 5 Platform Suite | 5.10.2 | Orquestração e execução da suíte de testes |
| **Leitura de DataPool** | Jackson Databind & YAML | 2.17.1 | Desserialização de massas YAML e serialização JSON |
| **Massa Dinâmica** | Datafaker | 2.2.2 | Geração em memória de nomes, e-mails, senhas e produtos |
| **Asserções** | AssertJ Core | 3.26.0 | Asserções fluentes e mensagens de falha detalhadas |

---

## 🏗️ 3. Arquitetura de Pastas

```text
📁 Serverest-java/
├── 📁 src/main/java/br/com/serverest/            # ⚙️ MOTOR CORE (Não precisa ser alterado para novos testes)
│   ├── 📁 client/
│   │   └── 📄 ApiClient.java                     # Executor HTTP modular e captura automática de Token Bearer
│   ├── 📁 config/
│   │   ├── 📄 ConfigurationManager.java          # Leitor de application.properties e variáveis de ambiente
│   │   └── 📄 RestAssuredConfigManager.java      # Fábrica de RequestSpecification, timeouts e logs de falha
│   ├── 📁 context/
│   │   └── 📄 TestContext.java                   # Sessão isolada do REST-Assured injetada via PicoContainer
│   └── 📁 utils/
│       └── 📄 DataPoolReader.java                # Leitor de arquivos YAML e interpolador de tags dinâmicas
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
│   │   ├── 📄 login-datapool.yml                 # Massa dinâmica do domínio de Login
│   │   └── 📄 usuarios-datapool.yml              # Massa dinâmica do domínio de Usuários
│   ├── 📁 features/
│   │   ├── 📄 login.feature                      # Cenários BDD de Autenticação (200 e 401)
│   │   └── 📄 usuarios_smoke.feature             # Cenário BDD de ciclo CRUD completo de Usuários
│   └── 📁 schemas/
│       ├── 📄 login-200-schema.json              # Contrato JSON Schema para Login 200 OK
│       └── 📄 login-401-schema.json              # Contrato JSON Schema para Login 401 Unauthorized
│
├── 📄 GUIA_DO_TESTER.md                          # 📘 Manual passo a passo para criação de novos cenários BDD
├── 📄 mvnw / mvnw.cmd                            # Maven Wrapper embutido (dispensa instalação manual do Maven)
└── 📄 pom.xml                                    # Gerenciamento de dependências e plugins Maven
```

---

## ▶️ 4. Como Executar os Testes

O projeto já inclui o **Maven Wrapper** (`mvnw` / `mvnw.cmd`), exigindo apenas o **JDK 17+** configurado no ambiente.

### Executar todos os testes da suíte
```powershell
.\mvnw.cmd clean test
```

### Executar apenas cenários específicos por `@tag`
```powershell
# Executar todos os cenários de Login (200 e 401):
.\mvnw.cmd test "-Dcucumber.filter.tags=@login"

# Executar apenas o cenário de Login 200:
.\mvnw.cmd test "-Dcucumber.filter.tags=@login_200"

# Executar o fluxo CRUD completo de Usuários:
.\mvnw.cmd test "-Dcucumber.filter.tags=@usuarios"
```

### Sobrescrever a URL base via linha de comando (ex: CI/CD ou ambiente local)
```powershell
.\mvnw.cmd test "-DAPI_SERVEREST=http://localhost:3000"
```

---

## 📊 5. Relatórios de Execução

Após qualquer execução, os relatórios são gerados automaticamente em:
* **Relatório Visual HTML:** `target/cucumber-reports/cucumber.html` *(basta abrir no navegador)*
* **Relatório JSON (para integração em CI/CD):** `target/cucumber-reports/cucumber.json`

> 💡 Em caso de falha em qualquer cenário, o `CucumberHooks` anexa automaticamente o payload JSON completo retornado pela API dentro do relatório HTML para facilitar o diagnóstico.

---

## 📘 6. Como Criar Novos Cenários de Teste?

Para manter a padronização da escrita em Gherkin (`Dado que` / `Quando` / `Então`) e criar novos testes sem precisar programar em Java, consulte o manual dedicado do projeto:

👉 **[Acesse aqui o GUIA_DO_TESTER.md](./GUIA_DO_TESTER.md)**

