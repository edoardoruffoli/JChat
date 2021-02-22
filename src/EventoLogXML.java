import com.thoughtworks.xstream.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author Edoardo
 */
class EventoLogXML implements Serializable{ // (00)
    
    private final String nomeEvento;
    private final String indirizzoIpClient;
    private final String timestamp;
    
    public EventoLogXML(String nomeEvento, String indirizzoIpClient) {
        this.nomeEvento = nomeEvento;
        this.indirizzoIpClient = indirizzoIpClient;
        Timestamp current_time = new Timestamp(System.currentTimeMillis()); // (01)
        this.timestamp = current_time.toString();
    }
    
    public String toString() {
        XStream xs = new XStream();
        xs.useAttributeFor(EventoLogXML.class, "nomeEvento");   // (02)
        xs.useAttributeFor(EventoLogXML.class, "indirizzoIpClient");
        xs.useAttributeFor(EventoLogXML.class, "timestamp");
        return xs.toXML(this) + '\n';   // (03)
    }
}

/*
    Note:
    (00)
        Classe che rappresenta un log da inviare al server di log in seguito al verificarsi
        di uno degli eventi di log.
    (01)
        Ottengo il timestamp corrente.
    (02)
        Secondo le regole di progettazione XML, i membri della classe sono stati classificati
        come attributi in quanto si tratta di stringhe semplici.
    (03)
        Viene aggiunto un carattere "a capo" per rendere più leggibile il file log.xml.
*/
