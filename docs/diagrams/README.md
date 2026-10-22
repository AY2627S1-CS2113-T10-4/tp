# Diagram ownership index

PlantUML sources are grouped by the primary feature responsibility recorded in
the [Developer Guide](../DeveloperGuide.md#team-contributions). Rendered files use
the same owner folder and base filename under [images](../images/README.md).
Luke's and shared diagrams are populated. Other members can add their owner
folders when contributing diagrams; empty directories are not tracked.

| Folder | Responsibility |
| --- | --- |
| [shared](shared) | Application architecture and the combined activity-creation/rating model. |
| `atharva` (not yet populated) | Activity creation and help documentation. |
| `asyraf` (not yet populated) | Listing, deletion and priority/time queries. |
| `oliver` (not yet populated) | Completion, storage/recovery, editing and search. |
| [luke](luke) | Optional add ratings, facility data/queries, route connections/search and exit. |

The combined creation class diagram remains shared by Atharva and Luke
because it covers the base activity model and optional ratings. Luke's exit
command is described in the guide and has no separate diagram.

## Shared

| Source | Rendered image | Developer Guide section |
| --- | --- | --- |
| [ArchitectureDiagram.puml](shared/ArchitectureDiagram.puml) | [PNG](../images/shared/ArchitectureDiagram.png) | [Architecture](../DeveloperGuide.md#architecture) |
| [ActivityCreationClassDiagram.puml](shared/ActivityCreationClassDiagram.puml) | [PNG](../images/shared/ActivityCreationClassDiagram.png) | [Activity creation and ratings](../DeveloperGuide.md#activity-creation-and-ratings) |

## Atharva

Awaiting member-provided diagrams.

## Asyraf

Awaiting member-provided diagrams.

## Oliver

Awaiting member-provided diagrams.

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
java -jar "PATH_TO_PLANTUML_JAR" -charset UTF-8 -tpng -o ../../images/luke docs/diagrams/luke/FacilityClassDiagram.puml
```

This writes `docs/images/luke/FacilityClassDiagram.png`. All class
and sequence diagrams use PNG. The campus connectivity graph uses SVG:

```text
java -jar "PATH_TO_PLANTUML_JAR" -charset UTF-8 -tsvg -o ../../images/luke docs/diagrams/luke/fass-accessibility-network.puml
```

Update each source and its matching rendered image together when changing a
diagram. Moving an unchanged source/image pair does not require regeneration.
