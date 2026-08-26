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
- Spring Security com JWT
- React no frontend

## Como rodar

Por enquanto o projeto ainda não tem o `docker-compose.yml` nem os módulos criados. Isso deve ser um dos primeiros passos.

```
docker compose up
```

## Status

Começando do zero. Próximos passos: subir a estrutura inicial do projeto, configurar o banco e as primeiras migrations.