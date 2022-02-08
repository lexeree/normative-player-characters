#!/bin/bash

#------------------ CONFIGURE ------------------
game='pacman'
normbase='vegan'
reasoner='DDPL'
agent='PacmanWeightedAgent'
approximated='no'
extractor=''
weight=''
num_train='1000'
num_games='100'
record='test'
RTCC='no'
NGRL='yes'
fixed_seed='yes'
graphics='no'
layout='littleClassic'





#-----------------------------------------------
blank=''
yes='yes'
no='no'
if [[ $RTCC == $yes ]]; then
s='--supervise'
else 
s=''
fi
if [[ $NGRL == $yes ]]; then
l='--learn'
else 
l=''
fi
if [[ $fixed_seed == $yes ]]; then
f='-f'
else 
f=''
fi
if [[ $graphics == $no ]]; then
q='-q'
else 
q=''
fi
if [[ $approximated == $yes ]]; then
ex='extractor='
else 
ex=''
fi
if [[ $weight == $blank ]]; then
w=''
else 
w='weight='
fi
if [[ $weight -ne $blank && $approximated == $yes ]]; then
com=','
else
com=''
fi
if [[ $weight -ne $blank || $approximated == $yes ]]; then
lab='-a '
else
lab=''
fi
if [[ $layout == $blank ]]; then
lab2=''
else 
lab2='-l '
fi
opt=$lab$ex$extractor$com$w$weight
all=$(($num_train+$num_games))
ll=$lab2$layout
echo $opt


#--------------------------------------------------------

java -jar ns_server.jar $game $normbase $reasoner 6666 &

sleep 1

cd $game

python2 pacman.py -p $agent $ll $opt $f $q -x $num_train -n $all --norm $normbase --reason $reasoner $s $l --rec $record --port 6666 
