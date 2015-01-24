package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.iterables.Collections;
import app.ar.com.dgarcia.processing.sandbox.iterables.MergeResult;
import app.ar.com.dgarcia.processing.sandbox.vortex.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This type represents a message producer that feeds known consumers with messages that receives as a stream
 * Created by ikari on 20/01/2015.
 */
public class ProducerImpl implements VortexProducer, VortexStream {

    private ProducerManifest manifest;
    private List<VortexConsumer> activeConsumers;

    public static ProducerImpl create(ProducerManifest manifest) {
        ProducerImpl producer = new ProducerImpl();
        producer.manifest = manifest;
        producer.activeConsumers = new ArrayList<>();
        return producer;
    }

    @Override
    public ProducerManifest getManifest() {
        return manifest;
    }

    @Override
    public void connectWith(List<VortexConsumer> consumers) {
        notifyingChangesToManifest(() -> {
            consumers.forEach(this::addActiveConsumer);
        });
    }
    @Override
    public void connectWith(VortexConsumer consumer) {
        notifyingChangesToManifest(() -> {
            addActiveConsumer(consumer);
        });
    }

    @Override
    public void disconnectAll() {
        // Assigned to temp list in order to be able to remove from original list
        List<VortexConsumer> disconnected = new ArrayList<>(activeConsumers);
        notifyingChangesToManifest(() -> {
            disconnected.forEach(this::removeActiveConsumer);
        });
    }

    @Override
    public void disconnectFrom(VortexConsumer consumer) {
        notifyingChangesToManifest(() -> {
            this.removeActiveConsumer(consumer);
        });
    }

    @Override
    public void updateConnectionsWith(List<VortexConsumer> newInterestedConsumers) {
        MergeResult<VortexConsumer> merged = Collections.merge(activeConsumers, newInterestedConsumers);
        notifyingChangesToManifest(()->{
            merged.getRemoved().forEach(this::removeActiveConsumer);
            merged.getAdded().forEach(this::addActiveConsumer);
        });

    }

    private void removeActiveConsumer(VortexConsumer consumer) {
        activeConsumers.remove(consumer);
        consumer.removeActiveProducer(this);
    }

    private void addActiveConsumer(VortexConsumer consumer) {
        consumer.addActiveProducer(this);
        activeConsumers.add(consumer);
    }

    private void notifyingChangesToManifest(Runnable code){
        boolean wasActive = hasActiveConsumers();
        code.run();
        boolean isActive = hasActiveConsumers();
        if(isActive && !wasActive){
            manifest.onConsumersAvailable(this);
        }else if(!isActive && wasActive){
            manifest.onNoConsumersAvailable();
        }
    }

    private boolean hasActiveConsumers() {
        return this.activeConsumers.size() > 0;
    }

    @Override
    public void receive(VortexMessage message) {
        for (VortexConsumer activeConsumer : activeConsumers) {
            VortexStream consumerStream = activeConsumer.getActiveStream();
            consumerStream.receive(message);
        }
    }
}
