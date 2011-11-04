
# configuration file for a MUSCLE CxA
abort "this is a configuration file for to be used with the MUSCLE bootstrap utility" if __FILE__ == $0

# configure cxa properties
cxa = Cxa.LAST

# #safe
#cxa.env["max_timesteps"] = 16*10 

#unsafe
cxa.env["max_timesteps"] = 18*10

cxa.env["cxa_path"] = File.dirname(__FILE__)

cxa.env["steps"] = 10

# declare kernels
cxa.add_kernel('Pong', 'examples.pingpong.Pong')
cxa.add_kernel('Ping', 'examples.pingpong.Ping')

# configure connection scheme
cs = cxa.cs

cs.attach('Ping' => 'Pong') {
	tie('out', 'in')
}

cs.attach('Pong' => 'Ping') {
	tie('out', 'in')
}