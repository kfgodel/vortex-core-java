package ar.com.kfgodel.vortex.impl.connection;

import ar.com.kfgodel.vortex.api.connection.ConnectionHandler;
import ar.com.kfgodel.vortex.api.connection.VortexConnection;
import ar.com.kfgodel.vortex.api.connection.VortexNet;

/**
 * Created by ikari on 25/01/2015.
 */
public class InMemoryNet implements VortexNet {

    @Override
    public VortexConnection connect(ConnectionHandler handler) {
        VortexConnectionImpl connection = VortexConnectionImpl.create(this, handler);
        connection.connect();
        return connection;
    }

    public static InMemoryNet create() {
        return new InMemoryNet();
    }

}
