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

package muscle.core.model;

import java.io.Serializable;

/**
container for a data message
@author Joris Borgdorff
*/
public class Observation<T extends Serializable> implements Serializable {
	private final Timestamp siTime; // global time where this data belongs to (may be null)
	private final Timestamp nextSITime; // time of the next observation
	private final T data; // our unwrapped data

	public Observation(T newData, Timestamp newSITime, Timestamp newNextSITime) {
		siTime = newSITime;
		data = newData;
		nextSITime = newNextSITime;
	}

	public T getData() {
		return data;
	}

	public Timestamp getTimestamp() {
		return siTime;
	}

	public Timestamp getNextTimestamp() {
		return nextSITime;
	}
	
	public <R extends Serializable> Observation<R> copyWithNewData(R newData) {
		return new Observation<R>(newData, siTime, nextSITime);
	}

	public String toString() {
		return "Observation<"+siTime+",type="+data.getClass().getSimpleName()+">";
	}	
}
