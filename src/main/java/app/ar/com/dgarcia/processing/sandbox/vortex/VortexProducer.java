package app.ar.com.dgarcia.processing.sandbox.vortex;

import java.util.List;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexProducer {

    ProducerManifest getManifest();

    void connectWith(List<VortexConsumer> consumerStreams);
    void disconnectAll();

    void connectWith(VortexConsumer consumer);
    void disconnectFrom(VortexConsumer consumer);

    void updateConnectionsWith(List<VortexConsumer> newInterestedConsumers);
}
