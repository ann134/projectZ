package pac2Graphics;


import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Vector2;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {

    //TODO головоломка: падающее дерево
    //TODO указывать положение объектов с помощью файлов. Аналогично файлам для пола

    private static final boolean DEBUG_MODE = false;
    private String debug = "hello";

    public static final double NANO_TO_BASE = 1.0e9;
    private long last;

    private World world;
    private Camera camera;


    private List<Floor> floors;

    private Kolobok kolobok;
    private Tree tree;
    private InvisibleObject spikes;
    private Branches branches;

    private Hero hero;
    private Greg greg;
    private Beast beast;
    private Light light;


    private AllWorldGameObjects allObjects;
    private List<Vector2> saves = new ArrayList<>();

    public GamePanel() throws IOException {
        camera = new Camera();

        saves.add(new Vector2(20, 6));
        saves.add(new Vector2(80, 6));
        saves.add(new Vector2(250, 6));

        this.initializeWorld(saves.get(2).x, saves.get(2).y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        render(g2d);
    }

    private void initializeWorld(double heroX, double heroY) throws IOException {
        this.world = new World();
        allObjects = new AllWorldGameObjects();

        loadFloor();
        for (Floor floor : floors) {
            allObjects.addObject(floor);
        }

        kolobok = addGameObject(new Kolobok(0.5), 21, 6);
        kolobok.body.applyForce(new Vector2(100.0, 0.0));

        tree = addGameObject(new Tree(1, 14), 64, 10);
        tree.body.rotate(Math.PI / 6);

        spikes = addGameObject(new InvisibleObject(7, 1.5), 85.5, 2);


        greg = addGameObject(new Greg(), 262, 6);
        branches = addGameObject(new Branches(), 262, 5.7);

        beast = addGameObject(new Beast(), 268, 10);
        hero = addGameObject(new Hero(world, allObjects), heroX, heroY);

        light = addGameObject(new Light(), 255, 6);




        //прямо здесь создаем класс без имени, который переопределяет метод collided
        /*listenCollisions(hero, kolobok, new CollidingAction() {
            @Override
            public void collided() {
                System.out.println("kolobok and hero collided " + System.nanoTime());
            }
        });

        listenCollisions(hero, tree, () -> {
            System.out.println("tree and hero collided " + System.nanoTime());
        });*/

        listenCollisions(hero, spikes, () -> {
                    if (hero.alive()) {
                        hero.kill(true);
                        hero.setMovementStart(System.nanoTime());
                    }
                }
        );

        /*WeldJoint joint = new WeldJoint(hero.body, kolobok.body, new Vector2(21, 5));
        world.addJoint(joint);*/
//        world.removeJoint(joint);
    }

    private <T extends GameObject> T addGameObject(T o, double x, double y) throws IOException {
        o.getBody().translate(x, y);
        allObjects.addObject(o);
        world.addBody(o.getBody());
        return o;
    }

    private void loadFloor() throws IOException {
        floors = new ArrayList<>();
        PixelCoords last = null;
        for (int x = 0; x < 10; x++) {
            Floor f = new Floor(x, last);
            last = f.getLastPC();
            floors.add(f);
            world.addBody(f.getBody());
        }
    }

    private void listenCollisions(GameObject o1, GameObject o2, CollidingAction action) {
        world.addListener(new CollisionListener() {
            @Override
            public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2) {
                return true;
            }

            @Override
            public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
                if (body1 == o1.body && body2 == o2.body || body2 == o1.body && body1 == o2.body)
                    action.collided();
                return true;
            }

            @Override
            public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Manifold manifold) {
                return true;
            }

            @Override
            public boolean collision(ContactConstraint contactConstraint) {
                return true;
            }
        });
    }



    public void start() throws IOException {
        this.last = System.nanoTime();

        Timer.start = System.nanoTime();

        Thread thread = new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    gameLoop();
                } catch (IOException e) {
                    System.out.println("relife Ошибка!");
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    private void gameLoop() throws IOException {
        long time = System.nanoTime();
        long diff = time - this.last;

        this.last = time;
        double elapsedTime = diff / NANO_TO_BASE; //сколько секунд прошло с прошлого раза

        if (hero.alive()) {
            hero.move();
            hero.processCarryButtonPress();
        } else {
            if (time - hero.getMovementStart() > 2e9) {
                Vector2 respawnPoint = getRespawnPoint();
                initializeWorld(respawnPoint.x, respawnPoint.y);
            }
        }

        camera.move(hero, elapsedTime);

        repaint();
        this.world.update(elapsedTime);
    }

    private Vector2 getRespawnPoint() {
        double deathPointX = hero.body.getWorldCenter().x;

        Vector2 respawnPoint = new Vector2(0, 0);
        double differenceMin = deathPointX - saves.get(0).x;

        for (Vector2 save : saves) {
            if (save.x < deathPointX) {
                if (deathPointX - save.x < differenceMin) {
                    differenceMin = deathPointX - save.x;
                    respawnPoint = save;
                }
            }
        }
        return respawnPoint;
    }



    private void render(Graphics2D g) {

        //определяем время, прошедшее с момента запуска программы, превратщаем его в номер кадра. номер кадра передается как параметр draw() у всех GameObject
        int frame = Timer.getFrameFrom(Timer.start);
        Canvas canvas = new Canvas(g, camera);

        canvas.resetTransform();
        camera.drawBackground(canvas);


        for (GameObject object: allObjects.getList()){
            canvas.transformBody(object.getBody());
            object.draw(canvas, frame);
            if (DEBUG_MODE) {
                object.drawDebug(canvas);
                setDebug(" coords: " + (int) hero.getBody().getWorldCenter().x + " , " + (int) hero.getBody().getWorldCenter().y); //только для героя
            }
        }

        canvas.kill();

        //рисуем текст дебаг
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(debug, 30, 30);
    }

    private void setDebug(String debug) {
        this.debug = debug;
    }

    public Hero getHero() {
        return hero;
    }
}

//TODO на весь экран
//TODO Проверить разные размеры экранов, для этого отключить режим всего экрана и задать разные размеры окну
//TODO запуск jar или лучше exe файла
//TODO один каталог с картинками, в нем подпапки, которые сейчас
//TODO сделать физическое тело героя уже - ближе к форме тела
