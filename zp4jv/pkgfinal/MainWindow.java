package polasek.zp4jv.pkgfinal;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class MainWindow extends JFrame {

    private JPanel panel = new JPanel();
    private PaintPanel paintP;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem newImage;
    private JMenuItem loadImage;
    private JMenuItem close;
    private JMenuItem saveImage;

    private JPanel pnTools;
    private TitledBorder tTools;
    private JComboBox<String> tools;
    private JPanel pnFill;
    private TitledBorder tFill;
    private JComboBox<String> fill;
    private JPanel pnStroke;
    private TitledBorder tStroke;
    private JComboBox<Integer> stroke;
    private JPanel pnColor;
    private TitledBorder tColor;
    private Button btColor;
    private JColorChooser colorChooser;
    
    private JPanel topPanel;

    public MainWindow() {
        setTitle("Paint without pain");
        
        //listener -> při zavření okna, aby vyskočil dialog na uložení
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                if (paintP.getImage() != null) {
                    try {
                        if(showSaveDialogExit(false)) e.getWindow().dispose();
                    } catch (IOException ex) {
                    }
                }
            }
        });

        //------------------------------menu------------------------------------
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menuBar.add(menu);
        setJMenuBar(menuBar);
 
        newImage = new JMenuItem("New");
        newImage.addActionListener((ActionEvent e) -> {
            if (paintP.getImage() != null) {
                try {
                    if (showSaveDialog()) {
                        paintP.clear();
                        paintP.repaint();
                    }
                } catch (IOException ex) {
                }
            }
        });
        menu.add(newImage);

        loadImage = new JMenuItem("Load");
        loadImage.addActionListener((ActionEvent e) -> {
            BufferedImage img = loadFile();
            if (img != null) {
                try {
                    if(showSaveDialog()) {
                        paintP.setImage(img);
                        paintP.repaint();
                    }
                } catch (IOException ex) {
                }             
            }
        });
        menu.add(loadImage);

        //menu ukládání
        saveImage = new JMenuItem("Save as...");
        saveImage.addActionListener((ActionEvent e) -> {
            try {
                saveFileTo(paintP.getImage());
            } catch (IOException ex) {
                System.out.println("Error: cannot save file");
            }
        });
        menu.add(saveImage);
      
        close = new JMenuItem("Exit");
        close.addActionListener((ActionEvent e) -> {
            if (paintP.getImage() != null) {
                try {
                    if(showSaveDialogExit(true)) {
                        dispose();
                    }
                } catch (IOException ex) {
                }
            }
        });
        menu.add(close);

        //----------------------------konec menu--------------------------------
        
        //combo box na tvary
        tools = new JComboBox<>();
        tools.addItem("Pencil");
        tools.addItem("Line");
        tools.addItem("Rectangle");
        tools.addItem("Ellipse");
        tools.addItem("Text");
        tools.addItem("Eraser");
        tools.addActionListener((ActionEvent event) -> {
            JComboBox<String> combo = (JComboBox<String>) event.getSource();
            String item = (String) combo.getSelectedItem();
            paintP.setActualTool(item);
            if (item.equals("Line") || item.equals("Pencil") || item.equals("Eraser") || item.equals("Text")) {
                fill.setEnabled(false);
                fill.setSelectedItem("Empty");
            } else {
                fill.setEnabled(true);
            }
            
            if(item.equals("Text")) {
                stroke.setSelectedItem(12);
                paintP.setTextSize((int)stroke.getSelectedItem());
            }
        });
        
        //přidání boxu do vlastního panelu a přidání borderu s textem
        tTools = BorderFactory.createTitledBorder("Tools");
        tTools.setTitleJustification(TitledBorder.CENTER);
        pnTools = new JPanel();
        pnTools.setLayout(new BoxLayout(pnTools, BoxLayout.X_AXIS));
        pnTools.add(tools);
        pnTools.setBorder(tTools);
        
        
        //combo box na filled
        fill = new JComboBox<>();
        fill.setEnabled(false);
        fill.addItem("Empty");
        fill.addItem("Filled");
        fill.addActionListener((ActionEvent event) -> {
            JComboBox<String> combo = (JComboBox<String>) event.getSource();
            String s = (String) combo.getSelectedItem();
            paintP.setActualFill(s);
            if(s.equals("Filled")) stroke.setEnabled(false);
            else stroke.setEnabled(true);
        });
        
        //přidání boxu do vlastního panelu a přidání borderu s textem
        tFill = BorderFactory.createTitledBorder("Fill");
        tFill.setTitleJustification(TitledBorder.CENTER);
        pnFill = new JPanel();
        pnFill.setLayout(new BoxLayout(pnFill, BoxLayout.X_AXIS));
        pnFill.add(fill);
        pnFill.setBorder(tFill);
        
        
        //combo box na tloušťku linky
        stroke = new JComboBox<>();
        for(int i = 0; i < 20; i++) {
            stroke.addItem(i+1);
        }
        stroke.addActionListener((ActionEvent event) -> {
            JComboBox<Integer> combo = (JComboBox<Integer>) event.getSource();
            paintP.prepareStroke((int)combo.getSelectedItem());
        });
        stroke.setEditable(true);
        stroke.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            //omezení na čísla a 3 znaky
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (stroke.getEditor().getItem().toString().length() < 3) {
                    if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                        getToolkit().beep();
                        e.consume();
                    }
                } else { 
                    e.consume();
                }
            }
        });
        
        //přidání boxu do vlastního panelu a přidání borderu s textem
        tStroke = BorderFactory.createTitledBorder("Size");
        tStroke.setTitleJustification(TitledBorder.CENTER);
        pnStroke = new JPanel();
        pnStroke.setLayout(new BoxLayout(pnStroke, BoxLayout.X_AXIS));
        pnStroke.add(stroke);
        pnStroke.setBorder(tStroke);
        
        
        //výběr barev a tlačítko k tomu
        colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().setSelectedColor(Color.BLACK);
        btColor = new Button();
        btColor.setBackground(colorChooser.getColor());
        btColor.addActionListener((ActionEvent e) -> {
            Color newColor = JColorChooser.showDialog(colorChooser, "Choose Color", btColor.getBackground());
            if (newColor != null) {
                btColor.setBackground(newColor);
                paintP.setColor(newColor);
            }
        });
        
        //přidání buttonu do vlastního panelu a přidání borderu s textem
        tColor = BorderFactory.createTitledBorder("Color");
        tColor.setTitleJustification(TitledBorder.CENTER);
        pnColor = new JPanel();
        pnColor.setLayout(new BoxLayout(pnColor, BoxLayout.X_AXIS));
        pnColor.add(btColor);
        pnColor.setBorder(tColor);

        
        //horní panel
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(pnTools);
        topPanel.add(pnFill);
        topPanel.add(pnStroke);
        topPanel.add(pnColor);
        pnStroke.setPreferredSize(new Dimension(topPanel.getHeight(), 1));

        //kreslící plátno
        paintP = new PaintPanel(tools.getSelectedItem().toString(), fill.getSelectedItem().toString(), btColor.getBackground(), (int)stroke.getSelectedItem());

        //přidání do hlavního panelu
        panel.setLayout(new BorderLayout());
        panel.add(paintP, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        getContentPane().add(panel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(800, 600));
        pack();
    }

    private boolean saveFileTo(BufferedImage img) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new OpenFileFilter(".jpg", "jpg"));
        fileChooser.setFileFilter(new OpenFileFilter(".gif", "gif"));
        fileChooser.setFileFilter(new OpenFileFilter(".bmp", "bmp"));
        fileChooser.setFileFilter(new OpenFileFilter(".png", "png"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File oneFile;
            switch (fileChooser.getFileFilter().getDescription()) {
                case "png":
                    oneFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".png");
                    ImageIO.write(img, "png", oneFile);
                    break;
                case "jpg":
                    oneFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".jpg");
                    ImageIO.write(img, "jpg", oneFile);
                    break;
                case "gif":
                    oneFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".gif");
                    ImageIO.write(img, "gif", oneFile);
                    break;
                case "bmp":
                    oneFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".bmp");
                    ImageIO.write(img, "bmp", oneFile);
                    break;
                default:
                    break;
            }
        } else return true;
        return false;
    }
    
    private BufferedImage loadFile() {
        BufferedImage img = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new OpenFileFilter(".jpg", "jpg"));
        fileChooser.setFileFilter(new OpenFileFilter(".gif", "gif"));
        fileChooser.setFileFilter(new OpenFileFilter(".bmp", "bmp"));
        fileChooser.setFileFilter(new OpenFileFilter(".png", "png"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                img = ImageIO.read(selectedFile);
            } catch (IOException ex) {
                System.out.println("Error: cannot read file");
            }
        }
        return img;
    }
    
    private boolean showSaveDialogExit(boolean cancel) throws IOException {
        if (paintP.getImage() != null) {
            int option;
            if(cancel) {
                option = JOptionPane.showConfirmDialog(null, "Would You Like to Save your Image?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            } else {
                option = JOptionPane.showConfirmDialog(null, "Would You Like to Save your Image before Leaving?", "Warning", JOptionPane.YES_NO_OPTION);
            }
        
            if (option == JOptionPane.YES_OPTION) {
                paintP.doBeforeSaving();
                saveFileTo(paintP.getImage());
            } else if(option == JOptionPane.NO_OPTION) {
                return true;
            }
        }
        return false;
    }
    
    private boolean showSaveDialog() throws IOException {
        if (paintP.getImage() != null) {
            int option = JOptionPane.showConfirmDialog(null, "Would You Like to Save your Previous Image?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                paintP.doBeforeSaving();
                if(saveFileTo(paintP.getImage())) return false;
            } else if(option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
                return false;
            }
        }
        return true;
    }
}
