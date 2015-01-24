package ar.com.kfgodel.vortex.api;

import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;

import java.util.List;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexEmitter {

    EmitterManifest getManifest();

    void connectWith(List<VortexReceiver> consumerStreams);
    void disconnectAll();

    void connectWith(VortexReceiver consumer);
    void disconnectFrom(VortexReceiver consumer);

    void updateConnectionsWith(List<VortexReceiver> newInterestedConsumers);
}
