package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Weapon.Fireball;

import java.util.TimerTask;

public class Turtle extends Enemies{
    public enum State {WALKING,JUMPING,STANDING_SHELL,MOVING_SHELL,DEAD}
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroy;
    private TextureRegion shell;
    private float deadRotationDegrees;
    int timer = 0;
    int waitingTime = 1;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("Koopa"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("Koopa"),16,0,16,32));
        shell = new TextureRegion(screen.getAtlas().findRegion("Koopa"),64,0,16,32);
        walkAnimation = new Animation<TextureRegion>(0.2f,frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;
        setBounds(getX(),getY(),16/ MarioBros.ppm,24/MarioBros.ppm);

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
        fDef.restitution = 1.8f;
        fDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void hitonHead(Mario mario) {
        if(currentState != State.STANDING_SHELL){
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }
        else{
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    @Override
    public void hitByFire(Fireball fireball) {
        if(currentState != State.STANDING_SHELL){
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }
        else{
            kick(fireball.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState(){
        return currentState;
    }


    @Override
    public void update(float dt) {
        setRegion(getFrames(dt));
        if (currentState == State.STANDING_SHELL && stateTime > 5){
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(b2body.getPosition().x - getWidth()/2,b2body.getPosition().y - 8/MarioBros.ppm);
        if(currentState == State.DEAD){
            deadRotationDegrees +=3;
            rotate(deadRotationDegrees);
            if(stateTime > 5 && !destroy){
                world.destroyBody(b2body);
                b2body = null;
                destroy = true;
            }
        }
        else {
            b2body.setLinearVelocity(velocity);
            /*java.util.Timer timer = new java.util.Timer();
            TimerTask myTask = new TimerTask() {
                @Override
                public void run() {
                    b2body.applyLinearImpulse(new Vector2(0,4f),b2body.getWorldCenter(),true);
                    b2body.setLinearVelocity(new Vector2(1,1));

                }
            };
            timer.schedule(myTask,5000,5000);
            b2body.applyLinearImpulse(new Vector2(0,-8f),b2body.getWorldCenter(),true);
            //java.util.Timer timer = new java.util.Timer();
            //timer.schedule(new QueuedAction(b2body),5000,1);*/



        }
    }

    public TextureRegion getFrames(float dt) {
        TextureRegion region;
        switch(currentState){

            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case JUMPING:
            case WALKING:

            default:
                region = walkAnimation.getKeyFrame(stateTime,true);
                break;

        }
        if(velocity.x > 0 && region.isFlipX() == false){
            region.flip(true,false);
        }
        if(velocity.x < 0 && region.isFlipX() == true){
            region.flip(true,false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    public void onEnemyHit(Enemies enemy){
        if(enemy instanceof  Turtle){
            if(((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }
            else if(currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING){
                return;
            }
            else{
                reverseVelocity(true,false);
            }
        }
        else if(enemy instanceof  BuzzyBeetle){
            if(((BuzzyBeetle) enemy).currentState == BuzzyBeetle.State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }
            else if(currentState == State.MOVING_SHELL && ((BuzzyBeetle) enemy).currentState == BuzzyBeetle.State.WALKING){
                return;
            }
            else{
                reverseVelocity(true,false);
            }

        }
        else if(currentState != State.MOVING_SHELL){
            reverseVelocity(true,false);
        }
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;
        for(Fixture fixture:b2body.getFixtureList()){
            fixture.setFilterData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(0,5f),b2body.getWorldCenter(),true);

    }

    public void draw(Batch batch){
        if(!destroy){
            super.draw(batch);
        }
    }


}
