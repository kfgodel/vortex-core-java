package ar.com.kfgodel.vortex;

import ar.com.dgarcia.javaspec.api.JavaSpec;
import ar.com.dgarcia.javaspec.api.JavaSpecRunner;
import ar.com.dgarcia.javaspec.api.Variable;
import ar.com.kfgodel.vortex.api.VortexEmitter;
import ar.com.kfgodel.vortex.api.VortexReceiver;
import ar.com.kfgodel.vortex.api.manifest.VortexInterest;
import ar.com.kfgodel.vortex.impl.EndpointImpl;
import ar.com.kfgodel.vortex.impl.manifest.AllInterest;
import ar.com.kfgodel.vortex.impl.manifest.EmitterManifestImpl;
import ar.com.kfgodel.vortex.impl.manifest.NoInterest;
import ar.com.kfgodel.vortex.impl.manifest.ReceiverManifestImpl;
import org.junit.runner.RunWith;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

/**
 * This type verifies the correct behavior of a vortex endpoint
 * Created by kfgodel on 18/01/15.
 */
@RunWith(JavaSpecRunner.class)
public class VortexEndpointTest extends JavaSpec<VortexTestContext> {
    @Override
    public void define() {
        describe("vortex endpoint", ()->{

            context().node(EndpointImpl::create);
            
            it("allows message passing between consumer and producer",()->{
                // The consumer declaration to hold the communication stream in a variable
                Variable<Consumer<Object>> producerStreamHolder = Variable.create();
                Consumer<Consumer<Object>> consumerAvailabilityHandler = (stream)-> producerStreamHolder.set(stream);
                EmitterManifestImpl producerManifest = EmitterManifestImpl.create(AllInterest.INSTANCE, consumerAvailabilityHandler);
                context().node().declareProducer(producerManifest);

                // The consumer declaration to mock the reception of the message
                Consumer<Object> mockedConsumerStream = mock(Consumer.class);
                Supplier<Consumer<Object>> producerAvailabilityHandler = ()-> mockedConsumerStream;
                declareConsumer(AllInterest.INSTANCE, producerAvailabilityHandler);

                // We send a message in one end and verify we received it in the other
                Object sentMessage = new Object();
                producerStreamHolder.get().accept(sentMessage);

                // It should be the exact same message
                verify(mockedConsumerStream).accept(sentMessage);
            });

            describe("consumers", () -> {
                describe("are notified", () -> {
                    describe("when matching producers", () -> {
                        describe("are available", () -> {
                            it("before consumer declaration", () -> {
                                declareProducer(AllInterest.INSTANCE);

                                // Mocked handler to verify
                                Supplier<Consumer<Object>> producerAvailabilityHandler = mockConsumerAvailabilityHandler();
                                declareConsumer(AllInterest.INSTANCE, producerAvailabilityHandler);

                                // Our handler gets called
                                verify(producerAvailabilityHandler).get();
                            });
                            it("after consumer declaration", () -> {
                                Supplier<Consumer<Object>> producerAvailabilityHandler = mockConsumerAvailabilityHandler();
                                declareConsumer(AllInterest.INSTANCE, producerAvailabilityHandler);

                                declareProducer(AllInterest.INSTANCE);

                                verify(producerAvailabilityHandler).get();
                            });
                        });
                        it("become unavailable after consumer declaration", () -> {
                            // The producer we will remove
                            VortexEmitter producer = declareProducer(AllInterest.INSTANCE);

                            Runnable producerUnavailabilityHandler = mock(Runnable.class);
                            declareConsumer(AllInterest.INSTANCE, producerUnavailabilityHandler);

                            //Let's remove the producer to get notified
                            context().node().retireProducer(producer);

                            verify(producerUnavailabilityHandler).run();
                        });
                    });
                    describe("when interest change", () -> {
                        it("makes un-matching producers into matching",()->{
                            declareProducer(AllInterest.INSTANCE);

                            Supplier<Consumer<Object>> producerAvailabilityHandler = mockConsumerAvailabilityHandler();
                            ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(NoInterest.INSTANCE, producerAvailabilityHandler);
                            context().node().declareConsumer(consumerManifest);

                            consumerManifest.changeInterest(AllInterest.INSTANCE);

                            verify(producerAvailabilityHandler).get();
                        });
                        it("makes matching producers into un-matching",()->{
                            declareProducer(AllInterest.INSTANCE);

                            Runnable producerUnavailabilityHandler = mock(Runnable.class);
                            ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(AllInterest.INSTANCE, mock(Supplier.class), producerUnavailabilityHandler);
                            context().node().declareConsumer(consumerManifest);

                            consumerManifest.changeInterest(NoInterest.INSTANCE);

                            verify(producerUnavailabilityHandler).run();
                        });
                    });
                });

                describe("are not notified", () -> {
                    describe("when un-matching producers", () -> {
                        describe("are available", () -> {
                            it("before consumer declaration", () -> {
                                declareProducer(AllInterest.INSTANCE);

                                Supplier<Consumer<Object>> producerAvailabilityHandler = mockConsumerAvailabilityHandler();
                                declareConsumer(NoInterest.INSTANCE, producerAvailabilityHandler);

                                verify(producerAvailabilityHandler, never()).get();
                            });

                            it("after consumer declaration", () -> {
                                Supplier<Consumer<Object>> producerAvailabilityHandler = mockConsumerAvailabilityHandler();
                                declareConsumer(NoInterest.INSTANCE, producerAvailabilityHandler);

                                declareProducer(AllInterest.INSTANCE);

                                verify(producerAvailabilityHandler, never()).get();
                            });
                        });
                        describe("are unavailable", () -> {
                            it("before consumer declaration", () -> {
                                Runnable producerUnavailabilityHandler = mock(Runnable.class);
                                declareConsumer(NoInterest.INSTANCE, producerUnavailabilityHandler);

                                verify(producerUnavailabilityHandler, never()).run();
                            });

                            it("after consumer declaration", () -> {
                                // The producer we will remove
                                VortexEmitter producer = declareProducer(AllInterest.INSTANCE);

                                Runnable producerUnavailabilityHandler = mock(Runnable.class);
                                declareConsumer(NoInterest.INSTANCE,  producerUnavailabilityHandler);

                                context().node().retireProducer(producer);

                                verify(producerUnavailabilityHandler, never()).run();
                            });
                        });
                    });

                    it("when matching producers are unavailable before consumer declaration", () -> {
                        Runnable producerUnavailabilityHandler = mock(Runnable.class);
                        declareConsumer(NoInterest.INSTANCE,  producerUnavailabilityHandler);

                        verify(producerUnavailabilityHandler, never()).run();
                    });

                    describe("when interest change", () -> {
                        it("doesn't make un-matching producers into matching producers",()->{
                            declareProducer(AllInterest.INSTANCE);

                            Supplier<Consumer<Object>> producerAvailabilityHandler = mockConsumerAvailabilityHandler();
                            ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(NoInterest.INSTANCE, producerAvailabilityHandler);
                            context().node().declareConsumer(consumerManifest);

                            consumerManifest.changeInterest(NoInterest.INSTANCE);

                            verify(producerAvailabilityHandler, never()).get();
                        });
                        it("doesn't make matching producers into un-matching producers",()->{
                            declareProducer(AllInterest.INSTANCE);

                            Runnable producerUnavailabilityHandler = mock(Runnable.class);
                            ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(AllInterest.INSTANCE, mock(Supplier.class), producerUnavailabilityHandler);
                            context().node().declareConsumer(consumerManifest);

                            consumerManifest.changeInterest(AllInterest.INSTANCE);

                            verify(producerUnavailabilityHandler, never()).run();
                        });
                    });

                });
            });

            describe("producers", () -> {
                describe("are notified", () -> {
                    describe("when matching consumers", () -> {
                        describe("are available", () -> {
                            it("before producer declaration", () -> {
                                declareConsumer(AllInterest.INSTANCE);

                                // Mocked handler to verify
                                Consumer<Consumer<Object>> consumerAvailabilityHandler = mock(Consumer.class);
                                declareProducer(AllInterest.INSTANCE, consumerAvailabilityHandler);

                                // Our handler gets called
                                verify(consumerAvailabilityHandler).accept(any(Consumer.class));
                            });
                            it("after consumer declaration", () -> {
                                Consumer<Consumer<Object>> consumerAvailabilityHandler = mock(Consumer.class);
                                declareProducer(AllInterest.INSTANCE, consumerAvailabilityHandler);

                                declareConsumer(AllInterest.INSTANCE);

                                verify(consumerAvailabilityHandler).accept(any(Consumer.class));
                            });
                        });
                        it("become unavailable after producer declaration", () -> {
                            VortexReceiver consumer = declareConsumer(AllInterest.INSTANCE);

                            Runnable consumerUnavailabilityHandler = mock(Runnable.class);
                            declareProducer(AllInterest.INSTANCE, consumerUnavailabilityHandler);

                            context().node().retireConsumer(consumer);

                            verify(consumerUnavailabilityHandler).run();
                        });
                    });
                    describe("when interest change", () -> {
                        it("makes un-matching consumers into matching",()->{
                            declareConsumer(AllInterest.INSTANCE);

                            Consumer<Consumer<Object>> consumerAvailabilityHandler = mock(Consumer.class);
                            EmitterManifestImpl producerManifest = EmitterManifestImpl.create(NoInterest.INSTANCE, consumerAvailabilityHandler);
                            context().node().declareProducer(producerManifest);

                            producerManifest.changeInterest(AllInterest.INSTANCE);

                            verify(consumerAvailabilityHandler).accept(any(Consumer.class));
                        });
                        it("makes matching consumers into un-matching",()->{
                            declareConsumer(AllInterest.INSTANCE);


                            Runnable consumerUnavailabilityHandler = mock(Runnable.class);
                            EmitterManifestImpl producerManifest = EmitterManifestImpl.create(AllInterest.INSTANCE, mock(Consumer.class), consumerUnavailabilityHandler);
                            context().node().declareProducer(producerManifest);

                            producerManifest.changeInterest(NoInterest.INSTANCE);

                            verify(consumerUnavailabilityHandler).run();
                        });
                    });
                });

                describe("are not notified", () -> {
                    describe("when un-matching consumers", () -> {
                        describe("are available", () -> {
                            it("before producer declaration", () -> {
                                declareConsumer(AllInterest.INSTANCE);

                                Consumer<Consumer<Object>> consumerAvailabilityHandler = mock(Consumer.class);
                                declareProducer(NoInterest.INSTANCE, consumerAvailabilityHandler);

                                verify(consumerAvailabilityHandler, never()).accept(any(Consumer.class));
                            });

                            it("after producer declaration", () -> {
                                Consumer<Consumer<Object>> consumerAvailabilityHandler = mock(Consumer.class);
                                declareProducer(NoInterest.INSTANCE, consumerAvailabilityHandler);

                                declareConsumer(AllInterest.INSTANCE);

                                verify(consumerAvailabilityHandler, never()).accept(any(Consumer.class));
                            });
                        });
                        describe("are unavailable", () -> {
                            it("before producer declaration", () -> {
                                Runnable consumerUnavailabilityHandler = mock(Runnable.class);
                                declareConsumer(NoInterest.INSTANCE, consumerUnavailabilityHandler);

                                verify(consumerUnavailabilityHandler, never()).run();
                            });

                            it("after producer declaration", () -> {
                                // The producer we will remove
                                VortexReceiver consumer = declareConsumer(AllInterest.INSTANCE);

                                Runnable consumerUnavailabilityHandler = mock(Runnable.class);
                                declareProducer(NoInterest.INSTANCE, consumerUnavailabilityHandler);

                                context().node().retireConsumer(consumer);

                                verify(consumerUnavailabilityHandler, never()).run();
                            });
                        });
                    });

                    it("when matching consumers are unavailable before producer declaration", () -> {
                        Runnable consumerUnavailabilityHandler = mock(Runnable.class);
                        declareProducer(NoInterest.INSTANCE, consumerUnavailabilityHandler);

                        verify(consumerUnavailabilityHandler, never()).run();
                    });

                    describe("when interest change", () -> {
                        it("doesn't make un-matching consumers into matching",()->{
                            declareConsumer(AllInterest.INSTANCE);

                            Consumer<Consumer<Object>> consumerAvailabilityHandler = mock(Consumer.class);
                            EmitterManifestImpl producerManifest = EmitterManifestImpl.create(NoInterest.INSTANCE, consumerAvailabilityHandler);
                            context().node().declareProducer(producerManifest);

                            producerManifest.changeInterest(NoInterest.INSTANCE);

                            verify(consumerAvailabilityHandler, never()).accept(any(Consumer.class));
                        });
                        it("doesn't make matching consumers into un-matching",()->{
                            declareConsumer(AllInterest.INSTANCE);

                            Runnable consumerUnavailabilityHandler = mock(Runnable.class);
                            EmitterManifestImpl producerManifest = EmitterManifestImpl.create(NoInterest.INSTANCE, mock(Consumer.class), consumerUnavailabilityHandler);
                            context().node().declareProducer(producerManifest);

                            producerManifest.changeInterest(AllInterest.INSTANCE);

                            verify(consumerUnavailabilityHandler, never()).run();
                        });
                    });

                });
            });
        });
    }

