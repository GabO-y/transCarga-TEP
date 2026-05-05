package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

public class FreteDAO {

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("TransCargaPU");

    private EntityManager getEM() {
        return EMF.createEntityManager();
    }

    public void cadastrarFrete(Frete frete) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(frete);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Frete> listarFretes() {
        EntityManager em = getEM();
        try {
            return em.createQuery("SELECT f FROM Frete f WHERE f.tipo IS NULL OR f.tipo = 'CONFIRMADO'", Frete.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    public Frete buscarPorId(Long id) {
        EntityManager em = getEM();
        try {
            return em.find(Frete.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public void atualizarFrete(Frete frete) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(frete);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar frete: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Frete> listarFretesPorUser(Long userId) {
        EntityManager em = getEM();
        try {
            if (userId != null) {
                return em.createQuery(
                        "SELECT f FROM Frete f WHERE f.user.id = :userId AND (f.tipo IS NULL OR f.tipo = 'CONFIRMADO')",
                        Frete.class)
                        .setParameter("userId", userId)
                        .getResultList();
            }
            return listarFretes();
        } finally {
            em.close();
        }
    }

    // --- Métodos para solicitações ---

    public void cadastrarSolicitacao(Frete solicitacao) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(solicitacao);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar solicitação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Frete> listarSolicitacoes() {
        EntityManager em = getEM();
        try {
            return em.createQuery(
                    "SELECT f FROM Frete f WHERE f.tipo = 'SOLICITACAO' ORDER BY f.dataRespostaAdmin ASC, f.id DESC",
                    Frete.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    public List<Frete> listarSolicitacoesPorUser(Long userId) {
        EntityManager em = getEM();
        try {
            if (userId != null) {
                return em.createQuery(
                        "SELECT f FROM Frete f WHERE f.tipo = 'SOLICITACAO' AND f.user.id = :userId ORDER BY f.id DESC",
                        Frete.class)
                        .setParameter("userId", userId)
                        .getResultList();
            }
            return List.of();
        } finally {
            em.close();
        }
    }

    public long contarSolicitacoesPendentes() {
        EntityManager em = getEM();
        try {
            return em.createQuery(
                    "SELECT COUNT(f) FROM Frete f WHERE f.tipo = 'SOLICITACAO' AND f.dataRespostaAdmin IS NULL",
                    Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public void atualizarStatusSolicitacao(Long id, String status, String motivoRejeicao) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Frete f = em.find(Frete.class, id);
            if (f != null) {
                f.setStatus(status);
                if (motivoRejeicao != null) {
                    f.setMotivoRejeicao(motivoRejeicao);
                }
                em.merge(f);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void confirmarFrete(Long id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Frete f = em.find(Frete.class, id);
            if (f != null) {
                f.setTipo("CONFIRMADO");
                f.setStatus("Pendente");
                f.setDataRespostaAdmin(null);
                f.setMotivoRejeicao(null);
                em.merge(f);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao confirmar frete: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void rejeitarSolicitacao(Long id, String motivo) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Frete f = em.find(Frete.class, id);
            if (f != null) {
                f.setStatus("Rejeitado");
                f.setMotivoRejeicao(motivo);
                f.setDataRespostaAdmin(java.time.LocalDateTime.now());
                em.merge(f);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao rejeitar solicitação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void encerrarSolicitacao(Long id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Frete f = em.find(Frete.class, id);
            if (f != null) {
                f.setTipo("ENCERRADO");
                f.setStatus("Encerrado");
                f.setDataRespostaAdmin(java.time.LocalDateTime.now());
                em.merge(f);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao encerrar solicitação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Frete> listarEncerradasRecentes() {
        EntityManager em = getEM();
        try {
            return em.createQuery(
                    "SELECT f FROM Frete f WHERE f.tipo = 'ENCERRADO' AND f.encerradoDispensado = false ORDER BY f.dataRespostaAdmin DESC",
                    Frete.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    public void dispensarEncerrada(Long id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Frete f = em.find(Frete.class, id);
            if (f != null) {
                f.setEncerradoDispensado(true);
                em.merge(f);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao dispensar encerrada: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void cancelarSolicitacao(Long id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Frete f = em.find(Frete.class, id);
            if (f != null) {
                f.setTipo("CANCELADO");
                f.setStatus("Cancelado");
                em.merge(f);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar solicitação: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
