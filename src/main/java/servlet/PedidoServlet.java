package servlet;

import com.google.gson.Gson;
import dao.PedidoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Pedido;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import static util.Conexion.getConexion;


@WebServlet("/pedido/*")
public class PedidoServlet extends HttpServlet {
    private static final long seriaLVersionUID = 1L;
    private PedidoDAO pedidoDao;

    @Override
    public void init() {
        try {
            Connection con = getConexion();
            pedidoDao = new PedidoDAO(con);
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
            // get the pedido with the specified id
            int id = Integer.parseInt(idString);
            try {
                Pedido pedido = pedidoDao.buscarPorId(id);
                if (pedido != null) {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("id", pedido.getId());
                    builder.add("idCliente", pedido.getIdCliente());
                    builder.add("fecha", new SimpleDateFormat("MMM dd, yyyy").format(pedido.getFecha()));
                    builder.add("total", pedido.getTotal());
                    builder.add("estado", pedido.getEstado());
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
            // get all the pedido
            try {
                // a different way to get the json and simpler TBH
                List<Pedido> pedido = pedidoDao.listar();

                Gson gson = new Gson();
                String pedidoJSON = gson.toJson(pedido);

                PrintWriter printWriter = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                printWriter.write(pedidoJSON);
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
        int idCliente = jsonObject.getInt("idCliente");
        String fechaString = jsonObject.getString("fecha");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal total = BigDecimal.valueOf(jsonObject.getJsonNumber("total").doubleValue());
        String estado = jsonObject.getString("estado");

        // parse the fechaString to a Date object
        Date fecha = null;
        try {
            fecha = formatter.parse(fechaString);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // create the Pedido object
        Pedido pedido = new Pedido(idCliente, fecha, total, estado);

        // insert the pedido into the database
        try {
            pedidoDao.insertar(pedido);
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
        int idCliente = jsonObject.getInt("idCliente");
        String fechaString = jsonObject.getString("fecha");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal total = BigDecimal.valueOf(jsonObject.getJsonNumber("total").doubleValue());
        String estado = jsonObject.getString("estado");

        // parse the fechaString to a Date object
        Date fecha = null;
        try {
            fecha = formatter.parse(fechaString);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // create the Pedido object
        Pedido pedido = new Pedido(id, idCliente, fecha, total, estado);

        // update the pedido in the database
        try {
            pedidoDao.actualizar(pedido);
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

        // Delete the corresponding Pedido object from the database
        try {
            Pedido pedido = pedidoDao.buscarPorId(id);
            if (pedido == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Pedido no encontrado");
                return;
            }
            // Here is where we delete it
            pedidoDao.eliminar(id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al borrar Pedido");
            return;
        }

        // Set the response status code to 204 No Content
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

}
