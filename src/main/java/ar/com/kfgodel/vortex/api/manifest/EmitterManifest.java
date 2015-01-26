package ar.com.kfgodel.vortex.api.manifest;

import java.util.function.Consumer;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface EmitterManifest {
    void onReceiversAvailable(Consumer<Object> stream);

    void onNoReceiversAvailable();

    VortexInterest getInterest();
    void changeInterest(VortexInterest newInterest);
    void setInterestChangeListener(Runnable changeListener);

}
