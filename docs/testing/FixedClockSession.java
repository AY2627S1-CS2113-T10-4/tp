package seedu.unienable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Runs a documented manual scenario with copied fixture data and a fixed local clock.
 * Compile against the application JAR so the driver shares the console's package.
 */
public final class FixedClockSession {
    private static final ZoneId SESSION_ZONE = ZoneId.of("Asia/Singapore");

    /** Prevents construction of this command-line utility. */
    private FixedClockSession() {
    }

    /**
     * Copies a fixture to disposable storage and starts an interactive console session.
     *
     * @param args fixture file followed by an ISO local date/time, such as 2026-11-22T12:00.
     * @throws Exception if the fixture cannot be copied or the arguments cannot be parsed.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: FixedClockSession FIXTURE LOCAL_DATE_TIME");
        }
        Clock clock = Clock.fixed(LocalDateTime.parse(args[1]).atZone(SESSION_ZONE).toInstant(), SESSION_ZONE);
        Path fixture = Path.of(args[0]);
        Path activityFile = Files.createTempDirectory("unienable-manual-").resolve("activities.txt");
        Files.copy(fixture, activityFile);
        System.err.println("Disposable activity file: " + activityFile);
        UniEnable.run(System.in, System.out, activityFile, clock);
    }
}
