package puppy.code;

public class ModoNormal implements ModoDificultad{
    @Override public float  getVelocidad(){
        return 220f;
    }

    @Override public float  getIntervaloSpawn(){
        return 0.8f;
    }

    @Override public String getNombre(){
        return "NORMAL";
    }
}
