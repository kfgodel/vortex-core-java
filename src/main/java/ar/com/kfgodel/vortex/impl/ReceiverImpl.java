package ar.com.kfgodel.vortex.impl;

import ar.com.kfgodel.iterables.Collections;
import ar.com.kfgodel.iterables.MergeResult;
import ar.com.kfgodel.vortex.api.VortexEmitter;
import ar.com.kfgodel.vortex.api.VortexReceiver;
import ar.com.kfgodel.vortex.api.manifest.ReceiverManifest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This type represents a message consumer that based on a manifest interacts with producers to get messages
 * Created by ikari on 20/01/2015.
 */
public class ReceiverImpl implements VortexReceiver {

    private ReceiverManifest manifest;
    private List<VortexEmitter> activeProducers;
    private Consumer<Object> activeStream;

    public static ReceiverImpl create(ReceiverManifest manifest) {
        ReceiverImpl consumer = new ReceiverImpl();
        consumer.manifest = manifest;
        consumer.activeProducers = new ArrayList<>();
        return consumer;
    }

    @Override
    public boolean isInterestedIn(VortexEmitter producer) {
        return this.manifest.isCompatibleWith(producer.getManifest());
    }


    @Override
    public Consumer<Object> getActiveStream() {
        if(activeStream == null){
            //sanity check
            throw new IllegalStateException("This consumer has no active stream and one is needed");
        }
        return this.activeStream;
    }

    @Override
    public void updateConnectionsWith(List<VortexEmitter> newInterestingProducers) {
        MergeResult<VortexEmitter> merged = Collections.merge(activeProducers, newInterestingProducers);
        merged.getAdded().forEach((addedProducer)-> addedProducer.connectWith(this));
        merged.getRemoved().forEach((removedProducer)-> removedProducer.disconnectFrom(this));
    }

    @Override
    public void connectWith(List<VortexEmitter> interestingProducers) {
        interestingProducers.forEach((producer) -> {
            producer.connectWith(this);
        });
    }
    @Override
    public void disconnectAll() {
        // Assigned to temp list in order to be able to remove from original list
        List<VortexEmitter> disconnected = new ArrayList<>(activeProducers);
        disconnected.forEach((producer) -> {
            producer.disconnectFrom(this);
        });
    }

    @Override
    public void addActiveProducer(VortexEmitter newProducer) {
        notifyingChangesToManifest(()->{
            activeProducers.add(newProducer);
        });
    }
    @Override
    public void removeActiveProducer(VortexEmitter producer) {
        notifyingChangesToManifest(() -> {
            activeProducers.remove(producer);
        });
    }

    private void notifyingChangesToManifest(Runnable code){
        boolean wasActive = hasActiveProducers();
        code.run();
        boolean isActive = hasActiveProducers();
        if(isActive && !wasActive){
            this.activeStream = manifest.onAvailableProducers();
        }else if(!isActive && wasActive){
            manifest.onNoAvailableProducers();
            this.activeStream = null;
        }
    }

    private boolean hasActiveProducers() {
        return this.activeProducers.size() > 0;
    }

}
