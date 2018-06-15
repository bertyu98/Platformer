package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Timer;

import java.util.TimerTask;

public class QueuedAction extends TimerTask {
    public Body b2body;

    QueuedAction(Body b2body){
        this.b2body = b2body;
    }

    @Override
    public void run() {
        b2body.applyLinearImpulse(new Vector2(0, 2f), b2body.getWorldCenter(), false);


    }
}
