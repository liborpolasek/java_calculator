package polasek.zp4jv.pkgfinal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;

public class PaintPanel extends JComponent implements MouseListener, MouseMotionListener {

    private String actualTool;
    private String ifFill;
    private Color color;
    private int stroke;
    private int preparedStroke;

    private Shape shape;
    private BufferedImage image;
    private JTextArea text;

    private boolean pressed;

    private Point dragStartPoint;
    private Point dragCurrentPoint;

    public PaintPanel(String actualTool, String ifFill, Color color, int stroke) {
        this.actualTool = actualTool;
        this.ifFill = ifFill;
        this.color = color;
        this.stroke = stroke;
        this.preparedStroke = stroke;
        
        dragStartPoint = new Point();
        dragCurrentPoint = new Point();
        pressed = false;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setActualTool(String s) {
        this.actualTool = s;
    }

    public void setActualFill(String actualFill) {
        this.ifFill = actualFill;
    }

    public BufferedImage getImage() {
        return this.image;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public void setColor(Color color) {
        this.color = color;
        if(text != null) {
            text.setForeground(color);
        }
    }
    
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        g2.drawImage(image, null, null);
        drawShape(g2);
    }

    private void drawShape(Graphics2D g2) {
        if (shape != null) {
            if(actualTool.equals("Eraser")) g2.setColor(Color.WHITE);
            else g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (ifFill()) {
                g2.fill(shape);
            } else {
                g2.draw(shape);
            }
        } else if(text != null) {
            this.add(text);
        }
    }
    
    public void clear() {
        image = null;
    }
    
    public void prepareStroke(int stroke) {
        this.preparedStroke = stroke;
        if (text != null) {
            float temp = stroke;
            text.setFont(text.getFont().deriveFont(temp));
        }
    }
    
    public void setTextSize(int size) {
        this.stroke = size;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            dragStartPoint.setLocation(e.getPoint());
            stroke = preparedStroke;
            processText();
            pressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        pressed = false;
        if(!actualTool.equals("Text")) image = toImage();
        shape = null;
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (shape == null && !actualTool.equals("Text")) {
            processText();
            createObject();
        }
        if(text == null && actualTool.equals("Text")) createObject();
        if (pressed) {
            dragCurrentPoint.x = e.getPoint().x;
            dragCurrentPoint.y = e.getPoint().y;

            drawShape(dragStartPoint, dragCurrentPoint);
            
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
 
    private Boolean ifFill() {
        return ifFill.equals("Filled");
    }

    private void createObject() {
        switch (actualTool) {
            case "Rectangle":
                shape = new Rectangle2D.Double();
                break;
            case "Ellipse":
                shape = new Ellipse2D.Double();
                break;
            case "Line":
                shape = new Line2D.Double();
                break;
            case "Pencil":
                shape = new Path2D.Double();
                ((Path2D) shape).moveTo(dragStartPoint.x, dragStartPoint.y);
                break;
            case "Eraser":
                shape = new Path2D.Double();
                ((Path2D) shape).moveTo(dragStartPoint.x, dragStartPoint.y);
                break;
            case "Text":
                text = new JTextArea();
                text.setLocation(dragStartPoint);
                text.setFont(text.getFont().deriveFont(1, stroke));
                text.setForeground(color);
                break;
            default:
                break;
        }
    }

    private void drawShape(Point start, Point current) {
        switch (actualTool) {
            case "Rectangle":
                ((Rectangle2D) shape).setFrameFromDiagonal(start, current);
                break;
            case "Ellipse":
                ((Ellipse2D) shape).setFrameFromCenter(start, current);
                break;
            case "Line":
                ((Line2D) shape).setLine(start, current);
                break;
            case "Pencil":
                ((Path2D) shape).lineTo(current.x, current.y);
                ((Path2D) shape).moveTo(current.x, current.y);
                break;
            case "Eraser":
                ((Path2D) shape).lineTo(current.x, current.y);
                ((Path2D) shape).moveTo(current.x, current.y);
                break;
            case "Text":
                drawText();
                break;
            default:
                break;
        }
    }
    
    private void drawText() {
        int x = Math.min(dragStartPoint.x, dragCurrentPoint.x);
        int y = Math.min(dragStartPoint.y, dragCurrentPoint.y);
        int width = Math.abs(dragStartPoint.x - dragCurrentPoint.x);
        int height = Math.abs(dragStartPoint.y - dragCurrentPoint.y);
        text.setLocation(x, y);
        text.setSize(width, height);
        text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        text.setLineWrap(true);
        text.setFont(text.getFont().deriveFont(stroke));
    }

    private BufferedImage toImage() {
        BufferedImage img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        Rectangle region = new Rectangle(0, 0, img.getWidth(), img.getHeight());

        g.setColor(this.getForeground());
        g.setFont(this.getFont());
        this.paintAll(g);
        return img.getSubimage(region.x, region.y, region.width, region.height);
    }
    
    private void processText() {
        if (text != null) {
            text.setOpaque(false);
            text.setBorder(null);
            text.setEditable(false);
            text.setCaretColor(Color.WHITE);
            image = toImage();
            this.remove(text);
            text = null;
        }
    }
    
    public void doBeforeSaving() {
        processText();
    }
}
