package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.vortex.ConsumerManifest;
import app.ar.com.dgarcia.processing.sandbox.vortex.ProducerManifest;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexInterest;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexStream;

import java.util.function.Supplier;

/**
 * Created by ikari on 21/01/2015.
 */
public class ConsumerManifestImpl implements ConsumerManifest {

    private VortexInterest consumerInterest;
    private Supplier<VortexStream> connectionHandler;
    private Runnable disconnectionHandler;
    private Runnable interestChangeListener;


    @Override
    public VortexStream onAvailableProducers() {
        return connectionHandler.get();
    }

    @Override
    public void onNoAvailableProducers() {
        disconnectionHandler.run();
    }

    @Override
    public boolean isCompatibleWith(ProducerManifest producerManifest) {
        return consumerInterest.intersects(producerManifest.getInterest());
    }

    @Override
    public VortexInterest getInterest() {
        return consumerInterest;
    }

    @Override
    public void changeInterest(VortexInterest newInterest) {
        this.consumerInterest = newInterest;
        interestChangeListener.run();
    }

    @Override
    public void setInterestChangeListener(Runnable changeListener) {
        this.interestChangeListener = changeListener;
    }

    public static ConsumerManifestImpl create(VortexInterest interest, Supplier<VortexStream> connectionHandler, Runnable disconnectionHandler) {
        ConsumerManifestImpl manifest = new ConsumerManifestImpl();
        manifest.consumerInterest = interest;
        manifest.connectionHandler = connectionHandler;
        manifest.disconnectionHandler = disconnectionHandler;
        manifest.interestChangeListener = NoOp.INSTANCE;
        return manifest;
    }

    public static ConsumerManifestImpl create(VortexInterest interest, Supplier<VortexStream> connectionHandler) {
        return create(interest, connectionHandler, NoOp.INSTANCE);
    }


}
