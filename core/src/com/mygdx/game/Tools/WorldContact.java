package com.mygdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Sprites.Enemies;
import com.mygdx.game.Sprites.Items;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.Sprites.TileObjects;

public class WorldContact implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        /*if(fixA.getUserData() == "head" || fixB.getUserData() == "head"){
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if(object.getUserData() instanceof TileObjects){
                ((TileObjects) object.getUserData()).onHeadHit((Mario)fixA.getUserData());
            }
        }*/

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;



        if(cDef == (MarioBros.MARIO_HEAD_BIT|MarioBros.COIN_BIT) || cDef == (MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.MARIO_HEAD_BIT){
                ((TileObjects) fixB.getUserData()).onHeadHit((Mario)fixA.getUserData());
            }
            else{
                ((TileObjects) fixA.getUserData()).onHeadHit((Mario)fixB.getUserData());
            }
        }

        else if(cDef == (MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                ((Enemies)fixA.getUserData()).hitonHead((Mario)fixB.getUserData());
            }
            else if (fixB.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                ((Enemies)fixB.getUserData()).hitonHead((Mario)fixA.getUserData());
            }
        }
        else if(cDef == (MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT){
                ((Enemies)fixA.getUserData()).reverseVelocity(true,false);
            }
            else {
                ((Enemies)fixB.getUserData()).reverseVelocity(true,false);
            }
        }
        else if(cDef == (MarioBros.MARIO_BIT| MarioBros.ENEMY_BIT)){
            Gdx.app.log("Mario","Died");
            if(fixA.getFilterData().categoryBits == MarioBros.MARIO_BIT){
                ((Mario)fixA.getUserData()).hit((Enemies)fixB.getUserData());
            }
            else{
                ((Mario)fixB.getUserData()).hit((Enemies)fixA.getUserData());
            }
        }
        else if(cDef ==(MarioBros.ENEMY_BIT|MarioBros.ENEMY_BIT)){
            ((Enemies)fixA.getUserData()).onEnemyHit((Enemies)fixB.getUserData());
            ((Enemies)fixB.getUserData()).onEnemyHit((Enemies)fixA.getUserData());
        }
        else if(cDef == (MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                ((Items)fixA.getUserData()).reverseVelocity(true,false);
            }
            else {
                ((Items)fixB.getUserData()).reverseVelocity(true,false);
            }
        }
        else if(cDef == (MarioBros.ITEM_BIT | MarioBros.MARIO_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.ITEM_BIT){
                ((Items)fixA.getUserData()).use((Mario)fixB.getUserData());
            }
            else {
                ((Items)fixB.getUserData()).use((Mario)fixA.getUserData());
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
