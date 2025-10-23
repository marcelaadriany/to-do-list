<h1 align="center">
  To-do List
</h1>

API REST para gerenciar tarefas (CRUD), desenvolvida com Spring Boot. 
Permite criar, listar, atualizar, marcar como concluída e excluir tarefas.

## Tecnologias

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [SpringDoc OpenAPI 3](https://springdoc.org/v2/#spring-webflux-support)
- [Mysql](https://dev.mysql.com/downloads/)
- [H2 Database (para testes](https://www.h2database.com/html/main.html)

## Práticas adotadas

- Princípios SOLID
- Arquitetura em camadas
- API RESTful
- Consultas com Spring Data JPA
- Injeção de Dependências
- Tratamento centralizados de erros
- Geração automática de documentação Swagger/OpenAPI 3

## Como Executar

- Clonar repositório
```
$ git clone https://github.com/marcelaadriany/to-do-list
```
- Construir o projeto:
```
$ ./mvnw clean package
```
- Executar a aplicação:
```
mvn spring-boot:run
```

A aplicação estará disponível em: [localhost:8080](http://localhost:8080).
A documentação (Swagger UI) pode ser acessada em: [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## API Endpoints
- Criar tarefa
```
$ http POST :8080/tasks title="Estudar Spring Boot" description="Praticar criação de APIs REST" done:=false

{
  "id": 1,
  "title": "Estudar Spring Boot",
  "description": "Praticar criação de APIs REST",
  "done": false,
  "createdAt": "2025-10-23T14:35:00",
  "completedAt": null
}
```

- Listar tarefas
```
$ http GET :8080/tasks

[
  {
    "id": 1,
    "title": "Estudar Spring Boot",
    "description": "Praticar criação de APIs REST",
    "done": false,
    "createdAt": "2025-10-23T14:35:00",
    "completedAt": null
  },
  {
    "id": 2,
    "title": "Fazer testes unitários",
    "description": "Usar JUnit e Mockito",
    "done": true,
    "createdAt": "2025-10-23T15:10:00",
    "completedAt": "2025-10-23T16:00:00"
  }
]
```

- Buscar tarefa por ID
```
$ http GET :8080/tasks/1

{
  "id": 1,
  "title": "Estudar Spring Boot",
  "description": "Praticar criação de APIs REST",
  "done": false,
  "createdAt": "2025-10-23T14:35:00",
  "completedAt": null
}
```

- Atualizar tarefa
```
$ http PUT :8080/tasks/1 title="Estudar Spring Boot (revisão)" description="Praticar controllers e DTOs" done:=false

{
  "id": 1,
  "title": "Estudar Spring Boot (revisão)",
  "description": "Praticar controllers e DTOs",
  "done": false,
  "createdAt": "2025-10-23T14:35:00",
  "completedAt": null
}
```

- Marcar tarefa como concluída
```
$ http PATCH :8080/tasks/1/done

{
  "id": 1,
  "title": "Estudar Spring Boot",
  "description": "Praticar criação de APIs REST",
  "done": true,
  "createdAt": "2025-10-23T14:35:00",
  "completedAt": "2025-10-23T17:00:00"
}
```

- Deletar tarefa
```
$ http DELETE :8080/tasks/1

(no content)
```