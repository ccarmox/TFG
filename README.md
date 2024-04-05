# <div align="center"> Trabajo de Fin de Grado
## <div align="center"> Grado en Ingeniería en Electrónica Industrial y Automática
## <div align="center"> Universidad de Vigo
### Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android

- Alumno: [Cristian Do Carmo Rodríguez](https://www.linkedin.com/in/cristian-do-carmo-rodr%C3%ADguez-9321a9163/)
- Tutora: [Lucía Díaz Vilariño](https://minaseenerxia.uvigo.es/es/docencia/profesorado/lucia-diaz-vilarino/)
- Cotutor: [Jesús Balado Frías](https://minaseenerxia.uvigo.es/es/docencia/profesorado/jesus-balado-frias/)



# Resumen
En este documento se estudia y desarrolla una aplicación para el sistema operativo móvil Android, codificada en Java, que unifica información de distintas fuentes con el objetivo de permitir una movilidad urbana óptima para los peatones a través de interfaces visuales sencillas e intuitivas, así como mediante audio y voz.

Para favorecer la simplificación del problema se trabaja con datos y regiones a pequeña escala, concretamente centrándose en la información disponible para el área de Vigo. Se utilizan como punto de partida los datos de la plataforma OpenStreetMap, de los que se extrae la posición de elementos urbanos comunes, tales como pasos de peatones o aceras, que son ampliados con información procedente de formatos de archivo de uso común en cuantificadores deportivos y de salud, así como datos generados desde el proyecto por el propio usuario.

Se importa, interpreta y transforma la información descrita anteriormente, filtrando aquella parte que resulte útil para el problema a resolver, generando las nubes de puntos correspondientes y almacenando dicha información en una base de datos local interna al dispositivo. 

Se define la interfaz de usuario y se implementan los algoritmos Dijkstra y A*, necesarios para la generación de rutas, acorde a unas características concretas de movilidad, haciendo uso de toda la información almacenada, así como de las tecnologías que componen el proyecto. 

Utilizando la plataforma Android Studio se crea y compila todo el proyecto hasta obtener una aplicación instalable en formato APK o Bundle según sea necesario.



**Palabras clave:** _Android, Dijkstra, A*, nube de puntos, navegación, GPS, ruta, movilidad._



# Fuentes de las imágenes utilizadas en el proyecto

- [Logo de la escuela de Ingeniería Industrial](https://eei.uvigo.es/es/escuela/comunicacion/imagen/)

- [Iconografía, Material Symbols](https://fonts.google.com/icons?icon.set=Material+Symbols)

- [Imagen planeta Tierra, Pixabay](https://pixabay.com/es/illustrations/tierra-mundo-planeta-globo-1303628/)

- [Imagen gansos, Pixabay](https://pixabay.com/es/illustrations/ganso-salvaje-ganso-salvaje-gansos-1643084/)

- [Imagen árboles, Pixabay](https://pixabay.com/es/photos/naturaleza-%C3%A1rbol-oto%C3%B1o-temporada-3817548/)

- [Imagen señal, Pixabay](https://pixabay.com/es/illustrations/se%C3%B1alizar-se%C3%B1ales-de-tr%C3%A1fico-firmar-2030781/)

- [Imagen papelera, Pixabay](https://pixabay.com/es/photos/desperdicio-papel-compartimiento-2788554/)

- [Imagen nube, Pixabay](https://pixabay.com/es/illustrations/nube-aislado-c%C3%BAmulo-transparente-2421760/)
