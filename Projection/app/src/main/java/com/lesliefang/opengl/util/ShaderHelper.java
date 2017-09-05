package com.lesliefang.opengl.util;

/**
 * Created by lesliefang on 06/08/2017.
 */

import android.support.compat.BuildConfig;
import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;


public class ShaderHelper {
    public static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderSource) {
        return compileShader(GL_VERTEX_SHADER, shaderSource);
    }

    public static int compileFragmentShader(String shaderSource) {
        return compileShader(GL_FRAGMENT_SHADER, shaderSource);
    }

    private static int compileShader(int type, String shaderSource) {
        int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Count not create new shader.");
            }
            return 0;
        }

        glShaderSource(shaderObjectId, shaderSource);
        glCompileShader(shaderObjectId);

        int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (BuildConfig.DEBUG) {
            String log = glGetShaderInfoLog(shaderObjectId);
            Log.d(TAG, log);
        }

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Compile shader failed.");
            }
            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Create new program failed.");
            }
            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        glLinkProgram(programObjectId);

        int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        if (BuildConfig.DEBUG) {
            String linkLog = glGetProgramInfoLog(programObjectId);
            Log.d(TAG, linkLog);
        }

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Link program failed.");
            }
            return 0;
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(TAG, glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }
}
