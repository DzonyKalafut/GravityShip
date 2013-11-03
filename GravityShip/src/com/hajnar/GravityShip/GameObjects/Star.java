package com.hajnar.GravityShip.GameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.hajnar.GravityShip.Assets;
import com.hajnar.GravityShip.Helper;

public class Star extends GameObject {

    private Sprite sprite;
    private boolean rotDirection;
    private boolean pickedUp;


    public Star(World world, float x, float y) {
        super(world, BodyType.StaticBody, GameObject.OBJECT_TYPE_STAR, x, y, 0);

        sprite = new Sprite(Assets.starRegion);
        sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth() / 2,
                objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight() / 2);

        Shape circle = new CircleShape();
        circle.setRadius(0.32f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;

        objectBody.createFixture(fixtureDef);
        circle.dispose();
        objectBody.setUserData(this);

        rotDirection = true;
        pickedUp = false;
    }

    public void update() {
        if (!pickedUp) {
            if (sprite.getRotation() > 10 || sprite.getRotation() < -10)
                rotDirection ^= true; //otoci smer rotacie
            if (rotDirection)
                sprite.rotate(2);
            else
                sprite.rotate(-2);

            sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth() / 2,
                    objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight() / 2);
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void pickUp() {
        pickedUp = true;
    }

    public void dropDown() {
        pickedUp = false;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }
}
