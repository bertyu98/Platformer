package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.UI;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Weapon.Fireball;

public class Mario extends Sprite {
    public enum State{FALLING,JUMPING,STANDING,RUNNING,GROWING,TRANSFORM,DEAD}

    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private TextureRegion marioDead;
    private Animation <TextureRegion> marioRun;
    private TextureRegion marioJump;

    public boolean runningRight;
    private float stateTimer;

    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToDefineFireMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;

    private boolean marioIsFire;
    private boolean runFireTransformAnimation;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private TextureRegion fireMarioStand;
    private TextureRegion fireMarioJump;
    private Animation<TextureRegion> fireMarioRun;
    private Animation<TextureRegion> fireMarioTransform;

    public static Array<Fireball> fireballs;
    private PlayScreen screen;



    public Mario(World world, PlayScreen screen){

        this.screen = screen;
        this.world = world;

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario_small"),i * 16,0,16,16));
        }
        marioRun = new Animation <TextureRegion>(0.1f,frames);

        frames.clear();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario_big"),i * 16,0,16,32));

        }
        bigMarioRun = new Animation <TextureRegion>(0.1f,frames);
        frames.clear();


        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("FireMario"), 16 * i, 0, 16, 32));
        }
        fireMarioRun = new Animation <TextureRegion>(0.1f,frames);
        frames.clear();

        /*frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        growMario = new Animation<TextureRegion>(0.2f,frames);*/
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario_big"), 16 * 15, 0, 16, 32));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Mario_big"), 0, 0, 16, 32));
        }
        growMario = new Animation<TextureRegion>(0.1f, frames);

        frames.clear();
        for (int i = 16; i < 19; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("FireMario"), 16 * i, 0, 16, 32));
        }
        fireMarioTransform = new Animation<TextureRegion>(0.1f,frames);

        marioJump = new TextureRegion(screen.getAtlas().findRegion("Mario_small"),80,0,16,16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("Mario_big"),80,0,16,32);
        fireMarioJump = new TextureRegion(screen.getAtlas().findRegion("FireMario"),80,0,16,32);

        defineMario();

        marioStand = new TextureRegion(screen.getAtlas().findRegion("Mario_small"),0,0,16,16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("Mario_big"),0,0,16,32);
        fireMarioStand = new TextureRegion(screen.getAtlas().findRegion("FireMario"),0,0,16,32);

        marioDead = new TextureRegion(screen.getAtlas().findRegion("Mario_small"),96,0,16,16);

        setBounds(0,0,16/MarioBros.ppm,16/MarioBros.ppm);
        setRegion(marioStand);

        fireballs = new Array<Fireball>();

    }

    public void update(float dt){
        if(screen.getUi().isTimeUp() && !isDead()){
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);

        }
        else{

        }

        if(marioIsBig){
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2 - 6/MarioBros.ppm);

        }
        else if(marioIsFire){
            setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2 - 6/MarioBros.ppm);
        }
        else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
        if (timeToDefineBigMario) {
            defineBigMario();
        }
        if(timeToDefineFireMario){
            defineFireMario();
        }
        if(timeToRedefineMario){
            redefineMario();
        }


        for(Fireball ball:fireballs){
            ball.update(dt);
            if(ball.isDestroyed()){
                fireballs.removeValue(ball,true);
            }
        }
    }

    public void redefineMario(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //PolygonShape shape = new PolygonShape();
        shape.setRadius(6/MarioBros.ppm);
        //shape.setAsBox(16/2/MarioBros.ppm,16/2/MarioBros.ppm);

        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.ENEMY_HEAD_BIT|MarioBros.ITEM_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.ppm,6/MarioBros.ppm),new Vector2(2/MarioBros.ppm,6/MarioBros.ppm));
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);

        timeToRedefineMario = false;

    }

    public void defineFireMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);
        b2body = null;

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10/MarioBros.ppm));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //PolygonShape shape = new PolygonShape();
        shape.setRadius(6/MarioBros.ppm);
        //shape.setAsBox(16/2/MarioBros.ppm,16/2/MarioBros.ppm);

        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.ENEMY_HEAD_BIT|MarioBros.ITEM_BIT
        |MarioBros.FLAG_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);
        shape.setPosition(new Vector2(0,-14/MarioBros.ppm));
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.ppm,6/MarioBros.ppm),new Vector2(2/MarioBros.ppm,6/MarioBros.ppm));
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);

        timeToDefineFireMario = false;

    }

    public void defineBigMario(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10/MarioBros.ppm));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        //PolygonShape shape = new PolygonShape();
        shape.setRadius(6/MarioBros.ppm);
        //shape.setAsBox(16/2/MarioBros.ppm,16/2/MarioBros.ppm);

        fDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fDef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT
                |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.ENEMY_HEAD_BIT|MarioBros.ITEM_BIT|MarioBros.FLAG_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);
        shape.setPosition(new Vector2(0,-14/MarioBros.ppm));
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.ppm,6/MarioBros.ppm),new Vector2(2/MarioBros.ppm,6/MarioBros.ppm));
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);

        timeToDefineBigMario = false;


    }

    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case TRANSFORM:
                region = fireMarioTransform.getKeyFrame(stateTimer);
                if(fireMarioTransform.isAnimationFinished(stateTimer)) {
                    runFireTransformAnimation = false;
                }
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                if(marioIsBig){
                    region = bigMarioJump;
                }
                else if(marioIsFire){
                    region = fireMarioJump;
                }
                else{
                    region = marioJump;
                }
                break;
            case RUNNING:
                if(marioIsBig){
                    region = bigMarioRun.getKeyFrame(stateTimer,true);
                }
                else if(marioIsFire){
                    region = fireMarioRun.getKeyFrame(stateTimer,true);
                }
                else{
                    region = marioRun.getKeyFrame(stateTimer,true);
                }
                break;
            default:
                if(marioIsBig){
                    region = bigMarioStand;
                }
                else if(marioIsFire){
                    region = fireMarioStand;
                }
                else{
                    region = marioStand;
                }
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
        if(marioIsDead){
            return State.DEAD;
        }
        else if(runFireTransformAnimation){
            return State.TRANSFORM;
        }
        else if(runGrowAnimation){
            return State.GROWING;
        }

        else if(b2body.getLinearVelocity().y > 0 || b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING){
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
        |MarioBros.OBJECT_BIT|MarioBros.ENEMY_BIT|MarioBros.ENEMY_HEAD_BIT|MarioBros.ITEM_BIT
        |MarioBros.FLAG_BIT;

        fDef.shape = shape;
        b2body.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/MarioBros.ppm,6/MarioBros.ppm),new Vector2(2/MarioBros.ppm,6/MarioBros.ppm));
        fDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fDef.shape = head;
        fDef.isSensor = true;

        b2body.createFixture(fDef).setUserData(this);

    }

    public void fireTransform(){
        if(!isBig() && !isFire()) {
            runFireTransformAnimation = true;
            marioIsFire = true;
            timeToDefineFireMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight()*2);
        }
        else if(isBig() && !isFire()){
            runFireTransformAnimation = false;
            marioIsFire = false;
        }
        else if(!isBig() && isFire()){
            runFireTransformAnimation = false;
            marioIsFire = true;
        }
    }

    public void grow(){
        if(!isBig() && !isFire()) {
            runGrowAnimation = true;
            marioIsBig = true;
            timeToDefineBigMario = true;
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        }
        else if(isBig() && !isFire()){
            runGrowAnimation = false;
            marioIsBig = true;
        }
        else if(!isBig() && isFire()){
            runGrowAnimation = false;
            marioIsBig = false;
        }
    }

    public void hit(Enemies enemies){
        if(enemies instanceof Turtle && ((Turtle) enemies).currentState == Turtle.State.STANDING_SHELL){
            ((Turtle) enemies).kick(enemies.b2body.getPosition().x > b2body.getPosition().x ? Turtle.KICK_RIGHT_SPEED:Turtle.KICK_LEFT_SPEED);
        }
        else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);

            }
            else if(marioIsFire){
                marioIsFire = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);

            }
            else {
                UI.loseLife();
                if(UI.getLives() == 0) {
                    marioIsDead = true;
                    Filter filter = new Filter();
                    filter.maskBits = MarioBros.NOTHING_BIT;
                    for (Fixture fixture : b2body.getFixtureList()) {
                        fixture.setFilterData(filter);
                    }
                    b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
                }
            }
        }
    }

    public boolean isDead(){
        return  marioIsDead;
    }

    public float getStateTimer(){
        return  stateTimer;
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public boolean isFire(){
        return marioIsFire;
    }

    public void jump(){
        if(currentState != State.JUMPING){
            b2body.applyLinearImpulse(new Vector2(0,4f),b2body.getWorldCenter(),true);
            currentState = State.JUMPING;
        }
    }

    public void fire(){
        fireballs.add(new Fireball(screen,b2body.getPosition().x,
                b2body.getPosition().y , runningRight ? true:false));
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(Fireball ball:fireballs){
            ball.draw(batch);
        }
    }

    public void die(){
        if(!isDead()){
            marioIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

}
