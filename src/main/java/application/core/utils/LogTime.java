package application.core.utils;

/**
 * <p>
 *      How to use LogTime:
 *      <code>
 *          LogTime log = new LogTime();
 *
 *          // ...some code
 *
 *          log.log("Song has been updated [pk=%d]", song.getId())
 *
 *          // ... again some code
 *
 *          log.log("All update take")
 *      </code>
 * </p>
 *
 * <p>
 *      You can accomulate total time:
 *      <code>
 *          public LogTime accomulateLogTime = new LogTime();
 *
 *          public void initLogTime() {
 *              accomulateLogTime = new LogTime();
 *              accomulateLogTime.pause();
 *          }
 *
 *          public void someMethod() {
 *              accomulateLogTime.start()
 *
 *              // ... some code
 *
 *              accomulateLogTime.pause()
 *          }
 *
 *          publoc void logTime() {
 *              accomulateLogTime.log("Accomulated time")
 *          }
 *      </code>
 * </p>
 */
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
