
Setup
~~~~~

Java Matrix Package (JAMA)
http://math.nist.gov/javanumerics/jama/

Download Jama-*.jar from
http://math.nist.gov/javanumerics/jama/Jama-1.0.2.jar

Eclipse -> Project Properties -> Java Build Path
        -> Libraries -> Add External JARs...

-------------------------------------------------------------------------------

Todo
~~~~

Complete the implementation of:

    src/math/Euler.java
    src/math/Midpoint.java
    src/math/RK4.java

Functions:

    rhs().eval(x,t) : right hand side of dx/dt = f(x,t) ODE
    stepSize()      : step size (denoted by h)

Run OdeMain as a Java application, which should generate:

    data/result-Euler
    data/result-Midpoint
    data/result-RK4

Plot the results:

    cd plot
    gnuplot plot-Euler.gnuplot
    gnuplot plot-Midpoint.gnuplot
    gnuplot plot-RK4.gnuplot

Analyse the results for each <method> (Euler, Midpoint, RK4):

    plot/generated/<method>-error-g.eps (global error plot)
    plot/generated/<method>-error-l.eps (local error plot)
    plot/generated/<method>-points2d.eps (visualization of the solution)

-------------------------------------------------------------------------------

Questions
~~~~~~~~~

    - Explain what you see.
    - Is this comparison "fair"? Why?
    - Can you make it "fair"? What do you see then? Explain.

