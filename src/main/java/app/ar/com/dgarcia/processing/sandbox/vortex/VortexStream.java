package app.ar.com.dgarcia.processing.sandbox.vortex;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexStream {
    void receive(VortexMessage message);
}
