import java.time.LocalDate;
import java.util.*;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

/**
 *
 * @author Edoardo
 */
public final class AreaContatti {   // (00)
    
    private final InterfacciaMessaggi interfacciaMessaggi;
    private final DatabaseManager databaseManager;
    private final ParametriConfigurazioneXML config;
    private final VBox contenitore;
    private static ObservableList<Utente> contattiConnessi;
    private FilteredList<Utente> contattiConnessiFiltrati;  // (01)
    private TableView<Utente> listaContatti;
    private final TableColumn emailCol;
    private final TableColumn nNuoviMessaggiCol;
    private Map<String, Integer> messaggiUtenteLetti;   // (02)
    private final int numUtentiDaMostrare;
    private final HBox contenitoreSearchBar;
    private final Label searchBarLabel;
    private final TextField searchBar;
    private final HBox contenitoreDatePicker;
    private static DatePicker from;
    private static DatePicker to;
    private final EventHandler<ActionEvent> cambioDataHandler;
    private final Timer timerListaContatti; // (03)
    private boolean visible;
    
    public AreaContatti(InterfacciaMessaggi i, ParametriConfigurazioneXML c, DatabaseManager db) {
        contenitore = new VBox(20);
        databaseManager = db;
        config = c;
        interfacciaMessaggi = i;

        listaContatti = new TableView<>();
        contattiConnessi = FXCollections.observableArrayList();   
        contattiConnessiFiltrati = new FilteredList(contattiConnessi);
          
        emailCol = new TableColumn("Contatti");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        nNuoviMessaggiCol = new TableColumn("");
        nNuoviMessaggiCol.setCellValueFactory(new PropertyValueFactory<>("nNuoviMessaggi"));
        nNuoviMessaggiCol.setCellFactory((tableColumn) -> {
            return new TableCell<Utente, Integer>() {
                protected void updateItem(Integer nuoviMessaggi, boolean empty) {
                    super.updateItem(nuoviMessaggi, empty);
                    if (nuoviMessaggi == null || empty || nuoviMessaggi <= 0) {
                        setText(null);
                    } else {
                        setText("!");   // (04)
                    }
                }
            };
        });
        
        listaContatti.getColumns().addAll(emailCol, nNuoviMessaggiCol);
        listaContatti.setItems(contattiConnessiFiltrati);   // (05)    
        listaContatti.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2 && listaContatti.getSelectionModel().getSelectedItem() != null) {
                // (06)
                contattoSelezionato(listaContatti.getSelectionModel().getSelectedItem().getEmail());
            }
        });
        messaggiUtenteLetti = new HashMap();
        numUtentiDaMostrare = c.getNumUtentiDaMostrare();   // (07)
        
        contenitoreSearchBar = new HBox(10);
        searchBarLabel = new Label("Cerca Contatto");
        searchBar = new TextField();
        searchBar.textProperty().addListener((observa,oldValue,newValue)->
                contattiConnessiFiltrati.setPredicate(x -> x.getEmail().contains(newValue))
           );   // (08)  
        searchBar.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) // (09)
                GestoreLog.creaLog("Focus campo CERCA", config);    // (10)
        });
        
        contenitoreSearchBar.getChildren().addAll(searchBarLabel, searchBar);
        
        contenitoreDatePicker = new HBox(10);
        
        LocalDate currentDate = LocalDate.now();
        to = new DatePicker();
        to.setValue(currentDate);
        
        LocalDate fromDate = currentDate.minusDays(c.getNumGiorniDaMostrare());
        from = new DatePicker();
        from.setValue(fromDate);
        
        cambioDataHandler = (ActionEvent ev)-> {   // (11)
            i.aggiornaGrafico();
        };

        from.setOnAction(cambioDataHandler);
        to.setOnAction(cambioDataHandler);      
        contenitoreDatePicker.getChildren().addAll(new Label("Da:"), from, new Label("A:"), to);
       
        timerListaContatti = new Timer();
        
        visible = false;    // (12)
        contenitore.getChildren().addAll(contenitoreDatePicker, contenitoreSearchBar, listaContatti);
    }
    
    private void avviaTimerAggiornamentoContatti() {    // (13)
        try {
            timerListaContatti.scheduleAtFixedRate(taskAggiornaListaContatti, 0, 2000); // (14)
        } catch(IllegalStateException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private final TimerTask taskAggiornaListaContatti = new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {        
                @Override
                public void run() {
                    int selectedRow = listaContatti.getSelectionModel().getSelectedIndex(); // (15)
                    contattiConnessi.clear();
                    databaseManager.caricaUtentiConnessiMostraNotifiche(contattiConnessi,
                                                                        interfacciaMessaggi.getContattoSelezionato(),
                                                                        interfacciaMessaggi.getOrarioAccesso(),
                                                                        messaggiUtenteLetti
                                                                        );  // (16)
                    listaContatti.getSelectionModel().select(selectedRow);  // (17)
                }
            });
        }
    };
   
    public void impostaStile() {
        contenitore.setLayoutY(100);
        contenitore.setLayoutX(15);
        contenitore.setVisible(false);    
        from.setPrefWidth(170);
        to.setPrefWidth(170);

        emailCol.prefWidthProperty().bind(listaContatti.widthProperty().multiply(0.85));
        nNuoviMessaggiCol.prefWidthProperty().bind(listaContatti.widthProperty().multiply(0.083));
        emailCol.setStyle("-fx-alignment: CENTER;");
        nNuoviMessaggiCol.setStyle("-fx-alignment: CENTER;" + " -fx-font-weight: bold");

        listaContatti.setFixedCellSize(50);
        listaContatti.setPrefWidth(350);
        listaContatti.setPrefHeight(listaContatti.getFixedCellSize()*numUtentiDaMostrare 
                                    + listaContatti.getFixedCellSize());   // (18)
    }   
    
    void contattoSelezionato(String contattoSelezionato) {
        System.out.println("DEBUG: Selezionato " + contattoSelezionato);

        for (Utente u : contattiConnessi ) {    // (19)
            if (u.getEmail().equals(contattoSelezionato)){
                messaggiUtenteLetti.put(contattoSelezionato, u.getNNuoviMessaggi());
                if (u.getNNuoviMessaggi() > 0)  // (20)
                    interfacciaMessaggi.aggiornaGrafico();  
                break;
            }
        }
        interfacciaMessaggi.apriChat(contattoSelezionato);  // (21)
    }
    
    void cambiaVisibilita() {
        visible = !visible;
        if(visible)
            avviaTimerAggiornamentoContatti();  // (22)
        else {
            timerListaContatti.cancel();
            timerListaContatti.purge(); // (23)
        }
        contenitore.setVisible(visible);
    }
    
    public LocalDate getFrom() { return from.getValue(); }
    public LocalDate getTo() { return to.getValue(); }
    public VBox getContenitore(){ return contenitore;}
}

