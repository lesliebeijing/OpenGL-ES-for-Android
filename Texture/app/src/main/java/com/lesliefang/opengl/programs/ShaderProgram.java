package com.lesliefang.opengl.programs;

import android.content.Context;

import com.lesliefang.opengl.util.ShaderHelper;
import com.lesliefang.opengl.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by leslie.fang on 2017-09-05.
 */

public class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "u_mvpMatrix";
    protected static final String U_TEXTURE_UNIT = "u_TexUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TexCoord";

    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        String vertexShaderSource = TextResourceReader.readTextFileFromResources(context, vertexShaderResourceId);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResources(context, fragmentShaderResourceId);

        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
    }

    public void useProgram() {
        glUseProgram(program);
    }
}
