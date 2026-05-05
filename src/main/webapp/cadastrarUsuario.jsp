<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<%
    // Se nao tem parametro "publico", exige admin
    if (request.getParameter("publico") == null) {
        User usuario = (User) session.getAttribute("user");
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (!"ADMIN".equals(usuario.getRole())) {
            response.sendRedirect("home.jsp");
            return;
        }
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
<%
            boolean isPublico = "true".equals(request.getParameter("publico"));
            if (isPublico) {
%>
            <input type="hidden" name="publico" value="true">
<%
            }
%>
            <div>
                <label for="username">Usuário:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div>
                <label for="password">Senha:</label>
                <input type="password" id="password" name="password" required minlength="6">
            </div>
            <div>
                <label for="endereco">Endereço:</label>
                <input type="text" id="endereco" name="endereco" maxlength="200" placeholder="Rua, número, bairro - Cidade/UF" required>
            </div>
<%
                if (isPublico) {
%>
            <input type="hidden" name="role" value="USER">
<%
                } else {
%>
            <div>
                <label for="role">Papel:</label>
                <select id="role" name="role" required>
                    <option value="">Selecione...</option>
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                </select>
            </div>
<%
                }
%>
            <input type="submit" value="Cadastrar">
        </form>
<%
            if (isPublico) {
%>
        <a href="login.jsp" style="display:block; text-align:center; margin-top:15px; color:#2c7cbd; text-decoration:none;">
            Voltar para o login
        </a>
<%
            }
%>
    </div>
</body>
</html>
