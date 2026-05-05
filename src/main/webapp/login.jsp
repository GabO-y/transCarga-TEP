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
        <% if ("cadastrado".equals(request.getParameter("sucesso"))) { %>
            <div style="background:#e8f5e9; color:#2e7d32; padding:10px; border-radius:4px; margin-bottom:15px; text-align:center;">Cadastro realizado com sucesso! Faça login.</div>
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
        <a href="cadastrarUsuario.jsp?publico=true" style="display:block; text-align:center; margin-top:15px; color:#2c7cbd; text-decoration:none;">Cadastrar-se</a>
    </div>
</body>
</html>
