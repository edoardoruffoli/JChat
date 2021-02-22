import javax.xml.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.*;
import javax.xml.validation.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

public class ValidazioneXML {   // (00)

    public static boolean valida(String xml, String fileSchema) {
        try {  
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); // (01)
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); // (02)
            Document d = db.parse(new InputSource(new StringReader(xml)));  // (03)

            Schema s = sf.newSchema(new StreamSource(new File(((fileSchema.compareTo("log.xsd") == 0) ? "..\\..\\" : "") + fileSchema))); // (04)
            s.newValidator().validate(new DOMSource(d));
        } catch (Exception e) {
            if (e instanceof SAXException) 
                System.out.println("Errore di validazione: " + e.getMessage());
            else
                System.out.println(e.getMessage());  
            return false; 
        }
        return true;
    }
}

/*
    Note:
    (00)
        Classe che si occupa della validazione di stringhe XML.
    (01)
        DocumentBuilderFactory istanzia dei parser che producono oggetti DOM da documenti XML
    (02)
        SchemaFactory legge rappresentazioni esterne di schemi, per la validazione.
    (03)
        DocumentBuilder permette il parsing da un InputSource: che rappresenta una sorgente
        di input XML ottenibile da un flusso di byte o una stringa.
    (04)
        La classe ServerLog viene mandata in esecuzione da JChat/build/classes.
*/