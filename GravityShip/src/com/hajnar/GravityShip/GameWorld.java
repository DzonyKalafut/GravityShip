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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.hajnar.GravityShip.GameObjects.*;

import java.util.ArrayList;
import java.util.Iterator;

public class GameWorld {

    public static final int WORLD_RUNNING = 1;
    public static final int WORLD_PAUSED = 2;
    public static final int WORLD_NEXT_LEVEL = 3;
    public static final int WORLD_GAME_OVER = 4;

    private World box2dWorld;
    private int state;
    private int numOfStars;
    private int numOfCollectedStars;

    private Player player;
    private Terrain terrain;
    private ArrayList<LandingZone> landingZones;
    private ArrayList<Canon> canons;
    private ArrayList<Bullet> bullets;
    private ArrayList<BlackHole> blackHoles;
    private ArrayList<Star> stars;
    private ArrayList<GameObject> objectsToDestroy;

    private ParticleEffect thrustParticleEffect;
    private Array<ParticleEmitter> thrustParticleEmitters;
    private ParticleEffect explosionParticleEffect;
    private Array<ParticleEmitter> explosionParticleEmitters;
    private ParticleEffect starsParticleEffect;
    private Array<ParticleEmitter> starsParticleEmitters;

    private CollisionProcessor contactListener;
    private GameObject[] collidingObjects;

    private int landedZoneType;
    private float landedDuration;
    private float firePauseDuration;
    private int startFuelAmmount;
    private float canonsFireInterval;

    private Vector2 tmpVector;


    public GameWorld() {
        state = WORLD_RUNNING;

        thrustParticleEffect = new ParticleEffect();
        thrustParticleEffect.load(Gdx.files.internal("data/thrust.p"), Gdx.files.internal("data"));
        thrustParticleEmitters = thrustParticleEffect.getEmitters();
        explosionParticleEffect = new ParticleEffect();
        explosionParticleEffect.load(Gdx.files.internal("data/explossion.p"), Gdx.files.internal("data"));
        explosionParticleEmitters = explosionParticleEffect.getEmitters();
        starsParticleEffect = new ParticleEffect();
        starsParticleEffect.load(Gdx.files.internal("data/stars.p"), Gdx.files.internal("data"));
        starsParticleEmitters = starsParticleEffect.getEmitters();

        landingZones = new ArrayList<LandingZone>();
        canons = new ArrayList<Canon>();
        bullets = new ArrayList<Bullet>();
        blackHoles = new ArrayList<BlackHole>();
        stars = new ArrayList<Star>();
        objectsToDestroy = new ArrayList<GameObject>();
        collidingObjects = new GameObject[2];

        landedZoneType = 0;
        landedDuration = 0;
        firePauseDuration = 0;
        startFuelAmmount = 0;
        canonsFireInterval = 0;

        tmpVector = new Vector2();
    }

