package utils;

import drawables.Point;
import solids.Axis;
import solids.Primitive;
import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

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

        Mat4 matFinal;
        if (solid instanceof Axis) {
            matFinal = view.mul(projection);
        } else {
            matFinal = model.mul(view).mul(projection);
        }

        List<Integer> indices = solid.getIndices(Primitive.LINES);
        for (int i = 0; i < indices.size(); i += 2) {
            Point3D a = solid.getVertices().get(indices.get(i));
            Point3D b = solid.getVertices().get(indices.get(i + 1));
            transformEdge(matFinal, a, b,
                    solid.getColorByEdge(i / 2));
        }
    }

    public void drawFilledFrame(Solid solid) { // z-buffer
        Mat4 matFinal = model.mul(view).mul(projection);

        List<Integer> indices = solid.getIndices(Primitive.TRIANGLES);
        for (int i = 0; i < indices.size() - 2; i += 3) {
            Point3D p1 = solid.getVertices().get(indices.get(i));
            Point3D p2 = solid.getVertices().get(indices.get(i+1));
            Point3D p3 = solid.getVertices().get(indices.get(i+2));
            transformTriangles(p1, p2, p3, matFinal);
        }
    }

    private void transformTriangles(Point3D p1, Point3D p2, Point3D p3, Mat4 matFinal) {

        p1 = p1.mul(matFinal);
        p2 = p2.mul(matFinal);
        p3 = p3.mul(matFinal);

        // řazení
        if (p1.getZ() < p2.getZ()) {
            Point3D p = p1;
            p1 = p2;
            p2 = p;
        }

        if (p2.getZ() < p3.getZ()) {
            Point3D p = p2;
            p2 = p3;
            p3 = p;
        }

        if (p1.getZ() < p2.getZ()) {
            Point3D p = p1;
            p1 = p2;
            p2 = p;
        }

        double zMin = 0;
        // 1. scénář (all in)
        if (p3.getZ() > zMin) {
            rasterizeTriangles(p1, p2, p3);
            return;
        }

        // 2. scénář (p3 out)
        if (p2.getZ() > zMin) {
            // t mezi p1 a p3
            double ta = ((zMin - p3.getZ()) / (p1.getZ() - p3.getZ()));
            Point3D p3a = p3.mul(1 - ta).add(p1.mul(ta));

            double tb = ((zMin - p3.getZ()) / (p2.getZ() - p3.getZ()));
            Point3D p3b = p3.mul(1 - tb).add(p2.mul(tb));

            rasterizeTriangles(p1, p2, p3a);
            rasterizeTriangles(p2, p3a, p3b);
            return;
        }
        // 3. scénář (only p1 in)
        if (p1.getZ() > zMin) {
            double t3 = ((zMin - p3.getZ()) / (p1.getZ() - p3.getZ()));
            double t2 = ((zMin - p2.getZ()) / (p1.getZ() - p2.getZ()));

            p3 = p3.mul(1 - t3).add(p1.mul(t3));
            p2 = p2.mul(1 - t2).add(p1.mul(t2));

            rasterizeTriangles(p1, p2, p3);
        }

        // 4. scénář (all out)
        return;

    }

    private void rasterizeTriangles(Point3D p1, Point3D p2, Point3D p3) {

        // dehomogenizace
        Optional<Vec3D> vo1 = p1.dehomog();
        Optional<Vec3D> vo2 = p2.dehomog();
        Optional<Vec3D> vo3 = p3.dehomog();

        // řešení Optional<E>
        if (!vo1.isPresent() || !vo2.isPresent() || !vo3.isPresent()) return;

        Vec3D v1 = vo1.get();
        Vec3D v2 = vo2.get();
        Vec3D v3 = vo3.get();

        // ořez zobrazovacím objemem TODO
        //if ((Math.min(Math.min(v1.getX(), v2.getX()), v3.getX()) > 1.0f) || (Math.max(Math.max(v1.getX(), v2.getX()), v3.getX()) <= -1.0f))
        // return (TODO)

        // transformace do okna
        v1 = v1.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        0.5
                ));
        v2 = v2.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        0.5
                ));
        v3 = v3.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        0.5
                ));


        // scanline (seřazení, interpolace = rasterizace)

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

        if (!vo1.isPresent() || !vo2.isPresent()) return;

        Vec3D v1 = vo1.get();
        Vec3D v2 = vo2.get();

        v1 = v1.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        1
                ));
        v2 = v2.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
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
