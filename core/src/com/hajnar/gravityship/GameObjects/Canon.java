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

public class Canon extends GameObject {

    private Sprite sprite;

    public Canon(World world, float x, float y, float rotDegrees) {
        super(world, BodyType.StaticBody, OBJECT_TYPE_CANON, x, y, (float) Math.toRadians(rotDegrees));

        sprite = new Sprite(Assets.canonRegion);
        sprite.setPosition(objectBody.getPosition().x * Helper.BOX_TO_WORLD - sprite.getWidth() / 2,
                objectBody.getPosition().y * Helper.BOX_TO_WORLD - sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(objectBody.getAngle()));

        FixtureDef fixtureDef = new FixtureDef();

        Assets.objectBodiesLoader.attachFixture(objectBody, "canon", fixtureDef, 1.28f);

        objectBody.setUserData(this);
    }

    public Sprite getSprite() {
        return sprite;
    }

}
