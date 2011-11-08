############################## Simulator properties ##################################

# Do not define to load default value "commons.sim.util.SimpleApplicationFactory"
saas.application.factoryclass=commons.sim.util.SimpleApplicationFactory

# Application description.
saas.application.numberoftiers=1

# From now on one must describe each tier. In case none description is given,
# tier heuristic will be round robin, starting with replica and unlimited.
# For each tier 3 properties might be set: heuristic

# Provide a list with saas.application.numberoftiers comma-separated values. Leave empty to load default.
# Possible values are:
# * ROUNDROBIN = loads commons.saas.schedulingheuristics.RoundRobinHeuristic (this is the default option)
# * ROUNDROBIN_HET = loads commons.saas.schedulingheuristics.RoundRobinHeuristicForHeterogenousMachines
# * RANJAN = loads commons.saas.schedulingheuristics.RanjanHeuristic
# * PROFITDRIVEN = loads commons.saas.schedulingheuristics.ProfitDrivenHeuristic
# * CUSTOM = please provide a value to saas.application.heuristicclass
saas.application.heuristic=ROUNDROBIN_HET
#saas.application.heuristicclass=
saas.application.startreplicas=5
saas.application.maxreplicas=5

# Time in milliseconds between machine start and application becomes up and running.
saas.setuptime=300000

saas.sla.maxrt=8000

######################################################################################