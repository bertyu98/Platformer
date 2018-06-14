package com.mygdx.game.Sprites;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;

public abstract class TileObjects {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected PlayScreen screen;
    protected Fixture fixture;
    protected MapObject object;

    public static Integer level = 1;



    public TileObjects(World world, TiledMap map, Rectangle bounds, PlayScreen screen, MapObject object){
        this.world = world;
        this.object = object;
        this.map = map;
        this.bounds = bounds;
        this.screen = screen;
        BodyDef bDef = new BodyDef();
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set((bounds.getX() + bounds.getWidth()/2)/ MarioBros.ppm,(bounds.getY() + bounds.getHeight()/2)/MarioBros.ppm);
        body = world.createBody(bDef);

        shape.setAsBox(bounds.getWidth()/2/MarioBros.ppm,bounds.getHeight()/2/MarioBros.ppm);
        fDef.shape = shape;
        fixture = body.createFixture(fDef);

    }
    public abstract void onHit(Mario mario);
    public abstract void onHeadHit(Mario mario);

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * MarioBros.ppm/16),(int)
                (body.getPosition().y * MarioBros.ppm/16));
    }
}


