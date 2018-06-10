package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;

public abstract class Items extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroy;
    protected Body body;

    public Items(PlayScreen screen,float x,float y){
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x,y);
        setBounds(getX(),getY(),16/ MarioBros.ppm,16/MarioBros.ppm);
        defineItem();
        toDestroy = false;
        destroy = false;
    }

    public abstract void defineItem();

    public abstract void use(Mario mario);

    public void draw(Batch batch){
        if(!destroy){
            super.draw(batch);
        }

    }

    public void update(float dt){
        if(toDestroy && !destroy){
            world.destroyBody(body);
            destroy = true;
        }
    }

    public void destroy(){
        toDestroy = true;
    }

    public void reverseVelocity(boolean x,boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }
}
