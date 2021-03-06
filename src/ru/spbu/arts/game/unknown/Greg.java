package ru.spbu.arts.game.unknown;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Greg extends GameObject {

    public final static double W = 1;
    public final static double H = 1.75;

    private BufferedImage greg;

    private long movementStart;

    public Greg () throws IOException {

        greg  = ImageIO.read(new File("images/greg/greg.png"));

        body = new Body();
        Rectangle beastShape = new Rectangle(W, H);
        body.addFixture(beastShape);

        body.setMass(MassType.INFINITE);
    }

    public void draw(ru.spbu.arts.game.unknown.Canvas canvas, int frameWhy) {

        /*int frame = Timer.getFrameFrom(movementStart); // вычисляем какой кадр с начала движения (глобальный кадр?)

        //BufferedImage[] wirtsArray = getWirtsArray(); //вибираем нужный массив

        BufferedImage greg = gregs[frame % gregs.length]; // делим на длинну массива, т е на число отрисованных повторяющихся кадров (локальный кадр?)*/

        canvas.drawImage(greg, -W / 2, H / 2, W, H);
    }



    public void drawDebug(ru.spbu.arts.game.unknown.Canvas canvas) {
        canvas.setColor(new Color(46, 133, 24));

        canvas.drawLine(-W / 2, H / 2, W / 2, H / 2);
        canvas.drawLine(-W / 2, -H / 2, W / 2, -H / 2);

        canvas.drawLine(-W / 2, H / 2, -W / 2, -H / 2);
        canvas.drawLine(W / 2, H / 2, W / 2, -H / 2);
    }

    /*public long getMovementStart() {
        return movementStart;
    }
    public void setMovementStart(long movementStart) {
        this.movementStart = movementStart;
    }*/
}
