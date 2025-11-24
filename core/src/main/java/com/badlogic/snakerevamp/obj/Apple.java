package com.badlogic.snakerevamp.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Apple {

    private Texture appleTexture;
    private Sprite appleSprite;
    private Rectangle appleRectangle;

    public Apple(){
        // Create apple texture
        Pixmap appleMap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        appleMap.setColor(Color.RED);
        appleMap.fill();

        // Apply apple texture
        appleTexture = new Texture(appleMap);
        appleMap.dispose();

        // Create apple sprite and rectangle
        appleSprite = new Sprite(appleTexture);
        appleSprite.setSize(1, 1);
        appleRectangle = new Rectangle();
    }

    public Rectangle getRectangle(){return appleRectangle;}

    // Randomize the apple position
    public void randomizePosition(int randX, int randY){
        // Set position of apple randomly
        appleSprite.setPosition(
            randX,
            randY
        );
        appleRectangle.set(
            appleSprite.getX(),
            appleSprite.getY(),
            appleSprite.getWidth(),
            appleSprite.getHeight()
        );
    }

    public void draw(SpriteBatch batch){
        appleSprite.draw(batch);
    }

    public void dispose(){
        appleTexture.dispose();
    }


}
