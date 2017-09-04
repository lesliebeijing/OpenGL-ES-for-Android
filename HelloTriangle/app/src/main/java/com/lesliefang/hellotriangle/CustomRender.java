package com.lesliefang.hellotriangle;


import android.content.Context;
import android.opengl.GLSurfaceView;

import com.lesliefang.hellotriangle.util.ShaderHelper;
import com.lesliefang.hellotriangle.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by lesliefang on 30/07/2017.
 */

public class CustomRender implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private Context context;
    private FloatBuffer vertexData;

    public CustomRender(Context context) {
        this.context = context;
        float[] vertices = {
                0f, 0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f
        };

        vertexData = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(vertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResources(context, R.raw.vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResources(context, R.raw.fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        int program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (BuildConfig.DEBUG) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        int a_Position = glGetAttribLocation(program, "a_Position");

        vertexData.position(0);
        glVertexAttribPointer(a_Position, 2, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(a_Position);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
}
