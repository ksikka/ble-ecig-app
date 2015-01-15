package ecig.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by kssworld93 on 1/13/15.
 */
public class MyApplication extends Application {
    public String embreMac;
    public EmbreAgent embre = new EmbreAgent();
}
