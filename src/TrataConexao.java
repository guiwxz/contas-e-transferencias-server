
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Mensagem;
import util.Operacao;
import util.Status;
import util.Usuario;

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
            
            String usuarioId = null;
            
            while(status != Status.SAIR){
                
                Mensagem req = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente " + id + ": \n" + req);
                Operacao operacao = req.getOperacao();
                Mensagem reply = new Mensagem(operacao);
                
                switch(status){         
                    case CONECTADO: {
                        switch(operacao){
                            case LOGIN: {
                                try{
                                    reply.setOperacao(Operacao.LOGINREPLY);
                                    String usuario = (String) req.getParam("user");
                                    String senha = (String) req.getParam("pass");
                                    
                                    Boolean flag = false;
                                    
                                    for (Usuario u: this.server.getUsuariosCadastrados()) {
                                        if (u.getId().equals(usuario) && u.getSenha().equals(senha)) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    
                                    if (!flag) {
                                        status = Status.CONECTADO;
                                        reply.setStatus(Status.PARAMNULL);
                                        reply.setParam("res", "Usuário não cadastrado ou senha incorreta");
                                        break;
                                    }
                                    
                                    status = Status.AUTENTICADO;
                                    usuarioId = usuario;
                                    
                                    reply.setStatus(Status.OK);
                                    reply.setParam("res", "Usuário logado com sucesso");
                                    
                                    
                                }catch(Exception e){
                                    reply = new Mensagem(Operacao.LOGINREPLY);
                                    reply.setStatus(Status.ERRO);
                                    
                                }
                                break;
                            }
                            
                            case CADASTRO: {
                                try {
                                    String usuario = (String) req.getParam("id");
                                    String senha = (String) req.getParam("senha");
                                    Boolean outFlag = false;
                                    
                                    for (Usuario u: this.server.getUsuariosCadastrados()) {
                                        System.out.println(this.server.getUsuariosCadastrados().size());
                                        if (u.getId().equals(usuario)) {
                                            outFlag = true;
                                            break;
                                        }
                                    }
                                    if (outFlag) {
                                        reply.setStatus(Status.OK);
                                        reply.setParam("res", "Nome de usuário/id já cadastrado");
                                        break;
                                    }
                                    
                                    this.server.addUsuariosCadastrados(new Usuario(usuario, senha));
                                    reply.setStatus(Status.OK);
                                    reply.setParam("res", "Usuário cadastrado com sucesso");
                                    
                                } catch (Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao cadastrar");
                                    
                                }
                                break;
                            }
                            case CARTEIRAS: {
                                try {
                                    reply.setOperacao(Operacao.CARTEIRASREPLY);
                                    List<Usuario> carteirasArray = this.server.getUsuariosCadastrados();
                                    if (carteirasArray.size() < 1 || carteirasArray == null) {
                                        reply.setStatus(Status.OK);
                                        reply.setParam("res", "Nenhuma conta está cadastrada");
                                        reply.setParam("carteiras", null);
                                        break;
                                    }
                                    
                                    reply.setStatus(Status.OK);
                                    reply.setParam("carteiras", carteirasArray);
                                    
                                    
                                } catch (Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao buscar carteiras cadastradas");
                                    
                                }
                                break;
                            }
                            case LOGOUT: {
                                reply.setStatus(Status.ERRO);
                                reply.setParam("res", "Você não está logado");
                                break;
                            }
                            case SAIR: {
                                status = Status.DESCONECTADO;
                                reply.setStatus(Status.OK);
                                reply.setParam("res", "Sistema fechado!");
                                break;
                            }
                            default: {
                                reply.setStatus(Status.ERRO);
                                reply.setParam("res", "Mensagem não autorizada ou inválida");
                                break;
                            }
                        }
                        break;
                    }
                        
                    case AUTENTICADO: {
                        switch(operacao){  
                            case CARTEIRAS: {
                                try {
                                    reply.setOperacao(Operacao.CARTEIRASREPLY);
                                    if (this.server.getUsuariosCadastrados().size() < 1 || this.server.getUsuariosCadastrados() == null) {
                                        reply.setStatus(Status.OK); // verificar se aqui retorna OK ou ERRO qdo nao existe contas
                                        reply.setParam("res", "Nenhuma conta está cadastrada");
                                        break;
                                    }
                                    
                                    List<Usuario> carteirasArray = this.server.getUsuariosCadastrados();

                                    reply.setStatus(Status.OK);
                                    reply.setParam("carteiras", carteirasArray);
                                    
                                    
                                } catch (Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao buscar carteiras cadastradas");
                                    
                                }
                                break;
                            }
                            case SALDO: {
                                try {
                                    reply.setOperacao(Operacao.SALDOREPLY);
                                    Boolean buscarExtrato = (Boolean) req.getParam("extrato");
                                    
                                    Usuario usuario = this.server.getUsuarioAutenticado(usuarioId);
                                    
                                    if (usuario != null) {
                                        reply.setParam("saldo", usuario.getSaldo());
                                        
                                        if (buscarExtrato) {
                                            reply.setParam("extrato", usuario.getFormatedExtrato());
                                        }
                                        reply.setStatus(Status.OK);
                                        
                                    } else {
                                        reply.setStatus(Status.PARAMNULL);
                                        reply.setParam("res", "Usuário não encontrado");
                                    }
                                    
                                } catch(Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao consultar saldo");
                                }
                                break;
                            }
                            case DEPOSITO: {
                                try {
                                    
                                    float valorDeposito = (float) req.getParam("valor");
                                    
                                    Usuario usuario = this.server.getUsuarioAutenticado(usuarioId);
                                    
                                    if (usuario != null) {
                                        float valorCompensado = this.server.handlePool(valorDeposito);
                                        
                                        usuario.handleDeposito(valorCompensado);
                                        reply.setParam("res", "Deposito realizado com sucesso");
                                        reply.setStatus(Status.OK);
                                        
                                    } else {
                                        reply.setStatus(Status.PARAMNULL);
                                        reply.setParam("res", "Usuário não encontrado");
                                    }
                                    
                                } catch(Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao fazer deposito");
                                }
                                break;
                            }
                                
                            case TRANSFERENCIA: {
                                try {
                                
                                    String idDestino = (String) req.getParam("id");
                                    float valor = (float) req.getParam("valor");

                                    Usuario usuarioDestino = null;
                                    Usuario usuario = this.server.getUsuarioAutenticado(usuarioId);

                                    if (usuario != null) {
                                        if (usuario.getSaldo() < valor || usuario.getSaldo() < 0) {
                                            reply.setStatus(Status.SEMSALDO);
                                            reply.setParam("res", "Usuário com saldo não disponível");
                                            break;
                                        }

                                        for (Usuario u: this.server.getUsuariosCadastrados()) {
                                            if (u.getId().equals(idDestino)) {
                                                usuarioDestino = u;
                                            }
                                        }

                                        if (usuarioDestino != null) {
                                            usuario.handleTransferencia(valor);

                                            float valorCompensado = this.server.handlePool(valor);
                                            usuarioDestino.handleDeposito(valorCompensado);


                                            reply.setStatus(Status.OK);
                                            reply.setParam("res", "Transferencia realizada com sucesso");
                                        } else {
                                            reply.setStatus(Status.PARAMNULL);
                                            reply.setParam("res", "Usuário destino não encontrado");
                                        }
                                    } else {
                                        reply.setStatus(Status.PARAMNULL);
                                        reply.setParam("res", "Usuário não encontrado");
                                    }
                                    
                                } catch(Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao fazer transferencia");
                                }
                                
                                break;
                            }
                            case RECOMPENSAR: {
                                try {
                                    reply.setOperacao(Operacao.RECOMPENSARREPLY);
                                    if (!usuarioId.equals("admin")) {
                                        reply.setParam("res", "Você não possui acesso de administrador");
                                        reply.setStatus(Status.OPINVALIDA);
                                        break;
                                    }
                                    float saldoTotal = 0;
                                    for (Usuario u: this.server.getUsuariosCadastrados()) {
                                        saldoTotal += u.getSaldo();
                                    }
                                    for (Usuario u: this.server.getUsuariosCadastrados()) {
                                        float deposito = 0;
                                        float porcentagem = 0;
                                        porcentagem = (u.getSaldo() * 100) / saldoTotal;

                                        deposito = this.server.getPool() * (porcentagem / 100);

                                        u.handleDeposito(deposito);
                                    }
                                    
                                    reply.setParam("res", "Recompensas distribuídas, pool total: " + this.server.getPool());
                                    reply.setStatus(Status.OK);
                                    
                                    this.server.resetPool();
                                    
                                } catch(Exception e) {
                                    reply.setStatus(Status.ERRO);
                                    reply.setParam("res", "Erro ao transferir recompensas");
                                }
                                
                                break;
                            }
                            case LOGOUT: {
                                status = Status.CONECTADO;
                                reply.setStatus(Status.OK);
                                reply.setParam("res", "Deslogado com sucesso");
                                break;
                            }
                            case SAIR: {
                                status = Status.DESCONECTADO;
                                reply.setStatus(Status.OK);
                                reply.setParam("res", "O sistema foi finalizado!");
                                break;
                            }
                            default: {
                                reply.setStatus(Status.ERRO);
                                reply.setParam("res", "Mensagem não autorizada ou inválida");
                                break;
                            }
                        }
                        break;
                    }
                    
                    default:
                        break;
                }

                output.writeObject(reply);
                output.flush();
            }
            input.close();
            output.close();
        
        }catch(EOFException ex) {
            System.out.println("Conexão finalizada");
            
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
