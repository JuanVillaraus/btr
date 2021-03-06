/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFrame;

/**
 *
 * @author siviso
 */
public class comSPV extends Thread {

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    Socket socket;
    byte[] mensaje_bytes = new byte[256];
    String mensaje = "";
    String msn;
    String texto;
    String info;
    String save;
    int nDatos;
    boolean error;
    boolean habilitado = false;
    int t = 300;
    winListener window;
    despliegue desp;
    Properties prop = new Properties();
    InputStream input = null;
    String DIR = "";
    int PORT = 0;
    //int n = 0;
    int nInfo = 0;
    int[] infoActual;
    int longBTR;

    public boolean getHabilitado() {
        return this.habilitado;
    }

    public void setHabilitado(boolean h) {
        this.habilitado = h;
    }

    public void setWindow(winListener window) {
        this.window = window;
    }

    public void setMarcacion(boolean b) {
        System.out.println("marcacion desde SPV");
        desp.setBMarcacion(b);
    }

    /*public String getSave() {
        save = "";
        int[][] waterfall = desp.getWaterfall();
        String[] time = desp.getTime();
        for (int x = 0; x < waterfall.length; x++) {
            save += time[x];
            for (int y = 0; y < waterfall[x].length; y++) {
                save += "," + waterfall[x][y];
            }
            save += ";\n";
        }
        return save;
    }*/
    public void run() {
        try {
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                DIR = prop.getProperty("dirSSPV");
                PORT = Integer.parseInt(prop.getProperty("portBTR"));
                longBTR = Integer.parseInt(prop.getProperty("longBTR"));
                System.out.println("BTR comSPV " + DIR + " " + PORT);
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
            desp = new despliegue(this.window);
            desp.addMouseListener(window);
            socket = new Socket(DIR, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            archivo a = new archivo();
            sleep(1000);
            //System.out.println("estoy en el run cpmspv");
            String hora;
            //long time;
            infoActual = new int[longBTR+1];

            while (true) {
                //time = System.currentTimeMillis();
                //System.out.println("estoy en el while true");
                if (getHabilitado()) {
                    //sleep(30);
                    //  mensaje = in.readLine();
                    error = false;
                    texto = "";
                    info = "";
                    nDatos = 0;
                    mensaje = "DatosBTR";
                    out.writeUTF(mensaje);
                    //System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    //System.out.println("Recibí: " + msn);
                    /*if (!("Beamforming OK".equals(msn))) {
                        error = true;
                        System.out.println("Error: esperba <Beamforming OK> y recibí <" + msn + ">, Compruebe la comunicación");
                    }*/
                    if (!error) {
                        mensaje = "BTR1";
                        out.writeUTF(mensaje);
                        //System.out.println("Envie: " + mensaje);
                        msn = "";
                        msn = inp.readLine();
                        //System.out.println("Recibí: " + msn);
                        nInfo = 0;
                        infoActual[nInfo] = 0;
                        nInfo ++;
                        char[] charArray = msn.toCharArray();
                        for (char temp : charArray) {
                            if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0') {
                                info += temp;
                            }
                            if (temp == ',' || temp == ';') {
                                nDatos++;
                                if (info != "") {
                                    try {
                                        infoActual[nInfo] = Integer.parseInt(info);
                                    } catch (Exception e) {
                                        System.err.println("Error: ParseInt: " + e.getMessage());
                                    }
                                    info = "";
                                    nInfo++;
                                } else {
                                    error = true;
                                    System.out.println("Error: dato en la posicion " + nDatos + " no fue encontrado");
                                }
                            }
                            if (temp == ';') {
                                if (nDatos != 40) {
                                    error = true;
                                    System.out.println("Error: esperaba recibir 40 datos y recibí " + nDatos);
                                }
                            }
                        }
                        if (!error) {
                            nDatos = 0;
                            error = false;
                            info = "";
                            mensaje = "BTR2";
                            //System.out.println("Envie: " + mensaje);
                            out.writeUTF(mensaje);
                            msn = inp.readLine();
                            //System.out.println("Recibí: " + msn);
                            charArray = msn.toCharArray();
                            for (char temp : charArray) {
                                if (temp == ',' || temp == ';') {
                                    nDatos++;
                                }
                                if (temp == ';') {
                                    if (nDatos != 40) {
                                        error = true;
                                        System.out.println("Error: esperaba recibir 40 datos y recibí " + nDatos);
                                    }
                                }
                            }
                            if (!error) {
                                nDatos = 0;
                                for (char temp : charArray) {
                                    if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0') {
                                        info += temp;
                                    }
                                    if (temp == ',' || temp == ';') {
                                        nDatos++;
                                        if (info != null) {
                                            try {
                                                infoActual[nInfo] = Integer.parseInt(info);
                                            } catch (Exception e) {
                                                System.err.println("Error: ParseInt: " + e.getMessage());
                                            }
                                            info = "";
                                            nInfo++;
                                        } else {
                                            error = true;
                                            System.out.println("Error: dato en la posicion " + nDatos + " no fue encontrado");
                                        }
                                    }
                                }
                                if (!error) {
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    hora = sdf.format(cal.getTime());
                                    //System.out.println("Graficaré: " + texto);
                                    //a.escribirTxtLine("resource/btrData.txt", texto);
                                    //window.repaint();
                                    //n++;
                                    //time = System.currentTimeMillis() - time;
                                    //System.out.println(n + "\tAveraged " + time + "ms per iteration");
                                    desp.setInfo(infoActual, hora);
                                }
                            }
                        }
                    }
                } else {
                    try {
                        sleep(t);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                        System.err.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
