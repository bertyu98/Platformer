package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Sprites.Enemies;
import com.mygdx.game.Tools.WorldContact;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.UI;
import com.mygdx.game.Sprites.Brick;
import com.mygdx.game.Sprites.Coin;
import com.mygdx.game.Sprites.Goomba;
import com.mygdx.game.Sprites.Mario;


public class PlayScreen implements Screen {

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private MarioBros game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;

    private UI ui;

    private World world;
    private Box2DDebugRenderer b2dr;


    private Mario player;
    //temporary
    Array<Goomba> goombas;

    private TextureAtlas atlas;

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public void handleInput(float dt){
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            player.b2body.applyLinearImpulse(new Vector2(0,4f),player.b2body.getWorldCenter(),true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2){
            player.b2body.applyLinearImpulse(new Vector2(0.1f,0),player.b2body.getWorldCenter(),true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f,0),player.b2body.getWorldCenter(),true);
        }
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f,6,2);

        player.update(dt);
        for(Enemies enemy:getGoombas()){
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224/MarioBros.ppm){
                enemy.b2body.setActive(true);
            }
        }

        ui.update(dt);

        gameCam.position.x = player.b2body.getPosition().x;

        gameCam.update();
        renderer.setView(gameCam);

    }

    public PlayScreen(MarioBros game){

        atlas = new TextureAtlas("Mario_and_Enemies.pack");


        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH/MarioBros.ppm,MarioBros.V_HEIGHT/MarioBros.ppm,gameCam);

        ui = new UI(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1Custom.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,1/MarioBros.ppm);
        gameCam.position.set(gamePort.getWorldWidth()/2,gamePort.getWorldHeight()/2,0);


        world = new World(new Vector2(0,-10),true);
        b2dr = new Box2DDebugRenderer();
        player = new Mario(world,this);

        BodyDef bDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        //goomba = new Goomba(this,3000/MarioBros.ppm,32/MarioBros.ppm);


        //ground
        for(MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rect.getX() + rect.getWidth()/2)/MarioBros.ppm,(rect.getY() + rect.getHeight()/2)/MarioBros.ppm);

            body = world.createBody(bDef);

            shape.setAsBox(rect.getWidth()/2/MarioBros.ppm,rect.getHeight()/2/MarioBros.ppm);
            fDef.shape = shape;
            body.createFixture(fDef);

        }
        //coins
        for(MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Coin(world,map,rect);

        }

        //bricks
        for(MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(world,map,rect);

        }

        //pipes
        for(MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rect.getX() + rect.getWidth()/2)/MarioBros.ppm,(rect.getY() + rect.getHeight()/2)/MarioBros.ppm);
            body = world.createBody(bDef);

            shape.setAsBox(rect.getWidth()/2/MarioBros.ppm,rect.getHeight()/2/MarioBros.ppm);
            fDef.shape = shape;
            fDef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fDef);

            world.setContactListener(new WorldContact());

        }
        //create goombas
        goombas = new Array<Goomba>();
        for(MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(this,rect.getX()/MarioBros.ppm,rect.getY()/MarioBros.ppm));
        }
    }

    public Array<Goomba>getGoombas(){
        return goombas;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        game.batch.setProjectionMatrix(ui.stage.getCamera().combined);
        ui.stage.draw();

        b2dr.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemies enemy:getGoombas()){
            enemy.draw(game.batch);
        }
        game.batch.end();


    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        ui.dispose();
    }
}
