package net.sourceforge.jasa;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class GraphingData extends JPanel {

    int[] yData = {
        21, 14, 18, 03, 86, 88, 74, 87, 54, 77,
        61, 55, 48, 60, 49, 36, 38, 27, 20, 18
    };
    int[] xData = new int[yData.length];
    int[] auxData = new int[yData.length];
    
    final int PAD = 20;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h - PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h - PAD, w - PAD, h - PAD));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = "data";
        float sy = PAD + ((h - 2 * PAD) - s.length() * sh) / 2 + lm.getAscent();
        for (int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float) font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw) / 2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
//        // Abcissa label.
//        s = "x axis";
//        sy = h - PAD + (PAD - sh) / 2 + lm.getAscent();
//        float sw = (float) font.getStringBounds(s, frc).getWidth();
//        float sx = (w - sw) / 2;
//        g2.drawString(s, sx, sy);
        // Draw lines.
        double xInc = (double) (w - 2 * PAD) / (yData.length - 1);
        double scale = (double) (h - 2 * PAD) / getMax();
        g2.setPaint(Color.green.darker());
        for (int i = 0; i < yData.length - 1; i++) {
            double x1 = PAD + i * xInc;
            double y1 = h - PAD - scale * yData[i];
            double x2 = PAD + (i + 1) * xInc;
            double y2 = h - PAD - scale * yData[i + 1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Mark data points.
        g2.setPaint(Color.red);
        for (int i = 0; i < yData.length; i++) {
            double x = PAD + i * xInc;
            double y = h - PAD - scale * yData[i];
            g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
        }
        // Mark X points.
        g2.setPaint(Color.red);
        String aux;
        for (int i = 0; i < yData.length; i++) {
            xData[i] = i + 1;
            auxData[i] = i;
        }
        for (int i = 0; i < xData.length; i++) {
            aux = String.valueOf(xData[i]);
            System.out.println("xdata: "+ xData[i] + "\naux: "+aux);
            //System.out.println(aux);
            double x = PAD + i * xInc;
            double y = h - PAD - scale;
            //System.out.println("Ponto: "+x+";"+y);
            g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
            g2.drawString(aux, (int) x, (int) y+20);
            
        }
        for (int i = 0,j=auxData.length-1; i < xData.length; i++,j--) {
            aux = String.valueOf(yData[i]);
            System.out.println("xdata: "+ yData[i] + "\naux: "+aux);
            //System.out.println(aux);
            double x = PAD ;
            double y =  h - PAD - scale * auxData[j]*xInc;
            //System.out.println("Ponto: "+x+";"+y);
            g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
            g2.drawString(aux, (int) x-10, (int) y);
            
        }
    }

    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for (int i = 0; i < yData.length; i++) {
            if (yData[i] > max) {
                max = yData[i];
            }
        }
        return max;
    }

    public void draw() {
        for (int i = 0; i < yData.length; i++) {
            xData[i] = i + 1;
            auxData[i] = i;
        }
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new GraphingData());
        f.setSize(400, 400);
        f.setLocation(200, 200);
        f.setVisible(true);
    }
}
