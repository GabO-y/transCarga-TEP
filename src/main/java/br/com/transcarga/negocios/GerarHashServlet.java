package br.com.transcarga.negocios;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;

@WebServlet("/gerar-hash")
public class GerarHashServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String senha = req.getParameter("senha");
        if (senha == null) senha = "admin123";
        
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("<h2>Hash BCrypt para: " + senha + "</h2>");
        resp.getWriter().println("<p style='font-family: monospace; background: #f0f0f0; padding: 10px;'>" + hash + "</p>");
        resp.getWriter().println("<p>Use este hash no SQL: <br>UPDATE usuario SET password='" + hash + "' WHERE username='admin';</p>");
    }
}
