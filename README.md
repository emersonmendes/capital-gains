# Projeto Nubank - Capital Gains

## Sobre as decisões técnicas
  
- Utilizando Java
  - Linguagem no qual eu tenho mais dominio
- 

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

#### 1 - Fazer o build
``` shell
    docker compose run --rm capital-gains-maven mvn clean install
```

#### 2 - Executar
``` shell
    
    # Exemplo 1:
    
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]' \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]'
    
    # Exemplo 2:
    
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar \
    '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]
    [{"operation":"buy", "unit-cost":10.00, "quantity": 10000}]'
    
    # Exemplo 3:
    
    docker compose run --rm -T capital-gains-app java -jar target/capital-gains.jar < sample/operations.json
        
```

## Como executar os testes
``` shell
    docker compose run --rm capital-gains-maven mvn test
```

## Notas adicionais