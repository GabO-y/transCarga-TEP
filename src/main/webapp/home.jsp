<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TransCarga - Dashboard</title>
    <link rel="stylesheet" href="style.css">
    <script>
        // Se esta página for carregada dentro de um iframe, redireciona o topo
        if (window.self !== window.top) {
            window.top.location.href = '${pageContext.request.contextPath}/home.jsp';
        }
    </script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            display: flex;
            min-height: 100vh;
            font-family: Arial, sans-serif;
        }
        .sidebar {
            width: 250px;
            background: #2c7cbd;
            color: white;
            display: flex;
            flex-direction: column;
            position: fixed;
            height: 100vh;
            left: 0;
            top: 0;
        }
        .sidebar-header {
            padding: 25px 20px;
            background: #1a6aad;
            text-align: center;
        }
        .sidebar-header h2 {
            margin: 0 0 5px 0;
            font-size: 1.3em;
        }
        .sidebar-header p {
            margin: 0;
            font-size: 0.85em;
            color: #d4e6f1;
        }
        .sidebar-nav {
            flex: 1;
            padding: 25px 0;
        }
        .sidebar-nav ul {
            list-style: none;
            padding: 0;
            margin: 0;
            display: block;
        }
        .sidebar-nav li {
            margin-bottom: 18px;
        }
        .sidebar-nav li:last-child {
            margin-bottom: 0;
        }
        .sidebar-nav a {
            display: block;
            padding: 15px 25px;
            color: #2c7cbd;
            background: white;
            text-decoration: none;
            transition: all 0.2s;
            border-left: 3px solid transparent;
            margin: 0 10px;
            border-radius: 4px;
        }
        .sidebar-nav a:hover {
            background: #e8f4f8;
            border-left: 3px solid #3a8fd4;
            color: #1a6aad;
        }
        .sidebar-nav a.active {
            background: #d4eef7;
            border-left: 3px solid #2c7cbd;
            color: #1a6aad;
            font-weight: 600;
        }
        /* hover movido para o bloco acima */
        .sidebar-footer {
            padding: 20px 25px;
            border-top: 1px solid #3a8fd4;
        }
        .sidebar-footer a {
            color: white;
            background: #c0392b;
            text-decoration: none;
            display: block;
            padding: 10px 15px;
            border-radius: 4px;
            text-align: center;
            font-size: 0.9em;
            transition: background 0.2s;
        }
        .sidebar-footer a:hover {
            background: #e74c3c;
            text-decoration: none;
        }
        .main-content {
            flex: 1;
            margin-left: 250px;
        }
        .main-content iframe {
            width: 100%;
            height: 100vh;
            border: none;
        }
    </style>
    <script>
        var contextPath = '${pageContext.request.contextPath}';
        function loadPage(page, element) {
            document.getElementById('mainFrame').src = contextPath + '/' + page;
            var links = document.querySelectorAll('.sidebar-nav a');
            links.forEach(function(link) {
                link.classList.remove('active');
            });
            if (element) {
                element.classList.add('active');
            }
        }
    </script>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>TransCarga</h2>
            <p><%= user.getUsername() %></p>
        </div>
        <nav class="sidebar-nav">
            <ul>
                <li><a href="#" onclick="loadPage('FreteServlet', this); return false;" class="active">Listar Fretes</a></li>
                <% if (isAdmin) { %>
                <li><a href="#" onclick="loadPage('cadastrarFrete.jsp', this); return false;">Cadastrar Frete</a></li>
                <li><a href="#" onclick="loadPage('listarUsuarios.jsp', this); return false;">Listar Usuários</a></li>
                <li><a href="#" onclick="loadPage('cadastrarUsuario.jsp', this); return false;">Cadastrar Usuário</a></li>
                <% } %>
            </ul>
        </nav>
        <div class="sidebar-footer">
            <a href="${pageContext.request.contextPath}/logout">Sair (Logout)</a>
        </div>
    </div>
    <div class="main-content">
        <iframe id="mainFrame" src="${pageContext.request.contextPath}/FreteServlet"></iframe>
    </div>
</body>
</html>
