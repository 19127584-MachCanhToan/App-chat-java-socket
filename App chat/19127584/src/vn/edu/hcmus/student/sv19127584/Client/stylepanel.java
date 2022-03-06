package vn.edu.hcmus.student.sv19127584.Client;

import javax.swing.*;
import java.awt.*;

/**
 * vn.edu.hcmus.student.sv19127584.Client
 * Created by 19127584 - Mạch Cảnh Toàn
 * Date 1/17/2022 - 9:18 PM
 * Description: ...
 */
public class stylepanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth();
        int h = getHeight();
        Color color2 = new Color(230, 145, 154);
        Color color1 = Color.WHITE;
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}