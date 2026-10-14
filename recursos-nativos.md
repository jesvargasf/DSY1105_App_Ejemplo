# Recursos nativos: la cámara

Un recurso nativo es una capacidad del teléfono que no vive en la app. La cámara, la galería y la ubicación son del sistema. La app las pide y muestra el resultado. Esta documentación fija qué se va a usar en el registro y cómo se reparte el trabajo. La guía paso a paso, con el código para pegar, viene después.

Hoy el registro pide nombre, correo y la casilla de términos. El nombre y el correo quedan en DataStore. La foto de la persona no existe todavía.

## Qué va a hacer la app

En la pantalla de registro aparece un botón **Tomar foto**. Al pulsarlo, se abre la cámara del teléfono. Cuando la persona acepta la foto, esa imagen se muestra en el formulario, encima de los campos.

La foto es opcional. El nombre, el correo y los términos se validan igual que ahora. Se puede enviar el registro sin foto.

La ruta `register_page` y el `NavHost` no cambian. La foto entra en `RegisterScreen` y en `RegistroViewModel`.

## Quién saca la foto

La app no dibuja un visor de cámara propio. Le pide al sistema que abra la aplicación de cámara que ya trae el teléfono, espera a que la persona termine y recibe el archivo.

Eso se hace con el contrato `ActivityResultContracts.TakePicture`. Un contrato es un acuerdo: la app entrega una dirección de archivo y la cámara escribe ahí la foto. Al volver, el resultado es `true` si la foto quedó guardada.

`TakePicture` encaja con el nivel de la app. El formulario sigue siendo una pantalla de Compose. La cámara es otra aplicación que se abre encima y se cierra sola.

## El permiso

La cámara es un permiso peligroso. Hay que declararlo y, además, pedirlo en el momento de usarlo.

En el manifiesto va esta línea, fuera de `<application>`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

Eso solo avisa que la app puede necesitar la cámara. No la autoriza. La primera vez que la persona pulsa **Tomar foto**, Android muestra el diálogo del sistema. Si acepta, las siguientes veces el permiso ya está dado y la cámara abre directo. Si rechaza, el formulario sigue usable y un aviso explica que la foto no se tomó.

El pedido en tiempo de ejecución usa `ActivityResultContracts.RequestPermission`, con el permiso `android.Manifest.permission.CAMERA`.

También se declara que la cámara no es obligatoria para instalar la app:

```xml
<uses-feature
    android:name="android.hardware.camera"
    android:required="false" />
```

Así el registro se puede instalar en un aparato sin cámara. En ese caso el botón no abre nada y el aviso lo dice.

## Dónde queda el archivo

DataStore guarda textos cortos, como el nombre y el correo. Una foto es un archivo, y no entra en esas preferencias.

Antes de abrir la cámara, la app crea un archivo JPEG vacío en la carpeta de caché, con un nombre nuevo cada vez (`foto-<tiempo>.jpg`). `FileProvider` convierte esa ruta privada en un `content://` que la cámara sí puede escribir. La app no le entrega al sistema la ruta interna del disco.

`FileProvider` necesita tres piezas, que la guía va a crear:

- Una clase de proveedor en el manifiesto, con autoridad `cl.duoc.myapplication.fileprovider`.
- El archivo `res/xml/file_paths.xml`, que autoriza solo la carpeta de caché.
- El permiso temporal de escritura sobre ese `content://`, para que la cámara pueda guardar la imagen y nada más.

El `ViewModel` guarda la dirección de la foto en la ficha, como texto. Al rotar el teléfono el `ViewModel` sigue vivo, así que la imagen sigue en pantalla. Al cerrar la app, la caché puede borrarse. El nombre y el correo siguen en DataStore. La foto no.

## Qué cambia en el proyecto

| Pieza | Dónde | Qué aporta |
| --- | --- | --- |
| Permiso y `FileProvider` | `AndroidManifest.xml` | Declaran la cámara y la puerta de salida del archivo |
| Rutas permitidas | `res/xml/file_paths.xml` | Limitan la escritura a la caché |
| Dirección de la foto | `RegistroUiState` | Un texto nuevo, vacío hasta que haya foto |
| Crear archivo y recordar la dirección | `RegistroViewModel` | Prepara el JPEG y publica la dirección cuando la cámara responde |
| Botón, aviso e imagen | `RegisterScreen` | Pide el permiso, abre la cámara y muestra la foto |

`RegistroPreferences` no se toca. Sigue leyendo y escribiendo nombre y correo.

## Recorrido al pulsar Tomar foto

1. La pantalla pregunta si el permiso `CAMERA` ya está concedido.
2. Si no lo está, el sistema muestra su diálogo. Si la persona rechaza, aparece un aviso y el recorrido termina.
3. Si el permiso está concedido, el `ViewModel` crea el JPEG en caché y publica su dirección.
4. La pantalla abre la cámara del sistema con esa dirección.
5. La persona acepta la foto. La cámara escribe el archivo y devuelve `true`.
6. La pantalla dibuja esa imagen. Si la persona cancela, el resultado es `false` y el formulario queda como estaba.

## Qué queda fuera de esta etapa

- Un visor de cámara dentro de la app, con `CameraX`.
- Elegir una imagen desde la galería.
- Guardar la foto junto al nombre y el correo para que sobreviva al cierre de la app.
- Hacer obligatoria la foto en `validar()`.

## Qué va a traer la guía

La guía de implementación está en `camara.md`. Arma el manifiesto, `FileProvider`, el campo en la ficha, las funciones del `ViewModel` y el botón con la imagen en `RegisterScreen`.
