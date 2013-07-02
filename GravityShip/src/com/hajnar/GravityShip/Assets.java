package com.hajnar.GravityShip;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ETC1;

public class Assets
{
  public static Texture buttonsTexture;
  public static Texture gameObjectsTexture;
  public static Texture backgroundTexture;
  public static Texture backgroundTexture2;
  public static Texture terrainTexture;
  public static Texture splashScreenTexture;
  public static TextureRegion gasRegion1;
  public static TextureRegion gasRegion2;
  public static TextureRegion spaceshipRegion;
  public static TextureRegion canonRegion;
  public static TextureRegion landzoneCommonRegion;
  public static TextureRegion landzoneStartRegion;
  public static TextureRegion landzoneFinishRegion;
  public static TextureRegion landzoneRefuelRegion;
  public static TextureRegion bulletRegion;
  public static TextureRegion starRegion;
  public static TextureRegion smokeParticleRegion;
  public static TextureRegion sparkParticleRegion;
  public static TextureRegion pauseBackground;
  public static TextureRegion gameoverBackground;
  public static TextureRegion succesBackground;
  public static TextureRegion startRegion;
  public static TextureRegion restartRegion;
  public static TextureRegion settingsRegion;
  public static TextureRegion exitRegion;
  public static TextureRegion nextLevelRegion;
  public static TextureRegion resumeRegion;
  public static TextureRegion backRegion;
  public static TextureRegion saveRegion;
  public static TextureRegion musicRegion;
  public static TextureRegion effectsRegion;
  public static TextureRegion vibrationRegion;
  public static TextureRegion onRegion;
  public static TextureRegion offRegion;
  public static TextureRegion starHUDRegion;
  public static TextureRegion[] blackHoleRegions;
  public static Sprite thrustSprite;
  public static Sprite leftSprite;
  public static Sprite rightSprite;
  public static Music music;
  public static Sound hitSound;
  public static Sound explosionSound;
  public static Sound buttonClickSound;
  public static Sound thrustSound;
  public static Sound starPickupSound;
  public static long thrustSoundInstace;
  public static BodyEditorLoader objectBodiesLoader;
  public static BitmapFont gameFont;
  public static String terrainVertexShader;
  public static String terrainFragmentShader;
  public static String hiPassVertexShader;
  public static String hiPassFragmentShader;
  public static String blurFragmentShader;

  public static void loadAssets()
  {    
	  Pixmap pixmap = new Pixmap(Gdx.files.internal("data/terrain4.png"));
	  ETC1.encodeImagePKM(pixmap).write(Gdx.files.local("terrain4.etc1"));   
	    
    if (GravityShip.deviceType == GravityShip.DEV_TYPE_ANDROID)
    {
      buttonsTexture = new Texture(Gdx.files.internal("data/menu_buttons.png"), true);
      buttonsTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);

      gameObjectsTexture = new Texture(Gdx.files.internal("data/game_objects.png"), true);
      gameObjectsTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
      
      pixmap = new Pixmap(Gdx.files.internal("data/background_color.png"));
      ETC1.encodeImagePKM(pixmap).write(Gdx.files.local("background_color.etc1"));
      
      backgroundTexture = new Texture(Gdx.files.local("background_color.etc1"), true);
      backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    else
    {
      buttonsTexture = new Texture(Gdx.files.internal("data/menu_buttons.png"));
      buttonsTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

      gameObjectsTexture = new Texture(Gdx.files.internal("data/game_objects.png"));
      gameObjectsTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

      backgroundTexture = new Texture(Gdx.files.internal("data/background_color.png"), true);
      backgroundTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
    }

    splashScreenTexture = new Texture(Gdx.files.internal("data/splash_screen.png"), true);
    splashScreenTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);

