package helper;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class NumberHelper
{
    /**
     * Converts a number to string and truncates its precision appropriately
     * if it is a floating point number.
     */
    public static String formatNumber(Number number, int prec)
    {
        if (0 <= prec && (number instanceof Float || number instanceof Double))
        {
            return keepPrecision(number, prec);
        }
        return number.toString();
    }

    /**
     * Keeps only the given precision of the given real number.
     * If prec is negative, it keeps everything.
     */
    public static String keepPrecision(Number num, int prec)
    {
        if (num instanceof Float || num instanceof Double)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.printf("%." + prec + "f", num);
            return sw.toString();
        }
        return "" + num;
    }
}
