Algunas notas sobre SIMD
======================================================================

El experimento de usar SIMD para implementar Adler (AdlerKernel8Vector) 
resultó, digamos, *interesante*.
Fue un experimento bonito y finalmente fuimos capaces de implementarlo
de una forma elegante (bajo nuestra limitada experiencia y criterio).
Pero también fue infructuoso porque el rendimiento no mejoró.

En el camino descubrimos que la operación `compress` tenía un rendimiento
malísimo, y que la alternativa `rearrange` parecía rendir bastante mejor
o al menos en la línea de otras operaciones.

Llegados a ese punto surgían muchas dudas y preguntas.
El rendimiento era malo porque:
- La implementación del API Vector de Java no está madura
y el rendimiento no va a ser bueno porque no optimizan adecuadamente.
- El nivel de abstracción de API Vector es muy alto y siempre va a ser
difícil conseguir un rendimiento similar a usar api específico de un juego
de instrucciones en C/C++.
- Algunas operaciones de API Vector no tienen un reflejo directo en el
juego de instrucciones SIMD de la máquina con la que estabamos trabajando
(i9-9880H).

Las respuestas a estas dudas exige profundizar en el mundo de SIMD
y de los muchos juegos de intrucciones de los diversos procesadores.

El i9-9880H tiene AVX y AVX pero no AVX512.
AVX512 no solo es interesante porque duplique el tamaño de los registros,
sino porque también duplica el juego de registros disponibles (de 16 a 32)
y porque introduce muchas operaciones nuevas.
Por tanto, con AVX512 el compilador tiene más juego para emitir mejor código.

Tras leer [una breve introducción a AVX y AVX2 para cálculo numérico](
https://www.codeproject.com/Articles/874396/Crunching-Numbers-with-AVX-and-AVX)
parece evidente que varias de las operaciones de API Vector usadas en
AdlerKernel8Vector no tienen una instrucción directa en AVX2.
En particular, parece que las reorganizaciones del contenido de un vector,
y la suma horizontal tienen bastantes limitaciones en AVX2.
Como la suma horizontal se usa mucho en ese código,
es normal que el rendimiento no haya resultado bueno.
