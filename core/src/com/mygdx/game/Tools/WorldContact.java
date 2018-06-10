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

public class WorldContact implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        if(fixA.getUserData() == "head" || fixB.getUserData() == "head"){
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if(object.getUserData() instanceof Items){
                ((Items) object.getUserData()).onHeadHit();
            }
        }

        if(cDef == (MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                ((Enemies)fixA.getUserData()).hitonHead();
            }
            else if (fixB.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT){
                ((Enemies)fixB.getUserData()).hitonHead();
            }
        }
        if(cDef == (MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT)){
            if(fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT){
                ((Enemies)fixA.getUserData()).reverseVelocity(true,false);
            }
            else {
                ((Enemies)fixB.getUserData()).reverseVelocity(true,false);
            }
        }
        if(cDef == (MarioBros.MARIO_BIT| MarioBros.ENEMY_BIT)){
            Gdx.app.log("Mario","Died");
        }
        if(cDef ==(MarioBros.ENEMY_BIT|MarioBros.ENEMY_BIT)){
            ((Enemies)fixA.getUserData()).reverseVelocity(true,false);
            ((Enemies)fixB.getUserData()).reverseVelocity(true,false);
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
