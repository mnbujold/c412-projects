package helper;

import Jama.Matrix;

public abstract class MatrixHelper
{
    /**
     * Creates a 1x1 matrix.
     */
    public static Matrix matrix1D(double c)
    {
        return new Matrix(new double[][]{new double[]{c}});
    }

    /**
     * Creates a 2x1 column matrix.
     */
    public static Matrix matrix2D(double c1, double c2)
    {
        return new Matrix(new double[][]{new double[]{c1},
                                         new double[]{c2}});
    }

    /**
     * Creates a 3x1 column matrix.
     */
    public static Matrix matrix3D(double c1, double c2, double c3)
    {
        return new Matrix(new double[][]{new double[]{c1},
                                         new double[]{c2},
                                         new double[]{c3}});
    }

    /**
     * Creates a 4x1 column matrix.
     */
    public static Matrix matrix4D(double c1, double c2, double c3, double c4)
    {
        return new Matrix(new double[][]{new double[]{c1},
                                         new double[]{c2},
                                         new double[]{c3},
                                         new double[]{c4}});
    }

    /**
     * Creates a column matrix.
     */
    public static Matrix matrixD(double[] values)
    {
        double[][] mat = new double[values.length][1];
        for (int i = 0; i < values.length; ++i)
        {
            mat[i][0] = values[i];
        }
        return new Matrix(mat);
    }

    //--------------------------------------------------------------------------

    /**
     * Returns a string matrix representation
     * using a given precision for reals.
     */
    public static String strMatrix(Matrix m, int prec)
    {
        String s = "[";
        for (int i = 0; i < m.getRowDimension(); ++i)
        {
            if (0 < i) { s += "; "; }
            for (int j = 0; j < m.getColumnDimension(); ++j)
            {
                if (0 < j) { s += " "; }
                s += NumberHelper.formatNumber(m.get(i, j), prec);
            }
        }
        s += "]";
        return s;
    }

    /**
     * Returns a string matrix representation
     * using default precision for reals.
     */
    public static String strMatrix(Matrix m)
    {
        return strMatrix(m, -1);
    }
}
