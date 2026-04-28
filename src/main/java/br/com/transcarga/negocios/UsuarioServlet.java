package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.UserDAO;
import br.com.transcarga.persistencia.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "usuario", urlPatterns = {"/usuario", "/listarUsuarios.html"})
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        System.out.println("[UsuarioServlet] POST recebido - username: " + username + ", role: " + role + ", password length: " + (password != null ? password.length() : "null"));
        
        if (username == null || password == null || role == null || username.isEmpty() || password.isEmpty()) {
            System.out.println("[UsuarioServlet] ERRO: Campos obrigatórios vazios!");
            response.sendRedirect("cadastrarUsuario.html?error=campos_vazios");
            return;
        }
        
        try {
            UserDAO dao = new UserDAO();
            dao.cadastrar(username, password, role);
            System.out.println("[UsuarioServlet] Usuário cadastrado com sucesso: " + username);
            response.sendRedirect("listarUsuarios.html?success=1");
        } catch (Exception e) {
            System.out.println("[UsuarioServlet] ERRO ao cadastrar: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("cadastrarUsuario.html?error=" + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[UsuarioServlet] GET - Listando usuários");
        try {
            UserDAO dao = new UserDAO();
            List<User> usuarios = dao.listar();
            System.out.println("[UsuarioServlet] Encontrados " + (usuarios != null ? usuarios.size() : "0") + " usuários");
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("/listarUsuarios.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("[UsuarioServlet] ERRO ao listar: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("home.jsp");
        }
    }
}
