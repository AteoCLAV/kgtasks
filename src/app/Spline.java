package app;

import java.awt.*;
import java.util.ArrayList;

public class Spline {
    ArrayList<Point> points;

    public Spline(ArrayList<Point> points) {
        this.points = points;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    private double[] h;

    private double[][] calcuclateSpline(ArrayList<Point> points, int n) {
        if (n < 2) return null;
        
        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = points.get(i).getX();
            y[i] = points.get(i).getY();
        }

        double[] a = y.clone();
        double[] b = new double[n - 1];
        double[] d = new double[n - 1];
        h = new double[n - 1];

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

        return new double[][]{a, b, c, d, x, y};
    }

    public void draw(Graphics g, ArrayList<Point> points) {
        g.setColor(Color.BLUE);
        int n = points.size();

        double[][] results = calcuclateSpline(points, n);
        double[] a = results[0];
        double[] b = results[1];
        double[] c = results[2];
        double[] d = results[3];
        double[] x = results[4];
        double[] y = results[5];

        ArrayList<Point> splinePoints = new ArrayList<>();
        for (int i = 0; i < n - 1; i++) {
            for (double t = 0; t <= 1; t += 0.001) {
                double xVal = x[i] + t * h[i];
                double yVal = a[i] + b[i] * t * h[i] + c[i] * t * t * h[i] * h[i] + d[i] * t * t * t * h[i] * h[i] * h[i];
                splinePoints.add(new Point((int) xVal, (int) yVal));
            }
        }
        int[] splineX = new int[splinePoints.size()];
        int[] splineY = new int[splinePoints.size()];
            for (int i = 0; i < splinePoints.size(); i++) {
            splineX[i] = splinePoints.get(i).x;
            splineY[i] = splinePoints.get(i).y;
            }
        g.drawPolyline(splineX, splineY, splinePoints.size());
    }
}