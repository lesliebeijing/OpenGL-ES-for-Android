package com.lesliefang.opengl;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lesliefang.opengl.objects.Mallet;
import com.lesliefang.opengl.objects.Table;
import com.lesliefang.opengl.objects.Puck;
import com.lesliefang.opengl.util.Geometry.Ray;
import com.lesliefang.opengl.util.Geometry.Plane;
import com.lesliefang.opengl.util.Geometry.Sphere;
import com.lesliefang.opengl.util.Geometry.Vector;
import com.lesliefang.opengl.util.Geometry;
import com.lesliefang.opengl.programs.ColorShaderProgram;
import com.lesliefang.opengl.programs.TextureShaderProgram;
import com.lesliefang.opengl.util.Geometry.Point;
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
    private float[] invertedViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    private boolean malletPressed = false;
    private Point blueMalletPosition;
    private Point previousBlueMalletPosition;

    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    private Point puckPosition;
    private Vector puckVector;

    public CustomRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
        puckPosition = new Point(0f, puck.height / 2f, 0f);
        puckVector = new Vector(0f, 0f, 0f);

        blueMalletPosition = new Point(0f, mallet.height / 2f, 0.4f);

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

        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        puckPosition = puckPosition.translate(puckVector);
        if (puckPosition.x < leftBound + puck.radius
                || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z < farBound + puck.radius
                || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        puckPosition = new Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius), puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );

        puckVector = puckVector.scale(0.99f);

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
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y,
                blueMalletPosition.z);
        colorProgram.setUniforms(mvpMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
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

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Sphere malletBoundingSphere = new Sphere(new Point(blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z),
                mallet.height / 2f);
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (malletPressed) {
            Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
            Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Point(clamp(touchedPoint.x,
                    leftBound + mallet.radius,
                    rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,
                            0f + mallet.radius,
                            nearBound - mallet.radius));

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay =
                new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay =
                new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
}
