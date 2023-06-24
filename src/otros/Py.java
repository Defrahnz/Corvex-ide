/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package otros;
import java.io.*;
/**
 *
 * @author nihil
 */
public class Py {
    public static String ejecutarPython(String ruta)throws IOException{
      ProcessBuilder proceso=new ProcessBuilder("python",ruta);
      proceso.redirectErrorStream(true);
      Process p=proceso.start();
      
      //OUT
      InputStream ip=p.getInputStream();
      BufferedReader buffer= new BufferedReader(new InputStreamReader(ip));
      
      StringBuilder out=new StringBuilder();
      String cadena;
      while((cadena=buffer.readLine())!=null){
         out.append(cadena).append("\n");
      }
      
      //Por si truena
      try{
        p.waitFor();
      }catch(InterruptedException e){
          e.printStackTrace();
      }
      buffer.close();
      ip.close();
      return out.toString();
    }
    
}
