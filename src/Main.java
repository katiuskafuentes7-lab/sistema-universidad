import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.nio.file.InvalidPathException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Main {
    private static final Color PRIMARY = new Color(25, 73, 126);
    private static final Color GREEN = new Color(37, 128, 78);
    private static final Color RED = new Color(190, 55, 55);
    private static final Color SURFACE = new Color(247, 249, 252);
    private static final Color BORDER = new Color(218, 226, 236);
    private static final ImageIcon UNIVERSITY_LOGO = loadUniversityLogo();
    private static final Path DATABASE = Path.of("data", "admision.db");
    private final ApplicantRepository repository = new ApplicantRepository(DATABASE);
    private boolean nextWindowMaximized;

    @SuppressWarnings("unused")
    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            UIManager.put("Panel.background", SURFACE);
            UIManager.put("OptionPane.background", SURFACE);
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 13));
            new Main().showPortal();
        });
    }

    private void showPortal() {
        JFrame frame = window("Portal principal de admisión", 880, 560);
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(28, 38, 24, 38));
        root.add(header("ADMISIÓN UNIVERSITARIA", "Selecciona el portal para continuar"),
                BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 20, 0));
        cards.setOpaque(false);
        Dimension cardsSize = new Dimension(860, 320);
        cards.add(portalCard("Portal del aspirante",
                "Completa tu solicitud, adjunta los documentos requeridos y consulta el avance de tu admisión.",
                PRIMARY, "01", "Registro y seguimiento",
                _ -> navigate(frame, this::showApplicantPortal)));
        cards.add(portalCard("Portal administrativo",
                "Revisa los expedientes y gestiona cada solicitud durante el proceso de admisión.",
                new Color(48, 108, 119), "02", "Revisión de expedientes", _ -> {
                    if (adminLogin()) {
                        navigate(frame, this::showAdminPortal);
                    }
                }));
        cards.setPreferredSize(cardsSize);
        cards.setMaximumSize(cardsSize);
        root.add(portalCardArea(cards), BorderLayout.CENTER);

        JLabel footer = new JLabel("Sistema de Admisión Universitaria  ·  Los datos se almacenan localmente",
                SwingConstants.CENTER);
        footer.setForeground(new Color(90, 100, 110));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        root.add(footer, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private JPanel portalCardArea(JPanel cards) {
        JPanel cardsArea = new JPanel(new java.awt.GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g.setPaint(new GradientPaint(0, 0, new Color(244, 248, 252),
                        width, height, new Color(250, 251, 253)));
                g.fillRect(0, 0, width, height);

                g.setStroke(new BasicStroke(1.2f));
                g.setColor(new Color(25, 73, 126, 18));
                int motifSize = Math.clamp(height / 3, 150, 220);
                int centerX = width / 2;
                int centerY = height / 2;
                g.drawOval(centerX - motifSize / 2, centerY - motifSize / 2,
                        motifSize, motifSize);
                g.drawOval(centerX - motifSize / 2 + 12, centerY - motifSize / 2 + 12,
                        motifSize - 24, motifSize - 24);

                g.setStroke(new BasicStroke(1.5f));
                g.setColor(new Color(25, 73, 126, 24));
                g.drawArc(18, height / 2 - 74, 148, 148, 245, 230);
                g.drawArc(width - 166, height / 2 - 74, 148, 148, 65, 230);
                g.drawArc(42, 24, 82, 82, 185, 160);
                g.drawArc(width - 124, height - 106, 82, 82, 5, 160);

                g.setColor(new Color(183, 145, 67, 105));
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int accent = 10;
                g.drawLine(52, 38, 52 + accent, 38);
                g.drawLine(52, 38 + 14, 52 + accent, 38 + 14);
                g.drawLine(width - 62, height - 38, width - 62 + accent, height - 38);
                g.drawLine(width - 62, height - 38 - 14, width - 62 + accent, height - 38 - 14);
                g.dispose();
            }
        };
        cardsArea.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;
        cardsArea.add(Box.createVerticalGlue(), constraints);
        constraints.gridy = 1;
        constraints.weighty = 0;
        cardsArea.add(cards, constraints);

        constraints.gridy = 2;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        cardsArea.add(Box.createVerticalGlue(), constraints);
        return cardsArea;
    }

    private JPanel portalCard(String title, String description, Color color,
                              String number, String service,
                              java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(12, 18));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createMatteBorder(4, 0, 0, 0, color)),
                BorderFactory.createEmptyBorder(30, 30, 28, 30)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(color);
        JLabel numberLabel = new JLabel(number, SwingConstants.CENTER);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 22));
        numberLabel.setForeground(color);
        numberLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        numberLabel.setPreferredSize(new Dimension(54, 54));
        JPanel cardHeading = new JPanel(new BorderLayout(12, 0));
        cardHeading.setOpaque(false);
        cardHeading.add(numberLabel, BorderLayout.WEST);
        cardHeading.add(titleLabel, BorderLayout.CENTER);
        JButton access = button("Ingresar", color);
        access.setPreferredSize(new Dimension(132, 42));
        access.addActionListener(action);
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);
        actionPanel.add(access, BorderLayout.EAST);
        card.add(cardHeading, BorderLayout.NORTH);
        card.add(portalDetails(description, service, color), BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel portalDetails(String description, String service, Color color) {
        JLabel descriptionLabel = new JLabel("<html><div style='width:260px'>" + description + "</div></html>");
        descriptionLabel.setForeground(new Color(80, 94, 110));
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JLabel serviceLabel = new JLabel(service.toUpperCase(Locale.ROOT));
        serviceLabel.setForeground(color);
        serviceLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        JPanel details = new JPanel(new BorderLayout(0, 15));
        details.setOpaque(false);
        details.add(serviceLabel, BorderLayout.NORTH);
        details.add(descriptionLabel, BorderLayout.CENTER);
        return details;
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
        JFrame frame = window("Portal del aspirante", 960, 620);
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        root.add(header("PORTAL DEL ASPIRANTE", "Registro y actualización de solicitudes"),
                BorderLayout.NORTH);
        JPanel options = new JPanel(new GridLayout(1, 2, 20, 0));
        options.setOpaque(false);
        options.add(portalCard("Formulario de registro",
                "Completa y envía una nueva solicitud de admisión con tus datos y documentos.",
                PRIMARY, "01", "Nueva solicitud",
                _ -> navigate(frame, () -> showApplicantForm(null))));
        options.add(portalCard("Actualizar datos y Documentos",
                "Busca tu solicitud existente por cédula y actualiza tu información o archivos adjuntos.",
                new Color(48, 108, 119), "02", "Solicitud existente", _ -> {
                    Applicant applicant = findApplicantForUpdate(frame);
                    if (applicant != null) {
                        navigate(frame, () -> showApplicantForm(applicant));
                    }
                }));
        Dimension optionsSize = new Dimension(860, 320);
        options.setPreferredSize(optionsSize);
        options.setMaximumSize(optionsSize);
        JPanel optionsArea = new JPanel(new GridBagLayout());
        optionsArea.setOpaque(false);
        optionsArea.add(options);
        root.add(optionsArea, BorderLayout.CENTER);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        footer.setOpaque(false);
        JButton back = button("Volver", RED);
        back.addActionListener(_ -> navigate(frame, this::showPortal));
        footer.add(back);
        root.add(footer, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private Applicant findApplicantForUpdate(JFrame parent) {
        JTextField identity = new JTextField();
        JTextField email = new JTextField();
        JTextField registrationNumber = new JTextField();
        JPanel credentials = new JPanel(new GridLayout(3, 2, 8, 8));
        credentials.add(new JLabel("Cédula:"));
        credentials.add(identity);
        credentials.add(new JLabel("Correo electrónico:"));
        credentials.add(email);
        credentials.add(new JLabel("Número de inscripción:"));
        credentials.add(registrationNumber);
        int result = JOptionPane.showConfirmDialog(parent, credentials,
                "Verificar solicitud para actualizar", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result != JOptionPane.OK_OPTION || identity.getText().isBlank()
                || email.getText().isBlank() || registrationNumber.getText().isBlank()) {
            return null;
        }
        try {
            Applicant applicant = repository.find("")
                    .stream()
                    .filter(record -> record.identity().equalsIgnoreCase(identity.getText().trim())
                            && record.email().equalsIgnoreCase(email.getText().trim())
                            && Integer.toString(record.id()).equals(registrationNumber.getText().trim()))
                    .findFirst()
                    .orElse(null);
            if (applicant == null) {
                JOptionPane.showMessageDialog(parent,
                        "Los datos ingresados no coinciden con una solicitud registrada.",
                        "Verificación fallida", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            if (!AdmissionProcessing.PENDING.equals(applicant.status())) {
                JOptionPane.showMessageDialog(parent,
                        "Solo se pueden actualizar solicitudes que aún estén pendientes.",
                        "Actualización no disponible", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return applicant;
        } catch (IOException ex) {
            showError(parent, ex.getMessage());
            return null;
        }
    }

    private void showApplicantForm(Applicant existingApplicant) {
        boolean updating = existingApplicant != null;
        JFrame frame = window(updating ? "Actualizar datos y documentos" : "Formulario de registro",
                900, 760);
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        JPanel heading = new JPanel(new BorderLayout(0, 8));
        heading.setOpaque(false);
        heading.add(header(updating ? "ACTUALIZAR DATOS Y DOCUMENTOS" : "FORMULARIO DE REGISTRO",
                updating ? "Revisa y actualiza tu solicitud pendiente"
                        : "Completa los campos marcados con * para registrar tu solicitud"),
                BorderLayout.CENTER);
        root.add(heading, BorderLayout.NORTH);

        ApplicantForm form = new ApplicantForm(repository, existingApplicant);
        root.add(new JScrollPane(form.panel()), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actions.setOpaque(false);
        JButton save = button(updating ? "Guardar actualización" : "Enviar solicitud", GREEN);
        JButton clear = button("Limpiar formulario", new Color(105, 119, 136));
        JButton query = button("Consultar estado", PRIMARY);
        JButton back = button("Volver al portal aspirante", RED);
        save.addActionListener(_ -> form.save());
        clear.addActionListener(_ -> form.clear());
        query.addActionListener(_ -> form.queryStatus());
        back.addActionListener(_ -> navigate(frame, this::showApplicantPortal));
        actions.add(save);
        actions.add(clear);
        actions.add(query);
        actions.add(back);
        root.add(actions, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private void showAdminPortal() {
        JFrame frame = window("Portal administrativo", 1200, 720);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SURFACE);
        JPanel heading = new JPanel(new BorderLayout(0, 8));
        heading.setOpaque(false);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        heading.add(header("PORTAL ADMINISTRATIVO", "Gestión y revisión de solicitudes"),
                BorderLayout.CENTER);
        JLabel processHint = new JLabel("  Bandeja de expedientes  ·  Seguimiento de cada etapa de admisión");
        processHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        processHint.setForeground(new Color(78, 95, 112));
        heading.add(processHint, BorderLayout.SOUTH);
        root.add(heading, BorderLayout.NORTH);
        AdminPanel admin = new AdminPanel(repository);
        root.add(admin.panel(), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bottom.setOpaque(false);
        JButton logout = button("Cerrar sesión", RED);
        logout.addActionListener(_ -> navigate(frame, this::showPortal));
        bottom.add(logout);
        root.add(bottom, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private JPanel header(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(new Color(22, 61, 101));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(57, 145, 159)),
                BorderFactory.createEmptyBorder(12, 18, 13, 22)));
        JPanel identity = new JPanel(new BorderLayout(14, 0));
        identity.setOpaque(false);
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(62, 72));
        if (UNIVERSITY_LOGO != null) {
            logo.setIcon(UNIVERSITY_LOGO);
            logo.setToolTipText("Universidad de Panamá");
        } else {
            logo.setText("UP");
            logo.setForeground(Color.WHITE);
            logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        }
        JLabel main = new JLabel(title);
        main.setForeground(Color.WHITE);
        main.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel detail = new JLabel(subtitle);
        detail.setForeground(new Color(213, 229, 240));
        detail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);
        titles.add(main);
        titles.add(detail);
        identity.add(logo, BorderLayout.WEST);
        identity.add(titles, BorderLayout.CENTER);
        header.add(identity, BorderLayout.CENTER);
        return header;
    }

    private static ImageIcon loadUniversityLogo() {
        try (InputStream resource = Main.class.getResourceAsStream("/universidad-panama-logo.png")) {
            if (resource != null) {
                return scaledLogo(ImageIO.read(resource));
            }
            Path sourceFile = Path.of("src", "universidad-panama-logo.png");
            if (Files.isRegularFile(sourceFile)) {
                return scaledLogo(ImageIO.read(sourceFile.toFile()));
            }
        } catch (IOException ex) {
            System.err.println("No se pudo cargar el logo de la Universidad de Panamá: " + ex.getMessage());
        }
        System.err.println("No se encontró src/universidad-panama-logo.png; se mostrará el monograma UP.");
        return null;
    }

    private static ImageIcon scaledLogo(java.awt.image.BufferedImage image) throws IOException {
        if (image == null) {
            throw new IOException("El archivo del logo no contiene una imagen válida.");
        }
        java.awt.Image scaled = image.getScaledInstance(58, 68, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private JFrame window(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(width, height));
        if (nextWindowMaximized) {
            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            nextWindowMaximized = false;
        }
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private void navigate(JFrame currentFrame, Runnable openNextScreen) {
        nextWindowMaximized = (currentFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        currentFrame.dispose();
        openNextScreen.run();
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

    private static void addFieldPair(JPanel panel, int row, String firstLabel,
                                     java.awt.Component firstField, String secondLabel,
                                     java.awt.Component secondField) {
        JPanel fields = new JPanel(new GridLayout(1, 2, 12, 0));
        fields.setOpaque(false);
        addStackedField(fields, firstLabel, firstField);
        addStackedField(fields, secondLabel, secondField);
        addFieldRow(panel, row, fields);
    }

    private static void addFieldTriple(JPanel panel, int row,
                                       String firstLabel, java.awt.Component firstField,
                                       String secondLabel, java.awt.Component secondField,
                                       String thirdLabel, java.awt.Component thirdField) {
        JPanel fields = new JPanel(new GridLayout(1, 3, 12, 0));
        fields.setOpaque(false);
        addStackedField(fields, firstLabel, firstField);
        addStackedField(fields, secondLabel, secondField);
        addStackedField(fields, thirdLabel, thirdField);
        addFieldRow(panel, row, fields);
    }

    private static void addFieldRow(JPanel panel, int row, JPanel fields) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 4;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(6, 4, 6, 4);
        panel.add(fields, constraints);
    }

    private static void addStackedField(JPanel panel, String labelText, java.awt.Component field) {
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 4));
        container.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(62, 76, 92));
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        container.add(label);
        container.add(field);
        panel.add(container);
    }

    private record Applicant(int id, String identificationType, String nationality, String identity,
                             String sex, String firstName, String secondName, String firstSurname,
                             String secondSurname, String birthDate, String photo, String studentType,
                             String email, String phone, String school, String schoolLocation, String credits,
                             String program, String campus, String status, String date, String phoneType,
                             String emergencyPhoneType, String emergencyPhone, String emailType,
                             String secondaryEmailType, String secondaryEmail, String addressType,
                             String province, String district, String corregimiento, String neighborhood,
                             String collegeType, String baccalaureate, String faculty, String schoolUnit,
                             String transcript, String identificationDocument) {
    }

    private static final class AdmissionProcessing {
        private static final String PENDING = "Pendiente";
        private static final String IN_PROGRESS = "En proceso";
        private static final String ADMITTED = "Admitido";
        private static final String REJECTED = "Rechazado";

        private AdmissionProcessing() {
        }

        private static List<String> validate(Applicant applicant) {
            List<String> issues = new ArrayList<>();
            if (applicant.identificationType().isBlank() || applicant.nationality().isBlank()
                    || applicant.identity().isBlank() || applicant.sex().isBlank()
                    || applicant.firstName().isBlank()
                    || applicant.firstSurname().isBlank() || applicant.birthDate().isBlank()
                    || applicant.photo().isBlank() || applicant.studentType().isBlank()) {
                issues.add("Faltan datos personales obligatorios.");
            }
            if (ApplicantForm.isInvalidEmail(applicant.email())
                    || ApplicantForm.isInvalidEmail(applicant.secondaryEmail())) {
                issues.add("Uno o más correos electrónicos no son válidos.");
            }
            try {
                LocalDate.parse(applicant.birthDate());
            } catch (java.time.format.DateTimeParseException ex) {
                issues.add("La fecha de nacimiento no tiene el formato AAAA-MM-DD.");
            }
            if (applicant.phoneType().isBlank() || applicant.phone().isBlank()
                    || applicant.emergencyPhoneType().isBlank() || applicant.emergencyPhone().isBlank()
                    || applicant.emailType().isBlank() || applicant.secondaryEmailType().isBlank()
                    || applicant.addressType().isBlank() || applicant.province().isBlank()
                    || applicant.district().isBlank() || applicant.corregimiento().isBlank()
                    || applicant.neighborhood().isBlank()) {
                issues.add("Faltan datos de contacto o dirección.");
            }
            if (applicant.collegeType().isBlank() || applicant.school().isBlank()
                    || applicant.baccalaureate().isBlank() || applicant.campus().isBlank()
                    || applicant.faculty().isBlank() || applicant.schoolUnit().isBlank()
                    || applicant.program().isBlank()) {
                issues.add("Faltan datos académicos obligatorios.");
            }
            if (attachmentInvalid(applicant.photo(), "jpg", "jpeg", "png", "gif", "pdf")) {
                issues.add("La foto tamaño carnet no está disponible o no cumple los requisitos.");
            }
            if (attachmentInvalid(applicant.transcript(), "pdf")) {
                issues.add("El boletín o créditos debe estar disponible en formato PDF y pesar hasta 1 MB.");
            }
            if (attachmentInvalid(applicant.identificationDocument(), "jpg", "jpeg", "png", "gif", "pdf")) {
                issues.add("La cédula debe estar disponible en un formato permitido y pesar hasta 1 MB.");
            }
            return issues;
        }

        private static String[] nextStatuses(String currentStatus) {
            return switch (currentStatus) {
                case PENDING -> new String[]{IN_PROGRESS};
                case IN_PROGRESS -> new String[]{ADMITTED, REJECTED};
                default -> new String[0];
            };
        }

        private static boolean attachmentInvalid(String filePath, String... extensions) {
            try {
                Path path = Path.of(filePath);
                String name = path.getFileName().toString();
                int separator = name.lastIndexOf('.');
                String extension = separator < 0 ? "" : name.substring(separator + 1).toLowerCase(Locale.ROOT);
                return !Files.isRegularFile(path) || !List.of(extensions).contains(extension)
                        || Files.size(path) > 1024 * 1024;
            } catch (IOException | InvalidPathException ex) {
                return false;
            }
        }
    }

    private static final class ApplicantForm {
        private final ApplicantRepository repository;
        private final Applicant existingApplicant;
        private final JPanel panel = new JPanel(new GridBagLayout());
        private boolean updatingAcademicOptions;
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
        private final JLabel photoStatus = new JLabel("Ningún archivo subido");
        private final JLabel photoPreview = new JLabel("", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(java.awt.Graphics graphics) {
                super.paintComponent(graphics);
                if (getIcon() != null) {
                    return;
                }
                java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
                g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int diameter = Math.min(104, Math.min(getWidth() - 24, getHeight() - 24));
                if (diameter > 0) {
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;
                    g.setColor(new Color(25, 73, 126));
                    g.fillOval(x, y, diameter, diameter);
                    g.setColor(new Color(145, 151, 158));
                    g.setStroke(new java.awt.BasicStroke(3));
                    g.drawOval(x + 1, y + 1, diameter - 2, diameter - 2);
                    int center = getWidth() / 2;
                    int head = diameter / 5;
                    g.fillOval(center - head / 2, y + diameter / 4, head, head);
                    java.awt.geom.Path2D shoulders = new java.awt.geom.Path2D.Double();
                    shoulders.moveTo(center - diameter * 0.27, y + diameter * 0.73);
                    shoulders.curveTo(center - diameter * 0.25, y + diameter * 0.57,
                            center + diameter * 0.25, y + diameter * 0.57,
                            center + diameter * 0.27, y + diameter * 0.73);
                    shoulders.closePath();
                    g.fill(shoulders);
                }
                g.dispose();
            }
        };
        private final JTextField transcript = new JTextField();
        private final JTextField identificationDocument = new JTextField();
        private final JLabel transcriptStatus = new JLabel("Ningún archivo subido");
        private final JLabel identificationStatus = new JLabel("Ningún archivo subido");
        private final JComboBox<String> studentType = new JComboBox<>(
                new String[]{"", "Primer ingreso", "Validación", "Cambio de facultad"});
        private final JComboBox<String> phoneType = new JComboBox<>(
                new String[]{"", "Personal", "Oficina", "Contacto"});
        private final JTextField phone = new JTextField();
        private final JComboBox<String> emergencyPhoneType = new JComboBox<>(
                new String[]{"", "Personal", "Oficina", "Contacto"});
        private final JTextField emergencyPhone = new JTextField();
        private final JComboBox<String> emailType = new JComboBox<>(
                new String[]{"", "Personal", "Oficina", "Contacto"});
        private final JTextField email = new JTextField();
        private final JComboBox<String> secondaryEmailType = new JComboBox<>(
                new String[]{"", "Personal", "Oficina", "Contacto"});
        private final JTextField secondaryEmail = new JTextField();
        private final JComboBox<String> addressType =
                new JComboBox<>(new String[]{"", "Personal", "Oficina", "Contacto"});
        private final JComboBox<String> province = new JComboBox<>(provinces());
        private final JComboBox<String> district = new JComboBox<>(new String[]{""});
        private final JComboBox<String> corregimiento = new JComboBox<>(new String[]{""});
        private final JTextField neighborhood = new JTextField();
        private final JComboBox<String> collegeType = new JComboBox<>(
                new String[]{"", "Público", "Privado", "Extranjero"});
        private final JComboBox<String> school = new JComboBox<>(new String[]{""});
        private final JComboBox<String> baccalaureate = new JComboBox<>(new String[]{""});
        private final JComboBox<String> faculty = new JComboBox<>(new String[]{""});
        private final JComboBox<String> schoolUnit = new JComboBox<>(new String[]{""});
        private final JComboBox<String> program = new JComboBox<>(new String[]{""});
        private final JComboBox<String> campus = new JComboBox<>(campuses());

        private ApplicantForm(ApplicantRepository repository, Applicant existingApplicant) {
            this.repository = repository;
            this.existingApplicant = existingApplicant;
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
            for (java.awt.Component input : List.of(identificationType, nationality, identity, sex,
                    names, surnames, birthDate, studentType, phoneType, phone, emergencyPhoneType,
                    emergencyPhone, emailType, email, secondaryEmailType, secondaryEmail, addressType,
                    province, district, corregimiento, neighborhood, collegeType, school, baccalaureate,
                    faculty, schoolUnit, program, campus)) {
                styleInput(input);
            }
            identificationType.addActionListener(_ -> updateNationalities());
            province.addActionListener(_ -> updateDistricts());
            district.addActionListener(_ -> updateCorregimientos());
            collegeType.addActionListener(_ -> {
                if (!updatingAcademicOptions) {
                    updateSchools();
                }
            });
            school.addActionListener(_ -> {
                if (!updatingAcademicOptions) {
                    updateBaccalaureates();
                }
            });
            campus.addActionListener(_ -> {
                if (!updatingAcademicOptions) {
                    updateFaculties();
                }
            });
            faculty.addActionListener(_ -> {
                if (!updatingAcademicOptions) {
                    updateSchoolUnits();
                }
            });
            schoolUnit.addActionListener(_ -> {
                if (!updatingAcademicOptions) {
                    updatePrograms();
                }
            });
            updateNationalities();
            addSection("Datos generales", 0);
            addGeneralInformationFields();
            addSection("Datos de contacto", 4);
            addContactSubsections();

            addSection("Datos académicos", 7);
            addFieldTriple(panel, 8, "Tipo de colegio *", collegeType,
                    "Colegio *", school, "Bachiller *", baccalaureate);
            addFieldTriple(panel, 9, "Sede donde estudiará *", campus,
                    "Facultad *", faculty, "Escuela *", schoolUnit);
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
            addFieldPair(panel, 10, "Carrera *", program,
                    "Admisión", admissionBox);
            for (JComboBox<String> field : List.of(collegeType, school, baccalaureate, campus,
                    faculty, schoolUnit, program)) {
                field.addActionListener(_ -> updateAdmissionNotice(admissionNotice));
            }
            GridBagConstraints fill = new GridBagConstraints();
            fill.gridx = 0;
            addSection("Documentos requeridos", 11);
            addRequiredDocuments();
            fill.gridy = 13;
            fill.gridwidth = 4;
            fill.weighty = 1;
            fill.fill = GridBagConstraints.VERTICAL;
            panel.add(new JPanel(), fill);
            if (existingApplicant != null) {
                loadApplicant(existingApplicant);
            }
        }

        private void addSection(String text, int row) {
            JLabel label = new JLabel(text);
            label.setForeground(new Color(28, 71, 110));
            label.setFont(new Font("SansSerif", Font.BOLD, 17));
            JPanel heading = sectionHeading(label);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 4;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(18, 4, 6, 4);
            panel.add(heading, constraints);
        }

        private void addGeneralInformationFields() {
            JPanel generalFields = new JPanel(new GridBagLayout());
            generalFields.setOpaque(false);
            addGeneralFieldTriple(generalFields, 0, "Tipo de identificación *", identificationType,
                    "Cédula *", identity, "Nacionalidad *", nationality);
            addGeneralFieldTriple(generalFields, 1, "Primer y segundo nombre *", names,
                    "Primer y segundo apellido *", surnames, "Fecha de nacimiento *", birthDate);

            JPanel generalDetails = new JPanel(new GridLayout(1, 3, 12, 0));
            generalDetails.setOpaque(false);
            addStackedField(generalDetails, "Género *", sex);
            addStackedField(generalDetails, "Foto tamaño carnet *", photoChooser());
            addStackedField(generalDetails, "Tipo de estudiante *", studentType);
            GridBagConstraints detailConstraints = new GridBagConstraints();
            detailConstraints.gridx = 0;
            detailConstraints.gridy = 2;
            detailConstraints.gridwidth = 3;
            detailConstraints.weightx = 1;
            detailConstraints.fill = GridBagConstraints.HORIZONTAL;
            detailConstraints.insets = new Insets(6, 4, 6, 4);
            generalFields.add(generalDetails, detailConstraints);

            photoPreview.setPreferredSize(new Dimension(125, 195));
            photoPreview.setMinimumSize(new Dimension(115, 160));
            photoPreview.setForeground(new Color(105, 119, 136));
            photoPreview.setFont(new Font("SansSerif", Font.PLAIN, 11));
            photoPreview.setBorder(BorderFactory.createLineBorder(BORDER));
            photoPreview.setOpaque(true);
            photoPreview.setBackground(Color.WHITE);
            GridBagConstraints photoConstraints = new GridBagConstraints();
            photoConstraints.gridx = 3;
            photoConstraints.gridy = 0;
            photoConstraints.gridheight = 3;
            photoConstraints.weightx = 0.4;
            photoConstraints.weighty = 1;
            photoConstraints.fill = GridBagConstraints.BOTH;
            photoConstraints.insets = new Insets(6, 8, 6, 4);
            generalFields.add(photoPreview, photoConstraints);

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 4;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(0, 0, 0, 0);
            panel.add(generalFields, constraints);
        }

        private void addGeneralFieldTriple(JPanel container, int row,
                                           String firstLabel, java.awt.Component firstField,
                                           String secondLabel, java.awt.Component secondField,
                                           String thirdLabel, java.awt.Component thirdField) {
            JPanel fields = new JPanel(new GridLayout(1, 3, 12, 0));
            fields.setOpaque(false);
            addStackedField(fields, firstLabel, firstField);
            addStackedField(fields, secondLabel, secondField);
            addStackedField(fields, thirdLabel, thirdField);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.gridwidth = 3;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(6, 4, 6, 4);
            container.add(fields, constraints);
        }

        private JPanel photoChooser() {
            photo.setEditable(false);
            JPanel chooser = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            chooser.setOpaque(false);
            JButton select = new JButton("Adjuntar foto");
            select.addActionListener(_ -> {
                chooseFile(photo, "Selecciona la foto tamaño carnet",
                        "jpg", "jpeg", "png", "gif", "pdf");
                photoStatus.setText(value(photo).isEmpty() ? "Ningún archivo subido" : "");
                updatePhotoPreview();
            });
            chooser.add(select);
            chooser.add(photoStatus);
            return chooser;
        }

        private JPanel sectionHeading(JLabel label) {
            JPanel heading = new JPanel(new BorderLayout(0, 3));
            heading.setOpaque(false);
            heading.add(label, BorderLayout.NORTH);
            JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
            divider.setForeground(new Color(25, 73, 126));
            divider.setBackground(new Color(25, 73, 126));
            heading.add(divider, BorderLayout.SOUTH);
            return heading;
        }

        private void addContactSubsections() {
            JPanel rows = new JPanel(new GridBagLayout());
            rows.setOpaque(false);
            addContactRow(rows, 0,
                    contactColumnHeading("Números telefónicos"),
                    contactColumnHeading("Correo electrónico"),
                    contactColumnHeading("Dirección"));
            addContactRow(rows, 1,
                    contactField("Tipo de contacto *", phoneType),
                    contactField("Tipo de correo *", emailType),
                    contactField("Tipo de dirección *", addressType));
            addContactRow(rows, 2,
                    contactField("Número de teléfono *", phone),
                    contactField("Correo electrónico *", email),
                    contactField("Provincia *", province));
            addContactRow(rows, 3,
                    contactField("Otro tipo de contacto *", emergencyPhoneType),
                    contactField("Otro tipo de correo *", secondaryEmailType),
                    contactField("Distrito *", district));
            addContactRow(rows, 4,
                    contactField("Número de emergencia *", emergencyPhone),
                    contactField("Correo alternativo *", secondaryEmail),
                    contactField("Corregimiento *", corregimiento));
            addContactRow(rows, 5, emptyContactCell(), emptyContactCell(),
                    contactField("Barrio *", neighborhood));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 5;
            constraints.gridwidth = 4;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(4, 4, 8, 4);
            panel.add(rows, constraints);
        }

        private void addContactRow(JPanel rows, int row, java.awt.Component first,
                                   java.awt.Component second, java.awt.Component third) {
            JPanel cells = new JPanel(new GridLayout(1, 3, 24, 0));
            cells.setOpaque(false);
            cells.add(first);
            cells.add(second);
            cells.add(third);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = row;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = row == 0
                    ? new Insets(0, 2, 3, 2) : new Insets(4, 2, 4, 2);
            rows.add(cells, constraints);
        }

        private JPanel contactColumnHeading(String title) {
            JLabel label = new JLabel(title);
            label.setForeground(new Color(28, 71, 110));
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            return sectionHeading(label);
        }

        private JPanel contactField(String label, java.awt.Component field) {
            JPanel container = new JPanel(new BorderLayout(0, 3));
            container.setOpaque(false);
            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setForeground(new Color(62, 76, 92));
            fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            container.add(fieldLabel, BorderLayout.NORTH);
            container.add(field, BorderLayout.CENTER);
            return container;
        }

        private JPanel emptyContactCell() {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            return empty;
        }

        private void updateAdmissionNotice(JLabel notice) {
            boolean academicInformationComplete = !selected(collegeType).isBlank()
                    && !selected(school).isBlank()
                    && !selected(baccalaureate).isBlank()
                    && !selected(campus).isBlank()
                    && !selected(faculty).isBlank()
                    && !selected(schoolUnit).isBlank()
                    && !selected(program).isBlank();
            notice.setText(academicInformationComplete
                    ? "La carrera admitirá: 40 personas después de haber realizado las pruebas."
                    : "");
        }

        private void styleInput(java.awt.Component input) {
            input.setFont(new Font("SansSerif", Font.PLAIN, 13));
            input.setForeground(new Color(39, 51, 65));
            input.setBackground(Color.WHITE);
            if (input instanceof JTextField textField) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            } else if (input instanceof JComboBox<?> combo) {
                combo.setBorder(BorderFactory.createLineBorder(BORDER));
            }
        }

        private void updatePhotoPreview() {
            photoPreview.setIcon(null);
            photoPreview.setText("");
            String filePath = value(photo);
            if (filePath.isEmpty()) {
                return;
            }
            Path path = Path.of(filePath);
            String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
            if (fileName.endsWith(".pdf")) {
                photoPreview.setText("<html><center>Vista previa<br>no disponible<br>para PDF</center></html>");
                return;
            }
            try {
                java.awt.image.BufferedImage image = ImageIO.read(path.toFile());
                if (image == null) {
                    photo.setText("");
                    photoStatus.setText("Ningún archivo subido");
                    JOptionPane.showMessageDialog(panel, "No se pudo leer la imagen seleccionada.",
                            "Imagen inválida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double scale = Math.min(113.0 / image.getWidth(), 183.0 / image.getHeight());
                int width = Math.max(1, (int) (image.getWidth() * scale));
                int height = Math.max(1, (int) (image.getHeight() * scale));
                photoPreview.setText("");
                photoPreview.setIcon(new ImageIcon(
                        image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH)));
            } catch (IOException ex) {
                showError(panel, ex.getMessage());
                photo.setText("");
                photoStatus.setText("Ningún archivo subido");
            }
        }

        private void addRequiredDocuments() {
            String transcriptLabel = "Boletín o créditos de décimo y undécimo * (PDF, máximo 1 MB)";
            String identityLabel = "Cédula o cédula juvenil * (JPG, PNG, GIF o PDF; máximo 1 MB)";
            addFieldPair(panel, 12, transcriptLabel,
                    documentChooser(transcript, transcriptStatus, transcriptLabel, "Adjuntar boletín", "pdf"),
                    identityLabel, documentChooser(identificationDocument, identificationStatus, identityLabel,
                            "Adjuntar cédula", "jpg", "jpeg", "png", "gif", "pdf"));
        }

        private JPanel documentChooser(JTextField target, JLabel status, String description,
                                       String buttonText, String... extensions) {
            target.setEditable(false);
            JPanel chooser = new JPanel(new BorderLayout(6, 0));
            JButton select = new JButton(buttonText);
            select.addActionListener(_ -> {
                chooseFile(target, description, extensions);
                status.setText(value(target).isEmpty() ? "Ningún archivo subido" : "");
            });
            chooser.add(select, BorderLayout.WEST);
            chooser.add(status, BorderLayout.CENTER);
            return chooser;
        }

        private void chooseFile(JTextField target, String description, String... extensions) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(description);
            chooser.setFileFilter(new FileNameExtensionFilter(
                    description + " (" + String.join(", ", extensions) + ")", extensions));
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                Path selectedFile = chooser.getSelectedFile().toPath();
                String fileName = selectedFile.getFileName().toString();
                int extensionSeparator = fileName.lastIndexOf('.');
                String extension = extensionSeparator < 0 ? ""
                        : fileName.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT);
                if (!Files.isRegularFile(selectedFile)
                        || !List.of(extensions).contains(extension)) {
                    JOptionPane.showMessageDialog(panel,
                            "Selecciona un archivo con formato: " + String.join(", ", extensions) + ".",
                            "Formato inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    if (Files.size(selectedFile) > 1024 * 1024) {
                        JOptionPane.showMessageDialog(panel, "El archivo no debe superar 1 MB.",
                                "Archivo demasiado grande", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (IOException ex) {
                    showError(panel, ex.getMessage());
                    return;
                }
                target.setText(selectedFile.toAbsolutePath().toString());
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

        private void loadApplicant(Applicant applicant) {
            identificationType.setSelectedItem(applicant.identificationType());
            nationality.setSelectedItem(applicant.nationality());
            identity.setText(applicant.identity());
            sex.setSelectedItem(applicant.sex());
            names.setText(joinNames(applicant.firstName(), applicant.secondName()));
            surnames.setText(joinNames(applicant.firstSurname(), applicant.secondSurname()));
            birthDate.setText(applicant.birthDate());
            studentType.setSelectedItem(applicant.studentType());
            photo.setText(applicant.photo());
            photoStatus.setText(existingFileStatus(applicant.photo()));
            updatePhotoPreview();

            phoneType.setSelectedItem(applicant.phoneType());
            phone.setText(applicant.phone());
            emergencyPhoneType.setSelectedItem(applicant.emergencyPhoneType());
            emergencyPhone.setText(applicant.emergencyPhone());
            emailType.setSelectedItem(applicant.emailType());
            email.setText(applicant.email());
            secondaryEmailType.setSelectedItem(applicant.secondaryEmailType());
            secondaryEmail.setText(applicant.secondaryEmail());
            addressType.setSelectedItem(applicant.addressType());
            province.setSelectedItem(applicant.province());
            updateDistricts();
            district.setSelectedItem(applicant.district());
            updateCorregimientos();
            corregimiento.setSelectedItem(applicant.corregimiento());
            neighborhood.setText(applicant.neighborhood());

            collegeType.setSelectedItem(applicant.collegeType());
            updateSchools();
            school.setSelectedItem(applicant.school());
            updateBaccalaureates();
            baccalaureate.setSelectedItem(applicant.baccalaureate());
            campus.setSelectedItem(applicant.campus());
            updateFaculties();
            faculty.setSelectedItem(applicant.faculty());
            updateSchoolUnits();
            schoolUnit.setSelectedItem(applicant.schoolUnit());
            updatePrograms();
            program.setSelectedItem(applicant.program());

            transcript.setText(applicant.transcript());
            transcriptStatus.setText(existingFileStatus(applicant.transcript()));
            identificationDocument.setText(applicant.identificationDocument());
            identificationStatus.setText(existingFileStatus(applicant.identificationDocument()));
        }

        private static String joinNames(String first, String second) {
            return second.isBlank() ? first : first + " " + second;
        }

        private static String existingFileStatus(String filePath) {
            if (filePath.isBlank()) {
                return "Ningún archivo subido";
            }
            Path path = Path.of(filePath);
            return "Archivo actual: " + path.getFileName();
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
            if (!hasValidAttachments()) {
                JOptionPane.showMessageDialog(panel,
                        "Verifica que la foto y los documentos tengan un formato permitido "
                                + "y no superen 1 MB.",
                        "Archivo inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (existingApplicant == null && repository.identityExists(value(identity))) {
                    JOptionPane.showMessageDialog(panel,
                            "Ya existe una solicitud registrada con esta cédula.",
                            "Solicitud duplicada", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (IOException ex) {
                showError(panel, ex.getMessage());
                return;
            }
            Applicant applicant = buildApplicant();
            try {
                if (existingApplicant == null) {
                    repository.insert(applicant);
                    JOptionPane.showMessageDialog(panel, "Solicitud enviada. Tu estado inicial es: Pendiente.",
                            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    repository.updateApplicant(applicant);
                    JOptionPane.showMessageDialog(panel, "Los datos y documentos se actualizaron correctamente.",
                            "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
                }
                clear();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(),
                        existingApplicant == null ? "Solicitud duplicada" : "Actualización no disponible",
                        JOptionPane.WARNING_MESSAGE);
            } catch (IOException ex) {
                showError(panel, ex.getMessage());
            }
        }

        private Applicant buildApplicant() {
            int id = existingApplicant == null ? 0 : existingApplicant.id();
            String status = existingApplicant == null
                    ? AdmissionProcessing.PENDING : existingApplicant.status();
            String applicationDate = existingApplicant == null
                    ? LocalDate.now().toString() : existingApplicant.date();
            return new Applicant(id, selected(identificationType), selected(nationality),
                    value(identity), selected(sex), value(names), "", value(surnames), "",
                    value(birthDate), value(photo), selected(studentType), value(email), value(phone),
                    selected(school), existingApplicant == null ? "" : existingApplicant.schoolLocation(),
                    existingApplicant == null ? "" : existingApplicant.credits(), selected(program),
                    selected(campus), status, applicationDate,
                    selected(phoneType), selected(emergencyPhoneType), value(emergencyPhone),
                    selected(emailType), selected(secondaryEmailType), value(secondaryEmail),
                    selected(addressType), selected(province), selected(district), selected(corregimiento),
                    value(neighborhood), selected(collegeType), selected(baccalaureate),
                    selected(faculty), selected(schoolUnit), value(transcript),
                    value(identificationDocument));
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
                    selected(secondaryEmailType), value(secondaryEmail), selected(addressType), selected(province),
                    selected(district), selected(corregimiento), value(neighborhood), selected(collegeType), selected(school),
                    selected(baccalaureate), selected(campus), selected(faculty), selected(schoolUnit),
                    selected(program), value(transcript), value(identificationDocument));
        }

        private boolean hasValidAttachments() {
            return isValidAttachment(value(photo), "jpg", "jpeg", "png", "gif", "pdf")
                    && isValidAttachment(value(transcript), "pdf")
                    && isValidAttachment(value(identificationDocument),
                    "jpg", "jpeg", "png", "gif", "pdf");
        }

        private boolean isValidAttachment(String filePath, String... extensions) {
            try {
                Path path = Path.of(filePath);
                String fileName = path.getFileName().toString();
                int extensionSeparator = fileName.lastIndexOf('.');
                String extension = extensionSeparator < 0 ? ""
                        : fileName.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT);
                return Files.isRegularFile(path) && List.of(extensions).contains(extension)
                        && Files.size(path) <= 1024 * 1024;
            } catch (IOException | InvalidPathException ex) {
                return false;
            }
        }

        private void updateDistricts() {
            setOptions(district, PanamaLocations.districts(selected(province)));
            updateCorregimientos();
        }

        private void updateCorregimientos() {
            setOptions(corregimiento,
                    PanamaLocations.corregimientos(selected(province), selected(district)));
        }

        private void updateSchools() {
            setOptions(school, schools(selected(collegeType)));
            updateBaccalaureates();
        }

        private void updateBaccalaureates() {
            setOptions(baccalaureate, baccalaureates(selected(school)));
        }

        private void updateFaculties() {
            setOptions(faculty, faculties(selected(campus)));
            updateSchoolUnits();
        }

        private void updateSchoolUnits() {
            setOptions(schoolUnit, schoolUnits(selected(faculty)));
            updatePrograms();
        }

        private void updatePrograms() {
            setOptions(program, programs(selected(schoolUnit)));
        }

        private void setOptions(JComboBox<String> combo, String[] options) {
            updatingAcademicOptions = true;
            try {
                combo.removeAllItems();
                for (String option : options) {
                    combo.addItem(option);
                }
                if (combo.getItemCount() > 0) {
                    combo.setSelectedIndex(0);
                }
            } finally {
                updatingAcademicOptions = false;
            }
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
                    email, secondaryEmail, neighborhood, transcript, identificationDocument)) {
                field.setText("");
            }
            photoStatus.setText("Ningún archivo subido");
            photoPreview.setIcon(null);
            photoPreview.setText("");
            transcriptStatus.setText("Ningún archivo subido");
            identificationStatus.setText("Ningún archivo subido");
            birthDate.reset();
            for (JComboBox<String> combo : List.of(identificationType, nationality, sex, studentType,
                    phoneType, emergencyPhoneType, emailType, secondaryEmailType, addressType, province,
                    district, corregimiento, school,
                    collegeType, baccalaureate, faculty, schoolUnit, program, campus)) {
                combo.setSelectedIndex(0);
            }
        }

        private JPanel panel() {
            return panel;
        }

        private static String[] campuses() {
            return new String[]{"", "Bocas del Toro", "Chiriquí", "Veraguas", "Panamá",
                    "Panamá Oeste", "Colón", "Coclé", "Herrera", "Los Santos", "Darién"};
        }

        private static String[] schools(String collegeType) {
            return switch (collegeType) {
                case "Público" -> new String[]{
                        "", "Instituto Nacional", "Instituto José Dolores Moscote",
                        "Instituto Fermín Naudeau", "Instituto América", "Instituto Comercial Panamá",
                        "Instituto Urracá", "Colegio Abel Bravo", "Instituto David",
                        "Colegio José Remón Cantera", "Instituto Profesional y Técnico de Veraguas",
                        "Instituto Profesional y Técnico de Azuero", "Instituto Profesional y Técnico de Chiriquí",
                        "Instituto Profesional y Técnico de La Chorrera", "Instituto Profesional y Técnico de Capira",
                        "Instituto Profesional y Técnico de Colón", "Instituto Profesional y Técnico de Coclé",
                        "Instituto Profesional y Técnico de Bocas del Toro",
                        "IPT Arnulfo Arias Madrid", "IPT Don Bosco", "IPT Fernando de Lesseps",
                        "IPT Jeptha B. Duncan", "IPT Louis Martinz", "IPT San Miguelito",
                        "IPT México", "IPT El Silencio", "IPT Chiriquí Grande",
                        "IPT La Pintada", "IPT Río Hato", "IPT Las Minas",
                        "IPT Omar Torrijos Herrera", "IPT Agropecuario de Chiriquí",
                        "IPT de Barú", "IPT de Tonosí", "IPT de Chepo",
                        "Colegio Artes y Oficios Melchor Lasso de la Vega",
                        "Colegio Richard Neumann", "Colegio Francisco Morazán",
                        "Colegio Elena Chávez de Pinate", "Colegio Rafael Quintero Villarreal",
                        "Colegio Félix Olivares Contreras", "Colegio José Daniel Crespo",
                        "Colegio Manuel María Tejada Roca", "Colegio José Guardia Vega",
                        "Colegio Rodolfo Chiari", "Colegio Salomón Ponce Aguilera",
                        "Colegio José Bonifacio Alvarado", "Colegio Secundario de Volcán",
                        "Colegio Secundario de Alanje", "Colegio Secundario de Guararé",
                        "Colegio Secundario de Las Lajas", "Colegio Secundario de Parita",
                        "Colegio Secundario de Atalaya", "Colegio Secundario de Chame",
                        "Colegio Secundario de Antón", "Colegio Secundario de La Arena",
                        "Centro Educativo de Tortí", "Centro Educativo de Kuna Nega"
                };
                case "Privado" -> new String[]{
                        "", "Colegio La Salle", "Colegio Javier", "Instituto Panamericano",
                        "Colegio San Agustín", "Colegio Brader", "Oxford International School",
                        "Colegio Bilingüe de Panamá", "Colegio Episcopal de Panamá",
                        "Colegio Isaac Rabin", "Colegio María Inmaculada",
                        "The Oxford School", "Balboa Academy", "International School of Panama",
                        "Metropolitan School of Panama", "King's College Panama",
                        "Boston School International", "Colegio Bilingüe Las Naciones",
                        "Colegio Bilingüe de Cerro Viento", "Colegio Bilingüe Panamá",
                        "Colegio Bilingüe Saint George", "Colegio Bilingüe Saint Mary",
                        "Colegio San Vicente de Paúl", "Colegio Nuestra Señora de Lourdes",
                        "Colegio Nuestra Señora del Carmen", "Colegio Real de Panamá",
                        "Colegio del Istmo", "Colegio Cambridge", "Colegio Alemán del Istmo",
                        "Colegio Italiano Enrico Fermi", "Colegio Francés Paul Gauguin",
                        "Colegio Chino Panameño", "Instituto Justo Arosemena",
                        "Instituto Técnico Don Bosco", "Instituto Episcopal San Cristóbal",
                        "Instituto Alberto Einstein", "Instituto Sun Yat-Sen",
                        "Instituto William H. Kilpatrick", "Instituto Panamericano de Educación",
                        "Academia Hebrea de Panamá", "Academia Interamericana de Panamá",
                        "Academia Bilingüe Panamá para el Futuro", "Academia Integral San Lucas",
                        "Centro Educativo Bellas Luces", "Centro Educativo de la Salle",
                        "Centro Educativo Nuestra Señora de la Merced",
                        "Escuela Internacional de María Inmaculada"
                };
                case "Extranjero" -> new String[]{
                        "", "Colegio extranjero 1", "Colegio extranjero 2", "Colegio extranjero 3",
                        "Colegio extranjero 4", "Colegio extranjero 5", "Colegio extranjero 6",
                        "Colegio extranjero 7", "Colegio extranjero 8", "Colegio extranjero 9",
                        "Colegio extranjero 10"
                };
                default -> new String[]{""};
            };
        }

        private static String[] baccalaureates(String school) {
            return switch (school) {
                case "Instituto Nacional", "Colegio La Salle", "Colegio Javier",
                        "Colegio Richard Neumann", "Colegio Francisco Morazán",
                        "Colegio Elena Chávez de Pinate", "Colegio Rafael Quintero Villarreal",
                        "Colegio Félix Olivares Contreras", "Colegio José Daniel Crespo",
                        "Colegio Manuel María Tejada Roca", "Colegio José Guardia Vega",
                        "Colegio Rodolfo Chiari", "Colegio Salomón Ponce Aguilera",
                        "Colegio José Bonifacio Alvarado", "Colegio Secundario de Volcán",
                        "Colegio Secundario de Alanje", "Colegio Secundario de Guararé",
                        "Colegio Secundario de Las Lajas", "Colegio Secundario de Parita",
                        "Colegio Secundario de Atalaya", "Colegio Secundario de Chame",
                        "Colegio Secundario de Antón", "Colegio Secundario de La Arena",
                        "Centro Educativo de Tortí", "Centro Educativo de Kuna Nega",
                        "The Oxford School", "Balboa Academy", "International School of Panama",
                        "Metropolitan School of Panama", "King's College Panama",
                        "Boston School International", "Colegio Bilingüe Las Naciones",
                        "Colegio Bilingüe de Cerro Viento", "Colegio Bilingüe Panamá",
                        "Colegio Bilingüe Saint George", "Colegio Bilingüe Saint Mary",
                        "Colegio San Vicente de Paúl", "Colegio Nuestra Señora de Lourdes",
                        "Colegio Nuestra Señora del Carmen", "Colegio Real de Panamá",
                        "Colegio del Istmo", "Colegio Cambridge" ->
                        new String[]{"", "Ciencias", "Letras", "Comercio", "Informática"};
                case "Instituto José Dolores Moscote", "Colegio Brader" ->
                        new String[]{"", "Ciencias", "Comercio", "Informática"};
                case "Instituto Fermín Naudeau", "Instituto David", "Colegio Isaac Rabin" ->
                        new String[]{"", "Ciencias", "Comercio", "Informática", "Humanidades"};
                case "Instituto América", "Instituto Panamericano", "Colegio Episcopal de Panamá",
                        "Colegio Alemán del Istmo", "Colegio Italiano Enrico Fermi",
                        "Colegio Francés Paul Gauguin", "Colegio Chino Panameño",
                        "Instituto Justo Arosemena", "Instituto Técnico Don Bosco",
                        "Instituto Episcopal San Cristóbal", "Instituto Alberto Einstein",
                        "Instituto Sun Yat-Sen", "Instituto William H. Kilpatrick",
                        "Instituto Panamericano de Educación", "Academia Hebrea de Panamá",
                        "Academia Interamericana de Panamá", "Academia Bilingüe Panamá para el Futuro",
                        "Academia Integral San Lucas", "Centro Educativo Bellas Luces",
                        "Centro Educativo de la Salle", "Centro Educativo Nuestra Señora de la Merced",
                        "Escuela Internacional de María Inmaculada" ->
                        new String[]{"", "Ciencias", "Letras", "Comercio"};
                case "Instituto Comercial Panamá", "Colegio San Agustín",
                        "Colegio Bilingüe de Panamá", "Colegio María Inmaculada" ->
                        new String[]{"", "Ciencias", "Comercio", "Humanidades"};
                case "Instituto Urracá", "Instituto Profesional y Técnico de Veraguas" ->
                        new String[]{"", "Ciencias", "Agropecuario", "Comercio", "Informática"};
                case "IPT El Silencio" ->
                        new String[]{"", "Agropecuaria", "Comercio", "Ciencias"};
                case "IPT Chiriquí Grande", "IPT de Barú", "IPT Agropecuario de Chiriquí" ->
                        new String[]{"", "Agropecuario", "Comercio", "Ciencias"};
                case "IPT La Pintada", "IPT Río Hato", "IPT Las Minas",
                        "IPT de Tonosí", "IPT de Chepo" ->
                        new String[]{"", "Agropecuario", "Comercio", "Ciencias", "Turismo"};
                case "IPT Arnulfo Arias Madrid", "IPT Don Bosco", "IPT Fernando de Lesseps",
                        "IPT Jeptha B. Duncan", "IPT Louis Martinz", "IPT San Miguelito",
                        "IPT México", "Instituto Profesional y Técnico de Azuero",
                        "Instituto Profesional y Técnico de Chiriquí",
                        "Instituto Profesional y Técnico de La Chorrera",
                        "Instituto Profesional y Técnico de Capira",
                        "Instituto Profesional y Técnico de Colón",
                        "Instituto Profesional y Técnico de Coclé",
                        "Instituto Profesional y Técnico de Bocas del Toro" ->
                        new String[]{"", "Industrial", "Comercio", "Informática", "Ciencias"};
                case "IPT Omar Torrijos Herrera" ->
                        new String[]{"", "Agropecuario", "Industrial", "Comercio", "Ciencias"};
                case "Colegio Artes y Oficios Melchor Lasso de la Vega" ->
                        new String[]{"", "Industrial", "Comercio"};
                case "Colegio Abel Bravo" ->
                        new String[]{"", "Ciencias", "Comercio", "Humanidades", "Marítimo"};
                case "Colegio José Remón Cantera", "Oxford International School" ->
                        new String[]{"", "Ciencias", "Letras", "Comercio", "Humanidades"};
                case "Colegio extranjero 1", "Colegio extranjero 2", "Colegio extranjero 3",
                        "Colegio extranjero 4", "Colegio extranjero 5", "Colegio extranjero 6",
                        "Colegio extranjero 7", "Colegio extranjero 8", "Colegio extranjero 9",
                        "Colegio extranjero 10" ->
                        new String[]{"", "Ciencias", "Letras", "Comercio", "Informática",
                                "Humanidades", "Otro"};
                default -> new String[]{""};
            };
        }

        private static String[] faculties(String campus) {
            return switch (campus) {
                case "Panamá" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Arquitectura y Diseño",
                        "Ciencias Agropecuarias", "Ciencias de la Educación",
                        "Ciencias Naturales, Exactas y Tecnología", "Comunicación Social",
                        "Derecho y Ciencias Políticas", "Economía", "Enfermería", "Farmacia",
                        "Humanidades", "Informática, Electrónica y Comunicación", "Ingeniería",
                        "Medicina", "Odontología", "Psicología"
                };
                case "Bocas del Toro" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Ciencias Agropecuarias",
                        "Ciencias de la Educación", "Enfermería", "Humanidades",
                        "Informática, Electrónica y Comunicación"
                };
                case "Chiriquí" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Ciencias Agropecuarias",
                        "Ciencias de la Educación", "Ciencias Naturales, Exactas y Tecnología",
                        "Derecho y Ciencias Políticas", "Economía", "Enfermería", "Humanidades",
                        "Informática, Electrónica y Comunicación", "Medicina", "Psicología"
                };
                case "Veraguas" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Ciencias Agropecuarias",
                        "Ciencias de la Educación", "Ciencias Naturales, Exactas y Tecnología",
                        "Derecho y Ciencias Políticas", "Economía", "Enfermería", "Humanidades",
                        "Informática, Electrónica y Comunicación", "Ingeniería", "Psicología"
                };
                case "Panamá Oeste" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Ciencias de la Educación",
                        "Derecho y Ciencias Políticas", "Economía", "Enfermería", "Humanidades",
                        "Informática, Electrónica y Comunicación", "Ingeniería", "Psicología"
                };
                case "Colón" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Ciencias de la Educación",
                        "Ciencias Naturales, Exactas y Tecnología", "Comunicación Social",
                        "Derecho y Ciencias Políticas", "Economía", "Enfermería", "Humanidades",
                        "Informática, Electrónica y Comunicación", "Ingeniería", "Psicología"
                };
                case "Coclé", "Herrera", "Los Santos", "Darién" -> new String[]{
                        "", "Administración de Empresas y Contabilidad", "Ciencias Agropecuarias",
                        "Ciencias de la Educación", "Ciencias Naturales, Exactas y Tecnología",
                        "Economía", "Enfermería", "Humanidades",
                        "Informática, Electrónica y Comunicación", "Psicología"
                };
                default -> new String[]{""};
            };
        }

        private static String[] schoolUnits(String faculty) {
            return switch (faculty) {
                case "Administración de Empresas y Contabilidad" -> new String[]{
                        "", "Escuela de Administración de Empresas",
                        "Escuela de Contabilidad"
                };
                case "Arquitectura y Diseño" -> new String[]{
                        "", "Escuela de Arquitectura", "Escuela de Diseño Gráfico"
                };
                case "Ciencias Agropecuarias" -> new String[]{
                        "", "Escuela de Ciencias Agropecuarias", "Escuela de Agronomía",
                        "Escuela de Medicina Veterinaria"
                };
                case "Ciencias de la Educación" -> new String[]{
                        "", "Escuela de Formación Pedagógica", "Escuela de Educación",
                        "Escuela de Educación Física"
                };
                case "Ciencias Naturales, Exactas y Tecnología" -> new String[]{
                        "", "Escuela de Biología", "Escuela de Física", "Escuela de Química",
                        "Escuela de Matemática", "Escuela de Estadística"
                };
                case "Comunicación Social" -> new String[]{
                        "", "Escuela de Periodismo", "Escuela de Relaciones Públicas",
                        "Escuela de Producción Audiovisual"
                };
                case "Derecho y Ciencias Políticas" -> new String[]{
                        "", "Escuela de Derecho", "Escuela de Ciencias Políticas"
                };
                case "Economía" -> new String[]{
                        "", "Escuela de Economía", "Escuela de Finanzas y Banca"
                };
                case "Enfermería" -> new String[]{"", "Escuela de Enfermería"};
                case "Farmacia" -> new String[]{
                        "", "Escuela de Farmacia", "Escuela de Tecnología Médica"
                };
                case "Humanidades" -> new String[]{
                        "", "Escuela de Filosofía", "Escuela de Historia",
                        "Escuela de Geografía", "Escuela de Español",
                        "Escuela de Inglés", "Escuela de Sociología"
                };
                case "Informática, Electrónica y Comunicación" -> new String[]{
                        "", "Escuela de Informática", "Escuela de Electrónica",
                        "Escuela de Comunicación"
                };
                case "Ingeniería" -> new String[]{
                        "", "Escuela de Ingeniería Civil", "Escuela de Ingeniería Industrial",
                        "Escuela de Ingeniería Mecánica", "Escuela de Ingeniería Eléctrica",
                        "Escuela de Ingeniería de Sistemas y Computación"
                };
                case "Medicina" -> new String[]{"", "Escuela de Medicina"};
                case "Odontología" -> new String[]{"", "Escuela de Odontología"};
                case "Psicología" -> new String[]{"", "Escuela de Psicología"};
                default -> new String[]{""};
            };
        }

        private static String[] programs(String school) {
            return switch (school) {
                case "Escuela de Administración de Empresas" -> new String[]{
                        "", "Licenciatura en Administración de Empresas",
                        "Licenciatura en Recursos Humanos", "Licenciatura en Administración Pública"
                };
                case "Escuela de Contabilidad" -> new String[]{
                        "", "Licenciatura en Contabilidad", "Licenciatura en Auditoría"
                };
                case "Escuela de Arquitectura" -> new String[]{"", "Licenciatura en Arquitectura"};
                case "Escuela de Diseño Gráfico" -> new String[]{"", "Licenciatura en Diseño Gráfico"};
                case "Escuela de Ciencias Agropecuarias", "Escuela de Agronomía" -> new String[]{
                        "", "Licenciatura en Ingeniería Agronómica",
                        "Licenciatura en Desarrollo Agropecuario"
                };
                case "Escuela de Medicina Veterinaria" -> new String[]{
                        "", "Licenciatura en Medicina Veterinaria"
                };
                case "Escuela de Formación Pedagógica", "Escuela de Educación" -> new String[]{
                        "", "Licenciatura en Educación Primaria",
                        "Licenciatura en Educación Preescolar",
                        "Licenciatura en Educación con especialización"
                };
                case "Escuela de Educación Física" -> new String[]{
                        "", "Licenciatura en Educación Física"
                };
                case "Escuela de Biología" -> new String[]{
                        "", "Licenciatura en Biología", "Licenciatura en Biología Ambiental"
                };
                case "Escuela de Física" -> new String[]{"", "Licenciatura en Física"};
                case "Escuela de Química" -> new String[]{
                        "", "Licenciatura en Química", "Licenciatura en Química Industrial"
                };
                case "Escuela de Matemática" -> new String[]{"", "Licenciatura en Matemática"};
                case "Escuela de Estadística" -> new String[]{"", "Licenciatura en Estadística"};
                case "Escuela de Periodismo" -> new String[]{
                        "", "Licenciatura en Periodismo"
                };
                case "Escuela de Relaciones Públicas" -> new String[]{
                        "", "Licenciatura en Relaciones Públicas"
                };
                case "Escuela de Producción Audiovisual" -> new String[]{
                        "", "Licenciatura en Producción Audiovisual"
                };
                case "Escuela de Derecho" -> new String[]{"", "Licenciatura en Derecho"};
                case "Escuela de Ciencias Políticas" -> new String[]{
                        "", "Licenciatura en Ciencias Políticas"
                };
                case "Escuela de Economía" -> new String[]{"", "Licenciatura en Economía"};
                case "Escuela de Finanzas y Banca" -> new String[]{
                        "", "Licenciatura en Finanzas y Banca"
                };
                case "Escuela de Enfermería" -> new String[]{
                        "", "Licenciatura en Ciencias de la Enfermería"
                };
                case "Escuela de Farmacia" -> new String[]{"", "Licenciatura en Farmacia"};
                case "Escuela de Tecnología Médica" -> new String[]{
                        "", "Licenciatura en Tecnología Médica"
                };
                case "Escuela de Filosofía" -> new String[]{"", "Licenciatura en Filosofía"};
                case "Escuela de Historia" -> new String[]{"", "Licenciatura en Historia"};
                case "Escuela de Geografía" -> new String[]{"", "Licenciatura en Geografía"};
                case "Escuela de Español" -> new String[]{"", "Licenciatura en Español"};
                case "Escuela de Inglés" -> new String[]{"", "Licenciatura en Inglés"};
                case "Escuela de Sociología" -> new String[]{"", "Licenciatura en Sociología"};
                case "Escuela de Informática", "Escuela de Ingeniería de Sistemas y Computación" ->
                        new String[]{"", "Licenciatura en Informática",
                                "Licenciatura en Ingeniería de Sistemas y Computación"};
                case "Escuela de Electrónica" -> new String[]{
                        "", "Licenciatura en Ingeniería Electrónica"
                };
                case "Escuela de Comunicación" -> new String[]{
                        "", "Licenciatura en Comunicación"
                };
                case "Escuela de Ingeniería Civil" -> new String[]{
                        "", "Licenciatura en Ingeniería Civil"
                };
                case "Escuela de Ingeniería Industrial" -> new String[]{
                        "", "Licenciatura en Ingeniería Industrial"
                };
                case "Escuela de Ingeniería Mecánica" -> new String[]{
                        "", "Licenciatura en Ingeniería Mecánica"
                };
                case "Escuela de Ingeniería Eléctrica" -> new String[]{
                        "", "Licenciatura en Ingeniería Eléctrica"
                };
                case "Escuela de Medicina" -> new String[]{"", "Doctor en Medicina"};
                case "Escuela de Odontología" -> new String[]{
                        "", "Doctor en Cirugía Dental"
                };
                case "Escuela de Psicología" -> new String[]{
                        "", "Licenciatura en Psicología"
                };
                default -> new String[]{""};
            };
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
        private final JLabel pendingCount = new JLabel("0", SwingConstants.CENTER);
        private final JLabel inProgressCount = new JLabel("0", SwingConstants.CENTER);
        private final JLabel admittedCount = new JLabel("0", SwingConstants.CENTER);
        private final JLabel rejectedCount = new JLabel("0", SwingConstants.CENTER);
        private int selectedId = -1;

        private AdminPanel(ApplicantRepository repository) {
            this.repository = repository;
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(32);
            table.setFillsViewportHeight(true);
            table.setFont(new Font("SansSerif", Font.PLAIN, 13));
            table.setForeground(new Color(43, 55, 69));
            table.setSelectionBackground(new Color(218, 234, 246));
            table.setSelectionForeground(new Color(28, 55, 80));
            table.setShowVerticalLines(false);
            table.setGridColor(new Color(235, 239, 244));
            JTableHeader tableHeader = table.getTableHeader();
            tableHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
            tableHeader.setForeground(new Color(50, 69, 89));
            tableHeader.setBackground(new Color(235, 241, 247));
            tableHeader.setPreferredSize(new Dimension(tableHeader.getPreferredSize().width, 36));
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                    selectedId = (int) table.getValueAt(table.getSelectedRow(), 0);
                }
            });
            refresh();
        }

        private JPanel panel() {
            JPanel root = new JPanel(new BorderLayout(10, 10));
            root.setBackground(SURFACE);
            root.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));
            JTextField search = new JTextField();
            search.setFont(new Font("SansSerif", Font.PLAIN, 14));
            search.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            JComboBox<String> statusFilter = new JComboBox<>(new String[]{
                    "Todos los estados", AdmissionProcessing.PENDING, AdmissionProcessing.IN_PROGRESS,
                    AdmissionProcessing.ADMITTED, AdmissionProcessing.REJECTED
            });
            JButton find = buttonStatic("Buscar", PRIMARY);
            JButton refreshButton = buttonStatic("Actualizar lista", new Color(105, 119, 136));
            JButton details = buttonStatic("Ver expediente", new Color(85, 105, 127));
            JButton status = buttonStatic("Procesar solicitud", new Color(48, 108, 119));
            JButton delete = buttonStatic("Eliminar solicitud", RED);
            find.addActionListener(_ -> refresh(search.getText(), selectedStatus(statusFilter)));
            search.addActionListener(_ -> refresh(search.getText(), selectedStatus(statusFilter)));
            statusFilter.addActionListener(_ -> refresh(search.getText(), selectedStatus(statusFilter)));
            refreshButton.addActionListener(_ -> refresh(search.getText(), selectedStatus(statusFilter)));
            details.addActionListener(_ -> showSelectedApplicant());
            status.addActionListener(_ -> updateStatus());
            delete.addActionListener(_ -> deleteSelectedApplicant());
            details.setEnabled(false);
            status.setEnabled(false);
            delete.setEnabled(false);
            table.getSelectionModel().addListSelectionListener(event -> {
                boolean selected = !event.getValueIsAdjusting() && table.getSelectedRow() >= 0;
                details.setEnabled(selected);
                status.setEnabled(selected);
                delete.setEnabled(selected);
            });
            JPanel toolbar = new JPanel(new BorderLayout(8, 0));
            toolbar.setOpaque(false);
            JPanel searchControls = new JPanel(new BorderLayout(8, 0));
            searchControls.setOpaque(false);
            searchControls.add(search, BorderLayout.CENTER);
            searchControls.add(statusFilter, BorderLayout.EAST);
            toolbar.add(searchControls, BorderLayout.CENTER);
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            actions.setOpaque(false);
            actions.add(find);
            actions.add(refreshButton);
            actions.add(details);
            actions.add(status);
            actions.add(delete);
            toolbar.add(actions, BorderLayout.EAST);
            JPanel header = new JPanel(new BorderLayout(0, 10));
            header.setOpaque(false);
            JLabel workflow = new JLabel("Flujo de revisión: Pendiente  →  En proceso  →  Admitido / Rechazado");
            workflow.setForeground(new Color(83, 101, 119));
            workflow.setFont(new Font("SansSerif", Font.PLAIN, 12));
            header.add(statusSummary(), BorderLayout.NORTH);
            JPanel controls = new JPanel(new BorderLayout(0, 8));
            controls.setOpaque(false);
            controls.add(toolbar, BorderLayout.CENTER);
            controls.add(workflow, BorderLayout.SOUTH);
            header.add(controls, BorderLayout.SOUTH);
            root.add(header, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
            root.add(scrollPane, BorderLayout.CENTER);
            return root;
        }

        private JPanel statusSummary() {
            JPanel summary = new JPanel(new GridLayout(1, 4, 10, 0));
            summary.setOpaque(false);
            summary.add(statusCard("Pendientes", pendingCount, new Color(105, 119, 136)));
            summary.add(statusCard("En proceso", inProgressCount, new Color(48, 108, 119)));
            summary.add(statusCard("Admitidas", admittedCount, GREEN));
            summary.add(statusCard("Rechazadas", rejectedCount, RED));
            return summary;
        }

        private JPanel statusCard(String title, JLabel count, Color accent) {
            JPanel card = new JPanel(new BorderLayout(8, 0));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER),
                            BorderFactory.createEmptyBorder(8, 10, 8, 10))));
            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(new Color(83, 101, 119));
            titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            count.setForeground(accent);
            count.setFont(new Font("SansSerif", Font.BOLD, 20));
            card.add(titleLabel, BorderLayout.CENTER);
            card.add(count, BorderLayout.EAST);
            return card;
        }

        private void refresh() {
            refresh("", "Todos los estados");
        }

        private static String selectedStatus(JComboBox<String> statusFilter) {
            Object selected = statusFilter.getSelectedItem();
            return selected == null ? "Todos los estados" : selected.toString();
        }

        private void refresh(String query, String statusFilter) {
            try {
                selectedId = -1;
                table.clearSelection();
                model.setRowCount(0);
                List<Applicant> applicants = repository.find("");
                int pending = 0;
                int inProgress = 0;
                int admitted = 0;
                int rejected = 0;
                for (Applicant applicant : applicants) {
                    switch (applicant.status()) {
                        case AdmissionProcessing.PENDING -> pending++;
                        case AdmissionProcessing.IN_PROGRESS -> inProgress++;
                        case AdmissionProcessing.ADMITTED -> admitted++;
                        case AdmissionProcessing.REJECTED -> rejected++;
                        default -> {
                        }
                    }
                    if (!matchesQuery(applicant, query)
                            || (!"Todos los estados".equals(statusFilter)
                            && !statusFilter.equals(applicant.status()))) {
                        continue;
                    }
                    model.addRow(new Object[]{applicant.id(), applicant.identity(),
                            applicant.firstName() + " " + applicant.firstSurname(), applicant.school(),
                            applicant.program(), applicant.campus(), applicant.status(), applicant.email(),
                            applicant.phone()});
                }
                pendingCount.setText(Integer.toString(pending));
                inProgressCount.setText(Integer.toString(inProgress));
                admittedCount.setText(Integer.toString(admitted));
                rejectedCount.setText(Integer.toString(rejected));
            } catch (IOException ex) {
                showError(table, ex.getMessage());
            }
        }

        private static boolean matchesQuery(Applicant applicant, String query) {
            if (query == null || query.isBlank()) {
                return true;
            }
            String searchable = (applicant.identity() + " " + applicant.firstName() + " "
                    + applicant.firstSurname() + " " + applicant.school() + " "
                    + applicant.program() + " " + applicant.campus() + " "
                    + applicant.email()).toLowerCase(Locale.ROOT);
            return searchable.contains(query.trim().toLowerCase(Locale.ROOT));
        }

        private void deleteSelectedApplicant() {
            if (selectedId < 0) {
                JOptionPane.showMessageDialog(table, "Selecciona una solicitud para eliminarla.",
                        "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Applicant applicant = repository.findById(selectedId).orElse(null);
                if (applicant == null) {
                    JOptionPane.showMessageDialog(table, "La solicitud ya no está disponible.",
                            "Solicitud no encontrada", JOptionPane.WARNING_MESSAGE);
                    refresh();
                    return;
                }
                if (!AdmissionProcessing.PENDING.equals(applicant.status())) {
                    JOptionPane.showMessageDialog(table,
                            "Solo se pueden eliminar solicitudes que todavía estén pendientes.",
                            "Eliminación no permitida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String applicantName = applicant.firstName() + " " + applicant.firstSurname();
                int confirmation = JOptionPane.showConfirmDialog(table,
                        "¿Eliminar la solicitud pendiente de " + applicantName + " (cédula "
                                + applicant.identity() + ")?\nEsta acción no se puede deshacer.",
                        "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmation == JOptionPane.YES_OPTION) {
                    repository.deletePending(selectedId);
                    refresh();
                    JOptionPane.showMessageDialog(table, "La solicitud fue eliminada.",
                            "Solicitud eliminada", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException | IllegalStateException ex) {
                showError(table, ex.getMessage());
            }
        }

        private void showSelectedApplicant() {
            if (selectedId < 0) {
                JOptionPane.showMessageDialog(table, "Selecciona una solicitud para ver su expediente.",
                        "Sin selección", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Applicant applicant = repository.findById(selectedId).orElse(null);
                if (applicant == null) {
                    JOptionPane.showMessageDialog(table, "La solicitud ya no está disponible.",
                            "Solicitud no encontrada", JOptionPane.WARNING_MESSAGE);
                    refresh();
                    return;
                }
                JTextArea details = new JTextArea(applicantDetails(applicant));
                details.setEditable(false);
                details.setCaretPosition(0);
                details.setFont(new Font("SansSerif", Font.PLAIN, 13));
                details.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
                JScrollPane scrollPane = new JScrollPane(details);
                scrollPane.setPreferredSize(new Dimension(640, 560));
                JOptionPane.showMessageDialog(table, scrollPane,
                        "Expediente de " + applicant.firstName() + " " + applicant.firstSurname(),
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError(table, ex.getMessage());
            }
        }

        private static String applicantDetails(Applicant a) {
            return "DATOS GENERALES\n"
                    + "ID de solicitud: " + a.id() + "\n"
                    + "Tipo de identificación: " + a.identificationType() + "\n"
                    + "Cédula: " + a.identity() + "\n"
                    + "Nacionalidad: " + a.nationality() + "\n"
                    + "Género: " + a.sex() + "\n"
                    + "Nombre: " + a.firstName() + " " + a.secondName() + "\n"
                    + "Apellido: " + a.firstSurname() + " " + a.secondSurname() + "\n"
                    + "Fecha de nacimiento: " + a.birthDate() + "\n"
                    + "Tipo de estudiante: " + a.studentType() + "\n"
                    + "Estado: " + a.status() + "\n"
                    + "Fecha de solicitud: " + a.date() + "\n\n"
                    + "DATOS DE CONTACTO\n"
                    + "Teléfono (" + a.phoneType() + "): " + a.phone() + "\n"
                    + "Teléfono de emergencia (" + a.emergencyPhoneType() + "): "
                    + a.emergencyPhone() + "\n"
                    + "Correo (" + a.emailType() + "): " + a.email() + "\n"
                    + "Correo alternativo (" + a.secondaryEmailType() + "): "
                    + a.secondaryEmail() + "\n"
                    + "Dirección (" + a.addressType() + "): " + a.neighborhood() + ", "
                    + a.corregimiento() + ", " + a.district() + ", " + a.province() + "\n\n"
                    + "DATOS ACADÉMICOS\n"
                    + "Tipo de colegio: " + a.collegeType() + "\n"
                    + "Colegio: " + a.school() + "\n"
                    + "Bachiller: " + a.baccalaureate() + "\n"
                    + "Sede: " + a.campus() + "\n"
                    + "Facultad: " + a.faculty() + "\n"
                    + "Escuela: " + a.schoolUnit() + "\n"
                    + "Carrera: " + a.program() + "\n\n"
                    + "DOCUMENTOS ADJUNTOS\n"
                    + "Foto tamaño carnet: " + a.photo() + "\n"
                    + "Boletín o créditos: " + a.transcript() + "\n"
                    + "Cédula o cédula juvenil: " + a.identificationDocument();
        }

        private void updateStatus() {
            if (selectedId < 0) {
                JOptionPane.showMessageDialog(table, "Selecciona una solicitud.", "Sin selección",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Applicant applicant = repository.findById(selectedId).orElse(null);
                if (applicant == null) {
                    JOptionPane.showMessageDialog(table, "La solicitud ya no está disponible.",
                            "Solicitud no encontrada", JOptionPane.WARNING_MESSAGE);
                    refresh();
                    return;
                }
                if (AdmissionProcessing.PENDING.equals(applicant.status())) {
                    List<String> issues = AdmissionProcessing.validate(applicant);
                    if (!issues.isEmpty()) {
                        JOptionPane.showMessageDialog(table,
                                "No se puede iniciar la revisión. Corrige estos puntos:\n\n- "
                                        + String.join("\n- ", issues),
                                "Expediente incompleto", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                String[] nextStatuses = AdmissionProcessing.nextStatuses(applicant.status());
                if (nextStatuses.length == 0) {
                    JOptionPane.showMessageDialog(table,
                            "Esta solicitud ya completó su proceso y no admite más cambios.",
                            "Proceso finalizado", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String nextStatus = (String) JOptionPane.showInputDialog(table,
                        "Etapa actual: " + applicant.status() + "\nSelecciona la siguiente etapa:",
                        "Procesar solicitud", JOptionPane.PLAIN_MESSAGE, null, nextStatuses,
                        nextStatuses[0]);
                if (nextStatus != null) {
                    repository.updateStatus(selectedId, nextStatus);
                    refresh();
                }
            } catch (IOException | IllegalStateException ex) {
                showError(table, ex.getMessage());
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

        private Optional<Applicant> findById(int id) throws IOException {
            return find("").stream().filter(applicant -> applicant.id() == id).findFirst();
        }

        private boolean identityExists(String identity) throws IOException {
            return find("").stream().anyMatch(applicant -> applicant.identity()
                    .equalsIgnoreCase(identity.trim()));
        }

        private void insert(Applicant applicant) throws IOException {
            ensureDatabase();
            if (identityExists(applicant.identity())) {
                throw new IllegalStateException("Ya existe una solicitud registrada con esta cédula.");
            }
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
                        applicant.secondaryEmail(), applicant.addressType(), applicant.province(),
                        applicant.district(), applicant.corregimiento(), applicant.neighborhood(),
                        applicant.collegeType(), applicant.baccalaureate(), applicant.faculty(),
                        applicant.schoolUnit(), applicant.transcript(), applicant.identificationDocument())));
                writer.newLine();
            }
        }

        private void updateApplicant(Applicant updatedApplicant) throws IOException {
            List<Applicant> applicants = find("");
            boolean updated = false;
            for (int i = 0; i < applicants.size(); i++) {
                Applicant existing = applicants.get(i);
                if (existing.id() != updatedApplicant.id()
                        && existing.identity().equalsIgnoreCase(updatedApplicant.identity().trim())) {
                    throw new IllegalStateException("La cédula ya está asociada a otra solicitud.");
                }
                if (existing.id() == updatedApplicant.id()) {
                    if (!AdmissionProcessing.PENDING.equals(existing.status())) {
                        throw new IllegalStateException(
                                "Solo se pueden actualizar solicitudes que aún estén pendientes.");
                    }
                    applicants.set(i, updatedApplicant);
                    updated = true;
                }
            }
            if (!updated) {
                throw new IllegalStateException("No se encontró la solicitud que deseas actualizar.");
            }
            rewrite(applicants);
        }

        private void deletePending(int id) throws IOException {
            List<Applicant> applicants = find("");
            Applicant applicant = applicants.stream()
                    .filter(record -> record.id() == id)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No se encontró la solicitud seleccionada."));
            if (!AdmissionProcessing.PENDING.equals(applicant.status())) {
                throw new IllegalStateException(
                        "Solo se pueden eliminar solicitudes que todavía estén pendientes.");
            }
            applicants.removeIf(record -> record.id() == id);
            rewrite(applicants);
        }

        private void updateStatus(int id, String status) throws IOException {
            List<Applicant> applicants = find("");
            boolean updated = false;
            for (int i = 0; i < applicants.size(); i++) {
                Applicant a = applicants.get(i);
                if (a.id() == id) {
                    if (!List.of(AdmissionProcessing.nextStatuses(a.status())).contains(status)) {
                        throw new IllegalStateException(
                                "La solicitud cambió de etapa. Actualiza la lista e inténtalo de nuevo.");
                    }
                    applicants.set(i, new Applicant(a.id(), a.identificationType(), a.nationality(), a.identity(),
                            a.sex(), a.firstName(), a.secondName(), a.firstSurname(), a.secondSurname(),
                            a.birthDate(), a.photo(), a.studentType(), a.email(), a.phone(), a.school(),
                            a.schoolLocation(), a.credits(), a.program(), a.campus(), status, a.date(),
                            a.phoneType(), a.emergencyPhoneType(), a.emergencyPhone(), a.emailType(),
                            a.secondaryEmailType(), a.secondaryEmail(), a.addressType(), a.province(),
                            a.district(), a.corregimiento(), a.neighborhood(), a.collegeType(),
                            a.baccalaureate(), a.faculty(), a.schoolUnit(), a.transcript(),
                            a.identificationDocument()));
                    updated = true;
                }
            }
            if (!updated) {
                throw new IllegalStateException("No se encontró la solicitud seleccionada.");
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
                    escape(a.secondaryEmailType()), escape(a.secondaryEmail()), escape(a.addressType()),
                    escape(a.province()), escape(a.district()), escape(a.corregimiento()),
                    escape(a.neighborhood()), escape(a.collegeType()), escape(a.baccalaureate()),
                    escape(a.faculty()), escape(a.schoolUnit()), escape(a.transcript()),
                    escape(a.identificationDocument()));
        }

        private static Applicant fromLine(String line) {
            String[] v = line.split("\t", -1);
            if (v.length == 8) {
                return new Applicant(Integer.parseInt(v[7]), "", "", v[1], "", v[0], "", "", "",
                        "", "", "", v[2], v[3], "", "", "", v[4], "", v[5], v[6],
                        "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            }
            if (v.length == 19) {
                return new Applicant(Integer.parseInt(v[18]), "", unescape(v[0]), unescape(v[1]),
                        unescape(v[2]), unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]),
                        unescape(v[7]), unescape(v[8]), "", unescape(v[9]), unescape(v[10]), unescape(v[11]),
                        unescape(v[12]), unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]),
                        unescape(v[17]), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            }
            if (v.length == 21) {
                return new Applicant(Integer.parseInt(v[20]), unescape(v[0]), unescape(v[1]), unescape(v[2]),
                        unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]), unescape(v[7]),
                        unescape(v[8]), unescape(v[9]), unescape(v[10]), unescape(v[11]), unescape(v[12]),
                        unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]), unescape(v[17]),
                        unescape(v[18]), unescape(v[19]), "", "", "", "", "", "", "", "", "", "", "", "", "",
                        "", "", "", "");
            }
            if (v.length != 36 && v.length != 38) {
                throw new IllegalStateException("Registro inválido en la base de datos.");
            }
            return new Applicant(Integer.parseInt(v[20]), unescape(v[0]), unescape(v[1]), unescape(v[2]),
                    unescape(v[3]), unescape(v[4]), unescape(v[5]), unescape(v[6]), unescape(v[7]),
                    unescape(v[8]), unescape(v[9]), unescape(v[10]), unescape(v[11]), unescape(v[12]),
                    unescape(v[13]), unescape(v[14]), unescape(v[15]), unescape(v[16]), unescape(v[17]),
                    unescape(v[18]), unescape(v[19]), unescape(v[21]), unescape(v[22]), unescape(v[23]),
                    unescape(v[24]), unescape(v[25]), unescape(v[26]), unescape(v[27]), unescape(v[28]),
                    unescape(v[29]), unescape(v[30]), unescape(v[31]), unescape(v[32]), unescape(v[33]),
                    unescape(v[34]), unescape(v[35]), v.length == 38 ? unescape(v[36]) : "",
                    v.length == 38 ? unescape(v[37]) : "");
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
