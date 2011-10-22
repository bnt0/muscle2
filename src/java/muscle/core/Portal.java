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

import muscle.core.ident.PortalID;
import java.io.Serializable;
import javax.measure.DecimalMeasure;
import javax.measure.quantity.Duration;
import java.math.BigDecimal;
import javatool.DecimalMeasureTool;
import muscle.core.kernel.InstanceController;
import muscle.core.messaging.Timestamp;
import utilities.SafeThread;

//
public abstract class Portal<T> extends SafeThread implements Serializable {
	protected final PortalID portalID;
	private int usedCount;
	protected Timestamp customSITime;
	protected final static long WAIT_FOR_ATTACHMENT_MILLIS = 10000l;
	
	Portal(PortalID newPortalID, InstanceController newOwnerAgent, int newRate, DataTemplate newDataTemplate) {
		portalID = newPortalID;
	
		// set custom time to 0
		customSITime = new Timestamp(0d);
		usedCount = 0;
	}

	// remove this in favor of the close method?
	public synchronized void dispose() {
		this.isDone = true;
		this.notifyAll();
	}

	public String getLocalName() {
		return portalID.getName();
	}

	public PortalID getPortalID() {
		return portalID;
	}

	/**
	current time of this portal in SI units
	 */
	public Timestamp getSITime() {
		return customSITime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) return false;
		
		return ((Portal) obj).portalID.equals(portalID);
	}

	@Override
	public int hashCode() {
		return portalID.hashCode();
	}

	@Override
	public String toString() {
		return getLocalName() + " used: " + usedCount + " SI time: " + getSITime();
	}

	void increment() {
		usedCount++;
	}
}
