# language: pt
@login @api @smoke
Funcionalidade: Autenticação na API (POST /login) com Passos Padronizados
  Como um QA utilizando a estrutura padronizada Multi-API
  Quero utilizar sempre os mesmos passos (Dado que / Quando / Então) alterando apenas método, rota, massa e status
  Para validar os cenários de Login 200 (Sucesso) e 401 (Não Autorizado)

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  @login_200
  Cenário: Realizar login com sucesso na API (Status 200)
    Dado que possuo um usuario cadastrado a partir da massa "usuario_base_para_login" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Login realizado com sucesso"
    E o token Bearer deve ser gerado e salvo no contexto
    E o contrato da resposta deve respeitar o schema "schemas/login-200-schema.json"

  @login_401
  Cenário: Impedir login com credenciais invalidas na API (Status 401)
    Dado que carrego o payload dinamico "login_credenciais_invalidas" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 401
    E o campo "message" na resposta JSON deve conter "Email e/ou senha inválidos"
    E o contrato da resposta deve respeitar o schema "schemas/login-401-schema.json"

