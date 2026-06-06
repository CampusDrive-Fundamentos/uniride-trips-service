Feature: Aceptación de Servicio

Como conductor, 
Quiero aceptar un grupo de viaje, 
Para bloquear esa solicitud, asignarme como chofer y dirigirme al campus.

Scenario: Asignación oficial de un conductor a un grupo de estudiantes.

Given que el conductor encuentra una ruta conveniente en la lista de solicitudes nocturnas.
When presiona el botón "Aceptar Viaje".
Then el sistema le asigna el trayecto, avisa a los estudiantes y le muestra la ruta de navegación hacia la universidad.

Examples:

|Campo| Valor Mostrado al Usuario|
|Estado| Viaje Aceptado|
|Siguiente paso| Dirígete a la universidad para recoger a los 4 pasajeros.|