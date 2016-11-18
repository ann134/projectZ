package pac2Graphics;


import org.dyn4j.dynamics.Body;

public abstract class GameObject {
    protected Body body;

    public abstract void draw(Canvas canvas, int frame);

    public void drawDebug(Canvas canvas) {
        //get body, draw all fixtures inside
    }

    public Body getBody() {
        return body;
    }
}
