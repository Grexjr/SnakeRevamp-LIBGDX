package com.badlogic.snakerevamp.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Snake {

    private Texture snakeTexture;
    private ArrayList<Sprite> bodySprites;
    private ArrayList<Rectangle> bodyRectangles;
    private ArrayList<Vector2> lastPositions;

    public Snake(){
        // Build the body texture
        Pixmap bodyMap = new Pixmap(1,1, Pixmap.Format.RGB888);
        bodyMap.setColor(Color.WHITE);
        bodyMap.fill();

        // Set the texture
        snakeTexture = new Texture(bodyMap);
        bodyMap.dispose();

        // Create a body of sprites and rectangles
        bodySprites = new ArrayList<>();
        bodyRectangles = new ArrayList<>();
        lastPositions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Sprite segment = new Sprite(snakeTexture);
            segment.setSize(1, 1);
            bodySprites.add(segment);
            bodyRectangles.add(new Rectangle());
        }
    }

    public Sprite getHeadSprite(){
        return bodySprites.get(0);
    }

    public Rectangle getHeadRectangle(){return bodyRectangles.get(0);}


    // Initializes the snake's body sprites and rectangles
    public void initSnake(float xPos, float yPos){
        // Set position of snake to the values
        bodySprites.get(0).setPosition(
            xPos,
            yPos
        );
        bodyRectangles.get(0).set(
            xPos,
            yPos,
            bodySprites.get(0).getWidth(),
            bodySprites.get(0).getHeight()
        );

        // Initialize body to offscreen at first
        for (int i = 1; i < bodySprites.size(); i++) {
            bodySprites.get(i).setPosition(
                -999,
                999
            );
        }
        // Initialize rectangles of body to offscreen
        for (int i = 1; i < bodyRectangles.size(); i++) {
            bodyRectangles.get(i).set(
                -999,
                999,
                bodySprites.get(i).getWidth(),
                bodySprites.get(i).getHeight()
            );
        }
    }


    // Method for moving the snake properly
    public void moveHead(float dirX, float dirY, float upperBoundX, float upperBoundY){
        Sprite head = bodySprites.get(0);
        Rectangle headRect = bodyRectangles.get(0);

        // Clear and save all old positions before moving anything
        lastPositions.clear();
        for (Sprite s : bodySprites) {
            lastPositions.add(new Vector2(s.getX(), s.getY()));
        }

        // Move head sprite and update rectangle to sprite location
        head.setPosition(
            head.getX() + dirX,
            head.getY() + dirY
        );
        headRect.set(
            head.getX(),
            head.getY(),
            head.getWidth(),
            head.getHeight()
        );

        // Wrap around code
        if (head.getX() > upperBoundX) {
            head.setX(0);
            headRect.setX(0);
        }
        if (head.getX() < 0) {
            head.setX(upperBoundX);
            headRect.setX(upperBoundX);
        }
        if (head.getY() > upperBoundY) {
            head.setY(0);
            headRect.setY(0);
        }
        if (head.getY() < 0) {
            head.setY(upperBoundY);
            headRect.setY(upperBoundY);
        }
    }

    // Method for checking collision with body
    public boolean runBodyCollisionCheck(){
        Rectangle headRect = bodyRectangles.get(0);

        for (int i = 1; i < bodyRectangles.size(); i++) {
            if (headRect.overlaps(bodyRectangles.get(i))) {
                return true;
            }
        }

        return false;
    }

    // Method for moving the rest of the body
    public void moveBody(){
        // Move the rest of the body and update rectangles
        for (int i = bodySprites.size() - 1; i > 0; i--) {
            bodySprites.get(i).setPosition(
                lastPositions.get(i - 1).x,
                lastPositions.get(i - 1).y
            );
            bodyRectangles.get(i).set(
                lastPositions.get(i - 1).x,
                lastPositions.get(i - 1).y,
                bodySprites.get(i).getWidth(),
                bodySprites.get(i).getHeight()
            );
        }
    }

    // Check if head overlaps a rectangle
    public boolean checkHeadOverlap(Rectangle query){
        return bodyRectangles.get(0).overlaps(query);
    }

    // Add a body segment if collision with apple occurs
    public void addBodySegment(){
        Sprite head = bodySprites.get(0);

        Sprite newBody = new Sprite(snakeTexture);
        bodySprites.add(newBody);

        // Hides it on the frames before its position is correctly updated
        newBody.setPosition(
            head.getX(),
            head.getY()
        );
        Rectangle newBodyRect = new Rectangle();
        bodyRectangles.add(newBodyRect);
        // Put rectangle at last part of tail
        newBodyRect.set(
            lastPositions.get(lastPositions.size() - 1).x,
            lastPositions.get(lastPositions.size() - 1).y,
            head.getWidth(),
            head.getHeight()
        );
    }

    // Draw the snake method
    public void draw(SpriteBatch batch){
        for(Sprite s : bodySprites){
            s.draw(batch);
        }
    }

    public void dispose(){
        snakeTexture.dispose();
    }





}
