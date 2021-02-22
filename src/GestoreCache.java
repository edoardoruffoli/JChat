import java.io.*;

/**
 *
 * @author Edoardo
 */
public class GestoreCache { // (00)
    
    public final static void salva(InterfacciaMessaggi i, String file) {    // (01)
        try (ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(file))) {
            oout.writeObject(new Cache(i)); // (02)
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public final static Cache carica(String file) { // (03)
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file))) {
            return (Cache)oin.readObject(); // (04)
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
/*
    Note:
    (00) 
        Classe che gestisce la lettura e scrittura dalla cache locale degli input.
    (01)
        Salva nella cache degli input (il file indicato da file) i valori di input 
        dell'interfaccia grafica.
    (02)
        Scrittura dell'oggetto Cache nel file binario.
    (03)
        Carica i valori contenuti nel file binario cache. 
    (04)
        Il metodo restituisce un oggetto Cache, con i valori letti dal file binario.
*/
