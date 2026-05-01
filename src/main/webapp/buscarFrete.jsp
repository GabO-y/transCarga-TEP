<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Buscar Frete - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <%
    User user = (User) session.getAttribute("user");
    boolean isAdmin = (user != null && "ADMIN".equalsIgnoreCase(user.getRole()));
    if (!isAdmin) {
        response.sendRedirect(request.getContextPath() + "/listarFretes.jsp?erro=sem-permissao");
        return;
    }
    %>
    <div class="container" style="max-width: 400px;">
        <h2>Buscar Entrega para Editar</h2>
        <form method="get" action="EditarFreteServlet">
            <div>
                <label for="id">ID da Entrega:</label>
                <input type="number" id="id" name="id" min="1" required>
            </div>
            <input type="submit" value="Buscar">
        </form>
        <a href="home.jsp" class="nav-link">← Voltar para Home</a>
    </div>
</body>
</html>
