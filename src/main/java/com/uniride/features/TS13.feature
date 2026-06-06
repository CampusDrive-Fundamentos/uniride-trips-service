Feature: Cancelación de Servicio por el Conductor

Como conductor, 
Quiero poder cancelar un servicio antes de llegar al campus, 
Para no perjudicar a los estudiantes si tengo una emergencia o falla mecánica.

Scenario: Liberación de grupo por emergencia antes del recojo.

Given que el conductor tiene un viaje aceptado pero aún no ha iniciado el recorrido.
When selecciona "Cancelar servicio" e indica que tuvo un problema mecánico.
Then el sistema anula su asignación y devuelve inmediatamente al grupo a la lista de "Buscando vehículo".

Examples:

|  Campo   |              Valor Mostrado al Usuario                      |
|  Estado  |                 Servicio Cancelado                          |
|  Mensaje | Hemos notificado al grupo y devuelto su anuncio a la lista. |