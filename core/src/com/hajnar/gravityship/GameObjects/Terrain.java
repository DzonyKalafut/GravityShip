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

import aurelienribon.bodyeditor.BodyEditorLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.hajnar.gravityship.Helper;

import java.util.ArrayList;

public class Terrain extends GameObject {

    public ArrayList<Mesh> meshes;
    public ArrayList<BoundingBox> boundingBoxes;
    public static final float TEXTURE_SCALE = 1f / 400f;

    public Terrain(World world, String filename) {
        super(world, BodyType.StaticBody, OBJECT_TYPE_TERRAIN, 0f, 0f, 0);

        FixtureDef fixtureDef = new FixtureDef();

        BodyEditorLoader boxloader = new BodyEditorLoader(Gdx.files.internal(filename));
        boxloader.attachFixture(objectBody, "terrain", fixtureDef, 1);

        objectBody.setUserData(this);

        generateMeshes();
    }

    public void generateMeshes() {
        Array<Fixture> terrainFixtures = objectBody.getFixtureList();
        Vector2 boxVertex = new Vector2();
        meshes = new ArrayList<Mesh>();
        boundingBoxes = new ArrayList<BoundingBox>();
        EarClippingTriangulator ear = new EarClippingTriangulator();

        for (Fixture terrainFixture : terrainFixtures) {
            PolygonShape shape = (PolygonShape) terrainFixture.getShape();
            boxVertex = new Vector2();
            int vc = shape.getVertexCount();
            FloatArray boxVertices = new FloatArray();
            ShortArray triaBoxVertices;

            for (int i = 0; i < vc; i++) {
                shape.getVertex(i, boxVertex);
                boxVertex = objectBody.getWorldPoint(boxVertex).scl(Helper.BOX_TO_WORLD);
                boxVertices.add(boxVertex.x);
                boxVertices.add(boxVertex.y);
            }
            // TODO: fix this to work correctly with the new triangulator
            triaBoxVertices = ear.computeTriangles(boxVertices);
            float[] meshVertices = new float[triaBoxVertices.size * 4];
            short[] meshIndices = new short[triaBoxVertices.size];
            for (int i = 0; i < triaBoxVertices.size; i++) {
                meshVertices[i * 4] = boxVertices.get(2 * triaBoxVertices.get(i));
                meshVertices[i * 4 + 1] = boxVertices.get(2 * triaBoxVertices.get(i) + 1);
                meshVertices[i * 4 + 2] = boxVertices.get(2 * triaBoxVertices.get(i)) * TEXTURE_SCALE;
                meshVertices[i * 4 + 3] = boxVertices.get(2 * triaBoxVertices.get(i) + 1) * TEXTURE_SCALE;
                meshIndices[i] = (short) i;
            }

            Mesh mesh = new Mesh(true, meshVertices.length/4, meshVertices.length/4,
                    new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
            mesh.setVertices(meshVertices);
            mesh.setIndices(meshIndices);
            meshes.add(mesh);
            boundingBoxes.add(mesh.calculateBoundingBox());
        }
    }

    public ArrayList<Mesh> getMeshes() {
        return meshes;
    }

    public ArrayList<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }


}
