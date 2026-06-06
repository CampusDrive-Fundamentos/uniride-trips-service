Feature: Visualización de Grupos Listos

Como conductor, 
Quiero ver una lista de grupos completos, 
Para elegir un servicio inmediato y dirigirme al punto de recojo.

Scenario: Exploración de solicitudes de viaje disponibles para conductores.

Given que el conductor se conecta a la aplicación y está disponible.
When abre la sección de viajes solicitados.
Then el sistema muestra los grupos que ya tienen su ruta trazada y están esperando vehículo.

Examples:

|Campo| Valor Mostrado al Usuario|
|Punto de Recojo| Puerta Principal UPC |
|Destino Final| Miraflores (3 paradas) |
|Tarifa Total| S/ 25.00 |