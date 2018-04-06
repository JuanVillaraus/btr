package BTR;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.awt.*;
import javax.swing.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author siviso
 */
public class winListener extends JFrame implements MouseListener {

    int longBTR;
    int marcBTR;
    int ang;
    archivo a = new archivo();

    public winListener() {
        //setName("BTR by SIVISO");
        //this.setTitle("BTR by SIVISO");
        
        //pack();
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);
        setVisible(true);
        setAlwaysOnTop(true);
        //setFocusable(true);
        Properties prop = new Properties();
        InputStream input = null;
        int posicionX = 0;
        int posicionY = 0;
        int dimensionX = 100;
        int dimensionY = 100;
        String modelo = "";
        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            posicionX = Integer.parseInt(prop.getProperty("posicionX"));
            posicionY = Integer.parseInt(prop.getProperty("posicionY"));
            dimensionX = Integer.parseInt(prop.getProperty("dimensionX"));
            dimensionY = Integer.parseInt(prop.getProperty("dimensionY"));
            longBTR = Integer.parseInt(prop.getProperty("longBTR"));
            modelo = prop.getProperty("modelo");
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
        setLocation(posicionX, posicionY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getX() + " " + e.getY());
        if (e.getX() > 75 && e.getY() < 100) {
            ang = ((e.getX() - 75) * 360) / (((getSize().width - 75) / longBTR) * longBTR);
            marcBTR = (int)((((e.getX() - 75) * 360) / (((getSize().width - 75) / longBTR) * longBTR)) / 4.5)+1;
            if (marcBTR > 80) {
                System.out.print(marcBTR + " - ");
                marcBTR = 1;
                System.out.println(marcBTR);
            }
            System.out.println("Angulo: " + ang + "\tMarcación: " + marcBTR + " ");
            try {
                a.escribirTxt("resource/angBTR.txt", ang);
                a.escribirTxt("resource/marcBTR.txt", marcBTR);
            } catch (IOException ex) {
                System.err.println("Error al intentar guardar la marcBTR " + ex.getMessage());;
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
