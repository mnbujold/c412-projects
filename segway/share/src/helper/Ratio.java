package helper;

public interface Ratio
{
    // Ratios between radian and degree.
    
    double RAD_TO_DEG = 180.0 / Math.PI;
    double DEG_TO_RAD = 1.0 / RAD_TO_DEG;

    // Ratios between meter, centimeter and millimeter.

    double M_TO_CM = 100.0;
    double CM_TO_M = 1.0 / M_TO_CM;
    double CM_TO_MM = 10.0;
    double MM_TO_CM = 1.0 / CM_TO_MM;
    double M_TO_MM = M_TO_CM * CM_TO_MM;
    double MM_TO_M = 1.0 / M_TO_MM;
    
    // Ratios between weight units.
    
    double KG_TO_G = 1000.0;
    double G_TO_KG = 1.0 / KG_TO_G;
    
    // Ratios between time units.

    double SEC_TO_MILLISEC = 1e3;
    double MILLISEC_TO_SEC = 1.0 / SEC_TO_MILLISEC;
    double SEC_TO_NANOSEC = 1e9;
    double NANOSEC_TO_SEC = 1.0 / SEC_TO_NANOSEC;
    double MILLISEC_TO_NANOSEC = 1e6;
    double NANOSEC_TO_MILLISEC = 1.0 / MILLISEC_TO_NANOSEC;
    
    // Ratios between power units.
    
    double VOLT_TO_MILLIVOLT = 1e3;
    double MILLIVOLT_TO_VOLT = 1.0 / VOLT_TO_MILLIVOLT;
}
