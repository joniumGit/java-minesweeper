package dev.jonium.fun;

import dev.jonium.fun.logic.Ui;

@SuppressWarnings("unused")
public final class Sweeper implements Runnable {

    public Sweeper() {
        // Reflection
    }

    @Override
    public void run() {
        new Ui();
    }

    public static void main(String[] args) {
        new Sweeper().run();
    }

}
