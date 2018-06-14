package com.mygdx.game.Screens;

import com.badlogic.gdx.Application;
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
import com.mygdx.game.Controller;
import com.mygdx.game.Sprites.BuzzyBeetle;
import com.mygdx.game.Sprites.CoinObject;
import com.mygdx.game.Sprites.Enemies;
import com.mygdx.game.Sprites.Flag;
import com.mygdx.game.Sprites.Flower;
import com.mygdx.game.Sprites.ItemDef;
import com.mygdx.game.Sprites.Items;
import com.mygdx.game.Sprites.Mushroom;
import com.mygdx.game.Sprites.TileObjects;
import com.mygdx.game.Sprites.Turtle;
import com.mygdx.game.Tools.WorldContact;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.UI;
import com.mygdx.game.Sprites.Brick;
import com.mygdx.game.Sprites.Coin;
import com.mygdx.game.Sprites.Goomba;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.Weapon.Fireball;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;


public class PlayScreen implements Screen {

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private MarioBros game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    Controller controller;

    private UI ui;

    public World world;
    private Box2DDebugRenderer b2dr;


    private Mario player;
    //temporary
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;
    private Array<BuzzyBeetle> buzzyBeetles;


    private Array<Items>items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private TextureAtlas atlas;

    public boolean levelCompleted;


    public TextureAtlas getAtlas(){
        return atlas;
    }



    public void handleInput(float dt){
        /*if(player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) /*&& player.isFire() && player.fireTimer > player.fireInterval*/ /*){
                player.fire();

            }*/
            if (controller.isUpPressed() | controller.aPressed()) {
                if ( player.b2body.getLinearVelocity().y == 0)
                    player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            }
            if ( controller.isRightPressed() && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);

            if ( controller.isLeftPressed() && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

            if (controller.bPressed() && player.isFire()) {
                player.fire();
            }
        }



    public void update(float dt){
        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f,6,2);

        player.update(dt);
        for(Enemies enemy:getEnemies()){
            enemy.update(dt);
            if(enemy.getX() > player.getX() - 224/MarioBros.ppm && enemy.b2body != null){
                enemy.b2body.setActive(true);
            }
        }

        for(Items item:items){
            item.update(dt);
        }


        ui.update(dt);

        if(player.currentState != Mario.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;
        }
        gameCam.update();
        renderer.setView(gameCam);

    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        if(player.currentState == Mario.State.FALLING && player.b2body.getLinearVelocity().y < -5){
            return true;
        }
        return false;
    }

    public PlayScreen(MarioBros game){

        atlas = new TextureAtlas("Mario_and_Enemies.pack");


        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH/MarioBros.ppm,MarioBros.V_HEIGHT/MarioBros.ppm,gameCam);

        ui = new UI(game.batch);

        if(TileObjects.level == 1) {
            mapLoader = new TmxMapLoader();
            map = mapLoader.load("level1Custom.tmx");
        }
        else if(TileObjects.level == 2){
            mapLoader = new TmxMapLoader();
            map = mapLoader.load("level2.tmx");
        }
        else{
            mapLoader = new TmxMapLoader();
            map = mapLoader.load("level3.tmx");
        }
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
        //flag
        for(MapObject object: map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Flag(world,map,rect,this,object);
        }

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
            new Coin(world,map,rect,this,object);

        }

        //bricks
        for(MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(world,map,rect,this,object);

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

        //create turtles
        turtles = new Array<Turtle>();
        for(MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(this,rect.getX()/MarioBros.ppm,rect.getY()/MarioBros.ppm));
        }

        buzzyBeetles = new Array<BuzzyBeetle>();
        for(MapObject object: map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            buzzyBeetles.add(new BuzzyBeetle(this,rect.getX()/MarioBros.ppm,rect.getY()/MarioBros.ppm));
        }

        controller = new Controller(game.batch);

        items = new Array<Items>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();


    }

    public Array<Enemies> getEnemies(){
        Array<Enemies> enemies = new Array<Enemies>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        enemies.addAll(buzzyBeetles);
        return enemies;
    }



    public void spawnItem(ItemDef itemDef){
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef itemDef = itemsToSpawn.poll();
            if(itemDef.type == Mushroom.class){
                items.add(new Mushroom(this,itemDef.position.x,itemDef.position.y));
            }
            else if(itemDef.type == CoinObject.class){
                items.add(new CoinObject(this,itemDef.position.x,itemDef.position.y));
            }
            else{
                items.add(new Flower(this,itemDef.position.x,itemDef.position.y));
            }
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

        if(Gdx.app.getType() == Application.ApplicationType.Android)
        {
            controller.draw();
        }


        game.batch.setProjectionMatrix(ui.stage.getCamera().combined);
        ui.stage.draw();

        b2dr.render(world,gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemies enemy:getEnemies()){
            enemy.draw(game.batch);
        }

        for(Items item:items){
            item.draw(game.batch);
        }


        game.batch.end();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();

        }

        if(TileObjects.level == 2 && levelCompleted){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        if(TileObjects.level == 3 && levelCompleted) {
            game.setScreen(new GameOverScreen(game));
            dispose();

        }

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

    public UI getUi(){
        return ui;
    }

}
