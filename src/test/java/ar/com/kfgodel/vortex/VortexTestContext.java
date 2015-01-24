package ar.com.kfgodel.vortex;

import ar.com.dgarcia.javaspec.api.TestContext;
import ar.com.kfgodel.vortex.api.VortexEndpoint;

import java.util.function.Supplier;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexTestContext extends TestContext {

    void node(Supplier<VortexEndpoint> definition);
    VortexEndpoint node();
}
