package app;

import java.awt.*;
import java.util.ArrayList;


public class Spline {
    private double[] y;
    private double[] x;
    private double[] D;
    private double[][] coef;
    private boolean isClosed;


    public Spline(ArrayList<Point> points) {
        if (points == null || points.size() < 2) {
            throw new IllegalArgumentException("At least 2 points are required");
        }
        
        int n = points.size();
        x = new double[n];
        y = new double[n];
        
        for (int i = 0; i < n; i++) {
            x[i] = points.get(i).x;
            y[i] = points.get(i).y;
        }
        
        this.isClosed = false;
        calculateDerivatives();
        calculateCoefficients();
    }


    private void calculateDerivatives() {
        int n = y.length - 1;
        D = new double[n + 1];

        double[] a = new double[n + 1];
        double[] b = new double[n + 1];
        double[] c = new double[n + 1];
        double[] r = new double[n + 1];
        
        if (isClosed) {
            b[0] = 4.0;
            c[0] = 1.0;
            a[n] = 1.0;
            r[0] = 3 * (y[1] - y[n]);
            
            for (int i = 1; i < n; i++) {
                a[i] = 1.0;
                b[i] = 4.0;
                c[i] = 1.0;
                r[i] = 3 * (y[i + 1] - y[i - 1]);
            }
            
            b[n] = 4.0;
            r[n] = 3 * (y[0] - y[n - 1]);
        } else {
            b[0] = 2.0;
            c[0] = 1.0;
            r[0] = 3 * (y[1] - y[0]);
            
            for (int i = 1; i < n; i++) {
                a[i] = 1.0;
                b[i] = 4.0;
                c[i] = 1.0;
                r[i] = 3 * (y[i + 1] - y[i - 1]);
            }
            
            a[n] = 1.0;
            b[n] = 2.0;
            r[n] = 3 * (y[n] - y[n - 1]);
        }

        for (int i = 1; i <= n; i++) {
            double m = a[i] / b[i - 1];
            b[i] = b[i] - m * c[i - 1];
            r[i] = r[i] - m * r[i - 1];
        }

        D[n] = r[n] / b[n];
        for (int i = n - 1; i >= 0; i--) {
            D[i] = (r[i] - c[i] * D[i + 1]) / b[i];
        }
    }


    private void calculateCoefficients() {
        int n = y.length - 1;
        coef = new double[n][4];
        
        for (int i = 0; i < n; i++) {
            coef[i][0] = y[i];                                          // a_i
            coef[i][1] = D[i];                                          // b_i
            coef[i][2] = 3 * (y[i + 1] - y[i]) - 2 * D[i] - D[i + 1];   // c_i
            coef[i][3] = 2 * (y[i] - y[i + 1]) + D[i] + D[i + 1];       // d_i
        }
    }


    public double evaluate(int i, double t) {
        if (i < 0 || i >= coef.length) {
            throw new IllegalArgumentException("Segment index out of range");
        }
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("Parameter t must be in [0,1]");
        }
        
        double t2 = t * t;
        double t3 = t2 * t;
        return coef[i][0] + coef[i][1] * t + coef[i][2] * t2 + coef[i][3] * t3;
    }


    public void draw(Graphics g, ArrayList<Point> points) {
        if (points.size() < 2) return;
        
        g.setColor(Color.BLUE);

        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            int steps = 50;
            Point prevPoint = p1;
            
            for (int j = 1; j <= steps; j++) {
                double t = (double) j / steps;
                double y = evaluate(i, t);
                double x = p1.x + t * (p2.x - p1.x);
                Point currentPoint = new Point((int) x, (int) y);
                g.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
                prevPoint = currentPoint;
            }
        }
    }
}
