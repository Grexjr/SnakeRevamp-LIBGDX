package com.badlogic.snakerevamp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {

    private final float MOVE_INTERVAL = 0.15f;
    private final Random RANDOM = new Random();

    private SnakeRevamp game;

    private Texture snakeTexture;
    private ArrayList<Sprite> bodySprites;
    private ArrayList<Rectangle> bodyRectangles;
    private ArrayList<Vector2> lastPositions;

    private Texture appleTexture;
    private Sprite appleSprite;
    private Rectangle appleRectangle;

    private float dirX, dirY;
    private float moveTimer = 0f;
    private boolean isAppleColliding = false, gameOver = false, gameStarted = false;

    public GameScreen(SnakeRevamp game) {
        this.game = game;

        // Build the body texture
        Pixmap bodyMap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        bodyMap.setColor(Color.WHITE);
        bodyMap.fill();

        // Apply body texture
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

    @Override
    public void show() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float worldCenterX = worldWidth / 2;
        float worldCenterY = worldHeight / 2;

        // Set position of snake to center of world -- need some way to stop the white box at bottom left of screen
        bodySprites.get(0).setPosition(
            worldCenterX - bodySprites.get(0).getWidth(),
            worldCenterY - bodySprites.get(0).getHeight()
        );
        bodyRectangles.get(0).set(
            worldCenterX - bodySprites.get(0).getWidth(),
            worldCenterY - bodySprites.get(0).getHeight(),
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

        // Set position of apple randomly
        appleSprite.setPosition(
            RANDOM.nextInt(0, Math.round(worldWidth)),
            RANDOM.nextInt(0, Math.round(worldHeight))
        );
        appleRectangle.set(
            appleSprite.getX(),
            appleSprite.getY(),
            appleSprite.getWidth(),
            appleSprite.getHeight()
        );

    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();
    }

    private void input() {

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // set movement to right
            dirX = 1;
            dirY = 0;
            // Set game to started because movement starts the game
            gameStarted = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // set movement to left
            dirX = -1;
            dirY = 0;
            gameStarted = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            // set movement to up
            dirX = 0;
            dirY = 1;
            gameStarted = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            // set movement to down
            dirX = 0;
            dirY = -1;
            gameStarted = true;
        }
    }

    private void logic(float delta) {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Sprite head = bodySprites.get(0);
        Rectangle headRect = bodyRectangles.get(0);

        if (!gameOver) {
            moveTimer += delta;

            if (moveTimer > MOVE_INTERVAL) {
                moveTimer -= MOVE_INTERVAL;

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
                System.out.println("Head Rectangle@(" + headRect.getX() + "," + headRect.getY() + ")");

                // Run body collision code if game is started
                if(gameStarted){
                    for (int i = 1; i < bodyRectangles.size(); i++) {
                        if (headRect.overlaps(bodyRectangles.get(i))) {
                            System.out.println("Head collided with:" + bodyRectangles.get(i) + " rectangle: " + i);
                            gameOver = true;
                        }
                    }
                }

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
                    System.out.println("BodyRectangle: " + i + "@(" + bodyRectangles.get(i).getX() + "," + bodyRectangles.get(i).getY() + ")");
                }
            }

            // Wrap around code
            if (head.getX() > worldWidth) {
                head.setX(0);
                headRect.setX(0);
            }
            if (head.getX() < 0) {
                head.setX(worldWidth);
                headRect.setX(worldWidth);
            }
            if (head.getY() > worldHeight) {
                head.setY(0);
                headRect.setY(0);
            }
            if (head.getY() < 0) {
                head.setY(worldHeight);
                headRect.setY(worldHeight);
            }

            // Code for running into apple
            if (headRect.overlaps(appleRectangle)) {
                if (!isAppleColliding) {
                    appleSprite.setPosition(
                        RANDOM.nextInt(0, Math.round(worldWidth)),
                        RANDOM.nextInt(0, Math.round(worldHeight))
                    );
                    appleRectangle.setPosition(
                        appleSprite.getX(),
                        appleSprite.getY()
                    );
                    isAppleColliding = true;

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
            } else {
                isAppleColliding = false;
            }


        }

    }

    private void draw() {
        ScreenUtils.clear(Color.DARK_GRAY);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Draw snake
        for (Sprite s : bodySprites) {
            s.draw(game.batch);
        }

        // Draw apple
        appleSprite.draw(game.batch);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if (width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
