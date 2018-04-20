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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import static java.time.temporal.TemporalQueries.localTime;
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
    private int[][] target = new int[2][3];//el primer corchete representa el numero de target. El segundo corchete representa 0.-poaición en X, 1.-posición en Y.
    private int nTarget = 0; //este dato representa el ultimo target que recibio mediante clic
    private int incTargetY = 0;
    int marcacionF = 0;
    archivo a = new archivo();
    int act = 0;
    int nBiestatico;
    boolean bBiestatico;
    private char modo;
    private int paso;
    private int puertoPPI = 0;
    InputStream input = null;
    DatagramSocket socket;
    byte[] mensaje_bytes = new byte[256];
    String mensaje = "";
    DatagramPacket paquete;
    int distTarget = 0;

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
        setModo('M');

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
        iActual = new int[longBTR + 1];
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

    public void setTimeActual(String hora) {
        String[] time = getTime();
        for (int x = time.length - 1; x > 0; x--) {
            time[x] = time[x - 1];
        }
        time[0] = hora;
        setTime(time);
    }

    public void setModo(char modo) {
        this.modo = modo;
    }

    public void setTarget(int[][] target) {
        this.target = target;
        /*for (int x = 0; x < target.length; x++) {
            System.out.println("SET ang: " + target[x][0] + "\tX: " + target[x][1] + "\tY: " + target[x][2]);
        }*/
    }

    public void setNTarget(int nTarget) {
        this.nTarget = nTarget;
    }

    public void setIncTargetY(int incTargetY) {
        this.incTargetY = incTargetY;
    }

    public void setPaso(int paso) {
        this.paso = paso;
        repaint();
    }

    public void setDistTarget(int distTarget) {
        this.distTarget = distTarget;
    }

    public void setPuertoPPI(int puertoPPI) {
        this.puertoPPI = puertoPPI;
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

    public char getModo() {
        return this.modo;
    }

    public int[][] getTarget() {
        return this.target;
    }

    public int getNTarget() {
        return this.nTarget;
    }

    public int getIncTargetY() {
        return this.incTargetY;
    }

    public int getPaso() {
        return this.paso;
    }

    public int getPuertoPPI() {
        return this.puertoPPI;
    }

    public int distTarget() {
        return this.distTarget;
    }

    public void desp(Graphics g, int limX, int limY) {
        setIncTargetY(limY);
        String DIR = "resource/btrData.txt";   //variable estatica que guarda el nombre del archivo donde se guardara la informacion recivida para desplegarse
        yi = inicioCascadaY;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        xi = inicioCascadaX;     //variable de control grafico en Y que guarda la acumulacion del incremento para la graficacion
        c = 0;
        int t = 0;
        int[][] waterfall = getWaterfall();
        int[] iActual = getIActual();
        String[] time = getTime();
        int[][] target = getTarget();
        int nTarget = getNTarget();
        //boolean newTarget = true;

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
        //xi += limX / 2;
        //int limXActual = (getSize().width - inicioCascadaX) / (longBTR - 1);
        int i = 0;
        if ("SSF".equals(modelo)) {
            i++;
        }
        for (; i < longBTR; i++) {
            g.drawLine(xi, 95 - (iActual[i] * 90 / 255), xi + limX, 95 - (iActual[i + 1] * 90 / 255));
            xi += limX;
            //xi += limXActual;
            if (i == longBTR - 1) {
                g.drawLine(xi, 95 - (iActual[i + 1] * 90 / 255), xi + limX, 95 - (iActual[1] * 90 / 255));
            }
        }
        //System.out.println("wf: " + waterfall[1][0] + "\tUmbral: " + umbralUp);

        Color angCero = Color.BLACK;
        for (int x = 0; x < waterfall.length; x++) {
            xi = inicioCascadaX;
            //if (waterfall[x][0] == 0) {
            for (int y = 1; y < waterfall[x].length; y++) {
                if ((waterfall[x][y] >= 0 && waterfall[x][y] <= 255) || waterfall[x][y] == 300) {
                    if (waterfall[x][y] == 300) {
                        g.setColor(new Color(237, 118, 014));
                    } else if (waterfall[x][y] < colorDw) {
                        g.setColor(Color.BLACK);
                        //System.out.print("M");
                    } else if (waterfall[x][y] > colorUp) {
                        //System.out.print("N");
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
                        //System.out.print("R");
                    } else if (waterfall[x][0] <= umbralDw) {
                        g.setColor(new Color(0, 0, (waterfall[x][y] - colorDw) * 255 / (colorUp - colorDw)));
                        //System.out.print("B");
                    }
                    if (y == 1) {
                        g.fillRect(xi, yi, limX / 2, limY);
                        xi += limX / 2;
                        angCero = g.getColor();
                    } else {
                        g.fillRect(xi, yi, limX, limY);
                        xi += limX;
                    }
                    if (y == waterfall[x].length - 1) {
                        g.setColor(angCero);
                        g.fillRect(xi, yi, limX / 2, limY);
                    }
                    if ((x % 10) == 0) {
                        g.setColor(Color.WHITE);
                        g.drawLine(inicioCascadaX - 10, yi, inicioCascadaX - 05, yi);
                        g.drawString(time[x], 5, yi + 3);
                    }
                } else {
                    System.out.println("Error #??: el valor a desplegar esta fuera de rango " + waterfall[x][y] + " X:" + x + " Y:" + y);
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
            g.fillOval(inicioCascadaX + (((limX * longBTR) * marcacionF) / 360) - limX, inicioCascadaY - 40, 10, 10);
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

        if ("SSF".equals(modelo)) {
            g.setColor(new Color(230, 95, 0));
            if (target[0][1] != 0 && target[0][2] != 0) {
                g.drawLine(target[0][1], 100, target[0][1], target[0][2]);
                g.drawLine(inicioCascadaX, target[0][2], target[0][1], target[0][2]);
                g.fillOval(target[0][1] - 5, target[0][2] - 5, 10, 10);
                g.drawString("T " + target[0][0] + "°", 10, inicioCascadaY - 100);
            }
            if (target[1][1] != 0 && target[1][2] != 0) {
                g.drawLine(target[1][1], 100, target[1][1], target[1][2]);
                g.drawLine(inicioCascadaX, target[1][2], target[1][1], target[1][2]);
                g.fillOval(target[1][1] - 5, target[1][2] - 5, 10, 10);
                g.drawString("T " + target[1][0] + "°", 10, inicioCascadaY - 80);
            }
            g.setFont(new Font("Calibri", 0, 8));
            g.setColor(Color.WHITE);
            if (target[0][1] != 0 && target[0][2] != 0) {
                g.drawString("1", target[0][1] - 2, target[0][2] + 3);
            }
            if (target[1][1] != 0 && target[1][2] != 0) {
                g.drawString("2", target[1][1] - 3, target[1][2] + 3);
            }
            int coorX1 = (target[0][1] - inicioCascadaX) / limX;
            int coorY1 = (target[0][2] - inicioCascadaY) / limY;
            System.out.println("\tcoor1: " + coorX1 + ", " + coorY1);
            if (coorX1 >= 0 && coorY1 >= 0) {
                System.out.println("\tWF1: " + waterfall[coorY1][coorX1]);
                System.out.println("\tT1 : " + time[coorY1]);
            }
            int coorX2 = (target[1][1] - inicioCascadaX) / limX;
            int coorY2 = (target[1][2] - inicioCascadaY) / limY;
            System.out.println("\tcoor2: " + coorX2 + ", " + coorY2);
            if (coorX2 >= 0 && coorY2 >= 0) {
                System.out.println("\tWF2: " + waterfall[coorY2][coorX2]);
                System.out.println("\tT2 : " + time[coorY2]);
            }
            if (coorY1 >= 0 && coorY2 >= 0 && !"".equals(time[coorY1]) && !"".equals(time[coorY2])) {
                String time1 = time[coorY1] + ':';
                String time2 = time[coorY2] + ':';
                String word = "";
                /*int n = 0;
                int[] hr = {0, 0, 0};
                int[] mn = {0, 0, 0};
                int[] sg = {0, 0, 0};
                for (int a = 1; a < hr.length; a++) {
                    char[] charArray = time1.toCharArray();
                    if (a == 2) {
                        charArray = time2.toCharArray();
                    }
                    for (char temp : charArray) {
                        if (!(temp == ':')) {
                            word += temp;
                        } else if ("" != word) {
                            switch (n) {
                                case 0:
                                    hr[a] = Integer.parseInt(word);
                                    word = "";
                                    n++;
                                    break;
                                case 1:
                                    mn[a] = Integer.parseInt(word);
                                    word = "";
                                    n++;
                                    break;
                                case 2:
                                    sg[a] = Integer.parseInt(word);
                                    word = "";
                                    n = 0;
                                    break;
                            }
                        }
                    }
                }
                int[] rst = {0, 0, 0};
                rst[1] = hr[1] * 60 * 60 + mn[1] * 60 + sg[1];
                rst[2] = hr[2] * 60 * 60 + mn[2] * 60 + sg[2];
                if (rst[1] < rst[2]) {
                    hr[0] = hr[2] - hr[1];
                    mn[0] = mn[2] - mn[1];
                    sg[0] = sg[2] - sg[1];
                    System.out.println("sg " + sg[0] + " = " + sg[2] + " - " + sg[1]);
                    System.out.println("mn " + mn[0] + " = " + mn[2] + " - " + mn[1]);
                    System.out.println("hr " + hr[0] + " = " + hr[2] + " - " + hr[1]);
                    rst[0] = rst[2] - rst[1];
                } else {
                    hr[0] = hr[1] - hr[2];
                    mn[0] = mn[1] - mn[2];
                    sg[0] = sg[1] - sg[2];
                    System.out.println("sg " + sg[0] + " = " + sg[1] + " - " + sg[2]);
                    System.out.println("mn " + mn[0] + " = " + mn[1] + " - " + mn[2]);
                    System.out.println("hr " + hr[0] + " = " + hr[1] + " - " + hr[2]);
                    rst[0] = rst[1] - rst[2];
                }

                if (sg[0] < 0) {
                    mn[0] -= 1;
                    sg[0] += 60;
                }
                if (mn[0] < 0) {
                    hr[0] -= 1;
                    mn[0] += 60;
                }
                System.out.println("dif: " + hr[0] + ":" + mn[0] + ":" + sg[0]);
                System.out.println("dist: " + rst[0] + " = " + rst[1] + " - " + rst[2]);*/
                int n = 0;
                if (coorY2 > coorY1) {
                    n = coorY2 - coorY1;
                } else {
                    n = coorY1 - coorY2;
                }
                System.out.println(n + " = " + coorY1 + " - " + coorY2);
                setDistTarget(n);
                /*try {
                    mensaje = 'D' + Integer.toString(rst[0]) + ';';
                    mensaje_bytes = mensaje.getBytes();
                    paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), InetAddress.getByName("localhost"), 5002);
                    socket.send(paquete);
                    //System.out.println("envio PPI: "+mensaje+" - " +getPuertoPPI());
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
                }*/
            }
        }
        /*for (int x = 0; x < target.length; x++) {
            System.out.println("DES ang: " + target[x][0] + "\tX: " + target[x][1] + "\tY: " + target[x][2]);
        }
        System.out.println("" + newTarget + " " + nTarget);
        System.out.println("");*/
    }

    public void setInfo(String infoActual, String hora, char modoActual) {
        //System.out.println("setInfo");
        info = "";
        act++;
        int[] infoActualNum = new int[longBTR + 1];
        int[][] waterfall = getWaterfall();
        String[] time;
        char modo = getModo();

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
                bBiestatico = true;
            } else if (!(temp == ',') && !(temp == ';')) {
                info += temp;
            } else {
                try {
                    infoActualNum[n] = (int) (Double.parseDouble(info)); //Integer.parseInt(info);
                    if (bBiestatico) {
                        if (infoActualNum[n] == 0) {
                            nBiestatico = 0;
                            bMarcacionF = true;
                        } else {
                            int nB = infoActualNum[n];
                            nBiestatico++;
                            if (nBiestatico > nB) {
                                for (; nBiestatico > nB; nBiestatico++) {
                                    for (int x = 0; x < infoActualNum.length; x++) {
                                        infoActualNum[x] = 0;
                                    }
                                    setIActual(infoActualNum);
                                    setTimeActual(hora);
                                }
                            }
                            System.out.println("nB: " + nB);
                        }
                        bBiestatico = false;
                        bMarcacionF = false;
                        n--;
                    }
                    if (bMarcacionF && n != 0) {
                        marcacionF = Integer.parseInt(info);
                        System.out.println("Ori: " + marcacionF);
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
        //System.out.print(getUmbralUp());
        System.out.println("Umbral: " + infoActualNum[0]);
        System.out.println("modo: " + modoActual);
        for (int i = 1; i < infoActualNum.length; i++) {
            System.out.print(" " + infoActualNum[i]);
        }
        System.out.println("");
        System.out.println("");
        setIActual(infoActualNum);
        tiempoLocal++;
        if ("SSF".equals(modelo) || act == 100) {
            for (int x = waterfall.length - 1; x > 0; x--) {
                waterfall[x] = waterfall[x - 1];
            }
            waterfall[0] = infoActualNum;
            setWaterfall(waterfall);
            setTimeActual(hora);
            act = 0;
        }
        //}
        if (modo != modoActual) {
            setModo(modoActual);
            for (int x = 0; x < infoActualNum.length; x++) {
                infoActualNum[x] = 300;
            }
            for (int x = waterfall.length - 1; x > 0; x--) {
                waterfall[x] = waterfall[x - 1];
            }
            waterfall[0] = infoActualNum;
            setWaterfall(waterfall);
            setTimeActual(hora);
        }
        int[][] target = getTarget();
        int incTargetY = getIncTargetY();
        //for (int x = 0; x < target.length; x++) {
        if (target[0][2] != 0) {
            System.out.print("target 0 : " + target[0][2]);
            target[0][2] += incTargetY;
            System.out.println(" a " + target[0][2]);
        }
        if (target[1][2] != 0) {
            System.out.print("target 1 : " + target[1][2]);
            target[1][2] += incTargetY;
            System.out.println(" a " + target[1][2]);
        }
        //}
        setTarget(target);
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
        String[] time = getTime();
        for (int x = 0; x < waterfall.length; x++) {
            for (int y = 0; y < waterfall[0].length; y++) {
                if (y == 0) {
                    s += time[x] + ",";
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

    public void newTarget(char[] charArray) {
        String word = "";
        int n = 0;
        int angBTR = 0;
        int marcBTRX = 0;
        int marcBTRY = 0;
        int[][] target = getTarget();
        boolean newTarget = true;
        //char[] charArray = paquete.toCharArray();
        for (char temp : charArray) {
            if (!(temp == ',') && !(temp == ';') && !(temp == 'T')) {
                word += temp;
            } else if ("" != word) {
                switch (n) {
                    case 0:
                        angBTR = Integer.parseInt(word);
                        word = "";
                        n++;
                        break;
                    case 1:
                        marcBTRX = Integer.parseInt(word);
                        word = "";
                        n++;
                        break;
                    case 2:
                        marcBTRY = Integer.parseInt(word);
                        word = "";
                        n++;
                        break;
                }
            }
        }
        for (int x = 0; x < target.length; x++) {
            if (target[x][1] < marcBTRX + 5 && target[x][1] > marcBTRX - 5 && target[x][2] < marcBTRY + 5 && target[x][2] > marcBTRY - 5) {
                target[x][1] = 0;
                target[x][2] = 0;
                newTarget = false;
                nTarget = x;
                //if (nTarget == -1) {
                //    nTarget = 1;
                //}
            }
        }
        if (newTarget) {
            target[nTarget][0] = angBTR;
            target[nTarget][1] = marcBTRX;
            target[nTarget][2] = marcBTRY;
            nTarget++;
            if (nTarget == 2) {
                nTarget = 0;
            }
        }
        setNTarget(nTarget);
        setTarget(target);

        /*for (int x = 0; x < target.length; x++) {
            System.out.println("DES ang: " + target[x][0] + "\tX: " + target[x][1] + "\tY: " + target[x][2]);
        }
        System.out.println("" + newTarget + " " + nTarget);
        System.out.println("");*/
    }
}
