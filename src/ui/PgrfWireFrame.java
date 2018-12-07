package ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import solids.Axis;
import solids.Cube;
import solids.Solid;
import transforms.Camera;
import transforms.Mat4PerspRH;
import transforms.Mat4RotZ;
import transforms.Mat4Transl;
import transforms.Point3D;
import utils.Transformer;

public class PgrfWireFrame extends JFrame {

    static int FPS = 1000 / 30;
    static int width = 800;
    static int height = 600;
    private JPanel panel;
    private BufferedImage img;

    private Transformer transformer;
    private Camera camera;
    private List<Solid> solids;

    private int beginX, beginY; // ovládání myší

    public static void main(String[] args) {
        PgrfWireFrame frame = new PgrfWireFrame();
        frame.init(width, height);
    }

    private void init(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // nastavení frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(width, height);
        setTitle("Drátový model");
        panel = new JPanel();
        add(panel);
        solids = new ArrayList<>();

        transformer = new Transformer(img);
        camera = new Camera();
        transformer.setProjection(
                new Mat4PerspRH(1,1,1,100));

        // vytvoření objektů
        initSolids();

        // listeners
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                beginX = e.getX();
                beginY = e.getY();
                super.mousePressed(e);
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                camera = camera.addAzimuth((Math.PI / 1000) * (beginX - e.getX()));
                camera = camera.addZenith((Math.PI / 1000) * (beginY - e.getY()));
                beginX = e.getX();
                beginY = e.getY();
                super.mouseDragged(e);
            }
        });
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        camera = camera.forward(0.1);
                        break;
                    case KeyEvent.VK_DOWN:
                        camera = camera.backward(0.1);
                        break;
                    case KeyEvent.VK_LEFT:
                        camera = camera.left(0.1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        camera = camera.right(0.1);
                        break;

                }
                super.keyReleased(e);
            }
        });

        // timer pro refresh draw()
        setLocationRelativeTo(null);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                draw();
            }
        }, 100, FPS);

    }

    private void initSolids() {
        // todo více objektů
        solids.add(new Axis());

        int count = 5;
        for (int i = 0; i < count; i++) {
            Cube cube = new Cube(1);
            for (int j = 0; j < cube.getVertices().size(); j++) {
                Point3D p = cube.getVertices().get(j);
                Point3D n = p
                        .mul(new Mat4Transl(0,2,0))
                        .mul(new Mat4RotZ((double)
                                i * 2d * Math.PI / (double) count));
                cube.getVertices().set(j, n);
            }
            solids.add(cube);
        }

    }

    private void draw() {
        // clear
        img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight());

        // transformer.setModel() todo
        transformer.setView(camera.getViewMatrix());

        for (Solid solid : solids) {
            transformer.drawWireFrame(solid); // výkres solids
        }

        panel.getGraphics().drawImage(img, 0, 0, null);
        panel.paintComponents(getGraphics());
    }
}
