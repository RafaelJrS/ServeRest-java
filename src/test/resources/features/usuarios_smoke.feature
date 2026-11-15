# language: pt
@usuarios @api @regressivo
Funcionalidade: Gestão de Usuários na API (/usuarios e /usuarios/{_id})
  Como um QA utilizando a estrutura padronizada Multi-API
  Quero exercitar todos os endpoints de Usuários (GET, POST, GET/{_id}, PUT/{_id}, DELETE/{_id})
  Para validar as regras de negócio de sucesso, exceção, integridade referencial e contratos da API ServeRest

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  # =========================================================================
  # 1. GET /usuarios - Listar usuários cadastrados
  # =========================================================================
  @get_usuarios_200
  Cenário: Listar todos os usuarios cadastrados na API (GET /usuarios - Status 200)
    Quando envio uma requisicao "GET" para a rota "/usuarios"
    Entao o status code da resposta deve ser 200
    E o tempo de resposta deve ser menor que 5000 milissegundos
    E o campo "quantidade" na resposta JSON nao deve ser nulo
    E o contrato da resposta deve respeitar o schema "schemas/usuarios-lista-200-schema.json"

  @get_usuarios_filtro_200
  Cenário: Filtrar usuarios administradores cadastrados via Query Param (GET /usuarios - Status 200)
    Dado que defino os parametros de consulta:
      | administrador | true |
    Quando envio uma requisicao "GET" para a rota "/usuarios"
    Entao o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "schemas/usuarios-lista-200-schema.json"

  # =========================================================================
  # 2. POST /usuarios - Cadastrar usuário
  # =========================================================================
  @post_usuarios_201
  Cenário: Cadastrar um novo usuario administrador com sucesso (POST /usuarios - Status 201)
    Dado que carrego o payload dinamico "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/usuarios"
    Entao o status code da resposta deve ser 201
    E o tempo de resposta deve ser menor que 5000 milissegundos
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
    E o campo "_id" na resposta JSON nao deve ser nulo
    E o contrato da resposta deve respeitar o schema "schemas/usuarios-post-201-schema.json"

  @post_usuarios_400
  Cenário: Impedir cadastro de usuario com e-mail ja utilizado (POST /usuarios - Status 400)
    Dado que possuo um usuario recem cadastrado na variavel "ID_USUARIO" a partir da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que reutilizo o mesmo payload do usuario recem cadastrado
    Quando envio uma requisicao "POST" para a rota "/usuarios"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Este email já está sendo usado"

  @post_usuarios_flag_admin_invalida_400
  Cenário: Impedir cadastro de usuario com campo administrador invalido (POST /usuarios - Status 400)
    Dado que carrego o payload dinamico "usuario_flag_admin_invalida" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/usuarios"
    Entao o status code da resposta deve ser 400
    E o campo "administrador" na resposta JSON deve conter "administrador deve ser 'true' ou 'false'"

  # =========================================================================
  # 3. GET /usuarios/{_id} - Buscar usuário por ID
  # =========================================================================
  @get_usuario_id_200
  Cenário: Buscar usuario existente por ID com sucesso (GET /usuarios/{_id} - Status 200)
    Dado que possuo um usuario recem cadastrado na variavel "ID_USUARIO" a partir da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_USUARIO"
    Quando envio uma requisicao "GET" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 200
    E o contrato da resposta deve respeitar o schema "schemas/usuarios-id-200-schema.json"

  @get_usuario_id_400
  Cenário: Buscar usuario por ID inexistente de 16 caracteres (GET /usuarios/{_id} - Status 400)
    Dado que defino o parametro de rota "_id" com o valor "idInexistente123"
    Quando envio uma requisicao "GET" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Usuário não encontrado"

  @get_usuario_id_tamanho_invalido_400
  Cenário: Impedir busca de usuario com ID fora do padrao de 16 caracteres (GET /usuarios/{_id} - Status 400)
    Dado que defino o parametro de rota "_id" com o valor "idCurto123"
    Quando envio uma requisicao "GET" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "id" na resposta JSON deve conter "id deve ter exatamente 16 caracteres alfanuméricos"

  # =========================================================================
  # 4. PUT /usuarios/{_id} - Editar usuário
  # =========================================================================
  @put_usuario_200
  Cenário: Editar dados de um usuario existente com sucesso (PUT /usuarios/{_id} - Status 200)
    Dado que possuo um usuario recem cadastrado na variavel "ID_USUARIO" a partir da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_USUARIO"
    E que carrego o payload dinamico "usuario_atualizado_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro alterado com sucesso"

  @put_usuario_201
  Cenário: Cadastrar usuario via PUT ao informar um ID inexistente (PUT /usuarios/{_id} - Status 201)
    Dado que defino o parametro de rota "_id" com o valor "idNovoViaPut999999"
    E que carrego o payload dinamico "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
    E o contrato da resposta deve respeitar o schema "schemas/usuarios-post-201-schema.json"

  @put_usuario_email_duplicado_400
  Cenário: Impedir edicao de usuario informando e-mail de outro usuario ja existente (PUT /usuarios/{_id} - Status 400)
    Dado que possuo um usuario recem cadastrado na variavel "ID_USUARIO_A" a partir da massa "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    E que possuo um usuario recem cadastrado na variavel "ID_USUARIO_B" a partir da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_USUARIO_A"
    E que reutilizo o mesmo payload do usuario recem cadastrado
    Quando envio uma requisicao "PUT" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Este email já está sendo usado"

  # =========================================================================
  # 5. DELETE /usuarios/{_id} - Excluir usuário
  # =========================================================================
  @delete_usuario_200
  Cenário: Excluir um usuario existente com sucesso (DELETE /usuarios/{_id} - Status 200)
    Dado que possuo um usuario recem cadastrado na variavel "ID_USUARIO" a partir da massa "usuario_comum_valido" do arquivo "usuarios-datapool.yml"
    E que defino o parametro de rota "_id" com o valor "ID_USUARIO"
    Quando envio uma requisicao "DELETE" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro excluído com sucesso"

  @delete_usuario_inexistente_200
  Cenário: Tentar excluir um usuario com ID inexistente (DELETE /usuarios/{_id} - Status 200)
    Dado que defino o parametro de rota "_id" com o valor "idInexistenteParaDelete999"
    Quando envio uma requisicao "DELETE" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Nenhum registro excluído"

  @delete_usuario_com_carrinho_400
  Cenário: Impedir exclusao de usuario que possui carrinho cadastrado (DELETE /usuarios/{_id} - Status 400)
    Dado que possuo um usuario com carrinho ativo salvo na variavel "ID_USUARIO_COM_CARRINHO"
    E que defino o parametro de rota "_id" com o valor "ID_USUARIO_COM_CARRINHO"
    Quando envio uma requisicao "DELETE" para a rota "/usuarios/{_id}"
    Entao o status code da resposta deve ser 400
    E o campo "message" na resposta JSON deve conter "Não é permitido excluir usuário com carrinho cadastrado"
    E o campo "idCarrinho" na resposta JSON nao deve ser nulo
