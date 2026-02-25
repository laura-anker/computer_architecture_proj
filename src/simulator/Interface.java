package simulator;

import javax.swing.*;
import java.awt.*;

public class Interface extends JFrame {

    private JTextField[] gprFields = new JTextField[4];
    private JTextField[] ixrFields = new JTextField[4];

    private JTextField pcField, marField, mbrField, irField;
    private JTextField ccField, mfrField, octalInputField;

    private JTextArea cacheArea, printerArea;
    private JTextField consoleInputField, programFileField;

    // simulator backend
    private simulator.memory.Memory memory;
    private simulator.cpu.CPU cpu;
    private simulator.io.ProgramLoader loader;

    public Interface() {

        // create simulator backend
        memory = new simulator.memory.Memory();
        cpu = new simulator.cpu.CPU(memory);
        loader = new simulator.io.ProgramLoader();
        
        setTitle("CSCI 6461 Machine Simulator");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftAndCenterPanel(), createRightPanel());
        split.setDividerLocation(820);
        add(split, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createLeftAndCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createLeftPanel(), BorderLayout.WEST);
        panel.add(createCenterPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(250, 600));
        left.setBorder(BorderFactory.createTitledBorder("Registers"));

        left.add(new JLabel("GPR"));
        for (int i = 0; i < 4; i++) {
            int regIndex = i; // required for lambda capture

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

            gprFields[i] = new JTextField(8);
            JButton btn = new JButton("R" + i);

            // LOAD BUTTON ACTION
            btn.addActionListener(e -> {
                try {
                    int value = Integer.parseInt(
                        gprFields[regIndex].getText(), 8); // octal input

                    cpu.getRegisters().GPR[regIndex].set(value);

                    refreshGUI();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid octal value for R" + regIndex
                    );
                }
            });

