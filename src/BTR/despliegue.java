/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author juan
 */
class despliegue {

    int xi, yi, c;
    int inc = 255 / 11;
    int ml[];
    String ch = "";
    String info;

    public void run(Graphics g, int limX, int limY) {
        System.out.println("estoy en el RUN del despliegue");
        archivo a = new archivo();

        String DIR = "resource/dataEj.txt";   //variable estatica que guarda el nombre del archivo donde se guardara la informacion recivida para desplegarse
        int n = 0;  //variable de control int que guarda el numero del color a desplegar
        yi = 100;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        xi = 50;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        String box = ""; //variable que guarda de char en char hasta llegar al tope asignado para proceder a convertirlo a int
        int[] topLine = new int[11];
        boolean bTopLine = true;
        c = 0;
        
        /*g.setColor(Color.GREEN);
        g.drawLine(30, 100, 30, 500);
        g.drawLine(30, 100, limX*120, limY*120);*/

        info = a.leerTxtLine(DIR);
        char[] charArray = info.toCharArray();
        for (char temp : charArray) {
            if (!(temp == ',') && !(temp == ';')) {
                box += "" + temp;
            } else if (temp == ',') {
                n = Integer.parseInt(box);
                if (bTopLine) {
                    topLine[c] = n;
                }
                if (n > 0 && n < 255) {
                    g.setColor(new Color(0, n, 0));
                    g.fillRect(xi, yi, limX, limY);
                    xi += limX + 1;
                    box = "";
                    c++;
                } else {
                    System.out.println("Error #??: el valor a desplegar esta fuera de rango");
                }
            } else if (temp == ';') {
                n = Integer.parseInt(box);
                if (bTopLine) {
                    topLine[c] = n;
                }
                if (n > 0 && n < 255) {
                    g.setColor(new Color(0, n, 0));
                    g.fillRect(xi, yi, limX, limY);
                    box = "";
                    bTopLine = false;
                }
                xi = 50;  
                yi += limY + 1;
            } else {
                System.out.println("Error #??: el valor a desplegar no se reconoce");
            }
        }
        xi = limX/2;
        yi = 95;
        g.setColor(new Color(0, 150, 0));
        for (int i = 0; i < 10; i++) {
            g.drawLine(xi, 95-(topLine[i]*90/255), xi+limX, 95-(topLine[i+1]*90/255));
            xi += limX;
        }
    }
}