    private VortexReceiver declareConsumer(VortexInterest interest) {
        ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(interest, mockConsumerAvailabilityHandler());
        return context().node().declareConsumer(consumerManifest);
    }

    private void declareConsumer(VortexInterest interest, Supplier<Consumer<Object>> producerAvailabilityHandler) {
        ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(interest, producerAvailabilityHandler);
        context().node().declareConsumer(consumerManifest);
    }

    private void declareConsumer(VortexInterest interest, Runnable producerUnavailabilityHandler) {
        ReceiverManifestImpl consumerManifest = ReceiverManifestImpl.create(interest, mock(Supplier.class), producerUnavailabilityHandler);
        context().node().declareConsumer(consumerManifest);
    }


    private Supplier<Consumer<Object>> mockConsumerAvailabilityHandler() {
        Supplier<Consumer<Object>> producerAvailabilityHandler = mock(Supplier.class);
        when(producerAvailabilityHandler.get()).thenReturn(mock(Consumer.class));
        return producerAvailabilityHandler;
    }

    private VortexEmitter declareProducer(VortexInterest producerInterest) {
        EmitterManifestImpl producerManifest = EmitterManifestImpl.create(producerInterest, mock(Consumer.class));
        return context().node().declareProducer(producerManifest);
    }

    private VortexEmitter declareProducer(VortexInterest producerInterest, Consumer<Consumer<Object>> consumerAvailabilityHandler) {
        EmitterManifestImpl producerManifest = EmitterManifestImpl.create(producerInterest, consumerAvailabilityHandler);
        return context().node().declareProducer(producerManifest);
    }

    private VortexEmitter declareProducer(VortexInterest producerInterest, Runnable consumerUnavailabilityHandler ) {
        EmitterManifestImpl producerManifest = EmitterManifestImpl.create(producerInterest, mock(Consumer.class), consumerUnavailabilityHandler);
        return context().node().declareProducer(producerManifest);
    }

}
