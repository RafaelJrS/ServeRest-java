# 📊 Relatório Final de Execução de Testes Automatizados (QA Sign-Off)

**Projeto:** ServeRest API Automation (`br.com.serverest:serverest-api-automation`)  
**Ambiente Alvo:** `https://serverest.dev`  
**Stack:** Java 17 + REST-Assured 5.4.0 + Cucumber 7.18.0 + PicoContainer + JUnit 5 Platform  
**Última Execução Regressiva Completa:** `14/11/2026 às 22:24:19` (59/59 PASS)  
**Status Global da Release:** 🟢 **APROVADO (100% PASS — 59/59 Cenários)**

---

## 📈 1. Sumário Executivo e Indicadores de Qualidade

| Indicador (KPI) | Resultado |
| :--- | :---: |
| **Total de Domínios Mapeados** | **4 / 4 (100%)** (`Login`, `Usuários`, `Produtos`, `Carrinhos`) |
| **Total de Endpoints Cobertos** | **16 / 16 (100%)** |
| **Total de Contratos JSON Schema Validados** | **11 Schemas** |
| **Total de Cenários Executados** | **59** |
| **Cenários Aprovados (PASS)** | **59 (100%)** ✅ |
| **Cenários Reprovados (FAIL / ERROR)** | **0 (0%)** |
| **SLA de Tempo de Resposta (< 5000 ms)** | **100% Dentro do SLA** ⚡ |

---

## 🛡️ 2. Distribuição por Frentes de Qualidade (Shift-Left Testing)

| Frente de Qualidade | Objetivo Técnico | Qtd. Cenários | Status |
| :--- | :--- | :---: | :---: |
| **1. Funcional (Caminho Feliz / CRUD)** | Garantir o funcionamento básico de criação, leitura, listagem com filtros, edição (`PUT` / Upsert) e exclusão. | **19** | ✅ **PASS** |
| **2. Contrato (JSON Schema Validation)** | Garantir tipagem, campos obrigatórios e proibição de propriedades extras (`additionalProperties: false`). | **14** *(embutidos)* | ✅ **PASS** |
| **3. Regras de Negócio e Duplicidade (`400`)** | Impedir cadastros duplicados (e-mail, nome de produto, mais de 1 carrinho, item repetido no carrinho) e recursos inexistentes. | **13** | ✅ **PASS** |
| **4. Segurança, RBAC e Token JWT (`401` / `403`)** | Validar bloqueio sem token (`401`), com token JWT adulterado (`401`) e com usuário comum tentando acessar rotas de administrador (`403`). | **14** | ✅ **PASS** |
| **5. Integridade Referencial entre Domínios (`400`)** | Impedir exclusão de usuário que possui carrinho ativo e de produto que faz parte de um carrinho ativo, ou edição para e-mail alheio. | **4** | ✅ **PASS** |
| **6. Análise de Valor Limite e Entrada (`400`)** | Validar formatos inválidos de e-mail, campos em branco, flag booleana inválida, IDs fora de 16 caracteres, preços `<= 0` e quantidades `<= 0`. | **7** | ✅ **PASS** |
| **7. Prova Matemática de Estoque (E2E)** | Validar baixa real no saldo do produto ao criar carrinho (`150 -> 148`), reabastecimento ao cancelar (`148 -> 150`) e baixa definitiva ao concluir. | **2** | ✅ **PASS** |

---

## 📋 3. Catálogo Completo dos 59 Cenários Executados

### 🔐 Domínio 1: Autenticação (`login.feature` — 4 Cenários)

| ID | Tag Cucumber | Verbo & Rota | Cenário BDD Executado | Frente de Qualidade | HTTP | Status |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: |
| **CT-01** | `@login_200` | `POST /login` | Realizar login com sucesso na API + Token Bearer + Contrato Schema + SLA | Funcional / Contrato / SLA | `200` | ✅ **PASS** |
| **CT-02** | `@login_401` | `POST /login` | Impedir login com credenciais inválidas na API + Contrato Schema + SLA | Segurança / Exceção | `401` | ✅ **PASS** |
| **CT-03** | `@login_email_invalido_400` | `POST /login` | Impedir login com formato de e-mail inválido (sem `@`) | Valor Limite / Entrada | `400` | ✅ **PASS** |
| **CT-04** | `@login_campos_branco_400` | `POST /login` | Impedir login com campos de e-mail e senha em branco (`""`) | Valor Limite / Entrada | `400` | ✅ **PASS** |

---

### 👤 Domínio 2: Usuários (`usuarios_smoke.feature` — 14 Cenários)

