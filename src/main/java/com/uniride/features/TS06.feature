Feature: Finalización del servicio

Como conductor, 
Quiero finalizar el servicio al dejar al último estudiante, 
Para cerrar la ruta en el sistema y quedar libre para otro viaje.

Scenario: Cierre total de la ruta por parte del chofer.

Given que el conductor confirma el descenso del último pasajero a bordo.
When desliza el control de "Finalizar Ruta".
Then el sistema cierra oficialmente el viaje, cambia el estado a completado y libera al conductor.

Examples:

|       Campo       |               Valor Mostrado al Usuario                     |
|  Estado del Viaje |                       Finalizado                            |
|    MensajeRuta    | Completada con éxito. Ya puedes recibir nuevas solicitudes. |