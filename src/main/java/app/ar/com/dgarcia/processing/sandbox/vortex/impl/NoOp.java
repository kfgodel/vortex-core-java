package app.ar.com.dgarcia.processing.sandbox.vortex.impl;

/**
 * Created by ikari on 21/01/2015.
 */
public class NoOp implements Runnable {

    public static NoOp INSTANCE = new NoOp();

    @Override
    public void run() {
        // We do nothing as null runnable
    }

    public static void asRunnable(){
        // Static version of same behavior as null operand where null is not allowed
    }
}