| ID | Tag Cucumber | Verbo & Rota | Cenário BDD Executado | Frente de Qualidade | HTTP | Status |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: |
| **CT-05** | `@get_usuarios_200` | `GET /usuarios` | Listar todos os usuários cadastrados na API + Contrato Schema + SLA | Funcional / Contrato / SLA | `200` | ✅ **PASS** |
| **CT-06** | `@get_usuarios_filtro_200` | `GET /usuarios?administrador=true` | Filtrar usuários administradores cadastrados via Query Param + Schema | Funcional / Contrato | `200` | ✅ **PASS** |
| **CT-07** | `@post_usuarios_201` | `POST /usuarios` | Cadastrar um novo usuário administrador com sucesso + Schema + SLA | Funcional / Contrato / SLA | `201` | ✅ **PASS** |
| **CT-08** | `@post_usuarios_400` | `POST /usuarios` | Impedir cadastro de usuário com e-mail já utilizado | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-09** | `@post_usuarios_flag_admin_invalida_400` | `POST /usuarios` | Impedir cadastro de usuário com campo `administrador` inválido (`"sim"`) | Valor Limite / Entrada | `400` | ✅ **PASS** |
| **CT-10** | `@get_usuario_id_200` | `GET /usuarios/{_id}` | Buscar usuário existente por ID com sucesso + Contrato Schema | Funcional / Contrato | `200` | ✅ **PASS** |
| **CT-11** | `@get_usuario_id_400` | `GET /usuarios/{_id}` | Buscar usuário por ID inexistente de 16 caracteres (`Usuário não encontrado`) | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-12** | `@get_usuario_id_tamanho_invalido_400` | `GET /usuarios/{_id}` | Impedir busca de usuário com ID fora do padrão de 16 caracteres | Valor Limite / Entrada | `400` | ✅ **PASS** |
| **CT-13** | `@put_usuario_200` | `PUT /usuarios/{_id}` | Editar dados de um usuário existente com sucesso | Funcional (CRUD) | `200` | ✅ **PASS** |
| **CT-14** | `@put_usuario_201` | `PUT /usuarios/{_id}` | Cadastrar usuário via `PUT` (Upsert) ao informar ID inexistente + Schema | Funcional / Contrato | `201` | ✅ **PASS** |
| **CT-15** | `@put_usuario_email_duplicado_400` | `PUT /usuarios/{_id}` | Impedir edição de usuário informando e-mail de outro usuário já existente | Integridade Referencial | `400` | ✅ **PASS** |
| **CT-16** | `@delete_usuario_200` | `DELETE /usuarios/{_id}` | Excluir um usuário existente com sucesso | Funcional (CRUD) | `200` | ✅ **PASS** |
| **CT-17** | `@delete_usuario_inexistente_200` | `DELETE /usuarios/{_id}` | Tentar excluir um usuário com ID inexistente (`Nenhum registro excluído`) | Regra de Negócio | `200` | ✅ **PASS** |
| **CT-18** | `@delete_usuario_com_carrinho_400` | `DELETE /usuarios/{_id}` | Impedir exclusão de usuário que possui carrinho cadastrado | Integridade Referencial | `400` | ✅ **PASS** |

---

### 📦 Domínio 3: Produtos (`produtos.feature` — 21 Cenários)

