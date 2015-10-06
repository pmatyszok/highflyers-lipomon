package com.highflyers.mwitas.lipomon.model;

import android.app.Activity;
import android.view.View;

import com.highflyers.mwitas.lipomon.exception.ViewNotFoundException;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class Helper {
    public static Object getView(Activity activity, int id, String friendlyName) throws ViewNotFoundException {
        Object result = activity.findViewById(id);
        return handleResult(result, friendlyName);
    }

    public static Object getView(View view, int id, String friendlyName) throws ViewNotFoundException {
        Object result = view.findViewById(id);
        return handleResult(result, friendlyName);
    }

    private static Object handleResult(Object result, String friendlyName) throws ViewNotFoundException {

        if (result == null) {
            throw new ViewNotFoundException("Cannot find view " + friendlyName);
        }

        return result;
    }
}
