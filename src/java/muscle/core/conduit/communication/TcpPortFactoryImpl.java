/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package muscle.core.conduit.communication;

import java.io.Serializable;
import java.util.concurrent.Callable;
import muscle.core.ConduitEntranceController;
import muscle.core.ConduitExitController;
import muscle.core.PortFactory;
import muscle.core.ident.PortalID;
import muscle.core.ident.ResolverFactory;
import muscle.core.kernel.InstanceController;
import muscle.core.messaging.serialization.BasicMessageConverter;
import muscle.core.messaging.serialization.ObservationConverter;
import muscle.core.messaging.serialization.SerializableDataConverter;
import muscle.net.SocketFactory;

/**
 *
 * @author Joris Borgdorff
 */
public class TcpPortFactoryImpl extends PortFactory {
	private final SocketFactory socketFactory;
		
	public TcpPortFactoryImpl(ResolverFactory rf, SocketFactory sf, IncomingMessageProcessor msgProcessor) {
		super(rf, msgProcessor);
		this.socketFactory = sf;
	}

	@Override
	protected <T extends Serializable> Callable<Receiver<T, ?, ?, ?>> getReceiverTask(final ConduitExitController exit, final PortalID port) {
		return new Callable<Receiver<T,?,?,?>>() {
			public Receiver<T, ?, ?, ?> call() throws Exception {
				exit.start();
				resolvePort(port);
			
				TcpReceiver recv = new TcpReceiver();
				recv.setDataConverter(new BasicMessageConverter(new SerializableDataConverter()));
				recv.setComplementaryPort(port);
				exit.setReceiver(recv);
			
				messageProcessor.addReceiver(exit.getIdentifier(), recv);
				
				return recv;
			}
		};
	}

	@Override
	protected <T extends Serializable> Callable<Transmitter<T, ?, ?, ?>> getTransmitterTask(InstanceController ic, final ConduitEntranceController entrance, final PortalID port) {
		return new Callable<Transmitter<T,?,?,?>>() {
			public Transmitter<T, ?, ?, ?> call() throws Exception {
				entrance.start();
				resolvePort(port);
				Transmitter trans;
//				if (TcpPortFactoryImpl.this.resolverFactory.getResolver().isLocal(port)) {
//					trans = new LocalTransmitter();
//				} else {
					trans = new XdrTcpTransmitter(socketFactory);
//				}
			
				trans.setDataConverter(new ObservationConverter(new SerializableDataConverter()));
				trans.setComplementaryPort(port);
				entrance.setTransmitter(trans);
				return trans;
			}
		};
	}
}