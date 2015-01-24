package ar.com.dgarcia.vortex;

import app.ar.com.dgarcia.processing.sandbox.vortex.VortexNode;
import ar.com.dgarcia.javaspec.api.TestContext;

import java.util.function.Supplier;

/**
 * Created by kfgodel on 18/01/15.
 */
public interface VortexTestContext extends TestContext {

    void node(Supplier<VortexNode> definition);
    VortexNode node();
}
