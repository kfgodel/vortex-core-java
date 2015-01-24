package ar.com.kfgodel.vortex.api;

import java.util.List;
import java.util.function.Consumer;

/**
 * This type represents a message receiver
 * Created by kfgodel on 18/01/15.
 */
public interface VortexReceiver {

    boolean isInterestedIn(VortexEmitter emitter);

    void addActiveEmitter(VortexEmitter newEmitter);
    void removeActiveEmitter(VortexEmitter emitter);

    void connectWith(List<VortexEmitter> interestingEmitters);
    void disconnectAll();

    Consumer<Object> getActiveStream();

    void updateConnectionsWith(List<VortexEmitter> newInterestingEmitters);
}
