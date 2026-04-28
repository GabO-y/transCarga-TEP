<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TransCarga - Home (Admin)</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <h1>TransCarga - Administração</h1>
        <h2>Bem-vindo ao Sistema de Gestão de Fretes</h2>
        <ul>
            <li><a href="cadastrarFrete.jsp">Cadastrar Frete</a></li>
            <li><a href="listarFretes.jsp">Listar Fretes</a></li>
            <li><a href="cadastrarUsuario.jsp">Cadastrar Usuário</a></li>
            <li><a href="listarUsuarios.jsp">Listar Usuários</a></li>
            <li><a href="logout">Sair (Logout)</a></li>
        </ul>
    </div>
    <footer>
        <p>TransCarga © 2026 - Todos os direitos reservados</p>
    </footer>
</body>
</html>
