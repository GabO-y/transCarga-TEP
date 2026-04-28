package br.com.transcarga.negocios;

import br.com.transcarga.persistencia.Frete;
import br.com.transcarga.persistencia.FreteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/FreteServlet")
public class FreteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final FreteDAO dao = new FreteDAO();

	private Map<String, String> getStatusColor(String status) {
		Map<String, String> colorMap = new HashMap<>();
		if ("Pendente".equals(status)) {
			colorMap.put("color", "#ff9800");
			colorMap.put("bg", "#fff3e0");
		} else if ("Em trânsito".equals(status)) {
			colorMap.put("color", "#2196f3");
			colorMap.put("bg", "#e3f2fd");
		} else if ("Entregue".equals(status)) {
			colorMap.put("color", "#4caf50");
			colorMap.put("bg", "#e8f5e9");
		} else {
			colorMap.put("color", "#666");
			colorMap.put("bg", "#f5f5f5");
		}
		return colorMap;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<Frete> fretes = dao.listarFretes();

		// Filtro por status
		String filtroStatus = request.getParameter("filtroStatus");
		if (filtroStatus != null && !filtroStatus.isEmpty()) {
			String statusFiltro = filtroStatus;
			fretes = fretes.stream()
					.filter(f -> f.getStatus() != null && f.getStatus().equals(statusFiltro))
					.collect(Collectors.toList());
		}

		// Filtro por transportadora
		String filtroTransportadora = request.getParameter("filtroTransportadora");
		if (filtroTransportadora != null && !filtroTransportadora.isEmpty()) {
			String transpFiltro = filtroTransportadora.toLowerCase();
			fretes = fretes.stream()
					.filter(f -> f.getTransportadora() != null
							&& f.getTransportadora().toLowerCase().contains(transpFiltro))
					.collect(Collectors.toList());
		}

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html lang='pt-BR'><head>");
		out.println("<meta charset='UTF-8'>");
		out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
		out.println("<title>Listar Fretes</title>");
		out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/style.css'>");
		out.println("</head><body style='background: white; padding: 20px;'>");

		// Formulário de filtro
		out.println("<div class='filtro-container'>");
		out.println("<form method='get' action='/transcarga/FreteServlet' class='filtro-form'>");
		out.println("<select name='filtroStatus' class='filtro-select'>");
		out.println("<option value=''>Todos os Status</option>");
		out.println("<option value='Pendente' " + ("Pendente".equals(filtroStatus) ? "selected" : "")
				+ ">Pendente</option>");
		out.println("<option value='Em trânsito' " + ("Em trânsito".equals(filtroStatus) ? "selected" : "")
				+ ">Em trânsito</option>");
		out.println("<option value='Entregue' " + ("Entregue".equals(filtroStatus) ? "selected" : "")
				+ ">Entregue</option>");
		out.println("</select>");
		out.println("<input type='text' name='filtroTransportadora' placeholder='Filtrar por transportadora' value='"
				+ (filtroTransportadora != null ? filtroTransportadora : "") + "' class='filtro-input'>");
		out.println("<button type='submit' class='filtro-btn'>🔍 Filtrar</button>");
		out.println("<a href='/transcarga/FreteServlet' class='filtro-btn-reset'>Limpar Filtros</a>");
		out.println("</form>");
		out.println("</div>");

		// Verifica se há fretes
		if (fretes.isEmpty()) {
			out.println("<div class='empty-message'>");
			out.println("<p>📦 Nenhuma Entrega Encontrada</p>");
			out.println("</div>");
		} else {
			out.println("<div class='table-responsive'>");
			out.println("<table>");
			out.println("<thead>"
					+ "<tr>"
					+ "<th>ID</th>"
					+ "<th>Origem</th>"
					+ "<th>Destino</th>"
					+ "<th>Peso (kg)</th>"
					+ "<th>Valor (R$)</th>"
					+ "<th>Transportadora</th>"
					+ "<th>Status</th>"
					+ "<th>Data Frete</th>"
					+ "<th>Data Entrega</th>"
					+ "</tr></thead>");

			for (Frete f : fretes) {
				// Tratar valores null ou inválidos
				String origem = (f.getOrigem() == null || f.getOrigem().trim().isEmpty()) ? "-" : f.getOrigem();
				String destino = (f.getDestino() == null || f.getDestino().trim().isEmpty()) ? "-" : f.getDestino();
				String transportadora = (f.getTransportadora() == null || f.getTransportadora().trim().isEmpty()) ? "-" : f.getTransportadora();
				String statusStr = (f.getStatus() == null || f.getStatus().trim().isEmpty()) ? "-" : f.getStatus();
				String dataFrete = (f.getDataFrete() == null) ? "-" : f.getDataFrete().toString();
				String dataEntrega = (f.getDataEntrega() == null) ? "-" : f.getDataEntrega().toString();

				// Determinar a classe CSS do status baseado no status original
				String statusClass = "";
				if (f.getStatus() != null) {
					if ("Pendente".equals(f.getStatus())) {
						statusClass = "status-pendente";
					} else if ("Em trânsito".equals(f.getStatus())) {
						statusClass = "status-em-transito";
					} else if ("Entregue".equals(f.getStatus())) {
						statusClass = "status-entregue";
					}
				}

				out.printf(
						"<tr><td>%d</td><td>%s</td><td>%s</td><td>%.2f</td><td class='valor-cell'>R$ %.2f</td><td>%s</td><td class='status-badge %s'>%s</td><td class='data-cell'>%s</td><td class='data-cell'>%s</td></tr>",
						f.getId(), origem, destino, f.getPeso(), f.getValor(), transportadora,
						statusClass, statusStr, dataFrete, dataEntrega);
			}
			out.println("</tbody></table>");
			out.println("</div>");
		}
		out.println("</body></html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String origem = request.getParameter("origem");
		String destino = request.getParameter("destino");
		double peso = Double.parseDouble(request.getParameter("peso"));
		double valor = Double.parseDouble(request.getParameter("valor"));
		String transportadora = request.getParameter("transportadora");
		String status = "Pendente"; // Status padrão
		LocalDate dataFrete = LocalDate.parse(request.getParameter("dataFrete"));
		LocalDate dataEntrega = LocalDate.parse(request.getParameter("dataEntrega"));
		String observacoes = request.getParameter("observacoes");

		Frete frete = new Frete();
		frete.setOrigem(origem);
		frete.setDestino(destino);
		frete.setPeso(peso);
		frete.setValor(valor);
		frete.setTransportadora(transportadora);
		frete.setStatus(status);
		frete.setDataFrete(dataFrete);
		frete.setDataEntrega(dataEntrega);
		frete.setObservacoes(observacoes);

		dao.cadastrarFrete(frete);
		response.sendRedirect("listarFretes.jsp");
	}

}
