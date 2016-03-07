/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BTR;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
//import java.net.*;
//import java.io.*;

/**
 *
 * @author juan
 */
public class BTR extends JComponent {

    int sizeCanalX = 0;
    int sizeCanalY;

    int xi, yi, c;
    int inc = 255 / 11;
    String infor;
    int ml[];
    //Graphics g;
    int gn = 0;

    public static void main(String[] args) {
        JFrame window = new JFrame("BTR by SIVISO");
        BTR wf = new BTR();
        window.add(wf);
        window.pack();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        //despliegue d = new despliegue();
        //d.run();
        comInterfaz c = new comInterfaz();
        c.run(window);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 500);
    }

    @Override
    protected void paintComponent(Graphics g) {
        gn++;
        System.out.println("paint component ciclo numero: " + gn);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getSize().width, getSize().height);

        sizeCanalX = getSize().width / 11;
        sizeCanalY = ((getSize().height)-100) / 12;
        despliegue d = new despliegue();
        d.run(g, sizeCanalX, sizeCanalY);

    }
}
