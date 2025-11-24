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
import com.badlogic.snakerevamp.obj.Apple;
import com.badlogic.snakerevamp.obj.Snake;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {

    private final float MOVE_INTERVAL = 0.15f;
    private final Random RANDOM = new Random();

    private SnakeRevamp game;

    private Snake snake;

    private Apple apple;

    private float dirX, dirY;
    private float moveTimer = 0f;
    private boolean isAppleColliding = false, gameOver = false, gameStarted = false;
    private int score;

    public GameScreen(SnakeRevamp game) {
        this.game = game;

        // Define the snake object
        snake = new Snake();

        // Define the apple object
        apple = new Apple();

        // Set default for score
        score = 0;
    }

    @Override
    public void show() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float worldCenterX = worldWidth / 2;
        float worldCenterY = worldHeight / 2;

        // Initialize the snake | TODO: Undo the hard coding of 1 and use the sprite width
        snake.initSnake(worldCenterX - snake.getSprite().getWidth(),worldCenterY - snake.getSprite().getHeight());

        // Set position of apple randomly
        apple.randomizePosition(
            RANDOM.nextInt(0,Math.round(worldWidth)),
            RANDOM.nextInt(0,Math.round(worldHeight))
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

        if (!gameOver) {
            moveTimer += delta;

            if (moveTimer > MOVE_INTERVAL) {
                moveTimer -= MOVE_INTERVAL;

                // Move snake head
                snake.moveHead(dirX,dirY,worldWidth,worldHeight);

                // Run body collision code if game is started
                if(gameStarted){
                    gameOver = snake.runBodyCollisionCheck();
                }

                // Move rest of body
                snake.moveBody();

            // Code for running into apple
            if (snake.checkHeadOverlap(apple.getRectangle())) {
                if (!isAppleColliding) {
                    // Randomize position | TODO: Add check if its on snake body re-randomize
                    apple.randomizePosition(
                        RANDOM.nextInt(0,Math.round(worldWidth)),
                        RANDOM.nextInt(0,Math.round(worldHeight))
                    );
                    isAppleColliding = true;
                    score += 1;

                    snake.addBodySegment();
                }
            } else {
                isAppleColliding = false;
            }
            }
        }
    }

    private void draw() {

        float worldHeight = game.viewport.getWorldHeight();

        ScreenUtils.clear(Color.DARK_GRAY);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        // Draw snake
        snake.draw(game.batch);

        // Draw apple
        apple.draw(game.batch);

        // Draw score in top right
        game.font.draw(game.batch,Integer.toString(score),1,worldHeight);

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
        snake.dispose();
        apple.dispose();
    }

    //TODO: Make sure all disposal code is good and filled in, refactor to simplify away from all in game screen

}
