package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.iterables.Collections;
import app.ar.com.dgarcia.processing.sandbox.iterables.MergeResult;
import app.ar.com.dgarcia.processing.sandbox.vortex.ConsumerManifest;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexConsumer;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexProducer;
import app.ar.com.dgarcia.processing.sandbox.vortex.VortexStream;

import java.util.ArrayList;
import java.util.List;

/**
 * This type represents a message consumer that based on a manifest interacts with producers to get messages
 * Created by ikari on 20/01/2015.
 */
public class ConsumerImpl implements VortexConsumer {

    private ConsumerManifest manifest;
    private List<VortexProducer> activeProducers;
    private VortexStream activeStream;

    public static ConsumerImpl create(ConsumerManifest manifest) {
        ConsumerImpl consumer = new ConsumerImpl();
        consumer.manifest = manifest;
        consumer.activeProducers = new ArrayList<>();
        return consumer;
    }

    @Override
    public boolean isInterestedIn(VortexProducer producer) {
        return this.manifest.isCompatibleWith(producer.getManifest());
    }


    @Override
    public VortexStream getActiveStream() {
        if(activeStream == null){
            //sanity check
            throw new IllegalStateException("This consumer has no active stream and one is needed");
        }
        return this.activeStream;
    }

    @Override
    public void updateConnectionsWith(List<VortexProducer> newInterestingProducers) {
        MergeResult<VortexProducer> merged = Collections.merge(activeProducers, newInterestingProducers);
        merged.getAdded().forEach((addedProducer)-> addedProducer.connectWith(this));
        merged.getRemoved().forEach((removedProducer)-> removedProducer.disconnectFrom(this));
    }

    @Override
    public void connectWith(List<VortexProducer> interestingProducers) {
        interestingProducers.forEach((producer) -> {
            producer.connectWith(this);
        });
    }
    @Override
    public void disconnectAll() {
        // Assigned to temp list in order to be able to remove from original list
        List<VortexProducer> disconnected = new ArrayList<>(activeProducers);
        disconnected.forEach((producer) -> {
            producer.disconnectFrom(this);
        });
    }

    @Override
    public void addActiveProducer(VortexProducer newProducer) {
        notifyingChangesToManifest(()->{
            activeProducers.add(newProducer);
        });
    }
    @Override
    public void removeActiveProducer(VortexProducer producer) {
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