/*
    Note:
    (00)
        Area che mostra la lista dei contatti connessi, due datepicker 'da' e 'a' e una
        barra di ricerca per filtrare la lista dei contatti.
    (01)
        Lista contenente solo i contatti i cui nomi corrispondono al pattern contneuto 
        nella searchBar.
    (02)
        Contiene il numero di messaggi letti nella chat con il contatto String dall'inizio 
        della sessione, definito dall'orario di accesso.
    (03)
        Timer aggiornamento contatti.
    (04)
        Se il contatto in questione ha inviato un messaggio quando l'utente non aveva 
        aperto una chat con esso, viene mostrato un '!' accanto al nome del contatto
        per notificare l'utente.
    (05)
        La TableView listaContatti mostrerà i contatti che corrispondono al pattern contenuto
        nella sarchBar.
    (06)
        Se viene fatto un doppio click su un contatto, viene aperta la chat con il contatto
        selezionato.
    (07)
        Il numero di utenti che possono essere visualizzati viene ricavato dall'oggetto
        parametriConfigurazioneXML.
    (08)
        Ogni volta che il pattern di testo della searchBar cambia, vengono rimossi dalla lista 
        contattiConnessiFiltrati tutti gli Utenti che non hanno un nome contenente il pattern.
    (09)
        La variabile newVal vale true quando il campo textfield è selezionato.
    (10)
        Viene inviato un log al server di log, per notificare l'evento di selezione del
        campo cerca.
        Le informazioni relative al server di log, quali IP e porta, sono contenuti nell'istanza
        config di ParametriConfigurazioneXML.
    (11)
        Quando il contenuto di uno dei due datepicker cambia, il contenuto del grafico 
        contenente le statistiche di utilizzo viene aggiornato.
    (12)
        Inizialmente l'areaContatti non è visibile.
    (13)
        Timer che si occupa di aggiornare periodicamente il contenuto della lista dei 
        contatti connessi, interrogando il database.
    (14)
        Task periodica del timer.
    (15)
        Salvo la riga selezionata dall'utente, perchè l'aggiornamento della TableView non
        la mantiene. 
    (16)
        Funzione che ritorna la lista dei contatti connessi più una colonna contenente il
        numero di messaggi inviati dal contatto corrispondente all'utente dopo l'orario di accesso 
        dell'utente.
    (17)
        Ripristino la riga selezionata dall'utente prima dell'aggiornamento della TableView.
    (18)
        Setto l'altezza della ListaContatti in modo da mostrare il numero di contatti specificati
        nel file di configurazione. Aggiungo anche il valore di una cella extra perchè l'header
        della TableView viene considerato nell'altezza totale della TableView.
    (19)
        Quando l'utente seleziona un contatto per aprire la chat, azzero eventuali notifiche
        di nuovi messaggi.
    (20)
        Se il contatto selezionato aveva inviato messaggi, e quindi era stata visualizzata
        una notifica, aggiorno il grafico delle statistiche.
    (21)
        Apre la chat con il contatto selezionato.
    (22)
        Avvio la task di aggiornamento della lista dei contatti connessi.
    (23)
        Fermo la task di aggiornamento della lista dei contatti connessi.
*/