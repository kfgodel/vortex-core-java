package ar.com.kfgodel.vortex.api;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexReceiver {

    boolean isInterestedIn(VortexEmitter producerManifest);

    void addActiveProducer(VortexEmitter newProducer);
    void removeActiveProducer(VortexEmitter producer);

    void connectWith(List<VortexEmitter> interestingProducers);
    void disconnectAll();

    Consumer<Object> getActiveStream();

    void updateConnectionsWith(List<VortexEmitter> newInterestingProducers);
}
