package app.ar.com.dgarcia.processing.sandbox.vortex;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface ProducerManifest {
    void onConsumersAvailable(VortexStream stream);

    void onNoConsumersAvailable();

    VortexInterest getInterest();
    void changeInterest(VortexInterest newInterest);
    void setInterestChangeListener(Runnable changeListener);

}
