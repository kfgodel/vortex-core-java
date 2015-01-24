package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.vortex.VortexInterest;

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
