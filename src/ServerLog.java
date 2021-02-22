import java.io.*;
import java.net.*;

/**
 *
 * @author Edoardo
 */
public class ServerLog {
    
    private final static int porta = 4242;
    private final static String FILE_LOG = "log.xml",
                                SCHEMA_LOG = "log.xsd";
    
    public static void main(String[] args) {
        System.out.println("Server di Log avviato");
        
        try(ServerSocket serverSocket = new ServerSocket(porta)) {
            while(true) {
                try(Socket socket = serverSocket.accept();
                    ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
                ) {
                    String log = (String)oin.readObject();
                    System.out.print(log);
                    if (ValidazioneXML.valida(log, SCHEMA_LOG))
                        GestoreFile.salva(log, FILE_LOG);
                }
            }
        } catch(IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
