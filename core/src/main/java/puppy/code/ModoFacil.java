package puppy.code;

public class ModoFacil implements ModoDificultad{
    @Override public float  getVelocidad()      {
        return 150f;
    }

    @Override public float  getIntervaloSpawn(){
        return 1.2f;
    }
    @Override public String getNombre(){
        return "FACIL";
    }
}
