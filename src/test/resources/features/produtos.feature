# language: pt
@produtos @api @regressivo
Funcionalidade: Gestão de Produtos na API (/produtos e /produtos/{_id})
  Como um QA utilizando a estrutura padronizada Multi-API
  Quero exercitar todos os endpoints de Produtos (GET, POST, GET/{_id}, PUT/{_id}, DELETE/{_id})
  Para validar as regras de negócio de sucesso, permissões (401/403), exceções (400) e contratos da API ServeRest

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  # =========================================================================
  # 1. GET /produtos - Listar produtos cadastrados
  # =========================================================================
  @get_produtos_200
  Cenário: Listar todos os produtos cadastrados na API (GET /produtos - Status 200)
    Quando envio uma requisicao "GET" para a rota "/produtos"
    Entao o status code da resposta deve ser 200
    E o campo "quantidade" na resposta JSON nao deve ser nulo
    E o contrato da resposta deve respeitar o schema "schemas/produtos-lista-200-schema.json"

  @get_produtos_filtro_200
  Cenário: Filtrar produtos cadastrados por preco via Query Param (GET /produtos - Status 200)
    Dado que defino os parametros de consulta:
      | preco | 470 |
    Quando envio uma requisicao "GET" para a rota "/produtos"
    Entao o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "schemas/produtos-lista-200-schema.json"

  # =========================================================================
  # 2. POST /produtos - Cadastrar produto (201, 400, 401, 403)
  # =========================================================================
  @post_produtos_201
  Cenário: Cadastrar um novo produto com sucesso autenticado como administrador (POST /produtos - Status 201)
    Dado que estou autenticado com um usuario da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que carrego o payload dinamico "produto_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/produtos"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
    E o campo "_id" na resposta JSON nao deve ser nulo
    E o contrato da resposta deve respeitar o schema "schemas/produtos-post-201-schema.json"

  @post_produtos_400
  Cenário: Impedir cadastro de produto com nome ja utilizado (POST /produtos - Status 400)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que reutilizo o mesmo payload do produto recem cadastrado
    Quando envio uma requisicao "POST" para a rota "/produtos"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Já existe produto com esse nome"

  @post_produtos_401
  Cenário: Impedir cadastro de produto sem Token Bearer de autenticacao (POST /produtos - Status 401)
    Dado que nao possuo token de autenticacao na sessao
    E que carrego o payload dinamico "produto_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/produtos"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  @post_produtos_403
  Cenário: Impedir cadastro de produto com usuario que nao e administrador (POST /produtos - Status 403)
    Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que carrego o payload dinamico "produto_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/produtos"
    Entao o status code da resposta deve ser 403
    E o campo "message" na resposta JSON deve conter "Rota exclusiva para administradores"

  # =========================================================================
  # 3. GET /produtos/{_id} - Buscar produto por ID (200, 400)
  # =========================================================================
  @get_produto_id_200
  Cenário: Buscar produto existente por ID com sucesso (GET /produtos/{_id} - Status 200)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_PRODUTO"
    Quando envio uma requisicao "GET" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "schemas/produtos-id-200-schema.json"

  @get_produto_id_400
  Cenário: Buscar produto por ID inexistente (GET /produtos/{_id} - Status 400)
    Dado que defino o parametro de rota "_id" com o valor "idProdInexist123"
    Quando envio uma requisicao "GET" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Produto não encontrado"

  # =========================================================================
  # 4. PUT /produtos/{_id} - Editar produto (200, 201, 400, 401, 403)
  # =========================================================================
  @put_produto_200
  Cenário: Editar dados de um produto existente com sucesso (PUT /produtos/{_id} - Status 200)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_PRODUTO"
    E que carrego o payload dinamico "produto_atualizado_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro alterado com sucesso"

  @put_produto_201
  Cenário: Cadastrar produto via PUT ao informar um ID inexistente (PUT /produtos/{_id} - Status 201)
    Dado que estou autenticado com um usuario da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "idProdNovoPut123"
    E que carrego o payload dinamico "produto_via_put_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
    E o contrato da resposta deve respeitar o schema "schemas/produtos-post-201-schema.json"

  @put_produto_400
  Cenário: Impedir edicao de produto utilizando nome de outro produto ja existente (PUT /produtos/{_id} - Status 400)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO_A" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que possuo um produto recem cadastrado na variavel "ID_PRODUTO_B" a partir da massa "produto_atualizado_valido" do arquivo "produtos-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_PRODUTO_A"
    E que reutilizo o mesmo payload do produto recem cadastrado
    Quando envio uma requisicao "PUT" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Já existe produto com esse nome"

  @put_produto_401
  Cenário: Impedir edicao de produto sem Token Bearer de autenticacao (PUT /produtos/{_id} - Status 401)
    Dado que nao possuo token de autenticacao na sessao
    E que defino o parametro de rota "_id" com o valor "idProdSemTokn123"
    E que carrego o payload dinamico "produto_atualizado_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  @put_produto_403
  Cenário: Impedir edicao de produto com usuario que nao e administrador (PUT /produtos/{_id} - Status 403)
    Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "idProdSemAdmn123"
    E que carrego o payload dinamico "produto_atualizado_valido" do arquivo "produtos-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 403
    E o campo "message" na resposta JSON deve conter "Rota exclusiva para administradores"

  # =========================================================================
  # 5. DELETE /produtos/{_id} - Excluir produto (200, 401, 403)
  # =========================================================================
  @delete_produto_200
  Cenário: Excluir um produto existente com sucesso (DELETE /produtos/{_id} - Status 200)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_PRODUTO"
    Quando envio uma requisicao "DELETE" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro excluído com sucesso"

  @delete_produto_inexistente_200
  Cenário: Tentar excluir um produto com ID inexistente (DELETE /produtos/{_id} - Status 200)
    Dado que estou autenticado com um usuario da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "idProdDelInex123"
    Quando envio uma requisicao "DELETE" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Nenhum registro excluído"

  @delete_produto_401
  Cenário: Impedir exclusao de produto sem Token Bearer de autenticacao (DELETE /produtos/{_id} - Status 401)
    Dado que nao possuo token de autenticacao na sessao
    E que defino o parametro de rota "_id" com o valor "idProdDelInex123"
    Quando envio uma requisicao "DELETE" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  @delete_produto_403
  Cenário: Impedir exclusao de produto com usuario que nao e administrador (DELETE /produtos/{_id} - Status 403)
    Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "idProdDelInex123"
    Quando envio uma requisicao "DELETE" para a rota "/produtos/{_id}"
    Entao o status code da resposta deve ser 403
    E o campo "message" na resposta JSON deve conter "Rota exclusiva para administradores"
