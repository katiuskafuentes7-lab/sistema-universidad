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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import java.io.Serial;
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

    @SuppressWarnings("unused")
    static void main(String[] args) {
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
                PRIMARY, _ -> {
                    frame.dispose();
                    showApplicantPortal();
                }));
        cards.add(portalCard("Portal administrativo",
                "Acceso para el personal encargado de revisar y gestionar las solicitudes.",
                new Color(110, 78, 145), _ -> {
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
        save.addActionListener(_ -> form.save());
        clear.addActionListener(_ -> form.clear());
        query.addActionListener(_ -> form.queryStatus());
        logout.addActionListener(_ -> {
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
        logout.addActionListener(_ -> {
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

    private record Applicant(int id, String identificationType, String nationality, String identity,
                             String sex, String firstName, String secondName, String firstSurname,
                             String secondSurname, String birthDate, String photo, String studentType,
                             String email, String phone, String school, String schoolLocation, String credits,
                             String program, String campus, String status, String date, String phoneType,
                             String emergencyPhoneType, String emergencyPhone, String emailType,
                             String secondaryEmailType, String secondaryEmail, String provinceType,
                             String province, String district, String corregimiento, String neighborhood,
                             String collegeType, String baccalaureate, String faculty, String schoolUnit) {
    }

    private static final class ApplicantForm {
        private final ApplicantRepository repository;
        private final JPanel panel = new JPanel(new GridBagLayout());
        private final JComboBox<String> identificationType =
                new JComboBox<>(new String[]{"", "Panameño", "Extranjero"});
        private final JComboBox<String> nationality =
                new JComboBox<>(new String[]{
                        "", "Panameño", "Antillas Holandesas (Antillas Holandesas)",
                        "Islas Caimán (Británica/Caimanesa)", "Eslovaquia (Eslovaca)",
                        "Alemania (Alemana)", "Argentina (Argentina)", "Australia (Australiana)",
                        "Brasil (Brasileña)", "Canadá (Canadiense)", "Chile (Chilena)",
                        "China (China)", "Colombia (Colombiana)", "Costa Rica (Costarricense)",
                        "Cuba (Cubana)", "Ecuador (Ecuatoriana)", "España (Española)",
                        "Estados Unidos (Estadounidense)", "Francia (Francesa)",
                        "Guatemala (Guatemalteca)", "Honduras (Hondureña)", "India (India)",
                        "Italia (Italiana)", "Jamaica (Jamaicana)", "Japón (Japonesa)",
                        "México (Mexicana)", "Nicaragua (Nicaragüense)", "Países Bajos (Neerlandesa)",
                        "Perú (Peruana)", "Portugal (Portuguesa)", "Puerto Rico (Puertorriqueña)",
                        "Reino Unido (Británica)", "República Dominicana (Dominicana)",
                        "Rusia (Rusa)", "Sudáfrica (Sudafricana)", "Suecia (Sueca)",
                        "Suiza (Suiza)", "Ucrania (Ucraniana)", "Uruguay (Uruguaya)",
                        "Venezuela (Venezolana)", "Otra"
                });
        private final JTextField identity = new JTextField();
        private final JComboBox<String> sex =
                new JComboBox<>(new String[]{"", "Femenino", "Masculino"});
        private final JTextField names = new JTextField();
        private final JTextField surnames = new JTextField();
        private final DateField birthDate = new DateField();
        private final JTextField photo = new JTextField();
        private final JComboBox<String> studentType = new JComboBox<>(
                new String[]{"", "Primer ingreso", "Validación", "Cambio de facultad"});
        private final JComboBox<String> phoneType = new JComboBox<>(
                new String[]{"", "Personal", "Residencial", "Trabajo"});
        private final JTextField phone = new JTextField();
        private final JComboBox<String> emergencyPhoneType = new JComboBox<>(
                new String[]{"", "Emergencia", "Familiar", "Otro"});
        private final JTextField emergencyPhone = new JTextField();
        private final JComboBox<String> emailType = new JComboBox<>(
                new String[]{"", "Personal", "Institucional", "Trabajo"});
        private final JTextField email = new JTextField();
        private final JComboBox<String> secondaryEmailType = new JComboBox<>(
                new String[]{"", "Personal", "Institucional", "Trabajo"});
        private final JTextField secondaryEmail = new JTextField();
        private final JComboBox<String> provinceType =
                new JComboBox<>(new String[]{"", "Provincia", "Comarca"});
        private final JComboBox<String> province = new JComboBox<>(provinces());
        private final JTextField district = new JTextField();
        private final JTextField corregimiento = new JTextField();
        private final JTextField neighborhood = new JTextField();
        private final JComboBox<String> collegeType = new JComboBox<>(
                new String[]{"", "Público", "Privado"});
        private final JTextField school = new JTextField();
        private final JComboBox<String> baccalaureate = new JComboBox<>(new String[]{
                "", "Ciencias", "Letras", "Comercio", "Informática", "Humanidades", "Otro"
        });
        private final JComboBox<String> faculty = new JComboBox<>(new String[]{
                "", "Administración de Empresas y Contabilidad", "Arquitectura y Diseño",
                "Ciencias Agropecuarias", "Ciencias de la Educación", "Ciencias Naturales, Exactas y Tecnología",
                "Comunicación Social", "Derecho y Ciencias Políticas", "Economía",
                "Enfermería", "Farmacia", "Humanidades", "Informática, Electrónica y Comunicación",
                "Ingeniería", "Medicina", "Odontología", "Psicología"
        });
        private final JComboBox<String> schoolUnit = new JComboBox<>(new String[]{
                "", "Escuela de Administración de Empresas", "Escuela de Arquitectura",
                "Escuela de Biología", "Escuela de Comunicación Social", "Escuela de Derecho",
                "Escuela de Economía", "Escuela de Enfermería", "Escuela de Ingeniería",
                "Escuela de Informática", "Escuela de Medicina", "Escuela de Psicología"
        });
        private final JComboBox<String> program = new JComboBox<>(programs());
        private final JComboBox<String> campus = new JComboBox<>(campuses());

        private ApplicantForm(ApplicantRepository repository) {
            this.repository = repository;
            panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
            identificationType.addActionListener(_ -> updateNationalities());
            updateNationalities();
            addSection("Datos generales", 0);
            addField(panel, 1, "Tipo de identificación *", identificationType);
            addField(panel, 2, "Cédula *", identity);
            addField(panel, 3, "Nacionalidad *", nationality);
            addField(panel, 4, "Primer y segundo nombre *", names);
            addField(panel, 5, "Primer y segundo apellido *", surnames);
            addField(panel, 6, "Fecha de nacimiento *", birthDate);
            addField(panel, 7, "Género *", sex);
            addPhotoField();
            addField(panel, 9, "Tipo de estudiante *", studentType);

            addSection("Datos de contacto", 10);
            addSubsection("Números telefónicos", 11);
            addField(panel, 12, "Tipo de contacto *", phoneType);
            addField(panel, 13, "Número de teléfono *", phone);
            addField(panel, 14, "Tipo de contacto de emergencia *", emergencyPhoneType);
            addField(panel, 15, "Número de emergencia *", emergencyPhone);

            addSubsection("Correo electrónico", 16);
            addField(panel, 17, "Tipo de correo *", emailType);
            addField(panel, 18, "Correo electrónico *", email);
            addField(panel, 19, "Tipo de correo alternativo *", secondaryEmailType);
            addField(panel, 20, "Correo alternativo *", secondaryEmail);

            addSubsection("Dirección", 21);
            addField(panel, 22, "Tipo de provincia *", provinceType);
            addField(panel, 23, "Provincia *", province);
            addField(panel, 24, "Distrito *", district);
            addField(panel, 25, "Corregimiento *", corregimiento);
            addField(panel, 26, "Barrio *", neighborhood);

            addSection("Datos académicos", 27);
            addField(panel, 28, "Tipo de colegio *", collegeType);
            addField(panel, 29, "Colegio *", school);
            addField(panel, 30, "Bachiller *", baccalaureate);
            addField(panel, 31, "Sede donde estudiará *", campus);
            addField(panel, 32, "Facultad *", faculty);
            addField(panel, 33, "Escuela *", schoolUnit);
            addField(panel, 34, "Carrera *", program);
            JLabel admissionNotice = new JLabel("");
            admissionNotice.setForeground(new Color(75, 88, 103));
            admissionNotice.setFont(new Font("SansSerif", Font.ITALIC, 12));
            JPanel admissionBox = new JPanel(new BorderLayout(8, 0));
            admissionBox.setBackground(Color.WHITE);
            admissionBox.setOpaque(true);
            admissionBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 208, 218)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            admissionBox.setPreferredSize(new Dimension(100, 48));
            admissionBox.add(admissionNotice, BorderLayout.CENTER);
            GridBagConstraints admissionConstraints = new GridBagConstraints();
            admissionConstraints.gridx = 0;
            admissionConstraints.gridy = 35;
            admissionConstraints.gridwidth = 2;
            admissionConstraints.weightx = 1;
            admissionConstraints.fill = GridBagConstraints.HORIZONTAL;
            admissionConstraints.insets = new Insets(6, 4, 6, 4);
            panel.add(admissionBox, admissionConstraints);
            for (JComboBox<String> field : List.of(collegeType, baccalaureate, campus, faculty,
                    schoolUnit, program)) {
                field.addActionListener(_ -> updateAdmissionNotice(admissionNotice));
            }
            school.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent event) {
                    updateAdmissionNotice(admissionNotice);
                }

                @Override
                public void removeUpdate(DocumentEvent event) {
                    updateAdmissionNotice(admissionNotice);
                }

                @Override
                public void changedUpdate(DocumentEvent event) {
                    updateAdmissionNotice(admissionNotice);
                }
            });
            GridBagConstraints fill = new GridBagConstraints();
            fill.gridx = 0;
            fill.gridy = 36;
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

        private void updateAdmissionNotice(JLabel notice) {
            boolean academicInformationComplete = !selected(collegeType).isBlank()
                    && !value(school).isBlank()
                    && !selected(baccalaureate).isBlank()
                    && !selected(campus).isBlank()
                    && !selected(faculty).isBlank()
                    && !selected(schoolUnit).isBlank()
                    && !selected(program).isBlank();
            notice.setText(academicInformationComplete
                    ? "La carrera admitirá: 40 personas después de haber realizado las pruebas."
                    : "");
        }

        private void addSubsection(String text, int row) {
            JLabel label = new JLabel(text);
            label.setForeground(new Color(75, 88, 103));
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            GridBagConstraints constraint = new GridBagConstraints();
            constraint.gridx = 0;
            constraint.gridy = row;
            constraint.gridwidth = 2;
            constraint.anchor = GridBagConstraints.WEST;
            constraint.fill = GridBagConstraints.HORIZONTAL;
            constraint.insets = new Insets(10, 4, 3, 4);
            panel.add(label, constraint);
        }

        private void addPhotoField() {
            photo.setEditable(false);
            JPanel chooser = new JPanel(new BorderLayout(6, 0));
            JButton select = new JButton("Adjuntar foto");
            select.addActionListener(_ -> chooseFile(photo));
            chooser.add(photo, BorderLayout.CENTER);
            chooser.add(select, BorderLayout.EAST);
            addField(panel, 8, "Foto tamaño carnet *", chooser);
        }

        private void chooseFile(JTextField target) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                target.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void updateNationalities() {
            String selected = (String) identificationType.getSelectedItem();
            nationality.removeAllItems();
            nationality.addItem("");
            if ("Panameño".equals(selected)) {
                nationality.addItem("Panameño");
            } else if ("Extranjero".equals(selected)) {
                for (String option : foreignNationalities()) {
                    nationality.addItem(option);
                }
            }
            nationality.setSelectedIndex(0);
        }

        private void save() {
            if (required().stream().anyMatch(String::isBlank)) {
                JOptionPane.showMessageDialog(panel, "Completa todos los campos obligatorios.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                LocalDate.parse(value(birthDate));
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(panel, "La fecha debe tener el formato AAAA-MM-DD.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (isInvalidEmail(value(email)) || isInvalidEmail(value(secondaryEmail))) {
                JOptionPane.showMessageDialog(panel, "Ingresa correos electrónicos válidos.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Applicant applicant = new Applicant(0, selected(identificationType),
                    selected(nationality), value(identity), selected(sex),
                    value(names), "", value(surnames), "",
                    value(birthDate), value(photo), selected(studentType), value(email), value(phone),
                    value(school), "", "", selected(program),
                    selected(campus), "Pendiente", LocalDate.now().toString(),
                    selected(phoneType), selected(emergencyPhoneType), value(emergencyPhone),
                    selected(emailType), selected(secondaryEmailType), value(secondaryEmail),
                    selected(provinceType), selected(province),
                    value(district), value(corregimiento),
                    value(neighborhood), selected(collegeType), selected(baccalaureate),
                    selected(faculty), selected(schoolUnit));
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
                    Applicant applicant = result.getFirst();
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
            return List.of(selected(identificationType), selected(nationality),
                    value(identity), value(names), value(surnames), value(birthDate), value(photo),
                    selected(sex), selected(studentType), selected(phoneType), value(phone),
                    selected(emergencyPhoneType), value(emergencyPhone), selected(emailType), value(email),
                    selected(secondaryEmailType), value(secondaryEmail), selected(provinceType), selected(province),
                    value(district), value(corregimiento), value(neighborhood), selected(collegeType), value(school),
                    selected(baccalaureate), selected(campus), selected(faculty), selected(schoolUnit),
                    selected(program));
        }

        private String selected(JComboBox<String> combo) {
            Object selected = combo.getSelectedItem();
            return selected == null ? "" : selected.toString();
        }

        private String value(JTextField field) {
            return field.getText().trim();
        }

        private void clear() {
            for (JTextField field : List.of(identity, names, surnames, photo, phone, emergencyPhone,
                    email, secondaryEmail, district, corregimiento, neighborhood, school)) {
                field.setText("");
            }
            birthDate.reset();
            for (JComboBox<String> combo : List.of(identificationType, nationality, sex, studentType,
                    phoneType, emergencyPhoneType, emailType, secondaryEmailType, provinceType, province,
                    collegeType, baccalaureate, faculty, schoolUnit, program, campus)) {
                combo.setSelectedIndex(0);
            }
        }

        private JPanel panel() {
            return panel;
        }

        private static String[] programs() {
            return new String[]{"", "Ingeniería de Sistemas", "Administración de Empresas", "Derecho",
                    "Medicina", "Psicología", "Contaduría Pública", "Educación"};
        }

        private static String[] campuses() {
            return new String[]{"", "Bocas del Toro", "Chiriquí", "Veraguas", "Panamá",
                    "Panamá Oeste", "Colón", "Coclé", "Herrera", "Los Santos", "Darién"};
        }

        private static String[] provinces() {
            return new String[]{"", "Bocas del Toro", "Chiriquí", "Coclé", "Colón", "Darién",
                    "Herrera", "Los Santos", "Panamá", "Panamá Oeste", "Veraguas",
                    "Comarca Emberá-Wounaan", "Comarca Guna Yala", "Comarca Ngäbe-Buglé"};
        }

        private static boolean isInvalidEmail(String address) {
            return !address.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
        }

        private static String[] foreignNationalities() {
            return new String[]{
                    "Antillas Holandesas (Antillas Holandesas)",
                    "Islas Caimán (Británica/Caimanesa)", "Eslovaquia (Eslovaca)",
                    "Alemania (Alemana)", "Argentina (Argentina)", "Australia (Australiana)",
                    "Brasil (Brasileña)", "Canadá (Canadiense)", "Chile (Chilena)",
                    "China (China)", "Colombia (Colombiana)", "Costa Rica (Costarricense)",
                    "Cuba (Cubana)", "Ecuador (Ecuatoriana)", "España (Española)",
                    "Estados Unidos (Estadounidense)", "Francia (Francesa)",
                    "Guatemala (Guatemalteca)", "Honduras (Hondureña)", "India (India)",
                    "Italia (Italiana)", "Jamaica (Jamaicana)", "Japón (Japonesa)",
                    "México (Mexicana)", "Nicaragua (Nicaragüense)", "Países Bajos (Neerlandesa)",
                    "Perú (Peruana)", "Portugal (Portuguesa)", "Puerto Rico (Puertorriqueña)",
                    "Reino Unido (Británica)", "República Dominicana (Dominicana)",
                    "Rusia (Rusa)", "Sudáfrica (Sudafricana)", "Suecia (Sueca)",
                    "Suiza (Suiza)", "Ucrania (Ucraniana)", "Uruguay (Uruguaya)",
                    "Venezuela (Venezolana)", "Otra"
            };
        }

        private static final class DateField extends JTextField {
            @Serial
            private static final long serialVersionUID = 1L;
            private static final String PLACEHOLDER = "AAAA-MM-DD";
            private int position;

            private DateField() {
                super(PLACEHOLDER);
                setCaretPosition(0);
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent event) {
                        char key = event.getKeyChar();
                        if (Character.isDigit(key)) {
                            event.consume();
                            if (position < PLACEHOLDER.length()) {
                                StringBuilder value = new StringBuilder(getText());
                                value.setCharAt(position, key);
                                setText(value.toString());
                                position++;
                                if (position < PLACEHOLDER.length()
                                        && PLACEHOLDER.charAt(position) == '-') {
                                    position++;
                                }
                                setCaretPosition(Math.min(position, getDocument().getLength()));
                            }
                        } else if (key == '\b') {
                            event.consume();
                            position = Math.max(0, position - 1);
                            while (position > 0 && PLACEHOLDER.charAt(position) == '-') {
                                position--;
                            }
                            StringBuilder value = new StringBuilder(getText());
                            value.setCharAt(position, PLACEHOLDER.charAt(position));
                            setText(value.toString());
                            setCaretPosition(position);
                        } else {
                            event.consume();
                        }
                    }
                });
            }

            private void reset() {
                setText(PLACEHOLDER);
                position = 0;
                setCaretPosition(0);
            }
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
            find.addActionListener(_ -> refresh(search.getText()));
            search.addActionListener(_ -> refresh(search.getText()));
            status.addActionListener(_ -> updateStatus());
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

    private record ApplicantRepository(Path database) {

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
                writer.write(toLine(new Applicant(nextId, applicant.identificationType(), applicant.nationality(),
                        applicant.identity(), applicant.sex(), applicant.firstName(), applicant.secondName(),
                        applicant.firstSurname(), applicant.secondSurname(), applicant.birthDate(), applicant.photo(),
                        applicant.studentType(), applicant.email(), applicant.phone(), applicant.school(),
                        applicant.schoolLocation(), applicant.credits(), applicant.program(), applicant.campus(),
                        applicant.status(), applicant.date(), applicant.phoneType(), applicant.emergencyPhoneType(),
                        applicant.emergencyPhone(), applicant.emailType(), applicant.secondaryEmailType(),
                        applicant.secondaryEmail(), applicant.provinceType(), applicant.province(),
                        applicant.district(), applicant.corregimiento(), applicant.neighborhood(),
                        applicant.collegeType(), applicant.baccalaureate(), applicant.faculty(),
                        applicant.schoolUnit())));
                writer.newLine();
            }
        }

        private void updateStatus(int id, String status) throws IOException {
            List<Applicant> applicants = find("");
            for (int i = 0; i < applicants.size(); i++) {
                Applicant a = applicants.get(i);
                if (a.id() == id) {
                    applicants.set(i, new Applicant(a.id(), a.identificationType(), a.nationality(), a.identity(),
                            a.sex(), a.firstName(), a.secondName(), a.firstSurname(), a.secondSurname(),
                            a.birthDate(), a.photo(), a.studentType(), a.email(), a.phone(), a.school(),
                            a.schoolLocation(), a.credits(), a.program(), a.campus(), status, a.date(),
                            a.phoneType(), a.emergencyPhoneType(), a.emergencyPhone(), a.emailType(),
                            a.secondaryEmailType(), a.secondaryEmail(), a.provinceType(), a.province(),
                            a.district(), a.corregimiento(), a.neighborhood(), a.collegeType(),
                            a.baccalaureate(), a.faculty(), a.schoolUnit()));
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
            return String.join("\t", escape(a.identificationType()), escape(a.nationality()),
                    escape(a.identity()), escape(a.sex()), escape(a.firstName()), escape(a.secondName()),
                    escape(a.firstSurname()), escape(a.secondSurname()), escape(a.birthDate()), escape(a.photo()),
                    escape(a.studentType()), escape(a.email()), escape(a.phone()), escape(a.school()),
                    escape(a.schoolLocation()), escape(a.credits()), escape(a.program()), escape(a.campus()),
                    escape(a.status()), escape(a.date()), String.valueOf(a.id()), escape(a.phoneType()),
                    escape(a.emergencyPhoneType()), escape(a.emergencyPhone()), escape(a.emailType()),
                    escape(a.secondaryEmailType()), escape(a.secondaryEmail()), escape(a.provinceType()),
                    escape(a.province()), escape(a.district()), escape(a.corregimiento()),
                    escape(a.neighborhood()), escape(a.collegeType()), escape(a.baccalaureate()),
                    escape(a.faculty()), escape(a.schoolUnit()));
        }

        private static Applicant fromLine(String line) {
            String[] v = line.split("\t", -1);
            if (v.length == 8) {
                return new Applicant(Integer.parseInt(v[7]), "", "", v[1], "", v[0], "", "", "",
                        "", "", "", v[2], v[3], "", "", "", v[4], "", v[5], v[6],
                        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            }
            if (v.length == 19) {
                return new Applicant(Integer.parseInt(v[18]), "", unescape(v[0]), unescape(v[1]),
                        unescape(v[2]), unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]),
                        unescape(v[7]), unescape(v[8]), "", unescape(v[9]), unescape(v[10]), unescape(v[11]),
                        unescape(v[12]), unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]),
                        unescape(v[17]), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            }
            if (v.length == 21) {
                return new Applicant(Integer.parseInt(v[20]), unescape(v[0]), unescape(v[1]), unescape(v[2]),
                        unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]), unescape(v[7]),
                        unescape(v[8]), unescape(v[9]), unescape(v[10]), unescape(v[11]), unescape(v[12]),
                        unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]), unescape(v[17]),
                        unescape(v[18]), unescape(v[19]), "", "", "", "", "", "", "", "", "", "", "", "", "",
                        "", "");
            }
            if (v.length != 36) {
                throw new IllegalStateException("Registro inválido en la base de datos.");
            }
            return new Applicant(Integer.parseInt(v[20]), unescape(v[0]), unescape(v[1]), unescape(v[2]),
                    unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]), unescape(v[7]),
                    unescape(v[8]), unescape(v[9]), unescape(v[10]), unescape(v[11]), unescape(v[12]),
                    unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]), unescape(v[17]),
                    unescape(v[18]), unescape(v[19]), unescape(v[21]), unescape(v[22]), unescape(v[23]),
                    unescape(v[24]), unescape(v[25]), unescape(v[26]), unescape(v[27]), unescape(v[28]),
                    unescape(v[29]), unescape(v[30]), unescape(v[31]), unescape(v[32]), unescape(v[33]),
                    unescape(v[34]), unescape(v[35]));
        }

        private static String escape(String value) {
            return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
        }

        private static String unescape(String value) {
            StringBuilder result = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                char current = value.charAt(i);
                if (current == '\\' && i + 1 < value.length()) {
                    char escaped = value.charAt(++i);
                    result.append(switch (escaped) {
                        case '\\' -> '\\';
                        case 't' -> '\t';
                        case 'n' -> '\n';
                        default -> {
                            result.append('\\');
                            yield escaped;
                        }
                    });
                } else {
                    result.append(current);
                }
            }
            return result.toString();
        }
    }

    private static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, "No fue posible completar la operación: " + message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
