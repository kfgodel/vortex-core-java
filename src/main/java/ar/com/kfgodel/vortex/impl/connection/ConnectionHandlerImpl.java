package ar.com.kfgodel.vortex.impl.connection;

import ar.com.kfgodel.lang.NoOp;
import ar.com.kfgodel.vortex.api.VortexEndpoint;
import ar.com.kfgodel.vortex.api.connection.ConnectionHandler;

import java.util.function.Consumer;

/**
 * Created by ikari on 26/01/2015.
 */
public class ConnectionHandlerImpl implements ConnectionHandler {

    private Consumer<VortexEndpoint> connectionHandler;
    private Runnable disconnectionHandler;


    @Override
    public void whenConnected(VortexEndpoint endpoint) {
        connectionHandler.accept(endpoint);
    }

    @Override
    public void whenDisconnected() {
        disconnectionHandler.run();
    }

    public static ConnectionHandlerImpl create(Consumer<VortexEndpoint> connectionHandler, Runnable disconnectionHandler) {
        ConnectionHandlerImpl handler = new ConnectionHandlerImpl();
        handler.connectionHandler = connectionHandler;
        handler.disconnectionHandler = disconnectionHandler;
        return handler;
    }

    public static ConnectionHandlerImpl create(Consumer<VortexEndpoint> connectionHandler) {
        return create(connectionHandler, NoOp.INSTANCE);
    }

}
