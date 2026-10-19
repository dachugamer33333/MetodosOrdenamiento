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
    XYChart.Data<String,Number> primero = null;
    XYChart.Data<String,Number> segundo = null;
    int tiempoRetardo =40;
    int numeroDatos = 40;

    private XYChart.Series<String, Number> serie;
    private ListaDoblementeEnlazada<Integer> lista;
    private boolean ordenando = false;

    @FXML
    private BarChart<String, Number> bacGrafica;

    @FXML
    private Button btnBurbuja;

    @FXML
    private Button btnNuevaLista;

    @FXML
    void MetodoNuevaLista(ActionEvent event) {
        if (!ordenando) {
            nuevaLista(numeroDatos);
        }
    }

    @FXML
    void metodoBurbuja(ActionEvent event) {
        if (ordenando || lista == null || lista.esVacia()) {
            return;
        }

        ordenando = true;
        btnBurbuja.setDisable(true);
        btnNuevaLista.setDisable(true);

        Task<Void> tarea = new Task<Void>() {
            @Override
            protected Void call() {
                bubbleSort(lista, actual -> {
                    Arreglo<Integer> valores = valoresDe(actual);
                    Platform.runLater(() -> aplicarValores(valores));
                    try {
                        Thread.sleep(tiempoRetardo);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
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

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        bacGrafica.setAnimated(false);
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

    private void finalizarOrdenamiento() {
        ordenando = false;
        btnBurbuja.setDisable(false);
        btnNuevaLista.setDisable(false);
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
                    Integer temp = actual.getDato();
                    actual.setDato(siguiente.getDato());
                    siguiente.setDato(temp);
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

}
