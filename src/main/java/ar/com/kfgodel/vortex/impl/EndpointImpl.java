package ar.com.kfgodel.vortex.impl;

import ar.com.kfgodel.vortex.api.VortexEmitter;
import ar.com.kfgodel.vortex.api.VortexEndpoint;
import ar.com.kfgodel.vortex.api.VortexReceiver;
import ar.com.kfgodel.vortex.api.connection.VortexConnection;
import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.ReceiverManifest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kfgodel on 18/01/15.
 */
public class EndpointImpl implements VortexEndpoint {

    private VortexConnection ownerConnection;
    private List<VortexEmitter> emitters;
    private List<VortexReceiver> receivers;

    public static EndpointImpl create(VortexConnection ownerConnection){
        EndpointImpl endpoint = new EndpointImpl();
        endpoint.receivers = new ArrayList<>();
        endpoint.emitters = new ArrayList<>();
        endpoint.ownerConnection = ownerConnection;
        return endpoint;
    }

    @Override
    public VortexEmitter declareEmitter(EmitterManifest emitterManifest) {
        VortexEmitter newEmitter = EmitterImpl.create(emitterManifest);

        List<VortexReceiver> interestedReceivers = calculateReceiversFor(newEmitter);
        newEmitter.connectWith(interestedReceivers);

        emitters.add(newEmitter);
        emitterManifest.setInterestChangeListener(() -> onEmitterInterestChanged(newEmitter));
        return newEmitter;
    }

    @Override
    public void retireEmitter(VortexEmitter emitter) {
        emitters.remove(emitter);
        emitter.disconnectAll();
    }

    @Override
    public VortexReceiver declareReceiver(ReceiverManifest receiverManifest) {
        ReceiverImpl newReceiver = ReceiverImpl.create(receiverManifest);

        List<VortexEmitter> interestingEmitters = calculateEmittersFor(newReceiver);
        newReceiver.connectWith(interestingEmitters);

        receivers.add(newReceiver);
        receiverManifest.setInterestChangeListener(() -> onReceiverInterestChanged(newReceiver));
        return newReceiver;
    }

    private List<VortexEmitter> calculateEmittersFor(VortexReceiver newReceiver) {
        return emitters.stream()
                    .filter(newReceiver::isInterestedIn)
                    .collect(Collectors.toList());
    }
    private List<VortexReceiver> calculateReceiversFor(VortexEmitter newEmitter) {
        return receivers.stream()
                .filter((receiver) -> receiver.isInterestedIn(newEmitter))
                .collect(Collectors.toList());
    }


    private void onReceiverInterestChanged(VortexReceiver changedReceiver) {
        List<VortexEmitter> newInterestingProducers = calculateEmittersFor(changedReceiver);
        changedReceiver.updateConnectionsWith(newInterestingProducers);
    }

    private void onEmitterInterestChanged(VortexEmitter changedEmitter) {
        List<VortexReceiver> newInterestedConsumers = calculateReceiversFor(changedEmitter);
        changedEmitter.updateConnectionsWith(newInterestedConsumers);
    }


    @Override
    public void retireReceiver(VortexReceiver receiver) {
        receivers.remove(receiver);
        receiver.disconnectAll();
    }

    @Override
    public VortexConnection getOwnerConnection() {
        return ownerConnection;
    }

}
