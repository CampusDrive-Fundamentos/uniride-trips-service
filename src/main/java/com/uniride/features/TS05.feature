Feature: Descuento Automático de Comisión

Como conductor, 
Quiero que la app se cobre sola la comisión (10%), 
Para no preocuparme por saldos manuales y mantener total transparencia.

Scenario: Cobro automático de plataforma mediante integración financiera al terminar el viaje

Given que la ruta de transporte ha finalizado exitosamente y se generó el cobro.
When el sistema detecta el cierre del viaje.
Then se comunica con el módulo de finanzas para debitar automáticamente el monto correspondiente.

Examples:

|Concepto                   |   Monto  |
|Tarifa cobrada             | S/ 25.00 |
|Comisión CampusDrive (10%) | S/ 2.50  |
|Ganancia Neta              | S/ 22.50 |

