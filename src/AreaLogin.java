import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 *
 * @author Edoardo
 */
public class AreaLogin {    // (00)
    
    private final static int SPACING = 10;
    private final DatabaseManager databaseManager;
    private final HBox contenitore;
    private final Label inputLabel;
    private TextField inputEmail;
    private Button connectBtn;
    private EventHandler<ActionEvent> connettiHandler, disconnettiHandler;
    
    public AreaLogin(InterfacciaMessaggi interfacciaMessaggi, ParametriConfigurazioneXML config, DatabaseManager db){
        contenitore = new HBox(SPACING);
        databaseManager = db;
        inputLabel = new Label("Email");
        inputEmail = new TextField();
        connectBtn = new Button("Connetti");
        
        connettiHandler = (ActionEvent ev )-> {
            boolean ret = databaseManager.inserisciUtenteConnesso(inputEmail.getText());    // (01)
            if (ret) {
                interfacciaMessaggi.connessioneAvvenuta(inputEmail.getText());  // (02)
                connectBtn.setText("Disconnetti");
                connectBtn.setOnAction(disconnettiHandler);
                inputEmail.setEditable(false);  // (03)
            }
            else
                interfacciaMessaggi.mostraErrore("        Errore di connessione");  // (04)
        };
        
        disconnettiHandler = (ActionEvent ev )-> {
            databaseManager.rimuoviUtenteConnesso(inputEmail.getText());    // (05)
            interfacciaMessaggi.disconnessioneAvvenuta();   // (06)
            connectBtn.setText("Connetti");
            connectBtn.setOnAction(connettiHandler);
            inputEmail.setEditable(true);   // (07)
        };

        connectBtn.setOnAction(connettiHandler);    
        contenitore.getChildren().addAll(inputLabel, inputEmail, connectBtn);
    }
    
    public void impostaStile(){
        contenitore.setLayoutX(280);
        contenitore.setLayoutY(10);
        inputEmail.setMaxWidth(500);
    }
    
    public void setInputEmail(String email) { inputEmail.setText(email); }
    public String getInputEmail() { return inputEmail.getText(); }
    public HBox getContenitore() { return contenitore;}
}

/* 
    Note:
    (00)
        Area dell'interfaccia grafica che contiene il TextInput e il Bottone per effettuare
        la connessione all'applicativo.
    (01)
        L'utente che effettua il login viene aggiunto al database: alla tabella utenti_connessi
    (02)
        Metodo che rende visibili le sezioni dell'interfaccia che non sono visibili prima
        della connessione dell'utente.
    (03)
        Dopo la connessione il campo input non può essere modificato.
    (04)
        Viene visualizzato un messaggio di errore nell'area contenente i messaggi di 
        sistema.
    (05)
        Dopo la disconnessione l'utente viene rimosso dalla tabella utenti_connessi del 
        database.
    (06)
        Metodo che nasconde le sezioni dell'interfaccia che non devono essere visibili
        prima di effettuare la connessione.
    (07)
        Dopo la disconnessione il campi input può essere modificato.
*/