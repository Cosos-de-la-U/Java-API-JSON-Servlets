package servlet;

import com.google.gson.Gson;
import dao.ClienteDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cliente;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import static util.Conexion.getConexion;


@WebServlet("/cliente/*")
public class ClienteServlet extends HttpServlet {
    private static final long seriaLVersionUID = 1L;
    private ClienteDAO clienteDao;

    @Override
    public void init() {
        try {
            Connection con = getConexion();
            clienteDao = new ClienteDAO(con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void destroy() {
        super.destroy();
        // close the database connection
        try {
            getConexion().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String idString = request.getParameter("id");
        if (idString != null) {
            // get the cliente with the specified id
            int id = Integer.parseInt(idString);
            try {
                Cliente cliente = clienteDao.buscarPorId(id);
                if (cliente != null) {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("nombre", cliente.getNombre());
                    builder.add("direccion", cliente.getDireccion());
                    builder.add("telefono", cliente.getTelefono());
                    builder.add("email", cliente.getEmail());
                    JsonObject jsonObject = builder.build();
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.print(jsonObject);
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            // get all the cliente
            try {
                // a different way to get the json and simpler TBH
                List<Cliente> cliente = clienteDao.listar();

                Gson gson = new Gson();
                String clienteJSON = gson.toJson(cliente);

                PrintWriter printWriter = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                printWriter.write(clienteJSON);
                printWriter.close();

            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        JsonReader jsonReader = Json.createReader(request.getReader());
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        // extract the fields from the JSON object
        String nombre = jsonObject.getString("fecha");
        String direccion = jsonObject.getString("fecha");
        String telefono = jsonObject.getString("telefono");
        String email = jsonObject.getString("estado");

        // create the Cliente object
        Cliente cliente = new Cliente(nombre, direccion, telefono, email);

        // insert the cliente into the database
        try {
            clienteDao.insertar(cliente);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // read the JSON payload from the request body
        JsonReader jsonReader = Json.createReader(request.getReader());
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        // extract the fields from the JSON object
        int id = jsonObject.getInt("id");
        // extract the fields from the JSON object
        String nombre = jsonObject.getString("fecha");
        String direccion = jsonObject.getString("fecha");
        String telefono = jsonObject.getString("telefono");
        String email = jsonObject.getString("estado");

        // create the Cliente object
        Cliente cliente = new Cliente(id, nombre, direccion, telefono, email);

        // update the cliente in the database
        try {
            clienteDao.actualizar(cliente);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Get the id parameter from the URL path
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id parameter");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id parameter: " + idParam);
            return;
        }

        // Delete the corresponding Cliente object from the database
        try {
            Cliente cliente = clienteDao.buscarPorId(id);
            if (cliente == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cliente no encontrado");
                return;
            }
            // Here is where we delete it
            clienteDao.eliminar(id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al borrar Cliente");
            return;
        }

        // Set the response status code to 204 No Content
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

}
