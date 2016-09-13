/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.Properties;

/**
 *
 * @author juan
 */
public class BTR extends JComponent {

    int inicioCascadaX = 75;
    int inicioCascadaY = 130;
    int sizeCanalX = 0;
    int sizeCanalY;
    int xi, yi, c;
    String infor;
    int ml[];
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

        window.setAlwaysOnTop(true);
        window.setFocusable(true);
        Properties prop = new Properties();
        InputStream input = null;
        int posicionX = 0;
        int posicionY = 0;
        try {
            input = new FileInputStream("config.properties");
            //load a properties file
            prop.load(input);
            //get the propperty value and print it out
            posicionX = Integer.parseInt(prop.getProperty("posicionX"));
            posicionY = Integer.parseInt(prop.getProperty("posicionY"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        window.setLocation(posicionX, posicionY);
        comInterfaz c = new comInterfaz();
        c.run(window);
        //comSPPsend s = new comSPPsend();
        //s.run();
    }

    @Override
    public Dimension getPreferredSize() {
        int dimensionX = 100;
        int dimensionY = 100;
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            //load a properties file
            prop.load(input);
            //get the propperty value and print it out
            dimensionX = Integer.parseInt(prop.getProperty("dimensionX"));
            dimensionY = Integer.parseInt(prop.getProperty("dimensionY"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Dimension(dimensionX, dimensionY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        gn++;
        System.out.println("paint component ciclo numero: " + gn);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);

        sizeCanalX = (getSize().width - inicioCascadaX - 11) / 11;
        sizeCanalY = ((getSize().height) - inicioCascadaY) / 100;
        //despliegue d = new despliegue();
        desp(g, sizeCanalX, sizeCanalY);

    }

    public void desp(Graphics g, int limX, int limY) {
        archivo a = new archivo();
        String DIR = "resource/btrData.txt";   //variable estatica que guarda el nombre del archivo donde se guardara la informacion recivida para desplegarse
        int n = 0;  //variable de control int que guarda el numero del color a desplegar
        yi = inicioCascadaY;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        xi = inicioCascadaX;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        String box = ""; //variable que guarda de char en char hasta llegar al tope asignado para proceder a convertirlo a int
        int[] topLine = new int[11];
        String topWord = "";
        boolean bTopLine = true;
        boolean bTopWord = true;
        c = 0;
        int t = 0;

        g.setColor(Color.WHITE);
        g.drawLine(inicioCascadaX - 5, 1, inicioCascadaX - 5, inicioCascadaY-30);
        g.drawLine(inicioCascadaX - 5, inicioCascadaY-30, getSize().width, inicioCascadaY-30);
        g.drawLine(inicioCascadaX - 5, inicioCascadaY, inicioCascadaX - 5, getSize().height - 20);
        //g.drawLine(inicioCascadaX - 5, getSize().height - 20, getSize().width, getSize().height - 20);
        //g.drawString("t/seg", 5, 100);
        for (int i = 0; i < 7; i++) {
            g.drawLine(inicioCascadaX + (((getSize().width - inicioCascadaX) / 6) * i), inicioCascadaY - 30, inicioCascadaX + (((getSize().width - inicioCascadaX) / 6) * i), inicioCascadaY - 25);
            if (i != 6) {
                g.drawString((i * 30) + "°", inicioCascadaX + (((getSize().width - inicioCascadaX) / 6) * i), inicioCascadaY - 10);
            } else {
                g.drawString((i * 30) + "°", getSize().width - 30, inicioCascadaY - 10);
            }
        }

        info = a.leerTxtLine(DIR, 100);
        char[] charArray = info.toCharArray();
        for (char temp : charArray) {
            if (!(temp == ',') && !(temp == ';')) {
                if (bTopWord) {
//                    topWord += temp;
                } else {
                    box += "" + temp;
                }
            } else if (temp == ',') {
                if (bTopWord) {
                    bTopWord = false;
                } else {
                    n = Integer.parseInt(box);
                    if (bTopLine) {
                        topLine[c] = n;
                    }
                    if (n >= 0 && n <= 255) {
                        g.setColor(new Color(0, n, 0));
                        g.fillRect(xi, yi, limX, limY);
                        xi += limX + 1;
                        box = "";
                        c++;
                    } else {
                        System.out.println("Error #??: el valor a desplegar esta fuera de rango");
                    }
                }
            } else if (temp == ';') {
                n = Integer.parseInt(box);
                if (bTopLine) {
                    topLine[c] = n;
                }
                if (n >= 0 && n <= 255) {
                    g.setColor(new Color(0, n, 0));
                    g.fillRect(xi, yi, limX, limY);
                    box = "";
                    bTopLine = false;
                }
                xi = inicioCascadaX;
                yi += limY;
                t++;
                if ((t % 10) == 0) {
                    g.setColor(Color.WHITE);
                    g.drawLine(inicioCascadaX - 10, yi, inicioCascadaX - 05, yi);
                    g.drawString(topWord + "", 5, yi + 3);
                }
                bTopWord = true;
                topWord = "";
            } else {
                System.out.println("Error #??: el valor a desplegar no se reconoce");
            }
        }
        xi = (limX / 2) + inicioCascadaX;
        //yi = inicioCascadaY;
        g.setColor(new Color(0, 150, 0));
        for (int i = 0; i < 10; i++) {
            g.drawLine(xi, 95 - (topLine[i] * 90 / 255), xi + limX, 95 - (topLine[i + 1] * 90 / 255));
            xi += limX;
        }
    }
}
