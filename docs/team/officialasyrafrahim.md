# Muhammad Asyraf Bin Abdul Rahim - Project Portfolio Page

GitHub: [@officialasyrafrahim](https://github.com/officialasyrafrahim)

## Overview

UniEnable is a Java command-line application for managing dated activities and
checking campus accessibility reference information. Asyraf's responsibilities
cover activity views, chronological indexes and deletion.

## Summary of responsibilities

| Version | Feature | Current status |
| --- | --- | --- |
| V1.0 | Chronological listing with `list` and demand ordering with `list demand`. | Implemented. |
| V1.0 | Delete activities using chronological indexes. | Implemented. |
| V2.0 | Rank completed and incomplete activities using `list priority`. | Implemented. |
| V2.0 | Show incomplete activities with `overdue` and `next`. | Implemented. |

Priority uses `URG * URG * (IMP + COM)` when all ratings are recorded. Missing
ratings produce `NA` and sort last. Equal scores retain chronological order and
equal date/start times retain add order. Priority and time-query rows preserve
indexes from the full chronological list; demand output numbers its own rows.

Time queries use the local clock and strict start boundaries. Overdue includes
incomplete activities that have already started; next selects the earliest
incomplete activity whose start is in the future. Exact starts appear in neither.

## Developer Guide sections

- [Asyraf's implementation responsibilities](../DeveloperGuide.md#asyraf-activity-views-and-deletion).
- [Ordering and displayed indexes](../DeveloperGuide.md#ordering-and-displayed-indexes).
- [Priority, overdue and next](../DeveloperGuide.md#priority-overdue-and-next-views).
- [Mathematical rationale](../DeveloperGuide.md#mathematical-rationale-and-theoretical-grounding).

## PlantUML sources and images

| Diagram | Source | Image |
| --- | --- | --- |
| Ordering class model | [PlantUML](../diagrams/asyraf/ActivityOrderingClassDiagram.puml) | [PNG](../images/asyraf/ActivityOrderingClassDiagram.png) |
| Delete activity sequence | [PlantUML](../diagrams/asyraf/DeleteActivitySequence.puml) | [PNG](../images/asyraf/DeleteActivitySequence.png) |
| Priority and time-query class model | [PlantUML](../diagrams/asyraf/PriorityAndTimeViewsClassDiagram.puml) | [PNG](../images/asyraf/PriorityAndTimeViewsClassDiagram.png) |
| Overdue query sequence | [PlantUML](../diagrams/asyraf/PriorityAndTimeViewsSequence.puml) | [PNG](../images/asyraf/PriorityAndTimeViewsSequence.png) |

Luke's rating enhancement supplies the values used for scoring. Oliver's
completion and editing commands update activities through the same canonical
index rule. Sorting and filtering leave the backing insertion order unchanged.

## Related code and tests

- Model: [ActivityList](../../src/main/java/seedu/unienable/model/ActivityList.java),
  [Activity](../../src/main/java/seedu/unienable/model/Activity.java).
- Formatting and parsing: [ActivityListFormatter](../../src/main/java/seedu/unienable/ui/activity/ActivityListFormatter.java),
  [ListDeleteCommandParser](../../src/main/java/seedu/unienable/parser/ListDeleteCommandParser.java).
- Verification: [ListPriorityCommandTest](../../src/test/java/seedu/unienable/command/activity/ListPriorityCommandTest.java),
  [OverdueCommandTest](../../src/test/java/seedu/unienable/command/activity/OverdueCommandTest.java),
  [NextCommandTest](../../src/test/java/seedu/unienable/command/activity/NextCommandTest.java),
  [DeleteCommandTest](../../src/test/java/seedu/unienable/command/activity/DeleteCommandTest.java).

[Team portfolios](README.md) · [About Us](../AboutUs.md) · [Developer Guide](../DeveloperGuide.md)
