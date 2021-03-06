package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Weapon.Fireball;

public class Goomba extends Enemies {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroy;



    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Goomba"),i*16,0,16,16));
        }
        walkAnimation = new Animation<TextureRegion>(0.4f,frames);
        stateTime = 0;
        setBounds(getX(),getY(),16/MarioBros.ppm,16/MarioBros.ppm);
        setToDestroy = false;
        destroy = false;
    }

    public void update(float dt){
        stateTime += dt;
        if(setToDestroy && !destroy){
            world.destroyBody(b2body);
            b2body = null;
            destroy = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("Goomba"),32,0,16,16));
            stateTime = 0;

        }
        else if(!destroy) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(),getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //PolygonShape shape = new PolygonShape();
        shape.setRadius(6/MarioBros.ppm);
        //shape.setAsBox(16/2/MarioBros.ppm,16/2/MarioBros.ppm);

        fDef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
        |MarioBros.ENEMY_BIT|MarioBros.OBJECT_BIT|MarioBros.MARIO_BIT|MarioBros.FIREBALL_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        //head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5,8).scl(1/MarioBros.ppm);
        vertices[1] = new Vector2(5,8).scl(1/MarioBros.ppm);
        vertices[2] = new Vector2(-3,3).scl(1/MarioBros.ppm);
        vertices[3] = new Vector2(3,3).scl(1/MarioBros.ppm);
        head.set(vertices);

        fDef.shape = head;
        fDef.restitution = 0.5f;
        fDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void hitonHead(Mario mario) {
        setToDestroy = true;
    }

    @Override
    public void hitByFire(Fireball fireball) {
        setToDestroy = true;
    }

    public void draw(Batch batch){
        if(!destroy || stateTime < 1){
            super.draw(batch);
        }
    }

    public void onEnemyHit(Enemies enemies){
        if(enemies instanceof Turtle && ((Turtle) enemies).currentState == Turtle.State.MOVING_SHELL){
            setToDestroy = true;
        }
        else if(enemies instanceof BuzzyBeetle && ((BuzzyBeetle) enemies).currentState == BuzzyBeetle.State.MOVING_SHELL){
            setToDestroy = true;
        }
        else{
            reverseVelocity(true,false);
        }
    }

}
