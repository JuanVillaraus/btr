/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.awt.event.MouseListener;
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
    DatagramPacket paqueteSend;
    int puerto = 0;
    String cadenaMensaje = "";
    DatagramPacket servPaquete;
    byte[] RecogerServidor_bytes = new byte[256];
    Properties prop = new Properties();
    InputStream input = null;
    despliegue desp;
    winListener window = new winListener();
    String word;
    archivo a = new archivo();

    //@Override
    public void run() {
        //JFrame window = new JFrame("BTR by SIVISO");
        //despliegue desp = new despliegue(window);
        try {
            a.escribirTxt("resource/angBTR.txt", -1);
            a.escribirTxt("resource/marcBTRX.txt", 0);
            a.escribirTxt("resource/marcBTRY.txt", 0);
        } catch (IOException ex) {
            System.err.println("Error al intentar guardar la marcBTR " + ex.getMessage());;
        }

        try {
            comSPPsend cspps = new comSPPsend();
            comSPV cspv = new comSPV();
            cspv.setWindow(window);
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                modelo = prop.getProperty("modelo");
                //System.out.println(modelo);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        System.err.println("BTR/comInterfaz - Error al leer modelo del archivo config: " + e.getMessage());
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
                        desp.addMouseListener(window);

                        break;
                    case "SSPV":
                        puerto = 5003;
                        //cspv.start();
                        //cspv.setHabilitado(true);
                        break;
                    default:
                        break;
                }
            }
            address = InetAddress.getByName("localhost");
            socket = new DatagramSocket();
            mensaje = "BTR";
            mensaje_bytes = mensaje.getBytes();
            paqueteSend = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
            //socket.send(paqueteSend);
            mensaje = "runBTR";
            mensaje_bytes = mensaje.getBytes();
            paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
            socket.send(paquete);
            System.out.println("enviamos " + mensaje + " para inicializar la comunicación con la interfaz");
            archivo a = new archivo();
            String hora;
            window.setPort(socket.getLocalPort());
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            do {
                RecogerServidor_bytes = new byte[256];
                servPaquete = new DatagramPacket(RecogerServidor_bytes, 256);
                socket.receive(servPaquete);
                cadenaMensaje = new String(RecogerServidor_bytes).trim();   //Convertimos el mensaje recibido en un string
                System.out.println("Recibí: " + cadenaMensaje);
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
                    case "BIESTATICO":
                        hora = sdf.format(cal.getTime());
                        desp.separacionWaterfall(hora);
                    case "ON":
                        window.setExtendedState(JFrame.NORMAL);
                        cspps.setHabilitado(true);
                        cspv.setHabilitado(true);
                        mensaje = "Cu" + Integer.toString(desp.getColorUp());
                        mensaje_bytes = mensaje.getBytes();
                        paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5002);
                        socket.send(paquete);
                        mensaje = "Cd" + Integer.toString(desp.getColorDw());
                        mensaje_bytes = mensaje.getBytes();
                        paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 5002);
                        socket.send(paquete);
                        break;
                    case "EXIT":
                        System.exit(0);
                        break;
                    case "SAVE":
                        /*if ("SSPV".equals(modelo)) {
                            a.save("resource/btrData.txt", cspv.getSave());
                        }*/
                        desp.save();
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
                    case "DISTARGET":
                        int[][] t = desp.getTarget();
                        mensaje = "D" + Integer.toString(desp.distTarget())+","+Integer.toString(t[0][0])+","+Integer.toString(t[1][0])+";";
                        /*String s = "";
                        for (int x = 0; x < t.length; x++) {
                            for (int y = 0; y < t[x].length; y++) {
                                System.out.print(" "+t[x][y]);
                            }
                            System.out.println("");
                        }*/
                        mensaje_bytes = mensaje.getBytes();
                        paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, puerto);
                        socket.send(paquete);
                        break;
                    default:
                        char[] charArray = cadenaMensaje.toCharArray();
                        word = "";
                        if (charArray[0] == 'C' || charArray[0] == 'U') {
                            for (int i = 2; i < charArray.length; i++) {
                                word += charArray[i];
                            }
                            if (charArray[0] == 'C') {
                                if (charArray[1] == 'u') {
                                    desp.setColorUp(Integer.parseInt(word));
                                } else if (charArray[1] == 'd') {
                                    desp.setColorDw(Integer.parseInt(word));
                                }
                            } else if (charArray[0] == 'U') {
                                if (charArray[1] == 'u') {
                                    desp.setUmbralUp((int)(Double.parseDouble(word)*100));
                                } else if (charArray[1] == 'd') {
                                    desp.setUmbralDw((int)(Double.parseDouble(word)*100));
                                }
                            }
                        } else if (charArray[0] == 'T') {
                            desp.newTarget(charArray);
                            //desp.newTarget(cadenaMensaje);
                        } else if (charArray[0] == 'P') {
                            for (int i = 1; i < charArray.length; i++) {
                                word += charArray[i];
                            }
                            //desp.setPaso(Integer.parseInt(word));
                            //desp.setPuertoPPI(Integer.parseInt(word));
                        } else if ("SSPP".equals(modelo) || "SSF".equals(modelo)) {
                            hora = sdf.format(cal.getTime());
                            //a.escribirTxtLine("resource/btrData.txt", texto);
                            //window.repaint();
                            if (charArray[0] == '$') {
                                desp.setInfo(cadenaMensaje, hora, 'B');
                            } else {
                                socket.send(paqueteSend);
                                desp.setInfo(cadenaMensaje, hora, 'M');
                            }
                            cspps.setEstadoEnvio(false);
                        }
                        break;
                }
            } while (true);
        } catch (Exception e) {
            System.err.println("BTR/comInterfaz - Error en la comunicación con la consola " + e.getMessage());
            System.exit(1);
        }
    }
}
