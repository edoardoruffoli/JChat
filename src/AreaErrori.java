import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 *
 * @author Edoardo
 */
public class AreaErrori {   // (00)
    
    private final TextArea areaDiTesto;
    private final Label titolo;
    private final InterfacciaMessaggi interfacciaMessaggi;
    private final VBox contenitore;
    private final ParametriConfigurazioneXML config;

    public AreaErrori(InterfacciaMessaggi i, ParametriConfigurazioneXML c) {
        contenitore = new VBox(20);
        areaDiTesto = new TextArea("         Benvenuto su JChat!");
        titolo = new Label("Area Messaggi");
        config = c;
        interfacciaMessaggi = i;
        contenitore.getChildren().addAll(titolo, areaDiTesto);
    }
    
    public void cambiaPosizione() {
        if (contenitore.getLayoutX() != 500){
            contenitore.setLayoutY(650);
            contenitore.setLayoutX(500);
        }
        else {
            contenitore.setLayoutY(150);
            contenitore.setLayoutX(280);
        }
    }
    
    public void impostaStile() {
        contenitore.setLayoutY(150);
        contenitore.setLayoutX(280);
        titolo.setStyle("-fx-font-weight: bold; " + 
                        "-fx-font-size: " + config.getFontSize()*1.3 + ";");
        areaDiTesto.setStyle("-fx-font-size: " + config.getFontSize()*1.3 + ";");
        areaDiTesto.setEditable(false); // (01)
        areaDiTesto.setPrefHeight(150);
        areaDiTesto.setPrefWidth(410);
        areaDiTesto.setWrapText(true);  // (02)
        contenitore.setAlignment(Pos.CENTER);
    }
    
    public void setMessaggioDiErrore(String msg) { areaDiTesto.setText(msg); }
    public VBox getContenitore() { return contenitore;} 
}

/*
    Note:
    (00)
        Sezione dell'interfaccia grafica dedicata alla visualizzazione di messaggi di sistema.
    (01)
        Non è possibile editare messaggi di sistema.
    (02)
        Permette che il messaggi di sistema venga visualizzato su più righe.
*/