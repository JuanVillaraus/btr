/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    String word;
    String save;
    int nDatos;
    boolean error;
    boolean habilitado = false;
    int t = 1000;
    JFrame window;
    despliegue desp;
    

    public boolean getHabilitado() {
        return this.habilitado;
    }

    public void setHabilitado(boolean h) {
        this.habilitado = h;
    }
    
    public void setWindow(JFrame window) {
        this.window = window;
    }
    
    public void setMarcacion(boolean b){
        System.out.println("marcacion desde SPV");
        desp.setBMarcacion(b);
    }
    
    public String getSave(){
        save = "";
        int[][]waterfall = desp.getWaterfall();
        String[]time = desp.getTime();
        for (int x = 0; x < waterfall.length; x++) {
            save += time[x];
            for (int y = 0; y < waterfall[x].length; y++) {
                save += "," + waterfall[x][y];
            }
            save += ";\n";
        }
        return save;
    }

    public void run() {
        try {
            desp = new despliegue(this.window);
            //socket = new Socket("127.0.0.1", 6001);
            socket = new Socket("192.168.1.10", 20000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            archivo a = new archivo();
            sleep(1000);
            //System.out.println("estoy en el run cpmspv");
            int n=0;
            String hora;

            while (true) {
                //System.out.println("estoy en el while true");
                if (getHabilitado()) {
                    //sleep(30);
                    //  mensaje = in.readLine();
                    error = false;
                    texto = "";
                    word = "";
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
                        msn = inp.readLine();
                        //System.out.println("Recibí: " + msn);
                        char[] charArray = msn.toCharArray();
                        for (char temp : charArray) {
                            if (temp == '1' || temp == '2' || temp == '3' || temp == '4' || temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9' || temp == '0') {
                                word += temp;
                            }
                            if (temp == ',' || temp == ';') {
                                nDatos++;
                                if (word != "") {
                                    texto += word;
                                    texto += ",";
                                    word = "";
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
                            word = "";
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
                                        word += temp;
                                    }
                                    if (temp == ',' || temp == ';') {
                                        nDatos++;
                                        if (word != null) {
                                            texto += word;
                                            texto += temp;
                                            word = "";
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
                                    desp.setInfo(texto,hora);
                                }
                            }
                        }
                    }
                } else {
                    sleep(2000);
                }
                try {
                    //sleep(t);                                //espera un segundo
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
