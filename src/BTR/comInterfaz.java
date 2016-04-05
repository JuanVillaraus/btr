/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
import javax.swing.*;

/**
 *
 * @author juan
 */
class comInterfaz extends Thread {

    //Definimos el sockets, número de bytes del buffer, y mensaje.
    DatagramSocket socket;
    InetAddress address;
    byte[] mensaje_bytes = new byte[256];
    String mensaje = "";
    //Paquete
    DatagramPacket paquete;
    String cadenaMensaje = "";
    DatagramPacket servPaquete;
    byte[] RecogerServidor_bytes = new byte[256];
    //String str;
    despliegue d = new despliegue();
    int[] n = new int[11];
    String texto = "";

    public void comunicacion() { // throws SocketException, UnknownHostException

        System.out.println("inicia public void comunicacion");
    }

    //@Override
    public void run(JFrame window) {
        try {
            mensaje_bytes = mensaje.getBytes();
            address = InetAddress.getByName("192.168.1.178");
            mensaje = "runBTR";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5002);
            socket = new DatagramSocket();
            socket.send(paquete);
            System.out.println("enviamos runBTR");
            //RecogerServidor_bytes = new byte[256];
            comSPPsend cspps = new comSPPsend();
            cspps.start();
            //comSSPreceive csppr = new comSSPreceive();
            //csppr.start();
            for (int i = 0; i < 11; i++) {
                n[i] = 0;
            }
            archivo a = new archivo();

            int i;
            do {
                RecogerServidor_bytes = new byte[256];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                System.out.println(cadenaMensaje);                          //Imprimimos el paquete recibido
                if ("BTR_OFF".equals(cadenaMensaje)) {
                     window.setVisible(false);
                    System.out.println("BTR esta deshabilitado");
                    if (cspps.getHabilitado()) {
                        cspps.setHabilitado(false);
                    }
                } else if ("BTR_ON".equals(cadenaMensaje)) {
                      window.setVisible(true);
                    System.out.println("BTR esta habilitado");
                    if (!cspps.getHabilitado()) {
                        cspps.setHabilitado(true);
                    }
                } else if (!("START OK!".equals(cadenaMensaje))) {
                    i = 0;
                    char[] charArray = cadenaMensaje.toCharArray();
                        texto = "";
                    for (char temp : charArray) {
                        if (i < 11 && ((int) temp > 0) && ((int) temp < 255)) {
                            texto += Integer.toString((int)temp);
                            if(i==10)
                                texto += ";";
                            else
                                texto += ",";
                        }else 
                            System.out.println("Error #??: el valor a guardar esta fuera de rango");
                        i++;
                    }
                    a.escribirTxt("resource/dataEj.txt", texto);
                    window.repaint();
                }
            } while (true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
