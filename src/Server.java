
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import util.Mensagem;
import util.Usuario;

/**
 * @author Giovani P.
 */
public class Server {

    private ServerSocket serverSocket;
    ArrayList<TrataConexao> clientes;
    ArrayList<Thread> threads;
    ArrayList<Usuario> usuariosCadastrados;
    
    public Server(){
        this.threads = new ArrayList<>();
        this.clientes = new ArrayList<>();
    }
    
    protected String enviaMsg(Mensagem m, int clienteID){
        return "";
    }
    
    private void criarServerSocket(int porta) throws IOException{
        serverSocket = new ServerSocket(porta);
    }
    
    private Socket esperaConexao() throws IOException{
        Socket socket = serverSocket.accept();
        return socket;
    }
    
    public void connectionLoop() throws IOException{
        int id = 0;
        while(true){
            Socket socket = esperaConexao();
            System.out.println("Cliente " + id + " conectado.");

            TrataConexao tc = new TrataConexao(this, socket, id++, usuariosCadastrados);
            Thread th = new Thread(tc);
            clientes.add(tc);
            threads.add(th);
            th.start();
        }
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        try{
            Server server = new Server();
            server.criarServerSocket(5555);
            server.connectionLoop(); 
        }catch(IOException e){
            //tratar
        }
    } 
}