| ID | Tag Cucumber | Verbo & Rota | Cenário BDD Executado | Frente de Qualidade | HTTP | Status |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: |
| **CT-19** | `@get_produtos_200` | `GET /produtos` | Listar todos os produtos cadastrados na API + Contrato Schema + SLA | Funcional / Contrato / SLA | `200` | ✅ **PASS** |
| **CT-20** | `@get_produtos_filtro_200` | `GET /produtos?preco=470` | Filtrar produtos cadastrados por preço via Query Param + Schema | Funcional / Contrato | `200` | ✅ **PASS** |
| **CT-21** | `@post_produtos_201` | `POST /produtos` | Cadastrar novo produto autenticado como administrador + Schema + SLA | Funcional / Contrato / SLA | `201` | ✅ **PASS** |
| **CT-22** | `@post_produtos_400` | `POST /produtos` | Impedir cadastro de produto com nome já utilizado | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-23** | `@post_produtos_preco_zero_400` | `POST /produtos` | Impedir cadastro de produto com preço igual a zero (`preco: 0`) | Valor Limite / Entrada | `400` | ✅ **PASS** |
| **CT-24** | `@post_produtos_quantidade_negativa_400` | `POST /produtos` | Impedir cadastro de produto com quantidade negativa (`quantidade: -1`) | Valor Limite / Entrada | `400` | ✅ **PASS** |
| **CT-25** | `@post_produtos_401` | `POST /produtos` | Impedir cadastro de produto sem Token Bearer de autenticação | Segurança (Sem Token) | `401` | ✅ **PASS** |
| **CT-26** | `@post_produtos_token_adulterado_401` | `POST /produtos` | Impedir cadastro de produto com Token Bearer adulterado na sessão | Segurança (JWT Forjado) | `401` | ✅ **PASS** |
| **CT-27** | `@post_produtos_403` | `POST /produtos` | Impedir cadastro de produto com usuário que não é administrador | Segurança (RBAC Admin) | `403` | ✅ **PASS** |
| **CT-28** | `@get_produto_id_200` | `GET /produtos/{_id}` | Buscar produto existente por ID com sucesso + Contrato Schema | Funcional / Contrato | `200` | ✅ **PASS** |
| **CT-29** | `@get_produto_id_400` | `GET /produtos/{_id}` | Buscar produto por ID inexistente de 16 caracteres (`Produto não encontrado`) | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-30** | `@put_produto_200` | `PUT /produtos/{_id}` | Editar dados de um produto existente com sucesso | Funcional (CRUD) | `200` | ✅ **PASS** |
| **CT-31** | `@put_produto_201` | `PUT /produtos/{_id}` | Cadastrar produto via `PUT` (Upsert) ao informar ID inexistente + Schema | Funcional / Contrato | `201` | ✅ **PASS** |
| **CT-32** | `@put_produto_400` | `PUT /produtos/{_id}` | Impedir edição de produto utilizando nome de outro produto já existente | Integridade Referencial | `400` | ✅ **PASS** |
| **CT-33** | `@put_produto_401` | `PUT /produtos/{_id}` | Impedir edição de produto sem Token Bearer de autenticação | Segurança (Sem Token) | `401` | ✅ **PASS** |
| **CT-34** | `@put_produto_403` | `PUT /produtos/{_id}` | Impedir edição de produto com usuário que não é administrador | Segurança (RBAC Admin) | `403` | ✅ **PASS** |
| **CT-35** | `@delete_produto_200` | `DELETE /produtos/{_id}` | Excluir um produto existente com sucesso | Funcional (CRUD) | `200` | ✅ **PASS** |
| **CT-36** | `@delete_produto_inexistente_200` | `DELETE /produtos/{_id}` | Tentar excluir um produto com ID inexistente (`Nenhum registro excluído`) | Regra de Negócio | `200` | ✅ **PASS** |
| **CT-37** | `@delete_produto_em_carrinho_400` | `DELETE /produtos/{_id}` | Impedir exclusão de produto que faz parte de um carrinho ativo | Integridade Referencial | `400` | ✅ **PASS** |
| **CT-38** | `@delete_produto_401` | `DELETE /produtos/{_id}` | Impedir exclusão de produto sem Token Bearer de autenticação | Segurança (Sem Token) | `401` | ✅ **PASS** |
| **CT-39** | `@delete_produto_403` | `DELETE /produtos/{_id}` | Impedir exclusão de produto com usuário que não é administrador | Segurança (RBAC Admin) | `403` | ✅ **PASS** |

---

### 🛒 Domínio 4: Carrinhos (`carrinhos.feature` — 20 Cenários)

