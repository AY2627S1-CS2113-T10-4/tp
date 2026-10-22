# Diagram images by responsibility

Rendered diagrams follow the same ownership folders as the [PlantUML sources](../diagrams/README.md).
The source index lists every source/image pair, the relevant Developer Guide
section, and regeneration commands.

| Folder | Responsibility |
| --- | --- |
| [shared](shared) | Shared architecture and the combined activity-creation/rating model. |
| [atharva](atharva) | Activity creation sequence. |
| [asyraf](asyraf) | Activity ordering/deletion and priority/time queries. |
| [oliver](oliver) | Completion, persistence, editing and name search. |
| [luke](luke) | Facilities, campus connectivity and route search. |

Luke's rating fields are also shown in the shared creation diagram; the add
sequence remains with Atharva's base command. Help and exit currently have no
separate diagrams. Images retain the source's base filename: class/sequence
diagrams use `.png`, and the campus connectivity graph uses `.svg`.
