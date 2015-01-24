package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

import app.ar.com.dgarcia.processing.sandbox.vortex.VortexInterest;

/**
 * This type represents the interest that accepts anything (all types of messages)
 * Created by ikari on 21/01/2015.
 */
public class AllInterest implements VortexInterest {

    public static AllInterest INSTANCE = new AllInterest();

    @Override
    public boolean intersects(VortexInterest otherInterest) {
        // Interseccion con vacio da vacio
        return true && !otherInterest.equals(NoInterest.INSTANCE);
    }
}
