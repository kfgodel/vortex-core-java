package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.vortex.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kfgodel on 18/01/15.
 */
public class NodeImpl implements VortexNode {

    private List<VortexProducer> producers;
    private List<VortexConsumer> consumers;

    public static NodeImpl create(){
        NodeImpl node = new NodeImpl();
        node.consumers = new ArrayList<>();
        node.producers = new ArrayList<>();
        return node;
    }

    @Override
    public VortexProducer declareProducer(ProducerManifest producerManifest) {
        VortexProducer newProducer = ProducerImpl.create(producerManifest);

        List<VortexConsumer> interestedConsumers = calculateConsumersFor(newProducer);
        newProducer.connectWith(interestedConsumers);

        producers.add(newProducer);
        producerManifest.setInterestChangeListener(() -> onProducerInterestChanged(newProducer));
        return newProducer;
    }

    @Override
    public void retireProducer(VortexProducer producer) {
        producers.remove(producer);
        producer.disconnectAll();
    }

    @Override
    public VortexConsumer declareConsumer(ConsumerManifest consumerManifest) {
        ConsumerImpl newConsumer = ConsumerImpl.create(consumerManifest);

        List<VortexProducer> interestingProducers = calculateProducersFor(newConsumer);
        newConsumer.connectWith(interestingProducers);

        consumers.add(newConsumer);
        consumerManifest.setInterestChangeListener(() -> onConsumerInterestChanged(newConsumer));
        return newConsumer;
    }

    private List<VortexProducer> calculateProducersFor(VortexConsumer newConsumer) {
        return producers.stream()
                    .filter(newConsumer::isInterestedIn)
                    .collect(Collectors.toList());
    }
    private List<VortexConsumer> calculateConsumersFor(VortexProducer newProducer) {
        return consumers.stream()
                .filter((consumer) -> consumer.isInterestedIn(newProducer))
                .collect(Collectors.toList());
    }


    private void onConsumerInterestChanged(VortexConsumer changedConsumer) {
        List<VortexProducer> newInterestingProducers = calculateProducersFor(changedConsumer);
        changedConsumer.updateConnectionsWith(newInterestingProducers);
    }

    private void onProducerInterestChanged(VortexProducer changedProducer) {
        List<VortexConsumer> newInterestedConsumers = calculateConsumersFor(changedProducer);
        changedProducer.updateConnectionsWith(newInterestedConsumers);
    }


    @Override
    public void retireConsumer(VortexConsumer consumer) {
        consumers.remove(consumer);
        consumer.disconnectAll();
    }

}
