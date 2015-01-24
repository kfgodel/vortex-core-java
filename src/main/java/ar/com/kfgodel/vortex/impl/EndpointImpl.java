package ar.com.kfgodel.vortex.impl;

import ar.com.kfgodel.vortex.api.VortexEmitter;
import ar.com.kfgodel.vortex.api.VortexEndpoint;
import ar.com.kfgodel.vortex.api.VortexReceiver;
import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;
import ar.com.kfgodel.vortex.api.manifest.ReceiverManifest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kfgodel on 18/01/15.
 */
public class EndpointImpl implements VortexEndpoint {

    private List<VortexEmitter> producers;
    private List<VortexReceiver> consumers;

    public static EndpointImpl create(){
        EndpointImpl node = new EndpointImpl();
        node.consumers = new ArrayList<>();
        node.producers = new ArrayList<>();
        return node;
    }

    @Override
    public VortexEmitter declareProducer(EmitterManifest emitterManifest) {
        VortexEmitter newProducer = EmitterImpl.create(emitterManifest);

        List<VortexReceiver> interestedConsumers = calculateConsumersFor(newProducer);
        newProducer.connectWith(interestedConsumers);

        producers.add(newProducer);
        emitterManifest.setInterestChangeListener(() -> onProducerInterestChanged(newProducer));
        return newProducer;
    }

    @Override
    public void retireProducer(VortexEmitter producer) {
        producers.remove(producer);
        producer.disconnectAll();
    }

    @Override
    public VortexReceiver declareConsumer(ReceiverManifest receiverManifest) {
        ReceiverImpl newConsumer = ReceiverImpl.create(receiverManifest);

        List<VortexEmitter> interestingProducers = calculateProducersFor(newConsumer);
        newConsumer.connectWith(interestingProducers);

        consumers.add(newConsumer);
        receiverManifest.setInterestChangeListener(() -> onConsumerInterestChanged(newConsumer));
        return newConsumer;
    }

    private List<VortexEmitter> calculateProducersFor(VortexReceiver newConsumer) {
        return producers.stream()
                    .filter(newConsumer::isInterestedIn)
                    .collect(Collectors.toList());
    }
    private List<VortexReceiver> calculateConsumersFor(VortexEmitter newProducer) {
        return consumers.stream()
                .filter((consumer) -> consumer.isInterestedIn(newProducer))
                .collect(Collectors.toList());
    }


    private void onConsumerInterestChanged(VortexReceiver changedConsumer) {
        List<VortexEmitter> newInterestingProducers = calculateProducersFor(changedConsumer);
        changedConsumer.updateConnectionsWith(newInterestingProducers);
    }

    private void onProducerInterestChanged(VortexEmitter changedProducer) {
        List<VortexReceiver> newInterestedConsumers = calculateConsumersFor(changedProducer);
        changedProducer.updateConnectionsWith(newInterestedConsumers);
    }


    @Override
    public void retireConsumer(VortexReceiver consumer) {
        consumers.remove(consumer);
        consumer.disconnectAll();
    }

}
