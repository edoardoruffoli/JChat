import javafx.stage.*;
import javafx.application.Application;
import javafx.scene.Scene;

/**
 *
 * @author Edoardo
 */
public class JChat extends Application {
    
    private final static String FILE_CONFIG = "config.xml",     // (01)
                                SCHEMA_CONFIG = "config.xsd",   // (02) 
                                FILE_CACHE = "cache.bin";       // (03)
                                
    private InterfacciaMessaggi interfacciaMessaggi;
  
    public void start(Stage stage) {
        ParametriConfigurazioneXML config =
            new ParametriConfigurazioneXML(
                ValidazioneXML.valida(GestoreFile.carica(FILE_CONFIG), SCHEMA_CONFIG) ? 
                    GestoreFile.carica(FILE_CONFIG) : null
            );  // (04)
        
        GestoreLog.creaLog("Apertura applicazione", config);    // (05)

        interfacciaMessaggi = new InterfacciaMessaggi(config, FILE_CACHE);  // (06)
        interfacciaMessaggi.impostaStile();

        stage.setOnCloseRequest((WindowEvent we) -> {    // (07)
            if (interfacciaMessaggi.getUtenteConnesso() != null) {
                interfacciaMessaggi.mostraErrore("Per chiudere l'applicazione devi prima disconnetterti");
                we.consume(); // (08) 
            }
            else {
                GestoreLog.creaLog("Chiusura applicazione", config);
                GestoreCache.salva(interfacciaMessaggi, FILE_CACHE);
                System.exit(0);
            }
        });
        
        stage.setTitle("JChat");
        Scene scene = new Scene(interfacciaMessaggi.getContenitore());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

/* 
    Note:
    (01) 
        File contenente i parametri di configurazione in XML.
    (02)
        File contenente lo schema XSD dei parametri di configurazione.
    (03)
        File di cache locale degli input.
    (04)
        Classe contenente i parametri di configurazione XML. I parametri vengono caricati solo se
        la validazione ha avuto successo, altrimenti passiamo null come parametro al costruttore
        di ParametriDiConfigurazioneXML così da impostare i valori di default.
    (05)
        Viene inviato un log al server di log, per notificare l'evento di avvio dell'applicazione.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (06)
        Creazione dell'interfaccia grafica dell'applicazione usando i parametri di configurazione
        e il contenuto della cache locale degli input.
    (07)
        Quando l'applicazione viene chiusa, si controlla che l'utente abbia effettuato la disconnessione.
        Se si è disconnesso allora si invia un log al server di log per notificare la chiusura 
        dell'applicazione e viene salvata la cache locale degli input.
        Se non si è disconnesso viene mostrato un messaggio di errore e viene impedita la chiusura.
    (08)
        Impedisce la chiusura dell'applicazione
*/