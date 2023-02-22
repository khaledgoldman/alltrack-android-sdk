package com.alltrack.sdk.scheduler;

public interface ThreadScheduler extends ThreadExecutor {
    void schedule(Runnable task, long millisecondsDelay);
}
