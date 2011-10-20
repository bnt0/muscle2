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

import muscle.Constant;
import utilities.MiscTool;
import utilities.Invoker;
import java.util.logging.Logger;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import muscle.Version;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import utilities.JVM;


/**
handle booting/terminating of MUSCLE
@author Jan Hegewald
*/
public class Boot {

	private List<Thread> otherHooks = new LinkedList<Thread>();
	private File infoFile;
	

	//
	static {

		// try to workaround LogManager deadlock http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6487638
		java.util.logging.LogManager manager = java.util.logging.LogManager.getLogManager();
		// n.b.: we still sometimes see the LogManager deadlock
	}

	
	//
	public Boot(String[] args) {
		
		System.out.println("booting muscle jvm "+java.lang.management.ManagementFactory.getRuntimeMXBean().getName());
		// make sure the JVM singleton has been inited
		JVM jvm = JVM.ONLY;
		
		infoFile = new File(MiscTool.joinPaths(JVM.ONLY.tmpDir().toString(), Constant.Filename.JVM_INFO));
		
//		System.out.println("my args are <"+MiscTool.joinItems(args, ", ")+">");
				
		// note: it seems like loggers can not be used within shutdown hooks
		Runtime.getRuntime().addShutdownHook(new JVMHook() );

		writeInitialInfo();


		JADE jade = new JADE(args);
		otherHooks.addAll(jade.getShutdownHooks());
	}


	//
	public static void main(String args[]) {
		
		new Boot(args);
//		jade.Boot.main(args); // forward booting to jade
	}
	
	
	//
	private void writeInitialInfo() {
	
		String nl = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
			writer = new FileWriter(infoFile);
			
			writer.write( "this is file <"+infoFile+"> created by <"+getClass()+">"+nl );
			writer.write( "start date: "+(new java.util.Date())+nl );
			writer.write( "cwd: "+System.getProperty("user.dir")+nl );
			writer.write( "user: "+System.getProperty("user.name")+nl );
			OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
			writer.write( "OS: "+os.getName()+" "+os.getVersion()+nl );
			writer.write( "CPU: "+os.getAvailableProcessors()+" "+os.getArch()+nl );
			writer.write(nl);
			writer.write( "name: "+ManagementFactory.getRuntimeMXBean().getName()+nl );
			writer.write( "version: "+Version.info()+nl );			
			writer.write( "vm: "+ManagementFactory.getRuntimeMXBean().getVmName()+" "+ManagementFactory.getRuntimeMXBean().getVmVersion()+nl );
			writer.write( "classpath: "+ManagementFactory.getRuntimeMXBean().getClassPath()+nl );
			writer.write( "JADE version: "+jade.core.Runtime.getVersionInfo()+nl );			
			writer.write( "arguments: "+MiscTool.joinItems(ManagementFactory.getRuntimeMXBean().getInputArguments(), System.getProperty("path.separator"))+nl );			
			writer.write(nl);
			writer.write( "executing ..."+nl );
			
			writer.close();
		}
		catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if(writer != null) {
				try {
					writer.close();
				}
				catch (java.io.IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}


	//
	private void writeClosingInfo() {
	
		String nl = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
			writer = new FileWriter(infoFile, true);
			
			writer.write(nl);
			writer.write( "... terminating"+nl );
			writer.write(nl);
			writer.write( "uptime: "+ManagementFactory.getRuntimeMXBean().getUptime()/1000.0+" s"+nl );
			writer.write( "end date: "+(new java.util.Date())+nl );
			
			writer.close();
		}
		catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if(writer != null) {
				try {
					writer.close();
				}
				catch (java.io.IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}


	//
	private void writeStackTraces() {
	
		String nl = System.getProperty("line.separator");
		FileWriter writer = null;
		try {
			writer = new FileWriter(infoFile, true);
			
			writer.write(nl);
			writer.write( "thread dump"+nl );
			writer.write(nl);
			
			ThreadMXBean tBean = ManagementFactory.getThreadMXBean();
			ThreadInfo[] tInfos = tBean.getThreadInfo(tBean.getAllThreadIds());
			for(ThreadInfo ti : tInfos) {
				writer.write(ti.toString()+nl);
			}
			
			writer.close();
		}
		catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if(writer != null) {
				try {
					writer.close();
				}
				catch (java.io.IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	
	//
	private class JVMHook extends Thread {
			
		// CTRL-C is signal 2 (SIGINT)
		// see rubys Signal.list for a full list on your OS
		public void run() {
			
			System.out.println("terminating muscle jvm "+java.lang.management.ManagementFactory.getRuntimeMXBean().getName());
			writeClosingInfo();

			// wait for all other threads/shutdownhooks to die
			while( !otherHooks.isEmpty() ) {

				for(Iterator<Thread> iter = otherHooks.iterator(); iter.hasNext(); ) {
					Thread t = iter.next();
					if( !t.isAlive() )
						iter.remove();
				}
				
				if( !otherHooks.isEmpty() ) {
					System.out.print(".");
					try {
						sleep(50);
					}
					catch(java.lang.InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			writeStackTraces();
			System.out.println("bye");
		}		
	}

}
