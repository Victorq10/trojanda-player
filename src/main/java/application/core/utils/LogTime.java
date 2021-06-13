package application.core.utils;

public class LogTime {
    long t1;
    long total;
    boolean isPause;

    public LogTime() {
        start();
    }

    public void start() {
        if (isPause || t1 == 0) {
            isPause = false;
            t1 = System.nanoTime();
        }
    }

    public LogTime pause() {
        if (!isPause) {
            isPause = true;
            total += System.nanoTime() - t1;
        }
        return this;
    }
    public long elapsedTime() {
        if (!isPause) {
            return total + System.nanoTime() - t1;
        }
        return total;
    }
    public void log(String message, Object...params) {
        System.out.println(
                String.format(message, params) +
                String.format(" [time: %dms]", elapsedTime() / 1_000_000)
        );
    }
}
