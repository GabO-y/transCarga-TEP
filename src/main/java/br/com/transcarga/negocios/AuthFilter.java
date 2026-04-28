package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String method = req.getMethod();
        System.out.println("[AuthFilter] " + method + " Requisição para URI: " + uri + " (ContextPath: " + contextPath + ")");

        // Libera páginas públicas, logout e recursos estáticos
        if (uri.endsWith("login") || uri.endsWith("login.jsp") || uri.endsWith("cadastrarUsuario.jsp")
                || uri.endsWith("/logout") || uri.endsWith("logout")
                || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg")
                || uri.contains("gerar-hash")) {
            System.out.println("[AuthFilter] Rota pública liberada: " + uri);
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User usuario = (session != null) ? (User) session.getAttribute("user") : null;
        
        System.out.println("[AuthFilter] Usuário na sessão: " + (usuario != null ? usuario.getUsername() + " (Role: " + usuario.getRole() + ")" : "NULO"));

        if (usuario == null) {
            System.out.println("[AuthFilter] Usuário não logado. Redirecionando para login.jsp");
            resp.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // Restrição por papel
        String role = usuario.getRole();
        System.out.println("[AuthFilter] Verificando autorização para role: " + role + " na URI: " + uri);
        
        if ("USER".equals(role)) {
            // USER só pode acessar listar fretes
            if (uri.contains("listarFretes") || uri.contains("FreteServlet")) {
                System.out.println("[AuthFilter] USER autorizado para: " + uri);
                chain.doFilter(request, response);
            } else {
                System.out.println("[AuthFilter] USER não autorizado para: " + uri + ". Redirecionando para listarFretes.jsp");
                resp.sendRedirect(contextPath + "/listarFretes.jsp");
            }
        } else if ("ADMIN".equals(role)) {
            // ADMIN tem acesso irrestrito
            System.out.println("[AuthFilter] ADMIN autorizado para: " + uri);
            chain.doFilter(request, response);
        } else {
            System.out.println("[AuthFilter] Role desconhecida: " + role + ". Redirecionando para login.jsp");
            resp.sendRedirect(contextPath + "/login.jsp");
        }
    }
}
