<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User, br.com.transcarga.persistencia.UserDAO, java.util.List" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ofertar Frete - TransCarga</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <%
    User user = (User) session.getAttribute("user");
    List<User> listaUsers = new UserDAO().listarApenasUsers();
    %>
    <div class="container" style="max-width: 500px;">
        <h2>Ofertar Frete para Usuário</h2>

        <div style="background:#e3f2fd; padding:12px; border-radius:6px; margin-bottom:15px; border-left:4px solid #2196f3;">
            <p style="margin:0; font-size:0.9em; color:#1565c0;">O frete será enviado como <strong>oferta</strong> para o usuário selecionado. Após o aceite dele, o frete será confirmado automaticamente.</p>
        </div>

        <% if ("sucesso".equals(request.getParameter("sucesso"))) { %>
            <div style="background:#e8f5e9; color:#2e7d32; padding:10px; border-radius:4px; margin-bottom:15px; text-align:center;">Oferta enviada com sucesso! Aguardando aceite do usuário.</div>
        <% } %>

        <form method="post" action="FreteServlet">
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
                <label for="dataFrete">Data do Frete:</label>
                <input type="date" id="dataFrete" name="dataFrete" required>
            </div>
            <div>
                <label for="observacoes">Observações:</label>
                <textarea id="observacoes" name="observacoes" rows="3" maxlength="500"></textarea>
            </div>
            <div>
                <label for="userId">Usuário <span style="color:#c0392b;">*</span>:</label>
                <select id="userId" name="userId" required>
                    <option value="">Selecione o usuário...</option>
                    <% for (User u : listaUsers) { %>
                    <option value="<%= u.getId() %>"><%= u.getUsername() %> (ID: <%= u.getId() %>)</option>
                    <% } %>
                </select>
            </div>
            <input type="submit" value="Enviar Oferta">
        </form>
    </div>
</body>
</html>
