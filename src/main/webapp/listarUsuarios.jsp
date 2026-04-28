<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<%@ page import="java.util.List" %>
<%
    // Verifica se o usuário está logado e é admin
    User usuarioLogado = (User) session.getAttribute("user");
    boolean isAdmin = false;
    if (usuarioLogado != null && usuarioLogado.getRole() != null) {
        isAdmin = "admin".equalsIgnoreCase(usuarioLogado.getRole());
    }

    if (usuarioLogado == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    if (!isAdmin) {
        response.sendRedirect("home.jsp");
        return;
    }

    // Se a lista de usuários não foi passada via atributo, redireciona para o Servlet
    List<User> usuarios = (List<User>) request.getAttribute("usuarios");
    if (usuarios == null) {
        response.sendRedirect("usuario");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Listar Usuários - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <% if (isAdmin) { %>
        <a href="home.jsp" class="nav-link" style="margin-top:0; margin-bottom:20px; display:inline-block;">← Voltar para Home</a>
        <% } else { %>
        <a href="logout" class="nav-link" style="margin-top:0; margin-bottom:20px; display:inline-block;">← Sair (Logout)</a>
        <% } %>
        <h2>Lista de Usuários</h2>

        <% if (request.getParameter("success") != null) { %>
            <div class="success-message">Usuário cadastrado com sucesso!</div>
        <% } %>

        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Usuário</th>
                        <th>Papel</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    System.out.println("[JSP] Listando usuários - quantidade: " + (usuarios != null ? usuarios.size() : "null"));
                    if (usuarios != null && !usuarios.isEmpty()) {
                        for (User u : usuarios) {
                    %>
                    <tr>
                        <td><%= u.getId() != null ? u.getId() : "-" %></td>
                        <td><%= u.getUsername() != null && !u.getUsername().trim().isEmpty() ? u.getUsername() : "-" %></td>
                        <td><%= u.getRole() != null && !u.getRole().trim().isEmpty() ? u.getRole() : "-" %></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="3" style="text-align:center;">Nenhum usuário cadastrado.</td>
                    </tr>
                    <%
                    }
                    %>
                </tbody>
            </table>
        </div>
        <a href="cadastrarUsuario.jsp" class="nav-link">Cadastrar Novo Usuário</a>
    </div>
    <footer>
        © Mossoró, 2025 - TransCarga
    </footer>
</body>
</html>
