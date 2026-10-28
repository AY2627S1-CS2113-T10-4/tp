package seedu.unienable;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.imageio.ImageIO;

import seedu.unienable.model.ActivityList;
import seedu.unienable.parser.Parser;
import seedu.unienable.storage.Storage;

/** Generates terminal-style guide images from actual command results at a fixed example time. */
public final class GenerateGuideImages {
    private GenerateGuideImages() {
    }

    /** Runs disposable in-memory examples without reading or writing the user's activity file. */
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser(Clock.fixed(Instant.parse("2026-12-01T12:00:00Z"), ZoneOffset.UTC));
        Storage noWrites = new Storage() {
            @Override
            public void save(ActivityList ignored) {
                throw new AssertionError("Guide examples must not save activity data.");
            }
        };
        ActivityList workflow = new ActivityList();
        StringBuilder transcript = new StringBuilder();
        for (String command : new String[] {
            "add n/CS2113 tutorial d/2026-12-01 s/14:00 e/15:00 dem/HIGH",
            "mark 1", "list"
        }) {
            transcript.append("> ").append(command).append('\n')
                    .append(parser.parse(command).execute(workflow, noWrites).getMessage()).append("\n\n");
        }
        render("activity-workflow", "Add, complete, and list an activity", transcript.toString().stripTrailing());

        ActivityList calendar = new ActivityList();
        for (String command : new String[] {
            "add n/Practice quiz d/2026-12-01 s/21:00 e/21:30 dem/HIGH imp/4 urg/5 com/3",
            "add n/Stretch break d/2026-12-01 s/21:30 e/21:45 dem/LOW imp/1 urg/1 com/1",
            "add n/Plan tomorrow d/2026-12-01 s/22:00 e/22:30 dem/MEDIUM imp/3 com/2",
            "add n/Project notes d/2026-12-01 s/22:30 e/23:00 dem/MEDIUM urg/2", "mark 1"
        }) {
            parser.parse(command).execute(calendar, noWrites);
        }
        render("calendar-today", "Calendar dashboard - 1 December 2026",
                "> view today\n" + parser.parse("view today").execute(calendar, noWrites).getMessage());
    }

    /** Renders unmodified result text in a monospaced panel and saves an accessible text companion. */
    private static void render(String name, String title, String text) throws Exception {
        String[] lines = text.split("\\R");
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        var metricsGraphics = probe.createGraphics();
        metricsGraphics.setFont(font);
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, metricsGraphics.getFontMetrics().stringWidth(line));
        }
        metricsGraphics.dispose();
        BufferedImage image = new BufferedImage(Math.max(720, width + 48), lines.length * 23 + 90,
                BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        graphics.setColor(new Color(19, 28, 39));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        graphics.setColor(new Color(169, 204, 235));
        graphics.drawString(title, 24, 32);
        graphics.setFont(font);
        int baseline = 70;
        for (String line : lines) {
            graphics.setColor(line.startsWith("> ") ? new Color(126, 220, 183) : new Color(231, 237, 244));
            graphics.drawString(line, 24, baseline);
            baseline += 23;
        }
        graphics.dispose();
        Path folder = Path.of("docs/images/user-guide");
        Files.createDirectories(folder);
        ImageIO.write(image, "png", folder.resolve(name + ".png").toFile());
        Files.writeString(folder.resolve(name + ".txt"), text + "\n", StandardCharsets.UTF_8);
    }
}
