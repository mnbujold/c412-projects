package math;

import Jama.Matrix;

public class Helper
{
    /**
     * Creates a 2x1 column matrix.
     */
    public static Matrix matrix2D(double c1, double c2)
    {
        return new Matrix(new double[][]{new double[]{c1},
                                         new double[]{c2}});
    }
}
