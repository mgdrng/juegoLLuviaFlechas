package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

public class PlayGame extends BaseScreen{
    private Main juego;
    private SpriteBatch lote;
    private ShapeRenderer dibujoPaneles;
    private BitmapFont font;
    private Texture flechaArriba;
    private Texture flechaAbajo;
    private Texture flechaIzquierda;
    private Texture flechaDerecha;

    private Array<Flechas> flechas = new Array<>();
    private float spawnTimer = 0f;
    private Random rng = new Random();

    private ModoDificultad dificultad = new ModoNormal();

    private float tamanio = 45f;
    private float spacing = 55f;
    private float inicioX;
    private float golpeY  = 80f;
    private float panelW  = 65f;
    private float panelX  = 10f;

    private int vidas  = 3;
    private int puntos = 0;

    private float margenGolpe = 30f;

    private boolean herido = false;
    private int tiempoHerido    = 0;
    private int tiempoHeridoMax = 50;

    public PlayGame(Main juego, ModoDificultad dificultad) {
        this.juego = juego;
        this.dificultad = dificultad;
    }

    @Override
    public void show() {
        lote          = new SpriteBatch();
        dibujoPaneles = new ShapeRenderer();
        font          = new BitmapFont();

        flechaArriba    = new Texture("flechaUp.png");
        flechaAbajo     = new Texture("flechaDown.png");
        flechaIzquierda = new Texture("flechaLeft.png");
        flechaDerecha   = new Texture("flechaRight.png");

        float screenWidth   = Gdx.graphics.getWidth();
        float centerX       = screenWidth / 2f;
        float totalCarriles = (4 * tamanio) + (3 * spacing);
        inicioX = centerX - (totalCarriles / 2f);
    }

    @Override
    protected void update(float tiempoFrame) {

        if (vidas <= 0) {
            juego.setScreen(new Menu(juego));
            return;
        }

        if (herido) {
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }

        spawnTimer += tiempoFrame;
        if (spawnTimer >= dificultad.getIntervaloSpawn()) {
            spawnTimer = 0f;
            int carril = rng.nextInt(4);
            flechas.add(new Flechas(carril, carrilX(carril), Gdx.graphics.getHeight()));
        }

        boolean izq    = Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
        boolean abajo  = Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
        boolean arriba = Gdx.input.isKeyJustPressed(Input.Keys.UP);
        boolean der    = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);

        for (int i = flechas.size - 1; i >= 0; i--) {
            Flechas f = flechas.get(i);
            f.mover(dificultad.getVelocidad() * tiempoFrame);

            if (f.getY() + tamanio < 0) {
                flechas.removeIndex(i);
                dañar();
                continue;
            }

            if (Math.abs(f.getY() - golpeY) <= margenGolpe) {
                if (teclaCorrecta(f.getTipo(), izq, abajo, arriba, der)) {
                    flechas.removeIndex(i);
                    puntos += 10;
                }
            }
        }
    }

    @Override
    protected void draw(float tiempoFrame) {
        dibujoPaneles.begin(ShapeRenderer.ShapeType.Filled);
        dibujoPaneles.setColor(0.12f, 0.12f, 0.12f, 1f);
        for (int carril = 0; carril < 4; carril++) {
            dibujoPaneles.rect(carrilX(carril) - panelX, 0, panelW, Gdx.graphics.getHeight());
        }
        dibujoPaneles.end();

        lote.begin();

        font.draw(lote, "Puntos: " + puntos, 5, Gdx.graphics.getHeight() - 5);
        font.draw(lote, "Vidas: "  + vidas,  Gdx.graphics.getWidth() - 120,
            Gdx.graphics.getHeight() - 5);

        if (!herido || (tiempoHerido % 6 < 3)) {
            for (int i = 0; i < flechas.size; i++) {
                Flechas flecha = flechas.get(i);
                lote.draw(texFor(flecha.getTipo()),
                    flecha.getX(), flecha.getY(), tamanio, tamanio);
            }
        }

        lote.draw(flechaIzquierda, carrilX(0), golpeY, tamanio, tamanio);
        lote.draw(flechaAbajo,     carrilX(1), golpeY, tamanio, tamanio);
        lote.draw(flechaArriba,    carrilX(2), golpeY, tamanio, tamanio);
        lote.draw(flechaDerecha,   carrilX(3), golpeY, tamanio, tamanio);

        lote.end();
    }

    private void dañar() {
        vidas--;
        herido       = true;
        tiempoHerido = tiempoHeridoMax;
    }

    private boolean teclaCorrecta(int tipo, boolean izq, boolean abajo, boolean arriba, boolean der) {
        switch (tipo) {
            case 0:  return izq;
            case 1:  return abajo;
            case 2:  return arriba;
            default: return der;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        lote.dispose();
        dibujoPaneles.dispose();
        font.dispose();
        flechaArriba.dispose();
        flechaAbajo.dispose();
        flechaIzquierda.dispose();
        flechaDerecha.dispose();
    }

    private float carrilX(int carril) {
        return inicioX + carril * (tamanio + spacing);
    }

    private Texture texFor(int tipo) {
        switch (tipo) {
            case 0:  return flechaIzquierda;
            case 1:  return flechaAbajo;
            case 2:  return flechaArriba;
            default: return flechaDerecha;
        }
    }
}
