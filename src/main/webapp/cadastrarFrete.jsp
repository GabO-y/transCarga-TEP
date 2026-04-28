<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastrar Frete - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <%
    User user = (User) session.getAttribute("user");
    boolean isAdmin = (user != null && "admin".equals(user.getRole()));
    %>
    <div class="container">
        <% if (isAdmin) { %>
        <a href="home.jsp" class="nav-link" style="margin-top:0; margin-bottom:20px; display:inline-block;">← Voltar para Home</a>
        <% } else { %>
        <a href="logout" class="nav-link" style="margin-top:0; margin-bottom:20px; display:inline-block;">← Sair (Logout)</a>
        <% } %>
        <h2>Cadastro de Frete</h2>
        <form method="post" action="cadastrarFrete">
            <div>
                <label for="origem">Origem:</label>
                <input type="text" id="origem" name="origem" maxlength="100" required>
            </div>
            <div>
                <label for="destino">Destino:</label>
                <input type="text" id="destino" name="destino" maxlength="100" required>
            </div>
            <div>
                <label for="peso">Peso (kg):</label>
                <input type="number" id="peso" name="peso" step="0.01" min="0.01" required>
            </div>
            <div>
                <label for="transportadora">Transportadora:</label>
                <input type="text" id="transportadora" name="transportadora" maxlength="100" required>
            </div>
            <div>
                <label for="valor">Valor (R$):</label>
                <input type="number" id="valor" name="valor" step="0.01" min="0.01" required>
            </div>
            <div>
                <label for="status">Status:</label>
                <select id="status" name="status" required>
                    <option value="">Selecione o status</option>
                    <option value="Pendente">Pendente</option>
                    <option value="Em trânsito">Em trânsito</option>
                    <option value="Entregue">Entregue</option>
                </select>
            </div>
            <div>
                <label for="dataFrete">Data do Frete:</label>
                <input type="date" id="dataFrete" name="dataFrete" required>
            </div>
            <div>
                <label for="dataEntrega">Data de Entrega:</label>
                <input type="date" id="dataEntrega" name="dataEntrega" required>
            </div>
            <div>
                <label for="observacoes">Observações:</label>
                <textarea id="observacoes" name="observacoes" rows="4" maxlength="500"></textarea>
            </div>
            <input type="submit" value="Cadastrar Frete">
        </form>
    </div>
    <footer>
        © Mossoró, 2025 - TransCarga
    </footer>
</body>
</html>
