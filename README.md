# Reto0Din

## Estructura

```text
src/
	main/
		java/com/mycompany/reto0din/
			app/                 # Inicio de JavaFX y navegación
			controller/          # Controladores de las vistas
			model/               # Entidades y reglas del dominio (al incorporarlas)
			repository/          # Consultas y persistencia (al incorporarlas)
			service/             # Casos de uso y lógica de aplicación (al incorporarlos)
		resources/
			com/mycompany/reto0din/views/  # Pantallas FXML
			database/            # Esquema y datos iniciales
			css/                 # Hojas de estilo (al incorporarlas)
			images/              # Imágenes e iconos (al incorporarlos)
	test/java/               # Pruebas, replicando los paquetes de producción
```

Las carpetas `model`, `repository`, `service`, `css` e `images` son destinos para
cuando el reto los necesite; no se crean clases vacías solo para llenar carpetas.
El SQL de preparación está en `src/main/resources/database/tolodb.sql`.

## Ejecutar

```bash
mvn clean javafx:run
```
