import java.util.*;

public class Main {

    // ===================== ALGORITMOS =====================

    static void bubbleSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    trocou = true;
                }
            }
            if (!trocou) break; // otimização: já está ordenado
        }
    }

    static void selectionSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[min]) min = j;
            }
            int t = a[i]; a[i] = a[min]; a[min] = t;
        }
    }

    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int chave = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > chave) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = chave;
        }
    }

    static void mergeSort(int[] a) {
        if (a.length < 2) return;
        int[] aux = new int[a.length];
        mergeSort(a, aux, 0, a.length - 1);
    }

    private static void mergeSort(int[] a, int[] aux, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        mergeSort(a, aux, lo, mid);
        mergeSort(a, aux, mid + 1, hi);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) aux[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i <= mid) aux[k++] = a[i++];
        while (j <= hi) aux[k++] = a[j++];
        System.arraycopy(aux, lo, a, lo, hi - lo + 1);
    }

    static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(int[] a, int lo, int hi) {
        while (lo < hi) {
            int p = particiona(a, lo, hi);
            // recursão na menor metade para limitar a profundidade da pilha
            if (p - lo < hi - p) {
                quickSort(a, lo, p - 1);
                lo = p + 1;
            } else {
                quickSort(a, p + 1, hi);
                hi = p - 1;
            }
        }
    }

    private static int particiona(int[] a, int lo, int hi) {
        // pivô = mediana de três (evita o pior caso em entradas ordenadas)
        int mid = (lo + hi) >>> 1;
        if (a[mid] < a[lo]) troca(a, mid, lo);
        if (a[hi] < a[lo]) troca(a, hi, lo);
        if (a[hi] < a[mid]) troca(a, hi, mid);
        troca(a, mid, hi); // pivô vai para o fim
        int pivo = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j] <= pivo) troca(a, ++i, j);
        }
        troca(a, i + 1, hi);
        return i + 1;
    }

    private static void troca(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    static void countingSort(int[] a) {
        if (a.length == 0) return;
        int min = a[0], max = a[0];
        for (int v : a) { if (v < min) min = v; if (v > max) max = v; }
        int[] cont = new int[max - min + 1];
        for (int v : a) cont[v - min]++;
        int k = 0;
        for (int i = 0; i < cont.length; i++) {
            while (cont[i]-- > 0) a[k++] = i + min;
        }
    }

    static void radixSort(int[] a) { // LSD base 10, valores não negativos
        if (a.length == 0) return;
        int max = a[0];
        for (int v : a) if (v > max) max = v;
        int[] saida = new int[a.length];
        for (int exp = 1; max / exp > 0; exp *= 10) {
            int[] cont = new int[10];
            for (int v : a) cont[(v / exp) % 10]++;
            for (int i = 1; i < 10; i++) cont[i] += cont[i - 1];
            for (int i = a.length - 1; i >= 0; i--) {
                saida[--cont[(a[i] / exp) % 10]] = a[i];
            }
            System.arraycopy(saida, 0, a, 0, a.length);
        }
    }

    static void bucketSort(int[] a) { // valores não negativos
        int n = a.length;
        if (n < 2) return;
        int min = a[0], max = a[0];
        for (int v : a) { if (v < min) min = v; if (v > max) max = v; }
        if (min == max) return;
        int numBuckets = n;
        List<List<Integer>> buckets = new ArrayList<>(numBuckets);
        for (int i = 0; i < numBuckets; i++) buckets.add(new ArrayList<>());
        double range = (double) (max - min + 1);
        for (int v : a) {
            int idx = (int) ((v - min) / range * numBuckets);
            buckets.get(idx).add(v);
        }
        int k = 0;
        for (List<Integer> b : buckets) {
            // insertion sort em cada balde
            for (int i = 1; i < b.size(); i++) {
                int chave = b.get(i);
                int j = i - 1;
                while (j >= 0 && b.get(j) > chave) { b.set(j + 1, b.get(j)); j--; }
                b.set(j + 1, chave);
            }
            for (int v : b) a[k++] = v;
        }
    }

    // ===================== BENCHMARK =====================

    interface Ordenador { void ordenar(int[] a); }

    static final int EXECUCOES = 10;
    static final int AQUECIMENTO = 300; // rodadas descartadas para o JIT compilar

    static double tempoMedioNs(Ordenador alg, int[] entrada) {
        // aquecimento (JIT), sem contar no resultado
        for (int i = 0; i < AQUECIMENTO; i++) {
            alg.ordenar(entrada.clone());
        }
        long total = 0;
        for (int i = 0; i < EXECUCOES; i++) {
            int[] copia = entrada.clone(); // cópia fora da medição; entrada original nunca é alterada
            long inicio = System.nanoTime();
            alg.ordenar(copia);
            long fim = System.nanoTime();
            total += (fim - inicio);
            if (!estaOrdenado(copia)) throw new IllegalStateException("Saída não ordenada!");
        }
        return total / (double) EXECUCOES;
    }

    static boolean estaOrdenado(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    static int[] crescente(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i + 1;
        return a;
    }

    static int[] decrescente(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    static int[] aleatorio(int n, long semente) {
        // mesmos valores distintos embaralhados (Fisher-Yates) com semente fixa
        int[] a = crescente(n);
        Random r = new Random(semente);
        for (int i = n - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
        return a;
    }

    public static void main(String[] args) {
        String[] nomes = {"Bubble sort", "Selection sort", "Insertion sort", "Merge sort",
                          "Quicksort", "Counting sort", "Radix sort", "Bucket sort"};
        Ordenador[] algs = {
            Main::bubbleSort, Main::selectionSort, Main::insertionSort, Main::mergeSort,
            Main::quickSort, Main::countingSort, Main::radixSort, Main::bucketSort
        };
        int[] tamanhos = {20, 2000};

        // resultados[alg][tipo][tamanho]  tipo: 0=ordenada, 1=aleatória, 2=inversa
        double[][][] res = new double[algs.length][3][tamanhos.length];

        for (int t = 0; t < tamanhos.length; t++) {
            int n = tamanhos[t];
            int[][] entradas = { crescente(n), aleatorio(n, 42L), decrescente(n) };
            for (int a = 0; a < algs.length; a++) {
                for (int e = 0; e < 3; e++) {
                    res[a][e][t] = tempoMedioNs(algs[a], entradas[e]);
                }
            }
        }

        // saída em CSV (ns) e em tabela legível
        System.out.println("algoritmo;ord_20;ord_2000;alea_20;alea_2000;inv_20;inv_2000");
        for (int a = 0; a < algs.length; a++) {
            StringBuilder sb = new StringBuilder(nomes[a]);
            for (int e = 0; e < 3; e++)
                for (int t = 0; t < tamanhos.length; t++)
                    sb.append(';').append(String.format(Locale.US, "%.1f", res[a][e][t]));
            System.out.println(sb);
        }
    }
}
