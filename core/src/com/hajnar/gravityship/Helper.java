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

package com.hajnar.gravityship;

import com.badlogic.gdx.Gdx;

public abstract class Helper {

    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 100f;

    public static final float FRUSTUM_WIDTH = 1200;
    public static final float FRUSTUM_HEIGHT = FRUSTUM_WIDTH * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());

    public static float WINDOW_WIDTH = Gdx.graphics.getWidth();
    public static float WINDOW_HEIGHT = Gdx.graphics.getHeight();
}
