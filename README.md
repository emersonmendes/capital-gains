# Projeto Nubank - Capital Gains

## Sobre as decisões técnicas


## Sobre as bibliotecas utilizadas no projeto


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


## Notas adicionais