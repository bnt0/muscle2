/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package muscle.client.communication;

import java.io.Serializable;
import java.util.concurrent.Callable;
import muscle.client.communication.message.JadeIncomingMessageProcessor;
import muscle.client.id.JadeIdentifier;
import muscle.client.id.JadePortalID;
import muscle.client.instance.ConduitEntranceControllerImpl;
import muscle.client.instance.ConduitExitControllerImpl;
import muscle.client.instance.JadeInstanceController;
import muscle.client.instance.ThreadedConduitExitController;
import muscle.core.kernel.InstanceController;
import muscle.core.model.Observation;
import muscle.id.PortalID;
import muscle.id.ResolverFactory;
import muscle.util.serialization.ByteJavaObjectConverter;
import muscle.util.serialization.PipeConverter;

/**
 *
 * @author Joris Borgdorff
 */
public class JadePortFactoryImpl extends PortFactory {
	public JadePortFactoryImpl(ResolverFactory rf, JadeIncomingMessageProcessor msgProcessor) {
		super(rf, msgProcessor);
	}
	
	@Override
	protected <T extends Serializable> Callable<Receiver<T, ?>> getReceiverTask(ConduitExitControllerImpl<T> localInstance, PortalID otherSide) {
		return new JadePortFactoryImpl.ReceiverTask<T>(localInstance, otherSide);
	}

	@Override
	protected <T extends Serializable> Callable<Transmitter<T, ?>> getTransmitterTask(InstanceController ic, ConduitEntranceControllerImpl<T> localInstance, PortalID otherSide) {
		return new JadePortFactoryImpl.TransmitterTask<T>(ic, localInstance, otherSide);
	}
	
	private class ReceiverTask<T extends Serializable> implements Callable<Receiver<T,?>> {
		private final JadePortalID port;
		private final ThreadedConduitExitController<T> exit;

		public ReceiverTask(ConduitExitControllerImpl<T> exit, PortalID other) {
			if (exit instanceof ThreadedConduitExitController) {
				this.exit = (ThreadedConduitExitController<T>)exit;
			} else {
				throw new IllegalArgumentException("Only threaded conduit exits can be created by JadePortFactoryImpl.");
			}
			if (other instanceof JadePortalID) {
				this.port = (JadePortalID) other;
			} else {
				throw new IllegalArgumentException("Only JADE portals can be created by JadePortFactoryImpl.");
			}
		}
		@Override
		@SuppressWarnings("unchecked")
		public Receiver<T, ?> call() throws Exception {
			exit.start();
			
			//resolvePort(port);
			
			@SuppressWarnings("unchecked")
			Receiver<T,T> recv = new JadeReceiver(new PipeConverter(), port);
			exit.setReceiver(recv);
			
			messageProcessor.addReceiver(exit.getIdentifier(), recv);
			
			return recv;
		}
	}
	
	
	private class TransmitterTask<T extends Serializable> implements Callable<Transmitter<T,?>> {
		private final JadePortalID port;
		private final JadeInstanceController instance;
		private final ConduitEntranceControllerImpl<T> entrance;

		public TransmitterTask(InstanceController inst, ConduitEntranceControllerImpl<T> entrance, PortalID other) {
			if (inst instanceof JadeInstanceController) {
				this.instance = (JadeInstanceController)inst;
			} else {
				throw new IllegalArgumentException("Only JADE InstanceControllers can be created by JadePortFactoryImpl.");
			}
			this.entrance = entrance;
			if (other instanceof JadePortalID) {
				this.port = (JadePortalID) other;
			} else {
				throw new IllegalArgumentException("Only JADE portals can be created by JadePortFactoryImpl.");
			}
		}
		@Override
		public Transmitter<T, ?> call() throws Exception {
			entrance.start();
			resolvePort(port);
			
			Transmitter<T,byte[]> trans = new JadeTransmitter<T>(instance, new ByteJavaObjectConverter<Observation<T>>(), port);
			entrance.setTransmitter(trans);
			
			return trans;
		}
	}
}