| ID | Tag Cucumber | Verbo & Rota | Cenário BDD Executado | Frente de Qualidade | HTTP | Status |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: |
| **CT-40** | `@get_carrinhos_200` | `GET /carrinhos` | Listar todos os carrinhos cadastrados na API + Contrato Schema + SLA | Funcional / Contrato / SLA | `200` | ✅ **PASS** |
| **CT-41** | `@get_carrinhos_filtro_200` | `GET /carrinhos?quantidadeTotal=2` | Filtrar carrinhos por `quantidadeTotal` via Query Param + Schema | Funcional / Contrato | `200` | ✅ **PASS** |
| **CT-42** | `@post_carrinhos_201` | `POST /carrinhos` | Cadastrar um novo carrinho com sucesso + Contrato Schema + SLA | Funcional / Contrato / SLA | `201` | ✅ **PASS** |
| **CT-43** | `@post_carrinhos_duplicado_400` | `POST /carrinhos` | Impedir cadastro de carrinho com produto duplicado na lista | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-44** | `@post_carrinhos_mais_de_um_400` | `POST /carrinhos` | Impedir cadastro de mais de 1 carrinho ativo para o mesmo usuário | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-45** | `@post_carrinhos_produto_inexistente_400` | `POST /carrinhos` | Impedir cadastro de carrinho com `idProduto` inexistente | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-46** | `@post_carrinhos_estoque_insuficiente_400` | `POST /carrinhos` | Impedir cadastro quando produto não possui estoque suficiente | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-47** | `@post_carrinhos_quantidade_zero_400` | `POST /carrinhos` | Impedir cadastro de carrinho com quantidade igual a zero (`quantidade: 0`) | Valor Limite / Entrada | `400` | ✅ **PASS** |
| **CT-48** | `@post_carrinhos_401` | `POST /carrinhos` | Impedir cadastro de carrinho sem Token Bearer de autenticação | Segurança (Sem Token) | `401` | ✅ **PASS** |
| **CT-49** | `@post_carrinhos_token_adulterado_401` | `POST /carrinhos` | Impedir cadastro de carrinho com Token Bearer adulterado na sessão | Segurança (JWT Forjado) | `401` | ✅ **PASS** |
| **CT-50** | `@get_carrinho_id_200` | `GET /carrinhos/{_id}` | Buscar carrinho existente por ID com sucesso + Contrato Schema | Funcional / Contrato | `200` | ✅ **PASS** |
| **CT-51** | `@get_carrinho_id_400` | `GET /carrinhos/{_id}` | Buscar carrinho por ID inexistente de 16 caracteres (`Carrinho não encontrado`) | Regra de Negócio | `400` | ✅ **PASS** |
| **CT-52** | `@delete_concluir_compra_200` | `DELETE /carrinhos/concluir-compra` | Concluir compra excluindo o carrinho ativo com sucesso | Funcional (Checkout) | `200` | ✅ **PASS** |
| **CT-53** | `@delete_concluir_compra_sem_carrinho_200` | `DELETE /carrinhos/concluir-compra` | Tentar concluir compra com usuário que não possui carrinho ativo | Regra de Negócio | `200` | ✅ **PASS** |
| **CT-54** | `@delete_concluir_compra_401` | `DELETE /carrinhos/concluir-compra` | Impedir conclusão de compra sem Token Bearer de autenticação | Segurança (Sem Token) | `401` | ✅ **PASS** |
| **CT-55** | `@delete_cancelar_compra_200` | `DELETE /carrinhos/cancelar-compra` | Cancelar compra excluindo o carrinho e reabastecendo o estoque | Funcional (Cancelamento) | `200` | ✅ **PASS** |
| **CT-56** | `@delete_cancelar_compra_sem_carrinho_200` | `DELETE /carrinhos/cancelar-compra` | Tentar cancelar compra com usuário que não possui carrinho ativo | Regra de Negócio | `200` | ✅ **PASS** |
| **CT-57** | `@delete_cancelar_compra_401` | `DELETE /carrinhos/cancelar-compra` | Impedir cancelamento de compra sem Token Bearer de autenticação | Segurança (Sem Token) | `401` | ✅ **PASS** |
| **CT-58** | `@e2e_estoque_cancelar_compra_200` | `POST /carrinhos` -> `GET /produtos/{_id}` -> `DELETE /cancelar-compra` | Validar baixa matemática de estoque (`150 -> 148`) e reabastecimento (`148 -> 150`) | Prova Matemática E2E | `200` | ✅ **PASS** |
| **CT-59** | `@e2e_estoque_concluir_compra_200` | `POST /carrinhos` -> `DELETE /concluir-compra` -> `GET /produtos/{_id}` | Validar baixa definitiva de estoque no produto após concluir compra (`150 -> 148`) | Prova Matemática E2E | `200` | ✅ **PASS** |

---

## ✅ 4. Parecer Técnico Final (QA Sign-Off)

A suíte automatizada encontra-se **100% operacional, idempotente e livre de dados estáticos (*Zero Flaky Tests*)**, com isolamento completo de sessão via **PicoContainer (`TestContext`)** e cobertura integral dos contratos e regras de negócio da API ServeRest.

---

## 🔄 5. Governança de Atualização Contínua e Regressivo

1. **Inclusão de Novos Cenários:** Sempre que um novo cenário for criado e validado com sucesso (`PASS`), ele é automaticamente catalogado na estrutura deste relatório (`CT-XX`) e nos contadores das frentes de qualidade.
2. **Gatilho Interativo de Regressão:** Após validar e registrar o novo cenário no relatório, o agente consulta o responsável:
   - **`Sim` (Rodar Regressivo Completo):** Executa toda a suíte (`.\mvnw.cmd test`) e o hook `@AfterAll` (`RegressionReportUpdater`) grava automaticamente a **data, hora exata e placar final** no cabeçalho deste documento.
   - **`Não` (Finalizar Execução Atual):** Mantém o novo cenário catalogado na tabela e encerra a tarefa imediatamente sem disparar a bateria completa.
