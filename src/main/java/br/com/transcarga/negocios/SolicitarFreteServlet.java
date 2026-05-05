package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/solicitarFrete")
public class SolicitarFreteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String destinoSugerido = user.getEndereco() != null ? user.getEndereco() : "";
        boolean enderecoDisponivel = user.getEndereco() != null && !user.getEndereco().trim().isEmpty();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='pt-BR'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Solicitar Frete - TransCarga</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
        out.println("</head><body style='background:white; padding:20px;'>");
        out.println("<div class='container' style='max-width:600px;'>");
        out.println("<h2>Solicitar Frete</h2>");

        String erro = request.getParameter("erro");
        if ("campos-vazios".equals(erro)) {
            out.println("<div class='error-message'>Preencha todos os campos obrigatórios.</div>");
        } else if ("peso-invalido".equals(erro)) {
            out.println("<div class='error-message'>O peso deve ser um número válido maior que zero.</div>");
        } else if ("sucesso".equals(request.getParameter("sucesso"))) {
            out.println("<div style='background:#e8f5e9;color:#2e7d32;padding:10px;border-radius:4px;margin-bottom:15px;text-align:center;'>Solicitação enviada com sucesso! Aguarde a resposta do admin.</div>");
        }

        out.println("<form method='post' action='" + request.getContextPath() + "/solicitarFrete'>");
        out.println("<div><label for='origem'>Origem:</label><input type='text' id='origem' name='origem' maxlength='100' required></div>");
        out.println("<div><label for='destino'>Destino:</label><input type='text' id='destino' name='destino' maxlength='100' value='" + destinoSugerido + "' " + (enderecoDisponivel ? "readonly title='Preenchido automaticamente com seu endereço. Contate o admin para alterar.' style='background:#f5f5f5;'" : "required") + "></div>");
        if (!enderecoDisponivel) {
            out.println("<small style='color:#e67e22;'>Seu endereço não está cadastrado. Preencha o destino manualmente.</small>");
        }
        out.println("<div><label for='peso'>Peso (kg):</label><input type='number' id='peso' name='peso' step='0.01' min='0.01' required></div>");
        out.println("<div><label for='observacoes'>Observações:</label><textarea id='observacoes' name='observacoes' rows='4' maxlength='500'></textarea></div>");
        out.println("<input type='submit' value='Enviar Solicitação'>");
        out.println("</form>");
        out.println("</div>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String origem = request.getParameter("origem");
        String destino = request.getParameter("destino");
        String pesoStr = request.getParameter("peso");
        String observacoes = request.getParameter("observacoes");

        if (origem == null || origem.trim().isEmpty() ||
            destino == null || destino.trim().isEmpty() ||
            pesoStr == null || pesoStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/solicitarFrete?erro=campos-vazios");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
            if (peso <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/solicitarFrete?erro=peso-invalido");
            return;
        }

        try {
            Frete solicitacao = new Frete();
            solicitacao.setOrigem(origem.trim());
            solicitacao.setDestino(destino.trim());
            solicitacao.setPeso(peso);
            solicitacao.setObservacoes(observacoes);
            solicitacao.setStatus("Solicitado");
            solicitacao.setTipo("SOLICITACAO");
            solicitacao.setOrigemCriacao("USER");
            solicitacao.setUser(user);
            solicitacao.setDataRespostaAdmin(null);

            FreteDAO dao = new FreteDAO();
            dao.cadastrarSolicitacao(solicitacao);
            System.out.println("[SolicitarFreteServlet] Solicitação criada por " + user.getUsername());

            response.sendRedirect(request.getContextPath() + "/solicitarFrete?sucesso=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/solicitarFrete?erro=" + e.getMessage());
        }
    }
}
