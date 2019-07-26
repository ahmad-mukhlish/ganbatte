package com.programmerbaper.skripsi.misc;

import android.content.Context;

public class CurrentActivityContext {

    private static Context actualContext = null ;

    public static Context getActualContext() {
        return actualContext;
    }

    public static void setActualContext(Context actualContext) {
        CurrentActivityContext.actualContext = actualContext;
    }
}
