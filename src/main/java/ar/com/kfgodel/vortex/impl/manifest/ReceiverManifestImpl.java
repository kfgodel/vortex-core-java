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

    private VortexInterest receiverInterest;
    private Supplier<Consumer<Object>> availabilityHandler;
    private Runnable unavailabilityHandler;
    private Runnable interestChangeListener;


    @Override
    public Consumer<Object> onEmittersAvailable() {
        return availabilityHandler.get();
    }

    @Override
    public void onNoEmittersAvailable() {
        unavailabilityHandler.run();
    }

    @Override
    public boolean isCompatibleWith(EmitterManifest emitterManifest) {
        return receiverInterest.intersects(emitterManifest.getInterest());
    }

    @Override
    public VortexInterest getInterest() {
        return receiverInterest;
    }

    @Override
    public void changeInterest(VortexInterest newInterest) {
        this.receiverInterest = newInterest;
        interestChangeListener.run();
    }

    @Override
    public void setInterestChangeListener(Runnable changeListener) {
        this.interestChangeListener = changeListener;
    }

    public static ReceiverManifestImpl create(VortexInterest interest, Supplier<Consumer<Object>> availabilityHandler, Runnable unavailabilityHandler) {
        ReceiverManifestImpl manifest = new ReceiverManifestImpl();
        manifest.receiverInterest = interest;
        manifest.availabilityHandler = availabilityHandler;
        manifest.unavailabilityHandler = unavailabilityHandler;
        manifest.interestChangeListener = NoOp.INSTANCE;
        return manifest;
    }

    public static ReceiverManifestImpl create(VortexInterest interest, Supplier<Consumer<Object>> availabilityHandler) {
        return create(interest, availabilityHandler, NoOp.INSTANCE);
    }


}
