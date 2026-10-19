package fes.aragon.unam.metodosordenamiento;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import mx.unam.fes.dinamico.ListaDoble.ListaDoblementeEnlazada;
import mx.unam.fes.dinamico.ListaDoble.Nodo;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {
    XYChart.Data<String,Number> primero = null;
    XYChart.Data<String,Number> segundo = null;
    int tiempoRetardo =40;
    int numeroDatos = 40;
    @FXML
    private BarChart<String, Number> bacGrafica;

    @FXML
    private Button btnBurbuja;

    @FXML
    private Button btnNuevaLista;

    @FXML
    void MetodoNuevaLista(ActionEvent event) {
        bacGrafica.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series = generarAleatoriosEnteros(numeroDatos);
        bacGrafica.getData().add(series);
    }

    @FXML
    void metodoBurbuja(ActionEvent event) {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        bacGrafica.setAnimated(false);
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series = generarAleatoriosEnteros(numeroDatos);
        bacGrafica.getData().add(series);
    }
    private XYChart.Series<String, Number> generarAleatoriosEnteros(int n) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Random rnd = new Random();
        for (int i = 1; i <= n; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), rnd.nextInt(100)));
        }

        return series;
    }
    public static ListaDoblementeEnlazada<Integer> bubbleSort(ListaDoblementeEnlazada<Integer> lista) {
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
                }

                actual = siguiente;
            }
        } while (cambiado);

        return lista;
    }

}
