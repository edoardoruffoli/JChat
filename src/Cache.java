import java.io.*;
import java.util.*;

/**
 *
 * @author Edoardo
 */
public class Cache implements Serializable {    // (00)
    
    private final String email;
    private Map <String, String> inputTestoCache;   
    
    public Cache(InterfacciaMessaggi i) {
        email = i.getAreaLoginInput();  // (02)
        inputTestoCache = i.getAreaChatInputTestoCache();   // (03)
    }
    
    public String getEmail() { return email; }
    public Map getInputTestoCache() { return inputTestoCache; }
}

/* Note
    (00)
        Alla chiusura dell'applicazione vengono salvati su cache locale gli input dell'utente.
        Classe che viene usata per salvare/caricare da file binario i valori della cache.
    (01)
        Crea un oggetto cache utilizzando i valori dell'interfaccia grafica.
    (02)
        Viene salvato il nome utente inserito nel campo di testo login.
    (03)
        Oggetto contenente tutti gli input di testo non nulli delle chat con i vari 
        contatti.
*/
