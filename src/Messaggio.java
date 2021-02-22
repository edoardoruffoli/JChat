import javafx.beans.property.*;
import javafx.event.*;
import javafx.scene.control.Button;

/**
 *
 * @author Edoardo
 */
public class Messaggio {    // (00)
    
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty mittente;
    private final SimpleStringProperty destinatario;
    private SimpleStringProperty messaggio;  
    private SimpleStringProperty orario;
    private SimpleBooleanProperty elimina;
    
    public Messaggio(Integer i, String mit, String des, String mes, String o) {
        id = new SimpleIntegerProperty(i);
        mittente = new SimpleStringProperty(mit);
        destinatario = new SimpleStringProperty(des);
        messaggio = new SimpleStringProperty(mes);
        orario = new SimpleStringProperty(o);
        elimina = new SimpleBooleanProperty(false);
    }

    public void setMessaggio(String mes) { messaggio.set(mes); }
    public void setOrario(String o) { orario.setValue(o); }
    public void setElimina(Boolean b) { elimina.setValue(b); }
    public Integer getId() { return id.get(); }
    public String getMittente() { return mittente.get(); }
    public String getDestinatario() { return destinatario.get(); } 
    public String getMessaggio() { return messaggio.get(); }
    public String getOrario() { return orario.get(); }
    public Object getElimina() { return elimina.get(); }
}

/* 
    Note:
    (00)
        Classe contenente le informazioni di un record messaggio del database. Contiene
        anche un campo aggiuntivo 'elimina' che verrà settatto a true se il mittente del 
        messaggio è lo stesso utente connesso all'applicativo, e quindi può eliminarlo.
        
*/
