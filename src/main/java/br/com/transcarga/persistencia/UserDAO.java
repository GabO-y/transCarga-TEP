package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.util.List;

public class UserDAO {

    private static final String PU = "TransCargaPU";

    private EntityManager getEM() {
        System.out.println("[UserDAO] Criando EntityManager para PU: " + PU);
        return Persistence.createEntityManagerFactory(PU).createEntityManager();
    }

    public User autenticar(String username, String password) {
        System.out.println("[UserDAO] Tentando autenticar username: " + username);
        EntityManager em = getEM();
        try {
            User user = em.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getSingleResult();
            System.out.println("[UserDAO] Usuário encontrado no banco: " + user.getUsername());
            System.out.println("[UserDAO] Hash armazenado no banco: " + user.getPassword());
            boolean senhaOk = user.checkPassword(password);
            System.out.println("[UserDAO] Verificação de senha para '" + password + "': " + senhaOk);
            if (senhaOk) {
                return user;
            }
        } catch (NoResultException e) {
            System.out.println("[UserDAO] Usuário não encontrado: " + username);
        } catch (Exception e) {
            System.out.println("[UserDAO] Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return null;
    }

    public void cadastrar(String username, String password, String role) {
        System.out.println("[UserDAO] Cadastrando usuário: " + username + ", role: " + role);
        EntityManager em = getEM();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            System.out.println("[UserDAO] Iniciando transação...");
            tx.begin();
            
            User user = new User(username, password, role);
            System.out.println("[UserDAO] Objeto User criado com hash: " + user.getPassword());
            
            System.out.println("[UserDAO] Fazendo persist...");
            em.persist(user);
            
            System.out.println("[UserDAO] Fazendo commit...");
            tx.commit();
            System.out.println("[UserDAO] Commit realizado com sucesso!");
        } catch (Exception e) {
            System.out.println("[UserDAO] ERRO ao cadastrar: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                System.out.println("[UserDAO] Fazendo rollback...");
                tx.rollback();
            }
        } finally {
            System.out.println("[UserDAO] Fechando EntityManager...");
            em.close();
        }
    }

    public List<User> listar() {
        System.out.println("[UserDAO] Listando usuários...");
        EntityManager em = getEM();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            System.out.println("[UserDAO] Erro ao listar: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}
