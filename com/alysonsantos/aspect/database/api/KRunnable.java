package com.alysonsantos.aspect.database.api;

public interface KRunnable extends Runnable {

    @Override
    default void run() {
        try {
            tryRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void tryRun() throws Exception;
}
