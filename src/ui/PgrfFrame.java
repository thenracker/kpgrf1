package ui;

import java.awt.Color;
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

import drawables.Drawable;
import drawables.DrawableType;
import drawables.Line;
import drawables.Point;
import drawables.Polygon;
import utils.Renderer;

public class PgrfFrame extends JFrame {

    static int FPS = 1000 / 30;
    static int width = 800;
    static int height = 600;

    //fixme smazat časem...
    private int x, y;
    private int startLineX, startLineY;

    private BufferedImage img;
    private JPanel panel;
    private Renderer renderer;

    private List<Drawable> drawables; // list objektů
    private Drawable drawable;

    private DrawableType type = DrawableType.LINE;
    private boolean fillMode = false;

    public static void main(String... args) {
        PgrfFrame pgrfFrame = new PgrfFrame();
        pgrfFrame.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        pgrfFrame.init(width, height);
    }

    private void init(int width, int height) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(width, height);
        setTitle("KPGRF1");
        drawables = new ArrayList<>();
        panel = new JPanel();
        add(panel);

        // mouse listeners
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                super.mouseMoved(e);
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!fillMode) {
                    if (type == DrawableType.LINE) {
                        // zadávání úsečky
                        if (startLineX == 0 && startLineY == 0){
                            startLineX = e.getX();
                            startLineY = e.getY();
                        } else {
                            // druhé kliknutí
                            Line line = new Line(startLineX, startLineY,
                                    e.getX(), e.getY());
                            drawables.add(line);
                            startLineX = 0; startLineY = 0;

                        }
                    }
                    if (type == DrawableType.POLYGON) {
                        // n-úhelník
                        if (drawable == null){
                            drawable = new Polygon();
                            ((Polygon) drawable).addPoint(new Point(e.getX(), e.getY()));
                        } else {
                            ((Polygon) drawable).addPoint(new Point(e.getX(), e.getY()));
                        }
                    }
                } else {
                    // seed fill / scan line todo
                    renderer.seedFill(e.getX(), e.getY(),
                            img.getRGB(e.getX(), e.getY()),
                            Color.BLACK.getRGB());
                }
                super.mouseClicked(e);
            }
        });

        // key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_U) {
                    type = DrawableType.LINE;
                }
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    type = DrawableType.POLYGON;
                }
                if (e.getKeyCode() == KeyEvent.VK_F) {
                    fillMode = !fillMode;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    if (drawable != null){
                        drawables.add(drawable);
                        drawable = null;
                    }
                }
                super.keyReleased(e);
            }
        });

        setLocationRelativeTo(null); // vycentrování okna

        renderer = new Renderer(img); // objekt renderování

        // obnovovací funkce
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                draw();
            }
        }, 100, FPS);

    }

    private void draw() {
        if (!fillMode) { // vyčištění plátna - jen v případě tvorby nových objektů
            img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight()); //clear
        }

        // vykreslování úsečky
        if (startLineX != 0 && startLineY != 0){
            renderer.lineTrivial(startLineX, startLineY, x, y);
        }
        if (drawable != null){
            drawable.draw(renderer);
        }

        for (Drawable drawable : drawables) {
            drawable.draw(renderer); // vykreslování všech těles
        }

        panel.getGraphics().drawImage(img, 0, 0, null);
        panel.paintComponents(getGraphics());
    }

}
