package ar.com.kfgodel.vortex.impl.manifest;

import ar.com.kfgodel.lang.NoOp;
import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.ReceiverManifest;
import ar.com.kfgodel.vortex.api.manifest.VortexInterest;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by ikari on 21/01/2015.
 */
public class ReceiverManifestImpl implements ReceiverManifest {

    private VortexInterest consumerInterest;
    private Supplier<Consumer<Object>> connectionHandler;
    private Runnable disconnectionHandler;
    private Runnable interestChangeListener;


    @Override
    public Consumer<Object> onAvailableProducers() {
        return connectionHandler.get();
    }

    @Override
    public void onNoAvailableProducers() {
        disconnectionHandler.run();
    }

    @Override
    public boolean isCompatibleWith(EmitterManifest emitterManifest) {
        return consumerInterest.intersects(emitterManifest.getInterest());
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

    public static ReceiverManifestImpl create(VortexInterest interest, Supplier<Consumer<Object>> connectionHandler, Runnable disconnectionHandler) {
        ReceiverManifestImpl manifest = new ReceiverManifestImpl();
        manifest.consumerInterest = interest;
        manifest.connectionHandler = connectionHandler;
        manifest.disconnectionHandler = disconnectionHandler;
        manifest.interestChangeListener = NoOp.INSTANCE;
        return manifest;
    }

    public static ReceiverManifestImpl create(VortexInterest interest, Supplier<Consumer<Object>> connectionHandler) {
        return create(interest, connectionHandler, NoOp.INSTANCE);
    }


}
