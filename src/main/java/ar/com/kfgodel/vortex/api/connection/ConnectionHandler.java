package ar.com.kfgodel.vortex.api.connection;

import ar.com.kfgodel.vortex.api.VortexEndpoint;

/**
 * This type represents a handler for connection and disconnection of vortex net
 * Created by ikari on 25/01/2015.
 */
public interface ConnectionHandler {

    void whenConnected(VortexEndpoint endpoint);
    void whenDisconnected();
}
