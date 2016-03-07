/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.io.*;

/**
 *
 * @author juan
 */
public class archivo {

    String info = "";
    
    public String leerTxtLine(String dir) {                                     //lee lo que haya en un archivo txt, recibe como parametros la direccion tipo String y devuelve el String del contenido en una sola linea
        try {
            BufferedReader bf = new BufferedReader(new FileReader(dir));
            String temp = "";
            String bfRead;
            while ((bfRead = bf.readLine()) != null) {
                temp += bfRead;
            }
            info = temp;
        } catch (Exception e) {
            System.err.println("SOY READ: No se encontro el archivo");
        }
        return info;
    }

    public String leerTxt(String dir) {                                         //lee lo que haya en un archivo txt, recibe como parametros la direccion tipo String y devuelve el String del contenido
        try {
            BufferedReader bf = new BufferedReader(new FileReader(dir));   
            String temp = "";
            String bfRead;
            while ((bfRead = bf.readLine()) != null) {
                temp += bfRead;
                temp += "\n";
            }
            info = temp;
        } catch (Exception e) {
            System.err.println("SOY READ: No se encontro el archivo");
        }
        return info;
    }

    public void escribirTxt(String dir, String texto) throws IOException {      //escribe un texto en una archivo existente o lo crea, recibe como parametro la direccion del texto y el texto ambos tipo String
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
            System.err.println("SOY WRITE: No se encontro el archivo " + dir);
        }
    }
}
