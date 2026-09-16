import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    private static final Color PRIMARY = new Color(25, 73, 126);
    private static final Color GREEN = new Color(37, 128, 78);
    private static final Color RED = new Color(190, 55, 55);
    private static final Path DATABASE = Path.of("data", "admision.db");
    private final ApplicantRepository repository = new ApplicantRepository(DATABASE);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new Main().showPortal();
        });
    }

    private void showPortal() {
        JFrame frame = window("Portal principal de admisión", 760, 500);
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        root.add(header("PORTAL PRINCIPAL", "Selecciona el tipo de acceso"), BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 25, 0));
        cards.add(portalCard("Portal del aspirante",
                "Registra tus datos, adjunta documentos y consulta el estado de tu solicitud.",
                PRIMARY, e -> {
                    frame.dispose();
                    showApplicantPortal();
                }));
        cards.add(portalCard("Portal administrativo",
                "Acceso para el personal encargado de revisar y gestionar las solicitudes.",
                new Color(110, 78, 145), e -> {
                    if (adminLogin()) {
                        frame.dispose();
                        showAdminPortal();
                    }
                }));
        root.add(cards, BorderLayout.CENTER);

        JLabel footer = new JLabel("Sistema de Admisión Universitaria | Todos los datos se almacenan localmente",
                SwingConstants.CENTER);
        footer.setForeground(new Color(90, 100, 110));
        root.add(footer, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private JPanel portalCard(String title, String description, Color color,
                              java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(12, 18));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 222, 231)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(color);
        JLabel descriptionLabel = new JLabel("<html><div style='width:260px'>" + description + "</div></html>");
        JButton access = button("Ingresar", color);
        access.addActionListener(action);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionLabel, BorderLayout.CENTER);
        card.add(access, BorderLayout.SOUTH);
        return card;
    }

    private boolean adminLogin() {
        JTextField user = new JTextField();
        JPasswordField password = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Usuario:"));
        panel.add(user);
        panel.add(new JLabel("Contraseña:"));
        panel.add(password);
        int result = JOptionPane.showConfirmDialog(null, panel, "Acceso administrativo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }
        if ("admin".equals(user.getText().trim()) && "admin123".equals(new String(password.getPassword()))) {
            return true;
        }
        JOptionPane.showMessageDialog(null, "Credenciales incorrectas.", "Acceso denegado",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private void showApplicantPortal() {
        JFrame frame = window("Portal del aspirante", 900, 760);
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        root.add(header("PORTAL DEL ASPIRANTE", "Formulario de solicitud de admisión"), BorderLayout.NORTH);

        ApplicantForm form = new ApplicantForm(repository);
        root.add(new JScrollPane(form.panel()), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton save = button("Enviar solicitud", GREEN);
        JButton clear = button("Limpiar formulario", RED);
        JButton query = button("Consultar estado", PRIMARY);
        JButton logout = button("Cerrar sesión", RED);
        save.addActionListener(e -> form.save());
        clear.addActionListener(e -> form.clear());
        query.addActionListener(e -> form.queryStatus());
        logout.addActionListener(e -> {
            frame.dispose();
            showPortal();
        });
        actions.add(save);
        actions.add(clear);
        actions.add(query);
        actions.add(logout);
        root.add(actions, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private void showAdminPortal() {
        JFrame frame = window("Portal administrativo", 1200, 720);
        JPanel root = new JPanel(new BorderLayout());
        root.add(header("PORTAL ADMINISTRATIVO", "Gestión completa de solicitudes"), BorderLayout.NORTH);
        AdminPanel admin = new AdminPanel(repository);
        root.add(admin.panel(), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = button("Cerrar sesión", RED);
        logout.addActionListener(e -> {
            frame.dispose();
            showPortal();
        });
        bottom.add(logout);
        root.add(bottom, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private JPanel header(String title, String subtitle) {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JLabel main = new JLabel(title);
        main.setForeground(Color.WHITE);
        main.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel detail = new JLabel(subtitle);
        detail.setForeground(new Color(220, 232, 245));
        header.add(main);
        header.add(detail);
        return header;
    }

    private JFrame window(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        return button;
    }

    private static void addField(JPanel panel, int row, String labelText, java.awt.Component field) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = row;
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(6, 4, 6, 18);
        panel.add(new JLabel(labelText), label);
        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = row;
        input.weightx = 1;
        input.fill = GridBagConstraints.HORIZONTAL;
        input.insets = new Insets(6, 4, 6, 4);
        panel.add(field, input);
    }

    private record Applicant(int id, String nationality, String identity, String sex, String firstName,
                             String secondName, String firstSurname, String secondSurname, String birthDate,
                             String photo, String email, String phone, String school, String schoolLocation,
                             String credits, String program, String campus, String status, String date) {
    }

    private static final class ApplicantForm {
        private final ApplicantRepository repository;
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JTextField nationality = new JTextField();
        private final JTextField identity = new JTextField();
        private final JComboBox<String> sex = new JComboBox<>(new String[]{"Femenino", "Masculino", "Otro"});
        private final JTextField firstName = new JTextField();
        private final JTextField secondName = new JTextField();
        private final JTextField firstSurname = new JTextField();
        private final JTextField secondSurname = new JTextField();
        private final JTextField birthDate = new JTextField("AAAA-MM-DD");
        private final JTextField photo = new JTextField();
        private final JTextField email = new JTextField();
        private final JTextField phone = new JTextField();
        private final JTextField school = new JTextField();
        private final JTextField schoolLocation = new JTextField();
        private final JTextField credits = new JTextField();
        private final JComboBox<String> program = new JComboBox<>(programs());
        private final JComboBox<String> campus = new JComboBox<>(campuses());

        private ApplicantForm(ApplicantRepository repository) {
            this.repository = repository;
            panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
            addSection("Datos personales", 0);
            addField(panel, 1, "Nacionalidad *", nationality);
            addField(panel, 2, "Identidad personal *", identity);
            addField(panel, 3, "Sexo *", sex);
            addField(panel, 4, "Primer nombre *", firstName);
            addField(panel, 5, "Segundo nombre", secondName);
            addField(panel, 6, "Primer apellido *", firstSurname);
            addField(panel, 7, "Segundo apellido", secondSurname);
            addField(panel, 8, "Fecha de nacimiento *", birthDate);
            addFileField(9, "Foto de perfil", photo, "Seleccionar foto");

            addSection("Datos de contacto", 10);
            addField(panel, 11, "Correo electrónico *", email);
            addField(panel, 12, "Teléfono *", phone);

            addSection("Registro del colegio", 13);
            addField(panel, 14, "Nombre del colegio *", school);
            addField(panel, 15, "Provincia / ubicación", schoolLocation);
            addFileField(16, "Créditos de secundaria *", credits, "Adjuntar documento");

            addSection("Preferencias de admisión", 17);
            addField(panel, 18, "Carrera primera opción *", program);
            addField(panel, 19, "Sede de estudios *", campus);
            GridBagConstraints fill = new GridBagConstraints();
            fill.gridx = 0;
            fill.gridy = 20;
            fill.gridwidth = 2;
            fill.weighty = 1;
            fill.fill = GridBagConstraints.VERTICAL;
            panel.add(new JPanel(), fill);
        }

        private void addSection(String text, int row) {
            JLabel label = new JLabel(text);
            label.setForeground(PRIMARY);
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            GridBagConstraints constraint = new GridBagConstraints();
            constraint.gridx = 0;
            constraint.gridy = row;
            constraint.gridwidth = 2;
            constraint.anchor = GridBagConstraints.WEST;
            constraint.fill = GridBagConstraints.HORIZONTAL;
            constraint.insets = new Insets(16, 4, 5, 4);
            panel.add(label, constraint);
        }

        private void addFileField(int row, String label, JTextField field, String buttonText) {
            field.setEditable(false);
            JPanel chooser = new JPanel(new BorderLayout(6, 0));
            JButton select = new JButton(buttonText);
            select.addActionListener(e -> chooseFile(field));
            chooser.add(field, BorderLayout.CENTER);
            chooser.add(select, BorderLayout.EAST);
            addField(panel, row, label, chooser);
        }

        private void chooseFile(JTextField target) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                target.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void save() {
            if (required().stream().anyMatch(String::isBlank)) {
                JOptionPane.showMessageDialog(panel, "Completa todos los campos obligatorios.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!email.getText().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                JOptionPane.showMessageDialog(panel, "Ingresa un correo electrónico válido.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Applicant applicant = new Applicant(0, value(nationality), value(identity),
                    (String) sex.getSelectedItem(), value(firstName), value(secondName), value(firstSurname),
                    value(secondSurname), value(birthDate), value(photo), value(email), value(phone),
                    value(school), value(schoolLocation), value(credits), (String) program.getSelectedItem(),
                    (String) campus.getSelectedItem(), "Pendiente", LocalDate.now().toString());
            try {
                repository.insert(applicant);
                JOptionPane.showMessageDialog(panel, "Solicitud enviada. Tu estado inicial es: Pendiente.",
                        "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                clear();
            } catch (IOException ex) {
                showError(panel, ex.getMessage());
            }
        }

        private void queryStatus() {
            String document = JOptionPane.showInputDialog(panel, "Ingresa tu identidad personal:");
            if (document == null || document.isBlank()) {
                return;
            }
            try {
                List<Applicant> result = repository.find(document.trim());
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No se encontró una solicitud con esa identidad.",
                            "Consulta", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Applicant applicant = result.get(0);
                    JOptionPane.showMessageDialog(panel,
                            "Aspirante: " + applicant.firstName() + " " + applicant.firstSurname()
                                    + "\nPrograma: " + applicant.program() + "\nSede: " + applicant.campus()
                                    + "\nEstado: " + applicant.status(), "Estado de solicitud",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                showError(panel, ex.getMessage());
            }
        }

        private List<String> required() {
            return List.of(value(nationality), value(identity), value(firstName), value(firstSurname),
                    value(birthDate), value(email), value(phone), value(school), value(credits));
        }

        private String value(JTextField field) {
            return field.getText().trim();
        }

        private void clear() {
            for (JTextField field : List.of(nationality, identity, firstName, secondName, firstSurname,
                    secondSurname, birthDate, photo, email, phone, school, schoolLocation, credits)) {
                field.setText("");
            }
            birthDate.setText("AAAA-MM-DD");
            sex.setSelectedIndex(0);
            program.setSelectedIndex(0);
            campus.setSelectedIndex(0);
        }

        private JPanel panel() {
            return panel;
        }

        private static String[] programs() {
            return new String[]{"Ingeniería de Sistemas", "Administración de Empresas", "Derecho",
                    "Medicina", "Psicología", "Contaduría Pública", "Educación"};
        }

        private static String[] campuses() {
            return new String[]{"Bocas del Toro", "Chiriquí", "Veraguas", "Panamá",
                    "Panamá Oeste", "Colón", "Coclé", "Herrera", "Los Santos", "Darién"};
        }
    }

    private static final class AdminPanel {
        private final ApplicantRepository repository;
        private final DefaultTableModel model = new DefaultTableModel(new Object[]{
                "ID", "Identidad", "Nombre", "Colegio", "Carrera", "Sede", "Estado", "Correo", "Teléfono"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        private final JTable table = new JTable(model);
        private int selectedId = -1;

        private AdminPanel(ApplicantRepository repository) {
            this.repository = repository;
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(27);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                    selectedId = (int) table.getValueAt(table.getSelectedRow(), 0);
                }
            });
            refresh();
        }

        private JPanel panel() {
            JPanel root = new JPanel(new BorderLayout(10, 10));
            root.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));
            JTextField search = new JTextField();
            JButton find = buttonStatic("Buscar", PRIMARY);
            JButton status = buttonStatic("Actualizar estado", new Color(185, 119, 15));
            find.addActionListener(e -> refresh(search.getText()));
            search.addActionListener(e -> refresh(search.getText()));
            status.addActionListener(e -> updateStatus());
            JPanel toolbar = new JPanel(new BorderLayout(8, 0));
            toolbar.add(search, BorderLayout.CENTER);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            actions.add(find);
            actions.add(status);
            toolbar.add(actions, BorderLayout.EAST);
            root.add(toolbar, BorderLayout.NORTH);
            root.add(new JScrollPane(table), BorderLayout.CENTER);
            return root;
        }

        private void refresh() {
            refresh("");
        }

        private void refresh(String query) {
            try {
                model.setRowCount(0);
                for (Applicant applicant : repository.find(query)) {
                    model.addRow(new Object[]{applicant.id(), applicant.identity(),
                            applicant.firstName() + " " + applicant.firstSurname(), applicant.school(),
                            applicant.program(), applicant.campus(), applicant.status(), applicant.email(),
                            applicant.phone()});
                }
            } catch (IOException ex) {
                showError(table, ex.getMessage());
            }
        }

        private void updateStatus() {
            if (selectedId < 0) {
                JOptionPane.showMessageDialog(table, "Selecciona una solicitud.", "Sin selección",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String status = (String) JOptionPane.showInputDialog(table, "Nuevo estado:", "Actualizar estado",
                    JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"Pendiente", "En proceso", "Admitido", "Rechazado"}, "En proceso");
            if (status != null) {
                try {
                    repository.updateStatus(selectedId, status);
                    refresh();
                } catch (IOException ex) {
                    showError(table, ex.getMessage());
                }
            }
        }

        private static JButton buttonStatic(String text, Color color) {
            JButton button = new JButton(text);
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            return button;
        }
    }

    private static final class ApplicantRepository {
        private final Path database;

        private ApplicantRepository(Path database) {
            this.database = database;
        }

        private List<Applicant> find(String query) throws IOException {
            ensureDatabase();
            List<Applicant> result = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(database, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    Applicant applicant = fromLine(line);
                    String searchable = (applicant.identity() + " " + applicant.firstName() + " "
                            + applicant.firstSurname() + " " + applicant.school() + " "
                            + applicant.program() + " " + applicant.campus()).toLowerCase(Locale.ROOT);
                    if (query == null || query.isBlank()
                            || searchable.contains(query.toLowerCase(Locale.ROOT))) {
                        result.add(applicant);
                    }
                }
            }
            return result;
        }

        private void insert(Applicant applicant) throws IOException {
            ensureDatabase();
            int nextId = find("").stream().mapToInt(Applicant::id).max().orElse(0) + 1;
            try (BufferedWriter writer = Files.newBufferedWriter(database, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND)) {
                writer.write(toLine(new Applicant(nextId, applicant.nationality(), applicant.identity(),
                        applicant.sex(), applicant.firstName(), applicant.secondName(), applicant.firstSurname(),
                        applicant.secondSurname(), applicant.birthDate(), applicant.photo(), applicant.email(),
                        applicant.phone(), applicant.school(), applicant.schoolLocation(), applicant.credits(),
                        applicant.program(), applicant.campus(), applicant.status(), applicant.date())));
                writer.newLine();
            }
        }

        private void updateStatus(int id, String status) throws IOException {
            List<Applicant> applicants = find("");
            for (int i = 0; i < applicants.size(); i++) {
                Applicant a = applicants.get(i);
                if (a.id() == id) {
                    applicants.set(i, new Applicant(a.id(), a.nationality(), a.identity(), a.sex(),
                            a.firstName(), a.secondName(), a.firstSurname(), a.secondSurname(), a.birthDate(),
                            a.photo(), a.email(), a.phone(), a.school(), a.schoolLocation(), a.credits(),
                            a.program(), a.campus(), status, a.date()));
                }
            }
            rewrite(applicants);
        }

        private void rewrite(List<Applicant> applicants) throws IOException {
            try (BufferedWriter writer = Files.newBufferedWriter(database, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Applicant applicant : applicants) {
                    writer.write(toLine(applicant));
                    writer.newLine();
                }
            }
        }

        private void ensureDatabase() throws IOException {
            if (database.getParent() != null) {
                Files.createDirectories(database.getParent());
            }
            if (Files.notExists(database)) {
                Files.createFile(database);
            }
        }

        private static String toLine(Applicant a) {
            return String.join("\t", escape(a.nationality()), escape(a.identity()), escape(a.sex()),
                    escape(a.firstName()), escape(a.secondName()), escape(a.firstSurname()),
                    escape(a.secondSurname()), escape(a.birthDate()), escape(a.photo()), escape(a.email()),
                    escape(a.phone()), escape(a.school()), escape(a.schoolLocation()), escape(a.credits()),
                    escape(a.program()), escape(a.campus()), escape(a.status()), escape(a.date()),
                    String.valueOf(a.id()));
        }

        private static Applicant fromLine(String line) {
            String[] v = line.split("\t", -1);
            if (v.length == 8) {
                return new Applicant(Integer.parseInt(v[7]), "", v[1], "", v[0], "", "", "",
                        "", "", v[2], v[3], "", "", "", v[4], "", v[5], v[6]);
            }
            if (v.length != 19) {
                throw new IllegalStateException("Registro inválido en la base de datos.");
            }
            return new Applicant(Integer.parseInt(v[18]), unescape(v[0]), unescape(v[1]), unescape(v[2]),
                    unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]), unescape(v[7]),
                    unescape(v[8]), unescape(v[9]), unescape(v[10]), unescape(v[11]), unescape(v[12]),
                    unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]), unescape(v[17]));
        }

        private static String escape(String value) {
            return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
        }

        private static String unescape(String value) {
            return value.replace("\\n", "\n").replace("\\t", "\t").replace("\\\\", "\\");
        }
    }

    private static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, "No fue posible completar la operación: " + message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
