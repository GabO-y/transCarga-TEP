package br.com.transcarga.persistencia;

import jakarta.persistence.*;
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
            return em.createQuery("SELECT f FROM Frete f", Frete.class).getResultList();
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
        } finally {
            em.close();
        }
    }
}
