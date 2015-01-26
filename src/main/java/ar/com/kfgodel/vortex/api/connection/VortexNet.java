package ar.com.kfgodel.vortex.api.connection;

/**
 * This type represents the whole vortex net as a single entity
 * Created by ikari on 25/01/2015.
 */
public interface VortexNet {

    VortexConnection connect(ConnectionHandler handler);
}
