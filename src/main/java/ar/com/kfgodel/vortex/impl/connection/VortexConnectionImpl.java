package ar.com.kfgodel.vortex.impl.connection;

import ar.com.kfgodel.vortex.api.connection.ConnectionHandler;
import ar.com.kfgodel.vortex.api.connection.VortexConnection;
import ar.com.kfgodel.vortex.api.connection.VortexNet;
import ar.com.kfgodel.vortex.impl.EndpointImpl;

/**
 * Created by ikari on 25/01/2015.
 */
public class VortexConnectionImpl implements VortexConnection {

    private VortexNet ownerNet;
    private ConnectionHandler handler;


    public static VortexConnectionImpl create(VortexNet ownerNet, ConnectionHandler handler) {
        VortexConnectionImpl connection = new VortexConnectionImpl();
        connection.handler = handler;
        connection.ownerNet = ownerNet;
        return connection;
    }

    @Override
    public void disconnect() {
        handler.whenDisconnected();
    }

    @Override
    public void connect() {
        EndpointImpl newEndpoint = EndpointImpl.create(this);
        handler.whenConnected(newEndpoint);
    }

    @Override
    public VortexNet getOwnerNet() {
        return ownerNet;
    }
}
