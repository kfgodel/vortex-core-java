package ar.com.kfgodel.vortex.api.manifest;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexInterest {
    boolean intersects(VortexInterest otherInterest);

    boolean contains(Object message);

}
