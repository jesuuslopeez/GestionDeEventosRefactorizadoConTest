Os propongo una tarea simple pero completa que puede realizarse en aproximadamente una hora. Se estructura en tres niveles de complejidad, de acuerdo con las instrucciones que diste. Los alumnos deberán desarrollar una API REST sencilla utilizando **Spring Boot 3.x**, en la que se enfoca en un modelo básico de datos con un máximo de 3 entidades, sus respectivos controladores, repositorios, y una query extendida en los repositorios.

### Proyecto: **Gestión de Eventos y Participantes**

### Descripción:
La aplicación será un sistema básico de gestión de eventos y participantes. Los usuarios podrán crear eventos, añadir participantes a dichos eventos y consultarlos. Los estudiantes deben generar un diagrama E/R a partir del cual desarrollarán las entidades en el proyecto.

---

### Nivel 1: **Tres entidades (10 puntos)**

1. **Entidades**:
   - **Evento**: Representa un evento al cual se pueden inscribir participantes.
   - **Participante**: Representa una persona que asiste a un evento.
   - **Organizador**: Representa a la persona que organiza el evento.

2. **Diagrama E/R**:
   Los estudiantes deberán crear un diagrama E/R simple que relacione las tres entidades:
   - Un **Evento** tiene un **Organizador**.
   - Un **Evento** tiene varios **Participantes** (Relación de uno a muchos).
   - Un **Organizador** puede estar asociado a varios **Eventos** (Relación de uno a muchos).

3. **Tareas**:
   - Crear las 3 entidades mencionadas (Evento, Participante, Organizador) con relaciones adecuadas.
   - Crear los controladores REST para cada entidad.
   - Crear los repositorios extendiendo **JpaRepository** para las 3 entidades.
     - Añadir una consulta adicional en cada repositorio. Ejemplos:
       - Buscar eventos por título.
       - Buscar participantes por email.
       - Buscar organizadores por nombre.

4. **Pruebas**:
   - Probar los endpoints con un cliente REST (Postman, Insomnia, etc.) realizando peticiones CRUD (Crear, Leer, Actualizar, Eliminar).
   - Pruebas adicionales con las queries personalizadas.

---

### Nivel 2: **Dos entidades (8 puntos máx.)**

1. **Entidades**:
   - **Evento**
   - **Participante**

2. **Diagrama E/R**:
   - Un **Evento** tiene varios **Participantes** (Relación de uno a muchos).

3. **Tareas**:
   - Crear las 2 entidades mencionadas (Evento, Participante) con relaciones adecuadas.
   - Crear los controladores REST para las 2 entidades.
   - Crear los repositorios extendiendo **JpaRepository** para ambas entidades.
     - Añadir una consulta adicional. Ejemplos:
       - Buscar eventos por título.
       - Buscar participantes por email.

4. **Pruebas**:
   - Probar los endpoints con un cliente REST realizando peticiones CRUD.
   - Probar las queries adicionales.

---

### Nivel 3: **Una entidad (6 puntos máx.)**

1. **Entidad**:
   - **Evento**

2. **Diagrama E/R**:
   - No es necesario. Solo una entidad simple.

3. **Tareas**:
   - Crear la entidad **Evento**.
   - Crear el controlador REST para la entidad.
   - Crear el repositorio extendiendo **JpaRepository** para **Evento**.
     - Añadir una consulta adicional (buscar eventos por título).

4. **Pruebas**:
   - Probar los endpoints con un cliente REST realizando peticiones CRUD.
   - Probar la query adicional.

---



### Fichero de pistas: GestionEventosPistas.md
"El código ofrecido en este documento se proporciona como pistas o guía para la implementación de la API REST. Los estudiantes que consulten directamente este código en lugar de intentar resolver los desafíos por sí mismos verán una reducción mínima en su calificación, ya que se evalúa la capacidad de aplicar los conceptos aprendidos."
Está en una rama diferente.


## RA y CE
La tarea propuesta de crear una API REST sencilla con **Spring Boot** tocaría los siguientes **Resultados de Aprendizaje (RA)** y sus correspondientes **criterios de evaluación**:

Los más relevantes y que más se tratan en esta tarea son el **RA5** y el **RA6**

### 1. **RA 1: Selecciona las arquitecturas y tecnologías de programación Web en entorno servidor, analizando sus capacidades y características propias.**
   - **Criterio e)**: Se han identificado y caracterizado los principales lenguajes y tecnologías relacionados con la programación Web en entorno servidor.
   - **Criterio g)**: Se han reconocido y evaluado las herramientas de programación en entorno servidor.

### 2. **RA 2: Escribe sentencias ejecutables por un servidor Web reconociendo y aplicando procedimientos de integración del código en lenguajes de marcas.**
   - **Criterio b)**: Se han identificado las principales tecnologías asociadas.
   - **Criterio e)**: Se han escrito sentencias simples y se han comprobado sus efectos en el documento resultante.

### 3. **RA 5: Desarrolla aplicaciones Web identificando y aplicando mecanismos para separar el código de presentación de la lógica de negocio.**
   - **Criterio a)**: Se han identificado las ventajas de separar la lógica de negocio de los aspectos de presentación de la aplicación.
   - **Criterio f)**: Se han escrito aplicaciones Web con mantenimiento de estado y separación de la lógica de negocio.
   - **Criterio g)**: Se han aplicado los principios de la programación orientada a objetos.

