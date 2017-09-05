package com.lesliefang.opengl;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lesliefang.opengl.util.ShaderHelper;
import com.lesliefang.opengl.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
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
    private float[] projMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private int u_mvpMatrix;

    public CustomRender(Context context) {
        this.context = context;
        float[] vertices = {
                // Triangle Fan
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,

                // Mallets
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
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
        int a_Color = glGetAttribLocation(program, "a_Color");
        u_mvpMatrix = glGetUniformLocation(program, "u_mvpMatrix");

        vertexData.position(0);
        glVertexAttribPointer(a_Position, 2, GL_FLOAT, false, 5 * BYTES_PER_FLOAT, vertexData);
        glEnableVertexAttribArray(a_Position);

        // 这里的 position 是按顶点的 index 来算的，不是按字节来算的
        vertexData.position(2);
        glVertexAttribPointer(a_Color, 3, GL_FLOAT, false, 5 * BYTES_PER_FLOAT, vertexData);
        glEnableVertexAttribArray(a_Color);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
//        float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
//
//        if (width > height) {
//            Matrix.orthoM(projMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 0f, 1f);
//        } else {
//            Matrix.orthoM(projMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 0f, 1f);
//        }

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        // 绕 X 轴负方向旋转 60 度，右手环绕确定正方向
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        // 这里 Z 轴正好是反的，因为投影用的是左手坐标系
        Matrix.perspectiveM(projMatrix, 0, 45, (float) width / (float) height, 1f, 10f);

        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, modelMatrix, 0);

        glUniformMatrix4fv(u_mvpMatrix, 1, false, mvpMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        glDrawArrays(GL_LINES, 6, 2);
        glDrawArrays(GL_POINTS, 8, 2);
    }
}
