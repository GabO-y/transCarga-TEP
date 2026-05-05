package br.com.transcarga.persistencia;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Frete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origem;
    private String destino;
    private double peso;
    private String transportadora;
    private double valor;
    private String status;
    private LocalDate dataFrete;
    private LocalDate dataEntrega;
    private String observacoes;

    // Campos para fluxo de solicitação
    // "SOLICITACAO" = criado pelo user, "CONFIRMADO" = aceito mutuamente, "CANCELADO" = cancelado
    private String tipo;
    private String motivoRejeicao;
    private LocalDateTime dataRespostaAdmin;
    private boolean encerradoDispensado;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataFrete() {
        return dataFrete;
    }

    public void setDataFrete(LocalDate dataFrete) {
        this.dataFrete = dataFrete;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public LocalDateTime getDataRespostaAdmin() {
        return dataRespostaAdmin;
    }

    public void setDataRespostaAdmin(LocalDateTime dataRespostaAdmin) {
        this.dataRespostaAdmin = dataRespostaAdmin;
    }

    public boolean isEncerradoDispensado() {
        return encerradoDispensado;
    }

    public void setEncerradoDispensado(boolean encerradoDispensado) {
        this.encerradoDispensado = encerradoDispensado;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
