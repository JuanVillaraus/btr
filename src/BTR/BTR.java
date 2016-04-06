/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
//import java.net.*;
//import java.io.*;

/**
 *
 * @author juan
 */
public class BTR extends JComponent {

    int sizeCanalX = 0;
    int sizeCanalY;

    int xi, yi, c;
    String infor;
    int ml[];
    //Graphics g;
    int gn = 0;
    
    String ch = "";
    String info;

    public static void main(String[] args) {
        JFrame window = new JFrame("BTR by SIVISO");
        BTR wf = new BTR();
        window.add(wf);
        window.pack();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        //despliegue d = new despliegue();
        //d.run();
        comInterfaz c = new comInterfaz();
        c.run(window);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(680, 550);
    }

    @Override
    protected void paintComponent(Graphics g) {
        gn++;
        System.out.println("paint component ciclo numero: " + gn);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);

        sizeCanalX = (getSize().width - 50) / 11;
        sizeCanalY = ((getSize().height)-140) / 100;
        //despliegue d = new despliegue();
        desp(g, sizeCanalX, sizeCanalY);

    }
    
    
    public void desp(Graphics g, int limX, int limY) {
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
        int t = 0;

        g.setColor(Color.WHITE);
        g.drawLine(45, 100, 45, getSize().height - 20);
        g.drawLine(45, getSize().height - 20, getSize().width, getSize().height - 20);
        g.drawString("t/seg", 5, 100);
        for (int i = 0; i < 7; i++) {
            g.drawLine(45 + (((getSize().width - 45) / 6) * i), getSize().height - 20, 45 + (((getSize().width - 45) / 6) * i), getSize().height - 15);
            if (i != 6) {
                g.drawString((i * 30) + "°", 35 + (((getSize().width - 45) / 6) * i), getSize().height - 1);
            } else {
                g.drawString((i * 30) + "°", getSize().width - 30, getSize().height - 1);
            }
        }

        info = a.leerTxtLine(DIR, 100);
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
                yi += limY;
                t++;
                if ((t % 5) == 0) {
                    g.setColor(Color.WHITE);
                    g.drawLine(35, yi, 45, yi);
                    g.drawString(t + "", 15, yi + 3);
                }
            } else {
                System.out.println("Error #??: el valor a desplegar no se reconoce");
            }
        }
        xi = (limX / 2) + 50;
        yi = 95;
        g.setColor(new Color(0, 150, 0));
        for (int i = 0; i < 10; i++) {
            g.drawLine(xi, 95 - (topLine[i] * 90 / 255), xi + limX, 95 - (topLine[i + 1] * 90 / 255));
            xi += limX;
        }
    }
}
