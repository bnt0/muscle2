/* !!! this file was generated automatically from <Array3D_double.java> DO NOT EDIT */
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

package utilities.array3d;	//edit Array3D_double.java:1 instead

import java.lang.reflect.Array;	//edit Array3D_double.java:2 instead
import java.lang.reflect.Constructor;	//edit Array3D_double.java:3 instead
import java.util.Arrays;	//edit Array3D_double.java:4 instead

// TODO: inherit functionality from cern.colt.matrix.BooleanMatrix3D?
/**
3D array backed by a 1D C-style array of primitive type
@author Jan Hegewald
*/
public class Array3D_boolean {

	private boolean[] data;	//edit Array3D_double.java:5 instead
	private int xSize;	//edit Array3D_double.java:6 instead
	private int ySize;	//edit Array3D_double.java:7 instead
	private int zSize;	//edit Array3D_double.java:8 instead
	private IndexStrategy indexStrategy;	//edit Array3D_double.java:9 instead

	
	//
	public Array3D_boolean(int newXSize, int newYSize, int newZSize) {
	
		this(newXSize, newYSize, newZSize, new boolean[newXSize*newYSize*newZSize], IndexStrategy.FortranIndexStrategy.class);	//edit Array3D_double.java:10 instead
	}

	//
	public Array3D_boolean(int newXSize, int newYSize, int newZSize, boolean[] newData) {
	
		this(newXSize, newYSize, newZSize, newData, IndexStrategy.FortranIndexStrategy.class);	//edit Array3D_double.java:11 instead
	}

	//
	public Array3D_boolean(int newXSize, int newYSize, int newZSize, Class<? extends IndexStrategy> strategyClass) {
	
		this(newXSize, newYSize, newZSize, new boolean[newXSize*newYSize*newZSize], strategyClass);	//edit Array3D_double.java:12 instead
	}


	//
	public Array3D_boolean(int newXSize, int newYSize, int newZSize, boolean[] newData, Class<? extends IndexStrategy> strategyClass) {
	
		xSize = newXSize;	//edit Array3D_double.java:13 instead
		ySize = newYSize;	//edit Array3D_double.java:14 instead
		zSize = newZSize;	//edit Array3D_double.java:15 instead

		data = newData;	//edit Array3D_double.java:16 instead
		
		if( !IndexStrategy.class.isAssignableFrom(strategyClass) )
			throw new IllegalArgumentException("index strategy must be a "+javatool.ClassTool.getName(IndexStrategy.class));	//edit Array3D_double.java:17 instead
			
		
		Constructor<? extends IndexStrategy> strategyConstructor = null;	//edit Array3D_double.java:18 instead
		try {
			strategyConstructor = strategyClass.getConstructor(int.class, int.class, int.class); // simply assume this constructor is indeed available
		}
		catch(java.lang.NoSuchMethodException e) {
			throw new RuntimeException(e);	//edit Array3D_double.java:19 instead
		}
		try {
			indexStrategy = strategyConstructor.newInstance(newXSize, newYSize, newZSize);	//edit Array3D_double.java:20 instead
		}
		catch(java.lang.InstantiationException e) {
			throw new RuntimeException(e);	//edit Array3D_double.java:21 instead
		}
		catch(java.lang.IllegalAccessException e) {
			throw new RuntimeException(e);	//edit Array3D_double.java:22 instead
		}
		catch(java.lang.reflect.InvocationTargetException e) {
			throw new RuntimeException(e);	//edit Array3D_double.java:23 instead
		}
	}
	
	
	//
	public void fill(boolean value) {
		
		Arrays.fill(data, value);	//edit Array3D_double.java:24 instead
	}


	//
	public boolean get(int x1, int x2, int x3) {

		return data[indexStrategy.index(x1, x2, x3)];	//edit Array3D_double.java:25 instead
	}
	
	//
	public boolean[] getData() {

		return data;	//edit Array3D_double.java:26 instead
	}


	//
	public void set(int x1, int x2, int x3, boolean value) {

		data[indexStrategy.index(x1, x2, x3)] = value;	//edit Array3D_double.java:27 instead
	}

	
	//
	public int getSize() {

		return data.length;	//edit Array3D_double.java:28 instead
	}

	//
	public int getX1Size() {

		return xSize;	//edit Array3D_double.java:29 instead
	}

	//
	public int getX2Size() {

		return ySize;	//edit Array3D_double.java:30 instead
	}

	//
	public int getX3Size() {

		return zSize;	//edit Array3D_double.java:31 instead
	}
}

/* !!! this file was generated automatically from <Array3D_double.java> DO NOT EDIT */
