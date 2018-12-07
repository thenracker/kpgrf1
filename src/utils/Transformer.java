package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Optional;

import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

public class Transformer {

    private BufferedImage img;

    private Mat4 model;
    private Mat4 view;
    private Mat4 projection;

    public Transformer(BufferedImage img) {
        this.img = img;
        // prozatím jednotkové matice
        this.model = new Mat4Identity();
        this.view = new Mat4Identity();
        this.projection = new Mat4Identity();
    }

    public void drawWireFrame(Solid solid) {

        // TODO vypočítat výslednou matici z model,view,projection
        Mat4 matFinal;
        matFinal = model.mul(view).mul(projection);

        for (int i = 0; i < solid.getIndices().size(); i += 2) {
            Point3D a = solid.getVertices().get(solid.getIndices().get(i));
            Point3D b = solid.getVertices().get(solid.getIndices().get(i + 1));
            transformEdge(matFinal, a, b, Color.BLACK.getRGB());
        }
    }

    private void transformEdge(Mat4 matFinal, Point3D p1, Point3D p2, int color) {
        // 1.) vynásobit body maticí
        p1 = p1.mul(matFinal);
        p2 = p2.mul(matFinal);

        // 2.) ořez dle W bodů
        if (p1.getW() <= 0 && p2.getW() <= 0) return;

        // 3.) tvorba z vektorů dehomogenizací (Point3D.dehomog())
        Optional<Vec3D> vo1 = p1.dehomog();
        Optional<Vec3D> vo2 = p2.dehomog();

        if (!vo1.isPresent() || !vo2.isPresent())return;

        Vec3D v1 = vo1.get();
        Vec3D v2 = vo2.get();

        v1 = v1.mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        1
                ));
        v2 = v2.mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1,1,0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        1
                ));

        // TODO 4.) ořez zobrazovací objem

        lineDDA((int) v1.getX(), (int) v1.getY(), (int) v2.getX(), (int) v2.getY(), color);
    }

    // metody vykreslování
    public void lineDDA(int x1, int y1, int x2, int y2, int color) {

        float k, g, h; //G = PŘÍRŮSTEK X, H = PŘÍRŮSTEK Y
        int dy = y2 - y1;
        int dx = x2 - x1;
        k = dy / (float) dx;

        //určení řídící osy
        if (Math.abs(dx) > Math.abs(dy)) {
            g = 1;
            h = k;
            if (x1 > x2) { // prohození
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
        } else {
            g = 1 / k;
            h = 1;
            if (y1 > y2) { //otočení
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
        }

        float x = x1;
        float y = y1;
        int max = Math.max(Math.abs(dx), Math.abs(dy));

        for (int i = 0; i <= max; i++) {
            drawPixel(Math.round(x), Math.round(y), color);
            x += g;
            y += h;
        }
    }

    private void drawPixel(int x, int y, int color) {
        if (x < 0 || x >= 800) return;
        if (y < 0 || y >= 600) return;
        img.setRGB(x, y, color);
    }

    // setters for matrix
    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }
}
