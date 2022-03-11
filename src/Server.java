
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
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
    List<Usuario> usuariosCadastrados;
    float pool;
    
    public Server(){
        this.threads = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.usuariosCadastrados = new ArrayList<Usuario>();
        this.usuariosCadastrados.add(new Usuario("admin", "admin"));
        this.pool = 0;
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

            TrataConexao tc = new TrataConexao(this, socket, id++);
            Thread th = new Thread(tc);
            clientes.add(tc);
            threads.add(th);
            th.start();
        }
    }
    
    public List<Usuario> getUsuariosCadastrados() {
        return this.usuariosCadastrados;
    }
    
    public void addUsuariosCadastrados(Usuario u) {
        this.usuariosCadastrados.add(u);
    }
    
    public Usuario getUsuarioAutenticado(String id) {
        for (Usuario u: this.usuariosCadastrados) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }
    
    public float handlePool(float valor) {
        float valorCompensado = (valor * 0.02f); // taxa de transaçoes de 2%
        
        this.pool += valorCompensado;
        
        return valor - valorCompensado;
    }
    
    public float getPool() {
        return this.pool;
    }
    
    public void resetPool() {
       this.pool = 0; 
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
