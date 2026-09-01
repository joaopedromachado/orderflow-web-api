# Order Flow API

Projeto de estudo para praticar uma stack Java/Spring mais completa, do backend ao frontend, incluindo mensageria, integração com AWS e autenticação.

A ideia é simular um sistema de pedidos onde a criação de um pedido dispara um processamento assíncrono (geração de comprovante e notificação por email), em vez de fazer tudo isso na hora, dentro do mesmo request.

Ainda em desenvolvimento, então bastante coisa aqui vai mudar.

## Stack planejada

- Java 21 + Spring Boot
- PostgreSQL com Flyway para migrations
- RabbitMQ para processamento assíncrono
- AWS S3 e Lambda
- Resend para envio de email
- Thymeleaf para geração de relatórios dos pedidos em PDF
- Spring Security com JWT + OAUTH2
- Swagger para documentação
- Consumindo API externa ViaCEP
- Docker para conteinerização da aplicação

### Stack adicional (Desafio)
- React/TypeScript/Tailwind para o frontend

## Como rodar

Por enquanto o projeto ainda não tem o `docker-compose.yml` nem os módulos criados. Isso deve ser um dos primeiros passos.

```
docker compose up -d
```

## Quadro do Trello

Para organizar o desenvolvimento, estou usando um quadro no Trello para acompanhar as tarefas, simulando um fluxo de trabalho parecido com o de um ambiente profissional. A ideia é manter visibilidade do que já foi feito, do que está em andamento e do que ainda falta implementar. (Não será aplicado métricas sob vigor de métodologias ágeis),

Link do quadro: <a href="https://trello.com/b/z0ICU6Zz/orderflow" target="_blank">Orderflow Board</a>

## Status

~~Começando do zero. Próximos passos: subir a estrutura inicial do projeto, configurar o banco e as primeiras migrations.~~<br>
Criar entidades Order e os respectivos endpoints no padrão REST, configurar RabbitMQ e docker-compose.yml para conectar o sistema de mensageria na mesma network do postgres

just4fun :D