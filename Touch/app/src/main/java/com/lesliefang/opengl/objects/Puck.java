package com.lesliefang.opengl.objects;

import com.lesliefang.opengl.data.VertexArray;
import com.lesliefang.opengl.objects.ObjectBuilder.DrawCommand;
import com.lesliefang.opengl.objects.ObjectBuilder.GeneratedData;
import com.lesliefang.opengl.programs.ColorShaderProgram;
import com.lesliefang.opengl.util.Geometry.Point;
import com.lesliefang.opengl.util.Geometry.Cylinder;


import java.util.List;

/**
 * Created by leslie.fang on 2017-09-06.
 */

public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(
                new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
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
