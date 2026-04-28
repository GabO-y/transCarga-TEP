package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.User;
import br.com.transcarga.persistencia.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        System.out.println("[LOGIN] Tentativa de login - Username: " + username + ", Password: " + password);
        
        UserDAO dao = new UserDAO();
        User user = dao.autenticar(username, password);
        
        System.out.println("[LOGIN] Resultado da autenticação: " + (user != null ? "SUCESSO - Role: " + user.getRole() : "FALHOU"));
        
        if (user != null) {
            req.getSession().setAttribute("user", user);
            String role = user.getRole();
            if ("admin".equalsIgnoreCase(role)) {
                System.out.println("[LOGIN] Admin " + username + " logado. Redirecionando para home.jsp");
                resp.sendRedirect("home.jsp");
            } else {
                System.out.println("[LOGIN] User " + username + " logado. Redirecionando para listarFretes.jsp");
                resp.sendRedirect("listarFretes.jsp");
            }
        } else {
            System.out.println("[LOGIN] Falha no login para " + username + ". Redirecionando para login.jsp?error=1");
            resp.sendRedirect("login.jsp?error=1");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("[LOGIN] GET request - Encaminhando para login.jsp");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
