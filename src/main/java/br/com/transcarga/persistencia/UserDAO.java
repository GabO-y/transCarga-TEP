package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.util.List;

public class UserDAO {

    private static final String PU = "TransCargaPU";
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PU);

    private EntityManager getEM() {
        return EMF.createEntityManager();
    }

    public User autenticar(String username, String password) {
        EntityManager em = getEM();
        try {
            User user = em.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getSingleResult();
            if (user.checkPassword(password)) {
                return user;
            }
        } catch (NoResultException e) {
            // usuário não encontrado
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return null;
    }

    public void cadastrar(String username, String password, String role) {
        EntityManager em = getEM();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            User user = new User(username, password, role);
            em.persist(user);

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } finally {
            em.close();
        }
    }

    public List<User> listar() {
        EntityManager em = getEM();
        try {
            System.out.println("[UserDAO] Executando query: SELECT u FROM User u");
            List<User> result = em.createQuery("SELECT u FROM User u", User.class).getResultList();
            System.out.println("[UserDAO] Encontrados " + (result != null ? result.size() : "null") + " usuários");
            return result != null ? result : new java.util.ArrayList<>();
        } catch (Exception e) {
            System.out.println("[UserDAO] ERRO ao listar: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        } finally {
            em.close();
        }
    }
}
