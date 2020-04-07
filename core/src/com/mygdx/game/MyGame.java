package com.mygdx.game;

public class MyGame extends GameBeta {

    MyScreen mainScreen;

    @Override
    public void create() {
        super.create();

        mainScreen = new MyScreen();

        setActiveScreen(mainScreen);
    }
}
