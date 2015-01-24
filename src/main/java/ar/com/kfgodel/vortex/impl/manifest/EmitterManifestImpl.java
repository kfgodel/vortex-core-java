package ar.com.kfgodel.vortex.impl.manifest;

import ar.com.kfgodel.lang.NoOp;
import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.VortexInterest;

import java.util.function.Consumer;

/**
 * Created by ikari on 21/01/2015.
 */
public class EmitterManifestImpl implements EmitterManifest {

    private VortexInterest producerInterest;
    private Consumer<Consumer<Object>> connectionHandler;
    private Runnable disconnectionHandler;
    private Runnable interestChangeListener;

    @Override
    public void onConsumersAvailable(Consumer<Object> stream) {
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

    public static EmitterManifestImpl create(VortexInterest interest, Consumer<Consumer<Object>> connectionHandler, Runnable disconnectionHandler) {
        EmitterManifestImpl manifest = new EmitterManifestImpl();
        manifest.producerInterest = interest;
        manifest.connectionHandler = connectionHandler;
        manifest.disconnectionHandler = disconnectionHandler;
        manifest.interestChangeListener = NoOp.INSTANCE;
        return manifest;
    }

    public static EmitterManifestImpl create(VortexInterest interest, Consumer<Consumer<Object>> connectionHandler) {
        return create(interest, connectionHandler, NoOp.INSTANCE);
    }
}
