# Oliver - Project Portfolio Page

GitHub: [@Oliver-koh](https://github.com/Oliver-koh)

## Overview

UniEnable is a Java command-line application for managing dated activities and
checking campus accessibility reference information. Oliver's responsibilities
cover maintaining activity state across changes and application restarts.

## Summary of responsibilities

| Version | Feature | Current status |
| --- | --- | --- |
| V1.0 | Mark and unmark activities using chronological indexes. | Implemented. |
| V1.0 | Save/load activity data, report corrupt records and create recovery backups. | Implemented. |
| V2.0 | Update supplied fields using `edit INDEX ...`. | Implemented. |
| V2.0 | Search activity names using `find KEYWORD`. | Implemented. |

The console saves commands that report an activity change. Storage restores
legacy six-field rows and rated nine-field rows, then saves in nine-field format.
Detected corruption requires a verified backup before replacement. A failed save
can leave changes in memory, so the documented recovery procedure matters.

Editing preserves omitted fields and completion. Date/time edits validate the
combined old and new values before replacement. Name search uses a literal,
case-insensitive substring and retains full chronological indexes without saving.
The existing Unicode-only blank-name validation defect is documented in the guide.

## Developer Guide sections

- [Oliver's implementation responsibilities](../DeveloperGuide.md#oliver-completion-storage-editing-and-search).
- [Completion and persistence](../DeveloperGuide.md#completion-and-persistence).
- [Activity editing and name search](../DeveloperGuide.md#activity-editing-and-name-search).
- [Recovery steps in the User Guide](../UserGuide.md#error-handling).

## PlantUML sources and images

| Diagram | Source | Image |
| --- | --- | --- |
| Persistence class model | [PlantUML](../diagrams/oliver/ActivityPersistenceClassDiagram.puml) | [PNG](../images/oliver/ActivityPersistenceClassDiagram.png) |
| Completion and save sequence | [PlantUML](../diagrams/oliver/CompletionAndSaveSequence.puml) | [PNG](../images/oliver/CompletionAndSaveSequence.png) |
| Edit/find class model | [PlantUML](../diagrams/oliver/EditFindClassDiagram.puml) | [PNG](../images/oliver/EditFindClassDiagram.png) |
| Edit activity sequence | [PlantUML](../diagrams/oliver/EditActivitySequence.puml) | [PNG](../images/oliver/EditActivitySequence.png) |
| Find activity sequence | [PlantUML](../diagrams/oliver/FindActivitySequence.puml) | [PNG](../images/oliver/FindActivitySequence.png) |

These flows share `ActivityList` and the console save contract with the other
members' commands. Changes to timing can recalculate indexes; rating-only edits
can change Asyraf's priority ordering while preserving chronological indexes.

## Related code and tests

- Code: [Storage](../../src/main/java/seedu/unienable/storage/Storage.java),
  [EditCommand](../../src/main/java/seedu/unienable/command/activity/EditCommand.java),
  [FindCommand](../../src/main/java/seedu/unienable/command/activity/FindCommand.java).
- Verification: [StorageTest](../../src/test/java/seedu/unienable/storage/StorageTest.java),
  [MarkCommandTest](../../src/test/java/seedu/unienable/command/activity/MarkCommandTest.java),
  [UnmarkCommandTest](../../src/test/java/seedu/unienable/command/activity/UnmarkCommandTest.java),
  [EditCommandTest](../../src/test/java/seedu/unienable/command/activity/EditCommandTest.java),
  [FindCommandTest](../../src/test/java/seedu/unienable/command/activity/FindCommandTest.java).

[Team portfolios](README.md) · [About Us](../AboutUs.md) · [Developer Guide](../DeveloperGuide.md)
