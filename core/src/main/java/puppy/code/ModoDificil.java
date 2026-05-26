package puppy.code;

public class ModoDificil implements ModoDificultad{
    @Override public float  getVelocidad(){
        return 340f;
    }

    @Override public float  getIntervaloSpawn(){
        return 0.45f;
    }

    @Override public String getNombre(){
        return "DIFICIL";
    }
}
