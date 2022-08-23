package com.vabrant.playground.test.structuremockups.integratedlibgdxplayground.shapes.redsquare.src.main.java.com.playground.redquare;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.vabrant.playground.test.baseapplications.ShapeDrawerPlayground;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RedSquarePlayground extends ShapeDrawerPlayground {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(600, 600);
        config.setIdleFPS(0);
        config.useVsync(false);
        config.setTitle("RedSquarePlayground");
        new Lwjgl3Application(new RedSquarePlayground(), config);
    }

    @Override
    public void draw(Batch batch, ShapeDrawer shapeDrawer) {
        super.draw(batch, shapeDrawer);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        float color = shapeDrawer.setColor(Color.RED);
        shapeDrawer.filledRectangle(300 - 25, 300 - 25, 50, 50);
        shapeDrawer.setColor(color);
        batch.end();
    }
}
