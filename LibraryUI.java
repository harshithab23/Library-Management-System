import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Custom JPanel with background image
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

// LibraryUI class for the frontend
public class LibraryUI extends JFrame {
    private LibraryBackend backend = new LibraryBackend();
    private JTextArea inventoryDisplay;
    private Icon dialogIcon;

    public LibraryUI() {
        setTitle("Library Management System");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        dialogIcon = resizeIcon(new ImageIcon("logo.png"), 40, 40);
        setIconImage(((ImageIcon) dialogIcon).getImage());

        BackgroundPanel bgPanel = new BackgroundPanel("bg.jpg");
        bgPanel.setLayout(new BorderLayout());

        // Inventory Display Styling
        inventoryDisplay = new JTextArea(10, 50);
        inventoryDisplay.setEditable(false);
        updateInventoryDisplay();

        inventoryDisplay.setBackground(new Color(240, 240, 240));
        inventoryDisplay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        inventoryDisplay.setFont(new Font("Arial", Font.PLAIN, 14));

        // Panel to center the inventory display
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setOpaque(false);
        inventoryPanel.setLayout(new GridBagLayout());
        inventoryPanel.add(new JScrollPane(inventoryDisplay));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton rentButton = new JButton("Rent Book");
        JButton returnButton = new JButton("Return Book");

        addButton.addActionListener(e -> addBook());
        deleteButton.addActionListener(e -> deleteBook());
        rentButton.addActionListener(e -> rentBook());
        returnButton.addActionListener(e -> returnBook());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);

        bgPanel.add(inventoryPanel, BorderLayout.CENTER);
        bgPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(bgPanel);
    }

    private Icon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    private void addBook() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Title:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        JTextField authorField = new JTextField();
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Rent Cost:"));
        JTextField rentCostField = new JTextField();
        inputPanel.add(rentCostField);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Add Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, dialogIcon);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String priceText = priceField.getText().trim();
            String rentCostText = rentCostField.getText().trim();

            if (!title.isEmpty() && !author.isEmpty() && !priceText.isEmpty() && !rentCostText.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceText);
                    double rentCost = Double.parseDouble(rentCostText);
                    backend.addBook(title, author, price, rentCost);
                    JOptionPane.showMessageDialog(this, "Book added!", "Success", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
                    updateInventoryDisplay();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and rent cost.",
                            "Input Error", JOptionPane.ERROR_MESSAGE, dialogIcon);
                }
            }
        }
    }

    private void deleteBook() {
        String title = (String) JOptionPane.showInputDialog(
                this, "Enter Book Title to Delete:", "Delete Book", JOptionPane.PLAIN_MESSAGE, dialogIcon, null, "");

        if (title != null && !title.isEmpty()) {
            backend.deleteBook(title);
            JOptionPane.showMessageDialog(this, "Book deleted if it exists and is not rented.",
                    "Delete Book", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            updateInventoryDisplay();
        }
    }

    private void rentBook() {
        String title = (String) JOptionPane.showInputDialog(
                this, "Enter Book Title to Rent:", "Rent Book", JOptionPane.PLAIN_MESSAGE, dialogIcon, null, "");

        if (title != null && !title.isEmpty()) {
            if (backend.rentBook(title)) {
                JOptionPane.showMessageDialog(this, "Book rented!", "Rent Book", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            } else {
                JOptionPane.showMessageDialog(this, "Book is unavailable or already rented.",
                        "Rent Book", JOptionPane.WARNING_MESSAGE, dialogIcon);
            }
            updateInventoryDisplay();
        }
    }

    private void returnBook() {
        String title = (String) JOptionPane.showInputDialog(
                this, "Enter Book Title to Return:", "Return Book", JOptionPane.PLAIN_MESSAGE, dialogIcon, null, "");

        if (title != null && !title.isEmpty()) {
            if (backend.returnBook(title)) {
                JOptionPane.showMessageDialog(this, "Book returned!", "Return Book", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            } else {
                JOptionPane.showMessageDialog(this, "Book is not rented or does not exist.",
                        "Return Book", JOptionPane.WARNING_MESSAGE, dialogIcon);
            }
            updateInventoryDisplay();
        }
    }

    private void updateInventoryDisplay() {
        StringBuilder displayText = new StringBuilder("Inventory:\n");
        for (Book book : backend.getInventory()) {
            displayText.append(String.format("%-30s %-20s Price: Rs%.2f Rent: Rs%.2f Status: %s\n",//
                    book.getTitle(), book.getAuthor(), book.getPrice(), book.getRentCost(),
                    book.isRented() ? "Rented" : "Available"));
        }
        displayText.append("\nTotal Books Available: ").append(backend.getBookCount());
        inventoryDisplay.setText(displayText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryUI libraryUI = new LibraryUI();
            libraryUI.setVisible(true);
        });
    }
}