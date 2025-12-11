package com.badlogic.snakerevamp.obj;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.values.RectangleSpawnShapeValue;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class Enemy {

    private static final Random RAND = new Random();

    private Texture enemyTexture;
    private ArrayList<Sprite> bodySprites;
    private ArrayList<Rectangle> bodyRectangles;
    private ArrayList<Vector2> lastPositions;
    // Sees in a straight line of x,y positions
    // In the sight check, if a rectangle is found, stop the search and return the rectangle found
    private ArrayList<Vector2> sight;

    // Need phase for AI--later

    // Need direction for movement
    int dirX,dirY;

    // Timer for random turns
    int timer,maxTimer;

    public Enemy(int size){
        // Build the body texture
        Pixmap bodyMap = new Pixmap(1,1, Pixmap.Format.RGB888);
        bodyMap.setColor(Color.BLUE);
        bodyMap.fill();

        // Set the texture
        enemyTexture = new Texture(bodyMap);
        bodyMap.dispose();

        bodySprites = new ArrayList<>();
        bodyRectangles = new ArrayList<>();
        lastPositions = new ArrayList<>();
        sight = new ArrayList<>();
        // Add sprites and rectangles based on size of the enemy
        for(int i = 0; i < size; i++){
            Sprite segment = new Sprite(enemyTexture);
            segment.setSize(1,1);
            bodySprites.add(segment);
            bodyRectangles.add(new Rectangle());
        }

        // Stationary at spawn
        dirX = 0;
        dirY = 0;

        // Timer init
        timer = 0;
        maxTimer = RAND.nextInt(60,600);
    }

    public Sprite getHeadSprite(){
        return bodySprites.get(0);
    }

    public Rectangle getHeadRectangle(){return bodyRectangles.get(0);}

    public int getDirX(){return dirX;}
    public int getDirY(){return dirY;}

    public void setDirX(int move){dirX = move;}
    public void setDirY(int move){dirY = move;}

    // Initializes the Enemy's body sprites and rectangles
    public void initEnemy(float xPos, float yPos){
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

    // Method for moving the snake properly -- no need to take direction from game
    public void moveHead(float upperBoundX, float upperBoundY){
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


    // Casting the vision
    public void castVision(int distance){
        // Clear the vision every time
        clearVision();

        for(int i = 0; i < distance; i++){
            // Value for sight that does not include head tile
            int add = i + 1;

            //Casts vision in different direction depending on which way the snake is moving
            if(dirX == 1){
                // Look right
                sight.add(new Vector2(getHeadSprite().getX() + add, getHeadSprite().getY()));
            }
            else if(dirY == 1){
                // Look up
                sight.add(new Vector2(getHeadSprite().getX(), getHeadSprite().getY() + add));
            }
            else if(dirX == -1){
                // Look left
                sight.add(new Vector2(getHeadSprite().getX() - add, getHeadSprite().getY()));
            }
            else if(dirY == -1){
                // Look down
                sight.add(new Vector2(getHeadSprite().getX(), getHeadSprite().getY() - add));
            }
        }
    }

    // Clearing the vision (to be done on turns)
    public void clearVision(){
        sight.clear();
    }

    // Iterate over sight to determine if it intersects with a rectangle -- only for player, for now walls just turn
    public boolean iterateSight(Rectangle intersector){
        for(Vector2 v : sight){
            if(new Vector2(intersector.getX(), intersector.getY()).equals(v)){
                return true;
            }
        }
        return false;
    }

    // Turn method
    public void turn(Rectangle wallRectangle){
        if(iterateSight(wallRectangle)){
            if(dirX != 0) {
                dirX = 0;
                // Turn random direction
                if (RAND.nextBoolean()) {
                    dirY = 1;
                } else {
                    dirY = -1;
                }
            } else if(dirY != 0){
                dirY = 0;
                // Turn random direction
                if (RAND.nextBoolean()){
                    dirX = 1;
                } else {
                    dirX = -1;
                }
            }
        }
    }


    // TODO: Add a random wander method that just has them randomly turn and interacts with their sight
    // Random timer to turn when just moving regardless of sight
    public void wander() {
        System.out.println(timer + "/" + maxTimer);
        timer += 1;

        if (timer > maxTimer) {
            if (dirX != 0) {
                dirX = 0;
                // Turn random direction
                if (RAND.nextBoolean()) {
                    dirY = 1;
                } else {
                    dirY = -1;
                }
            } else if (dirY != 0) {
                dirY = 0;
                // Turn random direction
                if (RAND.nextBoolean()) {
                    dirX = 1;
                } else {
                    dirX = -1;
                }
            }
            timer = 0;
            maxTimer = RAND.nextInt(60,600);
        }
    }

    // Draw the enemy method
    public void draw(SpriteBatch batch){
        for(Sprite s : bodySprites){
            s.draw(batch);
        }
    }

    public void dispose(){
        enemyTexture.dispose();
    }




}
