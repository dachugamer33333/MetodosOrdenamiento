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
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {
    XYChart.Data<String,Number> primero = null;
    XYChart.Data<String,Number> segundo = null;
    int tiempoRetardo =40;
    int numeroDatos = 40;
    @FXML
    private BarChart<?, ?> bacGrafica;

    @FXML
    private Button btnBurbuja;

    @FXML
    private Button btnNuevaLista;

    @FXML
    void MetodoNuevaLista(ActionEvent event) {

    }

    @FXML
    void metodoBurbuja(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
