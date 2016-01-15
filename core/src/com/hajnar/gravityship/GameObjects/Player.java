/*******************************************************************************
 * Copyright 2013 Ján Hajnár.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.hajnar.gravityship.GameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.hajnar.gravityship.Assets;
import com.hajnar.gravityship.Helper;

public class Player extends GameObject {

    public static final int SHIP_STATE_FLYING = 1;
    public static final int SHIP_STATE_LANDED = 2;
    public static final int SHIP_STATE_DEAD = 3;

    private int state;
    private boolean thrusterState;
    private float rotationRatio;

    private Sprite sprite;

    private float fuelAmmount;
    private float lifeAmmount;

    public Player(World world, float x, float y) {

        super(world, BodyType.DynamicBody, OBJECT_TYPE_PLAYER, x, y, 0);

        sprite = new Sprite(Assets.spaceshipRegion);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 6f;
        fixtureDef.restitution = 0f;

        Assets.objectBodiesLoader.attachFixture(objectBody, "spaceship", fixtureDef, 1.28f);

        objectBody.setUserData(this);
        objectBody.setAngularDamping(9);

        fuelAmmount = 5000;
        lifeAmmount = 100;
        state = SHIP_STATE_FLYING;
        thrusterState = false;
        rotationRatio = 0;
    }

    public Player(World world, float x, float y, int fuelAmmount) {

        super(world, BodyType.DynamicBody, OBJECT_TYPE_PLAYER, x, y, 0);

        sprite = new Sprite(Assets.spaceshipRegion);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 6f;
        fixtureDef.restitution = 0f;

        Assets.objectBodiesLoader.attachFixture(objectBody, "spaceship", fixtureDef, 1.28f);

        objectBody.setUserData(this);
        objectBody.setAngularDamping(9);

        this.fuelAmmount = fuelAmmount;
        lifeAmmount = 100;
        state = SHIP_STATE_FLYING;
        thrusterState = false;
        rotationRatio = 0;
    }


    public void update() {
        sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth() / 2,
                objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(objectBody.getAngle()));

    }

    public void consumeFuel(float delta) {
        fuelAmmount -= delta * 60;
    }

    public void refuel(float delta) {
        fuelAmmount += delta * 60;
    }

    public void kill() {
        lifeAmmount = 0;
        state = SHIP_STATE_DEAD;
    }

    public void doDamage(int ammount) {
        lifeAmmount -= ammount;
        if (lifeAmmount <= 0) {
            state = SHIP_STATE_DEAD;
            thrusterState = false;
            lifeAmmount = 0;
        }

    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getFuelAmmount() {
        return fuelAmmount;
    }

    public float getLifeAmmount() {
        return lifeAmmount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void thrustOn() {
        thrusterState = true;
    }

    public void thrustOff() {
        thrusterState = false;
    }

    public boolean isThrustEnabled() {
        return thrusterState;
    }


    public float getRotationRatio() {
        return rotationRatio;
    }

    public void setRotationRatio(float rotationRatio) {
        this.rotationRatio = rotationRatio;
    }


}


