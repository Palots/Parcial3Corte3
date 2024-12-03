/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package parcialcorteiii;

/**
 *
 * @author osori
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.w3c.dom.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Clase principal
public class GestionPersonas extends JFrame {
    private JTextField txtId, txtNombre, txtCorreo;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Lista para almacenar personas
    private List<Persona> personas = new ArrayList<>();

    public GestionPersonas() {
        // Configuración de la ventana
        setTitle("Gestión de Personas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel superior para el formulario
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2));
        panelFormulario.add(new JLabel("Identificación:"));
        txtId = new JTextField();
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        panelFormulario.add(txtCorreo);

        JButton btnAgregar = new JButton("Agregar");
        panelFormulario.add(btnAgregar);

        add(panelFormulario, BorderLayout.NORTH);

        // Configuración de la tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo"}, 0);
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 3));

        JButton btnGuardarTxt = new JButton("Guardar Archivo Plano");
        JButton btnLeerTxt = new JButton("Leer Archivo Plano");
        JButton btnGuardarXML = new JButton("Guardar XML");
        JButton btnLeerXML = new JButton("Leer XML");
        JButton btnGuardarJSON = new JButton("Guardar JSON");
        JButton btnLeerJSON = new JButton("Leer JSON");

        panelBotones.add(btnGuardarTxt);
        panelBotones.add(btnLeerTxt);
        panelBotones.add(btnGuardarXML);
        panelBotones.add(btnLeerXML);
        panelBotones.add(btnGuardarJSON);
        panelBotones.add(btnLeerJSON);

        add(panelBotones, BorderLayout.SOUTH);

        // Acción para agregar personas
        btnAgregar.addActionListener(e -> agregarPersona());

        // Acciones para archivos
        btnGuardarTxt.addActionListener(e -> guardarArchivoPlano());
        btnLeerTxt.addActionListener(e -> leerArchivoPlano());
        btnGuardarXML.addActionListener(e -> guardarXML());
        btnLeerXML.addActionListener(e -> leerXML());
        btnGuardarJSON.addActionListener(e -> guardarJSON());
        btnLeerJSON.addActionListener(e -> leerJSON());
    }

    // Clase de excepción personalizada
    class CorreoInvalidoException extends Exception {
        public CorreoInvalidoException(String mensaje) {
            super(mensaje);
        }
    }

    private void agregarPersona() {
        try {
            String id = txtId.getText();
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();

            if (id.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios.");
            }

            if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                throw new CorreoInvalidoException("El correo debe contener '@' y un dominio válido.");
            }

            Persona persona = new Persona(id, nombre, correo);
            personas.add(persona);
            modeloTabla.addRow(new Object[]{id, nombre, correo});
            txtId.setText("");
            txtNombre.setText("");
            txtCorreo.setText("");
        } catch (CorreoInvalidoException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void guardarArchivoPlano() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("personas.txt"))) {
            for (Persona persona : personas) {
                writer.write(persona.getId() + "," + persona.getNombre() + "," + persona.getCorreo());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Archivo plano guardado correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo plano: " + e.getMessage());
        }
    }

    private void leerArchivoPlano() {
        try (BufferedReader reader = new BufferedReader(new FileReader("personas.txt"))) {
            String linea;
            personas.clear();
            modeloTabla.setRowCount(0);
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    Persona persona = new Persona(datos[0], datos[1], datos[2]);
                    personas.add(persona);
                    modeloTabla.addRow(datos);
                }
            }
            JOptionPane.showMessageDialog(this, "Archivo plano leído correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo plano: " + e.getMessage());
        }
    }

    private void guardarXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("Personas");
            doc.appendChild(rootElement);

            for (Persona persona : personas) {
                Element personaElement = doc.createElement("Persona");

                Element id = doc.createElement("ID");
                id.appendChild(doc.createTextNode(persona.getId()));
                personaElement.appendChild(id);

                Element nombre = doc.createElement("Nombre");
                nombre.appendChild(doc.createTextNode(persona.getNombre()));
                personaElement.appendChild(nombre);

                Element correo = doc.createElement("Correo");
                correo.appendChild(doc.createTextNode(persona.getCorreo()));
                personaElement.appendChild(correo);

                rootElement.appendChild(personaElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("personas.xml"));
            transformer.transform(source, result);

            JOptionPane.showMessageDialog(this, "Archivo XML guardado correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo XML: " + e.getMessage());
        }
    }

    private void leerXML() {
        try {
            personas.clear();
            modeloTabla.setRowCount(0);

            File archivo = new File("personas.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            NodeList nodeList = doc.getElementsByTagName("Persona");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nodo = nodeList.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String id = elemento.getElementsByTagName("ID").item(0).getTextContent();
                    String nombre = elemento.getElementsByTagName("Nombre").item(0).getTextContent();
                    String correo = elemento.getElementsByTagName("Correo").item(0).getTextContent();

                    Persona persona = new Persona(id, nombre, correo);
                    personas.add(persona);
                    modeloTabla.addRow(new Object[]{id, nombre, correo});
                }
            }

            JOptionPane.showMessageDialog(this, "Archivo XML leído correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo XML: " + e.getMessage());
        }
    }

private void guardarJSON() {
    try {
        // Crea una instancia del ObjectMapper de Jackson
        ObjectMapper mapper = new ObjectMapper();
        // Habilita el formato bonito (indentación)
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Guarda el archivo JSON en el directorio del proyecto
        mapper.writeValue(new File("personas.json"), personas);

        JOptionPane.showMessageDialog(this, "Archivo JSON guardado correctamente.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el archivo JSON: " + e.getMessage());
    }
}


private void leerJSON() {
    try {
        // Crea una instancia del ObjectMapper de Jackson
        ObjectMapper mapper = new ObjectMapper();
        // Lee el archivo JSON y mapea los datos a una lista de personas
        personas = mapper.readValue(new File("personas.json"),
                mapper.getTypeFactory().constructCollectionType(List.class, Persona.class));

        // Limpia y actualiza la tabla
        modeloTabla.setRowCount(0);
        for (Persona persona : personas) {
            modeloTabla.addRow(new Object[]{persona.getId(), persona.getNombre(), persona.getCorreo()});
        }

        JOptionPane.showMessageDialog(this, "Archivo JSON leído correctamente.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al leer el archivo JSON: " + e.getMessage());
    }
}


    // Clase Persona
    public static class Persona {
        private String id;
        private String nombre;
        private String correo;

        public Persona() {
        }

        public Persona(String id, String nombre, String correo) {
            this.id = id;
            this.nombre = nombre;
            this.correo = correo;
        }

        public String getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCorreo() {
            return correo;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionPersonas().setVisible(true);
        });
    }
}
