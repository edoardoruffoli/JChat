import java.time.LocalDate;
import java.util.Arrays;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 *
 * @author Edoardo
 */
class AreaGrafico { // (00)
    
    private final VBox contenitore;
    private final Label titolo;
    private final BarChart<String, Integer> grafico;
    private boolean visible;
    private final InterfacciaMessaggi interfacciaMessaggi;
    private final DatabaseManager databaseManager;
    private final ParametriConfigurazioneXML config;
    
    public AreaGrafico(InterfacciaMessaggi i, ParametriConfigurazioneXML c, DatabaseManager db) {   
        contenitore = new VBox();
        titolo = new Label("Statistiche Utilizzo");

        CategoryAxis yAxis = new CategoryAxis();   
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
            "Inviati", "Ricevuti")));   // (01)
        NumberAxis xAxis = new NumberAxis();
        
        grafico = new BarChart(xAxis, yAxis);
        
        databaseManager = db;
        config = c;
        interfacciaMessaggi = i;
        visible = false;    // (02)
        contenitore.getChildren().addAll(titolo, grafico);
    }
    
    public void aggiornaDatiGrafico(LocalDate from, LocalDate to) { 
        if  (from == null || to == null)
            return;
        
        grafico.getData().clear();  // (03)
        
        Integer nInviati = databaseManager.ottieniNMessaggiInviati(interfacciaMessaggi.getUtenteConnesso(),
                                                                   from.toString(), 
                                                                   to.toString());  // (04)
        Integer nRicevuti = databaseManager.ottieniNMessaggiRicevuti(interfacciaMessaggi.getUtenteConnesso(), 
                                                                   from.toString(),
                                                                   to.toString());  // (05)
        
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();   
        series1.getData().add(new XYChart.Data(nInviati, "Inviati"));
        series1.getData().add(new XYChart.Data(nRicevuti, "Ricevuti"));
       
        grafico.getData().add(series1); // (06)
        grafico.setTitle("Inviati: " + nInviati.toString() + "  Ricevuti: " + nRicevuti.toString()); 
    }
    
    public void impostaStile() {
        contenitore.setLayoutY(650);
        contenitore.setLayoutX(0);
        contenitore.setVisible(false);
        contenitore.setAlignment(Pos.CENTER);
        titolo.setStyle("-fx-font-weight: bold; " + 
                        "-fx-font-size: " + config.getFontSize()*1.3 + ";");
        grafico.setPrefHeight(200);
        grafico.setPrefWidth(380);
    }
    
    void cambiaVisibilita() {
        visible = !visible;
        contenitore.setVisible(visible);
    }
    
    public VBox getContenitore() { return contenitore; }
}

/*
    Note:
    (00)
        Area dell'interfaccia grafica contenente il grafico sulle statistiche di utilizzo
        dell'applicativo.
    (01)
        Setta i nomi delle due barre del grafico.
    (02)
        Inizialmente la sezione contenente il grafico non è visible.
    (03)
        Viene azzerato il contenuto del grafico.
    (04)
        Numero di messaggi inviati dall'utente.
    (05)
        Numero di messaggi riceuvti dall'utente.
    (06)
        Viene aggiornato il contenuto del grafico.
*/
