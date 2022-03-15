/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GUI
 */
public class Usuario implements Serializable {
    private String id;
    private String senha;
    private float saldo;
    private List<String> extrato;
    
    public Usuario(String usuario, String senha) {
        this.id = usuario;
        this.senha = senha;
        this.saldo = 0;
        this.extrato = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public List<String> getExtrato() {
        return extrato;
    }

    public void setExtrato(List<String> extrato) {
        this.extrato = extrato;
    }
    
    /**
     * 
     * @param valor
     * @param tipo 
     * * 0: valor positivo
     * * 1: valor negativo
     */
    public void addTransacao(float valor, Integer tipo) {
        if (tipo == 0) {
           this.extrato.add("+ " + valor); 
        } else {        
            this.extrato.add("- " + valor);
        } 
    }
    
    public void handleDeposito(float valor) {
        this.saldo = this.saldo + valor;
        this.extrato.add("+ " + valor);
    }
    
    public void handleTransferencia(float valor) {
        this.saldo = this.saldo - valor;
        this.extrato.add("- " + valor);
    }
    
    public String getFormatedExtrato() {
        String formated = "\n------------------\n";
        
        if (this.extrato.size() > 0) {
        
            for (String it: this.extrato) {
                formated += it + "\n";
            }
        } else {
            formated += "Não há movimentações registradas.\n";
        }
        
        formated += "------------------\n";
        
        return formated;
    }
}
