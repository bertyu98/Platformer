package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Weapon.Fireball;

public abstract class Enemies extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;

    public Enemies(PlayScreen screen,float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(1,-2);
        b2body.setActive(false);
    }
    public abstract void onEnemyHit(Enemies enemies);
    protected abstract void defineEnemy();

    public abstract void hitonHead(Mario mario);

    public abstract void hitByFire(Fireball fireball);

    public void reverseVelocity(boolean x,boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y){
            velocity.y = -velocity.y;
        }
    }

    public abstract void update(float dt);
}
