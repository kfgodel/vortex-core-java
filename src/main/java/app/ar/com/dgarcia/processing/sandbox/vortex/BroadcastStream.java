package app.ar.com.dgarcia.processing.sandbox.vortex;

/**
 * Created by ikari on 20/01/2015.
 */
public interface BroadcastStream extends VortexStream {
    void addReceiver(VortexStream consumerStream);

    boolean hasReceivers();

    void removeReceiver(VortexStream consumerStream);
}
