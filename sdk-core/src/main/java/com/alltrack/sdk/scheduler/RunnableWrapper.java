package com.alltrack.sdk.scheduler;

import com.alltrack.sdk.AlltrackFactory;

public class RunnableWrapper implements Runnable {
    private Runnable runnable;

    RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Throwable t) {
            AlltrackFactory.getLogger().error("Runnable error [%s] of type [%s]",
                    t.getMessage(), t.getClass().getCanonicalName());
        }
    }
}
