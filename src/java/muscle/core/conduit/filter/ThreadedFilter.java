/*
* Copyright 2008, 2009 Complex Automata Simulation Technique (COAST) consortium
* Copyright 2010-2013 Multiscale Applications on European e-Infrastructures (MAPPER) project
*
* GNU Lesser General Public License
* 
* This file is part of MUSCLE (Multiscale Coupling Library and Environment).
* 
* MUSCLE is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* MUSCLE is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with MUSCLE.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * 
 */
package muscle.core.conduit.filter;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import muscle.client.LocalManager;
import muscle.core.model.Observation;
import muscle.util.concurrency.SafeThread;
import muscle.util.data.SerializableData;
import muscle.util.serialization.DataConverter;
import muscle.util.serialization.SerializableDataConverter;

/**
 *
 * @author Joris Borgdorff
 */
public class ThreadedFilter<E extends Serializable> extends SafeThread implements Filter<E,E> {
	protected final BlockingQueue<Observation<E>> incomingQueue;
	private final DataConverter<E,SerializableData> converter;
	private Filter<E, ?> nextFilter;
	private boolean processing;

	protected ThreadedFilter() {
		super("Filter");
		this.incomingQueue = new LinkedBlockingQueue<Observation<E>>();
		this.converter = new SerializableDataConverter<E>();
	}
	
	@Override
	protected synchronized boolean continueComputation() throws InterruptedException {
		while (!isDisposed() && incomingQueue.isEmpty()) {
			wait();
		}
		return !isDisposed();
	}
	
	@Override
	protected void execute() throws InterruptedException {
		Observation<E> message;
		synchronized (this) {
			this.processing = true;
			message = incomingQueue.remove();
		}
		if (message != null) {
			nextFilter.queue(message);
			nextFilter.apply();
		}
		synchronized (this) {
			processing = false;
			notifyAll();
		}
	}
	
	/** Queue a message, without necessarily processing it. */
	@Override
	public void queue(Observation<E> obs) {
		Observation<E> obsCopy = obs.privateCopy(converter);
		synchronized (this) {
			incomingQueue.add(obsCopy);
			notifyAll();
		}
	}

	/** Apply the filter to at least all the messages queued so far. */
	@Override
	public synchronized void apply() {
		notifyAll();
	}
	
	/** A threadedfilter is processing when it is processing itself, or a thread further down the filterchain is. */
	@Override
	public synchronized boolean isProcessing() {
		return processing || !incomingQueue.isEmpty() || nextFilter.isProcessing();
	}
	
	@Override
	public synchronized boolean waitUntilEmpty() throws InterruptedException {
		while ((this.processing || !incomingQueue.isEmpty()) && !this.isDisposed()) {
			wait();
		}
		if (this.isDisposed()) {
			return false;
		}
		return nextFilter.waitUntilEmpty();
	}
	
	@Override
	protected void handleInterruption(InterruptedException ex) {
		Logger.getLogger(getClass().toString()).severe("Filter interrupted.");
	}
	
	@Override
	protected void handleException(Throwable ex) {
		Logger.getLogger(getClass().toString()).log(Level.SEVERE, "Filter had exception.");
		LocalManager.getInstance().fatalException(ex);
	}
	
	@Override
	public void setNextFilter(Filter<E,?> qc) {
		this.nextFilter = qc;
	}
}
