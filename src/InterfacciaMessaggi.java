import java.sql.Timestamp;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 *
 * @author Edoardo
 */
public class InterfacciaMessaggi {  // (00)
    
    private final AnchorPane contenitore;   // (01)
    private final AreaLogin areaLogin;
    private final AreaChat areaChat;
    private final AreaContatti areaContatti;
    private final AreaGrafico areaGrafico;
    private final AreaErrori areaErrori;
    private final DatabaseManager databaseManager;
    private final ParametriConfigurazioneXML config;
    private String utenteConnesso;          // (02)
    private String contattoSelezionato;     // (03)
    private Timestamp orarioAccesso;        // (04)

    public InterfacciaMessaggi(ParametriConfigurazioneXML c, String fileCache) { 
        config = c;
        databaseManager = new DatabaseManager(this, config);
        contenitore = new AnchorPane();
        areaLogin = new AreaLogin(this, config, databaseManager);
        areaContatti = new AreaContatti(this, config, databaseManager);
        areaChat = new AreaChat(this, config, databaseManager);
        areaGrafico = new AreaGrafico(this, config, databaseManager);
        areaErrori = new AreaErrori(this, config);
        
        Cache cache = GestoreCache.carica(fileCache);   // (05)
        if (cache != null){
            areaLogin.setInputEmail(cache.getEmail());
            areaChat.setInputTestoCache(cache.getInputTestoCache());
        }
                
        contattoSelezionato = null;
        utenteConnesso = null;
        contenitore.getChildren().addAll(areaLogin.getContenitore(),
                                         areaContatti.getContenitore(),
                                         areaChat.getContenitore(),
                                         areaGrafico.getContenitore(),
                                         areaErrori.getContenitore()
                                        );
    }

    public void connessioneAvvenuta(String utente) {
        System.out.println("DEBUG: Utente connesso");
        areaErrori.setMessaggioDiErrore("       Connessione Avvenuta");
        GestoreLog.creaLog("Connessione", config);  // (06)
        utenteConnesso = utente;
        orarioAccesso = new Timestamp(System.currentTimeMillis());  // (07)
        areaContatti.cambiaVisibilita();    // (08)
        areaGrafico.cambiaVisibilita();     // (08)
        areaGrafico.aggiornaDatiGrafico(areaContatti.getFrom(), areaContatti.getTo());  // (09)
        areaErrori.cambiaPosizione();   // (10)
        
        transizioneFade();
    }
    
    public void disconnessioneAvvenuta() {
        System.out.println("DEBUG: Utente disconnesso");
        areaErrori.setMessaggioDiErrore("     Disconnessione Avvenuta");
        GestoreLog.creaLog("Disconnessione", config);   // (11)
        utenteConnesso = null;
        areaContatti.cambiaVisibilita();    // (12)
        areaGrafico.cambiaVisibilita();     // (12)
        areaErrori.cambiaPosizione();       // (13)
        
        if (areaChat.getVisibilita()){      
            areaChat.salvaInputTestoCorrente(); // (14)
            areaChat.cambiaVisibilita();    // (12)
        }
        
        transizioneFade();
    }
    
    public void apriChat(String contatto) {
        if (contattoSelezionato == null || !contattoSelezionato.equals(contatto)){
            contattoSelezionato = contatto;
            areaChat.mostraChat(contattoSelezionato);   // (15)
            
            if (!areaChat.getVisibilita())
                areaChat.cambiaVisibilita();
        }
    }
    
    public void aggiornaGrafico() {
        areaGrafico.aggiornaDatiGrafico(areaContatti.getFrom(), areaContatti.getTo());
    }
    
    public void mostraErrore(String msg) {
        areaErrori.setMessaggioDiErrore(msg);
    }
    
    void transizioneFade() {
        FadeTransition ft = new FadeTransition(Duration.millis(1000), contenitore);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
    
    public void impostaStile() {    // (16)
        contenitore.setStyle("-fx-background-color: "+ config.getColoreTema() + ";" +
                             "-fx-font-family: "+ config.getFont() + ";" + 
                             "-fx-font-size: "+ config.getFontSize() + ";"); // 17)
        areaLogin.impostaStile();
        areaContatti.impostaStile();
        areaChat.impostaStile();
        areaGrafico.impostaStile();
        areaErrori.impostaStile();
    }
    
    public String getAreaLoginInput() { return areaLogin.getInputEmail(); }
    public Map getAreaChatInputTestoCache() { return areaChat.getInputTestoCache(); }
    public String getUtenteConnesso() { return utenteConnesso; }
    public Timestamp getOrarioAccesso() { return orarioAccesso; }
    public String getContattoSelezionato() { return contattoSelezionato; }
    public String getFrom() { return areaContatti.getFrom().toString(); }
    public String getTo() { return areaContatti.getTo().toString(); }
    public AnchorPane getContenitore() { return contenitore; }
}

/*
    Note:
    (00)
        Classe che implementa l'interfaccia grafica dell'applicazione.
    (01)
        Contenitore dell'interfaccia.
    (02)
        Stringa contenente l'username dell'utente attualmente connesso.
    (03)
        Stringa contenente l'username del contatto con cui l'utente ha la chat aperta.
    (04)
        Orario accesso all'applicazione.
    (05)
        Caricamento dati dalla cache locale degli input.
    (06)
        Viene inviato un log al server di log, per notificare l'evento di connessione.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (07)
        Viene salvato l'orario di accesso al servizio. Sarà utile per la ricezione delle
        notifiche dei messaggi.
    (08)
        AreaContatti e AreaGrafico vengono rese visibili in seguito alla connessione 
        dell'utente al servizio.
    (09)
        Viene inizializzato il contenuto del grafico sulle statistiche di utilizzo usando
        i valori dei datepicker inizializzati con il file di configurazione.
    (10)
        L'AreaErrori che mostra i messaggi di sistema, viene spostata in basso a destra.
    (11)
        Viene inviato un log al server di log, per notificare l'evento di disconnessione.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (12)
        AreaContatti, AreaGrafico, AreaChat (se era visibile) vengono nascoste in seguito 
        alla disconnessione dell'utente al servizio.
    (13)
        L'AreaErrori che mostra i messaggi si sistema, viene spostata al centro della 
        schermata.
    (14)
        Viene salvato nella cache locale degli input il testo che era inserito nel textInput
        della chat.
    (15)
        Viene visualizzato la chat con il contatto passato come parametro.
    (16)
        Metodo che imposta lo stile dell'interfaccia grafica.
*/
