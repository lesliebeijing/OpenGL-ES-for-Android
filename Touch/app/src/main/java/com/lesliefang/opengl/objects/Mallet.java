package com.lesliefang.opengl.objects;

import com.lesliefang.opengl.Constants;
import com.lesliefang.opengl.data.VertexArray;
import com.lesliefang.opengl.programs.ColorShaderProgram;
import com.lesliefang.opengl.data.VertexArray;
import com.lesliefang.opengl.objects.ObjectBuilder.DrawCommand;
import com.lesliefang.opengl.objects.ObjectBuilder.GeneratedData;
import com.lesliefang.opengl.programs.ColorShaderProgram;
import com.lesliefang.opengl.util.Geometry.Point;
import com.lesliefang.opengl.util.Geometry.Cylinder;

import java.util.List;

import static android.opengl.GLES20.*;

/**
 * Created by leslie.fang on 2017-09-05.
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius;
    public final float height;
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        GeneratedData generatedData = ObjectBuilder.createMallet(new Point(0f,
                0f, 0f), radius, height, numPointsAroundMallet);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
