package com.vabrant.playground.test.structuremockups.integratedlibgdxplayground.shapes.bluecircle.src.main.java.com.playground.bluecircle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.vabrant.playground.test.baseapplications.ShapeDrawerPlayground;

public class BlueCirclePlayground extends ShapeDrawerPlayground {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(600, 600);
        config.setIdleFPS(0);
        config.useVsync(false);
        config.setTitle("Playground");
        ApplicationListener l = new BlueCirclePlayground();
        new Lwjgl3Application(l, config);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        float color = shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.filledCircle(300, 300, 50);
        shapeDrawer.setColor(color);
        batch.end();
    }
}
