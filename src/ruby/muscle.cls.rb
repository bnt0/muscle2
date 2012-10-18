require 'utilities'
include MuscleUtils

PROP_PORT_RANGE_MIN = "pl.psnc.mapper.muscle.portrange.min"
PROP_PORT_RANGE_MAX = "pl.psnc.mapper.muscle.portrange.max"
PROP_MAIN_PORT = "pl.psnc.mapper.muscle.mainport"
PROP_DEBUG = "pl.psnc.mapper.muscle.debug"
PROP_TRACE = "pl.psnc.mapper.muscle.trace"
PROP_MTO_ADDRESS = "pl.psnc.mapper.muscle.mto.address"
PROP_MTO_PORT = "pl.psnc.mapper.muscle.mto.port"

class Muscle
	def initialize
		@@LAST = self
		@env = {}
		
		# set value for LIBPATHENV or abort
		assert_LIBPATHENV @env

		@env_basename = "muscle.env.rb"
		# load (machine specific) default env
		load_env(File.expand_path("#{PARENT_DIR}/#{@env_basename}"), true)
	end
	
	#
	def add_env(e)
		@env.merge!(e){|key, oldval, newval| 
				if(key == "CLASSPATH" && oldval != nil)
					oldval=newval+File::PATH_SEPARATOR+oldval
				else
					oldval=newval
				end}
	end

	# helper method to add path variables
	def add_path(hsh)
		hsh.each do |path_key,path|
			@env[path_key] = "" if @env[path_key] == nil
			if(path.class == Array)
				@env[path_key] = (@env[path_key].split(File::PATH_SEPARATOR) + path).join(File::PATH_SEPARATOR)
			else
				@env[path_key] = (@env[path_key].split(File::PATH_SEPARATOR) + path.split(File::PATH_SEPARATOR)).join(File::PATH_SEPARATOR)		
			end
			# delete any empty items
			@env[path_key] = ((@env[path_key].split(File::PATH_SEPARATOR)).delete_if {|x| x==''}).join(File::PATH_SEPARATOR)	
		end
	end


	def add_classpath(p)
		add_path("CLASSPATH"=>p)
	end


	def add_libpath(p)
		add_path("libpath"=>p)
		ENV[@env["LIBPATHENV"]] = @env["libpath"]
	end

	# overwrite env setting
	def set(hsh)
		hsh.each do |k,v|
			@env[k] = v
		end
	end

	def Muscle.jclass
		'muscle.Env'
	end
	
	def Muscle.LAST
		@@LAST
	end
	
	def print_env(keys)
		if keys == true
			# print the complete env (sorted)
			env.keys.sort.each {|k| puts "#{k.inspect}=>#{env[k].inspect}"}
		else
			# print value for the specified key(s)
			if(keys.size == 1)
				# print raw value if output is for a single key (useful if you want to further process the output, e.g. CLASSPATH)
				puts env[keys.first] if env.has_key? keys.first
			else
				keys.each {|k| puts "#{k.inspect}=>#{env[k].inspect}" if env.has_key? k}
			end
		end
	end
	
	def run_manager(args)
		tmpXmx = env['Xmx']
		tmpXms = env['Xms']
		env['Xms'] = '20m'
		env['Xmx'] = '100m'
		command = JVM.build_command(args, env).first
		env['Xms'] = tmpXms
		env['Xmx'] = tmpXmx
		puts "=== Running MUSCLE2 Simulation Manager ==="
		if env['verbose']
			puts "Executing: #{command}"
		end
		Process.fork {exec(command)}
	end
	
	def find_manager_contact(manager_pid, contact_file_name = nil)
		if not contact_file_name
			contact_file_name = env['tmp_path'] + "/simulationmanager.#{manager_pid}.address"
		end
		tries_count = 0
		while !File.exists?(contact_file_name)
			if Process::waitpid(manager_pid, Process::WNOHANG)
				if not $?
					puts "Simulation Manager exited with an error."
					exit 1
				elsif $?.exitstatus != 0
					puts "Simulation Manager exited with an error."
					exit $?.exitstatus
				else
					puts "Simulation Manager exited before setting up connection."
					exit 0
				end
			end
			sleep 0.2
			tries_count += 1
			if tries_count % 25 == 0
				puts "Waiting for simulation manager to start listening, notified in file: #{contact_file_name}"
			end
		end

		while File.exists?(contact_file_name + ".lock") #waiting for lock file to disappear 
			if Process::waitpid(manager_pid, Process::WNOHANG)
				if not $?
					puts "\n\nSimulation Manager exited with an error."
					exit 1
				elsif $?.exitstatus != 0
					puts "\n\nSimulation Manager exited with an error."
					exit $?.exitstatus
				else
					puts "\n\nSimulation Manager exited before setting up connection."
					exit 0
				end
			end
			sleep 0.2
		end

		File.open(contact_file_name, "rb").read
	end
	
	def run_client(args, contact_addr)
		args << "-m"
		if env['manager']
				args << env['manager']
		else
			# main
			args << contact_addr
		end

		puts "=== Running MUSCLE2 Simulation ==="
		command = JVM.build_command(args, env).first
		if env['verbose']
			puts command
		end
		Process.fork {exec(command)}
	end
	
	def exec_native(kernel_name, extra_args)
		native_command = []
		cxa = Cxa.LAST
		if cxa.env[ kernel_name + ":mpiexec_command"] 
			native_command << cxa.env[ kernel_name + ":mpiexec_command"]
		end

		if cxa.env[ kernel_name + ":mpiexec_args"] 
			native_command << cxa.env[ kernel_name + ":mpiexec_args"].split(" ")
		end

		if cxa.env[kernel_name + ":debugger"] 
			native_command << cxa.env[ kernel_name + ":debugger"]
		end

		if cxa.env[ kernel_name + ":command"] 
			native_command << cxa.env[ kernel_name + ":command"]
		else
			puts "Missing #{kernel_name}:command property"
			exit 1
		end

		if cxa.env[ kernel_name + ":args"] 
			puts "Args: #{cxa.env['#{kernel_name}:args']}" 
			native_command << cxa.env["#{kernel_name}:args"].split(" ")
		end

		native_command << "--"

		# Remove --native from subcommand
		extra_args.reject! {|x| x == "--native" || x == "-n"}
		
		# If a shorthand notation contained -n, remove it
		# but, first remove jvmflags, as this also will start with a single '-'
		i = extra_args.index "--jvmflags"
		if i
			extra_args.delete_at(i)
			native_command << "--jvmflags" << extra_args.delete_at(i)
		end
			
		# remove -anm or -nm or comparable
		extra_args.collect! { |x| if x =~ /^-(n|\w*n)/ then x.delete "n" else x end }

		native_command << extra_args
		
		command = native_command.join(" ")
		
		puts "Executing: #{command}"
		exec(command)
	end
	
	def exec_mpi(args)
		command = JVM.build_command(args, env).first
		puts "Executing: #{command}"
		exec(command)
	end
	
	def apply_intercluster
		port_min = env['port_min'] || ENV['MUSCLE_PORT_MIN']
		port_max = env['port_max'] || ENV['MUSCLE_PORT_MAX']
		if(port_min.nil? or port_max.nil?)
			puts "Warning: intercluster specified, but no local port range given."
			puts "Maybe $MUSCLE_HOME/etc/muscle.profile was not sourced and $MUSCLE_PORT_MIN or $MUSCLE_PORT_MAX were not set?"
			puts "To specify them manually, use the flags --port-min and --port-max."
			return false
		else
			mto =	env['mto'] || ENV['MUSCLE_MTO']
			if (! mto.nil?)
				mtoHost = mto.split(':')[0]
				mtoPort = mto.split(':')[1]
			end

			if(mtoPort.nil? or mtoHost.nil?)
				puts "Warning: intercluster specified, but no MTO address/port given."
				puts "Maybe $MUSCLE_HOME/etc/muscle.profile was not sourced and $MUSCLE_MTO was not set?"
				puts "To specify the MTO address manually, use the flag --mto."
				return false
			else
				if(env.has_key?('qcg'))
					if (env['main'])
						env['bindport'] = 22 #master
					else
						env['manager'] = "localhost:22" #slave
					end
				else
					env['localport'] = 0 
				end

				if(env["jvmflags"].nil?)
					env["jvmflags"] = Array.new
				end

				env["jvmflags"] << "-Dpl.psnc.muscle.socket.factory=muscle.net.CrossSocketFactory"
				env["jvmflags"] << "-D" + PROP_PORT_RANGE_MIN + "=" + port_min
				env["jvmflags"] << "-D" + PROP_PORT_RANGE_MAX + "=" + port_max
				env["jvmflags"] << "-D" + PROP_MTO_ADDRESS		+ "=" + mtoHost
				env["jvmflags"] << "-D" + PROP_MTO_PORT			 + "=" + mtoPort.to_s

			end
		end
		return true
	end
	
	# visibility
	attr_reader :env, :env_basename
end
