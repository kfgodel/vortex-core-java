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
    private List<VortexReceiver> activeReceivers;

    public static EmitterImpl create(EmitterManifest manifest) {
        EmitterImpl emitter = new EmitterImpl();
        emitter.manifest = manifest;
        emitter.activeReceivers = new ArrayList<>();
        return emitter;
    }

    @Override
    public EmitterManifest getManifest() {
        return manifest;
    }

    @Override
    public void connectWith(List<VortexReceiver> interestedReceivers) {
        notifyingChangesToManifest(() -> {
            interestedReceivers.forEach(this::addActiveReceiver);
        });
    }
    @Override
    public void connectWith(VortexReceiver receiver) {
        notifyingChangesToManifest(() -> {
            addActiveReceiver(receiver);
        });
    }

    @Override
    public void disconnectAll() {
        // Assigned to temp list in order to be able to remove from original list
        List<VortexReceiver> disconnected = new ArrayList<>(activeReceivers);
        notifyingChangesToManifest(() -> {
            disconnected.forEach(this::removeActiveReceiver);
        });
    }

    @Override
    public void disconnectFrom(VortexReceiver receiver) {
        notifyingChangesToManifest(() -> {
            this.removeActiveReceiver(receiver);
        });
    }

    @Override
    public void updateConnectionsWith(List<VortexReceiver> newInterestedReceivers) {
        MergeResult<VortexReceiver> merged = Collections.merge(activeReceivers, newInterestedReceivers);
        notifyingChangesToManifest(()->{
            merged.getRemoved().forEach(this::removeActiveReceiver);
            merged.getAdded().forEach(this::addActiveReceiver);
        });

    }

    private void removeActiveReceiver(VortexReceiver receiver) {
        activeReceivers.remove(receiver);
        receiver.removeActiveEmitter(this);
    }

    private void addActiveReceiver(VortexReceiver receiver) {
        receiver.addActiveEmitter(this);
        activeReceivers.add(receiver);
    }

    private void notifyingChangesToManifest(Runnable code){
        boolean wasActive = hasActiveReceivers();
        code.run();
        boolean isActive = hasActiveReceivers();
        if(isActive && !wasActive){
            manifest.onReceiversAvailable(this);
        }else if(!isActive && wasActive){
            manifest.onNoReceiversAvailable();
        }
    }

    private boolean hasActiveReceivers() {
        return this.activeReceivers.size() > 0;
    }

    @Override
    public void accept(Object message) {
        for (VortexReceiver activeReceiver : activeReceivers) {
            Consumer<Object> receiverStream = activeReceiver.getActiveStream();
            receiverStream.accept(message);
        }
    }
}
