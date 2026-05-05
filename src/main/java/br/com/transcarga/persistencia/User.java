package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private String endereco;

    public User() {
    }

    public User(String username, String password, String role) {
        this.username = username;
        String hashGerado = hashPassword(password);
        System.out.println("[User] Hash gerado para '" + password + "': " + hashGerado);
        this.password = hashGerado;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainTextPassword) {
        boolean result = BCrypt.checkpw(plainTextPassword, this.password);
        System.out.println("[User] checkPassword('" + plainTextPassword + "') contra hash '" + this.password + "': " + result);
        return result;
    }
}
