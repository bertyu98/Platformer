package com.mygdx.game.Sprites;

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

public class Mario extends Sprite {
    public enum State{FALLING,JUMPING,STANDING,RUNNING};

    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation <TextureRegion> marioRun;
    private Animation <TextureRegion> marioJump;

    private boolean runningRight;
    private float stateTimer;


    public Mario(World world, PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(),i * 16,0,16,16));
        }
        marioRun = new Animation <TextureRegion>(0.1f,frames);
        frames.clear();

        for(int i = 4; i < 6; i++){
            frames.add(new TextureRegion(getTexture(),i*16,0,16,16));
        }
        marioJump = new Animation <TextureRegion>(0.1f,frames);


        defineMario();
        marioStand = new TextureRegion(getTexture(),0,0,16,16);
        setBounds(0,0,16/MarioBros.ppm,16/MarioBros.ppm);
        setRegion(marioStand);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer,true);
                break;
            default:
                region = marioStand;
                break;
        }
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        }
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        if(b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING){
            return State.JUMPING;
        }

        else if(b2body.getLinearVelocity().y < 0){
            return State.FALLING;
        }

        else if(b2body.getLinearVelocity().x != 0){
            return State.RUNNING;
        }

        else {
            return State.STANDING;
        }

    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(3640/MarioBros.ppm,32/MarioBros.ppm);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //PolygonShape shape = new PolygonShape();
        shape.setRadius(6/MarioBros.ppm);
        //shape.setAsBox(16/2/MarioBros.ppm,16/2/MarioBros.ppm);

        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
        |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.ENEMY_HEAD_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.ppm,6/MarioBros.ppm),new Vector2(2/MarioBros.ppm,6/MarioBros.ppm));
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData("head");

    }
}