### 4. **RA 6: Desarrolla aplicaciones de acceso a almacenes de datos, aplicando medidas para mantener la seguridad y la integridad de la información.**
   - **Criterio a)**: Se han analizado las tecnologías que permiten el acceso mediante programación a la información disponible en almacenes de datos.
   - **Criterio b)**: Se han creado aplicaciones que establezcan conexiones con bases de datos.
   - **Criterio c)**: Se ha recuperado información almacenada en bases de datos.
   - **Criterio d)**: Se ha publicado en aplicaciones Web la información recuperada.
   - **Criterio f)**: Se han creado aplicaciones Web que permitan la actualización y la eliminación de información disponible en una base de datos.

### 5. **RA 7: Desarrolla servicios Web analizando su funcionamiento e implantando la estructura de sus componentes.**
   - **Criterio d)**: Se ha programado un servicio Web.
   - **Criterio f)**: Se ha verificado el funcionamiento del servicio Web.

### 6. **RA 8: Genera páginas Web dinámicas analizando y utilizando tecnologías del servidor Web que añadan código al lenguaje de marcas.**
   - **Criterio a)**: Se han identificado las diferencias entre la ejecución de código en el servidor y en el cliente Web.
   - **Criterio g)**: Se han aplicado estas tecnologías en la programación de aplicaciones Web.

Estos RA y criterios se ajustan al enfoque de la tarea de desarrollo de una API REST con Spring Boot, ya que los estudiantes deberán trabajar tanto con la arquitectura del servidor, como con bases de datos y la separación de lógica de negocio en el backend.


### Rúbrica para Tarea de **API REST de Gestión de Eventos y Participantes**

Esta rúbrica evalúa la tarea de crear una API REST sencilla en **Spring Boot 3.x**, enfocada en un modelo básico de datos con un máximo de 3 entidades y sus respectivos controladores, repositorios y consultas extendidas. La tarea se estructura en tres niveles de complejidad, con una puntuación máxima de 10 puntos. Además, los criterios se alinean con los **Resultados de Aprendizaje (RA)** y **Criterios de Evaluación (CE)** definidos.

| Criterio de Evaluación       | Descripción                                                                                                                   | Nivel 3: 6 puntos                      | Nivel 2: 8 puntos                      | Nivel 1: 10 puntos                     | Puntos Totales |
|------------------------------|-------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|----------------------------------------|----------------------------------------|----------------|
| **Diseño del Modelo de Datos**   | Creación de entidades y relaciones correctas basadas en el diagrama E/R y el enunciado de la tarea (evento, participante, organizador). <br>**RA 1 (e)**, **RA 5 (a)** | No se realiza diagrama, pero incluye entidad `Evento`. <br> La relación es correcta. | Diagrama simple entre 2 entidades (evento y participante) con relación uno a muchos. | Diagrama E/R con 3 entidades, relaciones uno a muchos correctas. | /2 |
| **Implementación de Entidades JPA** | Implementación de entidades utilizando anotaciones JPA, y definiciones de atributos y relaciones correctas. <br> **RA 6 (b, g)** | Implementa `Evento` con sus atributos básicos. | Implementa `Evento` y `Participante` con relaciones correctas y atributos adecuados. | Implementa las 3 entidades con relaciones adecuadas (`@OneToMany`, `@ManyToOne`). | /2 |
| **Controladores REST**         | Implementación de controladores REST para CRUD, utilizando las anotaciones adecuadas y un diseño basado en las mejores prácticas de RESTful. <br> **RA 6 (d, f)**, **RA 7 (f)** | Crea un controlador CRUD básico para `Evento`. | Crea controladores para `Evento` y `Participante`, cubriendo operaciones CRUD. | Implementa controladores CRUD para las 3 entidades (`Evento`, `Participante`, `Organizador`). | /2 |
| **Repositorios y Consultas Extendidas** | Definición de repositorios y consultas personalizadas (queries extendidas) en cada repositorio para operaciones de búsqueda específicas. <br> **RA 6 (c)**, **RA 7 (d)** | Define repositorio `Evento` con una consulta extendida simple (ej. búsqueda por título). | Define repositorios `Evento` y `Participante`, con consultas adicionales para cada entidad. | Define los 3 repositorios con consultas adicionales según el enunciado. | /1 |
| **Pruebas y Verificación**      | Probar los endpoints con un cliente REST (Postman, Insomnia), realizando operaciones CRUD y consultas adicionales. Verificar funcionamiento y consistencia. <br> **RA 2 (e)**, **RA 8 (g)** | Realiza pruebas básicas de CRUD en el controlador `Evento`. | Realiza pruebas de CRUD y consultas adicionales en `Evento` y `Participante`. | Realiza pruebas completas en los controladores de las 3 entidades y en las consultas adicionales. | /2 |
| **Uso de Pistas y Soluciones**   | Uso adecuado del archivo de pistas sin depender exclusivamente de él para la implementación. <br> **RA 5 (g)** | Depende excesivamente de las pistas para la implementación. | Consulta las pistas de manera moderada, aplicando los conceptos propios. | Usa el archivo de pistas solo como referencia, implementando de forma autónoma. | /1 |

---

### Total de Puntos:

- **Nivel 3:** 6 puntos
- **Nivel 2:** 8 puntos
- **Nivel 1:** 10 puntos

### Instrucciones de Uso de la Rúbrica:
1. **Evaluación por Nivel**: Cada nivel de la rúbrica corresponde a uno de los niveles de complejidad en el enunciado (Nivel 1, Nivel 2, y Nivel 3).
2. **Puntuación Máxima**: 10 puntos, según la complejidad y el cumplimiento de los criterios de evaluación.
3. **RA y CE**: Los criterios están alineados con los Resultados de Aprendizaje y Criterios de Evaluación definidos en el enunciado.
