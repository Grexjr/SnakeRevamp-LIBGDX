package com.badlogic.snakerevamp.obj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

//TODO: Add a shapes class that determines shapes of dynamic walls based on patterns
// for now, just use single-tile walls
public class DynamicWallGroup {

    private ArrayList<Wall> dynamicWalls;

    public DynamicWallGroup(){
        // Define the arraylist
        dynamicWalls = new ArrayList<>();
    }

    public ArrayList<Rectangle> getRectangles() {
        ArrayList<Rectangle> rects = new ArrayList<>();
        for(Wall w : dynamicWalls){
            rects.add(w.getRectangle());
        }
        return rects;
    }

    // Initialize the walls
    public void initDynamicWalls(Random rand, int wallNum, float xBound, float yBound){
        for(int i = 0; i < wallNum; i++){
            Wall w = new Wall();
            w.setSpritePosition(
                // 1 because world starts at 0, so goes past the wall | TODO: Make this a parameter
                Math.round(rand.nextFloat(1,xBound)),
                Math.round(rand.nextFloat(1,yBound))
            );
            w.setRectanglePosition(
                w.getSprite().getX(),
                w.getSprite().getY()
            );
            dynamicWalls.add(w);
        }
    }

    // Check collision with a rectangle and any part of the dynamic walls
    public boolean checkCollision(Rectangle collider){
        boolean collision = false;
        for(Wall w : dynamicWalls){
            if(w.getRectangle().overlaps(collider)) collision = true;
        }
        return collision;
    }

    //
    public void draw(SpriteBatch batch){
        for(Wall w: dynamicWalls){
            w.draw(batch);
        }
    }

    public void dispose(){
        for(Wall w : dynamicWalls){
            w.dispose();
        }
    }


}
