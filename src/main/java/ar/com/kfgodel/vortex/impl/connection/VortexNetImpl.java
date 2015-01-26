package ar.com.kfgodel.vortex.impl.connection;

import ar.com.kfgodel.vortex.api.connection.ConnectionHandler;
import ar.com.kfgodel.vortex.api.connection.VortexConnection;
import ar.com.kfgodel.vortex.api.connection.VortexNet;
import ar.com.kfgodel.vortex.impl.EndpointImpl;

/**
 * Created by ikari on 25/01/2015.
 */
public class VortexNetImpl implements VortexNet {

    @Override
    public VortexConnection connect(ConnectionHandler handler) {
        EndpointImpl endpoint = EndpointImpl.create();
        VortexConnectionImpl connection = VortexConnectionImpl.create(endpoint, handler);
        connection.connect();
        return connection;
    }

    public static VortexNetImpl create() {
        return new VortexNetImpl();
    }

}
