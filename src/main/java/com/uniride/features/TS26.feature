Feature: Confirmación Manual de Llegada

Como estudiante, 
Quiero poder confirmar en la app que ya me bajé, 
Para que quede registrado mi descenso seguro en el sistema.

Scenario: Registro de descenso seguro y exitoso del estudiante.

Given que el vehículo llega al paradero exacto del estudiante en la noche.
When el pasajero desciende y presiona el botón "He llegado" en su pantalla.
Then el sistema registra la hora de llegada y marca el trayecto de ese usuario como completado.

Examples:

|Campo| Valor Mostrado al Usuario|
|Estado Personal| Llegada Confirmada|
|Mensaje| ¡Qué bueno que llegaste a salvo! Gracias por viajar con CampusDrive.