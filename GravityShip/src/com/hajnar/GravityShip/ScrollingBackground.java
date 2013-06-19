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

package com.hajnar.GravityShip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ScrollingBackground {
	
	private OrthographicCamera camera;
	private Vector2 [] textureCoords;
	private float x;
	private float y;
	private int textureWidth;
	private SpriteBatch batch;
	private TextureRegion textureRegion;
	private int tilesCountX;
	private int tilesCountY;

	public ScrollingBackground(OrthographicCamera camera, Texture texture, SpriteBatch batch)
	{
		if (texture.getWidth() == 256)
		{
			this.tilesCountX = 6;
			this.tilesCountY = 4;
		}
		if (texture.getWidth() == 512)
		{
			this.tilesCountX = 5;
			this.tilesCountY = 4;
		}
		this.textureRegion = new TextureRegion(texture);
		this.batch = batch;
		this.textureCoords = new Vector2[tilesCountX*tilesCountY];
		this.camera = camera;
		this.textureWidth = texture.getWidth();
		this.x = camera.frustum.planePoints[0].x;
		this.y = camera.frustum.planePoints[0].y;
        int index = 0;
        for (int i = 0; i < tilesCountY; i++)
        {
            for (int j = 0; j < tilesCountX; j++)
            {
                textureCoords[index] = new Vector2(x + (j * textureWidth), y + (i * textureWidth));
                index++;
            }
        }
	}	
	
	public ScrollingBackground(OrthographicCamera camera, TextureRegion textureRegion, SpriteBatch batch)
	{
		if (textureRegion.getRegionWidth() == 256)
		{
			this.tilesCountX = 6;
			this.tilesCountY = 4;
		}
		if (textureRegion.getRegionWidth() == 512)
		{
			this.tilesCountX = 5;
			this.tilesCountY = 4;
		}
		this.textureRegion = textureRegion;
		this.batch = batch;
		this.textureCoords = new Vector2[tilesCountX*tilesCountY];
		this.camera = camera;
		this.textureWidth = textureRegion.getRegionWidth();
		this.x = camera.frustum.planePoints[0].x;
		this.y = camera.frustum.planePoints[0].y;
        int index = 0;
        for (int i = 0; i < tilesCountY; i++)
        {
            for (int j = 0; j < tilesCountX; j++)
            {
                textureCoords[index] = new Vector2(x + (j * textureWidth), y + (i * textureWidth));
                index++;
            }
        }
	}	
	
	public void updateBackground()
	{	
		
	    for (int i = 0; i < textureCoords.length; i++)
	    {
	        if (textureCoords[i].x < camera.frustum.planePoints[0].x - textureWidth)
	        {
	            textureCoords[i].set(textureCoords[i].x + (tilesCountX)*textureWidth, textureCoords[i].y);
	        }
	        else if (textureCoords[i].x > camera.frustum.planePoints[1].x)
	        {
	            textureCoords[i].set(textureCoords[i].x - (tilesCountX)*textureWidth, textureCoords[i].y);
	        }
	        else if (textureCoords[i].y < camera.frustum.planePoints[0].y - textureWidth)
	        {
	            textureCoords[i].set(textureCoords[i].x, textureCoords[i].y + (tilesCountY)*textureWidth);
	        }
	        else if (textureCoords[i].y > camera.frustum.planePoints[3].y)
	        {
	            textureCoords[i].set(textureCoords[i].x, textureCoords[i].y - (tilesCountY)*textureWidth);
	        }
	        textureCoords[i].set((float)(textureCoords[i].x), (float)(textureCoords[i].y));
	    }
	}
	
	public void render()
	{
		for (int i = 0; i < tilesCountX*tilesCountY; i++) {
			batch.draw(textureRegion, textureCoords[i].x,  textureCoords[i].y);
			}
	}

}
