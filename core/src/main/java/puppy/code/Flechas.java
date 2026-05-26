package puppy.code;

public class Flechas {
    private int   tipo; // 0=izquierda, 1=abajo, 2=arriba, 3=derecha
    private float x;
    private float y;

    public Flechas(int tipo, float x, float startY) {
        this.tipo = tipo;
        this.x    = x;
        this.y    = startY;
    }

    public void mover(float distancia) {
        this.y -= distancia;
    }

    public int   getTipo() { return tipo; }
    public float getX()    { return x;    }
    public float getY()    { return y;    }
}
