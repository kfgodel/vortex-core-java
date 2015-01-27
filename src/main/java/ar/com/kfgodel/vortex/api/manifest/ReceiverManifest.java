package ar.com.kfgodel.vortex.api.manifest;

import java.util.function.Consumer;

/**
 * This type represents the definition of a consumer expectation and obligations towards a VortexNode
 * Created by kfgodel on 18/01/15.
 */
public interface ReceiverManifest {

    Consumer<Object> onEmittersAvailable();

    void onNoEmittersAvailable();

    boolean isCompatibleWith(EmitterManifest emitterManifest);

    VortexInterest getInterest();
    void changeInterest(VortexInterest newInterest);
    void setInterestChangeListener(Runnable changeListener);

    boolean appliesTo(Object message);
}
