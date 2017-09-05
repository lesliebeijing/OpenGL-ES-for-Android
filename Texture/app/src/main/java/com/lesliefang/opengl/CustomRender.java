package com.lesliefang.opengl;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lesliefang.opengl.objects.Mallet;
import com.lesliefang.opengl.objects.Table;
import com.lesliefang.opengl.programs.ColorShaderProgram;
import com.lesliefang.opengl.programs.TextureShaderProgram;
import com.lesliefang.opengl.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by lesliefang on 30/07/2017.
 */

public class CustomRender implements GLSurfaceView.Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private Context context;
    private float[] projMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    public CustomRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.mipmap.air_hockey_surface);
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
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        textureProgram.useProgram();
        textureProgram.setUniforms(mvpMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(mvpMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
