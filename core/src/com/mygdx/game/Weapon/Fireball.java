package com.mygdx.game.Weapon;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Enemies;
import com.mygdx.game.Sprites.Goomba;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.Sprites.Turtle;

import java.util.Iterator;

public class Fireball extends Sprite {

    PlayScreen screen;
    World world;
    Array<TextureRegion> frames;
    Animation <TextureRegion> fireAnimation;
    float stateTime;
    boolean destroyed;
    boolean setToDestroy;
    boolean fireRight;

    Body b2body;
    public Fireball(PlayScreen screen, float x, float y, boolean fireRight){
        this.fireRight = fireRight;
        this.screen = screen;
        this.world = screen.getWorld();
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("FireBall"),  0, 0, 8, 8));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("FireBall"),  8, 0, 8, 8));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("FireBall"),  0, 8, 8, 8));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("FireBall"),  8, 8, 8, 8));

        fireAnimation = new Animation<TextureRegion>(0.2f, frames);
        setRegion(fireAnimation .getKeyFrame(0));
        setBounds(x, y, 6 / MarioBros.ppm, 6  / MarioBros.ppm);
        defineFireBall();
    }

    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(fireRight ? getX() + 12 /MarioBros.ppm : getX() - 12 /MarioBros.ppm, getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        if(!world.isLocked())
            b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / MarioBros.ppm);
        fdef.filter.categoryBits = MarioBros.FIREBALL_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT;

        fdef.shape = shape;
        fdef.restitution = 1;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2(fireRight ? 4 : -4, 2.5f));
    }

    public void update(float dt){
        stateTime += dt;
        setRegion(fireAnimation.getKeyFrame(stateTime, true));
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if((stateTime > 3|| setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            b2body = null;


            /*for (Iterator<Body> iter = world.getBodies(); iter.hasNext();) {
                Body body = iter.next();
                if(body!=null) {
                    Fireball data = (Fireball) body.getUserData();
                    if(setToDestroy) {
                        world.destroyBody(body);
                        body.setUserData(null);
                        body = null;
                    }
                }*/
            destroyed = true;
        }
        else if(b2body.getLinearVelocity().y > 2f && b2body != null)
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);
        else if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0) && b2body != null)
            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public void kill(Enemies enemy){
        if(enemy instanceof Turtle){

        }
        else if(enemy instanceof Goomba){

        }
        else{
            setToDestroy();
        }

    }
}
