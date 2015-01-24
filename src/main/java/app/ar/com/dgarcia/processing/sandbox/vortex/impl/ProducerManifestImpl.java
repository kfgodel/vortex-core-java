package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.vortex.ProducerManifest;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexInterest;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexStream;

import java.util.function.Consumer;

/**
 * Created by ikari on 21/01/2015.
 */
public class ProducerManifestImpl implements ProducerManifest {

    private VortexInterest producerInterest;
    private Consumer<VortexStream> connectionHandler;
    private Runnable disconnectionHandler;
    private Runnable interestChangeListener;

    @Override
    public void onConsumersAvailable(VortexStream stream) {
        this.connectionHandler.accept(stream);
    }

    @Override
    public void onNoConsumersAvailable() {
        this.disconnectionHandler.run();
    }

    @Override
    public VortexInterest getInterest() {
        return producerInterest;
    }

    @Override
    public void changeInterest(VortexInterest newInterest) {
        this.producerInterest = newInterest;
        interestChangeListener.run();
    }

    @Override
    public void setInterestChangeListener(Runnable changeListener) {
        this.interestChangeListener = changeListener;
    }

    public static ProducerManifestImpl create(VortexInterest interest, Consumer<VortexStream> connectionHandler, Runnable disconnectionHandler) {
        ProducerManifestImpl manifest = new ProducerManifestImpl();
        manifest.producerInterest = interest;
        manifest.connectionHandler = connectionHandler;
        manifest.disconnectionHandler = disconnectionHandler;
        manifest.interestChangeListener = NoOp.INSTANCE;
        return manifest;
    }

    public static ProducerManifestImpl create(VortexInterest interest, Consumer<VortexStream> connectionHandler) {
        return create(interest, connectionHandler, NoOp.INSTANCE);
    }
}
