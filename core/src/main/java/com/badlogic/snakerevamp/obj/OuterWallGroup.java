package com.badlogic.snakerevamp.obj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class OuterWallGroup {

    private ArrayList<Wall> outerWalls;

    public OuterWallGroup(){
        // Define the arraylist
        outerWalls = new ArrayList<>();
    }

    // Initialize the outer walls positions of sprites and rectangles
    public void initOuterWall(float xBound, float yBound){
        for (int i = 0; i < 2; i++) {
            switch (i) {
                // First run; draw the top & bottom walls
                case 0 -> {
                    for (int j = 0; j < xBound; j++) {
                        Wall w = new Wall();
                        Wall w2 = new Wall();

                        outerWalls.add(w);
                        outerWalls.add(w2);

                        w.setSpritePosition(
                            j,
                            0
                        );
                        w2.setSpritePosition(
                            j,
                            yBound - 1
                        );
                        w.setRectanglePosition(
                            j,
                            0
                        );
                        w2.setRectanglePosition(
                            j,
                            yBound - 1
                        );
                    }
                }
                // Second run; draw the left & right walls
                case 1 -> {
                    for (int j = 0; j < yBound; j++) {
                        Wall w = new Wall();
                        Wall w2 = new Wall();

                        outerWalls.add(w);
                        outerWalls.add(w2);

                        w.setSpritePosition(
                            0,
                            j
                        );
                        w2.setSpritePosition(
                            xBound - 1,
                            j
                        );
                        w.setRectanglePosition(
                            0,
                            j
                        );
                        w2.setRectanglePosition(
                            xBound - 1,
                            j
                        );
                    }
                }
            }
        }
    }

    public void initOuterWall(float xBound, float yBound, float doorCenter, float doorWidth){
        for (int i = 0; i < 2; i++) {
            switch (i) {
                // First run; draw the top & bottom walls with a gap for the door
                case 0 -> {
                    for (int j = 0; j < xBound; j++) {
                        if(j < (doorCenter - doorWidth) || j > (doorCenter + doorWidth)){
                            Wall w = new Wall();
                            Wall w2 = new Wall();

                            outerWalls.add(w);
                            outerWalls.add(w2);

                            w.setSpritePosition(
                                j,
                                0
                            );
                            w2.setSpritePosition(
                                j,
                                yBound - 1
                            );
                            w.setRectanglePosition(
                                j,
                                0
                            );
                            w2.setRectanglePosition(
                                j,
                                yBound - 1
                            );
                        }
                    }
                }
                // Second run; draw the left & right walls
                case 1 -> {
                    for (int j = 0; j < yBound; j++) {
                        if(j < doorCenter - doorWidth || j > doorCenter + doorWidth){
                            Wall w = new Wall();
                            Wall w2 = new Wall();

                            outerWalls.add(w);
                            outerWalls.add(w2);

                            w.setSpritePosition(
                                0,
                                j
                            );
                            w2.setSpritePosition(
                                xBound - 1,
                                j
                            );
                            w.setRectanglePosition(
                                0,
                                j
                            );
                            w2.setRectanglePosition(
                                xBound - 1,
                                j
                            );
                        }
                    }
                }
            }
        }
    }

    public void initOuterWall(float xBound, float yBound, float doorCenterX, float doorCenterY,
                              float doorWidthX, float doorWidthY){
        for (int i = 0; i < 2; i++) {
            switch (i) {
                // First run; draw the top & bottom walls with a gap for the door
                case 0 -> {
                    for (int j = 0; j < xBound; j++) {
                        if(j < (doorCenterX - doorWidthX) || j > (doorCenterX + doorWidthX)){
                            Wall w = new Wall();
                            Wall w2 = new Wall();

                            outerWalls.add(w);
                            outerWalls.add(w2);

                            w.setSpritePosition(
                                j,
                                0
                            );
                            w2.setSpritePosition(
                                j,
                                yBound - 1
                            );
                            w.setRectanglePosition(
                                j,
                                0
                            );
                            w2.setRectanglePosition(
                                j,
                                yBound - 1
                            );
                        }
                    }
                }
                // Second run; draw the left & right walls
                case 1 -> {
                    for (int j = 0; j < yBound; j++) {
                        if(j < doorCenterY - doorWidthY || j > doorCenterY + doorWidthY){
                            Wall w = new Wall();
                            Wall w2 = new Wall();

                            outerWalls.add(w);
                            outerWalls.add(w2);

                            w.setSpritePosition(
                                0,
                                j
                            );
                            w2.setSpritePosition(
                                xBound - 1,
                                j
                            );
                            w.setRectanglePosition(
                                0,
                                j
                            );
                            w2.setRectanglePosition(
                                xBound - 1,
                                j
                            );
                        }
                    }
                }
            }
        }
    }

    // Check collision with a rectangle and any part of the outer wall
    public boolean checkCollision(Rectangle collider){
        boolean collision = false;
        for(Wall w : outerWalls){
            if(w.getRectangle().overlaps(collider)) collision = true;
        }
        return collision;
    }

    // Draw method
    public void draw(SpriteBatch batch){
        for(Wall w: outerWalls){
            w.draw(batch);
        }
    }

    // Dispose method
    public void dispose(){
        for(Wall w : outerWalls){
            w.dispose();
        }
    }




}