    public void loadWorld(int worldNumber) {
        try {
            XmlReader reader = new XmlReader();
            Element file = reader.parse(Gdx.files.internal("data/world" + worldNumber + ".xml"));
            Element level_definition = file.getChildByName("level_definition");

            startFuelAmmount = Integer.parseInt(level_definition.get("player_fuel_ammount"));
            canonsFireInterval = Float.parseFloat(level_definition.get("canons_fire_interval"));
            float gravityX = Float.parseFloat(level_definition.get("gravity_x"));
            float gravityY = Float.parseFloat(level_definition.get("gravity_y"));
            String terrainFileName = level_definition.get("terrain_file");


            box2dWorld = new World(new Vector2(gravityX, gravityY), true);
            terrain = new Terrain(box2dWorld, "data/" + terrainFileName);

            landingZones = new ArrayList<LandingZone>();
            canons = new ArrayList<Canon>();
            bullets = new ArrayList<Bullet>();
            blackHoles = new ArrayList<BlackHole>();
            stars = new ArrayList<Star>();
            objectsToDestroy = new ArrayList<GameObject>();

            Element objects = file.getChildByName("objects");
            Iterator<Element> iterator_objects = objects.getChildrenByName("object").iterator();
            while (iterator_objects.hasNext()) {
                Element object_element = (Element) iterator_objects.next();
                String type = object_element.get("type");
                float x = Float.parseFloat(object_element.get("x"));
                float y = Float.parseFloat(object_element.get("y"));

                if (type.equals("Canon")) {
                    float rotation = Float.parseFloat(object_element.get("rotation"));
                    canons.add(new Canon(box2dWorld, x, y, rotation));
                } else if (type.equals("BlackHole")) {
                    int strength = Integer.parseInt(object_element.get("strength"));
                    blackHoles.add(new BlackHole(box2dWorld, x, y, strength));
                } else if (type.equals("LandingZone")) {
                    float rotation = Float.parseFloat(object_element.get("rotation"));
                    String subtype = object_element.getAttribute("subtype");
                    if (subtype.equals("Start"))
                        landingZones.add(new LandingZone(1, box2dWorld, x, y, rotation));
                    else if (subtype.equals("Finish"))
                        landingZones.add(new LandingZone(2, box2dWorld, x, y, rotation));
                    else
                        landingZones.add(new LandingZone(3, box2dWorld, x, y, rotation));
                } else if (type.equals("Star")) {
                    stars.add(new Star(box2dWorld, x, y));
                }
            }
            numOfStars = stars.size();
            player = new Player(box2dWorld, 0, 0, startFuelAmmount);
            contactListener = new CollisionProcessor(this);
            box2dWorld.setContactListener(contactListener);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void update(float delta) {
        if (delta > 1 / 30f)
            box2dWorld.step(1 / 30f, 4, 2);
        else
            box2dWorld.step(delta, 4, 2);
        cleanUpDeadBodies();
        updateBlackHoles(delta);
        updateLandZones(delta);
        updatePlayerShip(delta);
        updateBullets();
        updateParticles();
        updateCanons();
        updateStars();
    }

    public void reset() {
        objectsToDestroy.add(player);
        player = new Player(box2dWorld, 0, 0, startFuelAmmount);
        state = WORLD_RUNNING;
        landedZoneType = 0;
        landedDuration = 0;
        firePauseDuration = 0;
        numOfCollectedStars = 0;
        int len = stars.size();
        for (int i = 0; i < len; i++)
            stars.get(i).dropDown();
    }

    public void pause() {
        state = WORLD_PAUSED;
    }

    public void resume() {
        state = WORLD_RUNNING;
    }

    public void updateParticles() {
        float angle = player.getSprite().getRotation() + 90;
        float x = (float) Math.cos(Math.toRadians(angle)) * 42;
        float y = (float) Math.sin(Math.toRadians(angle)) * 42;

        thrustParticleEffect.setPosition(player.getSprite().getX() + 64 - x, player.getSprite().getY() + 64 - y);
        thrustParticleEmitters.get(0).getAngle().setLow(angle - 150,
                angle - 210);
        thrustParticleEmitters.get(0).getAngle().setHigh(angle - 150,
                angle - 210);
        if (player.isThrustEnabled())
            thrustParticleEffect.start();
        else
            thrustParticleEffect.allowCompletion();
    }

    public void updateCanons() {
        int len = canons.size();
        firePauseDuration += Gdx.graphics.getDeltaTime();
        if (firePauseDuration > canonsFireInterval) {
            for (int i = 0; i < len; i++) {
                Body canonBody = canons.get(i).getBody();
                float angle = canonBody.getAngle();
                float x = -(float) Math.sin(angle);
                float y = (float) Math.cos(angle);
                bullets.add(new Bullet(box2dWorld, x * 0.8f + canonBody.getPosition().x, y * 0.8f + canonBody.getPosition().y, tmpVector.set(x * 2, y * 2)));
            }
            firePauseDuration = 0;
        }
    }

    public void updateBullets() {
        int len = bullets.size();
        for (int i = 0; i < len; i++) {
            bullets.get(i).update();
        }
    }

    public void updateBlackHoles(float delta) {
        int len = blackHoles.size();
        for (int i = 0; i < len; i++) {
            blackHoles.get(i).update(delta);
            Vector2 playerPosition = player.getBody().getPosition();
            Vector2 holePosition = blackHoles.get(i).getBody().getPosition();
            Vector2 gravityVector = (holePosition.sub(playerPosition));
            float gravityForce = blackHoles.get(i).getStrength() / gravityVector.len2();
            gravityVector.nor();
            gravityVector.mul(gravityForce);
            player.getBody().applyForceToCenter(gravityVector);
        }
    }

    private void updateLandZones(float delta) {
        int len = landingZones.size();
        for (int i = 0; i < len; i++)
            landingZones.get(i).update(delta);
    }

    public void updateStars() {
        int len = stars.size();
        for (int i = 0; i < len; i++) {
            stars.get(i).update();
        }
    }

    public void cleanUpDeadBodies() {
        int len = objectsToDestroy.size();
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < bullets.size(); j++) {
                if (bullets.get(j) == objectsToDestroy.get(i))
                    bullets.remove(j);
            }
            box2dWorld.destroyBody(objectsToDestroy.get(i).getBody());
        }
        objectsToDestroy.clear();
    }

