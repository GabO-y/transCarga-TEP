<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erro - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <%
    User user = (User) session.getAttribute("user");
    boolean isAdmin = (user != null && "ADMIN".equalsIgnoreCase(user.getRole()));
    Long idBuscado = null;
    try { idBuscado = Long.parseLong(request.getParameter("id")); } catch (Exception e) {}
    %>
    <div class="container" style="max-width: 400px;">
        <div class="error-message">
            <p>❌ Entrega não encontrada!</p>
            <% if (idBuscado != null) { %>
            <p>ID: <%= idBuscado %></p>
            <% } %>
        </div>
        <form method="get" action="EditarFreteServlet">
            <div>
                <label for="id">Tente outro ID:</label>
                <input type="number" id="id" name="id" min="1" required>
            </div>
            <input type="submit" value="Buscar">
        </form>
        <a href="listarFretes.jsp" class="nav-link">📦 Ver Listagem</a>
        <% if (isAdmin) { %>
        <a href="home.jsp" class="nav-link">← Voltar para Home</a>
        <% } else { %>
        <a href="logout" class="nav-link">← Sair (Logout)</a>
        <% } %>
    </div>
</body>
</html>
