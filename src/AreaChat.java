import java.util.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 *
 * @author Edoardo
 */
class AreaChat {    // (00)
    
    private String contatto;    // (01)
    private int maxCaratteri;
    private final VBox contenitore;
    private final HBox contenitoreInvio;
    private static ObservableList<Messaggio> messaggiChat;
    private TableView<Messaggio> chat;
    private final TableColumn messaggioCol;
    private final TableColumn orarioCol;
    private final TableColumn updateCol;    // (02)
    private final TextField inputTesto;
    private Map <String, String> inputTestoCache;
    private final Button invioBtn;
    private Timer timerChat; // (03) 
    private boolean primoAggiornamentoChat;
    private final EventHandler<ActionEvent> invioHandler; // (04)
    private Integer oldIdMessaggioSelezionato;    // (05)
    private final DatabaseManager databaseManager;
    private final InterfacciaMessaggi interfacciaMessaggi;
    private final ParametriConfigurazioneXML config;
    private boolean visible;
    
    public AreaChat(InterfacciaMessaggi i, ParametriConfigurazioneXML c, DatabaseManager db) {
        contenitore = new VBox(10);
        contenitoreInvio = new HBox(10);
        chat = new TableView<>();
        config = c;
        databaseManager = db;
        interfacciaMessaggi = i;
        contatto = null;
        
        maxCaratteri = config.getMaxCaratteriMessaggio();
        messaggiChat = FXCollections.observableArrayList();     
        orarioCol = new TableColumn();
        orarioCol.setCellValueFactory(new PropertyValueFactory<>("orario"));
        messaggioCol = new TableColumn();
        messaggioCol.setCellValueFactory(new PropertyValueFactory<>("messaggio"));
        messaggioCol.setCellFactory((tableColumn) -> {
            return new TableCell<Messaggio, String>() {
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        TextFlow flow = new TextFlow();
                        String parts[] = item.split(":");
                        
                        Text nomeMittente = new Text(parts[0] + ": ");
                        nomeMittente.setStyle("-fx-font-weight: bold;");

                        Text testoMessaggio = new Text(parts[1]);

                        flow.setPrefHeight(75);
                        flow.setStyle("-fx-font-family: " + config.getFont() + ";");
                        
                        flow.getChildren().addAll(nomeMittente, testoMessaggio);
                        setGraphic(flow);   // (06)
                    }
                }
            };
        });
        
        updateCol = new TableColumn();
        updateCol.setCellValueFactory(new PropertyValueFactory<>("elimina"));
        updateCol.setCellFactory((tableColumn) -> {
            return new TableCell<Messaggio, Boolean>() {
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty || !item) {
                        setGraphic(null);
                    } else {
                        Button eliminaBtn = new Button("X");
                        EventHandler<ActionEvent> eliminaHandler;
                        eliminaHandler = (ActionEvent ev)-> {
                            Messaggio m = messaggiChat.get(getIndex());
                            databaseManager.eliminaMessaggio(m.getId());
                            GestoreLog.creaLog("Eliminazione di un messaggio", config); // (07)
                        };   
                        
                        
                        eliminaBtn.visibleProperty().bind(
                              Bindings.createBooleanBinding(
                              () -> {
                                  if (this.getTableRow().getItem() != null && 
                                      chat.getSelectionModel().getSelectedItems().get(0) != null 
                                      && chat.getSelectionModel().getSelectedItems().get(0).equals(this.getTableRow().getItem()))
                                      return true;
                                  else 
                                      return false;
                                    }
                              )
                        );  // (08)
     
                        eliminaBtn.setOnAction(eliminaHandler);
                        setGraphic(eliminaBtn); // (06)
                    }
                }
            };
        });

        chat.getColumns().addAll(orarioCol, messaggioCol, updateCol);
        chat.setItems(messaggiChat); 
        
        oldIdMessaggioSelezionato = null;
        chat.getSelectionModel().selectedItemProperty().addListener((obs, old, neo) -> {
            if (neo != null) {
                if (oldIdMessaggioSelezionato == null || !Objects.equals(neo.getId(), oldIdMessaggioSelezionato)) {    // (09)
                    GestoreLog.creaLog("Selezione di un messaggio", config);    // (10)
                    oldIdMessaggioSelezionato = neo.getId();    // (11)
                }
            }
        });
        
        inputTesto = new TextField();
        inputTestoCache = new HashMap<>();
        invioBtn = new Button("Invia");
        
        invioHandler = (ActionEvent ev)-> {
            if ((inputTesto.getText().length() > 0) && 
                (inputTesto.getText().length() <= maxCaratteri)) {  // (12)
                databaseManager.inviaMessaggio(interfacciaMessaggi.getUtenteConnesso(),
                                              contatto, 
                                              inputTesto.getText());    // (13)
                inputTesto.clear(); // (14)
            }
            else if ((inputTesto.getText().length() > maxCaratteri)) {
                interfacciaMessaggi.mostraErrore("Messaggio troppo lungo");
            }
            GestoreLog.creaLog("Pressione tasto INVIA", config);    // (15)
        };    
        invioBtn.setOnAction(invioHandler);

        timerChat = new Timer();
        visible = false;
        
        contenitoreInvio.getChildren().addAll(inputTesto, invioBtn);
        contenitore.getChildren().addAll(chat, contenitoreInvio);
    }
    
    public void mostraChat(String c) {
        if (contatto != null) { 
            inputTestoCache.put(contatto, inputTesto.getText());    // (16)
        }
        inputTesto.setDisable(false);
        invioBtn.setDisable(false);
        chat.getItems().clear();
        chat.setPlaceholder(new Label("Caricamento in corso..."));
        inputTesto.clear();
        contatto = c;   // (17)
        messaggioCol.setText(c);
        inputTesto.setText(inputTestoCache.get(c));
        synchronized(this) {    // (18)
            primoAggiornamentoChat = true;
        }
   
        FadeTransition ft = new FadeTransition(Duration.millis(1000), contenitore);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();   
    }
    
    private void avviaTimerAggiornamentoChat() {
        try {
            timerChat.scheduleAtFixedRate(taskAggiornaChat, 0, 1000);
        } catch(IllegalStateException e) { System.err.println(e.getMessage()); }
    }
    
    private final TimerTask taskAggiornaChat = new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {        
                @Override
                public void run() {          
                    ObservableList oldMessaggiChat = FXCollections.observableArrayList(messaggiChat);   // (19)
                  
                    int selectedRow = chat.getSelectionModel().getSelectedIndex();  // (20)

                    chat.getItems().clear();

                    int ret = databaseManager.caricaChat(interfacciaMessaggi.getUtenteConnesso(), 
                                                         contatto, 
                                                         interfacciaMessaggi.getFrom(),
                                                         interfacciaMessaggi.getTo(),
                                                         messaggiChat); // (21)
                    if (ret == -1){
                        interfacciaMessaggi.mostraErrore("Errore caricamento conversazione");
                        return;
                    }
                    
                    if (ret == 1){
                        chat.setPlaceholder(new Label("Utente disconnesso"));
                        inputTesto.setDisable(true);
                        invioBtn.setDisable(true);
                        return;
                    }
                        
                    if (ret == 0 && messaggiChat.isEmpty()){
                        chat.setPlaceholder(new Label("Nessun messaggio in questa conversazione"));
                        primoAggiornamentoChat = false;
                        if (!oldMessaggiChat.isEmpty())
                                interfacciaMessaggi.aggiornaGrafico();
                        return;
                    }
                        
                    messaggiChat.forEach((Messaggio) ->{                        
                        if (Messaggio.getMittente().equals(interfacciaMessaggi.getUtenteConnesso())){
                            Messaggio.setElimina(true); // (22)
                            Messaggio.setMessaggio("TU: "+ Messaggio.getMessaggio()); 
                        }
                        else {
                            String nomeContatto = contatto.split("@")[0];
                            Messaggio.setMessaggio(nomeContatto + ": " + Messaggio.getMessaggio()); // (23)
                        }    
                        
                        // (24)
                        String nuovoOrario = Messaggio.getOrario();
                        String[] parts = nuovoOrario.split(" ");
                        nuovoOrario = parts[1];
                        nuovoOrario = nuovoOrario.substring(0, nuovoOrario.length()-3);
                        Messaggio.setOrario(nuovoOrario);  
                    });        
                     
                    synchronized(this) {    // (18)
                        if (oldMessaggiChat.size() != messaggiChat.size() && !primoAggiornamentoChat) 
                            interfacciaMessaggi.aggiornaGrafico();  // (25)

                        if (primoAggiornamentoChat) { // (26)
                            chat.scrollTo(messaggiChat.size()-1);
                            primoAggiornamentoChat = false;
                        }                                          
                    }
                    
                    chat.getSelectionModel().select(selectedRow);   // (27)
                }
            });
        }
    };
    
    public void impostaStile() {
        contenitore.setLayoutX(450);
        contenitore.setLayoutY(100);
        contenitore.setVisible(false);   
        contenitoreInvio.setPrefWidth(500);
  
        chat.setTableMenuButtonVisible(false);
        chat.setPrefWidth(500);
        chat.setPrefHeight(470);
        chat.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);    // (28)
        
        orarioCol.prefWidthProperty().bind(chat.widthProperty().multiply(0.15));
        messaggioCol.prefWidthProperty().bind(chat.widthProperty().multiply(0.65));
        updateCol.prefWidthProperty().bind(chat.widthProperty().multiply(0.146));

        orarioCol.setStyle("-fx-alignment: CENTER;");
        messaggioCol.setStyle("-fx-alignment: CENTER_LEFT;");
        updateCol.setStyle("-fx-alignment: CENTER;");
 
        orarioCol.setResizable(false);
        messaggioCol.setResizable(false);
        updateCol.setResizable(false);
        
        inputTesto.setPrefWidth(410);
        invioBtn.setPrefWidth(80);
    }   
    
    void cambiaVisibilita() {
        visible = !visible;
        if(visible)
            avviaTimerAggiornamentoChat();
        else {
            timerChat.cancel();
            timerChat.purge();
        }
        contenitore.setVisible(visible);
    }
    
    public void salvaInputTestoCorrente() { inputTestoCache.put(contatto, inputTesto.getText()); }
    public void setInputTestoCache(Map<String, String> map) { inputTestoCache = map; }
    public Map getInputTestoCache() { return inputTestoCache; }
    public boolean getVisibilita() { return visible; }
    public VBox getContenitore(){ return contenitore;}
}