    public void updatePlayerShip(float delta) {
        player.update();

        if (player.getState() != Player.SHIP_STATE_DEAD) {
            if (player.isThrustEnabled()) {
                if (player.getFuelAmmount() > 0) {
                    float angle = player.getBody().getAngle();
                    float x = -(float) Math.sin(angle) * 6;
                    float y = (float) Math.cos(angle) * 6;
                    player.getBody().applyForceToCenter(tmpVector.set(x, y));
                    player.consumeFuel(delta);
                } else
                    player.thrustOff();

                if (Assets.thrustSoundInstace < 0)
                    Assets.playSound(Assets.thrustSound);
            } else {
                Assets.thrustSound.stop(Assets.thrustSoundInstace);
                Assets.thrustSoundInstace = -7;
            }

            if (player.getRotationRatio() > 0) {
//				player.getBody().applyAngularImpulse(-0.13f*player.getRotationRatio());
                player.getBody().applyAngularImpulse(-7.8f * delta * player.getRotationRatio());

            }
            if (player.getRotationRatio() < 0) {
                player.getBody().applyAngularImpulse(-7.8f * delta * player.getRotationRatio());
            }


            if (player.getState() == Player.SHIP_STATE_LANDED) {
                landedDuration += Gdx.graphics.getDeltaTime();
                if (landedDuration > 1 && (Math.toDegrees(player.getBody().getAngle()) % 360 < 10
                        || Math.toDegrees(player.getBody().getAngle()) % 360 > 350)) {
                    if (landedZoneType == LandingZone.ZONETYPE_REFUEL)
                        player.refuel(delta);

                    if (landedZoneType == LandingZone.ZONETYPE_FINISH) {
                        if (numOfCollectedStars == numOfStars) {
                            state = WORLD_NEXT_LEVEL;
                            Gdx.input.setCursorCatched(false);
                        }
                    }
                }
            }
        } else {
            player.thrustOff();
            player.setRotationRatio(0);

            if (state != WORLD_GAME_OVER) {
                explosionParticleEffect.setPosition(player.getSprite().getX() + 64, player.getSprite().getY() + 64);
                explosionParticleEffect.start();
                Assets.playSound(Assets.explosionSound);
                Assets.vibrate(1500);
                state = WORLD_GAME_OVER;
                player.getBody().setActive(false);
                Assets.thrustSound.stop(Assets.thrustSoundInstace);
                Assets.thrustSoundInstace = -7;
                Gdx.input.setCursorCatched(false);
            }
        }
    }

