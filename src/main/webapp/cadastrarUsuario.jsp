<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<%
    User usuario = (User) session.getAttribute("user");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (!"ADMIN".equals(usuario.getRole())) {
        response.sendRedirect("home.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastrar Usuário - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container" style="max-width: 500px;">
        <h2>Cadastrar Novo Usuário</h2>

        <% if (request.getParameter("error") != null) { %>
            <div class="error-message">
                Erro ao cadastrar: <%= request.getParameter("error") %>
            </div>
        <% } %>

        <form method="post" action="usuario">
            <div>
                <label for="username">Usuário:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div>
                <label for="password">Senha:</label>
                <input type="password" id="password" name="password" required minlength="6">
            </div>
            <div>
                <label for="role">Papel:</label>
                <select id="role" name="role" required>
                    <option value="">Selecione...</option>
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
            </div>
            <input type="submit" value="Cadastrar">
        </form>
        <a href="home.jsp" class="nav-link">Voltar para Home</a>
    </div>
</body>
</html>
