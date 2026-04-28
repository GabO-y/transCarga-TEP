# TransCarga

Sistema web para gestão de fretes desenvolvido em Java com Jakarta EE 10.

## Tecnologias

- **Java 17**
- **Jakarta EE 10** (Servlets, JPA)
- **MySQL** (banco de dados)
- **Tomcat 10.1** (servidor de aplicação)
- **BCrypt** (criptografia de senhas)
- **Maven** (gerenciamento de dependências)

## Funcionalidades

### Administrador (ADMIN)
- Cadastrar, listar, editar e remover fretes
- Cadastrar e listar usuários
- Acesso a todas as funcionalidades do sistema

### Usuário Comum (USER)
- Visualizar lista de fretes
- Editar seus próprios fretes (se permitido)

## Estrutura do Projeto

```
transCargaHTMLJava/
├── src/main/java/br/com/transcarga/
│   ├── negocios/          # Servlets e filtros
│   │   ├── LoginServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── FreteServlet.java
│   │   ├── EditarFreteServlet.java
│   │   ├── UsuarioServlet.java
│   │   ├── AuthFilter.java
│   │   └── GerarHashServlet.java
│   └── persistencia/      # Entidades e DAOs
│       ├── Frete.java
│       ├── FreteDAO.java
│       ├── User.java
│       └── UserDAO.java
├── src/main/webapp/       # JSPs, CSS e recursos estáticos
│   ├── home.jsp          # Home do administrador
│   ├── login.jsp         # Tela de login
│   ├── listarFretes.jsp  # Listagem de fretes
│   ├── cadastrarFrete.jsp # Cadastro de fretes
│   ├── listarUsuarios.jsp # Listagem de usuários
│   ├── cadastrarUsuario.jsp # Cadastro de usuários
│   └── style.css        # Estilos da aplicação
└── pom.xml               # Configuração Maven
```

## Configuração

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```

2. Configure o banco de dados MySQL:
   - Crie um banco chamado `transcarga`
   - Configure as credenciais em `persistence.xml`

3. Gere um hash BCrypt para senhas iniciais:
   - Acesse `/transcarga/gerar-hash?password=sua_senha`

4. Compile e deploy:
   ```bash
   mvn clean package
   ```
   - Copie o WAR gerado para o diretório `webapps` do Tomcat

## Acesso

- **URL**: `http://localhost:8080/transcarga`
- **Login**: admin / user (conforme cadastrado no banco)

## Observações

- Senhas são armazenadas com hash BCrypt (não em texto plano)
- Sistema usa filtro (`AuthFilter`) para controle de acesso baseado em papel (role)
- Sessão é invalidada no logout
- Campos nulos nas listagens são exibidos como "-"

## Licença

Projeto acadêmico - TransCarga © 2026
