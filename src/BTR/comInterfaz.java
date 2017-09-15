/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author juan
 */
class comInterfaz extends Thread {

    DatagramSocket socket;
    InetAddress address;
    byte[] mensaje_bytes = new byte[256];
    String modelo = "";
    String mensaje = "";
    DatagramPacket paquete;
    int puerto = 0;
    String cadenaMensaje = "";
    DatagramPacket servPaquete;
    byte[] RecogerServidor_bytes = new byte[256];
    String texto = "";
    Properties prop = new Properties();
    InputStream input = null;
    despliegue desp;

    //@Override
    public void run() {
        JFrame window = new JFrame("BTR by SIVISO");
        //despliegue desp = new despliegue(window);
        try {
            comSPPsend cspps = new comSPPsend();
            comSPV cspv = new comSPV();
            cspv.setWindow(window);
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                modelo = prop.getProperty("modelo");
                //System.out.println(modelo);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        System.err.println("Error BTR en la carga del config: " + e.getMessage());
                    }
                }
            }
            if (null != modelo) {
                switch (modelo) {
                    case "SSPP":
                        puerto = 5001;
                        cspps.setPuerto(puerto);
                        cspps.start();
                        desp = new despliegue(window);
                        break;
                    case "SSF":
                        puerto = 5002;
                        cspps.setPuerto(puerto);
                        cspps.start();
                        desp = new despliegue(window);
                        break;
                    case "SSPV":
                        puerto = 5003;
                        break;
                    default:
                        break;
                }
            }
            //System.out.println(puerto);
            address = InetAddress.getByName("localhost");
            mensaje = "runBTR";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
            socket = new DatagramSocket();
            socket.send(paquete);
            System.out.println("enviamos " + mensaje + " para inicializar la comunicación con la interfaz");
            archivo a = new archivo();
            String hora;

            do {
                RecogerServidor_bytes = new byte[256];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                System.out.println("Recibí: " + cadenaMensaje);
                texto = "";
                switch (cadenaMensaje) {
                    case "RUN":
                        cspv.start();
                        try {
                            sleep(300);
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Error en el sleep del start en comInterfaz" + e.getMessage());
                        }
                    case "OFF":
                        window.setExtendedState(JFrame.ICONIFIED);
                        cspps.setHabilitado(false);
                        cspv.setHabilitado(false);
                        break;
                    case "ON":
                        window.setExtendedState(JFrame.NORMAL);
                        cspps.setHabilitado(true);
                        cspv.setHabilitado(true);
                        break;
                    case "EXIT":
                        System.exit(0);
                        break;
                    case "SAVE":
                        if ("SSPV".equals(modelo)) {
                            a.save("resource/btrData.txt", cspv.getSave());
                        }
                        break;
                    case "RP":
                        window.repaint();
                        break;
                    case "M_ON":
                        cspv.setMarcacion(true);
                        break;
                    case "M_OFF":
                        cspv.setMarcacion(false);
                        break;
                    case "LONG":
                        try {
                            input = new FileInputStream("config.properties");
                            prop.load(input);
                            mensaje = "LONG" + prop.getProperty("longBTR") + ";";
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
                        mensaje_bytes = mensaje.getBytes();
                        paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                        socket.send(paquete);
                        break;
                    default:
                        char[] charArray = cadenaMensaje.toCharArray();
                        for (char temp : charArray) {
                            texto += temp;
                        }
                        if ("SSPP".equals(modelo) || "SSF".equals(modelo)) {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            hora = sdf.format(cal.getTime());
                            //a.escribirTxtLine("resource/btrData.txt", texto);
                            //window.repaint();
                            desp.setInfo(texto, hora);
                        }
                        break;
                }
            } while (true);
        } catch (Exception e) {
            System.err.println("Error en la comunicación con la consola " + e.getMessage());
            System.exit(1);
        }
    }
}
