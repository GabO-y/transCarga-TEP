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
		List<Frete> fretes;
		HttpSession session = request.getSession(false);
		User userLogado = (session != null) ? (User) session.getAttribute("user") : null;

		// Se for USER, filtra apenas fretes associados a ele
		if (userLogado != null && "USER".equalsIgnoreCase(userLogado.getRole())) {
			fretes = dao.listarFretesPorUser(userLogado.getId());
		} else {
			// ADMIN ou não logado: lista todos
			fretes = dao.listarFretes();
		}

		// Filtros
		String filtroStatus = request.getParameter("filtroStatus");
		System.out.println("[FILTRO] Status recebido: " + filtroStatus);
		if (filtroStatus != null && !filtroStatus.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getStatus() != null && f.getStatus().equals(filtroStatus))
					.collect(Collectors.toList());
			System.out.println("[FILTRO] Após filtro status: " + fretes.size() + " fretes");
		}

		String filtroTransportadora = request.getParameter("filtroTransportadora");
		System.out.println("[FILTRO] Transportadora recebida: " + filtroTransportadora);
		if (filtroTransportadora != null && !filtroTransportadora.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getTransportadora() != null
							&& f.getTransportadora().toLowerCase().contains(filtroTransportadora.toLowerCase()))
					.collect(Collectors.toList());
			System.out.println("[FILTRO] Após filtro transportadora: " + fretes.size() + " fretes");
		}

		String filtroOrigem = request.getParameter("filtroOrigem");
		System.out.println("[FILTRO] Origem recebida: " + filtroOrigem);
		if (filtroOrigem != null && !filtroOrigem.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getOrigem() != null
							&& f.getOrigem().toLowerCase().contains(filtroOrigem.toLowerCase()))
					.collect(Collectors.toList());
			System.out.println("[FILTRO] Após filtro origem: " + fretes.size() + " fretes");
		}

		String filtroDestino = request.getParameter("filtroDestino");
		System.out.println("[FILTRO] Destino recebido: " + filtroDestino);
		if (filtroDestino != null && !filtroDestino.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getDestino() != null
							&& f.getDestino().toLowerCase().contains(filtroDestino.toLowerCase()))
					.collect(Collectors.toList());
			System.out.println("[FILTRO] Após filtro destino: " + fretes.size() + " fretes");
		}

		String filtroPesoMin = request.getParameter("filtroPesoMin");
		System.out.println("[FILTRO] PesoMin recebido: " + filtroPesoMin);
		if (filtroPesoMin != null && !filtroPesoMin.isEmpty()) {
			try {
				double min = Double.parseDouble(filtroPesoMin);
				System.out.println("[FILTRO] Aplicando peso min: " + min);
				fretes = fretes.stream()
						.filter(f -> f.getPeso() >= min)
						.collect(Collectors.toList());
				System.out.println("[FILTRO] Após filtro peso min: " + fretes.size() + " fretes");
			} catch (NumberFormatException e) {}
		}

		String filtroPesoMax = request.getParameter("filtroPesoMax");
		System.out.println("[FILTRO] PesoMax recebido: " + filtroPesoMax);
		if (filtroPesoMax != null && !filtroPesoMax.isEmpty()) {
			try {
				double max = Double.parseDouble(filtroPesoMax);
				System.out.println("[FILTRO] Aplicando peso max: " + max);
				fretes = fretes.stream()
						.filter(f -> f.getPeso() <= max)
						.collect(Collectors.toList());
				System.out.println("[FILTRO] Após filtro peso max: " + fretes.size() + " fretes");
			} catch (NumberFormatException e) {}
		}

		String filtroValorMin = request.getParameter("filtroValorMin");
		if (filtroValorMin != null && !filtroValorMin.isEmpty()) {
			try {
				double min = Double.parseDouble(filtroValorMin);
				fretes = fretes.stream()
						.filter(f -> f.getValor() >= min)
						.collect(Collectors.toList());
			} catch (NumberFormatException e) {}
		}

		String filtroValorMax = request.getParameter("filtroValorMax");
		System.out.println("[FILTRO] filtroValorMax recebido: " + filtroValorMax);
		if (filtroValorMax != null && !filtroValorMax.isEmpty()) {
			try {
				double max = Double.parseDouble(filtroValorMax);
				System.out.println("[FILTRO] Aplicando filtro valor max: " + max);
				fretes = fretes.stream()
						.filter(f -> f.getValor() <= max)
						.collect(Collectors.toList());
			} catch (NumberFormatException e) {}
		}

		String filtroDataInicio = request.getParameter("filtroDataInicio");
		System.out.println("[FILTRO] filtroDataInicio recebido: '" + filtroDataInicio + "'");
		if (filtroDataInicio != null && !filtroDataInicio.isEmpty()) {
			try {
				LocalDate inicio = LocalDate.parse(filtroDataInicio);
				System.out.println("[FILTRO] Data início parseada: " + inicio);
				fretes = fretes.stream()
						.filter(f -> f.getDataFrete() != null && !f.getDataFrete().isBefore(inicio))
						.collect(Collectors.toList());
				System.out.println("[FILTRO] Após filtro data início (dataFrete >=): " + fretes.size() + " fretes");
			} catch (Exception e) {
				System.out.println("[FILTRO] Erro ao parsear data início: " + filtroDataInicio + " - " + e.getMessage());
			}
		}

		String filtroDataFim = request.getParameter("filtroDataFim");
		System.out.println("[FILTRO] filtroDataFim recebido: '" + filtroDataFim + "'");
		if (filtroDataFim != null && !filtroDataFim.isEmpty()) {
			try {
				LocalDate fim = LocalDate.parse(filtroDataFim);
				System.out.println("[FILTRO] Data fim parseada: " + fim);
				// Filtra por dataEntrega (data de entrega) <= fim
				fretes = fretes.stream()
						.filter(f -> f.getDataEntrega() != null && !f.getDataEntrega().isAfter(fim))
						.collect(Collectors.toList());
				System.out.println("[FILTRO] Após filtro data fim (dataEntrega <=): " + fretes.size() + " fretes");
			} catch (Exception e) {
				System.out.println("[FILTRO] Erro ao parsear data fim: " + filtroDataFim + " - " + e.getMessage());
			}
		}

		String filtroUserId = request.getParameter("filtroUserId");

		// Filtro por usuário (apenas admin)
		if (userLogado != null && "ADMIN".equalsIgnoreCase(userLogado.getRole())) {
			if (filtroUserId != null && !filtroUserId.isEmpty()) {
				try {
					Long userIdFiltro = Long.parseLong(filtroUserId);
					fretes = fretes.stream()
							.filter(f -> f.getUser() != null && f.getUser().getId().equals(userIdFiltro))
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {}
			}
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
		out.println("<form method='get' action='" + request.getContextPath() + "/FreteServlet' style='display:block !important; margin-bottom:15px;'>");
		out.println("<select name='filtroStatus' style='display:inline-block !important; width:auto !important; padding:8px 10px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin:2px;'>");
		out.println("<option value=''>Todos os Status</option>");
		out.println("<option value='Pendente' " + ("Pendente".equals(filtroStatus) ? "selected" : "") + ">Pendente</option>");
		out.println("<option value='Em trânsito' " + ("Em trânsito".equals(filtroStatus) ? "selected" : "") + ">Em trânsito</option>");
		out.println("<option value='Entregue' " + ("Entregue".equals(filtroStatus) ? "selected" : "") + ">Entregue</option>");
		out.println("</select>");
		out.println("<input type='text' name='filtroTransportadora' placeholder='Transportadora' value='" + (filtroTransportadora != null ? filtroTransportadora : "") + "' style='display:inline-block !important; width:auto !important; padding:8px 10px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin:2px;'>");
		out.println("<input type='text' name='filtroOrigem' placeholder='Origem' value='" + (filtroOrigem != null ? filtroOrigem : "") + "' style='display:inline-block !important; width:auto !important; padding:8px 10px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin:2px;'>");
		out.println("<input type='text' name='filtroDestino' placeholder='Destino' value='" + (filtroDestino != null ? filtroDestino : "") + "' style='display:inline-block !important; width:auto !important; padding:8px 10px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin:2px;'>");
		out.println("<span style='display:inline-block !important; margin:2px; font-size:0.8em; color:#666;'>Peso Min</span><input type='number' step='0.01' name='filtroPesoMin' placeholder='Mín' value='" + (filtroPesoMin != null ? filtroPesoMin : "") + "' style='display:inline-block !important; width:75px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin-left:2px;'>");
		out.println("<span style='display:inline-block !important; margin:2px; font-size:0.8em; color:#666;'>Peso Máx</span><input type='number' step='0.01' name='filtroPesoMax' placeholder='Máx' value='" + (filtroPesoMax != null ? filtroPesoMax : "") + "' style='display:inline-block !important; width:75px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin-left:2px;'>");
		out.println("<span style='display:inline-block !important; margin:2px; font-size:0.8em; color:#666;'>Valor Min (R$)</span><input type='number' step='0.01' name='filtroValorMin' placeholder='Mín' value='" + (filtroValorMin != null ? filtroValorMin : "") + "' style='display:inline-block !important; width:80px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin-left:2px;'>");
		out.println("<span style='display:inline-block !important; margin:2px; font-size:0.8em; color:#666;'>Valor Máx (R$)</span><input type='number' step='0.01' name='filtroValorMax' placeholder='Máx' value='" + (filtroValorMax != null ? filtroValorMax : "") + "' style='display:inline-block !important; width:80px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin-left:2px;'>");
		out.println("<span style='display:inline-block !important; margin:2px; font-size:0.8em; color:#666;'>Dt.Início</span><input type='date' name='filtroDataInicio' value='" + (filtroDataInicio != null ? filtroDataInicio : "") + "' style='display:inline-block !important; width:140px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin-left:2px;'>");
		out.println("<span style='display:inline-block !important; margin:2px; font-size:0.8em; color:#666;'>Dt.Fim</span><input type='date' name='filtroDataFim' value='" + (filtroDataFim != null ? filtroDataFim : "") + "' style='display:inline-block !important; width:140px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin-left:2px;'>");

		// Filtro por usuário (apenas admin)
		if (userLogado != null && "ADMIN".equalsIgnoreCase(userLogado.getRole())) {
			out.println("<input type='number' name='filtroUserId' placeholder='ID Usuário' value='" + (filtroUserId != null ? filtroUserId : "") + "' style='display:inline-block !important; width:90px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin:2px;'>");
		}

		out.println("<button type='submit' style='display:inline-block !important; padding:8px 15px; background:#2c7cbd; color:white; border:none; border-radius:4px; font-size:0.9em; cursor:pointer; margin:2px;'>Filtrar</button>");
		out.println("<a href='" + request.getContextPath() + "/FreteServlet' style='display:inline-block !important; padding:8px 15px; background:#f5f5f5; color:#333; border:1px solid #ddd; border-radius:4px; font-size:0.9em; text-decoration:none; margin:2px;'>Limpar</a>");
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
					+ "<th>Usuário</th>"
					+ "<th>Ações</th>"
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

				String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";

				// Verifica se é ADMIN para mostrar link de edição
			HttpSession sessao = request.getSession(false);
			boolean isAdmin = false;
			if (sessao != null) {
				User usuarioLogado = (User) sessao.getAttribute("user");
				if (usuarioLogado != null && usuarioLogado.getRole() != null) {
					isAdmin = "ADMIN".equalsIgnoreCase(usuarioLogado.getRole());
				}
			}

			String acoes = isAdmin ? "<a href='" + request.getContextPath() + "/EditarFreteServlet?id=" + f.getId() + "' title='Editar'>✎ Editar</a>" : "-";

			out.printf(
						"<tr><td>%d</td><td>%s</td><td>%s</td><td>%.2f</td><td class='valor-cell'>R$ %.2f</td><td>%s</td><td class='status-badge %s'>%s</td><td class='data-cell'>%s</td><td class='data-cell'>%s</td><td>%s</td><td>%s</td></tr>",
						f.getId(), origem, destino, f.getPeso(), f.getValor(), transportadora,
						statusClass, statusStr, dataFrete, dataEntrega, userName, acoes);
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

		try {
			String origem = request.getParameter("origem");
			String destino = request.getParameter("destino");
			double peso = Double.parseDouble(request.getParameter("peso"));
			double valor = Double.parseDouble(request.getParameter("valor"));
			String transportadora = request.getParameter("transportadora");
			String status = request.getParameter("status");
			if (status == null || status.trim().isEmpty()) {
				status = "Pendente";
			}
			LocalDate dataFrete = LocalDate.parse(request.getParameter("dataFrete"));
			LocalDate dataEntrega = null;
			String dataEntregaStr = request.getParameter("dataEntrega");
			if (dataEntregaStr != null && !dataEntregaStr.trim().isEmpty()) {
				dataEntrega = LocalDate.parse(dataEntregaStr);
			}
			String observacoes = request.getParameter("observacoes");

			String userIdStr = request.getParameter("userId");
			User userAssociado = null;
			if (userIdStr != null && !userIdStr.trim().isEmpty()) {
				try {
					UserDAO userDAO = new UserDAO();
					userAssociado = userDAO.buscarPorId(Long.parseLong(userIdStr));
				} catch (NumberFormatException e) {
					// userId inválido, ignora
				}
			}

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
			frete.setUser(userAssociado);

			dao.cadastrarFrete(frete);
			// Redireciona o parent para a listagem (evita aninhamento de iframes)
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("  if (window.parent) {");
			out.println("    window.parent.document.getElementById('mainFrame').src = '" + request.getContextPath() + "/FreteServlet';");
			out.println("  } else {");
			out.println("    window.location.href = '" + request.getContextPath() + "/home.jsp';");
			out.println("  }");
			out.println("</script>");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("cadastrarFrete.jsp?erro=dados-invalidos");
		}
	}

}
