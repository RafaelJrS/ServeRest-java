# language: pt
@login @api @smoke @regressivo
Funcionalidade: Autenticação na API (POST /login) com Passos Padronizados
  Como um QA utilizando a estrutura padronizada Multi-API
  Quero utilizar sempre os mesmos passos (Dado que / Quando / Então) alterando apenas método, rota, massa e status
  Para validar os cenários de Login 200 (Sucesso), 401 (Não Autorizado) e 400 (Validação de Entrada)

  Contexto:
    Dado que utilizo a url da API "API_SERVEREST"
    E que a sessao da requisicao esta limpa e inicializada

  @login_200
  Cenário: Realizar login com sucesso na API (Status 200)
    Dado que possuo um usuario cadastrado a partir da massa "usuario_base_para_login" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 200
    E o tempo de resposta deve ser menor que 5000 milissegundos
    E o campo "message" na resposta JSON deve conter "Login realizado com sucesso"
    E o token Bearer deve ser gerado e salvo no contexto
    E o contrato da resposta deve respeitar o schema "schemas/login-200-schema.json"

  @login_401
  Cenário: Impedir login com credenciais invalidas na API (Status 401)
    Dado que carrego o payload dinamico "login_credenciais_invalidas" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 401
    E o tempo de resposta deve ser menor que 5000 milissegundos
    E o campo "message" na resposta JSON deve conter "Email e/ou senha inválidos"
    E o contrato da resposta deve respeitar o schema "schemas/login-401-schema.json"

  @login_email_invalido_400
  Cenário: Impedir login com formato de e-mail invalido (Status 400)
    Dado que carrego o payload dinamico "login_email_formato_invalido" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 400
    E o campo "email" na resposta JSON deve conter "email deve ser um email válido"

  @login_campos_branco_400
  Cenário: Impedir login com campos de e-mail e senha em branco (Status 400)
    Dado que carrego o payload dinamico "login_campos_em_branco" do arquivo "login-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/login"
    Entao o status code da resposta deve ser 400
    E o campo "email" na resposta JSON deve conter "email não pode ficar em branco"
    E o campo "password" na resposta JSON deve conter "password não pode ficar em branco"
