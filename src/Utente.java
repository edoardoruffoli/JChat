import javafx.beans.property.*;

/**
 *
 * @author Edoardo
 */
public class Utente {   // (00)
    
    private final SimpleStringProperty email;
    private SimpleIntegerProperty nNuoviMessaggi;   // (01) 
    
    public Utente(String s, Integer n) {
        email = new SimpleStringProperty(s);
        nNuoviMessaggi = new SimpleIntegerProperty(n);
    }
    
    public String getEmail() { return email.get(); }
    public Integer getNNuoviMessaggi() { return nNuoviMessaggi.get(); }
}

/*
    Note:
    (00)
        Classe contenente il nome di un utente e il numero dei messaggi inviati dall' utente
        e ancora non letti dall'utente connesso.
    (01)
        Numero di messaggi inviati dall'utente e non ancora letti dall'utente connesso.
*/
