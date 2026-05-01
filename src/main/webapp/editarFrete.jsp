<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.com.transcarga.persistencia.User, br.com.transcarga.persistencia.UserDAO, br.com.transcarga.persistencia.Frete, java.util.List" %>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Frete - TransCarga</title>
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
    Frete frete = (Frete) request.getAttribute("frete");
    List<User> listaUsers = new UserDAO().listarApenasUsers();
    if (frete == null) {
        response.sendRedirect(request.getContextPath() + "/FreteServlet");
        return;
    }
    %>
    <div class="container">
        <h2>Editar Frete #<%= frete.getId() %></h2>
        <form method="post" action="EditarFreteServlet">
            <input type="hidden" name="id" value="<%= frete.getId() %>">

            <div>
                <label for="origem">Origem:</label>
                <input type="text" id="origem" name="origem" maxlength="100" value="<%= frete.getOrigem() != null ? frete.getOrigem() : "" %>" required>
            </div>
            <div>
                <label for="destino">Destino:</label>
                <input type="text" id="destino" name="destino" maxlength="100" value="<%= frete.getDestino() != null ? frete.getDestino() : "" %>" required>
            </div>
            <div>
                <label for="peso">Peso (kg):</label>
                <input type="number" id="peso" name="peso" step="0.01" min="0" value="<%= frete.getPeso() %>" required>
            </div>
            <div>
                <label for="valor">Valor (R$):</label>
                <input type="number" id="valor" name="valor" step="0.01" min="0" value="<%= frete.getValor() %>" required>
            </div>
            <div>
                <label for="transportadora">Transportadora:</label>
                <input type="text" id="transportadora" name="transportadora" maxlength="100" value="<%= frete.getTransportadora() != null ? frete.getTransportadora() : "" %>" required>
            </div>
            <div>
                <label for="status">Status:</label>
                <select id="status" name="status" required onchange="atualizarDataEntrega()">
                    <option value="Pendente" <%= "Pendente".equals(frete.getStatus()) ? "selected" : "" %>>Pendente</option>
                    <option value="Em trânsito" <%= "Em trânsito".equals(frete.getStatus()) ? "selected" : "" %>>Em trânsito</option>
                    <option value="Entregue" <%= "Entregue".equals(frete.getStatus()) ? "selected" : "" %>>Entregue</option>
                </select>
            </div>
            <div>
                <label for="dataFrete">Data do Frete:</label>
                <input type="date" id="dataFrete" name="dataFrete" value="<%= frete.getDataFrete() != null ? frete.getDataFrete().toString() : "" %>" required>
            </div>
            <div>
                <label for="dataEntrega">Data de Entrega:</label>
                <input type="date" id="dataEntrega" name="dataEntrega" value="<%= frete.getDataEntrega() != null ? frete.getDataEntrega().toString() : "" %>" required>
            </div>
            <div>
                <label for="observacoes">Observações:</label>
                <textarea id="observacoes" name="observacoes" rows="4" maxlength="500"><%= frete.getObservacoes() != null ? frete.getObservacoes() : "" %></textarea>
            </div>
            <div>
                <label for="userId">Usuário (opcional):</label>
                <select id="userId" name="userId" style="width:100%; padding:10px; border-radius:4px; border:1px solid #ddd;">
                    <option value="">Nenhum (sem associação)</option>
                    <% for (User u : listaUsers) { %>
                    <option value="<%= u.getId() %>" <%= (frete.getUser() != null && u.getId().equals(frete.getUser().getId())) ? "selected" : "" %>><%= u.getUsername() %> (ID: <%= u.getId() %>)</option>
                    <% } %>
                </select>
                <small style="color:#666;">Selecione o usuário para associar ao frete</small>
            </div>
            <input type="submit" value="Salvar Alterações">
        </form>
    </div>
</body>
</html>
