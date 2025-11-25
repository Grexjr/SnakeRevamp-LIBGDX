package com.badlogic.snakerevamp.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Wall {

    private Texture wallTexture;
    private Sprite wallSprite;
    private Rectangle wallRectangle;

    public Wall(){
        // Make wall texture
        Pixmap wallMap = new Pixmap(1,1, Pixmap.Format.RGB888);
        wallMap.setColor(Color.GREEN);
        wallMap.fill();

        // Apply wall map to texture
        wallTexture = new Texture(wallMap);
        wallMap.dispose();

        // Apply texture to sprite
        wallSprite = new Sprite(wallTexture);
        wallSprite.setSize(1,1);

        // Add rectangle
        wallRectangle = new Rectangle();
    }

    public Rectangle getRectangle(){return wallRectangle;}

    public void setSpritePosition(float xPos, float yPos){
        wallSprite.setPosition(xPos,yPos);
    }

    public void setRectanglePosition(float xPos, float yPos){
        wallRectangle.set(
            xPos,
            yPos,
            wallSprite.getWidth(),
            wallSprite.getHeight()
        );
    }

    // When apple is collected, walls randomize
    public void randomizePosition(){

    }

    // Draw
    public void draw(SpriteBatch batch){
        wallSprite.draw(batch);
    }






}
