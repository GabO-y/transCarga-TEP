<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Listar Fretes</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <%
        User user = (User) session.getAttribute("user");
        boolean isAdmin = false;
        if (user != null && user.getRole() != null) {
            isAdmin = "admin".equals(user.getRole().toLowerCase());
        }
        if (isAdmin) {
        %>
        <a href="home.jsp" class="nav-link" style="margin-top:0; margin-bottom:20px; display:inline-block;">← Voltar para Home</a>
        <% } else { %>
        <a href="logout" class="nav-link" style="margin-top:0; margin-bottom:20px; display:inline-block;">← Sair (Logout)</a>
        <% } %>
        <h2>📦 Listagem de Fretes</h2>
        <!-- A tabela será carregada e processada 100% no servlet -->
        <iframe src="/transcarga/FreteServlet"
                style="width:100%; height:500px; border:2px solid #e0e0e0; border-radius: 8px;">
        </iframe>
    </div>
    <footer>
        © Mossoró, 2025 - TransCarga
    </footer>
</body>
</html>
