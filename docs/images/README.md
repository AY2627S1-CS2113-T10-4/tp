# Diagram images by responsibility

Rendered diagrams follow the same ownership and type folders as the
[PlantUML sources](../diagrams/README.md). Each member has `class/` and `sequence/`
subfolders. Luke's architecture diagram uses `luke/architecture/`, and the campus
connectivity graph uses `luke/graph/`.
The source index lists every source/image pair, the relevant Developer Guide
section, and regeneration commands.

| Folder | Class diagrams | Sequence diagrams | Other diagrams |
| --- | --- | --- | --- |
| [atharva](#atharva) | [Class](#atharva) | [Sequence](#atharva) | - |
| [asyraf](#asyraf) | [Class](#asyraf) | [Sequence](#asyraf) | - |
| [oliver](#oliver) | [Class](#oliver) | [Sequence](#oliver) | - |
| [luke](#luke) | [Class](#luke) | [Sequence](#luke) | Architecture and [connectivity graph](#luke) |

Atharva maintains the activity-creation diagrams and the six name, parameter,
and schedule validation diagrams in the `atharva` folder. Class/sequence diagrams use
`.png`, and the campus connectivity graph uses `.svg`.

<!-- @@author Atharva005 -->

## Atharva

- [ActivityNameValidatorClassDiagram.png](atharva/class/ActivityNameValidatorClassDiagram.png)
  shows command-word checks and printable-ASCII name validation.
- [ActivityNameValidationSequence.png](atharva/sequence/ActivityNameValidationSequence.png)
  shows valid-name and warning paths before command execution.
- [ActivityParameterValidatorClassDiagram.png](atharva/class/ActivityParameterValidatorClassDiagram.png)
  shows the shared prefix-typo checker used by add and edit.
- [ActivityParameterValidationSequence.png](atharva/sequence/ActivityParameterValidationSequence.png)
  shows a typo warning before an activity command executes.
- [ActivityScheduleValidatorClassDiagram.png](atharva/class/ActivityScheduleValidatorClassDiagram.png)
  shows duplicate and overlap validation before add/edit changes.
- [ActivityScheduleValidationSequence.png](atharva/sequence/ActivityScheduleValidationSequence.png)
  shows duplicate rejection, overlap approval, and successful validation.
- [ActivityCreationClassDiagram.png](atharva/class/ActivityCreationClassDiagram.png)
  shows activity creation and optional rating fields.
- [AddActivitySequence.png](atharva/sequence/AddActivitySequence.png) shows activity parsing,
  construction, and insertion, including optional ratings.
- [CalendarViewClassDiagram.png](atharva/class/CalendarViewClassDiagram.png) shows date
  ranges, chronological activity queries, and read-only dashboard results.
- [CalendarViewSequence.png](atharva/sequence/CalendarViewSequence.png) shows empty and
  populated periods, schedule grouping, and rating-table rendering.
- [TopicHelpClassDiagram.png](atharva/class/TopicHelpClassDiagram.png) shows menu and
  topic selection, validation, and read-only command results.
- [TopicHelpSequence.png](atharva/sequence/TopicHelpSequence.png) shows construction
  and execution for a known help topic, with notes on the other help scenarios.
<!-- @@author -->

<!-- @@author officialasyrafrahim -->

## Asyraf

### Class diagrams

- [ListActivityClassDiagram.png](asyraf/class/ListActivityClassDiagram.png) shows chronological listing.
- [ListDemandClassDiagram.png](asyraf/class/ListDemandClassDiagram.png) shows demand-ordered listing.
- [DeleteActivityClassDiagram.png](asyraf/class/DeleteActivityClassDiagram.png) shows activity deletion.
- [ListPriorityClassDiagram.png](asyraf/class/ListPriorityClassDiagram.png) shows priority listing.
- [OverdueActivityClassDiagram.png](asyraf/class/OverdueActivityClassDiagram.png) shows overdue activities.
- [NextActivityClassDiagram.png](asyraf/class/NextActivityClassDiagram.png) shows next activity.

### Sequence diagrams

- [ListActivitySequence.png](asyraf/sequence/ListActivitySequence.png) shows chronological listing.
- [ListDemandSequence.png](asyraf/sequence/ListDemandSequence.png) shows demand-ordered listing.
- [DeleteActivitySequence.png](asyraf/sequence/DeleteActivitySequence.png) shows activity deletion.
- [ListPrioritySequence.png](asyraf/sequence/ListPrioritySequence.png) shows priority listing.
- [OverdueActivitySequence.png](asyraf/sequence/OverdueActivitySequence.png) shows overdue activities.
- [NextActivitySequence.png](asyraf/sequence/NextActivitySequence.png) shows next activity.

<!-- @@author -->

<!-- @@author Oliver-koh -->

<a id="oliver"></a>

## Koh Li Wei Oliver

- [MarkActivityClassDiagram.png](oliver/class/MarkActivityClassDiagram.png) shows marking and the completion-change result.
- [MarkActivitySequence.png](oliver/sequence/MarkActivitySequence.png) shows marking, repeated requests, and conditional saving.
- [UnmarkActivityClassDiagram.png](oliver/class/UnmarkActivityClassDiagram.png) shows unmarking and the completion-change result.
- [UnmarkActivitySequence.png](oliver/sequence/UnmarkActivitySequence.png) shows unmarking, repeated requests, and conditional saving.
- [ActivityPersistenceClassDiagram.png](oliver/class/ActivityPersistenceClassDiagram.png)
  shows conditional saving and storage recovery state.
- [CompletionAndSaveSequence.png](oliver/sequence/CompletionAndSaveSequence.png) shows
  marking an activity and saving only when it changes.
- [EditActivityClassDiagram.png](oliver/class/EditActivityClassDiagram.png) shows edit
  parsing, chronological index resolution, replacement, and saving requests.
- [EditActivitySequence.png](oliver/sequence/EditActivitySequence.png) shows validation,
  replacement, saving, and confirmation.
- [FindActivityClassDiagram.png](oliver/class/FindActivityClassDiagram.png) shows keyword
  validation, case-insensitive matching, and chronological-index formatting.
- [FindActivitySequence.png](oliver/sequence/FindActivitySequence.png) shows searching and
  formatting with chronological indexes, without saving.
<!-- @@author -->

<!-- @@author lukelouyu -->

## Luke

- [ArchitectureDiagram.png](luke/architecture/ArchitectureDiagram.png) shows
  startup, commands, models, storage, and output responsibilities.
- [FacilityClassDiagram.png](luke/class/FacilityClassDiagram.png) shows facility
  models, loading, feature search, and command formatting.
- [FacilityLookupSequence.png](luke/sequence/FacilityLookupSequence.png) shows a
  feature query through the parser, manager, and formatter.
- [RoutingClassDiagram.png](luke/class/RoutingClassDiagram.png) shows route parsing,
  graph management, shortest-path solving, and route output.
- [RouteSearchSequence.png](luke/sequence/RouteSearchSequence.png) shows accessible
  edge selection, path solving, and segment display.
- [fass-accessibility-network.svg](luke/graph/fass-accessibility-network.svg) shows
  the bundled campus reference graph.

<!-- @@author -->
