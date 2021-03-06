package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.UI;
import com.mygdx.game.Screens.PlayScreen;


public class Brick extends TileObjects {
    public Brick(World world, TiledMap map, Rectangle bounds, PlayScreen screen, MapObject object){
        super(world,map,bounds,screen,object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHit(Mario mario) {

    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            Gdx.app.log("Brick", "Hit");
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            UI.addScore(200);
        }
    }
}
