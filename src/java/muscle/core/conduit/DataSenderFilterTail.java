/*
 * 
 */
package muscle.core.conduit;

import muscle.core.conduit.filter.FilterTail;
import muscle.core.messaging.BasicRemoteDataSinkHead;
import muscle.core.messaging.RemoteDataSinkHead;
import muscle.core.messaging.jade.ObservationMessage;
import muscle.exception.MUSCLERuntimeException;

/**
this behaviour is the connection to the (remote) ConduitExit and sends data messages to it
 */
/**
 *
 * @author Joris Borgdorff
 */
public class DataSenderFilterTail extends FilterTail<ObservationMessage> {
	private final RemoteDataSinkHead<ObservationMessage<?>> sink;
	private final BasicConduit conduit;
	private boolean shouldPause = false;

	public DataSenderFilterTail(BasicConduit conduit) {
		this.conduit = conduit;
		sink = new BasicRemoteDataSinkHead<ObservationMessage<?>>(conduit.exitName, conduit.exitAgent) {
			@Override
			public void put(ObservationMessage dmsg) {
				if (shouldPause) {
					this.waitForResume();
				}
				DataSenderFilterTail.this.conduit.sendMessage(dmsg);
			}
			
			private synchronized void waitForResume() {
				while (shouldPause) {
					try {
						this.wait();
					} catch (InterruptedException ex) {
						throw new MUSCLERuntimeException(ex);
					}
				}
			}

			@Override
			public void pause() {
				shouldPause = true;
			}

			@Override
			public synchronized void resume() {
				shouldPause = false;
				this.notifyAll();
			}
		};
		DataSenderFilterTail.this.conduit.addSink(sink);
	}

	@Override
	public void result(ObservationMessage dmsg) {
		dmsg.clearAllReceiver();
		dmsg.addReceiver(conduit.exitAgent);
		dmsg.setSender(conduit.getAID());
		sink.put(dmsg);
	}
	
}
