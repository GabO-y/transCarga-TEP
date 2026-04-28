<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container" style="max-width: 400px; margin-top:100px;">
        <h2>Login - TransCarga</h2>
        <% if (request.getParameter("error") != null) { %>
            <div class="error-message">Usuário ou senha inválidos!</div>
        <% } %>
        <form method="post" action="login">
            <div>
                <label for="username">Usuário:</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div>
                <label for="password">Senha:</label>
                <input type="password" id="password" name="password" required>
            </div>
            <input type="submit" value="Entrar">
        </form>
    </div>
</body>
</html>
