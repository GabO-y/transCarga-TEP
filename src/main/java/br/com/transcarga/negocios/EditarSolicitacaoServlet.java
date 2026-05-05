package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/editarSolicitacao")
public class EditarSolicitacaoServlet extends HttpServlet {
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

            if ("Em análise".equals(solicitacao.getStatus())) {
                out.println("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'><title>Edição indisponível</title></head>");
                out.println("<body><div class='container'><h2>Não é possível editar — o admin já respondeu esta solicitação.</h2>");
                out.println("<a href='" + request.getContextPath() + "/userResposta?id=" + id + "'>Ver detalhes</a></div></body></html>");
                return;
            }

            String destinoSugerido = user.getEndereco() != null ? user.getEndereco() : "";

            boolean isRejeitado = "Rejeitado".equals(solicitacao.getStatus());

            out.println("<!DOCTYPE html>");
            out.println("<html lang='pt-BR'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>" + (isRejeitado ? "Reenviar Solicitação #" + id : "Editar Solicitação #" + id) + "</title>");
            out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
            out.println("</head><body style='background:white; padding:20px;'>");
            out.println("<div class='container' style='max-width:600px;'>");
            out.println("<h2>" + (isRejeitado ? "Reenviar Solicitação #" + id : "Editar Solicitação #" + id) + "</h2>");

            if (isRejeitado) {
                out.println("<div style='background:#fff3e0; padding:15px; border-radius:8px; margin-bottom:15px; border-left:4px solid #f57c00;'>");
                out.println("<p style='margin:0; font-weight:600; color:#e65100;'>Esta solicitação foi rejeitada.</p>");
                out.println("<p style='margin:5px 0 0; color:#666; font-size:0.9em;'>Edite os dados abaixo e envie novamente para o admin. A solicitação anterior será substituída.</p>");
                if (solicitacao.getMotivoRejeicao() != null) {
                    out.println("<p style='margin:10px 0 0; font-size:0.9em;'><strong>Motivo anterior:</strong> " + solicitacao.getMotivoRejeicao() + "</p>");
                }
                out.println("</div>");
            }

            String erro = request.getParameter("erro");
            if ("campos-vazios".equals(erro)) {
                out.println("<div class='error-message'>Preencha todos os campos obrigatórios.</div>");
            } else if ("peso-invalido".equals(erro)) {
                out.println("<div class='error-message'>O peso deve ser um número válido maior que zero.</div>");
            }

            out.println("<form method='post' action='" + request.getContextPath() + "/editarSolicitacao'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<div><label for='origem'>Origem:</label><input type='text' id='origem' name='origem' value='" + (solicitacao.getOrigem() != null ? solicitacao.getOrigem() : "") + "' maxlength='100' required></div>");

            boolean enderecoDisponivel = user.getEndereco() != null && !user.getEndereco().trim().isEmpty();
            out.println("<div><label for='destino'>Destino:</label><input type='text' id='destino' name='destino' value='" + (solicitacao.getDestino() != null ? solicitacao.getDestino() : "") + "' maxlength='100' " + (enderecoDisponivel ? "readonly title='Preenchido automaticamente com seu endereço.' style='background:#f5f5f5;'" : "required") + "></div>");

            out.println("<div><label for='peso'>Peso (kg):</label><input type='number' id='peso' name='peso' step='0.01' min='0.01' value='" + solicitacao.getPeso() + "' required></div>");
            out.println("<div><label for='observacoes'>Observações:</label><textarea id='observacoes' name='observacoes' rows='4' maxlength='500'>" + (solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "") + "</textarea></div>");
            out.println("<input type='submit' value='" + (isRejeitado ? "Reenviar Solicitação" : "Salvar Alterações") + "'>");
            out.println("</form>");

            out.println("<br><a href='" + request.getContextPath() + "/userResposta?id=" + id + "'>← Voltar</a>");
            out.println("</div></body></html>");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
        }
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
        String idParam = request.getParameter("id");
        String origem = request.getParameter("origem");
        String destino = request.getParameter("destino");
        String pesoStr = request.getParameter("peso");
        String observacoes = request.getParameter("observacoes");

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/FreteServlet");
            return;
        }

        if (origem == null || origem.trim().isEmpty() ||
            destino == null || destino.trim().isEmpty() ||
            pesoStr == null || pesoStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/editarSolicitacao?id=" + idParam + "&erro=campos-vazios");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
            if (peso <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/editarSolicitacao?id=" + idParam + "&erro=peso-invalido");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            Frete solicitacao = dao.buscarPorId(id);

            if (solicitacao == null || !"SOLICITACAO".equals(solicitacao.getTipo()) ||
                solicitacao.getUser() == null || !solicitacao.getUser().getId().equals(user.getId())) {
                response.sendRedirect(request.getContextPath() + "/FreteServlet");
                return;
            }

            solicitacao.setOrigem(origem.trim());
            solicitacao.setDestino(destino.trim());
            solicitacao.setPeso(peso);
            solicitacao.setObservacoes(observacoes);
            solicitacao.setStatus("Solicitado");
            solicitacao.setMotivoRejeicao(null);
            solicitacao.setDataRespostaAdmin(null);
            dao.atualizarFrete(solicitacao);

            response.sendRedirect(request.getContextPath() + "/userResposta?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/editarSolicitacao?id=" + idParam + "&erro=" + e.getMessage());
        }
    }
}
