/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corvex;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
/**
 *
 * @author nihil
 */
public class Corvex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        /*JFrame ventana=new JFrame();
        ventana.setResizable(true);
        ventana.setPreferredSize(new Dimension(850, 600));
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);*/
        Ventana vent=new Ventana();
        vent.setResizable(true);
        vent.setVisible(true);
        vent.setLocationRelativeTo(null);
        /*SwingUtilities.invokeLater(() -> {
            ventana.setVisible(true);
        });*/
    }
    
}
