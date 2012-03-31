package visual.engine;

import geom3d.Plane;
import geom3d.Point3D;
import visual.Constant;
import model.scene.Color;

/**
 * Visualization of geom3d.Cylinder.
 * 
 * The circular elements are represented by "numPoints" rectangles.
 */
public class Cylinder extends AbstractEngineObject
{
    public Cylinder(geom3d.Cylinder cylinder, int numPoints, Color color)
    {
        this.cylinder = cylinder;
        this.color = color;
        
        double radius = cylinder.radius();
        
        double angle, angleIncr = 2.0 * Math.PI / numPoints;
        z = new float[numPoints];
        y = new float[numPoints];
        for (int i = 0; i < numPoints; ++i)
        {
            angle = i * angleIncr;
            z[i] = (float) (Math.sin(angle) * radius);
            y[i] = (float) (Math.cos(angle) * radius);
        }

        Point3D u = cylinder.u();
        
        Point3D pyz = Plane.planeYZ().projection(u);
        double norm = pyz.norm2();
        if (0.0 < norm)
        {
            angle = Math.acos(pyz.iprod(Point3D.unitX()) / norm);
            setRotationZ((float) angle);
        }
        
        Point3D pxz = Plane.planeXZ().projection(u);
        norm = pxz.norm2();
        if (0.0 < norm)
        {
            angle = Math.acos(pxz.iprod(Point3D.unitY()) / norm);
            setRotationY((float) angle);
        }
        
        Point3D un = u.copy().normalize2();
        setTranslation(un.mulL(u.norm2()/2).addL(cylinder.p()));
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    Data[] quadData()
    {
        int numPoints = z.length;
        
        float[] vertices = new float[4 * numPoints * Constant.VERTEX_FLOATSIZE];
        float[] normals = new float[4 * numPoints * Constant.VERTEX_FLOATSIZE];
        
        float depth = (float) cylinder.u().norm2()/2, ny, nz, nd;
        int vi = 0, ni = 0, nextI;
        for (int i = 0; i < numPoints; ++i)
        {
            nextI = (i+1) % numPoints;
            ny = (z[i] + z[nextI]) / 2f;
            nz = (y[i] + y[nextI]) / 2f;
            nd = (float) Math.sqrt(ny*ny + nz*nz);
            ny /= nd; nz /= nd;
            
            vertices[vi++] = -depth;    normals[ni++] = 0f;
            vertices[vi++] = z[i];      normals[ni++] = ny;
            vertices[vi++] = y[i];      normals[ni++] = nz;
            
            vertices[vi++] = depth;     normals[ni++] = 0f;
            vertices[vi++] = z[i];      normals[ni++] = ny;
            vertices[vi++] = y[i];      normals[ni++] = nz;

            vertices[vi++] = depth;     normals[ni++] = 0f;
            vertices[vi++] = z[nextI];  normals[ni++] = ny;
            vertices[vi++] = y[nextI];  normals[ni++] = nz;
            
            vertices[vi++] = -depth;    normals[ni++] = 0f;
            vertices[vi++] = z[nextI];  normals[ni++] = ny;
            vertices[vi++] = y[nextI];  normals[ni++] = nz;
        }
        
        int[] indices = new int[4 * numPoints];
        for (int i = 0; i < indices.length; ++i) indices[i] = i;
        
        return new Data[]{new Data(vertices, normals, color, indices)};
    }
    
    @Override
    Data[] polygonData()
    {
        int numPoints = z.length, iL = 0, iR = 0;
        float depth = (float) cylinder.u().norm2()/2;
        
        float[] verticesL = new float[numPoints * Constant.VERTEX_FLOATSIZE];
        float[] normalsL = new float[numPoints * Constant.VERTEX_FLOATSIZE];
        for (int i = 0; i < numPoints; ++i)
        {
            verticesL[iL] = -depth;     normalsL[iL++] = -1f;
            verticesL[iL] = z[i];       normalsL[iL++] =  0f;
            verticesL[iL] = y[i];       normalsL[iL++] =  0f;
        }
        
        float[] verticesR = new float[numPoints * Constant.VERTEX_FLOATSIZE];
        float[] normalsR = new float[numPoints * Constant.VERTEX_FLOATSIZE];
        for (int i = 0; i < numPoints; ++i)
        {
            verticesR[iR] = -verticesL[iL-3];   normalsR[iR++] = 1f;
            verticesR[iR] =  verticesL[iL-2];   normalsR[iR++] = 0f;
            verticesR[iR] =  verticesL[iL-1];   normalsR[iR++] = 0f;
            iL -= 3;
        }
        
        int[] indices = new int[numPoints];
        for (int i = 0; i < indices.length; ++i) indices[i] = i;
        
        return new Data[]{new Data(verticesL, normalsL, color, indices),
                          new Data(verticesR, normalsR, color, indices)};
    }
    
    @Override
    public void register()
    {
        super.register();
        z = y = null;
    }
    
    //--------------------------------------------------------------------------
    
    private final geom3d.Cylinder cylinder;
    private final Color color;
    
    private float[] z, y;
}
