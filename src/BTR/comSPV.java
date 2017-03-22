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
    int nDatos;
    boolean error;
    boolean habilitado = false;
    int t = 1000;
    JFrame window;

    public boolean getHabilitado() {
        return this.habilitado;
    }

    public void setHabilitado(boolean h) {
        this.habilitado = h;
    }
    
    public void setWindow(JFrame window) {
        this.window = window;
    }

    public void run() {
        try {
            //socket = new Socket("127.0.0.1", 6001);
            socket = new Socket("192.168.1.10", 30000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            archivo a = new archivo();
            sleep(1000);

            while (true) {
                if (getHabilitado()) {
                    //mensaje = in.readLine();
                    error = false;
                    texto = "";
                    word = "";
                    nDatos = 0;
                    mensaje = "DatosBTR\n";
                    out.writeUTF(mensaje);
                    System.out.println("Envie: " + mensaje);
                    msn = inp.readLine();
                    System.out.println("Recibí: " + msn);
                    if (!("Beamforming OK".equals(msn))) {
                        error = true;
                        System.out.println("Error: esperba <Beamforming OK> y recibí <" + msn + ">, Compruebe la comunicación");
                    }
                    if (!error) {
                        mensaje = "BTR1\n";
                        out.writeUTF(mensaje);
                        System.out.println("Envie: " + mensaje);
                        msn = inp.readLine();
                        System.out.println("Recibí: " + msn);
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
                            mensaje = "BTR2\n";
                            System.out.println("Envie: " + mensaje);
                            out.writeUTF(mensaje);
                            msn = inp.readLine();
                            System.out.println("Recibí: " + msn);
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
                                        if (word != "") {
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
                                    texto = sdf.format(cal.getTime()) + "," + texto;
                                    System.out.println("Guardaré: " + texto);
                                    a.escribirTxtLine("resource/btrData.txt", texto);
                                    window.repaint();
                                }
                            }
                        }
                    }
                }
                try {
                    sleep(t);                                //espera un segundo
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
