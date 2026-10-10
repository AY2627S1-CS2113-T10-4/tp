# UniEnable Developer Guide

## Product scope

UniEnable is a command-line project for managing dated activities with start/end
times, demand levels, and completion status, and looking up hub accessibility
information. The target users and detailed value proposition remain for the
team to refine. The agreed v1.0 commands and activity data contract are in
[V1_SPEC.md](V1_SPEC.md).

Development is breadth-first and incremental: this common baseline provides
shared APIs and console bootstrap behavior; each feature owner introduces a
vertical slice and its tests in a separate PR.

## Acknowledgements

Based on the supplied tp-master starter. Add sources of reused or adapted ideas,
code, documentation, and libraries alongside the relevant contributions.

## Design & implementation

The baseline uses `seedu.unienable.UniEnable`, `Ui`, and `Parser` to run a console
loop. `bye` produces an exit result; other input receives an unimplemented-command
message. Shared command, activity, exception, and storage API skeletons are
listed in [V1_SPEC.md](V1_SPEC.md). Storage is not connected to the bootstrap.

### Future feature design

Feature owners will document their implemented designs and diagrams here.

## User Stories

To be agreed by the team for the v1.0 scope.

## Non-Functional Requirements

Java 25 is required. Preserve the starter Gradle wrapper, JUnit, Checkstyle,
and GitHub Actions CI. Further product requirements remain to be agreed.

## Glossary

- Activity: a named item with a date, start/end times, demand, and completion state.
- Demand: one of LOW, MEDIUM, or HIGH.
- Hub: a facility lookup code; the supported codes await the supplied dataset.

## Instructions for manual testing

Build with `./gradlew clean check` and `./gradlew shadowJar`.
Run `java -jar build/libs/unienable.jar`; verify the greeting, an unimplemented
input message, and clean exit with `bye`. Feature owners will add their own
manual testing instructions when those features are implemented.
