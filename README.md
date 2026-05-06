# TransCarga

Sistema web para gestão de fretes desenvolvido em Java com Jakarta EE 10.

## Tecnologias

- **Java 21**
- **Jakarta EE 10** (Servlets, JPA, JSP)
- **MariaDB** (banco de dados)
- **Hibernate 6.4.0.Final** (JPA Provider)
- **Tomcat 10.1** (servidor de aplicação)
- **BCrypt** (criptografia de senhas)
- **Maven** (gerenciamento de dependências)

## Funcionalidades

### Administrador (ADMIN)
- Cadastrar, listar, editar e remover fretes
- Cadastrar e listar usuários
- Responder solicitações de frete dos users (aprovar, rejeitar, propor valor)
- Enviar ofertas de frete diretamente para users
- Visualizar todas as solicitações e ofertas enviadas
- Acesso a todas as funcionalidades do sistema

### Usuário Comum (USER)
- Visualizar lista de fretes
- Solicitar fretes para o admin analisar
- Responder ofertas do admin (aceitar ou rejeitar)
- Visualizar todas as solicitações pendentes, em análise e rejeitadas em seção unificada
- Editar seus próprios fretes (se permitido)

## Fluxo de Solicitações

1. **User solicita** → `SOLICITACAO` com `origemCriacao=USER`, status `Solicitado`
2. **Admin responde** → aprova (vira `CONFIRMADO`), rejeita (vira `Rejeitado` com motivo) ou propõe (vira `Em análise`)
3. **User decide** → aceita proposta (vira `Pendente`/`CONFIRMADO`) ou rejeita (vira `Encerrado` com motivo)
4. **Admin oferta** → cria frete com `origemCriacao=ADMIN`, status `Solicitado`, user recebe e decide

## Estrutura do Projeto

```
transCargaHTMLJava/
├── src/main/java/br/com/transcarga/
│   ├── negocios/          # Servlets e filtros
│   │   ├── LoginServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── FreteServlet.java
│   │   ├── EditarFreteServlet.java
│   │   ├── SolicitarFreteServlet.java
│   │   ├── SolicitacaoServlet.java
│   │   ├── EditarSolicitacaoServlet.java
│   │   ├── UserRespostaServlet.java
│   │   ├── UsuarioServlet.java
│   │   ├── AuthFilter.java
│   │   └── GerarHashServlet.java
│   └── persistencia/      # Entidades e DAOs
│       ├── Frete.java
│       ├── FreteDAO.java
│       ├── User.java
│       └── UserDAO.java
├── src/main/webapp/       # JSPs, CSS e recursos estáticos
│   ├── home.jsp              # Home do administrador
│   ├── login.jsp             # Tela de login
│   ├── listarFretes.jsp      # Listagem de fretes
│   ├── cadastrarFrete.jsp    # Cadastro/oferta de fretes
│   ├── editarFrete.jsp       # Edição de fretes
│   ├── buscarFrete.jsp       # Busca de fretes
│   ├── erroFrete.jsp         # Página de erro
│   ├── listarUsuarios.jsp    # Listagem de usuários
│   ├── cadastrarUsuario.jsp  # Cadastro de usuários
│   └── style.css             # Estilos da aplicação
└── pom.xml                   # Configuração Maven
```

## Configuração

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```

2. Configure o banco de dados MariaDB:
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
- Redirects via JavaScript são usados para evitar iframe aninhado ao submeter formulários
- Entity `Frete` possui campos de controle de fluxo: `tipo`, `origemCriacao`, `motivoRejeicao`, `dataRespostaAdmin`, `encerradoDispensado`
- Hibernate em modo `update` sincroniza o schema automaticamente

## Licença

Projeto acadêmico - TransCarga © 2026
