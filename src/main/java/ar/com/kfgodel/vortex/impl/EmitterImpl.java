package ar.com.kfgodel.vortex.impl;

import ar.com.kfgodel.iterables.Collections;
import ar.com.kfgodel.iterables.MergeResult;
import ar.com.kfgodel.vortex.api.VortexEmitter;
import ar.com.kfgodel.vortex.api.VortexReceiver;
import ar.com.kfgodel.vortex.api.manifest.EmitterManifest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This type represents a message producer that feeds known consumers with messages that receives as a stream
 * Created by ikari on 20/01/2015.
 */
public class EmitterImpl implements VortexEmitter, Consumer<Object> {

    private EmitterManifest manifest;
    private List<VortexReceiver> activeConsumers;

    public static EmitterImpl create(EmitterManifest manifest) {
        EmitterImpl producer = new EmitterImpl();
        producer.manifest = manifest;
        producer.activeConsumers = new ArrayList<>();
        return producer;
    }

    @Override
    public EmitterManifest getManifest() {
        return manifest;
    }

    @Override
    public void connectWith(List<VortexReceiver> consumers) {
        notifyingChangesToManifest(() -> {
            consumers.forEach(this::addActiveConsumer);
        });
    }
    @Override
    public void connectWith(VortexReceiver consumer) {
        notifyingChangesToManifest(() -> {
            addActiveConsumer(consumer);
        });
    }

    @Override
    public void disconnectAll() {
        // Assigned to temp list in order to be able to remove from original list
        List<VortexReceiver> disconnected = new ArrayList<>(activeConsumers);
        notifyingChangesToManifest(() -> {
            disconnected.forEach(this::removeActiveConsumer);
        });
    }

    @Override
    public void disconnectFrom(VortexReceiver consumer) {
        notifyingChangesToManifest(() -> {
            this.removeActiveConsumer(consumer);
        });
    }

    @Override
    public void updateConnectionsWith(List<VortexReceiver> newInterestedConsumers) {
        MergeResult<VortexReceiver> merged = Collections.merge(activeConsumers, newInterestedConsumers);
        notifyingChangesToManifest(()->{
            merged.getRemoved().forEach(this::removeActiveConsumer);
            merged.getAdded().forEach(this::addActiveConsumer);
        });

    }

    private void removeActiveConsumer(VortexReceiver consumer) {
        activeConsumers.remove(consumer);
        consumer.removeActiveProducer(this);
    }

    private void addActiveConsumer(VortexReceiver consumer) {
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
    public void accept(Object message) {
        for (VortexReceiver activeConsumer : activeConsumers) {
            Consumer<Object> consumerStream = activeConsumer.getActiveStream();
            consumerStream.accept(message);
        }
    }
}
