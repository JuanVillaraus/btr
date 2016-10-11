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
public class comSPPsend extends Thread {

    //Definimos el sockets, número de bytes del buffer, y mensaje.
    DatagramSocket socket;
    InetAddress address;
    byte[] mensaje_bytes = new byte[256];
    String mensaje = "";
    //Paquete
    DatagramPacket paquete;
    boolean habilitado = false;

    public comSPPsend() {
        System.out.println("hilo de comunicacion al SPP modo solicitud a iniciado");

    }

    public boolean getHabilitado() {
        return this.habilitado;
    }

    public void setHabilitado(boolean h) {
        this.habilitado = h;
    }

    @Override
    public void run() {
        System.out.println("El run de comunicacion al SPP modo solicitud a iniciado");
        try {
            mensaje_bytes = mensaje.getBytes();
            //address = InetAddress.getByName("192.168.1.178");
            address = InetAddress.getByName("localhost");
            mensaje = "BTR";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5002);
            socket = new DatagramSocket();
            int n = 0;
            while (true) {
                if (getHabilitado()) {
                    n++;
                    System.out.println(n);
                    socket.send(paquete);
                }
                try {
                    sleep(1000);                                //espera un segundo
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
