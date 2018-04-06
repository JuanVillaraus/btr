/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.*;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author siviso
 */
public class despliegue extends JComponent {

    int inicioCascadaX = 75;
    int inicioCascadaY = 130;
    int sizeCanalX = 0;
    int sizeCanalY;
    int xi, yi, c;
    String infor;
    int gn = 0;
    String info;
    String modelo;
    int longBTR;
    int tiempoOper;
    int tiempoLocal;
    int fX, fY;
    private boolean bMarcacion;
    private int[] iActual;
    private int[][] waterfall;
    private String[] time;
    private int colorUp = 255;
    private int colorDw = 0;
    private int UmbralUp = 2;
    private int UmbralDw = 1;
    int marcacionF = 0;
    archivo a = new archivo();
    int act = 0;

    public despliegue(JFrame window) {
        //this.addMouseListener((MouseListener) window);
        window.add(this);
        //window.pack();
        /*window.setUndecorated(true);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setAlwaysOnTop(true);*/

        //window.setFocusable(true);
        Properties prop = new Properties();
        InputStream input = null;

        int posicionX = 0;
        int posicionY = 0;
        int dimensionX = 0;
        int dimensionY = 0;

        tiempoOper = 100;
        tiempoLocal = 0;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            posicionX = Integer.parseInt(prop.getProperty("posicionX"));
            posicionY = Integer.parseInt(prop.getProperty("posicionY"));
            dimensionX = Integer.parseInt(prop.getProperty("dimensionX"));
            dimensionY = Integer.parseInt(prop.getProperty("dimensionY"));
            longBTR = Integer.parseInt(prop.getProperty("longBTR"));
            modelo = prop.getProperty("modelo");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        window.setSize(dimensionX, dimensionY);
        window.setLocation(posicionX, posicionY);
        waterfall = new int[100][longBTR + 1];
        for (int x = 0; x < 100; x++) {                                         //inicializa el waterfall en cero
            for (int y = 0; y < longBTR + 1; y++) {
                waterfall[x][y] = 0;
            }
        }
        setWaterfall(waterfall);
        iActual = new int[longBTR];
        for (int i = 0; i < longBTR; i++) {
            iActual[i] = 0;
        }
        setIActual(iActual);
        time = new String[100];
        for (int i = 0; i < 100; i++) {
            time[i] = "";
        }
        setTime(time);
    }

