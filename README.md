# Projeto Nubank - Capital Gains

## Sobre as decisões técnicas
  
- Utilização de Java (21)
  - Escolhido por ser a linguagem na qual tenho mais experiência.
  - Benefícios da versão 21: suporte a Virtual Threads, melhorias de desempenho e maior maturidade da linguagem.
- Uso do Java Streams API para manipulação eficiente de dados.
- Implementação de princípios SOLID para melhor organização e manutenção do código.
- Uso de containers Docker
  - Facilita a execução do projeto sem necessidade de configurações adicionais na máquina.
  - Permite isolamento do ambiente e consistência entre execuções.

## Sobre as bibliotecas utilizadas no projeto

- Jackson
  - Para serialização e desserialização de objetos JSON, facilitando a conversão entre objetos Java e JSON.
- JUnit
  - Para a criação e execução de testes unitários, garantindo a qualidade e a funcionalidade do código.
- AssertJ
  - Para fornecer uma API fluente e legível para asserções em testes, tornando a verificação de resultados mais intuitiva.
- Mockito
  - Para mockar dependências em testes, permitindo a simulação do comportamento de objetos e facilitando a testabilidade de componentes isolados.

## Como executar o projeto

    Obs: É apenas necessário a instalação do Docker. 
    Foi utilizado nesse projeto a versão 28.0.1

### 1 - Fazer o build
``` shell
    docker compose run --rm capital-gains-maven mvn clean install
```

### 2 - Executar

#### Exemplo 1: Passando operações inline
``` shell  
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]' \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]'
```  
#### Exemplo 2: Passando operações inline com múltiplas linhas
``` shell  
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]
    [{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]'
``` 
#### Exemplo 3: Passando operações via arquivo JSON
``` shell  
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar < sample/operations.json        
```

## Como executar os testes
``` shell
    docker compose run --rm capital-gains-maven mvn test
```

