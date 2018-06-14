package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;

public class Flag extends TileObjects {
    public Flag(World world, TiledMap map, Rectangle bounds, PlayScreen screen, MapObject object) {
        super(world, map, bounds, screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.FLAG_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {

    }

    public void onHit(Mario mario){
        Gdx.app.log("Level","Completed");
    }
}
