package visual;

import java.util.HashSet;
import java.util.Set;


public class Keyboard
{
    public static enum Key
    {
        ESCAPE(org.lwjgl.input.Keyboard.KEY_ESCAPE),
        SPACE(org.lwjgl.input.Keyboard.KEY_SPACE),
        ENTER(org.lwjgl.input.Keyboard.KEY_RETURN),
        
        INSERT(org.lwjgl.input.Keyboard.KEY_INSERT),
        DELETE(org.lwjgl.input.Keyboard.KEY_DELETE),
        HOME(org.lwjgl.input.Keyboard.KEY_HOME),
        END(org.lwjgl.input.Keyboard.KEY_END),
        
        LSHIFT(org.lwjgl.input.Keyboard.KEY_LSHIFT),
        RSHIFT(org.lwjgl.input.Keyboard.KEY_RSHIFT),
        LCONTROL(org.lwjgl.input.Keyboard.KEY_LCONTROL),
        RCONTROL(org.lwjgl.input.Keyboard.KEY_RCONTROL),
        LBRACKET(org.lwjgl.input.Keyboard.KEY_LBRACKET),
        RBRACKET(org.lwjgl.input.Keyboard.KEY_RBRACKET),
        LMETA(org.lwjgl.input.Keyboard.KEY_LMETA),
        RMETA(org.lwjgl.input.Keyboard.KEY_RMETA),
        
        UP(org.lwjgl.input.Keyboard.KEY_UP),
        DOWN(org.lwjgl.input.Keyboard.KEY_DOWN),
        LEFT(org.lwjgl.input.Keyboard.KEY_LEFT),
        RIGHT(org.lwjgl.input.Keyboard.KEY_RIGHT),
        
        A(org.lwjgl.input.Keyboard.KEY_A),
        B(org.lwjgl.input.Keyboard.KEY_B),
        C(org.lwjgl.input.Keyboard.KEY_C),
        D(org.lwjgl.input.Keyboard.KEY_D),
        E(org.lwjgl.input.Keyboard.KEY_E),
        F(org.lwjgl.input.Keyboard.KEY_F),
        G(org.lwjgl.input.Keyboard.KEY_G),
        H(org.lwjgl.input.Keyboard.KEY_H),
        I(org.lwjgl.input.Keyboard.KEY_I),
        J(org.lwjgl.input.Keyboard.KEY_J),
        K(org.lwjgl.input.Keyboard.KEY_K),
        L(org.lwjgl.input.Keyboard.KEY_L),
        M(org.lwjgl.input.Keyboard.KEY_M),
        N(org.lwjgl.input.Keyboard.KEY_N),
        O(org.lwjgl.input.Keyboard.KEY_O),
        P(org.lwjgl.input.Keyboard.KEY_P),
        Q(org.lwjgl.input.Keyboard.KEY_Q),
        R(org.lwjgl.input.Keyboard.KEY_R),
        S(org.lwjgl.input.Keyboard.KEY_S),
        T(org.lwjgl.input.Keyboard.KEY_T),
        U(org.lwjgl.input.Keyboard.KEY_U),
        V(org.lwjgl.input.Keyboard.KEY_V),
        W(org.lwjgl.input.Keyboard.KEY_W),
        X(org.lwjgl.input.Keyboard.KEY_X),
        Y(org.lwjgl.input.Keyboard.KEY_Y),
        Z(org.lwjgl.input.Keyboard.KEY_Z),
        
        NUM_0(org.lwjgl.input.Keyboard.KEY_0),
        NUM_1(org.lwjgl.input.Keyboard.KEY_1),
        NUM_2(org.lwjgl.input.Keyboard.KEY_2),
        NUM_3(org.lwjgl.input.Keyboard.KEY_3),
        NUM_4(org.lwjgl.input.Keyboard.KEY_4),
        NUM_5(org.lwjgl.input.Keyboard.KEY_5),
        NUM_6(org.lwjgl.input.Keyboard.KEY_6),
        NUM_7(org.lwjgl.input.Keyboard.KEY_7),
        NUM_8(org.lwjgl.input.Keyboard.KEY_8),
        NUM_9(org.lwjgl.input.Keyboard.KEY_0),
        
        ADD(org.lwjgl.input.Keyboard.KEY_ADD),
        SUBTRACT(org.lwjgl.input.Keyboard.KEY_SUBTRACT),
        MULTIPLY(org.lwjgl.input.Keyboard.KEY_MULTIPLY),
        
        SLASH(org.lwjgl.input.Keyboard.KEY_SLASH),
        PERIOD(org.lwjgl.input.Keyboard.KEY_PERIOD),
        APOSTROPHE(org.lwjgl.input.Keyboard.KEY_APOSTROPHE),        
        COLON(org.lwjgl.input.Keyboard.KEY_COLON),
        COMMA(org.lwjgl.input.Keyboard.KEY_COMMA),
        UNDERLINE(org.lwjgl.input.Keyboard.KEY_UNDERLINE),
        
        F1(org.lwjgl.input.Keyboard.KEY_F1),
        F2(org.lwjgl.input.Keyboard.KEY_F2),
        F3(org.lwjgl.input.Keyboard.KEY_F3),
        F4(org.lwjgl.input.Keyboard.KEY_F4),
        F5(org.lwjgl.input.Keyboard.KEY_F5),
        F6(org.lwjgl.input.Keyboard.KEY_F6),
        F7(org.lwjgl.input.Keyboard.KEY_F7),
        F8(org.lwjgl.input.Keyboard.KEY_F8),
        F9(org.lwjgl.input.Keyboard.KEY_F9),
        F10(org.lwjgl.input.Keyboard.KEY_F10),
        F11(org.lwjgl.input.Keyboard.KEY_F11),
        F12(org.lwjgl.input.Keyboard.KEY_F12);
        
        private Key(int code)
        {
            this.code = code;
        }
        public final int code;
    };

    //--------------------------------------------------------------------------

    public static void create()
    throws Exception
    {
        org.lwjgl.input.Keyboard.create();
    }

    public static boolean isKeyDown(Key key)
    {
        return org.lwjgl.input.Keyboard.isKeyDown(key.code);
    }

    public static Set<Key> downKeys()
    {
        Set<Key> keys = new HashSet<Key>();
        for (Key key : Key.values())
        {
            if (isKeyDown(key)) { keys.add(key); }
        }
        return keys;
    }
}
