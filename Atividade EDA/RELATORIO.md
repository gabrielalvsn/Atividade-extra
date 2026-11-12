# Implementando os Algoritmos de Ordenação — Análise Experimental

**Disciplina:** EDA · **Curso:** Análise e Desenvolvimento de Sistemas · **UEPB**

## 1. Metodologia

- **Algoritmos (Java, arquivo `Main.java`):** Bubble Sort (com parada antecipada), Selection Sort, Insertion Sort, Merge Sort, Quicksort (pivô mediana-de-três), Counting Sort, Radix Sort (LSD, base 10) e Bucket Sort (n baldes, insertion sort em cada balde).
- **Entradas (tamanhos 20 e 2000):**
  1. elementos distintos em ordem crescente (1, 2, ..., n);
  2. elementos distintos em ordem decrescente (n, ..., 1);
  3. elementos distintos em ordem aleatória (permutação de 1..n com semente fixa 42).
- **Comparação justa (OBS. 1, 2 e 3):** os três arrays de cada tamanho são criados **uma única vez** e são os mesmos para todos os algoritmos e para todas as repetições. Cada execução ordena uma **cópia** (`clone()`) do array original, então a saída de um algoritmo nunca vira entrada de outro. A cópia é feita fora do trecho cronometrado.
- **Tempo:** `System.nanoTime()` antes e depois de cada chamada de ordenação. Cada célula é a **média de 10 execuções**. Antes das 10 medidas há 300 rodadas de aquecimento descartadas, para o compilador JIT da JVM otimizar o código. Depois de cada execução, o programa verifica se a saída está de fato ordenada.
- **Consolidação:** o programa completo foi executado 3 vezes (3 JVMs); a tabela traz a **mediana** das três médias por célula, para reduzir o ruído de coleta de lixo e escalonamento do sistema operacional.

## 2. Resultados (tempo médio de execução)

| Algoritmo | Ord. n=20 | Ord. n=2000 | Aleat. n=20 | Aleat. n=2000 | Inv. n=20 | Inv. n=2000 |
|---|---|---|---|---|---|---|
| Bubble sort | 0,1 µs | 6,8 µs | 0,6 µs | 2,49 ms | 0,6 µs | 3,14 ms |
| Selection sort | 0,5 µs | 905,5 µs | 0,5 µs | 1,00 ms | 0,5 µs | 1,17 ms |
| Insertion sort | 1,8 µs | 2,2 µs | 0,4 µs | 777,9 µs | 0,6 µs | 1,46 ms |
| Merge sort | 4,3 µs | 43,7 µs | 0,6 µs | 91,6 µs | 0,5 µs | 46,0 µs |
| Quicksort | 0,7 µs | 19,5 µs | 0,6 µs | 62,9 µs | 0,5 µs | 32,5 µs |
| Counting sort | 0,5 µs | 21,7 µs | 0,3 µs | 15,8 µs | 0,3 µs | 14,4 µs |
| Radix sort | 0,7 µs | 36,3 µs | 0,6 µs | 39,3 µs | 1,4 µs | 41,3 µs |
| Bucket sort | 2,8 µs | 116,3 µs | 2,5 µs | 84,0 µs | 2,5 µs | 81,9 µs |

*(1 ms = 1000 µs = 1.000.000 ns. Os valores variam entre máquinas e execuções; o que importa é a ordem de grandeza e o comportamento relativo.)*

## 3. Comentários

**Com n = 2000 as diferenças são claras e seguem a teoria.**

- **Algoritmos O(n²) (Bubble, Selection, Insertion):** são de 10 a 100 vezes mais lentos que os demais em entradas aleatória e inversa. O Bubble Sort foi o pior (2,49 ms aleatório, 3,14 ms inverso), porque faz muitas trocas, e não apenas comparações.
- **Selection Sort:** praticamente não depende da entrada (0,9 a 1,2 ms), pois sempre percorre o restante do array para achar o mínimo. Mesmo com a entrada já ordenada ele continua quadrático, ao contrário dos outros dois.
- **Melhor caso de Bubble e Insertion:** com a entrada já ordenada, ambos caem para O(n): 6,8 µs e 2,2 µs, ou seja, centenas de vezes mais rápidos que o Selection no mesmo cenário. O Bubble só consegue isso graças à parada antecipada quando não há trocas.
- **Insertion Sort no pior caso:** na entrada inversa gastou cerca de o dobro do tempo da aleatória (1,46 ms contra 0,78 ms), pois cada elemento precisa ser deslocado até o início, ~n²/2 deslocamentos contra ~n²/4 em média no caso aleatório.
- **Merge Sort (O(n log n)):** tempo estável entre os casos (44 a 92 µs). A entrada aleatória é a mais lenta, provavelmente por causa de previsão de desvios (as comparações do merge ficam imprevisíveis).
- **Quicksort:** 19 a 63 µs. Com pivô mediana-de-três ele **não** degenera em O(n²) para entradas ordenadas ou inversas; com o pivô no primeiro ou último elemento, essas entradas seriam o pior caso. No caso aleatório é o mais lento dos três cenários, de novo por desvios imprevisíveis.
- **Counting Sort:** foi o mais rápido em n=2000 (14 a 22 µs). Ele é O(n + k), e aqui o intervalo de valores k é igual a n, o cenário ideal. Também não depende da ordem inicial.
- **Radix Sort:** tempo praticamente constante (36 a 41 µs) nos três tipos de entrada, como esperado para O(d·(n+b)): os valores têm 4 dígitos, então são sempre 4 passadas, independentemente da ordem.
- **Bucket Sort:** ficou atrás de Counting e Radix (82 a 116 µs), apesar de também ser linear no caso médio. O custo vem da criação de 2000 listas, do boxing de `int` para `Integer` e do insertion sort em cada balde. É um bom exemplo de que a constante escondida pela notação assintótica importa na prática.

**Com n = 20 não é possível tirar conclusões confiáveis.** Todos os algoritmos terminam em menos de alguns microssegundos, a faixa em que o tempo é dominado por ruído: resolução do `nanoTime`, coleta de lixo, JIT e escalonador do sistema. Por isso aparecem valores "estranhos", como Insertion Sort mais lento com a entrada ordenada (1,8 µs) do que com a aleatória (0,4 µs), o que contradiz a teoria e deve ser lido como ruído. O que se vê de consistente é que os algoritmos que alocam memória auxiliar (Merge, Bucket) têm um custo fixo maior que os que ordenam in-place, e que para entradas tão pequenas isso pesa mais que a complexidade assintótica. A diferença de comportamento só aparece quando n cresce, como se vê em n=2000.

**Conclusão geral:** para n pequeno qualquer algoritmo serve, e os simples (Insertion) chegam a ser competitivos. Para n maior, os algoritmos O(n log n) e os lineares (Counting, Radix) são muito superiores aos quadráticos. Counting e Radix ganham quando os valores são inteiros em um intervalo limitado, enquanto Merge e Quicksort são de propósito geral. Entre os quadráticos, Insertion Sort é o melhor na prática, principalmente em entradas quase ordenadas.

## 4. Como executar

```
javac Main.java
java Main
```

A saída é um CSV (`;` como separador) com os tempos médios, em nanossegundos, de cada algoritmo para cada combinação de entrada e tamanho. Os algoritmos Counting, Radix e Bucket assumem inteiros não negativos, o que vale para as entradas do experimento.
