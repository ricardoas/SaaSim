#!/bin/bash

bash saasim.sh config-static-small-1.properties 2>> table.output
bash saasim.sh config-static-small-2.properties 2>> table.output
bash saasim.sh config-static-small-3.properties 2>> table.output
bash saasim.sh config-static-small-4.properties 2>> table.output
bash saasim.sh config-static-large-1.properties 2>> table.output
bash saasim.sh config-static-large-2.properties 2>> table.output
bash saasim.sh config-static-xlarge.properties 2>> table.output
bash saasim.sh config-ranjan-MIN.properties 2>> table.output
bash saasim.sh config-ranjan-WO-SETUP-MIN.properties 2>> table.output
bash saasim.sh config-ranjan-HOUR.properties 2>> table.output
bash saasim.sh config-ranjan-WO-SETUP-HOUR.properties 2>> table.output