    public void handleStartedCollisions(GameObject obj1, GameObject obj2) {
        collidingObjects[0] = obj1;
        collidingObjects[1] = obj2;
        for (int i = 0; i <= 1; i++) {
            if (collidingObjects[i].getObjectType() == GameObject.OBJECT_TYPE_BULLET) {
                if (collidingObjects[1 - i].getObjectType() == GameObject.OBJECT_TYPE_PLAYER) {
                    player.doDamage(30);
                    Assets.playSound(Assets.hitSound);
                    Assets.vibrate(60);
                }
                boolean duplicate = false;
                for (int j = 0; j < objectsToDestroy.size(); j++) {
                    if (collidingObjects[i] == objectsToDestroy.get(j))
                        duplicate = true;
                }
                if (!duplicate)
                    objectsToDestroy.add(collidingObjects[i]);
            } else if (collidingObjects[i].getObjectType() == GameObject.OBJECT_TYPE_PLAYER) {
                if (collidingObjects[1 - i].getObjectType() == GameObject.OBJECT_TYPE_LANDZONE) {
                    if (collidingObjects[1 - i].getSubType() == LandingZone.ZONETYPE_START) {
                        landedZoneType = LandingZone.ZONETYPE_START;
                        player.setState(Player.SHIP_STATE_LANDED);
                    } else if (collidingObjects[1 - i].getSubType() == LandingZone.ZONETYPE_FINISH) {
                        landedZoneType = LandingZone.ZONETYPE_FINISH;
                        player.setState(Player.SHIP_STATE_LANDED);
                    } else {
                        landedZoneType = LandingZone.ZONETYPE_REFUEL;
                        player.setState(Player.SHIP_STATE_LANDED);
                    }
                } else if (collidingObjects[1 - i].getObjectType() == GameObject.OBJECT_TYPE_STAR) {
                    Star star = (Star) collidingObjects[1 - i];
                    if (!star.isPickedUp()) {
                        Assets.playSound(Assets.starPickupSound);
                        star.pickUp();
                        Vector2 starPosition = star.getBody().getPosition();
                        starsParticleEffect.setPosition(starPosition.x * Helper.BOX_TO_WORLD, starPosition.y * Helper.BOX_TO_WORLD);
                        starsParticleEffect.start();
                        numOfCollectedStars++;
                    }
                } else if (collidingObjects[1 - i].getObjectType() == GameObject.OBJECT_TYPE_TERRAIN) {
                    player.doDamage(3);
                    Assets.playSound(Assets.hitSound);
                    Assets.vibrate(60);
                } else if (collidingObjects[1 - i].getObjectType() == GameObject.OBJECT_TYPE_BLACKHOLE)
                    player.kill();
            }
        }
    }

    public void handleEndedCollisions(GameObject obj1, GameObject obj2) {
        collidingObjects[0] = obj1;
        collidingObjects[1] = obj2;
        for (int i = 0; i <= 1; i++) {
            if (collidingObjects[i].getObjectType() == GameObject.OBJECT_TYPE_PLAYER) {
                if (collidingObjects[1 - i].getObjectType() == GameObject.OBJECT_TYPE_LANDZONE) {
                    landedZoneType = 0;
                    landedDuration = 0;
                    player.setState(Player.SHIP_STATE_FLYING);
                }
            }
        }
    }

    public ParticleEffect getThrustParticleEffect() {
        return thrustParticleEffect;
    }

    public void setThrustParticleEffect(ParticleEffect thrustParticleEffect) {
        this.thrustParticleEffect = thrustParticleEffect;
    }

    public ParticleEffect getExplosionParticleEffect() {
        return explosionParticleEffect;
    }

    public void setExplosionParticleEffect(ParticleEffect explosionParticleEffect) {
        this.explosionParticleEffect = explosionParticleEffect;
    }

    public World getBox2dWorld() {
        return box2dWorld;
    }

    public int getState() {
        return state;
    }

    public int getNumOfStars() {
        return numOfStars;
    }

    public int getNumOfCollectedStars() {
        return numOfCollectedStars;
    }

    public Player getPlayer() {
        return player;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public ArrayList<Canon> getCanons() {
        return canons;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public ArrayList<BlackHole> getBlackHoles() {
        return blackHoles;
    }

    public ArrayList<Star> getStars() {
        return stars;
    }

    public Array<ParticleEmitter> getThrustParticleEmitters() {
        return thrustParticleEmitters;
    }

    public Array<ParticleEmitter> getExplosionParticleEmitters() {
        return explosionParticleEmitters;
    }

    public ParticleEffect getStarsParticleEffect() {
        return starsParticleEffect;
    }

    public Array<ParticleEmitter> getStarsParticleEmitters() {
        return starsParticleEmitters;
    }

    public ArrayList<LandingZone> getLandingZones() {
        return landingZones;
    }

    public void setNumOfStars(int numOfStars) {
        this.numOfStars = numOfStars;
    }

    public void setNumOfCollectedStars(int numOfCollectedStars) {
        this.numOfCollectedStars = numOfCollectedStars;
    }
}
