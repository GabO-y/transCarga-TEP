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

        // Libera páginas públicas, logout e recursos estáticos
        if (uri.endsWith("login") || uri.endsWith("login.jsp") || uri.endsWith("cadastrarUsuario.jsp")
                || uri.endsWith("/logout") || uri.endsWith("logout")
                || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg")
                || uri.contains("gerar-hash")
                || uri.endsWith("usuario")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User usuario = (session != null) ? (User) session.getAttribute("user") : null;

        if (usuario == null) {
            resp.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // Restrição por papel
        String role = usuario.getRole();

        if ("USER".equals(role)) {
            // USER pode acessar: home, listar fretes, solicitar frete, editar solicitação, responder, minhas solicitações
            if (uri.endsWith("home.jsp") || uri.contains("listarFretes") || uri.contains("FreteServlet")
                    || uri.contains("solicitarFrete") || uri.contains("editarSolicitacao") || uri.contains("userResposta")
                    || uri.contains("minhasSolicitacoes")) {
                chain.doFilter(request, response);
            } else {
                resp.sendRedirect(contextPath + "/home.jsp");
            }
        } else if ("ADMIN".equals(role)) {
            // ADMIN tem acesso irrestrito
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(contextPath + "/login.jsp");
        }
    }
}
