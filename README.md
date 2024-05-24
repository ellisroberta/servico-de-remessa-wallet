# Wallet Management Service
O Wallet Management Service é responsável pelo gerenciamento das carteiras digitais.

## Descrição
O Wallet Management Service é um microserviço independente que oferece funcionalidades de consulta de saldo e processamento de transações financeiras nas carteiras digitais.

## Arquitetura
O serviço Wallet Management é projetado como um microserviço independente e segue os princípios de arquitetura de microsserviços.
Ele é desenvolvido usando tecnologias como Spring Boot, Java rabbitMq, e integrações com banco de dados H2 para armazenamento dos dados dos usuários.

## Integrações
O Wallet Management Service se integra com os seguintes serviços:

- [User Management Service] (https://github.com/ellisroberta/servico-de-remessa-user): responsável pelo gerenciamento de usuários.

## Requisitos do Sistema
Para executar o Wallet Management Service, verifique se você possui os seguintes requisitos instalados:

- Java 17: [Instalar Java 17](https://www.oracle.com/java/technologies/downloads/)
- Maven: [Instalar Maven](https://maven.apache.org/install.html)
- Docker: [Instalar Docker](https://docs.docker.com/get-docker/)

Certifique-se de que o Java 17 e o Maven estejam configurados corretamente em seu ambiente.

## Compilação e Execução

### 1. Compilar o Projeto:
Navegue até a raiz do projeto e execute o seguinte comando para compilar o projeto utilizando Maven:

```bash
Copiar código
mvn clean package
```

### 2. Construir Imagem Docker:
Antes de iniciar o ambiente com docker-compose, construa a imagem Docker do Wallet Management Service:

```bash
Copiar código
docker build -t wallet-management .
```

Observação: Certifique-se de ter construído as imagens das dependências antes de executar o docker-compose, como o Authentication Service e o Profile Service.

### 3. Executar com Docker Compose:
Inicie o ambiente utilizando docker-compose, garantindo que o comando seja executado no diretório principal do projeto:

```bash
Copiar código
docker-compose up -d
```

Isso garantirá que todos os serviços necessários sejam iniciados corretamente.

## Documentação do Swagger
O Wallet Management Service possui uma documentação do Swagger que descreve os endpoints disponíveis e fornece informações detalhadas sobre como consumir a API.

Para acessar a documentação do Swagger, siga as etapas abaixo:

Verifique se o docker-compose foi corretamente executado.
Abra o navegador e vá para a URL: http://localhost/wallet/swagger-ui.html.
Isso abrirá a interface do Swagger, onde você poderá explorar os endpoints, enviar solicitações e visualizar as respostas.

Certifique-se de que o serviço esteja em execução para acessar a documentação do Swagger.

## Funcionalidades Principais
- Consulta de Saldo em uma Carteira. 
- Criação de uma Nova Carteira.
- Atualização de uma Carteira Existente.
- Deleção de uma Carteira.
- Consulta de Todas as Transações.
- Consulta de uma Transação Específica.

## Exemplos de Uso (Curl)
Aqui estão alguns exemplos de como usar as funcionalidades do Wallet Management Service com curl:

### WalletController

- GET /api/wallets/{userId} 
    Retorna a carteira associada ao usuário com o ID especificado.

```bash
Copiar código
curl -X GET "http://localhost/wallet/api/wallets/{userId}" -H "accept: */*"
```

- GET /api/wallets
    Retorna todas as carteiras cadastradas.

```bash
Copiar código
curl -X GET "http://localhost/wallet/api/wallets" -H "accept: */*"
```

- POST /api/wallets
    Cria uma nova carteira com os dados fornecidos no corpo da requisição.

```bash
curl -X POST "http://localhost/wallet/api/wallets" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"userId\": \"12345678-1234-1234-1234-123456789abc\", \"balanceBrl\": 0.0, \"balanceUsd\": 0.0 }"
```

- PUT /api/wallets/{id}
    Atualiza os dados de uma carteira existente com o ID especificado.

```bash
Copiar código
curl -X PUT "http://localhost/wallet/api/wallets/{id}" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balanceBrl\": 100.0, \"balanceUsd\": 50.0 }"
```

- DELETE /api/wallets/{id}
  Deleta uma carteira pelo ID especificado.

```bash
Copiar código
curl -X DELETE "http://localhost/wallet/api/wallets/{id}" -H "accept: */*"
```

### TransactionController

- GET /api/transactions
  Retorna todas as transações registradas.

```bash
Copiar código
curl -X GET "http://localhost/wallet/api/transactions" -H "accept: */*"
```

- GET /api/transactions/{transactionId}
  Retorna uma transação específica pelo ID.

```bash
Copiar código
curl -X GET "http://localhost/wallet/api/transactions/{transactionId}" -H "accept: */*"
```