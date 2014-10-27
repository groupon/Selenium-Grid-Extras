package com.groupon.seleniumgridextras.utilities.threads;


import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommonThreadPool {

    private static Logger logger = Logger.getLogger(CommonThreadPool.class);
    protected static ExecutorService cachedPool;

    public static Future startCallable(Callable callable){

        if (cachedPool == null){
            logger.info("Initializing new ");
            cachedPool = Executors.newCachedThreadPool();
        }

        return cachedPool.submit(callable);
    }
}
