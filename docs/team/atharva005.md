# Atharva Rege - Project Portfolio Page

GitHub: [@Atharva005](https://github.com/Atharva005)

## Overview

UniEnable is a Java command-line application for managing dated activities and
checking campus accessibility reference information. Atharva's responsibilities
cover creating activities and helping users discover the command formats.

## Summary of responsibilities

| Version | Feature | Current status |
| --- | --- | --- |
| V1.0 | Add activities with a name, date, start/end times and optional demand. | Implemented. |
| V1.0 | Show supported commands through `help`. | Implemented. |
| V2.0 | Calendar dashboards: `view today`, `view tomorrow` and `view week`. | Planned; unavailable in the current build. |

The base add flow validates required fields, dates/times and demand, then creates
an incomplete activity. A valid activity must end after its start on the same
day and after the current local time. The shared console saves successful additions.
Luke's optional rating enhancement extends this flow; its shared model is linked below.

## Developer Guide sections

- [Atharva's implementation responsibilities](../DeveloperGuide.md#atharva-activity-creation-and-help).
- [Activity creation and ratings](../DeveloperGuide.md#activity-creation-and-ratings).
- [Help command](../DeveloperGuide.md#help-command).

## PlantUML sources and images

| Diagram | Source | Image |
| --- | --- | --- |
| Add activity sequence | [PlantUML](../diagrams/atharva/AddActivitySequence.puml) | [PNG](../images/atharva/AddActivitySequence.png) |
| Shared activity creation and rating model | [PlantUML](../diagrams/shared/ActivityCreationClassDiagram.puml) | [PNG](../images/shared/ActivityCreationClassDiagram.png) |

The sequence belongs to the base add command. The class model is shared with
Luke because it also documents the optional rating fields. Help currently has
no separate diagram.

## Related code and tests

- Code: [AddCommandParser](../../src/main/java/seedu/unienable/parser/AddCommandParser.java),
  [AddCommand](../../src/main/java/seedu/unienable/command/activity/AddCommand.java),
  [HelpCommand](../../src/main/java/seedu/unienable/command/HelpCommand.java).
- Verification: [AddCommandParserTest](../../src/test/java/seedu/unienable/parser/AddCommandParserTest.java),
  [AddCommandTest](../../src/test/java/seedu/unienable/command/activity/AddCommandTest.java),
  [HelpCommandTest](../../src/test/java/seedu/unienable/command/HelpCommandTest.java).

[Team portfolios](README.md) · [About Us](../AboutUs.md) · [Developer Guide](../DeveloperGuide.md)
