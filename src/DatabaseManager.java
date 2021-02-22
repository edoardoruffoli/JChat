import java.sql.*;
import java.util.*;
import javafx.collections.*;

/**
 *
 * @author Edoardo
 */
public class DatabaseManager {  // (00)
    
    private final InterfacciaMessaggi interfacciaMessaggi;
    private static String utenteConnesso;
    private final String indirizzoIpServerDBMS;
    private final int portaServerDBMS;
    private final String usernameDBMS;
    private final String passwordDBMS;
    
    public DatabaseManager(InterfacciaMessaggi i, ParametriConfigurazioneXML config) {
        interfacciaMessaggi = i;
        utenteConnesso = "";
        indirizzoIpServerDBMS = config.getIndirizzoIpServerDBMS();  // (01)
        portaServerDBMS = config.getPortaServerDBMS();
        usernameDBMS = config.getUsernameDBMS();
        passwordDBMS = config.getPasswordDBMS();
    }
    
    public boolean inserisciUtenteConnesso(String s) {
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps = co.prepareStatement("INSERT INTO utenti_connessi VALUES (?)");
        ){
            ps.setString(1, s);
            System.out.println("DEBUG: rows affected;" + ps.executeUpdate());
            utenteConnesso = s;
            return true;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }
    
    public boolean rimuoviUtenteConnesso(String s) {
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps = co.prepareStatement("DELETE FROM utenti_connessi WHERE email = ?");
        ){
            ps.setString(1, s);
            System.out.println("DEBUG: rows affected;" + ps.executeUpdate());
            return true;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }
    
