import com.thoughtworks.xstream.*;

public class ParametriConfigurazioneXML { // (00)
    
    	private final int maxCaratteriMessaggio;	
	private final String font;
	private final double fontSize;
	private final String coloreTema;
	private final String indirizzoIpServerDBMS;
	private final int portaServerDBMS;
	private final String usernameDBMS;
	private final String passwordDBMS;
	private final String indirizzoIpClient;
	private final String indirizzoIpServerDiLog;
	private final int portaServerDiLog;
        private final int numUtentiDaMostrare;
	private final int numGiorniDaMostrare;
    
    public ParametriConfigurazioneXML(String xml) {
        if (xml == null || xml.compareTo("") == 0) {    // (01)
            maxCaratteriMessaggio = 30;
            numUtentiDaMostrare = 5;
            font = "Helvetica";
            fontSize = 20.0;
            coloreTema = "white";
            indirizzoIpServerDBMS = "127.0.0.1";
            portaServerDBMS = 3306;
            usernameDBMS = "root";
            passwordDBMS = "";
            indirizzoIpClient = "127.0.0.1";
            indirizzoIpServerDiLog = "127.0.0.1";
            portaServerDiLog = 4242;
            numGiorniDaMostrare = 31;
        } else { 
            XStream xs = creaXStream();
            ParametriConfigurazioneXML p = (ParametriConfigurazioneXML) xs.fromXML(xml); // (02)
            
            maxCaratteriMessaggio = p.maxCaratteriMessaggio;
            numUtentiDaMostrare = p.numUtentiDaMostrare;
            font = p.font;
            fontSize = p.fontSize;
            coloreTema = p.coloreTema;
            indirizzoIpServerDBMS = p.indirizzoIpServerDBMS;
            portaServerDBMS = p.portaServerDBMS;
            usernameDBMS = p.usernameDBMS;
            passwordDBMS = p.passwordDBMS;
            indirizzoIpClient = p.indirizzoIpClient;
            indirizzoIpServerDiLog = p.indirizzoIpServerDiLog;
            portaServerDiLog = p.portaServerDiLog;
            numGiorniDaMostrare = p.numGiorniDaMostrare;
        }
    }
    
    private XStream creaXStream() {             
        XStream xs = new XStream();
        xs.useAttributeFor(ParametriConfigurazioneXML.class, "indirizzoIpClient");  // (03)
        xs.useAttributeFor(ParametriConfigurazioneXML.class, "indirizzoIpServerDBMS");
        xs.useAttributeFor(ParametriConfigurazioneXML.class, "portaServerDBMS");
        xs.useAttributeFor(ParametriConfigurazioneXML.class, "indirizzoIpServerDiLog");
        xs.useAttributeFor(ParametriConfigurazioneXML.class, "portaServerDiLog"); 
        return xs;
    }
    
    public int getMaxCaratteriMessaggio() { return this.maxCaratteriMessaggio; }
    public String getFont() { return this.font; }
    public double getFontSize() { return this.fontSize; }
    public String getColoreTema() { return this.coloreTema; }
    public String getIndirizzoIpServerDBMS() { return this.indirizzoIpServerDBMS; }
    public int getPortaServerDBMS() { return this.portaServerDBMS; }
    public String getUsernameDBMS() { return this.usernameDBMS; }
    public String getPasswordDBMS() { return this.passwordDBMS; }
    public String getIndirizzoIpClient() { return this.indirizzoIpClient; }
    public String getIndirizzoIpServerDiLog() { return this.indirizzoIpServerDiLog; }
    public int getPortaServerDiLog() { return this.portaServerDiLog; }
    public int getNumUtentiDaMostrare() { return this.numUtentiDaMostrare; }
    public int getNumGiorniDaMostrare() { return this.numGiorniDaMostrare; }
}

/* Note:
    (00) 
        Classe dedicata al prelievo da file XML dei parametri di configurazione.
    (01) 
        Inizializzazione di default dei parametri di configurazione se il file xml non 
        esiste oppure non è stato validato.
    (02)
        Deserializza da formato XML a formato Java.
    (03)
        In base alle regole di progettazione XML, viene classificato come attributo in 
        quanto stringa semplice e non cambia frequentemente.
*/