import java.io.*;
import java.nio.file.*;

public class GestoreFile {  // (00)
    // (01)
    public static String carica(String file) {  // (02)
        try {
            return new String(Files.readAllBytes(Paths.get(file)));     // (03)
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null; // 
    }
    
    public static void salva(Object o, String file) {   // (04)
        try { //02
            Files.write(Paths.get("..\\..\\" + file), o.toString().getBytes(), StandardOpenOption.APPEND);
            
       } catch (IOException e) {
            System.out.println(e.getMessage());
       }
    }
}

/* 
    Note:
    (00)
        
    (01)

    (02)

    (03)
*/