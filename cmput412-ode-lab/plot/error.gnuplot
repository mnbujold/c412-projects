# Parameters:
#   datafile : input data file path
#   outfile  : plot output file (without extension)
datafile="../data/result-" . METHOD
outfile="generated/" . METHOD . "-error"

set terminal postscript eps enhanced "Helvetica" 22 color solid
set output outfile."-g.eps"
set xlabel "time"
set ylabel "global error (norm2 distance)"
set nokey
plot datafile using 2:7 with lines linewidth 3

set terminal postscript eps enhanced "Helvetica" 22 color solid
set output outfile."-l.eps"
set xlabel "time"
set ylabel "local error (norm2 distance)"
set nokey
plot datafile using 2:8 with lines linewidth 3

