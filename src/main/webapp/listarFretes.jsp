<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Listar Fretes</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <h2>📦 Listagem de Fretes</h2>
        <!-- A tabela será carregada e processada 100% no servlet -->
        <iframe src="FreteServlet"
                style="width:100%; height:500px; border:2px solid #e0e0e0; border-radius: 8px;">
        </iframe>
    </div>
</body>
</html>
