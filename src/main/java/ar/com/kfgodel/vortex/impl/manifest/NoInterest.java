package ar.com.kfgodel.vortex.impl.manifest;

import ar.com.kfgodel.vortex.api.manifest.VortexInterest;

/**
 * This type represents the interest that doesn't want any message
 * Created by ikari on 21/01/2015.
 */
public class NoInterest implements VortexInterest {

    public static NoInterest INSTANCE = new NoInterest();

    @Override
    public boolean intersects(VortexInterest producerInterest) {
        return false;
    }
}
