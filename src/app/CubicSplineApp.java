package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CubicSplineApp extends JPanel {

    private final ArrayList<Point> points = new ArrayList<>();

    public CubicSplineApp() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addPoint(e.getPoint());
                repaint();
            }
        });
    }
    private void addPoint(Point point) {
        for (Point p : points) {
            double distance = point.distance(p);
            if (distance < 5) {
                System.out.println("Точка " + point + " слишком близко к существующей точке.");
                return;
            }
        }
        points.add(point);
        System.out.println("Точка " + point + " успешно добавлена.");
    }

    @Override
    protected void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paintComponent(g);
        drawAxes(g);
        drawPoints(g);
        if (points.size() > 1) {
            try {
                drawCubicSpline(g);
            } catch (IllegalStateException e) {
                System.err.println("Ошибка при отрисовке сплайна: " + e.getMessage());
            }
        }
    }

    private void drawAxes(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2); // X-axis
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight()); // Y-axis
    }

    private void drawPoints(Graphics g) {
        g.setColor(Color.RED);
        for (Point p : points) {
            g.fillOval(p.x - 3, p.y - 3, 6, 6);
        }
    }

    private void drawCubicSpline(Graphics g) {
        Spline spline = new Spline(points);
        spline.draw(g, points);
    }
}