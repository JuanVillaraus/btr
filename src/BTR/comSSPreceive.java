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

/**
 *
 * @author juan
 */
public class comSSPreceive extends Thread {

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

    public comSSPreceive() {
        System.out.println("hilo de comunicacion al SSP modo escucha a iniciado");
    }

    @Override
    public void run() {
        System.out.println("El run de comunicacion al SSP modo escucha a iniciado");
        try {
            RecogerServidor_bytes = new byte[256];
            servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);      //Esperamos a recibir un paquete
            while (true) {
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                System.out.println(cadenaMensaje);                          //Imprimimos el paquete recibido
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
