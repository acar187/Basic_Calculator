import javax.swing.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class App {
    JTextField screen;
    JLabel placeholder;
    JButton button;
    JPanel mainPanel;
    public void gui(){
        setupLookAndFeel();
        JFrame frame = new JFrame("Calculator");
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 4));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(mainPanel, BorderLayout.CENTER);
        screen = createScreen();
        frame.add(screen, BorderLayout.NORTH);
        createButton();
        frame.setVisible(true);
        frame.setResizable(true);
        //frame.setLocationRelativeTo(null);
    }
    
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JTextField createScreen(){
        screen = new JTextField(); 
        screen.setText("0"); // Standardwert setzen
        screen.setFocusable(false); // Textfeld nicht fokussierbar machen
        
        screen.setPreferredSize(new Dimension(100, 80)); // Breite und Höhe manuell setzen
        screen.setEditable(false); // Textfeld nicht editierbar machen
        screen.setFont(new Font("Arial", Font.PLAIN, 32)); // Schriftart und -größe setzen
        screen.setHorizontalAlignment(JTextField.RIGHT); // Text rechtsbündig ausrichten
        screen.setBackground(Color.LIGHT_GRAY); // Hintergrundfarbe setzen
        screen.setForeground(Color.BLACK); // Textfarbe setzen
        return screen;
    }

    private void createButton(){
        
        String[] buttonLabels = {
            "7", "8", "9", "AC","/",
            "4", "5", "6", " ","x",
            "1", "2", "3"," ", "+", 
            " " , "0", ".", "=","-",
        };
        for (String blabel : buttonLabels) {    
            button = new JButton(blabel);
            button.addActionListener(createListenerForLabel(blabel));
            button.setFont(new Font("Arial", Font.PLAIN, 32)); // Schriftart und -größe setzen
            button.setBackground(getColor()); // Hintergrundfarbe setzen
            if (blabel.contains(" ")) {
                button.setEnabled(false);
            }
            mainPanel.add(button);
        }
    }

    private Color getColor() {
        // Hintergrundfarbe für die Schaltflächen
       if (button.getText().equals("AC")|| button.getText().equals("=") || button.getText().equals("x") || button.getText().equals("/") || button.getText().equals("+") || button.getText().equals("-")) {
            return Color.ORANGE;
        }
        return null;
    }

    private ActionListener createListenerForLabel(String label) {
        switch (label) {
            case "AC":
                return e -> screen.setText("0"); // Reset the screen
            case "=":
                return new bresultListener(); // Perform calculation
            case "+":
            case "x":
            case "/":
                return e -> screen.setText(screen.getText() + label); // Append operator
            case ".":
                return e -> {
                    //if (!screen.getText().contains(".")) { // Prevent multiple commas
                        screen.setText(screen.getText() + "."); // Append comma
                    //}
                };
            case " ":
                return e -> {}; // Do nothing for empty buttons
            default: // Numbers
                return e -> {
                    if ("0".equals(screen.getText())) {
                        screen.setText(label); // Replace "0" with the number
                    } else {
                        screen.setText(screen.getText() + label); // Append the number
                    }
                };
            case "-":
                return e -> {
                    if (screen.getText().equals("0")) {
                        screen.setText("-"); // Set to negative if currently "0"
                    } else {
                        screen.setText(screen.getText() + "-"); // Append minus sign
                    }
                };
        }
    }

    public class bresultListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String input = screen.getText().trim();
    
            // Eingabe validieren
            if (input.isEmpty() || input.matches(".*[-+x/]$")) {
                screen.setText("Error");
                return;
            }
    
            // Teile die Eingabe und führe die Berechnung durch
            String[] parts = input.split("(?<=[-+x/])|(?=[-+x/])");
            
            if (parts.length > 1 && parts[0].equals("-")) {
                parts[1] = "-" + parts[1]; // Combine negative sign with the next number
                parts = java.util.Arrays.copyOfRange(parts, 1, parts.length); // Remove the standalone "-"
            }
            calculate(parts);
        }
    
        private void calculate(String[] parts) {
            try {
                // Liste für die Zwischenberechnung
                List<String> intermediate = new ArrayList<>();
    
                // Erster Durchlauf: Multiplikation und Division
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals("x") || parts[i].equals("/")) {
                        double leftOperand = Double.parseDouble(intermediate.remove(intermediate.size() - 1));
                        double rightOperand = Double.parseDouble(parts[i + 1]);
                        double tempResult = 0;
    
                        if (parts[i].equals("x")) {
                            tempResult = leftOperand * rightOperand; // Multiplikation
                        } else if (parts[i].equals("/")) {
                            if (rightOperand != 0) {
                                tempResult = leftOperand / rightOperand; // Division
                            } else {
                                throw new ArithmeticException("Division durch Null ist nicht erlaubt.");
                            }
                        }
    
                        intermediate.add(String.valueOf(tempResult));
                        i++; // Überspringe den nächsten Operand, da er bereits verarbeitet wurde
                    } else {
                        intermediate.add(parts[i]);
                    }
                }
    
                // Zweiter Durchlauf: Addition und Subtraktion
                double result = Double.parseDouble(intermediate.get(0)); // Starte mit dem ersten Wert
                for (int i = 1; i < intermediate.size(); i += 2) {
                    String operator = intermediate.get(i);
                    double operand = Double.parseDouble(intermediate.get(i + 1));
    
                    if (operator.equals("+")) {
                        result += operand; // Addition
                    } else if (operator.equals("-")) {
                        result -= operand; // Subtraktion
                    }
                }
    
                // Ergebnis anzeigen
                if (result % 1 == 0) { // Überprüfen, ob das Ergebnis eine ganze Zahl ist
                    screen.setText(String.valueOf((int) result)); // Ganze Zahl anzeigen
                } else {
                    screen.setText(String.valueOf(result)); // Dezimalzahl anzeigen
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                System.out.println("Ungültiges Eingabeformat.");
                screen.setText("Error");
            } catch (ArithmeticException ex) {
                System.out.println("Fehler: " + ex.getMessage());
                screen.setText("Error");
            }
        }
    }
    public static void main(String[] args) throws Exception {
        App app = new App();
        app.gui();
           
    }
}