            row.add(new JLabel("" + i));
            row.add(gprFields[i]);
            row.add(btn);
            left.add(row);
        }

        left.add(Box.createVerticalStrut(10));
        left.add(new JLabel("IXR"));
        for (int i = 1; i <= 3; i++) {

            int regIndex = i;

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

            ixrFields[i] = new JTextField(8);
            JButton btn = new JButton("X" + i);

            btn.addActionListener(e -> {
                try {
                    int value = Integer.parseInt(
                        ixrFields[regIndex].getText(), 8);

                    cpu.getRegisters().IX[regIndex].set(value);

                    refreshGUI();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid octal value for X" + regIndex
                    );
                }
            });

            row.add(new JLabel("" + i));
            row.add(ixrFields[i]);
            row.add(btn);
            left.add(row);
        }

        return left;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setPreferredSize(new Dimension(350, 600));

        center.add(createCPURegisterPanel());
        center.add(createBinaryPanel());
        center.add(createControlPanel());
        center.add(createButtonPanel());

        return center;
    }

    private JPanel createCPURegisterPanel() {
        JPanel regs = new JPanel();
        regs.setLayout(new BoxLayout(regs, BoxLayout.Y_AXIS));
        regs.setBorder(BorderFactory.createTitledBorder("CPU Registers"));
    
        pcField = new JTextField(8);
        marField = new JTextField(8);
        mbrField = new JTextField(8);
        irField  = new JTextField(8);
    
        regs.add(createHorizontalFieldWithButton("PC", pcField));
        regs.add(createHorizontalFieldWithButton("MAR", marField));
        regs.add(createHorizontalFieldWithButton("MBR", mbrField));
        regs.add(createHorizontalField("IR", irField));  // no button for IR
    
        return regs;
    }

    private JPanel createHorizontalFieldWithButton(String label, JTextField field) {

        JPanel panel = new JPanel(new BorderLayout(8, 0));

        JLabel lbl = new JLabel(label);
        JButton btn = new JButton(label);

        btn.setPreferredSize(new Dimension(45, 25));

        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        // BUTTON ACTION
        btn.addActionListener(e -> {
            try {
                int value = Integer.parseInt(field.getText(), 8);
                var regs = cpu.getRegisters();

                switch (label) {
                    case "PC":  regs.PC.set(value); break;
                    case "MAR": regs.MAR.set(value); break;
                    case "MBR": regs.MBR.set(value); break;
                }

                refreshGUI();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid octal value for " + label);
            }
        });

        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.add(btn, BorderLayout.EAST);

        return panel;
    }

    // Smaller Binary Panel
    private JPanel createBinaryPanel() {
        JPanel binary = new JPanel(new BorderLayout());
        binary.setBorder(BorderFactory.createTitledBorder("Binary"));
        JTextArea binaryArea = new JTextArea(2, 20);   // made smaller
        binaryArea.setEditable(false);
        binary.add(new JScrollPane(binaryArea), BorderLayout.CENTER);
        return binary;
    }

    private JPanel createControlPanel() {
        JPanel controls = new JPanel(new GridLayout(1, 3, 10, 10));
        controls.setBorder(BorderFactory.createTitledBorder("Controls"));

        ccField = new JTextField(4);
        mfrField = new JTextField(4);
        octalInputField = new JTextField(4);

        controls.add(createVerticalField("CC", ccField));
        controls.add(createVerticalField("MFR", mfrField));
        controls.add(createVerticalField("Octal Input", octalInputField));

        return controls;
    }

    // IPL RED SHAPE + BUTTONS CONFIRMED
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loadBtn = new JButton("Load");
        JButton runBtn  = new JButton("Run");
        JButton stepBtn = new JButton("Step");
        JButton haltBtn = new JButton("Halt");

        panel.add(loadBtn);
        panel.add(runBtn);
        panel.add(stepBtn);
        panel.add(haltBtn);

        panel.add(createIPLIndicator());

        //add button actions
        loadBtn.addActionListener(e -> loadProgram());
        stepBtn.addActionListener(e -> stepCPU());
        runBtn.addActionListener(e -> runCPU());

        return panel;
    }

    private JComponent createIPLIndicator() {
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.RED);
                g.fillOval(0, 0, 40, 40);
                g.setColor(Color.WHITE);
                g.drawString("IPL", 12, 25);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(45, 45);
            }
        };
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(420, 600));

        cacheArea = new JTextArea(12, 30);
        printerArea = new JTextArea(6, 30);
        consoleInputField = new JTextField(30);

        right.add(new JLabel("Cache Content"));
        right.add(new JScrollPane(cacheArea));
        right.add(Box.createVerticalStrut(10));

        right.add(new JLabel("Printer"));
        right.add(new JScrollPane(printerArea));
        right.add(Box.createVerticalStrut(10));

        right.add(new JLabel("Console Input"));
        right.add(consoleInputField);

        return right;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Program File:"));
        programFileField = new JTextField(60);
        bottom.add(programFileField);
        return bottom;
    }

    private JPanel createVerticalField(String label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(80, 25));
        panel.add(lbl);
        panel.add(field);
        return panel;
    }

    private JPanel createHorizontalField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
    
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interface().setVisible(true));
    }

    //copies register values to GUI fields
    private void refreshGUI() {

        var regs = cpu.getRegisters();

        // GPRs
        for (int i = 0; i < 4; i++) {
            gprFields[i].setText(
                Integer.toOctalString(regs.GPR[i].get())
            );
        }

        // IX registers (1–3)
        for (int i = 1; i <= 3; i++) {
            ixrFields[i].setText(
                Integer.toOctalString(regs.IX[i].get())
            );
        }

        pcField.setText(Integer.toOctalString(regs.PC.get()));
        marField.setText(Integer.toOctalString(regs.MAR.get()));
        mbrField.setText(Integer.toOctalString(regs.MBR.get()));
        irField.setText(Integer.toOctalString(regs.IR.get()));
    }

    private void loadProgram() {

        try {
            String file = programFileField.getText();
            
            if (file.isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter program file.");
                return;
            }

            int start = loader.load(file, memory);

            cpu.getRegisters().PC.set(start);

            refreshGUI();

            printerArea.append("Program loaded. Start = "
                    + Integer.toOctalString(start) + "\n");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stepCPU() {

        cpu.cycle();
        refreshGUI();
    }

    private void runCPU() {

        new Thread(() -> {

            boolean running = true;

            while (running) {

                running = cpu.cycle();

                SwingUtilities.invokeLater(this::refreshGUI);

                try {
                    Thread.sleep(400);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }
}
