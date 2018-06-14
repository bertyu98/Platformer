package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.UI;
import com.mygdx.game.Screens.PlayScreen;


public class Coin extends TileObjects {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(World world, TiledMap map, Rectangle bounds, PlayScreen screen, MapObject object){
        super(world,map,bounds,screen,object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHit(Mario mario) {

    }

    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN){

        }
        else {
            if (object.getProperties().containsKey("mushroom")) {
                Gdx.app.log("Coin", "Hit");
                getCell().setTile(tileSet.getTile(BLANK_COIN));

                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.ppm)
                        , Mushroom.class));
                getCell().setTile(tileSet.getTile(BLANK_COIN));
            }
            else if(object.getProperties().containsKey("flower")){
                getCell().setTile(tileSet.getTile(BLANK_COIN));

                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.ppm)
                        , Flower.class));
                getCell().setTile(tileSet.getTile(BLANK_COIN));
            }
            else{
                Gdx.app.log("Coin", "Hit");
                getCell().setTile(tileSet.getTile(BLANK_COIN));

                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.ppm)
                        , CoinObject.class));
                getCell().setTile(tileSet.getTile(BLANK_COIN));
            }


        }
    }
}
