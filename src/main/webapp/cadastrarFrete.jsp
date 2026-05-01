<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User, br.com.transcarga.persistencia.UserDAO, java.util.List" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastrar Frete - TransCarga</title>
    <link rel="stylesheet" href="style.css">
    <script>
        function atualizarDataEntrega() {
            var status = document.getElementById('status').value;
            var dataEntrega = document.getElementById('dataEntrega');
            if (status === 'Entregue') {
                dataEntrega.required = true;
                dataEntrega.disabled = false;
            } else {
                dataEntrega.required = false;
                dataEntrega.value = '';
                dataEntrega.disabled = true;
            }
        }
        window.onload = atualizarDataEntrega;
    </script>
</head>
<body>
    <%
    User user = (User) session.getAttribute("user");
    List<User> listaUsers = new UserDAO().listarApenasUsers();
    %>
    <div class="container">
        <h2>Cadastro de Frete</h2>
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
                <label for="status">Status:</label>
                <select id="status" name="status" required onchange="atualizarDataEntrega()">
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
            <div>
                <label for="userId">Usuário (opcional):</label>
                <select id="userId" name="userId" style="width:100%; padding:10px; border-radius:4px; border:1px solid #ddd;">
                    <option value="">Nenhum (sem associação)</option>
                    <% for (User u : listaUsers) { %>
                    <option value="<%= u.getId() %>"><%= u.getUsername() %> (ID: <%= u.getId() %>)</option>
                    <% } %>
                </select>
                <small style="color:#666;">Selecione o usuário para associar ao frete</small>
            </div>
            <input type="submit" value="Cadastrar Frete">
        </form>
    </div>
</body>
</html>
