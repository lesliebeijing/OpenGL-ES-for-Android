package com.lesliefang.opengl;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lesliefang.opengl.objects.Mallet;
import com.lesliefang.opengl.objects.Table;
import com.lesliefang.opengl.objects.Puck;
import com.lesliefang.opengl.programs.ColorShaderProgram;
import com.lesliefang.opengl.programs.TextureShaderProgram;
import com.lesliefang.opengl.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by lesliefang on 30/07/2017.
 */

public class CustomRender implements GLSurfaceView.Renderer {
    private Context context;
    private float[] viewMatrix = new float[16];
    private float[] projMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] viewProjectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

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
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

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

        // view matrix
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);

        // projection matrix 这里 Z 轴正好是反的，因为投影用的是左手坐标系
        Matrix.perspectiveM(projMatrix, 0, 45, (float) width / (float) height, 1f, 10f);

        Matrix.multiplyMM(viewProjectionMatrix, 0, projMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(mvpMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(mvpMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        // 物体中心默认都是 (0,0,0) 通过变换把 XZ 轴的下半部分提上来
        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(mvpMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(mvpMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }
}
