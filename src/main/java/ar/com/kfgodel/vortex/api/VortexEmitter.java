package ar.com.kfgodel.vortex.api;

import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;

import java.util.List;

/**
 * This type represents a message emitter
 * Created by kfgodel on 18/01/15.
 */
public interface VortexEmitter {

    EmitterManifest getManifest();

    void connectWith(List<VortexReceiver> interestedReceivers);
    void disconnectAll();

    void connectWith(VortexReceiver receiver);
    void disconnectFrom(VortexReceiver receiver);

    void updateConnectionsWith(List<VortexReceiver> newInterestedReceivers);
}
