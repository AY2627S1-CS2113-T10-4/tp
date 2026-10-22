# Diagram ownership index

PlantUML sources are grouped by the primary feature responsibility recorded in
the [Developer Guide](../DeveloperGuide.md#team-contributions). Rendered files use
the same owner folder and base filename under [images](../images/README.md).
These groups identify maintenance responsibility; shared classes can appear in
several members' diagrams.

| Folder | Responsibility |
| --- | --- |
| [shared](shared) | Application architecture and the combined activity-creation/rating model. |
| [atharva](atharva) | Base activity creation and help. The add sequence also includes Luke's rating enhancement. |
| [asyraf](asyraf) | Chronological/demand listing, deletion, priority, overdue and next. |
| [oliver](oliver) | Completion, activity storage/recovery, editing and name search. |
| [luke](luke) | Optional add ratings, facility data/queries, route connections/search and exit. |

Help and exit are described in the guide and do not have separate diagrams.
The combined creation class diagram is shared by Atharva and Luke; Asyraf's
priority diagrams describe scoring the stored ratings, and Oliver's diagrams
describe updating and persisting them.

## Shared

| Source | Rendered image | Developer Guide section |
| --- | --- | --- |
| [ArchitectureDiagram.puml](shared/ArchitectureDiagram.puml) | [PNG](../images/shared/ArchitectureDiagram.png) | [Architecture](../DeveloperGuide.md#architecture) |
| [ActivityCreationClassDiagram.puml](shared/ActivityCreationClassDiagram.puml) | [PNG](../images/shared/ActivityCreationClassDiagram.png) | [Activity creation and ratings](../DeveloperGuide.md#activity-creation-and-ratings) |

## Atharva

| Source | Rendered image | Developer Guide section |
| --- | --- | --- |
| [AddActivitySequence.puml](atharva/AddActivitySequence.puml) | [PNG](../images/atharva/AddActivitySequence.png) | [Activity creation and ratings](../DeveloperGuide.md#activity-creation-and-ratings) |

## Asyraf

| Source | Rendered image | Developer Guide section |
| --- | --- | --- |
| [ActivityOrderingClassDiagram.puml](asyraf/ActivityOrderingClassDiagram.puml) | [PNG](../images/asyraf/ActivityOrderingClassDiagram.png) | [Ordering and displayed indexes](../DeveloperGuide.md#ordering-and-displayed-indexes) |
| [DeleteActivitySequence.puml](asyraf/DeleteActivitySequence.puml) | [PNG](../images/asyraf/DeleteActivitySequence.png) | [Ordering and displayed indexes](../DeveloperGuide.md#ordering-and-displayed-indexes) |
| [PriorityAndTimeViewsClassDiagram.puml](asyraf/PriorityAndTimeViewsClassDiagram.puml) | [PNG](../images/asyraf/PriorityAndTimeViewsClassDiagram.png) | [Priority, overdue and next](../DeveloperGuide.md#priority-overdue-and-next-views) |
| [PriorityAndTimeViewsSequence.puml](asyraf/PriorityAndTimeViewsSequence.puml) | [PNG](../images/asyraf/PriorityAndTimeViewsSequence.png) | [Priority, overdue and next](../DeveloperGuide.md#priority-overdue-and-next-views) |

## Oliver

| Source | Rendered image | Developer Guide section |
| --- | --- | --- |
| [ActivityPersistenceClassDiagram.puml](oliver/ActivityPersistenceClassDiagram.puml) | [PNG](../images/oliver/ActivityPersistenceClassDiagram.png) | [Completion and persistence](../DeveloperGuide.md#completion-and-persistence) |
| [CompletionAndSaveSequence.puml](oliver/CompletionAndSaveSequence.puml) | [PNG](../images/oliver/CompletionAndSaveSequence.png) | [Completion and persistence](../DeveloperGuide.md#completion-and-persistence) |
| [EditFindClassDiagram.puml](oliver/EditFindClassDiagram.puml) | [PNG](../images/oliver/EditFindClassDiagram.png) | [Activity editing and name search](../DeveloperGuide.md#activity-editing-and-name-search) |
| [EditActivitySequence.puml](oliver/EditActivitySequence.puml) | [PNG](../images/oliver/EditActivitySequence.png) | [Activity editing and name search](../DeveloperGuide.md#activity-editing-and-name-search) |
| [FindActivitySequence.puml](oliver/FindActivitySequence.puml) | [PNG](../images/oliver/FindActivitySequence.png) | [Activity editing and name search](../DeveloperGuide.md#activity-editing-and-name-search) |

## Luke

| Source | Rendered image | Developer Guide section |
| --- | --- | --- |
| [FacilityClassDiagram.puml](luke/FacilityClassDiagram.puml) | [PNG](../images/luke/FacilityClassDiagram.png) | [Facility lookup](../DeveloperGuide.md#facility-lookup) |
| [FacilityLookupSequence.puml](luke/FacilityLookupSequence.puml) | [PNG](../images/luke/FacilityLookupSequence.png) | [Facility lookup](../DeveloperGuide.md#facility-lookup) |
| [fass-accessibility-network.puml](luke/fass-accessibility-network.puml) | [SVG](../images/luke/fass-accessibility-network.svg) | [Connectivity graph](../DeveloperGuide.md#connectivity-graph) |
| [RoutingClassDiagram.puml](luke/RoutingClassDiagram.puml) | [PNG](../images/luke/RoutingClassDiagram.png) | [Accessible route search](../DeveloperGuide.md#accessible-route-search) |
| [RouteSearchSequence.puml](luke/RouteSearchSequence.puml) | [PNG](../images/luke/RouteSearchSequence.png) | [Accessible route search](../DeveloperGuide.md#accessible-route-search) |

## Regenerating images

Use Java 25 and a PlantUML JAR. Run commands from the repository root. Because
sources are now one owner directory deeper, `-o` must point to
`../../images/OWNER`, relative to the source file's directory. For example:

```text
java -jar "PATH_TO_PLANTUML_JAR" -charset UTF-8 -tpng -o ../../images/asyraf docs/diagrams/asyraf/PriorityAndTimeViewsClassDiagram.puml
```

This writes `docs/images/asyraf/PriorityAndTimeViewsClassDiagram.png`. All class
and sequence diagrams use PNG. The campus connectivity graph uses SVG:

```text
java -jar "PATH_TO_PLANTUML_JAR" -charset UTF-8 -tsvg -o ../../images/luke docs/diagrams/luke/fass-accessibility-network.puml
```

Update each source and its matching rendered image together when changing a
diagram. Moving an unchanged source/image pair does not require regeneration.