/*
    Note:
    (00)
        Classe che implementa la sezione dell'interfaccia contenente la chat vera e propria
        con il contatto selezionato dall'utente.
    (01)
        Contatto con cui ho aperto la chat.
    (02)
        Colonna contenente i bottoni elimina messaggio (che saranno resi visibili dopo
        aver selezionato il messaggio con un click).
    (03)
        Timer associato a una task periodica di aggiornamento del contenuto della chat.
    (04)
        Handler associato al bottone di invio messaggio.
    (05)
        Oggetto utile per implementare la seleziona messaggio, memorizza l'indice dell'ultimo 
        messaggio selezionato dall'utente.
    (06)
        Setto l'aspetto della cella.
    (07)
        Viene inviato un log al server di log, per notificare l'evento di eliminazione
        di un messaggio.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (08)
        La visbilità del bottone elimina è collegata a una funzione booleana che restituisce
        true se l'elemento selezionato dall'utente corrisponde all'elemento contenuto 
        nella cella.
    (09)
        Se la cella selezionata dall'utente è cambiata allora la considero come una nuova
        selezione, evito dunque selezioni duplicate dovute all'aggiornamento della TableView
        chat.
    (10)
        Viene inviato un log al server di log, per notificare l'evento di selezione
        di un messaggio.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (11)
        Salvo l'indice del messaggio selezionato dall'utente.
    (12)
        Il messaggio non viene inviato se possiede un numero di caratteri maggiore di quelli
        specificati nel file di configurazione.
    (13)
        Aggiungo il messaggio inviato al database.
    (14)
        Svuoto l'input di testo.   
    (15)
        Viene inviato un log al server di log, per notificare l'evento di invio di un messaggio.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (16)
        Salvo il testo inserito nel TextInput, ma non inviato, del contatto con cui avevo
        la chat aperta in precendeza
    (17)
        Aggiorno il contatto con cui ho aperto la chat.
    (18)
        L'Oggetto primoAggiornamentoChat viene manipolato sia dal thread principale che
        dal thread di aggiornamento chat.
    (19)
        Faccio una copia della ObservableList così se la old è diverso dalla new aggiorno 
        il grafico.
    (20)
        Salvo la rige selezionata dall'utente perchè l'aggiornamento del contenuto della
        TableView non mantiene le selezioni.
    (21)
        Carico dal database i messaggi della chat con il contatto selezionato inviati 
        nell'intervallo di tempo specificato dai datepicker 'da' 'a'.
    (22)
        L'utente connesso è il proprietario di tale messaggio e quindi può eliminarlo.
    (23)
        Modifico il nome visualizzato a schermo del contatto, rimuovendo le parti relative
        all'indirizzo mail.
    (24)
        Modifico l'orario visualizzato a schermo dei messaggi in modo da mostrare solo 
        ora e minuto di invio.
    (25)
        Se i messaggi sono cambiati, aggiorno il grafico. Ho fatto un confronto sulla lunghezza
        delle sue list, non sul contenuto di esse, per avere più efficienza.
    (26)
        Se la chat viene aperta per la prima volta, mostro i messaggi a partire dal più 
        recente.    
    (27)
        Ri-seleziono la riga che l'utente aveva selezionato prima dell'aggiornamento. 
    (28)
        Non è possibile modificare la dimensione delle colonne dall'applicativo.
*/