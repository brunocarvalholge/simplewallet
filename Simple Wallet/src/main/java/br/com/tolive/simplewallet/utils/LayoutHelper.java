package br.com.tolive.simplewallet.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class LayoutHelper {
	public static int dpToPixel(Context context, int dp) {
	    float scale = getDisplayDensityFactor(context);
	    return (int) (dp * scale + 0.5f);
	}

	private static float getDisplayDensityFactor(Context context) {
	    if (context != null) {
	        Resources res = context.getResources();
	        if (res != null) {
	            DisplayMetrics metrics = res.getDisplayMetrics();
	            if(metrics != null) {
	                return metrics.density;
	            }
	        }
	    }
	    return 1.0f;
	}
}
