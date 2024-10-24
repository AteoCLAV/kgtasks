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
                points.add(e.getPoint());
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAxes(g);
        drawPoints(g);
        if (points.size() > 1) {
            drawCubicSpline(g);
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
        g.setColor(Color.BLUE);
        int n = points.size();
        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        double[] a = y.clone();
        double[] b = new double[n - 1];
        double[] d = new double[n - 1];
        double[] h = new double[n - 1];

        for (int i = 0; i < n - 1; i++) {
            h[i] = x[i + 1] - x[i];
        }

        double[] alpha = new double[n - 1];
        for (int i = 1; i < n - 1; i++) {
            alpha[i] = (3.0 / h[i]) * (a[i + 1] - a[i]) - (3.0 / h[i - 1]) * (a[i] - a[i - 1]);
        }

        double[] c = new double[n];
        double[] l = new double[n];
        double[] mu = new double[n];
        double[] z = new double[n];

        l[0] = 1;
        z[0] = 0;
        for (int i = 1; i < n - 1; i++) {
            l[i] = 2 * (x[i + 1] - x[i - 1]) - h[i - 1] * mu[i - 1];
            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        l[n - 1] = 1;
        z[n - 1] = 0;
        c[n - 1] = 0;

        for (int j = n - 2; j >= 0; j--) {
            c[j] = z[j] - mu[j] * c[j + 1];
            b[j] = (a[j + 1] - a[j]) / h[j] - h[j] * (c[j + 1] + 2 * c[j]) / 3;
            d[j] = (c[j + 1] - c[j]) / (3 * h[j]);
        }

        int prevX = (int) x[0];
        int prevY = (int) y[0];
        for (int i = 0; i < n - 1; i++) {
            for (double t = 0; t <= 1; t += 0.01) {
                double xVal = x[i] + t * h[i];
                double yVal = a[i] + b[i] * t * h[i] + c[i] * t * t * h[i] * h[i] + d[i] * t * t * t * h[i] * h[i] * h[i];
                g.drawLine(prevX, prevY, (int) xVal, (int) yVal);
                prevX = (int) xVal;
                prevY = (int) yVal;
            }
        }
    }
}