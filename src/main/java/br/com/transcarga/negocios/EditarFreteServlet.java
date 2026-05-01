package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import br.com.transcarga.persistencia.User;
import br.com.transcarga.persistencia.UserDAO;
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

        String idParam = request.getParameter("id");
        String acao = request.getParameter("acao");

        // Se não tem ID, mostra formulário de busca
        if (idParam == null || idParam.isEmpty()) {
            HttpSession session = request.getSession(false);
            boolean isAdmin = false;
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null && user.getRole() != null) {
                    isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
                }
            }

            if (!isAdmin) {
                response.sendRedirect(request.getContextPath() + "/listarFretes.jsp?erro=sem-permissao");
                return;
            }

            request.getRequestDispatcher("/buscarFrete.jsp").forward(request, response);
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            FreteDAO dao = new FreteDAO();
            Frete frete = dao.buscarPorId(id);

            if (frete == null) {
                request.setAttribute("id", id);
                request.getRequestDispatcher("/erroFrete.jsp").forward(request, response);
                return;
            }

            // Exibe formulário de edição via JSP
            request.setAttribute("frete", frete);
            request.getRequestDispatcher("/editarFrete.jsp").forward(request, response);
            return;

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/buscarFrete.jsp?erro=id-invalido");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // Verifica se é ADMIN
        HttpSession sessaoPost = request.getSession(false);
        boolean isAdminPost = false;
        if (sessaoPost != null) {
            User usuarioLogadoPost = (User) sessaoPost.getAttribute("user");
            if (usuarioLogadoPost != null && usuarioLogadoPost.getRole() != null) {
                isAdminPost = "ADMIN".equalsIgnoreCase(usuarioLogadoPost.getRole());
            }
        }
        if (!isAdminPost) {
            response.sendRedirect("listarFretes.jsp?erro=sem-permissao");
            return;
        }

        try {
            Long id = Long.parseLong(request.getParameter("id"));
            FreteDAO freteDAO = new FreteDAO();
            Frete frete = freteDAO.buscarPorId(id);

            if (frete == null) {
                response.sendRedirect("listarFretes.jsp?erro=frete-nao-encontrado");
                return;
            }

            String origem = request.getParameter("origem");
            String destino = request.getParameter("destino");
            double peso = Double.parseDouble(request.getParameter("peso"));
            double valor = Double.parseDouble(request.getParameter("valor"));
            String transportadora = request.getParameter("transportadora");
            String status = request.getParameter("status");
            LocalDate dataFrete = LocalDate.parse(request.getParameter("dataFrete"));
            LocalDate dataEntrega = LocalDate.parse(request.getParameter("dataEntrega"));
            String observacoes = request.getParameter("observacoes");
            String userIdStr = request.getParameter("userId");

            frete.setOrigem(origem);
            frete.setDestino(destino);
            frete.setPeso(peso);
            frete.setValor(valor);
            frete.setTransportadora(transportadora);
            frete.setStatus(status);
            frete.setDataFrete(dataFrete);
            frete.setDataEntrega(dataEntrega);
            frete.setObservacoes(observacoes);

            // Atualiza usuário associado se informado
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                try {
                    UserDAO userDAO = new UserDAO();
                    User novoUser = userDAO.buscarPorId(Long.parseLong(userIdStr));
                    frete.setUser(novoUser);
                } catch (NumberFormatException e) {
                    // ID inválido, mantém o usuário atual
                }
            } else {
                frete.setUser(null); // remove associação se campo vazio
            }

            freteDAO.atualizarFrete(frete);
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("  if (window.parent) {");
            out.println("    window.parent.document.getElementById('mainFrame').src = '" + request.getContextPath() + "/FreteServlet';");
            out.println("  } else {");
            out.println("    window.location.href = '" + request.getContextPath() + "/home.jsp';");
            out.println("  }");
            out.println("</script>");
            return;

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("  if (window.parent) {");
            out.println("    window.parent.document.getElementById('mainFrame').src = '" + request.getContextPath() + "/FreteServlet';");
            out.println("  } else {");
            out.println("    window.location.href = '" + request.getContextPath() + "/home.jsp';");
            out.println("  }");
            out.println("</script>");
        }
    }
}
