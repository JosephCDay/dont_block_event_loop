package com.jcd;

import io.vertx.core.Vertx;
import com.jcd.vertx.AppVerticle;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

public class Main {
    public static Vertx vertx;
    
    public static void main(String[] args) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new AppVerticle());
        System.out.println("Running");
        
        // Add JMX.  Not needed to run the app, just handy.
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        } catch (Exception e)
        {
            // do nothing
        }
    }
}