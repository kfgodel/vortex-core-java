package app.ar.com.dgarcia.processing.sandbox.vortex;

/**
 * This type represents the definition of a consumer expectation and obligations towards a VortexNode
 * Created by kfgodel on 18/01/15.
 */
public interface ConsumerManifest {

    VortexStream onAvailableProducers();

    void onNoAvailableProducers();

    boolean isCompatibleWith(ProducerManifest producerManifest);

    VortexInterest getInterest();
    void changeInterest(VortexInterest newInterest);
    void setInterestChangeListener(Runnable changeListener);
}
