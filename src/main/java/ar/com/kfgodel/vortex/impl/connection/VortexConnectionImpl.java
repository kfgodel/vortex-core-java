package ar.com.kfgodel.vortex.impl.connection;

import ar.com.kfgodel.vortex.api.VortexEndpoint;
import ar.com.kfgodel.vortex.api.connection.ConnectionHandler;
import ar.com.kfgodel.vortex.api.connection.VortexConnection;

/**
 * Created by ikari on 25/01/2015.
 */
public class VortexConnectionImpl implements VortexConnection {

    private VortexEndpoint endpoint;
    private ConnectionHandler handler;


    public static VortexConnectionImpl create(VortexEndpoint endpoint, ConnectionHandler handler) {
        VortexConnectionImpl connection = new VortexConnectionImpl();
        connection.endpoint = endpoint;
        connection.handler = handler;
        return connection;
    }

    @Override
    public void disconnect() {
        handler.whenDisconnected();
    }

    @Override
    public void connect() {
        handler.whenConnected(endpoint);
    }
}
