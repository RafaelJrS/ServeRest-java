# language: pt
@regressivo @api @usuarios
Funcionalidade: Gestão de Usuários via Steps Modulares e Massa Dinâmica (YAML)
  Como um Engenheiro de Qualidade em estratégia Shift-Left
  Quero exercitar os verbos HTTP (GET, POST, PUT, DELETE) com isolamento de sessão via PicoContainer
  Para garantir o contrato e regras de negócio da API sem dados fixos no código

  Cenário: Ciclo completo CRUD de usuário utilizando DataPool dinâmico e rotas parametrizadas
    Dado que a sessao da requisicao esta limpa e inicializada
    E que carrego o payload dinamico "usuario_admin_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "POST" para a rota "/usuarios"
    Entao o status code da resposta deve ser 201
    E o campo "message" na resposta JSON deve conter "Cadastro realizado com sucesso"
    E armazeno o valor do campo "_id" da resposta na variavel de contexto "ID_USUARIO_CRIADO"

    Dado que a sessao da requisicao esta limpa e inicializada
    E que defino o parametro de rota "id" com o valor "ID_USUARIO_CRIADO"
    Quando envio uma requisicao "GET" para a rota "/usuarios/{id}"
    Entao o status code da resposta deve ser 200

    Dado que a sessao da requisicao esta limpa e inicializada
    E que defino o parametro de rota "id" com o valor "ID_USUARIO_CRIADO"
    E que carrego o payload dinamico "usuario_atualizado_valido" do arquivo "usuarios-datapool.yml"
    Quando envio uma requisicao "PUT" para a rota "/usuarios/{id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro alterado com sucesso"

    Dado que a sessao da requisicao esta limpa e inicializada
    E que defino o parametro de rota "id" com o valor "ID_USUARIO_CRIADO"
    Quando envio uma requisicao "DELETE" para a rota "/usuarios/{id}"
    Entao o status code da resposta deve ser 200
    E o campo "message" na resposta JSON deve conter "Registro excluído com sucesso"

