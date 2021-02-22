import java.io.*;
import java.net.*;

/**
 *
 * @author Edoardo
 */
class GestoreLog {  // (00)
    
    private static void invia(EventoLogXML log, String ipServerLog, int portaServerLog) {
        try (Socket socket = new Socket(ipServerLog, portaServerLog);
            ObjectOutputStream oout = new ObjectOutputStream(socket.getOutputStream());
        ) {
            oout.writeObject(log.toString());
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static void creaLog(String nomeEvento, ParametriConfigurazioneXML config) {
        invia(
                new EventoLogXML(nomeEvento, config.getIndirizzoIpClient()),
                config.getIndirizzoIpServerDiLog(),
                config.getPortaServerDiLog()
        );
    }
}

/*
    Note:
    (00)
        Classe che si occupa di creare e inviare i log al server di log.
*/
