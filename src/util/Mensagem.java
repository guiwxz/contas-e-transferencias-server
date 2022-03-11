package util;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Giovani P.
 */
public class Mensagem implements Serializable{
    private Operacao operacao;
    private Status status;
    
    Map<String, Object> params;
    
    public Mensagem(Operacao operacao){
        this.operacao = operacao;
        params = new HashMap<>();
    }
    
    public Operacao getOperacao(){
        return operacao;
    }
    
    public void setOperacao(Operacao operacao){
        this.operacao = operacao;
    }
    
    public void setStatus(Status s){
        this.status = s;
    }
    
    public Status getStatus(){
        return status;
    }
    
    public void setParam(String chave, Object valor){
        params.put(chave, valor);
    }
    
    public Object getParam(String chave){
        return params.get(chave);
    }
    
    @Override
    public String toString(){
        String m = "Operacao: " + operacao;
        m+="\nStatus: " + status;
        m+="\nParametros: ";
        for(String p : params.keySet()){
            m+="\n"+p+": "+ params.get(p);
        }
        return m;
    }
    
}
