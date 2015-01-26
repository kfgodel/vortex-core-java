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
    private List<VortexEmitter> activeEmitters;
    private Consumer<Object> activeStream;

    public static ReceiverImpl create(ReceiverManifest manifest) {
        ReceiverImpl receiver = new ReceiverImpl();
        receiver.manifest = manifest;
        receiver.activeEmitters = new ArrayList<>();
        return receiver;
    }

    @Override
    public boolean isInterestedIn(VortexEmitter emitter) {
        return this.manifest.isCompatibleWith(emitter.getManifest());
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
    public void updateConnectionsWith(List<VortexEmitter> newInterestingEmitters) {
        MergeResult<VortexEmitter> merged = Collections.merge(activeEmitters, newInterestingEmitters);
        merged.getAdded().forEach((addedEmitter)-> addedEmitter.connectWith(this));
        merged.getRemoved().forEach((removedEmitter)-> removedEmitter.disconnectFrom(this));
    }

    @Override
    public void connectWith(List<VortexEmitter> interestingEmitters) {
        interestingEmitters.forEach((emitter) -> {
            emitter.connectWith(this);
        });
    }
    @Override
    public void disconnectAll() {
        // Assigned to temp list in order to be able to remove from original list
        List<VortexEmitter> disconnected = new ArrayList<>(activeEmitters);
        disconnected.forEach((emitter) -> {
            emitter.disconnectFrom(this);
        });
    }

    @Override
    public void addActiveEmitter(VortexEmitter newEmitter) {
        notifyingChangesToManifest(()->{
            activeEmitters.add(newEmitter);
        });
    }
    @Override
    public void removeActiveEmitter(VortexEmitter emitter) {
        notifyingChangesToManifest(() -> {
            activeEmitters.remove(emitter);
        });
    }

    private void notifyingChangesToManifest(Runnable code){
        boolean wasActive = hasActiveEmitters();
        code.run();
        boolean isActive = hasActiveEmitters();
        if(isActive && !wasActive){
            this.activeStream = manifest.onEmittersAvailable();
        }else if(!isActive && wasActive){
            manifest.onNoEmittersAvailable();
            this.activeStream = null;
        }
    }

    private boolean hasActiveEmitters() {
        return this.activeEmitters.size() > 0;
    }

}
