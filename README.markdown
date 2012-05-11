Welcome to SaaSim!
==================

Table of Contents

1. General Info

2. People

3. Building

4. Configuring

5. Running

1. General Info
---------------

SaaSim is a simulator based on market of Cloud Computing, where companies provide
virtual resources on demand, such as infrastructure, data storage and software services. 
So, SaaSim aims to simulate the behavior of such companies when dealing with planning 
and management capacity of a SaaS application, and also how is the dynamic provisioning 
in the act of meeting the requests from a client, seeking the best solutions with an 
emphasis on business metrics.
	
2. People
---------

* Raquel Vigolvino Lopes - _raquel_ at _dsc.ufcg.edu.br_
* [David Candeia Maia](http://github.com/davidcmm)
* [Ricardo Araújo Santos](http://github.com/ricardoas)
* [Lília Rodrigues Sampaio](http://github.com/liliarsampaio)

3. Building
-----------

On a terminal window run:

    mvn clean package

and a **target/saasim-${version}-all.zip** file will be created.

4. Configuring
--------------

SaaSim need some configuration files to work, 
	** config.properties : contains the basic informations to SaaSim work, like the 
planning heuristic, machine type, provisioning heuristic, workload parser, and others. 
Also, this file include all files necessary to SaaSim: saas.plans, saas.app, saas.users, 
iaas.providers, iaas.plan ;
	** saas.plans : contains values of plans based on big commerce values. Informations 
like the plan name(bronze, gold, etc), price, limits of cpu, storage, and others.
	** saas.app : contains information about the application, like number of tiers and 
heuristic.
	** saas.users : contains information about the client(s) using the simulator(his id,
plan chosen, storage, others).
	** iaas.plan : contains information about the plan and its provider(name, machine types, reservation).
	** iaas.providers : contains information about the providers and yours features. 

5. Running
----------

After reviewing the `saasim.conf` file, run:
 
    ./saasim.sh