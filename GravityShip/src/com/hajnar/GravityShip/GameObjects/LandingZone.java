package com.hajnar.GravityShip.GameObjects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.hajnar.GravityShip.Assets;

public class LandingZone extends GameObject
{
  public static final int ZONETYPE_START = 1;
  public static final int ZONETYPE_FINISH = 2;
  public static final int ZONETYPE_REFUEL = 3;
  private Sprite sprite;
  private int zoneType;
  private TextureRegion[] animRegions;
  private Animation blinkAnimation;
  private float animTime;

  public LandingZone(int zoneType, World world, float x, float y, float rotDegrees)
  {
    super(world, BodyType.StaticBody, 2, x, y, (float)Math.toRadians(rotDegrees));

    this.zoneType = zoneType;
    this.animRegions = new TextureRegion[2];
    this.animRegions[0] = Assets.landzoneCommonRegion;
    this.sprite = new Sprite(this.animRegions[0]);

    if (zoneType == 1)
      this.animRegions[1] = Assets.landzoneStartRegion;
    else if (zoneType == 2)
      this.animRegions[1] = Assets.landzoneFinishRegion;
    else {
      this.animRegions[1] = Assets.landzoneRefuelRegion;
    }
    this.blinkAnimation = new Animation(0.5F, this.animRegions);

    this.sprite.setPosition(this.objectBody.getPosition().x * 100.0F - this.sprite.getWidth() / 2.0F, 
      this.objectBody.getPosition().y * 100.0F - this.sprite.getHeight() / 2.0F);
    this.sprite.setRotation((float)Math.toDegrees(this.objectBody.getAngle()));

    FixtureDef fixtureDef = new FixtureDef();

    Assets.objectBodiesLoader.attachFixture(this.objectBody, "landingZone", fixtureDef, 2.56F);

    this.objectBody.setUserData(this);
    this.animTime = 0.0F;
  }

  public void update(float delta)
  {
    this.animTime += delta;
    this.sprite.setRegion(this.blinkAnimation.getKeyFrame(this.animTime, true));
  }

  public Sprite getSprite() {
    return this.sprite;
  }

  public int getSubType()
  {
    return this.zoneType;
  }
}