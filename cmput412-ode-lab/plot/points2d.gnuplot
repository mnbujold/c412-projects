# Parameters:
#   datafile : input data file path
#   outfile  : plot output file (without extension)
datafile="../data/result-" . METHOD
outfile="generated/" . METHOD . "-points2d"

outfile=outfile.".eps"
set terminal postscript eps enhanced "Helvetica" 22 color solid

set output outfile
set xlabel "x_1"
set ylabel "x_2"
set xrange [-5:5]
set yrange [-5:5]
set size ratio -1
set xzeroaxis
set yzeroaxis
set nokey
plot datafile using 3:4 with lines \
   , datafile using 5:6 with lines

