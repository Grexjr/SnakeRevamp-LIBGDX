package com.badlogic.snakerevamp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.snakerevamp.obj.*;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {

    private final String TITLE_STRING = "SNAKE EVOLVED";
    private final String INSTRUCTIONS_STRING = "Press 'Space' to start. \n Press 'R' to restart";

    private final float MOVE_INTERVAL = 0.15f;
    private final Random RANDOM = new Random();

    private SnakeRevamp game;

    private Snake snake;

    // Will eventually be arraylist
    private ArrayList<Enemy> enemyList;

    private Apple apple;

    private OuterWallGroup outerWalls;
    private DynamicWallGroup dynamicWalls;

    private float dirX, dirY;
    private float moveTimer = 0f;
    private boolean isAppleColliding = false, gameOver = false, gameStarted = false, isMoving = false;
    private int score;

    public GameScreen(SnakeRevamp game) {
        this.game = game;

        // Define the snake object
        snake = new Snake();

        // Define the enemy object
        enemyList = new ArrayList<>();
        Enemy e = new Enemy(3);
        enemyList.add(e);

        // Define the apple object
        apple = new Apple();

        // Define wall arraylists
        outerWalls = new OuterWallGroup();
        dynamicWalls = new DynamicWallGroup();

        // Set default for score
        score = 0;
    }

    @Override
    public void show() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        float worldCenterX = worldWidth / 2;
        float worldCenterY = worldHeight / 2;

        // Initialize the snake
        snake.initSnake(worldCenterX - snake.getHeadSprite().getWidth(), worldCenterY - snake.getHeadSprite().getHeight());

        // Initialize the enemy
        enemyList.get(0).initEnemy(10,10);
        enemyList.get(0).setDirX(1);

        // Set position of apple randomly
        apple.randomizePosition(
            RANDOM.nextInt(0, Math.round(worldWidth)),
            RANDOM.nextInt(0, Math.round(worldHeight))
        );

        // Set position of outer wall
        outerWalls.initOuterWall(
            worldWidth,
            worldHeight,
            worldWidth/2 - 1,
            worldHeight/2 - 1,
            3,
            3
        );

        // Set randomized positions of dynamic walls
        dynamicWalls.initDynamicWalls(RANDOM,55,worldWidth-2,worldHeight-2);

        // Check to ensure it does not overlap with a dynamic wall
        if(dynamicWalls.checkCollision(apple.getRectangle())){
            apple.randomizePosition(
                RANDOM.nextInt(1, Math.round(worldWidth-2)),
                RANDOM.nextInt(1, Math.round(worldHeight-2))
            );
        }

        if(outerWalls.checkCollision(apple.getRectangle())){
            apple.randomizePosition(
                RANDOM.nextInt(1, Math.round(worldWidth-2)),
                RANDOM.nextInt(1, Math.round(worldHeight-2))
            );
        }

    }

    @Override
    public void render(float delta) {
        input();
        logic(delta);
        draw();
    }

    private void input() {

        if(gameStarted && !gameOver){
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                // set movement to right
                dirX = 1;
                dirY = 0;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                // set movement to left
                dirX = -1;
                dirY = 0;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                // set movement to up
                dirX = 0;
                dirY = 1;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                // set movement to down
                dirX = 0;
                dirY = -1;
                isMoving = true;
            }
        } else {
            if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
                gameStarted = true;
            }
        }
    }

    private void logic(float delta) {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        if (!gameOver && gameStarted) {
            moveTimer += delta;

            if (moveTimer > MOVE_INTERVAL) {
                moveTimer -= MOVE_INTERVAL;

                // Move snake head
                snake.moveHead(dirX, dirY, worldWidth, worldHeight);

                // Run collision code if movement has started
                if (isMoving) {
                    // Code for running into body
                    gameOver = snake.runBodyCollisionCheck();
                    if(!gameOver)
                    {
                        // Code for running into wall
                        gameOver = outerWalls.checkCollision(snake.getHeadRectangle());
                        gameOver = dynamicWalls.checkCollision(snake.getHeadRectangle());
                    }

                    if(!gameOver){
                        // Code for running into enemy snake
                        for(Enemy e : enemyList){
                            gameOver = e.checkCollision(snake.getHeadRectangle());
                        }
                    }
                }

                // Move rest of body
                snake.moveBody();

                // Code for running into apple
                if (snake.checkHeadOverlap(apple.getRectangle())) {
                    if (!isAppleColliding) {
                        // Randomize position | TODO: Add check if its on snake body re-randomize
                        apple.randomizePosition(
                            // Minus two so apples do not spawn on the wall
                            RANDOM.nextInt(1, Math.round(worldWidth-2)),
                            RANDOM.nextInt(1, Math.round(worldHeight-2))
                        );

                        // Check to ensure it does not overlap with a dynamic wall
                        if(dynamicWalls.checkCollision(apple.getRectangle())){
                            apple.randomizePosition(
                                RANDOM.nextInt(1, Math.round(worldWidth-2)),
                                RANDOM.nextInt(1, Math.round(worldHeight-2))
                            );
                        }

                        isAppleColliding = true;

                        score += 1;

                        snake.addBodySegment();
                    }
                } else {
                    isAppleColliding = false;
                }

                // Move enemy in same movement counter as player--global movement counter
                for(Enemy e : enemyList){
                    e.moveHead(worldWidth,worldHeight);
                    e.moveBody();
                }
            }

            for(Enemy e : enemyList){
                e.castVision(4);
                for(Rectangle r : outerWalls.getRectangles()){
                    e.turn(r);
                }
                for(Rectangle r : dynamicWalls.getRectangles()){
                    e.turn(r);
                }
                e.wander();
            }

            for(int i = enemyList.size() - 1; i == 0; i--){
                if(snake.checkCollision(enemyList.get(i).getHeadRectangle())){
                    //Need to remove
                    enemyList.remove(enemyList.get(i));
                }
            }

        }
    }

    private void draw() {

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        // Draw outer wall
        outerWalls.draw(game.batch);

        if(gameStarted){
            // Draw apple
            apple.draw(game.batch);

            // Draw snake
            snake.draw(game.batch);

            // Draw dynamic walls
            dynamicWalls.draw(game.batch);

            // Draw enemies
            for(Enemy e : enemyList){
                e.draw(game.batch);
            }



            // Draw score in top right
            game.font.draw(game.batch, Integer.toString(score), 1, worldHeight);
        } else {
            game.font.draw(game.batch,TITLE_STRING,(worldWidth/2) - 10,worldHeight/2);
            game.font.draw(game.batch,INSTRUCTIONS_STRING,(worldWidth/2) - 10,(worldHeight/2)-3);
        }

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
        outerWalls.dispose();
    }

    //TODO: Make sure all disposal code is good and filled in, refactor to simplify away from all in game screen

}
