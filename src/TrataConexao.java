
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Mensagem;
import util.Operacao;
import util.Status;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author VIP
 */
public class TrataConexao implements Runnable{
    
    private Server server;
    private Socket socket;
    private int id;
    private ObjectOutputStream output;
    private ObjectInputStream input; 
    private Status status;

    public TrataConexao(Server server, Socket socket, int id) {
        this.server = server;
        this.socket = socket;
        this.id = id;
        this.status = Status.DESCONECTADO;
    }
    
    @Override
    public void run(){
        try {
            trataConexao();
            
        } catch (IOException ex) {
            System.out.println("Erro no cliente " + id + ": " + ex.getMessage());
            Logger.getLogger(TrataConexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TrataConexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TrataConexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fechaSocket(Socket socket) throws IOException{
        socket.close();
    }
    
    private void trataConexao() throws IOException, ClassNotFoundException, InterruptedException{
        //Protocol
        try{
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream()); 
            status = Status.CONECTADO;
            System.out.println("Tratando.....");
            while(status != Status.DESCONECTADO){
                
                Mensagem m = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente " + id + ": \n" + m);
                Operacao operacao = m.getOperacao();
                Mensagem reply = new Mensagem(operacao);
                
                switch(status){
                    case CONECTADO:
                        switch(operacao){
                            case LOGIN:
                                try{
                                    System.out.println("ta blx");
                                }catch(Exception e){
                                    reply = new Mensagem(Operacao.LOGINREPLY);
                                    reply.setStatus(Status.ERRO);
                                }
                                break;
                            case LOGOUT:
                                break;
                            case CADASTRO:
                                break;
                            case SALDO:
                                break;
                            case DEPOSITO:
                                break;
                            case TRANSFERENCIA:
                                break;
                            case RECOMPENSAR:
                                break;
                            default:
                                break;
                        }
                    case DESCONECTADO:
                        break;
                }

                output.writeObject(reply);
                output.flush();
            }
            input.close();
            output.close();
            
        }catch(IOException ex){
            System.out.println("Erro no servidor");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }catch(ClassNotFoundException ex){
            System.out.println("Erro no servidor");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            fechaSocket(socket);
        }
    }
}
