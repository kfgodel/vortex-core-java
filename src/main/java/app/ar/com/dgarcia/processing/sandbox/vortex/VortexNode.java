package app.ar.com.dgarcia.processing.sandbox.vortex;

/**
 * This type represents a communication hub to send and receive messages
 * Created by ikari on 17/01/2015.
 */
public interface VortexNode {

    VortexProducer declareProducer(ProducerManifest producerManifest);

    void retireProducer(VortexProducer addedProducer);

    VortexConsumer declareConsumer(ConsumerManifest consumerManifest);

    void retireConsumer(VortexConsumer addedConsumer);
}
