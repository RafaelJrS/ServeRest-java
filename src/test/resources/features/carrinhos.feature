# language: pt
@carrinhos @api @regressivo
Funcionalidade: Gestão de Carrinhos na API (/carrinhos, /carrinhos/{_id}, /concluir-compra e /cancelar-compra)
  Como um QA utilizando a estrutura padronizada Multi-API
  Quero exercitar todos os endpoints de Carrinhos (GET, POST, GET/{_id}, DELETE concluir-compra e DELETE cancelar-compra)
  Para validar as regras de negócio de sucesso, controle de estoque, permissões (401), exceções (400) e contratos da API ServeRest

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  # =========================================================================
  # 1. GET /carrinhos - Listar carrinhos cadastrados (200)
  # =========================================================================
  @get_carrinhos_200
  Cenário: Listar todos os carrinhos cadastrados na API (GET /carrinhos - Status 200)
    Quando envio uma requisicao "GET" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 200
    E o campo "quantidade" na resposta JSON nao deve ser nulo
    E o contrato da resposta deve respeitar o schema "schemas/carrinhos-lista-200-schema.json"

  @get_carrinhos_filtro_200
  Cenário: Filtrar carrinhos cadastrados por quantidadeTotal via Query Param (GET /carrinhos - Status 200)
    Dado que defino os parametros de consulta:
      | quantidadeTotal | 2 |
    Quando envio uma requisicao "GET" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "schemas/carrinhos-lista-200-schema.json"

  # =========================================================================
  # 2. POST /carrinhos - Cadastrar carrinho (201, 400x4, 401)
  # =========================================================================
  @post_carrinhos_201
  Cenário: Cadastrar um novo carrinho com sucesso (POST /carrinhos - Status 201)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que carrego o payload dinamico "carrinho_valido" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
    E o campo "_id" na resposta JSON nao deve ser nulo
    E o contrato da resposta deve respeitar o schema "schemas/carrinhos-post-201-schema.json"

  @post_carrinhos_duplicado_400
  Cenário: Impedir cadastro de carrinho com produto duplicado (POST /carrinhos - Status 400)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que carrego o payload dinamico "carrinho_produto_duplicado" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Não é permitido possuir produto duplicado"

  @post_carrinhos_mais_de_um_400
  Cenário: Impedir cadastro de mais de um carrinho para o mesmo usuario (POST /carrinhos - Status 400)
    Dado que possuo um carrinho recem cadastrado na variavel "ID_CARRINHO" a partir da massa "carrinho_valido" do arquivo "carrinhos-datapool.yml"
    E que carrego o payload dinamico "carrinho_valido" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Não é permitido ter mais de 1 carrinho"

  @post_carrinhos_produto_inexistente_400
  Cenário: Impedir cadastro de carrinho com produto inexistente (POST /carrinhos - Status 400)
    Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que carrego o payload dinamico "carrinho_produto_inexistente" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Produto não encontrado"

  @post_carrinhos_estoque_insuficiente_400
  Cenário: Impedir cadastro de carrinho quando produto nao possui estoque suficiente (POST /carrinhos - Status 400)
    Dado que possuo um produto recem cadastrado na variavel "ID_PRODUTO" a partir da massa "produto_valido" do arquivo "produtos-datapool.yml"
    E que carrego o payload dinamico "carrinho_estoque_insuficiente" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Produto não possui quantidade suficiente"

  @post_carrinhos_401
  Cenário: Impedir cadastro de carrinho sem Token Bearer de autenticacao (POST /carrinhos - Status 401)
    Dado que nao possuo token de autenticacao na sessao
    E que carrego o payload dinamico "carrinho_produto_inexistente" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/carrinhos"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  # =========================================================================
  # 3. GET /carrinhos/{_id} - Buscar carrinho por ID (200, 400)
  # =========================================================================
  @get_carrinho_id_200
  Cenário: Buscar carrinho existente por ID com sucesso (GET /carrinhos/{_id} - Status 200)
    Dado que possuo um carrinho recem cadastrado na variavel "ID_CARRINHO" a partir da massa "carrinho_valido" do arquivo "carrinhos-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_CARRINHO"
    Quando envio uma requisicao "GET" para a rota "/carrinhos/{_id}"
    Entao o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "schemas/carrinhos-id-200-schema.json"

  @get_carrinho_id_400
  Cenário: Buscar carrinho por ID inexistente (GET /carrinhos/{_id} - Status 400)
    Dado que defino o parametro de rota "_id" com o valor "idCarrInexist123"
    Quando envio uma requisicao "GET" para a rota "/carrinhos/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Carrinho não encontrado"

  # =========================================================================
  # 4. DELETE /carrinhos/concluir-compra - Excluir carrinho ao concluir compra (200, 401)
  # =========================================================================
  @delete_concluir_compra_200
  Cenário: Concluir compra excluindo o carrinho ativo com sucesso (DELETE /carrinhos/concluir-compra - Status 200)
    Dado que possuo um carrinho recem cadastrado na variavel "ID_CARRINHO" a partir da massa "carrinho_valido" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "DELETE" para a rota "/carrinhos/concluir-compra"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro excluído com sucesso"

  @delete_concluir_compra_sem_carrinho_200
  Cenário: Tentar concluir compra com usuario que nao possui carrinho ativo (DELETE /carrinhos/concluir-compra - Status 200)
    Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "DELETE" para a rota "/carrinhos/concluir-compra"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Não foi encontrado carrinho para esse usuário"

  @delete_concluir_compra_401
  Cenário: Impedir conclusao de compra sem Token Bearer de autenticacao (DELETE /carrinhos/concluir-compra - Status 401)
    Dado que nao possuo token de autenticacao na sessao
    Quando envio uma requisicao "DELETE" para a rota "/carrinhos/concluir-compra"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

  # =========================================================================
  # 5. DELETE /carrinhos/cancelar-compra - Cancelar compra e reabastecer estoque (200, 401)
  # =========================================================================
  @delete_cancelar_compra_200
  Cenário: Cancelar compra excluindo o carrinho e reabastecendo o estoque (DELETE /carrinhos/cancelar-compra - Status 200)
    Dado que possuo um carrinho recem cadastrado na variavel "ID_CARRINHO" a partir da massa "carrinho_valido" do arquivo "carrinhos-datapool.yml"
    Quando envio uma requisicao "DELETE" para a rota "/carrinhos/cancelar-compra"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro excluído com sucesso. Estoque dos produtos reabastecido"

  @delete_cancelar_compra_sem_carrinho_200
  Cenário: Tentar cancelar compra com usuario que nao possui carrinho ativo (DELETE /carrinhos/cancelar-compra - Status 200)
    Dado que estou autenticado com um usuario da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "DELETE" para a rota "/carrinhos/cancelar-compra"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Não foi encontrado carrinho para esse usuário"

  @delete_cancelar_compra_401
  Cenário: Impedir cancelamento de compra sem Token Bearer de autenticacao (DELETE /carrinhos/cancelar-compra - Status 401)
    Dado que nao possuo token de autenticacao na sessao
    Quando envio uma requisicao "DELETE" para a rota "/carrinhos/cancelar-compra"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"

