package com.groupon.seleniumgridextras.monitor;

import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by xhu on 3/12/14.
 */
public abstract class DaemonCallable implements Callable {

    public void setInterval(int interval) {
        this.interval = interval;
    }

    private volatile int interval = 1000;
    private volatile boolean stop = false;

    public void stop() {
        stop = true;
    }

    public Future getFuture() {
        return future;
    }

    private Future future;

    public DaemonCallable start() {
        future = CommonThreadPool.startCallable(this);
        return this;
    }

    @Override
    public Object call() throws Exception {
        beforeRun();
        try {
            while (!stop) {
                run();
                Thread.sleep(interval);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            afterRun();
            return null;
        }
    }

    protected void beforeRun() {}
    protected void afterRun() {}
    protected abstract void run();

}
