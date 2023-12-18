java -jar filter.jar 6666 &

sleep 1

cd merchant

python3 run_filter_violation_counting.py

