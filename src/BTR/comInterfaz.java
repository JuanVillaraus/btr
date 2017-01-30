/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.*;

/**
 *
 * @author juan
 */
class comInterfaz extends Thread {

    DatagramSocket socket;
    InetAddress address;
    byte[] mensaje_bytes = new byte[256];
    String mensaje = "";
    DatagramPacket paquete;
    String cadenaMensaje = "";
    DatagramPacket servPaquete;
    byte[] RecogerServidor_bytes = new byte[256];
    String texto = "";

    //@Override
    public void run(JFrame window) {
        try {
            mensaje_bytes = mensaje.getBytes();
            address = InetAddress.getByName("localhost");
            mensaje = "runBTR";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5002);
            socket = new DatagramSocket();
            socket.send(paquete);
            System.out.println("enviamos runBTR para inicializar la comunicación con el software");
            comSPPsend cspps = new comSPPsend();
            cspps.start();
            archivo a = new archivo();

            do {
                RecogerServidor_bytes = new byte[256];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                texto = "";
                if ("OFF".equals(cadenaMensaje)) {
                    window.setExtendedState(JFrame.ICONIFIED);
                    //System.out.println("BTR esta deshabilitado");
                    if (cspps.getHabilitado()) {
                        cspps.setHabilitado(false);
                    }
                } else if ("ON".equals(cadenaMensaje)) {
                    window.setExtendedState(JFrame.NORMAL);
                    //System.out.println("BTR esta habilitado");
                    if (!cspps.getHabilitado()) {
                        cspps.setHabilitado(true);
                    }
                } else if ("EXIT".equals(cadenaMensaje)) {
                    System.exit(0);
                } else if ("SAVE".equals(cadenaMensaje)) {
                    a.save("resource/btrData.txt");
                } else if ("RP".equals(cadenaMensaje)) {                    //BTR repaint
                    window.repaint();
                } else if ("LONG".equals(cadenaMensaje)) {                    
                    Properties prop = new Properties();
                    InputStream input = null;
                    try {
                        input = new FileInputStream("config.properties");
                        prop.load(input);
                        mensaje = "LONG" + prop.getProperty("longBTR") + ";";
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
                    mensaje_bytes = mensaje.getBytes();
                    paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5002);
                    socket.send(paquete);
                } else if (!("START OK!".equals(cadenaMensaje))) {
                    char[] charArray = cadenaMensaje.toCharArray();
                    for (char temp : charArray) {
                        texto += temp;
                    }
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    texto = sdf.format(cal.getTime()) + "," + texto;
                    a.escribirTxtLine("resource/btrData.txt", texto);

                    window.repaint();
                }
            } while (true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
