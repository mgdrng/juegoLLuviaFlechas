package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Menu extends BaseScreen {
    private Main juego;
    private SpriteBatch batch;
    private BitmapFont font;

    public Menu(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font  = new BitmapFont();
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            juego.setScreen(new PlayGame(juego, new ModoFacil()));
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            juego.setScreen(new PlayGame(juego, new ModoNormal()));
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
            juego.setScreen(new PlayGame(juego, new ModoDificil()));
    }

    @Override
    protected void draw(float delta) {
        batch.begin();
        font.draw(batch, "Selecciona dificultad:", 200, 340);
        font.draw(batch, "[1] Facil", 200, 310);
        font.draw(batch, "[2] Normal", 200, 280);
        font.draw(batch, "[3] Dificil", 200, 250);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
