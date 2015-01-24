package ar.com.kfgodel.vortex.impl.manifest;

import ar.com.kfgodel.lang.NoOp;
import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.VortexInterest;

import java.util.function.Consumer;

/**
 * Created by ikari on 21/01/2015.
 */
public class EmitterManifestImpl implements EmitterManifest {

    private VortexInterest emitterInterest;
    private Consumer<Consumer<Object>> availabilityHandler;
    private Runnable unavailabilityHandler;
    private Runnable interestChangeListener;

    @Override
    public void onReceiversAvailable(Consumer<Object> stream) {
        this.availabilityHandler.accept(stream);
    }

    @Override
    public void onNoReceiversAvailable() {
        this.unavailabilityHandler.run();
    }

    @Override
    public VortexInterest getInterest() {
        return emitterInterest;
    }

    @Override
    public void changeInterest(VortexInterest newInterest) {
        this.emitterInterest = newInterest;
        interestChangeListener.run();
    }

    @Override
    public void setInterestChangeListener(Runnable changeListener) {
        this.interestChangeListener = changeListener;
    }

    public static EmitterManifestImpl create(VortexInterest interest, Consumer<Consumer<Object>> availabilityHandler, Runnable unavailabilityHandler) {
        EmitterManifestImpl manifest = new EmitterManifestImpl();
        manifest.emitterInterest = interest;
        manifest.availabilityHandler = availabilityHandler;
        manifest.unavailabilityHandler = unavailabilityHandler;
        manifest.interestChangeListener = NoOp.INSTANCE;
        return manifest;
    }

    public static EmitterManifestImpl create(VortexInterest interest, Consumer<Consumer<Object>> availabilityHandler) {
        return create(interest, availabilityHandler, NoOp.INSTANCE);
    }
}
