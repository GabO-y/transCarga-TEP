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
        cadastrar(username, password, role, null);
    }

    public void cadastrar(String username, String password, String role, String endereco) {
        EntityManager em = getEM();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            User user = new User(username, password, role);
            user.setEndereco(endereco);
            em.persist(user);

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar usuário: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void atualizarEndereco(Long userId, String endereco) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setEndereco(endereco);
                em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar endereço: " + e.getMessage(), e);
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

    public List<User> listarApenasUsers() {
        EntityManager em = getEM();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.role = 'USER'", User.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public User buscarPorUsername(String username) {
        EntityManager em = getEM();
        try {
            return em.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public User buscarPorId(Long id) {
        EntityManager em = getEM();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }
}
