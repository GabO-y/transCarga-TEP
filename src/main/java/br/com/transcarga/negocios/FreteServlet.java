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

		boolean isAdmin = userLogado != null && "ADMIN".equalsIgnoreCase(userLogado.getRole());
		boolean isUser = userLogado != null && "USER".equalsIgnoreCase(userLogado.getRole());

		if (isUser) {
			fretes = new ArrayList<>();
			fretes.addAll(dao.listarFretesPorUser(userLogado.getId()));
			fretes.addAll(dao.listarSolicitacoesPorUser(userLogado.getId()));
		} else {
			fretes = dao.listarFretes();
		}

		String filtroStatus = request.getParameter("filtroStatus");
		if (filtroStatus != null && !filtroStatus.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getStatus() != null && f.getStatus().equals(filtroStatus))
					.collect(Collectors.toList());
		}

		String filtroTransportadora = request.getParameter("filtroTransportadora");
		if (filtroTransportadora != null && !filtroTransportadora.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getTransportadora() != null
							&& f.getTransportadora().toLowerCase().contains(filtroTransportadora.toLowerCase()))
					.collect(Collectors.toList());
		}

		String filtroOrigem = request.getParameter("filtroOrigem");
		if (filtroOrigem != null && !filtroOrigem.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getOrigem() != null
							&& f.getOrigem().toLowerCase().contains(filtroOrigem.toLowerCase()))
					.collect(Collectors.toList());
		}

		String filtroDestino = request.getParameter("filtroDestino");
		if (filtroDestino != null && !filtroDestino.isEmpty()) {
			fretes = fretes.stream()
					.filter(f -> f.getDestino() != null
							&& f.getDestino().toLowerCase().contains(filtroDestino.toLowerCase()))
					.collect(Collectors.toList());
		}

		String filtroPesoMin = request.getParameter("filtroPesoMin");
		if (filtroPesoMin != null && !filtroPesoMin.isEmpty()) {
			try {
				double min = Double.parseDouble(filtroPesoMin);
				fretes = fretes.stream()
						.filter(f -> f.getPeso() >= min)
						.collect(Collectors.toList());
			} catch (NumberFormatException e) {}
		}

		String filtroPesoMax = request.getParameter("filtroPesoMax");
		if (filtroPesoMax != null && !filtroPesoMax.isEmpty()) {
			try {
				double max = Double.parseDouble(filtroPesoMax);
				fretes = fretes.stream()
						.filter(f -> f.getPeso() <= max)
						.collect(Collectors.toList());
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
		if (filtroValorMax != null && !filtroValorMax.isEmpty()) {
			try {
				double max = Double.parseDouble(filtroValorMax);
				fretes = fretes.stream()
						.filter(f -> f.getValor() <= max)
						.collect(Collectors.toList());
			} catch (NumberFormatException e) {}
		}

		String filtroDataInicio = request.getParameter("filtroDataInicio");
		if (filtroDataInicio != null && !filtroDataInicio.isEmpty()) {
			try {
				LocalDate inicio = LocalDate.parse(filtroDataInicio);
				fretes = fretes.stream()
						.filter(f -> f.getDataFrete() != null && !f.getDataFrete().isBefore(inicio))
						.collect(Collectors.toList());
			} catch (Exception e) {}
		}

		String filtroDataFim = request.getParameter("filtroDataFim");
		if (filtroDataFim != null && !filtroDataFim.isEmpty()) {
			try {
				LocalDate fim = LocalDate.parse(filtroDataFim);
				fretes = fretes.stream()
						.filter(f -> f.getDataEntrega() != null && !f.getDataEntrega().isAfter(fim))
						.collect(Collectors.toList());
			} catch (Exception e) {}
		}

		String filtroUserId = request.getParameter("filtroUserId");

		if (isAdmin && filtroUserId != null && !filtroUserId.isEmpty()) {
			try {
				Long userIdFiltro = Long.parseLong(filtroUserId);
				fretes = fretes.stream()
						.filter(f -> f.getUser() != null && f.getUser().getId().equals(userIdFiltro))
						.collect(Collectors.toList());
			} catch (NumberFormatException e) {}
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

		if (isAdmin) {
			out.println("<input type='number' name='filtroUserId' placeholder='ID Usuário' value='" + (filtroUserId != null ? filtroUserId : "") + "' style='display:inline-block !important; width:90px !important; padding:8px 8px; border:1px solid #ddd; border-radius:4px; font-size:0.9em; margin:2px;'>");
		}

		out.println("<button type='submit' style='display:inline-block !important; padding:8px 15px; background:#2c7cbd; color:white; border:none; border-radius:4px; font-size:0.9em; cursor:pointer; margin:2px;'>Filtrar</button>");
		out.println("<a href='" + request.getContextPath() + "/FreteServlet' style='display:inline-block !important; padding:8px 15px; background:#f5f5f5; color:#333; border:1px solid #ddd; border-radius:4px; font-size:0.9em; text-decoration:none; margin:2px;'>Limpar</a>");
		out.println("</form>");
		out.println("</div>");

		String sucesso = request.getParameter("sucesso");
		if ("frete-aceito".equals(sucesso)) {
			out.println("<div style='background:#e8f5e9;color:#2e7d32;padding:10px;border-radius:4px;margin-bottom:15px;text-align:center;'>Frete aceito! Agora está como Pendente.</div>");
		} else if ("oferta-enviada".equals(sucesso)) {
			out.println("<div style='background:#e8f5e9;color:#2e7d32;padding:10px;border-radius:4px;margin-bottom:15px;text-align:center;'>Oferta enviada com sucesso! Aguardando aceite do usuário.</div>");
		}

		if (isUser) {
			List<Frete> ofertasAdmin = dao.listarOfertasAdminPorUser(userLogado.getId());
			List<Frete> ofertasAtivas = new ArrayList<>();
			List<Frete> ofertasAceitas = new ArrayList<>();
			List<Frete> ofertasRejeitadas = new ArrayList<>();
			for (Frete f : ofertasAdmin) {
				if ("CONFIRMADO".equals(f.getTipo())) {
					ofertasAceitas.add(f);
				} else if ("ENCERRADO".equals(f.getTipo())) {
					ofertasRejeitadas.add(f);
				} else {
					ofertasAtivas.add(f);
				}
			}

			List<Frete> solicitacoesUser = dao.listarSolicitacoesPorUser(userLogado.getId());

			boolean temConteudo = !ofertasAtivas.isEmpty() || !ofertasAceitas.isEmpty() || !ofertasRejeitadas.isEmpty()
					|| !solicitacoesUser.isEmpty() || !fretes.isEmpty();

			if (!temConteudo) {
				out.println("<div class='empty-message'>");
				out.println("<p>Nenhuma entrega encontrada.</p>");
				out.println("</div>");
				out.println("</body></html>");
				return;
			}

			if (!ofertasAtivas.isEmpty()) {
				out.println("<h3 style='color:#2196f3; margin:20px 0 10px;'>Ofertas do Admin — Aguardando Aceite (" + ofertasAtivas.size() + ")</h3>");
				out.println("<div class='table-responsive'><table>");
				out.println("<thead><tr><th>ID</th><th>Origem</th><th>Destino</th><th>Peso</th><th>Transportadora</th><th>Valor</th><th>Data</th><th>Ações</th></tr></thead>");
				out.println("<tbody>");
				for (Frete f : ofertasAtivas) {
					out.println("<tr>");
					out.println("<td>" + f.getId() + "</td>");
					out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
					out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
					out.printf("<td>%.2f kg</td>", f.getPeso());
					out.println("<td>" + (f.getTransportadora() != null ? f.getTransportadora() : "-") + "</td>");
					out.printf("<td class='valor-cell'>R$ %.2f</td>", f.getValor());
					out.println("<td>" + (f.getDataFrete() != null ? f.getDataFrete() : "-") + "</td>");
					out.println("<td><a href='" + request.getContextPath() + "/userResposta?id=" + f.getId() + "' style='color:#2c7cbd;'>Ver detalhes</a></td>");
					out.println("</tr>");
				}
				out.println("</tbody></table></div>");
			}

			if (!ofertasAceitas.isEmpty()) {
				out.println("<h3 style='color:#4caf50; margin:20px 0 10px;'>Ofertas Aceitas (" + ofertasAceitas.size() + ")</h3>");
				out.println("<div class='table-responsive'><table>");
				out.println("<thead><tr><th>ID</th><th>Origem</th><th>Destino</th><th>Peso</th><th>Transportadora</th><th>Valor</th><th>Data</th></tr></thead>");
				out.println("<tbody>");
				for (Frete f : ofertasAceitas) {
					out.println("<tr>");
					out.println("<td>" + f.getId() + "</td>");
					out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
					out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
					out.printf("<td>%.2f kg</td>", f.getPeso());
					out.println("<td>" + (f.getTransportadora() != null ? f.getTransportadora() : "-") + "</td>");
					out.printf("<td class='valor-cell'>R$ %.2f</td>", f.getValor());
					out.println("<td>" + (f.getDataFrete() != null ? f.getDataFrete() : "-") + "</td>");
					out.println("</tr>");
				}
				out.println("</tbody></table></div>");
			}

			if (!ofertasRejeitadas.isEmpty()) {
				out.println("<h3 style='color:#c0392b; margin:20px 0 10px;'>Ofertas Rejeitadas (" + ofertasRejeitadas.size() + ")</h3>");
				out.println("<div class='table-responsive'><table>");
				out.println("<thead><tr><th>ID</th><th>Origem</th><th>Destino</th><th>Motivo</th></tr></thead>");
				out.println("<tbody>");
				for (Frete f : ofertasRejeitadas) {
					out.println("<tr>");
					out.println("<td>" + f.getId() + "</td>");
					out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
					out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
					out.println("<td style='max-width:200px; overflow:hidden; text-overflow:ellipsis;'>" + (f.getMotivoRejeicao() != null ? f.getMotivoRejeicao() : "-") + "</td>");
					out.println("</tr>");
				}
				out.println("</tbody></table></div>");
			}

			out.println("<h3 style='color:#2c7cbd; margin:20px 0 10px;'>Minhas Solicitações (" + solicitacoesUser.size() + ")</h3>");
			if (solicitacoesUser.isEmpty()) {
				out.println("<p style='color:#999; font-style:italic;'>Nenhuma solicitação registrada.</p>");
			} else {
				out.println("<div class='table-responsive'><table>");
				out.println("<thead><tr><th>ID</th><th>Origem</th><th>Destino</th><th>Peso</th><th>Transportadora</th><th>Valor</th><th>Status</th><th>Ações</th></tr></thead>");
				out.println("<tbody>");
				for (Frete f : solicitacoesUser) {
					String statusStr = (f.getStatus() != null) ? f.getStatus() : "-";
					String statusClass = "";
					if ("Solicitado".equals(statusStr)) statusClass = "status-solicitado";
					else if ("Em análise".equals(statusStr)) statusClass = "status-em-analise";
					else if ("Rejeitado".equals(statusStr)) statusClass = "status-rejeitado";
					else if ("Pendente".equals(statusStr)) statusClass = "status-pendente";
					else if ("Confirmado".equals(statusStr)) statusClass = "status-entregue";

					out.println("<tr>");
					out.println("<td>" + f.getId() + "</td>");
					out.println("<td>" + (f.getOrigem() != null ? f.getOrigem() : "-") + "</td>");
					out.println("<td>" + (f.getDestino() != null ? f.getDestino() : "-") + "</td>");
					out.printf("<td>%.2f</td>", f.getPeso());
					out.println("<td>" + (f.getTransportadora() != null ? f.getTransportadora() : "-") + "</td>");
					out.printf("<td class='valor-cell'>R$ %.2f</td>", f.getValor());
					out.println("<td><span class='status-badge " + statusClass + "'>" + statusStr + "</span></td>");
					out.println("<td><a href='" + request.getContextPath() + "/userResposta?id=" + f.getId() + "' style='color:#2c7cbd;'>Ver detalhes</a></td>");
					out.println("</tr>");
				}
				out.println("</tbody></table></div>");
			}

			if (!fretes.isEmpty()) {
				out.println("<h3 style='margin:20px 0 10px;'>Fretes Confirmados</h3>");
				out.println("<div class='table-responsive'>");
				out.println("<table>");
				out.println("<thead><tr><th>ID</th><th>Origem</th><th>Destino</th><th>Peso (kg)</th><th>Valor (R$)</th><th>Transportadora</th><th>Status</th><th>Data Frete</th><th>Data Entrega</th><th>Ações</th></tr></thead>");
				out.println("<tbody>");
				for (Frete f : fretes) {
					String statusClass = "";
					if (f.getStatus() != null) {
						if ("Pendente".equals(f.getStatus())) statusClass = "status-pendente";
						else if ("Em trânsito".equals(f.getStatus())) statusClass = "status-em-transito";
						else if ("Entregue".equals(f.getStatus())) statusClass = "status-entregue";
					}
					out.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%.2f</td><td class='valor-cell'>R$ %.2f</td><td>%s</td><td class='status-badge %s'>%s</td><td class='data-cell'>%s</td><td class='data-cell'>%s</td><td>%s</td></tr>",
							f.getId(), (f.getOrigem() != null ? f.getOrigem() : "-"), (f.getDestino() != null ? f.getDestino() : "-"),
							f.getPeso(), f.getValor(), (f.getTransportadora() != null ? f.getTransportadora() : "-"),
							statusClass, (f.getStatus() != null ? f.getStatus() : "-"),
							(f.getDataFrete() != null ? f.getDataFrete() : "-"), (f.getDataEntrega() != null ? f.getDataEntrega() : "-"),
							"-");
				}
				out.println("</tbody></table></div>");
			}
		} else {
			if (fretes.isEmpty()) {
				out.println("<div class='empty-message'>");
				out.println("<p>Nenhuma Entrega Encontrada</p>");
				out.println("</div>");
			} else {
				out.println("<div class='table-responsive'>");
				out.println("<table>");
				out.println("<thead><tr><th>ID</th><th>Origem</th><th>Destino</th><th>Peso (kg)</th><th>Valor (R$)</th><th>Transportadora</th><th>Status</th><th>Data Frete</th><th>Data Entrega</th><th>Usuário</th><th>Ações</th></tr></thead>");
				out.println("<tbody>");
				for (Frete f : fretes) {
					String statusClass = "";
					if (f.getStatus() != null) {
						if ("Pendente".equals(f.getStatus())) statusClass = "status-pendente";
						else if ("Em trânsito".equals(f.getStatus())) statusClass = "status-em-transito";
						else if ("Entregue".equals(f.getStatus())) statusClass = "status-entregue";
					}
					String userName = (f.getUser() != null && f.getUser().getUsername() != null) ? f.getUser().getUsername() : "-";
					String acoes = isAdmin ? "<a href='" + request.getContextPath() + "/EditarFreteServlet?id=" + f.getId() + "' title='Editar'>Editar</a>" : "-";
					out.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%.2f</td><td class='valor-cell'>R$ %.2f</td><td>%s</td><td class='status-badge %s'>%s</td><td class='data-cell'>%s</td><td class='data-cell'>%s</td><td>%s</td><td>%s</td></tr>",
							f.getId(), (f.getOrigem() != null ? f.getOrigem() : "-"), (f.getDestino() != null ? f.getDestino() : "-"),
							f.getPeso(), f.getValor(), (f.getTransportadora() != null ? f.getTransportadora() : "-"),
							statusClass, (f.getStatus() != null ? f.getStatus() : "-"),
							(f.getDataFrete() != null ? f.getDataFrete() : "-"), (f.getDataEntrega() != null ? f.getDataEntrega() : "-"),
							userName, acoes);
				}
				out.println("</tbody></table></div>");
			}
		}

		out.println("</body></html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		HttpSession session = request.getSession(false);
		User userLogado = (session != null) ? (User) session.getAttribute("user") : null;
		if (userLogado == null || !"ADMIN".equalsIgnoreCase(userLogado.getRole())) {
			response.sendRedirect("login.jsp");
			return;
		}

		try {
			String origem = request.getParameter("origem");
			String destino = request.getParameter("destino");
			double peso = Double.parseDouble(request.getParameter("peso"));
			double valor = Double.parseDouble(request.getParameter("valor"));
			String transportadora = request.getParameter("transportadora");
			LocalDate dataFrete = LocalDate.parse(request.getParameter("dataFrete"));
			String observacoes = request.getParameter("observacoes");
			String userIdStr = request.getParameter("userId");

			if (userIdStr == null || userIdStr.trim().isEmpty()) {
				response.sendRedirect("cadastrarFrete.jsp?erro=usuario-obrigatorio");
				return;
			}

			User userAssociado = null;
			try {
				UserDAO userDAO = new UserDAO();
				userAssociado = userDAO.buscarPorId(Long.parseLong(userIdStr));
			} catch (NumberFormatException e) {
				response.sendRedirect("cadastrarFrete.jsp?erro=usuario-invalido");
				return;
			}

			if (userAssociado == null) {
				response.sendRedirect("cadastrarFrete.jsp?erro=usuario-nao-encontrado");
				return;
			}

			Frete frete = new Frete();
			frete.setOrigem(origem);
			frete.setDestino(destino);
			frete.setPeso(peso);
			frete.setValor(valor);
			frete.setTransportadora(transportadora);
			frete.setStatus("Solicitado");
			frete.setDataFrete(dataFrete);
			frete.setObservacoes(observacoes);
			frete.setUser(userAssociado);
			frete.setTipo("SOLICITACAO");
			frete.setOrigemCriacao("ADMIN");
			frete.setDataRespostaAdmin(null);

			dao.cadastrarSolicitacao(frete);

			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("  if (window.parent) {");
			out.println("    window.parent.document.getElementById('mainFrame').src = '" + request.getContextPath() + "/FreteServlet?sucesso=oferta-enviada';");
			out.println("  } else {");
			out.println("    window.location.href = '" + request.getContextPath() + "/home.jsp';");
			out.println("  }");
			out.println("</script>");
		} catch (Exception e) {
			e.printStackTrace();
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("  if (window.parent) {");
			out.println("    window.parent.document.getElementById('mainFrame').src = '" + request.getContextPath() + "/FreteServlet?erro=oferta-falhou';");
			out.println("  } else {");
			out.println("    window.location.href = '" + request.getContextPath() + "/home.jsp';");
			out.println("  }");
			out.println("</script>");
		}
	}

}
