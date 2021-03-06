package ru.spbu.arts.game.unknown;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tree extends GameObject {

    private BufferedImage tre;

    // для физического тела
    private double w;
    private double h;

    // для рисовашек
    private double W;
    private double H;

    public Tree(double w, double h) throws IOException {
        this.W = w;
        this.H = h;

        this.w = W/4;
        this.h = H;

        tre = ImageIO.read(new File("images/tree.png"));

        body = new Body();
        org.dyn4j.geometry.Rectangle r = new org.dyn4j.geometry.Rectangle(this.w, this.h);
//        body.addFixture(heroShape, BodyFixture.DEFAULT_DENSITY, 10, BodyFixture.DEFAULT_RESTITUTION);

        body.addFixture(r, 2, BodyFixture.DEFAULT_FRICTION, 0.2);

        /*r.translate();*/
        body.setMass(MassType.NORMAL);

    }

    public void draw(ru.spbu.arts.game.unknown.Canvas canvas, int frame) {
        canvas.drawImage(tre, -W / 2.0, H / 2.0, W, H);
    }

    public void drawDebug(ru.spbu.arts.game.unknown.Canvas canvas){
        canvas.setColor(new Color(46, 133, 24));

        canvas.drawLine(-w / 2, h / 2, w / 2, h / 2);
        canvas.drawLine(-w / 2, -h / 2, w / 2, -h / 2);

        canvas.drawLine(-w / 2, h / 2, -w / 2, -h / 2);
        canvas.drawLine(w / 2, h / 2, w / 2, -h / 2);
    }
}
