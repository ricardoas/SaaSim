############################## Simulator properties ##################################

# Do not define to load default value "commons.sim.util.SimpleApplicationFactory"
sim.applicationfactoryclass=commons.sim.util.SimpleApplicationFactory

# Application description.
sim.application.numberoftiers=1

# From now on one must describe each tier. In case none description is given,
# tier heuristic will be round robin, starting with replica and unlimited.
# For each tier 3 properties might be set: heuristic

# Provide a list with sim.application.numberoftiers comma-separated values. Leave empty to load default.
# Possible values are:
# * ROUNDROBIN = loads commons.sim.schedulingheuristics.RoundRobinHeuristic (this is the default option)
# * RANJAN = loads commons.sim.schedulingheuristics.RanjanHeuristic
# * PROFITDRIVEN = loads commons.sim.schedulingheuristics.ProfitDrivenHeuristic
# * CUSTOM = please provide a value to sim.application.heuristicclass
sim.application.heuristic=ROUNDROBIN
#sim.application.heuristicclass=
sim.application.startreplicas=1
sim.application.maxreplicas=

# Time in milliseconds between machine start and application becomes up and running.
sim.setuptime=300000

######################################################################################