package app.ar.com.dgarcia.processing.sandbox.vortex;

import java.util.List;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexConsumer {

    boolean isInterestedIn(VortexProducer producerManifest);

    void addActiveProducer(VortexProducer newProducer);
    void removeActiveProducer(VortexProducer producer);

    void connectWith(List<VortexProducer> interestingProducers);
    void disconnectAll();

    VortexStream getActiveStream();

    void updateConnectionsWith(List<VortexProducer> newInterestingProducers);
}
