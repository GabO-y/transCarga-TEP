package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/SolicitacaoServlet")
public class SolicitacaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final FreteDAO dao = new FreteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect("home.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            mostrarDetalhe(request, response, idParam);
        } else {
            mostrarLista(request, response);
        }
    }

    private void mostrarLista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        List<Frete> solicitacoes = dao.listarSolicitacoes();
        List<Frete> ofertasAdmin = dao.listarOfertasAdmin();
        List<Frete> encerradas = dao.listarEncerradasRecentes();

        List<Frete> aguardandoAdmin = solicitacoes.stream()
                .filter(f -> !"ADMIN".equals(f.getOrigemCriacao()) && f.getDataRespostaAdmin() == null)
                .collect(Collectors.toList());
        List<Frete> aguardandoUser = solicitacoes.stream()
                .filter(f -> !"ADMIN".equals(f.getOrigemCriacao()) && f.getDataRespostaAdmin() != null && !"Rejeitado".equals(f.getStatus()))
                .collect(Collectors.toList());
        List<Frete> rejeitadas = solicitacoes.stream()
                .filter(f -> !"ADMIN".equals(f.getOrigemCriacao()) && "Rejeitado".equals(f.getStatus()))
                .collect(Collectors.toList());

        List<Frete> ofertasPendentes = ofertasAdmin.stream()
                .filter(f -> "SOLICITACAO".equals(f.getTipo()))
                .collect(Collectors.toList());
        List<Frete> ofertasConfirmadas = ofertasAdmin.stream()
                .filter(f -> "CONFIRMADO".equals(f.getTipo()))
                .collect(Collectors.toList());
        List<Frete> ofertasRejeitadas = ofertasAdmin.stream()
                .filter(f -> "ENCERRADO".equals(f.getTipo()))
                .collect(Collectors.toList());

        String filtro = request.getParameter("filtro");

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Solicitações - TransCarga</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container'>");
        out.println("<h2>Solicitações de Frete</h2>");

        if (!encerradas.isEmpty()) {
            out.println("<div style='background:#fff3e0; padding:15px; border-radius:8px; margin-bottom:20px; border-left:4px solid #ff9800;'>");
            out.println("<h3 style='margin:0 0 10px; font-size:1em; color:#e65100;'>Solicitações Encerradas pelo Usuário</h3>");
            for (Frete enc : encerradas) {
                String encUserName = (enc.getUser() != null && enc.getUser().getUsername() != null) ? enc.getUser().getUsername() : "-";
                String encMotivo = enc.getMotivoRejeicao() != null ? enc.getMotivoRejeicao() : "Sem motivo informado";
                out.println("<div style='background:white; padding:10px 12px; border-radius:6px; margin-bottom:8px; display:flex; justify-content:space-between; align-items:center; gap:10px;'>");
                out.println("<div style='flex:1;'>");
                out.println("<strong>#" + enc.getId() + " - " + encUserName + "</strong>");
                out.println("<br><span style='font-size:0.85em; color:#666;'>Motivo: " + encMotivo + "</span>");
                out.println("</div>");
                out.println("<div style='display:flex; gap:8px; align-items:center;'>");
                out.println("<a href='" + request.getContextPath() + "/SolicitacaoServlet?id=" + enc.getId() + "' style='font-size:0.85em; color:#2c7cbd;'>Ver detalhes</a>");
                out.println("<form method='post' action='" + request.getContextPath() + "/SolicitacaoServlet' style='display:inline;'>");
                out.println("<input type='hidden' name='id' value='" + enc.getId() + "'>");
                out.println("<button type='submit' name='acao' value='dispensar' style='background:none; border:none; color:#999; cursor:pointer; font-size:1.2em; padding:2px 6px; line-height:1;' title='Dispensar aviso'>✕</button>");
                out.println("</form>");
                out.println("</div>");
                out.println("</div>");
            }
            out.println("</div>");
        }

        boolean temSolicitacoes = !aguardandoAdmin.isEmpty() || !aguardandoUser.isEmpty() || !rejeitadas.isEmpty();
        boolean temOfertas = !ofertasPendentes.isEmpty() || !ofertasConfirmadas.isEmpty() || !ofertasRejeitadas.isEmpty();

        if (temOfertas) {
            out.println("<h3 style='color:#2196f3; margin:20px 0 10px;'>Ofertas Enviadas — Aguardando Aceite (" + ofertasPendentes.size() + ")</h3>");
            if (ofertasPendentes.isEmpty()) {
                out.println("<p style='color:#999; font-style:italic;'>Nenhuma oferta pendente.</p>");
            } else {
                renderizarTabelaOfertas(out, request, ofertasPendentes);
            }

            out.println("<h3 style='color:#4caf50; margin:20px 0 10px;'>Ofertas Aceitas (" + ofertasConfirmadas.size() + ")</h3>");
            if (ofertasConfirmadas.isEmpty()) {
                out.println("<p style='color:#999; font-style:italic;'>Nenhuma oferta aceita.</p>");
            } else {
                renderizarTabelaOfertasConfirmadas(out, ofertasConfirmadas);
            }

            out.println("<h3 style='color:#c0392b; margin:20px 0 10px;'>Ofertas Rejeitadas (" + ofertasRejeitadas.size() + ")</h3>");
            if (ofertasRejeitadas.isEmpty()) {
                out.println("<p style='color:#999; font-style:italic;'>Nenhuma oferta rejeitada.</p>");
            } else {
                renderizarTabelaOfertasRejeitadas(out, ofertasRejeitadas);
            }
        }

        out.println("<h3 style='color:#2c7cbd; margin:20px 0 10px;'>Aguardando Sua Resposta (" + aguardandoAdmin.size() + ")</h3>");
        if (aguardandoAdmin.isEmpty()) {
            out.println("<p style='color:#999; font-style:italic;'>Nenhuma solicitação pendente.</p>");
        } else {
            renderizarTabelaSolicitacao(out, request, aguardandoAdmin);
        }

        out.println("<h3 style='color:#ff9800; margin:20px 0 10px;'>Aguardando Resposta do Usuário (" + aguardandoUser.size() + ")</h3>");
        if (aguardandoUser.isEmpty()) {
            out.println("<p style='color:#999; font-style:italic;'>Nenhuma solicitação aguardando o usuário.</p>");
        } else {
            renderizarTabelaSolicitacao(out, request, aguardandoUser);
        }

        out.println("<h3 style='color:#c0392b; margin:20px 0 10px;'>Rejeitadas (" + rejeitadas.size() + ")</h3>");
        if (rejeitadas.isEmpty()) {
            out.println("<p style='color:#999; font-style:italic;'>Nenhuma solicitação rejeitada.</p>");
        } else {
            renderizarTabelaRejeitadas(out, request, rejeitadas);
        }

        if (!temSolicitacoes && !temOfertas) {
            out.println("<div class='empty-message'><p>Nenhuma solicitação ou oferta encontrada.</p></div>");
        }

        out.println("</div></body></html>");
    }

    private void renderizarTabelaOfertas(PrintWriter out, HttpServletRequest request, List<Frete> lista) {
        out.println("<div class='table-responsive'><table>");
        out.println("<thead><tr><th>ID</th><th>Usuário</th><th>Origem</th><th>Destino</th><th>Peso</th><th>Valor</th><th>Data</th><th>Ações</th></tr></thead>");
        out.println("<tbody>");
        for (Frete f : lista) {
            String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";
            out.println("<tr>");
            out.println("<td>" + f.getId() + "</td>");
            out.println("<td>" + userName + "</td>");
            out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
            out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
            out.printf("<td>%.2f kg</td>", f.getPeso());
            out.printf("<td class='valor-cell'>R$ %.2f</td>", f.getValor());
            out.println("<td>" + (f.getDataFrete() != null ? f.getDataFrete() : "-") + "</td>");
            out.println("<td><a href='" + request.getContextPath() + "/SolicitacaoServlet?id=" + f.getId() + "' style='color:#2c7cbd;'>Ver detalhes</a></td>");
            out.println("</tr>");
        }
        out.println("</tbody></table></div>");
    }

    private void renderizarTabelaOfertasConfirmadas(PrintWriter out, List<Frete> lista) {
        out.println("<div class='table-responsive'><table>");
        out.println("<thead><tr><th>ID</th><th>Usuário</th><th>Origem</th><th>Destino</th><th>Peso</th><th>Valor</th><th>Data</th></tr></thead>");
        out.println("<tbody>");
        for (Frete f : lista) {
            String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";
            out.println("<tr>");
            out.println("<td>" + f.getId() + "</td>");
            out.println("<td>" + userName + "</td>");
            out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
            out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
            out.printf("<td>%.2f kg</td>", f.getPeso());
            out.printf("<td class='valor-cell'>R$ %.2f</td>", f.getValor());
            out.println("<td>" + (f.getDataFrete() != null ? f.getDataFrete() : "-") + "</td>");
            out.println("</tr>");
        }
        out.println("</tbody></table></div>");
    }

    private void renderizarTabelaOfertasRejeitadas(PrintWriter out, List<Frete> lista) {
        out.println("<div class='table-responsive'><table>");
        out.println("<thead><tr><th>ID</th><th>Usuário</th><th>Origem</th><th>Destino</th><th>Motivo</th></tr></thead>");
        out.println("<tbody>");
        for (Frete f : lista) {
            String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";
            out.println("<tr>");
            out.println("<td>" + f.getId() + "</td>");
            out.println("<td>" + userName + "</td>");
            out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
            out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
            out.println("<td style='max-width:200px; overflow:hidden; text-overflow:ellipsis;'>" + (f.getMotivoRejeicao() != null ? f.getMotivoRejeicao() : "-") + "</td>");
            out.println("</tr>");
        }
        out.println("</tbody></table></div>");
    }

    private void renderizarTabelaSolicitacao(PrintWriter out, HttpServletRequest request, List<Frete> lista) {
        out.println("<div class='table-responsive'><table>");
        out.println("<thead><tr><th>ID</th><th>Usuário</th><th>Origem</th><th>Destino</th><th>Peso (kg)</th><th>Status</th><th>Ações</th></tr></thead>");
        out.println("<tbody>");
        for (Frete f : lista) {
            String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";
            String origem = (f.getOrigem() != null) ? f.getOrigem() : "-";
            String destino = (f.getDestino() != null) ? f.getDestino() : "-";
            String statusStr = (f.getStatus() != null) ? f.getStatus() : "-";

            String statusClass = "";
            if ("Solicitado".equals(statusStr)) statusClass = "status-solicitado";
            else if ("Em análise".equals(statusStr)) statusClass = "status-em-analise";
            else if ("Rejeitado".equals(statusStr)) statusClass = "status-rejeitado";
            else if ("Pendente".equals(statusStr)) statusClass = "status-pendente";

            out.println("<tr>");
            out.println("<td>" + f.getId() + "</td>");
            out.println("<td>" + userName + "</td>");
            out.println("<td>" + origem + "</td>");
            out.println("<td>" + destino + "</td>");
            out.printf("<td>%.2f</td>", f.getPeso());
            out.println("<td><span class='status-badge " + statusClass + "'>" + statusStr + "</span></td>");
            out.println("<td><a href='" + request.getContextPath() + "/SolicitacaoServlet?id=" + f.getId() + "' style='color:#2c7cbd;'>Ver detalhes</a></td>");
            out.println("</tr>");
        }
        out.println("</tbody></table></div>");
    }

    private void renderizarTabelaRejeitadas(PrintWriter out, HttpServletRequest request, List<Frete> lista) {
        out.println("<div class='table-responsive'><table>");
        out.println("<thead><tr><th>ID</th><th>Usuário</th><th>Origem</th><th>Destino</th><th>Motivo</th><th>Ações</th></tr></thead>");
        out.println("<tbody>");
        for (Frete f : lista) {
            String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";
            String origem = (f.getOrigem() != null) ? f.getOrigem() : "-";
            String destino = (f.getDestino() != null) ? f.getDestino() : "-";
            String motivo = f.getMotivoRejeicao() != null ? f.getMotivoRejeicao() : "-";

            out.println("<tr>");
            out.println("<td>" + f.getId() + "</td>");
            out.println("<td>" + userName + "</td>");
            out.println("<td>" + origem + "</td>");
            out.println("<td>" + destino + "</td>");
            out.println("<td style='max-width:200px; overflow:hidden; text-overflow:ellipsis;'>" + motivo + "</td>");
            out.println("<td><a href='" + request.getContextPath() + "/SolicitacaoServlet?id=" + f.getId() + "' style='color:#2c7cbd;'>Ver detalhes</a></td>");
            out.println("</tr>");
        }
        out.println("</tbody></table></div>");
    }

    private void mostrarDetalhe(HttpServletRequest request, HttpServletResponse response, String idParam) throws IOException {
        PrintWriter out = response.getWriter();

        try {
            Long id = Long.parseLong(idParam);
            Frete solicitacao = dao.buscarPorId(id);

            if (solicitacao == null) {
                out.println("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'><title>Solicitação não encontrada</title></head>");
                out.println("<body><div class='container'><h2>Solicitação não encontrada</h2>");
                out.println("<a href='" + request.getContextPath() + "/SolicitacaoServlet'>Voltar para lista</a></div></body></html>");
                return;
            }

            String tipo = solicitacao.getTipo() != null ? solicitacao.getTipo() : "";
            String origemCriacao = solicitacao.getOrigemCriacao() != null ? solicitacao.getOrigemCriacao() : "USER";

            if ("ADMIN".equals(origemCriacao)) {
                mostrarDetalheOfertaAdmin(request, response, solicitacao);
                return;
            }

            if (!"SOLICITACAO".equals(tipo) && !"ENCERRADO".equals(tipo)) {
                out.println("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'><title>Solicitação não encontrada</title></head>");
                out.println("<body><div class='container'><h2>Solicitação não encontrada</h2>");
                out.println("<a href='" + request.getContextPath() + "/SolicitacaoServlet'>Voltar para lista</a></div></body></html>");
                return;
            }

            if ("ENCERRADO".equals(tipo)) {
                mostrarDetalheEncerrada(request, response, solicitacao);
                return;
            }

            mostrarDetalheAtiva(request, response, solicitacao);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet");
        }
    }

    private void mostrarDetalheOfertaAdmin(HttpServletRequest request, HttpServletResponse response, Frete solicitacao) throws IOException {
        PrintWriter out = response.getWriter();
        Long id = solicitacao.getId();
        String tipo = solicitacao.getTipo();
        String userName = (solicitacao.getUser() != null && solicitacao.getUser().getUsername() != null) ? solicitacao.getUser().getUsername() : "-";

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Oferta Enviada #" + id + "</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container' style='max-width:700px;'>");
        out.println("<h2>Oferta Enviada #" + id + "</h2>");

        out.println("<div style='background:#f9f9f9; padding:20px; border-radius:8px; margin-bottom:20px;'>");
        out.println("<h3 style='margin-top:0;'>Dados da Oferta</h3>");
        out.println("<p><strong>Usuário:</strong> " + userName + "</p>");
        out.println("<p><strong>Origem:</strong> " + (solicitacao.getOrigem() != null ? solicitacao.getOrigem() : "-") + "</p>");
        out.println("<p><strong>Destino:</strong> " + (solicitacao.getDestino() != null ? solicitacao.getDestino() : "-") + "</p>");
        out.println("<p><strong>Peso:</strong> " + String.format("%.2f kg", solicitacao.getPeso()) + "</p>");
        out.println("<p><strong>Transportadora:</strong> " + (solicitacao.getTransportadora() != null ? solicitacao.getTransportadora() : "-") + "</p>");
        out.println("<p><strong>Valor:</strong> R$ " + String.format("%.2f", solicitacao.getValor()) + "</p>");
        out.println("<p><strong>Data do Frete:</strong> " + (solicitacao.getDataFrete() != null ? solicitacao.getDataFrete() : "-") + "</p>");
        out.println("<p><strong>Observações:</strong> " + (solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "-") + "</p>");

        if ("CONFIRMADO".equals(tipo)) {
            out.println("<div style='background:#e8f5e9; padding:10px; border-radius:6px; margin-top:10px; border-left:4px solid #4caf50;'>");
            out.println("<p style='margin:0; font-weight:600; color:#2e7d32;'>Oferta Aceita pelo Usuário</p>");
            out.println("</div>");
        } else if ("ENCERRADO".equals(tipo)) {
            out.println("<div style='background:#ffebee; padding:10px; border-radius:6px; margin-top:10px; border-left:4px solid #c62828;'>");
            out.println("<p style='margin:0; font-weight:600; color:#c62828;'>Oferta Rejeitada pelo Usuário</p>");
            if (solicitacao.getMotivoRejeicao() != null) {
                out.println("<p style='margin:5px 0 0;'>Motivo: " + solicitacao.getMotivoRejeicao() + "</p>");
            }
            out.println("</div>");
        } else {
            out.println("<div style='background:#e3f2fd; padding:10px; border-radius:6px; margin-top:10px; border-left:4px solid #2196f3;'>");
            out.println("<p style='margin:0; font-weight:600; color:#1565c0;'>Aguardando resposta do usuário...</p>");
            out.println("</div>");
        }
        out.println("</div>");

        out.println("<br><a href='" + request.getContextPath() + "/SolicitacaoServlet'>← Voltar para lista</a>");
        out.println("</div></body></html>");
    }

    private void mostrarDetalheEncerrada(HttpServletRequest request, HttpServletResponse response, Frete solicitacao) throws IOException {
        PrintWriter out = response.getWriter();
        Long id = solicitacao.getId();
        String userName = (solicitacao.getUser() != null && solicitacao.getUser().getUsername() != null) ? solicitacao.getUser().getUsername() : "-";

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Solicitação Encerrada #" + id + "</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container' style='max-width:700px;'>");
        out.println("<h2>Solicitação Encerrada #" + id + "</h2>");

        out.println("<div style='background:#ffebee; padding:20px; border-radius:8px; margin-bottom:20px; border-left:4px solid #c62828;'>");
        out.println("<p style='margin:0; font-weight:600; color:#c62828; font-size:1.1em;'>Esta solicitação foi encerrada pelo usuário.</p>");
        out.println("<p style='margin:5px 0 0; color:#666;'>O usuário recusou a oferta. A solicitação não está mais ativa.</p>");
        out.println("</div>");

        out.println("<div style='background:#f9f9f9; padding:20px; border-radius:8px; margin-bottom:20px;'>");
        out.println("<h3 style='margin-top:0;'>Dados da Solicitação</h3>");
        out.println("<p><strong>Usuário:</strong> " + userName + "</p>");
        out.println("<p><strong>Origem:</strong> " + (solicitacao.getOrigem() != null ? solicitacao.getOrigem() : "-") + "</p>");
        out.println("<p><strong>Destino:</strong> " + (solicitacao.getDestino() != null ? solicitacao.getDestino() : "-") + "</p>");
        out.println("<p><strong>Peso:</strong> " + String.format("%.2f kg", solicitacao.getPeso()) + "</p>");
        out.println("<p><strong>Observações:</strong> " + (solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "-") + "</p>");
        out.println("<p><strong>Motivo da Recusa:</strong> " + (solicitacao.getMotivoRejeicao() != null ? solicitacao.getMotivoRejeicao() : "-") + "</p>");

        if (solicitacao.getDataRespostaAdmin() != null) {
            out.println("<p><strong>Transportadora (oferta anterior):</strong> " + (solicitacao.getTransportadora() != null ? solicitacao.getTransportadora() : "-") + "</p>");
            out.println("<p><strong>Valor (oferta anterior):</strong> R$ " + String.format("%.2f", solicitacao.getValor()) + "</p>");
        }
        out.println("</div>");

        out.println("<br><a href='" + request.getContextPath() + "/SolicitacaoServlet'>← Voltar para lista</a>");
        out.println("</div></body></html>");
    }

    private void mostrarDetalheAtiva(HttpServletRequest request, HttpServletResponse response, Frete solicitacao) throws IOException {
        PrintWriter out = response.getWriter();
        Long id = solicitacao.getId();

        String userName = (solicitacao.getUser() != null && solicitacao.getUser().getUsername() != null) ? solicitacao.getUser().getUsername() : "-";
        String userEndereco = (solicitacao.getUser() != null && solicitacao.getUser().getEndereco() != null) ? solicitacao.getUser().getEndereco() : "-";
        String statusStr = solicitacao.getStatus() != null ? solicitacao.getStatus() : "-";
        boolean adminRespondeu = solicitacao.getDataRespostaAdmin() != null;

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Detalhe da Solicitação #" + id + "</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container' style='max-width:700px;'>");
        out.println("<h2>Solicitação #" + id + "</h2>");

        out.println("<div style='background:#f9f9f9; padding:20px; border-radius:8px; margin-bottom:20px;'>");
        out.println("<h3 style='margin-top:0;'>Dados da Solicitação</h3>");
        out.println("<p><strong>Usuário:</strong> " + userName + "</p>");
        out.println("<p><strong>Endereço do Usuário:</strong> " + userEndereco + "</p>");
        out.println("<p><strong>Origem:</strong> " + (solicitacao.getOrigem() != null ? solicitacao.getOrigem() : "-") + "</p>");
        out.println("<p><strong>Destino:</strong> " + (solicitacao.getDestino() != null ? solicitacao.getDestino() : "-") + "</p>");
        out.println("<p><strong>Peso:</strong> " + String.format("%.2f kg", solicitacao.getPeso()) + "</p>");
        out.println("<p><strong>Observações:</strong> " + (solicitacao.getObservacoes() != null ? solicitacao.getObservacoes() : "-") + "</p>");
        out.println("<p><strong>Status:</strong> <span class='status-badge status-" + statusStr.toLowerCase().replace(" ", "-") + "'>" + statusStr + "</span></p>");

        if ("Rejeitado".equals(statusStr) && solicitacao.getMotivoRejeicao() != null) {
            out.println("<p style='color:#c0392b;'><strong>Motivo da Rejeição:</strong> " + solicitacao.getMotivoRejeicao() + "</p>");
        }
        out.println("</div>");

        if (adminRespondeu) {
            out.println("<div style='background:#fff3e0; padding:20px; border-radius:8px; margin-bottom:20px;'>");
            out.println("<h3 style='margin-top:0;'>Dados Preenchidos pelo Admin</h3>");
            out.println("<p><strong>Transportadora:</strong> " + (solicitacao.getTransportadora() != null ? solicitacao.getTransportadora() : "-") + "</p>");
            out.println("<p><strong>Valor:</strong> R$ " + String.format("%.2f", solicitacao.getValor()) + "</p>");
            out.println("<p><strong>Status do Frete:</strong> " + (solicitacao.getStatus() != null ? solicitacao.getStatus() : "-") + "</p>");
            out.println("<p><strong>Data do Frete:</strong> " + (solicitacao.getDataFrete() != null ? solicitacao.getDataFrete() : "-") + "</p>");
            out.println("<p><strong>Data de Entrega:</strong> " + (solicitacao.getDataEntrega() != null ? solicitacao.getDataEntrega() : "-") + "</p>");
            out.println("</div>");

            if ("Rejeitado".equals(statusStr)) {
                out.println("<form method='post' action='" + request.getContextPath() + "/SolicitacaoServlet'>");
                out.println("<input type='hidden' name='id' value='" + id + "'>");
                out.println("<button type='submit' name='acao' value='rever' style='padding:10px 20px; background:#2c7cbd; color:white; border:none; border-radius:4px; cursor:pointer;'>↺ Rever solicitação</button>");
                out.println("</form>");
            } else {
                out.println("<form method='post' action='" + request.getContextPath() + "/SolicitacaoServlet'>");
                out.println("<input type='hidden' name='id' value='" + id + "'>");
                out.println("<p style='font-size:0.9em; color:#666; margin-bottom:10px;'>Reeditar campos do frete:</p>");
                out.println("<div style='background:#f0f7ff; padding:20px; border-radius:8px; margin-bottom:15px;'>");
                out.println("<div><label for='transportadora'>Transportadora:</label><input type='text' id='transportadora' name='transportadora' value='" + (solicitacao.getTransportadora() != null ? solicitacao.getTransportadora() : "") + "' maxlength='100' required></div>");
                out.println("<div><label for='valor'>Valor (R$):</label><input type='number' id='valor' name='valor' value='" + solicitacao.getValor() + "' step='0.01' min='0.01' required></div>");
                out.println("<div><label for='statusFrete'>Status do Frete:</label><select id='statusFrete' name='statusFrete' required>");
                out.println("<option value='Pendente'" + ("Pendente".equals(solicitacao.getStatus()) ? " selected" : "") + ">Pendente</option>");
                out.println("<option value='Em trânsito'" + ("Em trânsito".equals(solicitacao.getStatus()) ? " selected" : "") + ">Em trânsito</option>");
                out.println("</select></div>");
                out.println("<div><label for='dataFrete'>Data do Frete:</label><input type='date' id='dataFrete' name='dataFrete' value='" + (solicitacao.getDataFrete() != null ? solicitacao.getDataFrete() : "") + "' required></div>");
                out.println("<div><label for='dataEntrega'>Data de Entrega:</label><input type='date' id='dataEntrega' name='dataEntrega' value='" + (solicitacao.getDataEntrega() != null ? solicitacao.getDataEntrega() : "") + "'></div>");
                out.println("</div>");
                out.println("<button type='submit' name='acao' value='reeditar' style='padding:10px 20px; background:#2c7cbd; color:white; border:none; border-radius:4px; cursor:pointer;'>Atualizar Campos</button>");
                out.println("</form>");
            }
        } else {
            out.println("<h3>Responder Solicitação</h3>");

            out.println("<form method='post' action='" + request.getContextPath() + "/SolicitacaoServlet' style='margin-top:20px;'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<div style='background:#f0f7ff; padding:20px; border-radius:8px; margin-bottom:15px;'>");
            out.println("<p style='font-size:0.9em; color:#666;'>Preencha os campos do frete para enviar ao usuário:</p>");
            out.println("<div><label for='transportadora'>Transportadora:</label><input type='text' id='transportadora' name='transportadora' maxlength='100' required></div>");
            out.println("<div><label for='valor'>Valor (R$):</label><input type='number' id='valor' name='valor' step='0.01' min='0.01' required></div>");
            out.println("<div><label for='statusFrete'>Status do Frete:</label><select id='statusFrete' name='statusFrete' required><option value='Pendente'>Pendente</option><option value='Em trânsito'>Em trânsito</option></select></div>");
            out.println("<div><label for='dataFrete'>Data do Frete:</label><input type='date' id='dataFrete' name='dataFrete' required></div>");
            out.println("<div><label for='dataEntrega'>Data de Entrega:</label><input type='date' id='dataEntrega' name='dataEntrega'></div>");
            out.println("</div>");
            out.println("<button type='submit' name='acao' value='aceitar' style='padding:10px 20px; background:#4caf50; color:white; border:none; border-radius:4px; cursor:pointer; font-size:1em;'>✓ Aceitar e Enviar</button>");
            out.println("</form>");

            out.println("<form method='post' action='" + request.getContextPath() + "/SolicitacaoServlet' style='margin-top:15px;'>");
            out.println("<input type='hidden' name='id' value='" + id + "'>");
            out.println("<div><label for='motivoRejeitar'>Motivo da Rejeição:</label>");
            out.println("<textarea id='motivoRejeitar' name='motivoRejeitar' rows='3' maxlength='500' style='width:100%; padding:8px; border:1px solid #ddd; border-radius:4px;' required></textarea></div>");
            out.println("<button type='submit' name='acao' value='rejeitar' style='padding:10px 20px; background:#c0392b; color:white; border:none; border-radius:4px; cursor:pointer; font-size:1em; margin-top:10px;'>✗ Rejeitar</button>");
            out.println("</form>");
        }

        out.println("<br><a href='" + request.getContextPath() + "/SolicitacaoServlet'>← Voltar para lista</a>");
        out.println("</div></body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect("home.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet");
            return;
        }

        Long id = Long.parseLong(idParam);

        if ("aceitar".equals(acao)) {
            String transportadora = request.getParameter("transportadora");
            String valorStr = request.getParameter("valor");
            String statusFrete = request.getParameter("statusFrete");
            String dataFreteStr = request.getParameter("dataFrete");
            String dataEntregaStr = request.getParameter("dataEntrega");

            if (transportadora == null || transportadora.trim().isEmpty() ||
                valorStr == null || valorStr.trim().isEmpty() ||
                statusFrete == null || statusFrete.trim().isEmpty() ||
                dataFreteStr == null || dataFreteStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id + "&erro=campos-obrigatorios");
                return;
            }

            try {
                double valor = Double.parseDouble(valorStr);
                java.time.LocalDate dataFrete = java.time.LocalDate.parse(dataFreteStr);
                java.time.LocalDate dataEntrega = null;
                if (dataEntregaStr != null && !dataEntregaStr.trim().isEmpty()) {
                    dataEntrega = java.time.LocalDate.parse(dataEntregaStr);
                }

                Frete f = dao.buscarPorId(id);
                if (f != null) {
                    f.setTransportadora(transportadora.trim());
                    f.setValor(valor);
                    f.setStatus("Em análise");
                    f.setDataFrete(dataFrete);
                    f.setDataEntrega(dataEntrega);
                    f.setDataRespostaAdmin(java.time.LocalDateTime.now());
                    dao.atualizarFrete(f);
                }
                response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id + "&erro=dados-invalidos");
            }

        } else if ("rejeitar".equals(acao)) {
            String motivo = request.getParameter("motivoRejeitar");
            if (motivo == null || motivo.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id + "&erro=motivo-obrigatorio");
                return;
            }
            dao.rejeitarSolicitacao(id, motivo.trim());
            response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet");

        } else if ("rever".equals(acao)) {
            Frete f = dao.buscarPorId(id);
            if (f != null) {
                f.setStatus("Solicitado");
                f.setDataRespostaAdmin(null);
                f.setMotivoRejeicao(null);
                dao.atualizarFrete(f);
            }
            response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id);

        } else if ("reeditar".equals(acao)) {
            String transportadora = request.getParameter("transportadora");
            String valorStr = request.getParameter("valor");
            String statusFrete = request.getParameter("statusFrete");
            String dataFreteStr = request.getParameter("dataFrete");
            String dataEntregaStr = request.getParameter("dataEntrega");

            try {
                double valor = Double.parseDouble(valorStr);
                java.time.LocalDate dataFrete = java.time.LocalDate.parse(dataFreteStr);
                java.time.LocalDate dataEntrega = null;
                if (dataEntregaStr != null && !dataEntregaStr.trim().isEmpty()) {
                    dataEntrega = java.time.LocalDate.parse(dataEntregaStr);
                }

                Frete f = dao.buscarPorId(id);
                if (f != null) {
                    f.setTransportadora(transportadora.trim());
                    f.setValor(valor);
                    f.setStatus("Em análise");
                    f.setDataFrete(dataFrete);
                    f.setDataEntrega(dataEntrega);
                    dao.atualizarFrete(f);
                }
                response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id + "&erro=dados-invalidos");
            }
        } else if ("dispensar".equals(acao)) {
            dao.dispensarEncerrada(id);
            response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet");
        } else {
            response.sendRedirect(request.getContextPath() + "/SolicitacaoServlet?id=" + id);
        }
    }
}
