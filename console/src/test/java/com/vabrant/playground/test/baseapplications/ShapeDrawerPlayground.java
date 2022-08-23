package com.vabrant.playground.test.baseapplications;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShapeDrawerPlayground extends ApplicationAdapter {

    public Batch batch;
    public ShapeDrawer shapeDrawer;

    @Override
    public void create() {
        batch = new SpriteBatch();

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.drawPixel(0, 0, 0xFFFFFFFF);
        shapeDrawer = new ShapeDrawer(batch, new TextureRegion(new Texture(pix)));
    }

    @Override
    public void render() {
       Gdx.gl.glClearColor(1, 1, 1, 1);
       Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

       batch.begin();
       draw(batch, shapeDrawer);
       batch.end();
    }

    public void draw(Batch batch, ShapeDrawer shapeDrawer) {}

}
