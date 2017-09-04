package com.lesliefang.hellotriangle.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lesliefang on 06/08/2017.
 */

public class TextResourceReader {
    public static String readTextFileFromResources(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (Resources.NotFoundException e) {
            throw new RuntimeException("Resource not found " + resourceId);
        } catch (IOException e) {
            throw new RuntimeException("could not open resource " + resourceId);
        }

        return body.toString();
    }
}