    public void caricaUtentiConnessiMostraNotifiche(ObservableList<Utente> ol, String contattoSelezionato,
            Timestamp current_timestamp, Map<String, Integer> messaggiUtenteLetti) {    // (02)
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps0 = co.prepareStatement(
                        "CREATE OR REPLACE VIEW nNuoviMessaggi AS" +
                        "	SELECT mittente, COUNT(*) as nNuoviMessaggi" +
                        "	FROM chat" +
                        "	WHERE destinatario = ? AND orario > ?" +
                        "	GROUP BY mittente; "
            );
            
            PreparedStatement ps1 = co.prepareStatement(
                        "SELECT email, nNuoviMessaggi " +
                        "FROM utenti_connessi LEFT OUTER JOIN nNuoviMessaggi ON email = mittente " +
                        "WHERE email != ?" +
                        "ORDER BY nNuoviMessaggi DESC"
            );
        ){
            ps0.setString(1, utenteConnesso);
            ps0.setString(2, current_timestamp.toString());
            ps1.setString(1, utenteConnesso);
            
            ps0.executeUpdate();    // (03)
            ResultSet rs = ps1.executeQuery(); 
            
            while (rs.next()){
                Integer messaggiGiaLetti;
                
                if (messaggiUtenteLetti.get(rs.getString("email")) == null)
                    messaggiGiaLetti = 0;
                else
                    messaggiGiaLetti = messaggiUtenteLetti.get(rs.getString("email"));  // (06)
   
                // (04)
                if (rs.getString("email").equals(contattoSelezionato) && 
                        (rs.getInt("nNuoviMessaggi") > messaggiGiaLetti)) { // (05)
                        messaggiGiaLetti = rs.getInt("nNuoviMessaggi");
                        messaggiUtenteLetti.put(rs.getString("email"), messaggiGiaLetti);   // (06)
                } 
                Integer nuoviMessaggi = rs.getInt("nNuoviMessaggi") - messaggiGiaLetti;
                ol.add(new Utente(rs.getString("email"), nuoviMessaggi));  
            }
        } catch (SQLException e) { System.err.println(e.getMessage());}  
    }
  
    public int caricaChat( String mit, String des, String from, String to, ObservableList<Messaggio> ol ) {

        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
                
            // (07)
            PreparedStatement ps0 = co.prepareStatement(
                        "SELECT COUNT(*) as Connesso FROM utenti_connessi WHERE email = ?"
            );
                
            PreparedStatement ps1 = co.prepareStatement(
                        "SELECT * FROM chat "
                        + "WHERE ((mittente = ? && destinatario = ?) || (mittente = ? && destinatario = ?)) "
                        + "AND (DATE(orario) BETWEEN ? AND ? )"
                        + "ORDER BY orario ASC"
            );
        ){
            int destinatarioConnesso = 0;
            ps0.setString(1, des);
            ResultSet rs = ps0.executeQuery();
            
            if (rs.next())
                destinatarioConnesso = rs.getInt("Connesso");
            
            // (07)
            if (destinatarioConnesso == 0)
                return 1;
            
            ps1.setString(1, mit);
            ps1.setString(2, des);
            ps1.setString(3, des);
            ps1.setString(4, mit);
            ps1.setString(5, from);
            ps1.setString(6, to);

            rs = ps1.executeQuery();
     
            while (rs.next()){
                String orario =  rs.getString("orario").substring(0, rs.getString("orario").length() - 2);  // (08)
                
                ol.add(new Messaggio(rs.getInt("id"),
                                     rs.getString("mittente"), 
                                     rs.getString("destinatario"),
                                     rs.getString("messaggio"),
                                     orario));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); return -1;}  
        return 0;
    }

    public void inviaMessaggio(String mit, String des, String mes) {
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps = co.prepareStatement("INSERT INTO chat VALUES (NULL, ?, ?, ?, current_timestamp())");
        ){
            ps.setString(1, mit);
            ps.setString(2, des);
            ps.setString(3, mes);
            System.out.println("DEBUG: rows affected;" + ps.executeUpdate());
        } catch (SQLException e) { System.err.println(e.getMessage());}
    }

    public void eliminaMessaggio(int id) {
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps = co.prepareStatement("DELETE FROM chat WHERE id = ?");
        ){
            ps.setInt(1, id);
            System.out.println("DEBUG: rows affected;" + ps.executeUpdate());
        } catch (SQLException e) { System.err.println(e.getMessage());}
    }
    
    public int ottieniNMessaggiInviati(String utente, String from, String to) {
        int nMessaggiInviati = 0;
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps = co.prepareStatement(
                        "SELECT COUNT(*) as Inviati FROM chat "
                        + "WHERE mittente = ? AND "
                        + "(orario BETWEEN ? AND ?)"
            );
        ){
            ps.setString(1, utente);
            ps.setString(2, from + " 00:00:00");    // (09)
            ps.setString(3, to + " 23:59:59");      // (09)
            
            ResultSet rs = ps.executeQuery();  
            
            if (rs.next())
                nMessaggiInviati = rs.getInt("Inviati");

        } catch (SQLException e) { System.err.println(e.getMessage());}
        
        return nMessaggiInviati;
    }
    
    public int ottieniNMessaggiRicevuti(String utente, String from, String to) {
        int nMessaggiRicevuti = 0;
        try ( Connection co = DriverManager.getConnection("jdbc:mysql://" + 
                indirizzoIpServerDBMS + ":" + portaServerDBMS + "/jchat", 
                usernameDBMS, passwordDBMS);
            PreparedStatement ps = co.prepareStatement(
                        "SELECT COUNT(*) as Inviati FROM chat "
                        + "WHERE destinatario = ? AND " 
                        + "(orario BETWEEN ? AND ?)"
            );
        ){
            ps.setString(1, utente);
            ps.setString(2, from + " 00:00:00");    // (09)
            ps.setString(3, to + " 23:59:59");      // (09)
            ResultSet rs = ps.executeQuery();    
            
            if (rs.next())
                nMessaggiRicevuti = rs.getInt("Inviati");
            
        } catch (SQLException e) { System.err.println(e.getMessage());}
        
        return nMessaggiRicevuti;
    }
}

/*
    Note:
    (00)
        Classe che gestisce le operazione con cui è possibile accedere al database 
        dell'applicativo.
    (01)
        I parametri di configurazione sono utilizzati per inizializzare i dati relativi
        al server DBMS.
    (02)
        Metodo che restituisce una lista degli utenti connessi all'applicazione affiancando 
        al nome utene anche il numero di messaggi inviati da essi dopo il timestamp 
        specificato come parametro, ovvero i messaggi ancora non visualizzato dall'utente.
    (03)
        Creo la View.
    (04)
        Non considero come messaggi da visualizzare i messaggi inviati dal contatto con 
        cui l'utente connesso ha attualmente la chat aperta.
    (05)
        C'è un nuovo messaggio non letto.
    (06)
        Aggiorno il contenuto della Map che associa ad ogni contatto connesso attualmente
        il numero di messaggi non ancora visualizzato dall'utente che sta usando l'applicativo.
    (07)
        Verifico che il contatto sia ancora online, altrimenti avviso l'utente della 
        disconnessione.
    (08)    
        Rimuovo il .0 che veniva aggiunto automaticamente al timestamp.
    (09)
        Converto i valori Date restituiti dai due datepicker nei corrispondenti valori 
        timestamp.
*/
