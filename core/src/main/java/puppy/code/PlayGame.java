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

    private float tamanio = 45f;
    private float spacing = 55f;
    private float inicioX;
    private float golpeY  = 80f;
    private float panelW  = 65f;
    private float panelX  = 10f;

    // ── Sistema de vidas y puntos ──────────────────────────
    private int vidas  = 3;
    private int puntos = 0;

    // Ventana de acierto: ±margen píxeles respecto a golpeY
    private float margenGolpe = 30f;

    // Estado "herido" (igual que Tarro)
    private boolean herido        = false;
    private int     tiempoHerido  = 0;
    private int     tiempoHeridoMax = 50;

    public PlayGame(Main juego) {
        this.juego = juego;
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

        // Si no hay vidas, volver al menú
        if (vidas <= 0) {
            juego.setScreen(new Menu(juego));
            return;
        }

        // Cooldown de herido
        if (herido) {
            tiempoHerido--;
            if (tiempoHerido <= 0) herido = false;
        }

        // Spawn de flechas
        spawnTimer += tiempoFrame;
        if (spawnTimer >= 0.8f) {
            spawnTimer = 0f;
            int carril = rng.nextInt(4);
            flechas.add(new Flechas(carril, carrilX(carril), Gdx.graphics.getHeight()));
        }

        // Mover flechas y detectar eventos
        for (int i = flechas.size - 1; i >= 0; i--) {
            Flechas f = flechas.get(i);
            f.mover(220f * tiempoFrame);

            // Flecha pasó la zona de golpe sin ser presionada → pierde vida
            if (f.getY() + tamanio < 0) {
                flechas.removeIndex(i);
                dañar();
                continue;
            }

            // Detectar tecla correcta cuando la flecha está en la zona de golpe
            if (Math.abs(f.getY() - golpeY) <= margenGolpe) {
                if (teclaCorrecta(f.getTipo())) {
                    flechas.removeIndex(i);
                    puntos += 10;
                }
            }
        }
    }

    @Override
    protected void draw(float tiempoFrame) {
        // Paneles de carril
        dibujoPaneles.begin(ShapeRenderer.ShapeType.Filled);
        dibujoPaneles.setColor(0.12f, 0.12f, 0.12f, 1f);
        for (int carril = 0; carril < 4; carril++) {
            dibujoPaneles.rect(carrilX(carril) - panelX, 0, panelW, Gdx.graphics.getHeight());
        }
        dibujoPaneles.end();

        lote.begin();

        // HUD: vidas y puntos
        font.draw(lote, "Puntos: " + puntos, 5,   Gdx.graphics.getHeight() - 5);
        font.draw(lote, "Vidas: "  + vidas,  Gdx.graphics.getWidth() - 120,
            Gdx.graphics.getHeight() - 5);

        // Flechas cayendo (parpadeo si está herido)
        if (!herido || (tiempoHerido % 6 < 3)) {
            for (int i = 0; i < flechas.size; i++) {
                Flechas flecha = flechas.get(i);
                lote.draw(texFor(flecha.getTipo()),
                    flecha.getX(), flecha.getY(), tamanio, tamanio);
            }
        }

        // Flechas estáticas zona de golpe
        lote.draw(flechaIzquierda, carrilX(0), golpeY, tamanio, tamanio);
        lote.draw(flechaAbajo,     carrilX(1), golpeY, tamanio, tamanio);
        lote.draw(flechaArriba,    carrilX(2), golpeY, tamanio, tamanio);
        lote.draw(flechaDerecha,   carrilX(3), golpeY, tamanio, tamanio);

        lote.end();
    }

    // ── Helpers ───────────────────────────────────────────

    private void dañar() {
        vidas--;
        herido       = true;
        tiempoHerido = tiempoHeridoMax;
    }

    /** Devuelve true si se presionó la tecla que corresponde al tipo de flecha */
    private boolean teclaCorrecta(int tipo) {
        switch (tipo) {
            case 0: return Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
            case 1: return Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
            case 2: return Gdx.input.isKeyJustPressed(Input.Keys.UP);
            default: return Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);
        }
    }

    @Override
    public void dispose() {
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