    backgroundTexture2 = new Texture(Gdx.files.internal("data/background2.png"), true);
    backgroundTexture2.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);    

    gasRegion1 = new TextureRegion(backgroundTexture2, 0, 0, 512, 512);
    gasRegion2 = new TextureRegion(backgroundTexture2, 512, 0, 256, 256);
    
    terrainTexture = new Texture(Gdx.files.local("terrain4.etc1"), false);
    terrainTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    terrainTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);

    spaceshipRegion = new TextureRegion(gameObjectsTexture, 0, 0, 128, 128);
    canonRegion = new TextureRegion(gameObjectsTexture, 128, 0, 128, 128);
    landzoneCommonRegion = new TextureRegion(gameObjectsTexture, 512, 0, 256, 64);
    landzoneStartRegion = new TextureRegion(gameObjectsTexture, 256, 0, 256, 64);
    landzoneFinishRegion = new TextureRegion(gameObjectsTexture, 256, 64, 256, 64);
    landzoneRefuelRegion = new TextureRegion(gameObjectsTexture, 256, 128, 256, 64);
    bulletRegion = new TextureRegion(gameObjectsTexture, 160, 128, 16, 16);
    starRegion = new TextureRegion(gameObjectsTexture, 0, 128, 64, 64);
    smokeParticleRegion = new TextureRegion(gameObjectsTexture, 128, 128, 32, 32);
    sparkParticleRegion = new TextureRegion(gameObjectsTexture, 128, 161, 16, 16);

    pauseBackground = new TextureRegion(buttonsTexture, 0, 450, 70, 60);
    gameoverBackground = new TextureRegion(buttonsTexture, 162, 450, 90, 60);
    succesBackground = new TextureRegion(buttonsTexture, 82, 450, 70, 60);

    blackHoleRegions = new TextureRegion[9];

    blackHoleRegions[0] = new TextureRegion(gameObjectsTexture, 0, 256, 256, 256);
    blackHoleRegions[1] = new TextureRegion(gameObjectsTexture, 256, 256, 256, 256);
    blackHoleRegions[2] = new TextureRegion(gameObjectsTexture, 512, 256, 256, 256);
    blackHoleRegions[3] = new TextureRegion(gameObjectsTexture, 768, 256, 256, 256);
    blackHoleRegions[4] = new TextureRegion(gameObjectsTexture, 0, 512, 256, 256);
    blackHoleRegions[5] = new TextureRegion(gameObjectsTexture, 256, 512, 256, 256);
    blackHoleRegions[6] = new TextureRegion(gameObjectsTexture, 512, 512, 256, 256);
    blackHoleRegions[7] = new TextureRegion(gameObjectsTexture, 768, 512, 256, 256);
    blackHoleRegions[8] = new TextureRegion(gameObjectsTexture, 0, 768, 256, 256);

    startRegion = new TextureRegion(buttonsTexture, 0, 0, 512, 64);
    restartRegion = new TextureRegion(buttonsTexture, 0, 192, 512, 64);
    settingsRegion = new TextureRegion(buttonsTexture, 0, 64, 512, 64);
    exitRegion = new TextureRegion(buttonsTexture, 0, 128, 512, 64);
    nextLevelRegion = new TextureRegion(buttonsTexture, 0, 256, 512, 64);
    resumeRegion = new TextureRegion(buttonsTexture, 0, 320, 512, 64);
    backRegion = new TextureRegion(buttonsTexture, 0, 848, 256, 64);
    saveRegion = new TextureRegion(buttonsTexture, 256, 848, 256, 64);
    musicRegion = new TextureRegion(buttonsTexture, 0, 720, 512, 64);
    effectsRegion = new TextureRegion(buttonsTexture, 0, 656, 512, 64);
    vibrationRegion = new TextureRegion(buttonsTexture, 0, 784, 512, 64);
    onRegion = new TextureRegion(buttonsTexture, 0, 912, 87, 64);
    offRegion = new TextureRegion(buttonsTexture, 88, 912, 104, 64);
    starHUDRegion = new TextureRegion(buttonsTexture, 256, 912, 32, 32);

    thrustSprite = new Sprite(buttonsTexture, 0, 513, 140, 140);
    leftSprite = new Sprite(buttonsTexture, 140, 513, 140, 140);
    rightSprite = new Sprite(buttonsTexture, 281, 513, 140, 140);

    objectBodiesLoader = new BodyEditorLoader(Gdx.files.internal("data/gravityShipObjects.obj2d"));

    gameFont = new BitmapFont(Gdx.files.internal("data/gamefont.fnt"), false);
    gameFont.setColor(1.0F, 1.0F, 1.0F, 1.0F);

    music = Gdx.audio.newMusic(Gdx.files.internal("data/music.mp3"));
    music.setVolume(0.6F);
    music.setLooping(true);

    buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("data/buttonclick.ogg"));
    thrustSound = Gdx.audio.newSound(Gdx.files.internal("data/thrust.ogg"));
    hitSound = Gdx.audio.newSound(Gdx.files.internal("data/hit.ogg"));
    explosionSound = Gdx.audio.newSound(Gdx.files.internal("data/explosion.ogg"));
    starPickupSound = Gdx.audio.newSound(Gdx.files.internal("data/star_pickup.ogg"));
    thrustSoundInstace = -7L;

    terrainVertexShader = "attribute vec2 a_position; \nattribute vec2 a_texCoord0; \nuniform mat4 u_projectionViewMatrix; \nvarying vec2 v_texCoords;void main() \n{ \nv_texCoords = a_texCoord0; \ngl_Position = u_projectionViewMatrix * vec4(a_position, 0.0, 1.0); \n}";

    terrainFragmentShader = 
      "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main() \n{ \ngl_FragColor = texture2D(u_texture, v_texCoords); \n}";

    hiPassVertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projTrans;\n \nvarying vec4 vColor;\nvarying vec2 vTexCoord;\nvoid main() {\n\tvColor = a_color;\n\tvTexCoord = a_texCoord0;\n\tgl_Position =  u_projTrans * a_position;\n}";

    hiPassFragmentShader = 
      "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nuniform sampler2D u_texture; \nuniform float brightPassThreshold; \nvarying vec2 vTexCoord; \nvoid main(void) { \nvec3 luminanceVector = vec3(0.2125, 0.7154, 0.0721); \nvec4 sample = texture2D(u_texture, vTexCoord); \nfloat luminance = dot(luminanceVector, sample.rgb); \nluminance = max(0.0, luminance - brightPassThreshold); \nsample.rgb *= sign(luminance); \nsample.a = 1.0; \ngl_FragColor = sample; \n}";

    blurFragmentShader = 
      "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying LOWP vec4 vColor;\nvarying vec2 vTexCoord;\n\nuniform sampler2D u_texture;\nuniform float resolution;\nuniform float radius;\nuniform vec2 dir;\n\nvoid main() {\n\tvec4 sum = vec4(0.0);\n\tvec2 tc = vTexCoord;\n\tfloat blur = radius/resolution; \n    \n    float hstep = dir.x;\n    float vstep = dir.y;\n    \n\tsum += texture2D(u_texture, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.05;\n\tsum += texture2D(u_texture, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.09;\n\tsum += texture2D(u_texture, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.12;\n\tsum += texture2D(u_texture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.15;\n\t\n\tsum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.16;\n\t\n\tsum += texture2D(u_texture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.15;\n\tsum += texture2D(u_texture, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.12;\n\tsum += texture2D(u_texture, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.09;\n\tsum += texture2D(u_texture, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.05;\n\n\tgl_FragColor = vColor * vec4(sum.rgb, 1.0);\n}";
  }

  public static void playSound(Sound sound)
  {
    if (GravityShip.prefs.isSoundOn())
    {
      if (sound == thrustSound)
        thrustSoundInstace = sound.play(0.8F);
      else
        sound.play(0.8F);
    }
  }

  public static void vibrate(int lengthMS) {
    if (GravityShip.prefs.isVibrationOn())
    {
      Gdx.input.vibrate(lengthMS);
    }
  }

  public static void disposeAssets()
  {
    buttonsTexture.dispose();
    music.dispose();
    hitSound.dispose();
    explosionSound.dispose();
    buttonClickSound.dispose();
    thrustSound.dispose();
    starPickupSound.dispose();
    gameFont.dispose();
    backgroundTexture.dispose();
    backgroundTexture2.dispose();
    terrainTexture.dispose();
    splashScreenTexture.dispose();
  }
}