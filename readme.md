[JAVA_BADGE]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[SPRING_BADGE]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[SQLSERVER_BADGE]: https://img.shields.io/badge/Microsoft%20SQL%20Server-CC2927?style=for-the-badge&logo=microsoft%20sql%20server&logoColor=white
[SWAGGER_BADGE]: https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white
[DOCKER_BADGE]: https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white

<h1 align="center" style="font-weight: bold;">DSCatalog 🎬💻</h1>


<div style="text-align: center;">

![java][JAVA_BADGE]
![spring][SPRING_BADGE]
![Docker][DOCKER_BADGE]
![Swagger][SWAGGER_BADGE]

[//]: # (![MicrosoftSQL Server][SQLSERVER_BADGE])

</div>


<p align="center">
 <a href="#tech">Tecnologias</a> • 
 <a href="#started">Getting Started</a> • 
  <a href="#routes">Endpoints da API</a> •
 <a href="#colab">Colaboradores</a> •
</p>

<p align="center">
    <b>Projeto criado durante o curso Spring Specialist da instituição DevSuperior do professor Nélio Alves.</b>
</p>

<h2 id="tech">💻 Tecnologias</h2>

Este projeto utiliza as seguintes tecnologias e frameworks:

- **Java 21**: Linguagem de programação para o desenvolvimento backend.
- **Spring Framework**: Framework para criação de aplicações Java robustas e escaláveis.
- **Spring Security**: Módulo do Spring para segurança e controle de acesso.
- **Spring Data JPA**: Abstração de persistência de dados baseada no JPA.
- **H2 MemoryDatabase**: Banco de dados relacional utilizado na aplicação.
- **Docker**: Ferramenta para criação e gerenciamento de containers.
- **Swagger**: Ferramenta para documentação e teste de APIs.

<h2 id="started">🚀 Getting started</h2>

<h3>Pré-requisitos</h3>

Antes de começar, você precisará ter os seguintes softwares instalados em sua máquina:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Java 21](https://www.oracle.com/br/java/technologies/downloads/#java21)
- [Git](https://git-scm.com/)

<h3>Clone</h3>

Para clonar o repositório do projeto, execute o seguinte comando no terminal:

```bash
git clone https://github.com/RafaelJaber/DsCatalog.git
```

<h3>Iniciando o Projeto</h3>

Navegue até o diretório do projeto e suba os containers Docker:

```bash
cd dscatalog
docker-compose up -d
```

<h3>Rodando a Aplicação</h3>

Para iniciar a aplicação, você pode utilizar uma IDE ou seguir os passos abaixo para compilar e rodar via Maven:

```bash
./mvnw deploy
```

```bash
java -jar backend/target/dscatalog-0.0.1-SNAPSHOT.jar
```

<h2 id="routes">📍 Endpoints da API</h2>

| Rota                                    | Descrição                                               |
|-----------------------------------------|---------------------------------------------------------|
| <kbd>POST /oauth2/token</kbd>           | Logar                                                   |
| <kbd>GET /categories</kbd>              | Buscar categorias - Sem autenticação                    |
| <kbd>GET /categories/{categoryId}</kbd> | Buscar categoria informando o ID - Sem autenticação     |
| <kbd>POST /categories</kbd>             | Cadastrar categorias - Estar autenticado                |
| <kbd>PUT /categories</kbd>              | Atualizar categoria informando o id - Estar autenticado |
| <kbd>DEL /categories/{categoryId}</kbd> | Deletar categoria informando o id - Estar autenticado   |
| <kbd>GET /products</kbd>                | Buscar produtos - Sem autenticação                      |
| <kbd>GET /products/{productId}</kbd>    | Buscar produto informando o id - Sem autenticação       |
| <kbd>POST /products</kbd>               | Cadastrar produto - Estar autenticado                   |
| <kbd>PUT /products</kbd>                | Atualizar produto informando o id - Estar autenticado   |
| <kbd>DEL /products/{productId}</kbd>    | Deletar produto informando o id - Estar autenticado     |
| <kbd>GET /users</kbd>                   | Buscar usuários - Permissão admin                       |
| <kbd>GET /users/{userId}</kbd>          | Buscar usuário informando o id - Permissão admin        |
| <kbd>POST /users</kbd>                  | Cadastrar usuário - Permissão admin                     |
| <kbd>PUT /users</kbd>                   | Atualizar usuário informando o id - Permissão admin     |
| <kbd>DEL /users/{userId}</kbd>          | Deletar usuário informando o id - Permissão admin       |

<h2 id="colab">🤝 Colaboradores</h2>

<table>
  <tr>
    <td align="center">
      <a href="#">
        <img src="https://github.com/rafaeljaber.png" width="100px;" alt="Fernanda Kipper Profile Picture"/><br>
        <sub>
          <b>Rafael Jáber</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="#">
        <img src="https://github.com/acenelio.png" width="100px;" alt="Foto Nélio Alves"/><br>
        <sub>
          <b>Nélio Alves</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="#">
        <img src="https://github.com/devsuperior.png" width="100px;" alt="Foto DevSuperior"/><br>
        <sub>
          <b>DevSuperior</b>
        </sub>
      </a>
    </td>
  </tr>
</table>