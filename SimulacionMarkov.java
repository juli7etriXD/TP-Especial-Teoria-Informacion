public class SimulacionMarkov {

    private static final double EPSILON = 0.0001;
    private static final int N_MIN = 10000;

    public static void main(String[] args) {
        double[][] PS1 = {
            {0.56, 0.13, 0.00}, // Fila L
            {0.38, 0.56, 0.30}, // Fila M
            {0.06, 0.31, 0.70}  // Fila H
        };
        System.out.println("\n---------------------------------");
        System.out.println("Analizando Fuente S1 (Cursada Regular)...");
        calcularMetricas(PS1);
    }

    public static void calcularMetricas(double[][] matrizTransicion) {
        int numEstados = matrizTransicion.length;
        String[] nombresEstados = {"L (Low)", "M (Medium)", "H (High)"};

        long N = 0;
        long[] exitos = new long[numEstados];
        double[] prob_ant = new double[numEstados];
        double[] prob_act = new double[numEstados];
        
        // Inicializamos con valores que fuercen la entrada al bucle -> esta bien? 
        for (int i = 0; i < numEstados; i++) {
            prob_ant[i] = -1.0;
            prob_act[i] = 0.0;
        }

        long[] ultimo_vistazo = new long[numEstados];
        long[] suma_tiempos_recurrencia = new long[numEstados];
        long[] cantidad_recurrencias = new long[numEstados];
        for (int i = 0; i < numEstados; i++) {
            ultimo_vistazo[i] = -1;
        }

        double[][] matrizAcumulada = calcularMatrizAcumulada(matrizTransicion);

        // Definimos un estado inicial al azar (0=L, 1=M, 2=H)
        int estado_act = (int)(Math.random() * numEstados);

        while (!converge(prob_ant, prob_act, EPSILON) || N < N_MIN) {
            
            System.arraycopy(prob_act, 0, prob_ant, 0, numEstados);

            estado_act = siguienteEstado(estado_act, matrizAcumulada);

            exitos[estado_act]++;
            N++;

            for (int i = 0; i < numEstados; i++) {
                prob_act[i] = (double) exitos[i] / N;
            }

            // 3. Cálculos para el Tiempo Medio de Recurrencia
            if (ultimo_vistazo[estado_act] != -1) {
                long pasos = N - ultimo_vistazo[estado_act];
                suma_tiempos_recurrencia[estado_act] += pasos;
                cantidad_recurrencias[estado_act]++;
            }

            ultimo_vistazo[estado_act] = N;
        }

        // --- Impresión de Resultados ---
        System.out.println("Iteraciones totales hasta convergencia (N): " + N);
        System.out.println("\n--- Distribucion Estacionaria ---");
        for (int i = 0; i < numEstados; i++) {
            System.out.printf("Estado %s: %.4f\n", nombresEstados[i], prob_act[i]);
        }

        System.out.println("\n--- Tiempo Medio de Primera Recurrencia ---");
        for (int i = 0; i < numEstados; i++) {
            double tiempoMedio = (double) suma_tiempos_recurrencia[i] / cantidad_recurrencias[i];
            System.out.printf("Estado %s: %.4f pasos\n", nombresEstados[i], tiempoMedio);
        }
    }

    private static boolean converge(double[] ant, double[] act, double umbral) {
        for (int i = 0; i < ant.length; i++) {
            if (Math.abs(ant[i] - act[i]) > umbral) {
                return false; 
            }
        }
        return true; 
    }

    private static int siguienteEstado(int estadoAnterior, double[][] matrizAcumulada) {
        double r = Math.random();
        for (int i = 0; i < matrizAcumulada.length; i++) {
            if (r < matrizAcumulada[i][estadoAnterior]) {
                return i;
            }
        }
        return matrizAcumulada.length - 1;
    }

    // Calcula las probabilidades acumuladas iterando sobre columnas
    private static double[][] calcularMatrizAcumulada(double[][] matrizTransicion) {
        int numEstados = matrizTransicion.length;
        double[][] acumulada = new double[numEstados][numEstados];

        for (int col = 0; col < numEstados; col++) {
            double suma = 0;
            for (int fila = 0; fila < numEstados; fila++) {
                suma += matrizTransicion[fila][col];
                acumulada[fila][col] = suma;
            }
        }
        return acumulada;
    }
}