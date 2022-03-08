
package util;

import java.io.Serializable;

/**
 * @author Giovani P.
 */
public class Arquivo implements Serializable{
    private String nome;
    private String conteudo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
