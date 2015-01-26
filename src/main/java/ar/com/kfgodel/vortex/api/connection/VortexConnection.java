package ar.com.kfgodel.vortex.api.connection;

/**
 * This type represents a global connection to the vortex net
 * Created by ikari on 25/01/2015.
 */
public interface VortexConnection {

    void disconnect();

    void connect();
}
