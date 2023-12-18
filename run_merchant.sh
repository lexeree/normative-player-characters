#!/bin/bash

cd merchant

python3 run_optimal.py 

wait

cd ..

java -jar ns_server.jar merchant pacifist DDPL2 6666 &

sleep 1

cd merchant

python3 run_correct.py 

wait

sleep 1

cd ..

java -jar ns_server.jar merchant pacifist DDPL2 6666 &

sleep 1

cd merchant

python3 run_ngrl.py

wait

sleep 1

cd ..

java -jar ns_server.jar merchant pacifist DDPL2 6666 &

sleep 1

cd merchant

python3 run_dncc.py 


