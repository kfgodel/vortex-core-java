package ar.com.kfgodel.vortex.api;

import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.ReceiverManifest;

/**
 * This type represents a communication hub to send and receive messages
 * Created by ikari on 17/01/2015.
 */
public interface VortexEndpoint {

    VortexEmitter declareProducer(EmitterManifest emitterManifest);

    void retireProducer(VortexEmitter addedProducer);

    VortexReceiver declareConsumer(ReceiverManifest receiverManifest);

    void retireConsumer(VortexReceiver addedConsumer);
}
