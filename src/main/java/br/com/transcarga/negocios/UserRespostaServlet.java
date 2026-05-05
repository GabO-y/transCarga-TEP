package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

@WebServlet("/userResposta")
public class UserRespostaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final FreteDAO dao = new FreteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("home.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Long id = Long.parseLong(idParam);
            Frete solicitacao = dao.buscarPorId(id);

            if (solicitacao == null || !"SOLICITACAO".equals(solicitacao.getTipo()) ||
                solicitacao.getUser() == null || !solicitacao.getUser().getId().equals(user.getId())) {
                out.println("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'><title>Acesso negado</title></head>");
                out.println("<body><div class='container'><h2>Solicitação não encontrada ou sem permissão</h2>");
                out.println("<a href='" + request.getContextPath() + "/FreteServlet'>Voltar</a></div></body></html>");
                return;
            }

            boolean isOfertaAdmin = "ADMIN".equals(solicitacao.getOrigemCriacao());

            if (isOfertaAdmin) {
                renderizarOfertaAdmin(request, response, solicitacao, user);
            } else {
                renderizarSolicitacaoUser(request, response, solicitacao, user);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
        }
    }

    private void renderizarOfertaAdmin(HttpServletRequest request, HttpServletResponse response, Frete solicitacao, User user) throws IOException {
        PrintWriter out = response.getWriter();
        Long id = solicitacao.getId();
        String statusStr = solicitacao.getStatus() != null ? solicitacao.getStatus() : "-";
        boolean confirmada = "CONFIRMADO".equals(solicitacao.getTipo());
        boolean rejeitada = "ENCERRADO".equals(solicitacao.getTipo());

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Oferta do Admin #" + id + "</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container' style='max-width:700px;'>");
        out.println("<h2>Oferta do Admin #" + id + "</h2>");

        out.println("<div style='background:#f9f9f9; padding:20px; border-radius:8px; margin-bottom:20px;'>");
        out.println("<h3 style='margin-top:0;'>Detalhes do Frete</h3>");
        out.println("<p><strong>Origem:</strong> " + (solicitacao.getOrigem() != null ? solicitacao.getOrigem() : "-") + "</p>");
        out.println("<p><strong>Destino:</strong> " + (solicitacao.getDestino() != null ? solicitacao.getDestino() : "-") + "</p>");
        out.println("<p><strong>Peso:</strong> " + String.format("%.2f kg", solicitacao.getPeso()) + "</p>");
        out.println("<p><strong>Transportadora:</strong> " + (solicitacao.getTransportadora() != null ? solicitacao.getTransportadora() : "-") + "</p>");
        out.println("<p><strong>Valor:</strong> R$ " + String.format("%.2f", solicitacao.getValor()) + "</p>");
        out.println("<p><strong>Data do Frete:</strong> " + (solicitacao.getDataFrete() != null ? solicitacao.getDataFrete() : "-") + "</p>");
        out.println("<p><strong>Observações:</strong> " + (solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "-") + "</p>");
        out.println("<p><strong>Status:</strong> <span class='status-badge status-" + statusStr.toLowerCase().replace(" ", "-") + "'>" + statusStr + "</span></p>");
        out.println("</div>");

        if (confirmada) {
            out.println("<div style='background:#e8f5e9; padding:15px; border-radius:8px; margin-bottom:20px; border-left:4px solid #4caf50;'>");
            out.println("<p style='margin:0; font-weight:600; color:#2e7d32;'>Frete Confirmado!</p>");
            out.println("<p style='margin:5px 0 0; color:#666;'>Você aceitou esta oferta. O frete aparecerá na sua lista de fretes.</p>");
            out.println("</div>");
        } else if (rejeitada) {
            out.println("<div style='background:#ffebee; padding:15px; border-radius:8px; margin-bottom:20px; border-left:4px solid #c62828;'>");
            out.println("<p style='margin:0; font-weight:600; color:#c62828;'>Oferta Rejeitada</p>");
            if (solicitacao.getMotivoRejeicao() != null) {
                out.println("<p style='margin:5px 0 0; color:#666;'>Motivo: " + solicitacao.getMotivoRejeicao() + "</p>");
            }
            out.println("</div>");
        } else {
            out.println("<div style='background:#e3f2fd; padding:15px; border-radius:8px; margin-bottom:20px; border-left:4px solid #2196f3;'>");
            out.println("<p style='margin:0; font-weight:600; color:#1565c0;'>Oferta pendente — sua resposta é necessária.</p>");
            out.println("</div>");

            String erro = request.getParameter("erro");
            if ("motivo-obrigatorio".equals(erro)) {
                out.println("<div class='error-message'>Informe o motivo da recusa.</div>");
            }

            out.println("<h3>Sua Resposta</h3>");
            out.println("<form method='post' action='" + request.getContextPath() + "/userResposta'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<div style='display:flex; gap:10px; align-items:flex-end; flex-wrap:wrap;'>");
            out.println("<div><button type='submit' name='acao' value='aceitar' style='padding:10px 25px; background:#4caf50; color:white; border:none; border-radius:4px; cursor:pointer; font-size:1em;'>✓ Aceitar Frete</button></div>");
            out.println("<div style='flex:1;'><label for='motivoRecusa' style='font-size:0.9em;'>Motivo da Recusa (obrigatório):</label>");
            out.println("<textarea id='motivoRecusa' name='motivoRecusa' rows='2' maxlength='500' style='width:100%; padding:8px; border:1px solid #ddd; border-radius:4px;'></textarea></div>");
            out.println("<div><button type='submit' name='acao' value='rejeitar' style='padding:10px 20px; background:#c0392b; color:white; border:none; border-radius:4px; cursor:pointer; font-size:1em;'>✗ Recusar</button></div>");
            out.println("</div>");
            out.println("</form>");
        }

        out.println("<br><a href='" + request.getContextPath() + "/FreteServlet'>← Voltar para Listar Fretes</a>");
        out.println("</div></body></html>");
    }

    private void renderizarSolicitacaoUser(HttpServletRequest request, HttpServletResponse response, Frete solicitacao, User user) throws IOException {
        PrintWriter out = response.getWriter();
        Long id = solicitacao.getId();
        String statusStr = solicitacao.getStatus() != null ? solicitacao.getStatus() : "-";
        boolean adminRespondeu = solicitacao.getDataRespostaAdmin() != null;

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Minha Solicitação #" + id + "</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container' style='max-width:700px;'>");
        out.println("<h2>Solicitação #" + id + "</h2>");

        out.println("<div style='background:#f9f9f9; padding:20px; border-radius:8px; margin-bottom:20px;'>");
        out.println("<h3 style='margin-top:0;'>Sua Solicitação</h3>");
        out.println("<p><strong>Origem:</strong> " + (solicitacao.getOrigem() != null ? solicitacao.getOrigem() : "-") + "</p>");
        out.println("<p><strong>Destino:</strong> " + (solicitacao.getDestino() != null ? solicitacao.getDestino() : "-") + "</p>");
        out.println("<p><strong>Peso:</strong> " + String.format("%.2f kg", solicitacao.getPeso()) + "</p>");
        out.println("<p><strong>Observações:</strong> " + (solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "-") + "</p>");
        out.println("<p><strong>Status:</strong> <span class='status-badge status-" + statusStr.toLowerCase().replace(" ", "-") + "'>" + statusStr + "</span></p>");
        out.println("</div>");

        if ("Rejeitado".equals(statusStr)) {
            out.println("<div style='background:#ffebee; padding:15px; border-radius:8px; margin-bottom:20px; border-left:4px solid #c62828;'>");
            out.println("<p style='margin:0; font-weight:600; color:#c62828;'>Solicitação Rejeitada pelo Admin</p>");
            if (solicitacao.getMotivoRejeicao() != null) {
                out.println("<p style='margin:5px 0 0; color:#666;'>Motivo: " + solicitacao.getMotivoRejeicao() + "</p>");
            }
            out.println("</div>");

            String erro = request.getParameter("erro");
            if ("motivo-obrigatorio".equals(erro)) {
                out.println("<div class='error-message'>Informe o motivo da recusa.</div>");
            }

            out.println("<div style='display:flex; gap:10px; margin-top:15px;'>");
            out.println("<a href='" + request.getContextPath() + "/editarSolicitacao?id=" + id + "' style='padding:10px 20px; background:#2c7cbd; color:white; text-decoration:none; border-radius:4px; text-align:center;'>✎ Editar para Reenviar</a>");

            out.println("<form method='post' action='" + request.getContextPath() + "/userResposta'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<button type='submit' name='acao' value='cancelar' style='padding:10px 20px; background:#999; color:white; border:none; border-radius:4px; cursor:pointer;'>Cancelar</button>");
            out.println("</form>");
            out.println("</div>");

        } else if (adminRespondeu) {
            out.println("<div style='background:#e8f5e9; padding:20px; border-radius:8px; margin-bottom:20px;'>");
            out.println("<h3 style='margin-top:0;'>Proposta do Admin</h3>");
            out.println("<p><strong>Transportadora:</strong> " + (solicitacao.getTransportadora() != null ? solicitacao.getTransportadora() : "-") + "</p>");
            out.println("<p><strong>Valor:</strong> R$ " + String.format("%.2f", solicitacao.getValor()) + "</p>");
            out.println("<p><strong>Status do Frete:</strong> " + (solicitacao.getStatus() != null ? solicitacao.getStatus() : "-") + "</p>");
            out.println("<p><strong>Data do Frete:</strong> " + (solicitacao.getDataFrete() != null ? solicitacao.getDataFrete() : "-") + "</p>");
            out.println("<p><strong>Data de Entrega:</strong> " + (solicitacao.getDataEntrega() != null ? solicitacao.getDataEntrega() : "-") + "</p>");
            out.println("</div>");

            String erro = request.getParameter("erro");
            if ("motivo-obrigatorio".equals(erro)) {
                out.println("<div class='error-message'>Informe o motivo da recusa.</div>");
            }

            out.println("<h3>Sua Resposta</h3>");
            out.println("<form method='post' action='" + request.getContextPath() + "/userResposta'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<div style='display:flex; gap:10px; align-items:flex-end; flex-wrap:wrap;'>");
            out.println("<div><button type='submit' name='acao' value='aceitar' style='padding:10px 25px; background:#4caf50; color:white; border:none; border-radius:4px; cursor:pointer; font-size:1em;'>✓ Aceitar Frete</button></div>");

            out.println("<div style='flex:1;'><label for='motivoRecusa' style='font-size:0.9em;'>Motivo da Recusa (obrigatório):</label>");
            out.println("<textarea id='motivoRecusa' name='motivoRecusa' rows='2' maxlength='500' style='width:100%; padding:8px; border:1px solid #ddd; border-radius:4px;'></textarea></div>");
            out.println("<div><button type='submit' name='acao' value='rejeitar' style='padding:10px 20px; background:#c0392b; color:white; border:none; border-radius:4px; cursor:pointer; font-size:1em;'>✗ Recusar</button></div>");
            out.println("</div>");
            out.println("</form>");

        } else {
            out.println("<div style='background:#fff3e0; padding:15px; border-radius:8px; margin-bottom:20px;'>");
            out.println("<p style='margin:0;'>Aguardando resposta do administrador...</p>");
            out.println("</div>");

            String editarUrl = request.getContextPath() + "/editarSolicitacao?id=" + id;
            out.println("<div style='display:flex; gap:10px; margin-top:15px;'>");
            out.println("<a href='" + editarUrl + "' style='padding:10px 20px; background:#2c7cbd; color:white; text-decoration:none; border-radius:4px; text-align:center;'>✎ Editar Solicitação</a>");

            out.println("<form method='post' action='" + request.getContextPath() + "/userResposta'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<button type='submit' name='acao' value='cancelar' style='padding:10px 20px; background:#999; color:white; border:none; border-radius:4px; cursor:pointer;'>Cancelar</button>");
            out.println("</form>");
            out.println("</div>");
        }

        out.println("<br><a href='" + request.getContextPath() + "/FreteServlet'>← Voltar para Listar Fretes</a>");
        out.println("</div></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
            return;
        }

        Long id = Long.parseLong(idParam);
        Frete solicitacao = dao.buscarPorId(id);

        if (solicitacao == null || solicitacao.getUser() == null || !solicitacao.getUser().getId().equals(user.getId())) {
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
            return;
        }

        String tipo = solicitacao.getTipo();
        boolean isOfertaAdmin = "ADMIN".equals(solicitacao.getOrigemCriacao());

        if ("aceitar".equals(acao)) {
            dao.confirmarFrete(id);
            response.sendRedirect(request.getContextPath() + "/FreteServlet?sucesso=frete-aceito");

        } else if ("rejeitar".equals(acao)) {
            String motivo = request.getParameter("motivoRecusa");
            if (motivo == null || motivo.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/userResposta?id=" + id + "&erro=motivo-obrigatorio");
                return;
            }
            dao.encerrarSolicitacao(id, motivo.trim());
            response.sendRedirect(request.getContextPath() + "/FreteServlet");

        } else if ("cancelar".equals(acao)) {
            dao.cancelarSolicitacao(id);
            response.sendRedirect(request.getContextPath() + "/FreteServlet");

        } else if ("reenviar".equals(acao)) {
            solicitacao.setStatus("Solicitado");
            solicitacao.setMotivoRejeicao(null);
            solicitacao.setDataRespostaAdmin(null);
            dao.atualizarFrete(solicitacao);
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
        } else {
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
        }
    }
}
