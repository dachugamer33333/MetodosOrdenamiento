package fes.aragon.unam.metodosordenamiento;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import mx.unam.fes.dinamico.ListaDoble.ListaDoblementeEnlazada;
import mx.unam.fes.dinamico.ListaDoble.Nodo;
import mx.unam.fes.estatico.Arreglo;
import mx.unam.fes.exepciones.IndicieFueraExeption;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PrincipalController implements Initializable {
    private int tiempoRetardo = 40;
    private int numeroDatos = 40;

    private XYChart.Series<String, Number> serie;
    private ListaDoblementeEnlazada<Integer> lista;
    private boolean ordenando = false;

    @FXML
    private BarChart<String, Number> bacGrafica;

    @FXML
    private Button btnBurbuja;

    @FXML
    private Button btnInsercion;

    @FXML
    private Button btnMezcla;

    @FXML
    private Button btnShaker;

    @FXML
    private Button btnQuickSort;

    @FXML
    private Button btnNuevaLista;

    @FXML
    void MetodoNuevaLista(ActionEvent event) {
        if (ordenando) {
            nuevaLista(numeroDatos);
            ordenando = false;
        }
    }

    @FXML
    void metodoBurbuja(ActionEvent event) {
        ejecutarOrdenamiento(() -> bubbleSort(lista, this::notificarCambio));
    }

    @FXML
    void metodoInsercion(ActionEvent event) {
        ejecutarOrdenamiento(() -> insertionSort(lista, this::notificarCambio));
    }

    @FXML
    void metodoMezcla(ActionEvent event) {
        ejecutarOrdenamiento(() -> mergeSort(lista, 0, lista.getLongitud() - 1, this::notificarCambio));
    }

    @FXML
    void metodoShaker(ActionEvent event) {
        ejecutarOrdenamiento(() -> shakerSort(lista, this::notificarCambio));
    }

    @FXML
    void metodoQuickSort(ActionEvent event) {
        ejecutarOrdenamiento(() -> quickSort(lista, 0, lista.getLongitud() - 1, this::notificarCambio));
    }

    private void ejecutarOrdenamiento(Runnable algoritmo) {
        if (ordenando || lista == null || lista.esVacia()) {
            return;
        }

        ordenando = true;
        setBotonesDeshabilitados(true);

        Task<Void> tarea = new Task<Void>() {
            @Override
            protected Void call() {
                algoritmo.run();
                return null;
            }
        };

        tarea.setOnSucceeded(e -> {
            actualizarGrafica();
            finalizarOrdenamiento();
        });
        tarea.setOnFailed(e -> {
            actualizarGrafica();
            finalizarOrdenamiento();
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();

    }

    private void notificarCambio(ListaDoblementeEnlazada<Integer> actual) {
        Arreglo<Integer> valores = valoresDe(actual);
        Platform.runLater(() -> aplicarValores(valores));
        try {
            Thread.sleep(tiempoRetardo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void setBotonesDeshabilitados(boolean deshabilitar) {

            if (btnBurbuja != null) btnBurbuja.setDisable(deshabilitar);
            if (btnInsercion != null) btnInsercion.setDisable(deshabilitar);
            if (btnMezcla != null) btnMezcla.setDisable(deshabilitar);
            if (btnShaker != null) btnShaker.setDisable(deshabilitar);
            if (btnQuickSort != null) btnQuickSort.setDisable(deshabilitar);
            if (btnNuevaLista != null) btnNuevaLista.setDisable(deshabilitar);



    }

    private void finalizarOrdenamiento() {

        setBotonesDeshabilitados(false);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        bacGrafica.setAnimated(false);
        bacGrafica.setLegendVisible(false); // Quita el recuadro naranja sobrante abajo
        nuevaLista(numeroDatos);
    }

    private void nuevaLista(int n) {
        bacGrafica.getData().clear();
        lista = new ListaDoblementeEnlazada<>();
        serie = new XYChart.Series<>();
        Random rnd = new Random();
        for (int i = 1; i <= n; i++) {
            int valor = rnd.nextInt(100);
            lista.agregarCola(valor);
            serie.getData().add(new XYChart.Data<>(String.valueOf(i), valor));
        }
        bacGrafica.getData().add(serie);
    }

    private void actualizarGrafica() {
        aplicarValores(valoresDe(lista));
    }

    private void aplicarValores(Arreglo<Integer> valores) {
        try {
            for (int i = 0; i < valores.numElementos() && i < serie.getData().size(); i++) {
                serie.getData().get(i).setYValue(valores.recuperar(i));
            }
        } catch (IndicieFueraExeption e) {
            throw new RuntimeException(e);
        }
    }

    private static Arreglo<Integer> valoresDe(ListaDoblementeEnlazada<Integer> lista) {
        Arreglo<Integer> valores = new Arreglo<>(lista.getLongitud());
        Nodo<Integer> actual = lista.getCabeza();
        while (actual != null) {
            try {
                valores.insertar(actual.getDato());
            } catch (IndicieFueraExeption e) {
                throw new RuntimeException(e);
            }
            actual = actual.getSiguiente();
        }
        return valores;
    }

    private static void intercambiar(Nodo<Integer> n1, Nodo<Integer> n2) {
        Integer temp = n1.getDato();
        n1.setDato(n2.getDato());
        n2.setDato(temp);
    }

    private static Nodo<Integer> obtenerNodoEn(ListaDoblementeEnlazada<Integer> lista, int indice) {
        Nodo<Integer> temp = lista.getCabeza();
        for (int c = 0; c < indice && temp != null; c++) {
            temp = temp.getSiguiente();
        }
        return temp;
    }

    // -------------------------------------------------------------
    // 1.  BURBUJA (Bubble Sort)
    // -------------------------------------------------------------
    public static ListaDoblementeEnlazada<Integer> bubbleSort(ListaDoblementeEnlazada<Integer> lista) {
        return bubbleSort(lista, null);
    }

    public static ListaDoblementeEnlazada<Integer> bubbleSort(
            ListaDoblementeEnlazada<Integer> lista,
            Consumer<ListaDoblementeEnlazada<Integer>> alIntercambiar) {
        if (lista == null || lista.esVacia()) {
            return lista;
        }

        boolean cambiado;
        do {
            cambiado = false;
            Nodo<Integer> actual = lista.getCabeza();

            while (actual != null && actual.getSiguiente() != null) {
                Nodo<Integer> siguiente = actual.getSiguiente();

                if (actual.getDato() > siguiente.getDato()) {
                    intercambiar(actual, siguiente);
                    cambiado = true;

                    if (alIntercambiar != null) {
                        alIntercambiar.accept(lista);
                    }
                }

                actual = siguiente;
            }
        } while (cambiado);

        return lista;
    }

    // -------------------------------------------------------------
    // 2.  INSERCIÓN (Insertion Sort)
    // -------------------------------------------------------------
    public static ListaDoblementeEnlazada<Integer> insertionSort(ListaDoblementeEnlazada<Integer> lista) {
        return insertionSort(lista, null);
    }

    public static ListaDoblementeEnlazada<Integer> insertionSort(
            ListaDoblementeEnlazada<Integer> lista,
            Consumer<ListaDoblementeEnlazada<Integer>> alModificar) {
        if (lista == null || lista.esVacia() || lista.getLongitud() < 2) {
            return lista;
        }

        Nodo<Integer> actual = lista.getCabeza().getSiguiente();
        while (actual != null) {
            Integer temp = actual.getDato();
            Nodo<Integer> k = actual.getanterior();

            while (k != null && k.getDato() > temp) {
                k.getSiguiente().setDato(k.getDato());
                if (alModificar != null) {
                    alModificar.accept(lista);
                }
                k = k.getanterior();
            }

            if (k == null) {
                lista.getCabeza().setDato(temp);
            } else {
                k.getSiguiente().setDato(temp);
            }

            if (alModificar != null) {
                alModificar.accept(lista);
            }

            actual = actual.getSiguiente();
        }

        return lista;
    }

    // -------------------------------------------------------------
    // 3.  MEZCLA (Merge Sort)
    // -------------------------------------------------------------
    public static void mergeSort(ListaDoblementeEnlazada<Integer> lista) {
        if (lista != null && !lista.esVacia()) {
            mergeSort(lista, 0, lista.getLongitud() - 1, null);
        }
    }

    public static void mergeSort(
            ListaDoblementeEnlazada<Integer> lista,
            int inicio,
            int fin,
            Consumer<ListaDoblementeEnlazada<Integer>> alModificar) {
        if (inicio < fin) {
            int medio = (inicio + fin) / 2;
            mergeSort(lista, inicio, medio, alModificar);
            mergeSort(lista, medio + 1, fin, alModificar);
            merge(lista, inicio, medio, fin, alModificar);
        }
    }

    private static void merge(
            ListaDoblementeEnlazada<Integer> lista,
            int inicio,
            int medio,
            int fin,
            Consumer<ListaDoblementeEnlazada<Integer>> alModificar) {
        int n1 = medio - inicio + 1;
        int n2 = fin - medio;

        int[] izq = new int[n1];
        int[] der = new int[n2];

        Nodo<Integer> actual = obtenerNodoEn(lista, inicio);
        for (int i = 0; i < n1; i++) {
            izq[i] = actual.getDato();
            actual = actual.getSiguiente();
        }
        for (int j = 0; j < n2; j++) {
            der[j] = actual.getDato();
            actual = actual.getSiguiente();
        }

        int i = 0;
        int j = 0;
        actual = obtenerNodoEn(lista, inicio);

        while (i < n1 && j < n2) {
            if (izq[i] <= der[j]) {
                actual.setDato(izq[i]);
                i++;
            } else {
                actual.setDato(der[j]);
                j++;
            }
            if (alModificar != null) {
                alModificar.accept(lista);
            }
            actual = actual.getSiguiente();
        }

        while (i < n1) {
            actual.setDato(izq[i]);
            i++;
            if (alModificar != null) {
                alModificar.accept(lista);
            }
            actual = actual.getSiguiente();
        }

        while (j < n2) {
            actual.setDato(der[j]);
            j++;
            if (alModificar != null) {
                alModificar.accept(lista);
            }
            actual = actual.getSiguiente();
        }
    }

    // -------------------------------------------------------------
    // 4. SACUDIDA (Shaker)
    // -------------------------------------------------------------
    public static ListaDoblementeEnlazada<Integer> shakerSort(ListaDoblementeEnlazada<Integer> lista) {
        return shakerSort(lista, null);
    }

    public static ListaDoblementeEnlazada<Integer> shakerSort(
            ListaDoblementeEnlazada<Integer> lista,
            Consumer<ListaDoblementeEnlazada<Integer>> alIntercambiar) {
        if (lista == null || lista.esVacia() || lista.getLongitud() < 2) {
            return lista;
        }

        boolean cambiado = true;
        Nodo<Integer> inicio = lista.getCabeza();
        Nodo<Integer> fin = null;

        while (cambiado) {
            cambiado = false;
            Nodo<Integer> actual = inicio;

            // Barrida hacia adelante (izquierda a derecha)
            while (actual != null && actual.getSiguiente() != fin) {
                Nodo<Integer> siguiente = actual.getSiguiente();
                if (actual.getDato() > siguiente.getDato()) {
                    intercambiar(actual, siguiente);
                    cambiado = true;

                    if (alIntercambiar != null) {
                        alIntercambiar.accept(lista);
                    }
                }
                actual = siguiente;
            }

            if (!cambiado) {
                break;
            }

            cambiado = false;
            fin = actual;

            if (inicio == fin) {
                break;
            }

            // Barrida hacia atrás (derecha a izquierda)
            actual = fin.getanterior();
            while (actual != null && actual != inicio.getanterior()) {
                Nodo<Integer> siguiente = actual.getSiguiente();
                if (actual.getDato() > siguiente.getDato()) {
                    intercambiar(actual, siguiente);
                    cambiado = true;

                    if (alIntercambiar != null) {
                        alIntercambiar.accept(lista);
                    }
                }
                actual = actual.getanterior();
            }

            inicio = inicio.getSiguiente();
        }

        return lista;
    }

    // -------------------------------------------------------------
    // 5. QuickSort
    // -------------------------------------------------------------
    public static void quickSort(ListaDoblementeEnlazada<Integer> lista) {
        if (lista != null && !lista.esVacia()) {
            quickSort(lista, 0, lista.getLongitud() - 1, null);
        }
    }

    public static void quickSort(
            ListaDoblementeEnlazada<Integer> lista,
            int bajo,
            int alto,
            Consumer<ListaDoblementeEnlazada<Integer>> alIntercambiar) {
        if (bajo < alto) {
            int pi = particion(lista, bajo, alto, alIntercambiar);
            quickSort(lista, bajo, pi - 1, alIntercambiar);
            quickSort(lista, pi + 1, alto, alIntercambiar);
        }
    }

    private static int particion(
            ListaDoblementeEnlazada<Integer> lista,
            int bajo,
            int alto,
            Consumer<ListaDoblementeEnlazada<Integer>> alIntercambiar) {
        Nodo<Integer> nodoAlto = obtenerNodoEn(lista, alto);
        int pivote = nodoAlto.getDato();

        int i = bajo - 1;
        Nodo<Integer> nodoI = (i >= 0) ? obtenerNodoEn(lista, i) : null;
        Nodo<Integer> nodoJ = obtenerNodoEn(lista, bajo);

        for (int j = bajo; j < alto; j++) {
            if (nodoJ.getDato() <= pivote) {
                i++;
                if (nodoI == null) {
                    nodoI = lista.getCabeza();
                } else {
                    nodoI = nodoI.getSiguiente();
                }

                if (i != j) {
                    intercambiar(nodoI, nodoJ);
                    if (alIntercambiar != null) {
                        alIntercambiar.accept(lista);
                    }
                }
            }
            nodoJ = nodoJ.getSiguiente();
        }

        i++;
        if (nodoI == null) {
            nodoI = lista.getCabeza();
        } else {
            nodoI = nodoI.getSiguiente();
        }

        if (i != alto) {
            intercambiar(nodoI, nodoAlto);
            if (alIntercambiar != null) {
                alIntercambiar.accept(lista);
            }
        }

        return i;
    }
}