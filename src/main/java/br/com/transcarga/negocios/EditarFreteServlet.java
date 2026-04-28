package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

@WebServlet("/EditarFreteServlet")
public class EditarFreteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String acao = request.getParameter("acao");

        // Se não tem ID, mostra formulário de busca
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession(false);
            boolean isAdmin = false;
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null && user.getRole() != null) {
                    isAdmin = "admin".equalsIgnoreCase(user.getRole());
                }
            }

            out.println("<!DOCTYPE html>");
            out.println("<html lang='pt-BR'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Editar Frete</title>");
            out.println("    <link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container' style='max-width: 400px;'>");
            out.println("        <h2>🔍 Buscar Entrega para Editar</h2>");
            out.println("        <form method='get' action='/transcarga/EditarFreteServlet'>");
            out.println("            <div>");
            out.println("                <label for='id'>ID da Entrega:</label>");
            out.println("                <input type='number' id='id' name='id' min='1' required>");
            out.println("            </div>");
            out.println("            <input type='submit' value='🔍 Buscar'>");
            out.println("        </form>");
            if (isAdmin) {
                out.println("        <a href='home.jsp' class='nav-link'>← Voltar para Home</a>");
            } else {
                out.println("        <a href='logout' class='nav-link'>← Sair (Logout)</a>");
            }
            out.println("    </div>");
            out.println("    <footer>");
            out.println("        © Mossoró, 2025 - TransCarga");
            out.println("    </footer>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            FreteDAO dao = new FreteDAO();
            Frete frete = dao.buscarPorId(id);

            if (frete == null) {
                HttpSession session = request.getSession(false);
                boolean isAdmin = false;
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    if (user != null && user.getRole() != null) {
                        isAdmin = "admin".equalsIgnoreCase(user.getRole());
                    }
                }

                out.println("<!DOCTYPE html>");
                out.println("<html lang='pt-BR'>");
                out.println("<head>");
                out.println("    <meta charset='UTF-8'>");
                out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                out.println("    <title>Editar Frete</title>");
                out.println("    <link rel='stylesheet' href='style.css'>");
                out.println("</head>");
                out.println("<body>");
                out.println("    <div class='container' style='max-width: 400px;'>");
                out.println("        <div class='error-message'>");
                out.println("            <p>❌ Entrega não encontrada!</p>");
                out.println("            <p>ID: " + id + "</p>");
                out.println("        </div>");
                out.println("        <form method='get' action='/transcarga/EditarFreteServlet'>");
                out.println("            <div>");
                out.println("                <label for='id'>Tente outro ID:</label>");
                out.println("                <input type='number' id='id' name='id' min='1' required>");
                out.println("            </div>");
                out.println("            <input type='submit' value='🔍 Buscar'>");
                out.println("        </form>");
                out.println("        <a href='listarFretes.jsp' class='nav-link'>📦 Ver Listagem</a>");
                if (isAdmin) {
                    out.println("        <a href='home.jsp' class='nav-link'>← Voltar para Home</a>");
                } else {
                    out.println("        <a href='logout' class='nav-link'>← Sair (Logout)</a>");
                }
                out.println("    </div>");
                out.println("    <footer>");
                out.println("        © Mossoró, 2025 - TransCarga");
                out.println("    </footer>");
                out.println("</body>");
                out.println("</html>");
                return;
            }

            // Exibe formulário de edição
            out.println("<!DOCTYPE html>");
            out.println("<html lang='pt-BR'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>Editar Frete</title>");
            out.println("    <link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container'>");
            out.println("        <h2>✏️ Editar Entrega #" + frete.getId() + "</h2>");
            out.println("        <form method='post' action='/transcarga/EditarFreteServlet' accept-charset='UTF-8'>");
            out.println("            <input type='hidden' name='id' value='" + frete.getId() + "'>");
            out.println("            <div>");
            out.println("                <label for='origem'>Origem:</label>");
            out.println("                <input type='text' id='origem' name='origem' maxlength='100' value='"
                    + (frete.getOrigem() != null ? frete.getOrigem() : "") + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='destino'>Destino:</label>");
            out.println("                <input type='text' id='destino' name='destino' maxlength='100' value='"
                    + (frete.getDestino() != null ? frete.getDestino() : "") + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='peso'>Peso (kg):</label>");
            out.println("                <input type='number' id='peso' name='peso' step='0.01' min='0' value='"
                    + frete.getPeso() + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='valor'>Valor (R$):</label>");
            out.println("                <input type='number' id='valor' name='valor' step='0.01' min='0' value='"
                    + frete.getValor() + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='transportadora'>Transportadora:</label>");
            out.println(
                    "                <input type='text' id='transportadora' name='transportadora' maxlength='100' value='"
                            + (frete.getTransportadora() != null ? frete.getTransportadora() : "") + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='status'>Status:</label>");
            out.println("                <select id='status' name='status' required>");
            out.println("                    <option value='Pendente' "
                    + ("Pendente".equals(frete.getStatus()) ? "selected" : "") + ">Pendente</option>");
            out.println("                    <option value='Em trânsito' "
                    + ("Em trânsito".equals(frete.getStatus()) ? "selected" : "") + ">Em trânsito</option>");
            out.println("                    <option value='Entregue' "
                    + ("Entregue".equals(frete.getStatus()) ? "selected" : "") + ">Entregue</option>");
            out.println("                </select>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='dataFrete'>Data do Frete:</label>");
            out.println("                <input type='date' id='dataFrete' name='dataFrete' value='"
                    + frete.getDataFrete() + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='dataEntrega'>Data de Entrega (Prevista):</label>");
            out.println("                <input type='date' id='dataEntrega' name='dataEntrega' value='"
                    + frete.getDataEntrega() + "' required>");
            out.println("            </div>");
            out.println("            <div>");
            out.println("                <label for='observacoes'>Observações:</label>");
            out.println("                <textarea id='observacoes' name='observacoes' maxlength='500' rows='3'>"
                    + (frete.getObservacoes() != null ? frete.getObservacoes() : "") + "</textarea>");
            out.println("            </div>");
            out.println("            <input type='submit' value='✓ Salvar Alterações'>");
            out.println("        </form>");

            HttpSession session = request.getSession(false);
            boolean isAdmin = false;
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null && user.getRole() != null) {
                    isAdmin = "admin".equalsIgnoreCase(user.getRole());
                }
            }

            if (isAdmin) {
                out.println("        <a href='home.jsp' class='nav-link'>← Voltar para Home</a>");
            } else {
                out.println("        <a href='logout' class='nav-link'>← Sair (Logout)</a>");
            }

            out.println("    </div>");
            out.println("    <footer>");
            out.println("        © Mossoró, 2025 - TransCarga");
            out.println("    </footer>");
            out.println("</body>");
            out.println("</html>");

        } catch (NumberFormatException e) {
            out.println("ID inválido!");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        try {
            Long id = Long.parseLong(request.getParameter("id"));
            String origem = request.getParameter("origem");
            String destino = request.getParameter("destino");
            double peso = Double.parseDouble(request.getParameter("peso"));
            double valor = Double.parseDouble(request.getParameter("valor"));
            String transportadora = request.getParameter("transportadora");
            String status = request.getParameter("status");
            LocalDate dataFrete = LocalDate.parse(request.getParameter("dataFrete"));
            LocalDate dataEntrega = LocalDate.parse(request.getParameter("dataEntrega"));
            String observacoes = request.getParameter("observacoes");

            Frete frete = new Frete();
            frete.setId(id);
            frete.setOrigem(origem);
            frete.setDestino(destino);
            frete.setPeso(peso);
            frete.setValor(valor);
            frete.setTransportadora(transportadora);
            frete.setStatus(status);
            frete.setDataFrete(dataFrete);
            frete.setDataEntrega(dataEntrega);
            frete.setObservacoes(observacoes);

            FreteDAO dao = new FreteDAO();
            dao.atualizarFrete(frete);

            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html lang='pt-BR'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <title>Sucesso</title>");
            out.println("    <link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("    <div class='container' style='max-width: 400px;'>");
            out.println("        <div class='success-message'>");
            out.println("            <p>✅ Entrega atualizada com sucesso!</p>");
            out.println("        </div>");
            out.println("        <a href='listarFretes.jsp' class='nav-link'>📦 Ver Listagem</a>");

            HttpSession session = request.getSession(false);
            boolean isAdmin = false;
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null && user.getRole() != null) {
                    isAdmin = "admin".equalsIgnoreCase(user.getRole());
                }
            }

            if (isAdmin) {
                out.println("        <a href='home.jsp' class='nav-link'>← Voltar para Home</a>");
            } else {
                out.println("        <a href='logout' class='nav-link'>← Sair (Logout)</a>");
            }

            out.println("    </div>");
            out.println("    <footer>");
            out.println("        © Mossoró, 2025 - TransCarga");
            out.println("    </footer>");
            out.println("</body>");
            out.println("</html>");

        } catch (Exception e) {
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.println("Erro ao atualizar frete: " + e.getMessage());
        }
    }
}
