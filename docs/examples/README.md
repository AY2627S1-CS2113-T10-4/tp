# User Guide output images

`GenerateGuideImages.java` renders actual command results in a terminal-style panel.
These are reproducible output illustrations, rather than screenshots of a specific
terminal application. The clock is fixed to 1 December 2026 at 12:00 UTC, and all
activities exist only in memory. Each PNG has a plain-text companion.

From the repository root, use Java 25:

```powershell
.\gradlew.bat classes
javac -cp build/classes/java/main -d build/guide-images docs/examples/GenerateGuideImages.java
java "-Djava.awt.headless=true" -cp "build/guide-images;build/classes/java/main;build/resources/main" seedu.unienable.GenerateGuideImages
```

On macOS/Linux, replace the classpath semicolons with colons.
