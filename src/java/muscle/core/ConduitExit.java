/*
Copyright 2008,2009 Complex Automata Simulation Technique (COAST) consortium

GNU Lesser General Public License

This file is part of MUSCLE (Multiscale Coupling Library and Environment).

    MUSCLE is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MUSCLE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with MUSCLE.  If not, see <http://www.gnu.org/licenses/>.
*/

package muscle.core;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import muscle.core.model.Observation;
import muscle.exception.MUSCLEConduitExhaustedException;
import muscle.id.PortalID;
import muscle.util.data.Takeable;
import muscle.util.logging.ActivityListener;
import muscle.util.logging.ActivityProtocol;

/**
 * A ConduitExit outputs messages that are sent over a conduit.
 * Messages are accessed over the blocking receive() function, which returns only messages of a single type.
 * When no more messages will be sent over the conduit, due to the conduit stopping,
 * hasNext() will return false and receive() will throw an exception.
 * It is not thread-safe, in the sense that only a single thread may call hasNext() and receive().
 * 
 * @author Joris Borgdorff
 * @param <T> Type of data to be received from the conduit
*/
public class ConduitExit<T extends Serializable> { // generic T will be the underlying unwrapped data, e.g. double[]
	private final Takeable<Observation<T>> queue;
	private final ConduitExitController<T> controller;
	private final static Logger logger = Logger.getLogger(ConduitExit.class.getName());
	private boolean isDone;
	private Observation<T> nextElem;
	private ActivityListener actLogger;
	private PortalID id;
	
	public ConduitExit(ConduitExitController<T> control) {
		this.queue = control.getMessageQueue();
		this.controller = control;
		this.isDone = false;
		this.nextElem = null;
		this.actLogger = null;
		this.id = null;
	}

	/**
	 * Whether the next call to hasNext or receive will return without blocking.
	 * This is a non-blocking function. If it returns true, that does not guarantee that hasNext returns true.
	 * @return if and only if true, receive() will return without blocking
	 */
	public boolean ready() {
		return this.nextElem != null || !this.queue.isEmpty();
	}
	
	/**
	 * Whether there will be a next piece of data. It is a blocking function,
	 * waiting for the next message or a signal that no more messages will come.
	 * If the sending end has stopped, the result is false. As long as receive()
	 * is not called, subsequent calls to hasNext() return the same result.
	 * After hasNext() returns true, the subsequent call to receive() is
	 * guaranteed to return a result. Conversely, if hasNext() returns false,
	 * the subsequent call to receive() will throw a
	 * MUSCLEConduitExhaustedException.
	 * @return true if and only if there is a next piece of data
	 */
	public boolean hasNext() {
		// As long as receive() is not called, return true.
		if (this.nextElem != null) {
			return true;
		}
		if (this.isDone) {
			return false;
		}
		try {
			if (actLogger != null) actLogger.activity(ActivityProtocol.BEGIN_RECEIVE, id);
			this.nextElem = this.queue.take();
			if (actLogger != null) actLogger.activity(ActivityProtocol.END_RECEIVE, id);
		} catch (InterruptedException ex) {
			logger.log(Level.WARNING, "Receiving message interrupted.", ex);
		}
		if (this.nextElem == Observation.EMPTY) {
			this.nextElem = null;
			this.isDone = true;
		}
		
		return !isDone;
	}
	
	/**
	 * Receive one piece of data.
	 * The call is blocking, meaning that it won't return until data is
	 * received. Data returned does not need to be copied.
	 * 
	 * @throws MUSCLEConduitExhaustedException if hasNext would produce false.
	 * @return a piece of data
	 */
	public T receive() {
		return receiveObservation().getData();
	}
	
	/**
	 * Receive one observation, containing data, the timestamp it belongs to and the timestamp of the next message.
	 * The call is blocking, meaning that it won't return until data is
	 * received. Data returned does not need to be copied.
	 * 
	 * @throws MUSCLEConduitExhaustedException if hasNext would produce false.
	 * @return a piece of data
	 */
	public Observation<T> receiveObservation() {
		if (!hasNext()) {
			if (actLogger != null) actLogger.activity(ActivityProtocol.RECEIVE_FAILED, id);			
			throw new MUSCLEConduitExhaustedException("Can not receive from conduit: the other submodel has stopped.");
		}
		
		// Indicate to the controller that the message is received, so it can
		// do last minute edits.
		this.controller.messageReceived(nextElem);
		Observation<T> tmp = this.nextElem;
		this.nextElem = null;
		return tmp;
	}
	
	@Override
	public String toString() {
		return this.controller.toString();
	}
	
	public void setActivityLogger(ActivityListener actLogger, PortalID id) {
		this.actLogger = actLogger;
		this.id = id;
	}
}
