package ni.edu.uam.prueba1s9.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.prueba1s9.database.DatabaseConnection;
import ni.edu.uam.prueba1s9.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductoController {

    @FXML
    private TextField idProducto;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtBuscar;
    @FXML
    private CheckBox chkProductosExistentes;
    @FXML
    private ComboBox<String> cmbCategoriaBus;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();


    @FXML
    private void initialize(){
        configurarCmbCategoria();
        configurarTblProductos();

        tblProductos.setItems(productos);

        cmbCategoriaBus.getItems().setAll(
                cmbCategoria.getItems()
        );

        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        llenarFormulario(seleccionado);
                    }
                });


        actualizarTabla();

    }


    // Cargar las categorías desde PostgreSQL
    private void configurarCmbCategoria() {
        cmbCategoria.getItems().clear();

        cmbCategoria.getItems().setAll(
                "Papelería",
                "Tecnología",
                "Accesorios"
        );

        cmbCategoria.getSelectionModel().clearSelection();

    }

    // Configurar columnas de la tabla
    private void configurarTblProductos() {
        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock"));
    }

    // Preparar el formulario para un nuevo producto
    @FXML
    private void nuevoProducto() {
        limpiar();
    }

    // Guardar producto
    @FXML
    private void guardar() {
        if (!validarCampos()) {
            return;
        }

        String sql =
                "INSERT INTO inventario.producto " +
                        "(codigo, nombre, categoria, precio, stock) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Producto guardado correctamente",
                        "El producto ha sido guardado con éxito.");

                limpiar();
                actualizarTabla();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                        "Producto no guardado",
                        "No se insertó ningún registro.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar el producto",
                    ex.getMessage());
        }
    }

    // Actualizar producto seleccionado
    @FXML
    private void actualizar() {
        Producto seleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Sin selección",
                    "Selecciona un producto antes de actualizar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }


        String sql =
                "SELECT p.id_producto, p.codigo, p.nombre, " +
                        "p.categoria, p.precio, p.stock " +
                        "FROM inventario.producto p " +
                        "ORDER BY p.codigo ASC";

        cargarProductos(sql);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            statement.setInt(6, seleccionado.getIdProducto());

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Producto actualizado",
                        "El producto ha sido actualizado correctamente.");

                limpiar();
                actualizarTabla();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                        "Sin cambios",
                        "No se actualizó ningún producto activo.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo actualizar el producto",
                    ex.getMessage());
        }
    }


    @FXML
    private void eliminar() {

        Producto seleccionado =
                tblProductos.getSelectionModel().getSelectedItem();

        // 2. Comprobar que exista una selección
        if (seleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Atención",
                    "Sin selección",
                    "Selecciona un producto antes de eliminar."
            );
            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(
                "¿Deseas eliminar físicamente el producto "
                        + seleccionado.getNombre() + "?"
        );
        confirmacion.setContentText(
                "El registro se eliminará definitivamente de la base de datos."
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        // Eliminarlo de sql
        String sql =
                "DELETE FROM inventario.producto "
                        + "WHERE id_producto = ?";


        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, seleccionado.getIdProducto());

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas == 1) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Éxito",
                        "Producto eliminado",
                        "El producto se eliminó físicamente "
                                + "de la base de datos."
                );

                limpiar();
                actualizarTabla();

            } else {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Atención",
                        "Producto no eliminado",
                        "El producto ya no existe en la base de datos. "
                                + "Recarga la tabla."
                );

                actualizarTabla();
            }

        } catch (SQLException ex) {

            // Registrar detalles técnicos para diagnóstico
            System.err.println(
                    "Error al eliminar producto. SQLState: "
                            + ex.getSQLState()
            );
            ex.printStackTrace();
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo eliminar el producto",
                    "Ocurrió un error al eliminar el producto. "
                            + "Verifica la conexión y las restricciones "
                            + "de la base de datos."
            );
        }
    }


    // Copiar datos de la tabla al formulario
    private void llenarFormulario(Producto p) {
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtPrecio.setText(Double.toString(p.getPrecio()));
        txtStock.setText(Integer.toString(p.getStock()));

    }

    // Actualizar tabla con productos activos
    @FXML
    private void actualizarTabla() {

        String sql =
                "SELECT p.id_producto, p.codigo, p.nombre, " +
                        "p.categoria, p.precio, p.stock " +
                        "FROM inventario.producto p " +
                        "ORDER BY p.id_producto ASC";

        cargarProductos(sql);
    }

    // Ejecutar consulta y llenar la tabla
    private void cargarProductos(String sql) {
        productos.clear();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Producto producto = new Producto();

                producto.setIdProducto(
                        resultSet.getInt("id_producto"));
                producto.setCodigo(
                        resultSet.getString("codigo"));
                producto.setNombre(
                        resultSet.getString("nombre"));
                producto.setCategoria(
                        resultSet.getString("categoria"));
                producto.setPrecio(
                        resultSet.getDouble("precio"));
                producto.setStock(
                        resultSet.getInt("stock"));

                productos.add(producto);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los productos",
                    ex.getMessage());
        }
    }

    // Validar campos obligatorios y valores numéricos
    private boolean validarCampos() {
        if (txtCodigo.getText().isBlank()
                || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank()
                || txtStock.getText().isBlank()
                || cmbCategoria.getValue() == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Campos incompletos",
                    "Completa todos los campos obligatorios.");
            return false;
        }

        try {
            double precio =
                    Double.parseDouble(txtPrecio.getText().trim());
            int stock =
                    Integer.parseInt(txtStock.getText().trim());

            if (!Double.isFinite(precio)
                    || precio <= 0
                    || stock < 0) {

                mostrarAlerta(Alert.AlertType.WARNING, "Validación",
                        "Valores inválidos",
                        "El precio debe ser mayor que cero y el stock " +
                                "debe ser un entero no negativo.");
                return false;
            }

        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación",
                    "Valores inválidos",
                    "El precio debe ser numérico y el stock un entero.");
            return false;
        }

        return true;
    }


    // Limpiar formulario y selección
    @FXML
    private void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        cmbCategoria.setValue(null);

        tblProductos.getSelectionModel().clearSelection();
        txtCodigo.requestFocus();
    }

    // Mostrar alertas
    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String encabezado,
            String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    // Buscar y filtrar productos

    @FXML
    private void buscarProducto() {

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_producto, p.codigo, p.nombre, " +
                        "p.categoria, p.precio, p.stock " +
                        "FROM inventario.producto p " +
                        "WHERE 1 = 1 "
        );

        List<Object> parametros = new ArrayList<>();

        String busqueda = txtBuscar.getText().trim();


        if (!busqueda.isEmpty()) {
            sql.append(
                    "AND (p.codigo ILIKE ? OR p.nombre ILIKE ?) "
            );

            parametros.add("%" + busqueda + "%");
            parametros.add("%" + busqueda + "%");
        }


        String categoria = cmbCategoriaBus.getValue();

        if (categoria != null
                && !categoria.equals("Todas")) {

            sql.append("AND p.categoria = ? ");
            parametros.add(categoria);
        }

        if (chkProductosExistentes.isSelected()) {
            sql.append("AND p.stock > 0 ");
        }

        sql.append("ORDER BY p.codigo ASC");

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                statement.setObject(i + 1, parametros.get(i));
            }

            ObservableList<Producto> resultados =
                    FXCollections.observableArrayList();

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Producto producto = new Producto();

                    producto.setIdProducto(
                            resultSet.getInt("id_producto")
                    );

                    producto.setCodigo(
                            resultSet.getString("codigo")
                    );

                    producto.setNombre(
                            resultSet.getString("nombre")
                    );

                    producto.setCategoria(
                            resultSet.getString("categoria")
                    );

                    producto.setPrecio(
                            resultSet.getDouble("precio")
                    );

                    producto.setStock(
                            resultSet.getInt("stock")
                    );

                    resultados.add(producto);
                }
            }

            productos.setAll(resultados);

            if (resultados.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Búsqueda",
                        "Sin coincidencias",
                        "No se encontraron productos con los filtros indicados."
                );
            }

        } catch (SQLException ex) {

            System.err.println(
                    "Error al buscar productos. SQLState: "
                            + ex.getSQLState()
            );

            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo realizar la búsqueda",
                    "Verifica la conexión con PostgreSQL."
            );
        }
    }



    // Restablecer los filtros
    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbCategoriaBus.getSelectionModel().clearSelection();
        chkProductosExistentes.setSelected(false);

        actualizarTabla();
    }

    @FXML
    public void recargarDatos() {
        try {
            actualizarTabla();

        } catch (Exception ex) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron recargar los datos",
                    "Ocurrió un problema al consultar la base de datos."
            );

            ex.printStackTrace();
        }
    }
}