    @Override
    public Dimension getPreferredSize() {
        int dimensionX = 100;
        int dimensionY = 100;
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            dimensionX = Integer.parseInt(prop.getProperty("dimensionX"));
            dimensionY = Integer.parseInt(prop.getProperty("dimensionY"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return new Dimension(dimensionX, dimensionY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        gn++;
        //System.out.println("paint component ciclo numero: " + gn);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);

        sizeCanalX = (getSize().width - inicioCascadaX) / longBTR;
        sizeCanalY = ((getSize().height) - inicioCascadaY) / 100;
        desp(g, sizeCanalX, sizeCanalY);

    }

    public void setWaterfall(int[][] waterfall) {
        this.waterfall = waterfall;
    }

    public void setIActual(int[] iActual) {
        this.iActual = iActual;
    }

    public void setTime(String[] time) {
        this.time = time;
    }

    public void setBMarcacion(boolean bMarcacion) {
        this.bMarcacion = bMarcacion;
        repaint();
    }

    public void setColorUp(int colorUp) {
        this.colorUp = colorUp;
        repaint();
    }

    public void setColorDw(int colorDw) {
        this.colorDw = colorDw;
        repaint();
    }

    public void setUmbralUp(int umbralUp) {
        this.UmbralUp = umbralUp;
        repaint();
    }

    public void setUmbralDw(int umbralDw) {
        this.UmbralDw = umbralDw;
        repaint();
    }

    public int[][] getWaterfall() {
        return this.waterfall;
    }

    public int[] getIActual() {
        return this.iActual;
    }

    public String[] getTime() {
        return this.time;
    }

    public boolean getBMarcacion() {
        return this.bMarcacion;
    }

    public int getColorUp() {
        return this.colorUp;
    }

    public int getColorDw() {
        return this.colorDw;
    }

    public int getUmbralUp() {
        return this.UmbralUp;
    }

    public int getUmbralDw() {
        return this.UmbralDw;
    }

    public void desp(Graphics g, int limX, int limY) {
        archivo a = new archivo();
        String DIR = "resource/btrData.txt";   //variable estatica que guarda el nombre del archivo donde se guardara la informacion recivida para desplegarse
        yi = inicioCascadaY;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        xi = inicioCascadaX;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        c = 0;
        int t = 0;
        int[][] waterfall = getWaterfall();
        int[] iActual = getIActual();
        String[] time = getTime();

        int umbralUp = getUmbralUp();
        int umbralDw = getUmbralDw();
        int colorUp = getColorUp();  //Integer.parseInt(a.leerTxtLine("resource/colorUp.txt"));
        int colorDw = getColorDw();  //Integer.parseInt(a.leerTxtLine("resource/colorDw.txt"));
        int marcacion = Integer.parseInt(a.leerTxtLine("resource/marcacion.txt"));
        int angBTR = Integer.parseInt(a.leerTxtLine("resource/angBTR.txt"));

        g.setColor(Color.WHITE);
        g.drawLine(inicioCascadaX - 5, 1, inicioCascadaX - 5, inicioCascadaY - 30);
        g.drawLine(inicioCascadaX - 5, inicioCascadaY - 30, getSize().width, inicioCascadaY - 30);
        g.drawLine(inicioCascadaX - 5, inicioCascadaY, inicioCascadaX - 5, getSize().height - 20);

        //System.out.println(modelo);
        //System.out.println("SSPP".equals(modelo));
        if ("SSF".equals(modelo) || "SSPV".equals(modelo)) {
            for (int i = 0; i < longBTR - 1; i++) {
                g.drawLine(inicioCascadaX + (((limX * longBTR) / 6) * i), inicioCascadaY - 30, inicioCascadaX + (((limX * longBTR) / 6) * i), inicioCascadaY - 25);
                if (i != longBTR - 1) {
                    g.drawString((i * 60) + "°", inicioCascadaX + (((limX * longBTR) / 6) * i) - 10, inicioCascadaY - 10);
                } else {
                    g.drawString((i * 60) + "°", inicioCascadaX + (((limX * longBTR) / 6) * i) - 23, inicioCascadaY - 10);
                }
            }
        } else if ("SSPP".equals(modelo)) {
            for (int i = 0; i < longBTR - 1; i++) {
                g.drawLine(inicioCascadaX + (((limX * longBTR) / 6) * i), inicioCascadaY - 30, inicioCascadaX + (((limX * longBTR) / 6) * i), inicioCascadaY - 25);
                if (i != longBTR - 1) {
                    g.drawString((i * 30) + "°", inicioCascadaX + (((limX * longBTR) / 6) * i) - 10, inicioCascadaY - 10);
                } else {
                    g.drawString((i * 30) + "°", inicioCascadaX + (((limX * longBTR) / 6) * i) - 23, inicioCascadaY - 10);
                }
            }
        }
        //new-------------------------------------------------------------------------------------------------------------------
        g.setColor(new Color(0, 150, 0));
        xi += limX / 2;
        for (int i = 0; i < longBTR - 1; i++) {
            g.drawLine(xi, 95 - (iActual[i] * 90 / 255), xi + limX, 95 - (iActual[i + 1] * 90 / 255));
            xi += limX;
        }
        System.out.println("wf: "+waterfall[1][0]+"\tUmbral: "+umbralUp);

        for (int x = 0; x < waterfall.length; x++) {
            xi = inicioCascadaX;
            //if (waterfall[x][0] == 0) {
                for (int y = 1; y < waterfall[x].length; y++) {
                    if (waterfall[x][y] >= 0 && waterfall[x][y] <= 255) {
                        if (waterfall[x][y] < colorDw) {
                            g.setColor(Color.BLACK);
                            System.out.print("M");
                        } else if (waterfall[x][y] > colorUp) {
                            System.out.print("N");
                            if (waterfall[x][0] > umbralUp || waterfall[x][0] == 0) {
                                g.setColor(Color.GREEN);
                            } else if (waterfall[x][0] > umbralDw) {
                                g.setColor(Color.RED);
                            } else if (waterfall[x][0] <= umbralDw) {
                                g.setColor(Color.BLUE);
                            }
                        } else if (waterfall[x][0] > umbralUp || waterfall[x][0] == 0) { 
                            g.setColor(new Color(0, (waterfall[x][y] - colorDw) * 255 / (colorUp - colorDw), 0));
                        } else if (waterfall[x][0] > umbralDw) {
                            g.setColor(new Color((waterfall[x][y] - colorDw) * 255 / (colorUp - colorDw), 0, 0));
                            System.out.print("R");
                        } else if (waterfall[x][0] <= umbralDw) {
                            g.setColor(new Color(0, 0, (waterfall[x][y] - colorDw) * 255 / (colorUp - colorDw)));
                            System.out.print("B");
                        }
                        g.fillRect(xi, yi, limX, limY);
                        if ((x % 10) == 0) {
                            g.setColor(Color.WHITE);
                            g.drawLine(inicioCascadaX - 10, yi, inicioCascadaX - 05, yi);
                            g.drawString(time[x], 5, yi + 3);
                        }
                        xi += limX;
                    } else {
                        System.out.println("Error #??: el valor a desplegar esta fuera de rango");
                    }
                }
                //System.out.println("");
            //}
            yi += limY;
        }
        if (getBMarcacion()) {
            fX = (((getSize().width - 5 - inicioCascadaX) / 36) * (marcacion / 10)) + inicioCascadaX;
            System.out.println(fX);
            fY = inicioCascadaY - 3;
            marcacion(g, fX, fY);
        }
        if (marcacionF != 0) {
            g.setColor(Color.YELLOW);
            g.drawString("M " + marcacionF + "°", 10, inicioCascadaY - 40);
            g.fillOval(inicioCascadaX + (((limX * longBTR) * marcacionF) / 360) - (limX / 2), inicioCascadaY - 40, 10, 10);
        }
        if ("SSPV".equals(modelo)) {
            if (angBTR >= 0 && angBTR <= 360) {
                g.setColor(new Color(230, 95, 0));
                g.drawLine(((angBTR * limX * longBTR) / 360) + inicioCascadaX, 5, ((angBTR * limX * longBTR) / 360) + inicioCascadaX, 100);
                g.drawString("M " + angBTR + "°", 10, inicioCascadaY - 60);
            }
        }
        //new-------------------------------------------------------------------------------------------------------------------
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        float[] dash = {5};
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f));
        g2d.drawLine(inicioCascadaX - 5, 95 - (colorUp * 90 / 255), getSize().width, 95 - (colorUp * 90 / 255));
        g2d.drawLine(inicioCascadaX - 5, 95 - (colorDw * 90 / 255), getSize().width, 95 - (colorDw * 90 / 255));
    }

    public void setInfo(String infoActual, String hora) {
        //System.out.println("setInfo");
        info = "";
        act++;
        int[] infoActualNum = new int[longBTR + 1];
        int[][] waterfall;
        String[] time;
        for (int x = 0; x < infoActualNum.length; x++) {
            infoActualNum[x] = 0;
        }
        int n = 0;
        infoActualNum[n] = 0;
        n++;
        boolean bMarcacionF = false;
        if ("SSF".equals(modelo)) {
            bMarcacionF = true;
        }
        char[] charArray = infoActual.toCharArray();
        for (char temp : charArray) {
            if (temp == '$') {
                System.out.println("Biestatico");
                n--;
            } else if (!(temp == ',') && !(temp == ';')) {
                info += temp;
            } else {
                try {
                    infoActualNum[n] = (int) (Double.parseDouble(info)); //Integer.parseInt(info);
                    if (bMarcacionF && n != 0) {
                        marcacionF = Integer.parseInt(info);
                        n--;
                        bMarcacionF = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error catch del setInfo: " + e.getMessage());
                }
                info = "";
                n++;
            }
        }
        System.out.print(getUmbralUp());
        for (int i = 0; i < infoActualNum.length; i++) {
            System.out.print(" " + infoActualNum[i]);
        }
        System.out.println("");
        System.out.println("");
        setIActual(infoActualNum);
        tiempoLocal++;
        if ("SSF".equals(modelo) || act == 100) {
            waterfall = getWaterfall();
            for (int x = waterfall.length - 1; x > 0; x--) {
                waterfall[x] = waterfall[x - 1];
            }
            waterfall[0] = infoActualNum;
            setWaterfall(waterfall);
            time = getTime();
            for (int x = time.length - 1; x > 0; x--) {
                time[x] = time[x - 1];
            }
            time[0] = hora;
            setTime(time);
            act = 0;
        }
        //}
        repaint();
    }

    public void setInfo(int[] infoActual, String hora) {
        int[][] waterfall = getWaterfall();
        String[] time = getTime();
        setIActual(infoActual);
        for (int x = waterfall.length - 1; x > 0; x--) {
            waterfall[x] = waterfall[x - 1];
        }
        waterfall[0] = infoActual;
        setWaterfall(waterfall);
        for (int x = time.length - 1; x > 0; x--) {
            time[x] = time[x - 1];
        }
        time[0] = hora;
        setTime(time);
    }

    public void marcacion(Graphics g, int fX, int fY) {
        g.setColor(Color.GRAY);
        double ang, angSep, tx, ty;
        int dist = 0;
        Point punto1 = null, punto2 = null;
        punto1 = new Point(fX, fY);
        punto2 = new Point(fX, fY - 30);
        dist = 7;
        ty = -(punto1.y - punto2.y) * 1.0;
        tx = (punto1.x - punto2.x) * 1.0;
        ang = Math.atan(ty / tx);
        if (tx < 0) {
            ang += Math.PI;
        }

        Point p1 = new Point(), p2 = new Point(), punto = punto2;
        angSep = 25.0;

        p1.x = (int) (punto.x + dist * Math.cos(ang - Math.toRadians(angSep)));
        p1.y = (int) (punto.y - dist * Math.sin(ang - Math.toRadians(angSep)));
        p2.x = (int) (punto.x + dist * Math.cos(ang + Math.toRadians(angSep)));
        p2.y = (int) (punto.y - dist * Math.sin(ang + Math.toRadians(angSep)));

        Graphics2D g2D = (Graphics2D) g;
        //g.setColor(Color.BLUE);
        g2D.setStroke(new BasicStroke(3.7f));
        g.drawLine(punto1.x, punto1.y, punto2.x, punto2.y);
        g.drawLine(p1.x, p1.y, punto.x, punto.y);
        g.drawLine(p2.x, p2.y, punto.x, punto.y);

    }

    public void save() {
        String s = "";
        for (int x = 0; x < waterfall.length; x++) {
            for (int y = 0; y < waterfall[0].length; y++) {
                if (y == 0) {
                    s += time[y] + ",";
                }
                s += waterfall[x][y];
                if (y < waterfall[0].length - 1) {
                    s += ",";
                }
            }
            s += ";";
            if (x < waterfall.length - 1) {
                s += "\n";
            }
        }
        a.save("resource/BTR", s);
    }

}
