package visual.draw;

import java.nio.FloatBuffer;

import model.ModelParameters;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import visual.Helper;

public class Segway
{
    public Segway(double scale, double glue)
    {
        //this.scale = scale;
        this.glue = glue;
        
        bodyW = (ModelParameters.W - ModelParameters.Ww) * scale;
        bodyH = ModelParameters.H * scale;
        bodyD = ModelParameters.D * scale;
        
        Color bodyFrontColor = new Color(200, 200, 200);
        Color bodyBackC = new Color(50, 50, 50);
        Color wheelC = new Color(30, 30, 30);
        Color wheelSignC = new Color(150, 150, 150);
        Color panelC = new Color(175, 175, 225);
        
        double bodyRatio = 0.7;
        bodyFront = new Brick(bodyW, bodyH, bodyD * bodyRatio, bodyFrontColor);
        bodyBack = new Brick(bodyW, bodyH, bodyD * (1.0-bodyRatio), bodyBackC);
        
        wheelR = ModelParameters.R * scale;
        double wheelD = ModelParameters.Ww * scale;
        wheelDp2 = wheelD / 2.0;
        lWheel = new Wheel(20, wheelR, wheelD, wheelC);
        rWheel = new Wheel(20, wheelR, wheelD, wheelC);
        
        double wsScale = wheelR * 0.75;
        lWheelSign = new WheelSign(wsScale, wheelSignC);
        rWheelSign = new WheelSign(wsScale, wheelSignC);
        
        bodyFrontOffset = -bodyBack.getD();
        bodyBackOffset = bodyFront.getD();
        bodyHOffset = bodyH / 2.0;
        wheelOffset = bodyW / 2.0 + wheelDp2;
        
        panelColor = Helper.colorToFB(panelC);
        panelOffset = bodyFront.getD() + glue;
    }

    //--------------------------------------------------------------------------

    public void draw(double x,
                     double y,
                     double z,
                     float pitchAngle,
                     float yawAngle,
                     float lWheelAngle,
                     float rWheelAngle)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + wheelR, z);
        GL11.glRotatef(yawAngle, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(pitchAngle, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslated(0.0, bodyHOffset, bodyFrontOffset);
        bodyFront.draw();
        GL11.glTranslated(0.0, 0.0, -panelOffset);
        drawPanel();
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + wheelR, z);
        GL11.glRotatef(yawAngle, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(pitchAngle, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslated(0.0, bodyHOffset, bodyBackOffset);
        bodyBack.draw();
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + wheelR, z);
        GL11.glRotatef(yawAngle, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslated(-wheelOffset, 0.0, 0.0);
        GL11.glRotatef(lWheelAngle, -1.0f, 0.0f, 0.0f);
        lWheel.draw();
        GL11.glTranslated(-wheelDp2-glue, 0.0, 0.0);
        lWheelSign.draw();
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + wheelR, z);
        GL11.glRotatef(yawAngle, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslated(wheelOffset, 0.0, 0.0);
        GL11.glRotatef(rWheelAngle, -1.0f, 0.0f, 0.0f);
        rWheel.draw();
        GL11.glTranslated(wheelDp2+glue, 0.0, 0.0);
        rWheelSign.draw();
        GL11.glPopMatrix();
    }

    //--------------------------------------------------------------------------

    private void drawPanel()
    {
        double w = 0.4 * bodyW;
        double h = 0.15 * bodyH;
        double y = 0.2 * bodyH;
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, panelColor);
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex3d(-w, y + h, 0.0);
            GL11.glVertex3d( w, y + h, 0.0);
            GL11.glVertex3d( w, y - h, 0.0);
            GL11.glVertex3d(-w, y - h, 0.0);
        }
        GL11.glEnd();
    }

    //--------------------------------------------------------------------------

    private double glue;

    private double bodyW; // scaled body width (without wheels)
    private double bodyH; // scaled body height
    private double bodyD; // scaled body depth

    private double bodyHOffset;
    private double bodyFrontOffset;
    private double bodyBackOffset;
    private double wheelOffset;
    private double panelOffset;
    
    private Brick bodyFront;
    private Brick bodyBack;
    private double wheelR, wheelDp2;
    private Wheel lWheel, rWheel;
    private WheelSign lWheelSign, rWheelSign;
    private FloatBuffer panelColor;
}
