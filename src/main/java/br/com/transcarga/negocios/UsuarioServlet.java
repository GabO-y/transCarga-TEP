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
        boolean isPublico = request.getParameter("publico") != null;

        System.out.println("[UsuarioServlet] POST - username: " + username + ", publico: " + isPublico);

        // Se for cadastro público (link "Cadastrar-se"), força role USER
        if (isPublico) {
            role = "USER";
        }

        // Validações
        if (username == null || password == null || role == null ||
            username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("[UsuarioServlet] ERRO: Campos vazios");
            response.sendRedirect("cadastrarUsuario.jsp?erro=campos-vazios");
            return;
        }

        // Apenas admin pode criar outros admins (quando não é cadastro público)
        if ("ADMIN".equalsIgnoreCase(role) && !isPublico) {
            HttpSession sessao = request.getSession(false);
            boolean isAdmin = false;
            if (sessao != null) {
                User userLogado = (User) sessao.getAttribute("user");
                if (userLogado != null && userLogado.getRole() != null) {
                    isAdmin = "ADMIN".equalsIgnoreCase(userLogado.getRole());
                }
            }
            if (!isAdmin) {
                System.out.println("[UsuarioServlet] ERRO: Sem permissão de admin");
                response.sendRedirect("cadastrarUsuario.jsp?erro=sem-permissao-admin");
                return;
            }
        }

        try {
            UserDAO dao = new UserDAO();

            // O UserDAO.cadastrar chama o construtor do User, que já aplica BCrypt.hashpw
            // Verifica se usuário já existe
            User existente = dao.buscarPorUsername(username.trim());
            if (existente != null) {
                System.out.println("[UsuarioServlet] ERRO: Usuário já existe");
                response.sendRedirect("cadastrarUsuario.jsp?erro=usuario-existente");
                return;
            }

            // Cadastra usando o método existente no UserDAO (senha é hasheada no construtor do User)
            dao.cadastrar(username.trim(), password, role);
            System.out.println("[UsuarioServlet] Usuário cadastrado com sucesso: " + username + " (role: " + role + ")");

            if (isPublico) {
                // Cadastro público: redireciona para login
                response.sendRedirect("login.jsp?sucesso=cadastrado");
            } else {
                // Admin: redireciona para lista de usuários
                response.sendRedirect("listarUsuarios.html");
            }
        } catch (Exception e) {
            System.out.println("[UsuarioServlet] ERRO ao cadastrar: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("cadastrarUsuario.jsp?erro=" + e.getMessage());
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
