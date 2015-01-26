package ar.com.kfgodel.vortex.api;

import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.ReceiverManifest;

/**
 * This type represents a communication hub to send and receive messages
 * Created by ikari on 17/01/2015.
 */
public interface VortexEndpoint {

    VortexEmitter declareEmitter(EmitterManifest emitterManifest);

    void retireEmitter(VortexEmitter addedProducer);

    VortexReceiver declareReceiver(ReceiverManifest receiverManifest);

    void retireReceiver(VortexReceiver addedConsumer);
}
