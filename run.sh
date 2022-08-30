#!/bin/bash

#------------------ CONFIGURE ------------------
game='pacman'
normbase='benevolent'
reasoner='DDPL2'
agent='PacmanSubIdealAgent'
approximated='no'
extractor=''
weight=''
num_train='5000'
num_games='100'
record='test'
OCC='no'
NGRL='no'
NGRLS='yes'
part='no'
fixed_seed='yes'
graphics='yes'
layout='mediumOpen'





#-----------------------------------------------
blank=''
yes='yes'
no='no'
pacman='pacman'
if [[ $OCC == $yes ]]; then
s='--supervise'
else 
s=''
fi
if [[ $NGRL == $yes ]]; then
l='--learn'
else 
l=''
fi
if [[ $NGRLS == $yes ]]; then
l2='--sublearn'
else 
l2=''
fi
if [[ $part == $yes ]]; then
p='--partial'
else 
p=''
fi
if [[ $fixed_seed == $yes ]]; then
f='-f'
else 
f=''
fi
if [[ $game == $pacman && $graphics == $no ]]; then
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


#--------------------------------------------------------

java -jar ns_server.jar $game $normbase $reasoner 6666 &

sleep 1

cd $game

python2 $game.py -p $agent $ll $opt $f $q -x $num_train -n $all --norm $normbase --reason $reasoner $s $l $l2 $p --rec $record --port 6666 
