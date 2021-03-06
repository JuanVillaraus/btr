/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author juan
 */
public class archivo {

    String info = "";

    public String leerTxtLine(String dir) {                                     //lee lo que haya en un archivo txt, recibe como parametros la direccion tipo String y devuelve el String del contenido en una sola linea
        try {
            int lim = 0;
            BufferedReader bf = new BufferedReader(new FileReader(dir));
            String temp = "";
            String bfRead;
            while ((bfRead = bf.readLine()) != null && lim < 20) {
                temp += bfRead;
                lim++;
            }
            info = temp;
        } catch (Exception e) {
            System.err.println("SOY READ LINE: No se encontro el archivo en " + dir);
        }
        return info;
    }

    public String leerTxtLine(String dir, int lim) {                 //lee lo que haya en un archivo txt, recibe como parametros la direccion tipo String y devuelve el String del contenido en una sola linea
        try {
            BufferedReader bf = new BufferedReader(new FileReader(dir));
            String temp = "";
            String bfRead;
            while ((bfRead = bf.readLine()) != null && lim > 0) {
                temp += bfRead;
                lim--;
            }
            info = temp;
        } catch (Exception e) {
            System.err.println("SOY READ LINE con lim: No se encontro el archivo en " + dir);
        }
        return info;
    }

    public String leerTxt(String dir) {                                         //lee lo que haya en un archivo txt, recibe como parametros la direccion tipo String y devuelve el String del contenido
        try {
            BufferedReader bf = new BufferedReader(new FileReader(dir));
            String temp = "";
            String bfRead;
            int lim = 200;
            while (((bfRead = bf.readLine()) != null) && (lim > 0)) {
                temp += bfRead;
                temp += "\n";
                lim--;
            }
            info = temp;
        } catch (Exception e) {
            System.err.println("SOY READ: No se encontro el archivo en " + dir);
        }
        return info;
    }

    public void escribirTxtLine(String dir, String texto) throws IOException {      //escribe un texto en una archivo existente o lo crea, recibe como parametro la direccion del texto y el texto ambos tipo String
        BufferedWriter bw;
        try {
            File archivo = new File(dir);
            if (archivo.exists()) {
                info = leerTxt(dir);
                bw = new BufferedWriter(new FileWriter(archivo));
                bw.write(texto + "\n" + info);
            } else {
                bw = new BufferedWriter(new FileWriter(archivo));
                bw.write(texto + "\n");
            }
            bw.close();
        } catch (Exception e) {
            System.err.println("SOY WRITE LINE: hay un error ");
        }
    }

    public void escribirTxt(String dir, String texto) throws IOException {      //escribe un texto en una archivo existente o lo crea, recibe como parametro la direccion del texto y el texto ambos tipo String
        BufferedWriter bw;
        try {
            File archivo = new File(dir);
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(texto);
            bw.close();
        } catch (Exception e) {
            System.err.println("SOY WRITE hay un error ");
        }
    }
    
    public void escribirTxt(String dir, int texto) throws IOException {      //escribe un texto en una archivo existente o lo crea, recibe como parametro la direccion del texto y el texto ambos tipo String
        BufferedWriter bw;
        try {
            File archivo = new File(dir);
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(Integer.toString(texto));
            bw.close();
        } catch (Exception e) {
            System.err.println("SOY WRITE hay un error ");
        }
    }

    public void save(String dir) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String date = sdf.format(cal.getTime());
        BufferedWriter bw;
        try {
            File archivo = new File("resource/btrData_" + date + ".txt");
            info = leerTxt(dir);
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(info + "\n");
            bw.close();
        } catch (Exception e) {
            System.err.println("SOY SAVE: No se encontro el archivo " + dir);
        }
    }
    
    public void save(String dir, String save) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String date = sdf.format(cal.getTime());
        BufferedWriter bw;
        try {
            File archivo = new File(dir + "_" + date + ".txt");
            //info = leerTxt(dir);
            bw = new BufferedWriter(new FileWriter(archivo));
            bw.write(save);
            bw.close();
        } catch (Exception e) {
            System.err.println("SOY SAVE: No se encontro el archivo " + dir);
        }
    }
}
