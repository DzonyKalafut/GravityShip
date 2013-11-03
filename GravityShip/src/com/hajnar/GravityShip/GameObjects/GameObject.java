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

package com.hajnar.GravityShip.GameObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class GameObject {

    public static final int OBJECT_TYPE_PLAYER = 0;
    public static final int OBJECT_TYPE_TERRAIN = 1;
    public static final int OBJECT_TYPE_LANDZONE = 2;
    public static final int OBJECT_TYPE_CANON = 3;
    public static final int OBJECT_TYPE_BULLET = 4;
    public static final int OBJECT_TYPE_BLACKHOLE = 5;
    public static final int OBJECT_TYPE_STAR = 6;

    private int objectType;

    protected Body objectBody;


    public GameObject(World world, BodyType bodyType, int objectType, float x, float y, float angle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        bodyDef.angle = angle;

        objectBody = world.createBody(bodyDef);

        this.objectType = objectType;
    }

    public Body getBody() {
        return objectBody;
    }

    public int getSubType() {
        return 0;
    }

    public int getObjectType() {
        return objectType;
    }
}
