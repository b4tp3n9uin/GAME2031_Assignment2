package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import org.w3c.dom.events.MouseEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

import jdk.nashorn.internal.ir.LabelNode;

public class MyScreen extends ScreenBeta {

    //Actors
    ActorBeta Background;
    ActorBeta pNet;
    ActorBeta gNet;
    ActorBeta Goalie;
    ActorBeta Player;
    ActorBeta Puck;

    //Actions
    MoveToAction left;
    MoveToAction right;

    // Score trackers and Labels
    int pScore, gScore;
    Label pPoints, gPoints;

    //Actions Grouping
    SequenceAction sequence;
    RepeatAction repeat;

    // Puck Movement
    float puckVelocityX;
    float puckVelocityY;
    Random randXDirection;

    @Override
    public void initialize() {

        // Initialize Background
        Background = new ActorBeta(0.0f, 0.0f, mainStage);
        Background.loadTexture("Background.png");
        Background.setWidth(Gdx.graphics.getWidth());
        Background.setHeight(Gdx.graphics.getHeight());

        // Initialize Actions
        left = new MoveToAction();
        right = new MoveToAction();

        // Initialize Physics Value
        randXDirection = new Random();

        // Initialize Score Values
        pScore = 0;
        gScore = 0;


        // Set Actors
        pNet = new ActorBeta(Gdx.graphics.getWidth() - 50.0f, Gdx.graphics.getHeight() / 3, mainStage);
        pNet.loadTexture("pNet.png");
        pNet.setBoundaryRectangle();

        gNet = new ActorBeta(0.0f, Gdx.graphics.getHeight() / 3, mainStage);
        gNet.loadTexture("gNet.png");
        gNet.setBoundaryRectangle();

        Goalie = new ActorBeta(Gdx.graphics.getWidth() / 9, Gdx.graphics.getHeight() * 3 / 4, mainStage);
        Goalie.loadTexture("Goalie.jpg");
        Goalie.setHeight(Goalie.getHeight() / 1.5f); Goalie.setWidth(Goalie.getWidth() / 1.5f);
        Goalie.setBoundaryRectangle();

        Player = new ActorBeta(Gdx.graphics.getWidth() * 3 / 4, Gdx.graphics.getHeight() / 2, mainStage);
        Player.loadTexture("player.png");
        Player.setHeight(Player.getHeight() / 1.5f); Player.setWidth(Player.getWidth() / 1.5f);
        Player.setBoundaryRectangle();

        Puck = new ActorBeta(Gdx.graphics.getWidth() / 2 -75, Gdx.graphics.getHeight() /2 -75, mainStage);
        Puck.loadTexture("puck.png");
        Puck.setHeight(Puck.getHeight() / 3 ); Puck.setWidth(Puck.getWidth() / 3);
        Puck.setBoundaryRectangle();

        // Set Actions
        left.setPosition(Gdx.graphics.getWidth() / 9, 0);
        left.setDuration(2.0f);

        right.setPosition(Gdx.graphics.getWidth() / 9, Gdx.graphics.getHeight() * 3 / 4);
        right.setDuration(2.0f);

        // Initalize Group Actions
        sequence = new SequenceAction(left, right);
        repeat = new RepeatAction();

        // Set Repeat Action
        repeat.setAction(sequence);
        repeat.setCount(RepeatAction.FOREVER);

        // Add Action to AI
        Goalie.addAction(repeat);

        // Set Values to Puck Velocity
        puckVelocityX = 0.0f;
        puckVelocityY = 0.0f;


        // Initialize the Score Labels
        pPoints = new Label(String.format("%01d", pScore), new Label.LabelStyle(new BitmapFont(), Color.GREEN));
        pPoints.setSize(200, 200);
        pPoints.setFontScale(10, 10);
        pPoints.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 12);
        pPoints.setAlignment(Align.center);
        mainStage.addActor(pPoints);

        gPoints = new Label(String.format("%01d", pScore), new Label.LabelStyle(new BitmapFont(), Color.RED));
        gPoints.setSize(200, 200);
        gPoints.setFontScale(10, 10);
        gPoints.setPosition(Gdx.graphics.getWidth() / 2 - 250, Gdx.graphics.getHeight() / 12);
        gPoints.setAlignment(Align.center);
        mainStage.addActor(gPoints);

    }

    @Override
    public void update(float dt) {
        Puck.moveBy(puckVelocityX, puckVelocityY);

        // Player Input
        if (Gdx.input.isTouched())
        {
            Player.setPosition(Gdx.input.getX(), Gdx.input.getY()*-1 + 1000);
        }
        // Prevents player going to the opposing side
        if (Player.getX() <= Gdx.graphics.getWidth() / 2)
        {
            Player.setPosition(Gdx.graphics.getWidth() / 2, Player.getY());
        }


        // Check Wall Collisions
        if (Puck.getX() >= Gdx.graphics.getWidth())
        {
            puckVelocityX = -puckVelocityX;
        }

        if (Puck.getX() <= 0)
        {
            puckVelocityX = -puckVelocityX;
        }


        // Check Wall Collisions
        if (Puck.getY() >= Gdx.graphics.getHeight() - 50)
        {
            puckVelocityY = -puckVelocityY;
        }

        if (Puck.getY() <= 0)
        {
            puckVelocityY = -puckVelocityY;
        }


        // Check Player & Goalie Collisions
        if (Puck.overlaps(Player))
        {
            puckVelocityX = -7;
        }

        if (Puck.overlaps(Goalie))
        {
            puckVelocityX = 7;
            puckVelocityY = 7 - randXDirection.nextInt(14);
        }

        // GOALS for either team
        if (Puck.overlaps(pNet))
        {
            Puck.setPosition(Gdx.graphics.getWidth() / 2-75, Gdx.graphics.getHeight() /2-75);
            puckVelocityX = 0;
            puckVelocityY = 0;
            gScore = gScore + 1;
        }

        if (Puck.overlaps(gNet))
        {
            Puck.setPosition(Gdx.graphics.getWidth() / 2-75, Gdx.graphics.getHeight() /2-75);
            puckVelocityX = 0;
            puckVelocityY = 0;
            pScore = pScore + 1;
        }

        // Once someone scores 5 points, the game is finished
        if (pScore == 5)
        {
            Puck.setPosition(100000, 100000);

            Label win = new Label("You Won!", new Label.LabelStyle(new BitmapFont(), Color.GREEN));
            win.setSize(200, 200);
            win.setFontScale(15, 15);
            win.setPosition(Gdx.graphics.getWidth() / 2 - 250, Gdx.graphics.getHeight() / 2);
            win.setAlignment(Align.center);
            mainStage.addActor(win);
        }

        if (gScore == 5)
        {
            Puck.setPosition(100000, 100000);

            Label lose = new Label("Game Over", new Label.LabelStyle(new BitmapFont(), Color.RED));
            lose.setSize(200, 200);
            lose.setFontScale(15, 15);
            lose.setPosition(Gdx.graphics.getWidth() / 2 - 250, Gdx.graphics.getHeight() / 2);
            lose.setAlignment(Align.center);
            mainStage.addActor(lose);
        }

    }
}
