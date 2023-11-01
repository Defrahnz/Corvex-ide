package corvex;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 *
 * @author Usuario
 */
public class Ventana extends javax.swing.JFrame {

    public String RutaActual = "";
    private LineNumber lineNumber;
    private boolean isNightMode;
    String rutaActual = System.getProperty("user.dir");
    /**
     * Creates new form Interfaz
     */
    public Ventana() {
        initComponents();
        inicializar();
        colors();
    }

    private void inicializar() {
        setTitle("Nuevo archivo");
        lineNumber = new LineNumber(this.TextAreaCodigo);
        this.jScrollPane8.setRowHeaderView(this.lineNumber);
        //this.jCheckBoxTheme.setSelected(false);
        this.isNightMode = false;
        this.TextAreaCodigo.addCaretListener(new CaretListener() {
            // Each time the caret is moved, it will trigger the listener and its method caretUpdate.
            // It will then pass the event to the update method including the source of the event (which is our textarea control)
            @Override
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                int line;
                try {
                    line = getLineOfOffset(TextAreaCodigo, dot);
                    int positionInLine = dot - getLineStartOffset(TextAreaCodigo, line);
                    jLabelLine.setText("linea: " + (line + 1) + ", columna: " + (positionInLine + 1));
                } catch (BadLocationException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    static int getLineOfOffset(JTextComponent comp, int offset) throws BadLocationException {
        Document doc = comp.getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    static int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException {
        Element map = comp.getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= map.getElementCount()) {
            throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    //METODO PARA PINTAS LAS PALABRAS 
    private void colors() {

        final StyleContext cont = StyleContext.getDefaultStyleContext();

        //COLORES 
        final AttributeSet attred = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 0, 35));
        final AttributeSet attgreen = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 255, 54));
        final AttributeSet attblue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 147, 255));
        final AttributeSet attpink = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 192, 203));
        final AttributeSet attblack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 0, 0));
        final AttributeSet attgray = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(155, 155, 155));
        final AttributeSet attOperadores = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 57, 128));
        final AttributeSet attwhite = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.WHITE);
        //STYLO 
        DefaultStyledDocument doc = new DefaultStyledDocument() {
            @Override
            public void postRemoveUpdate(DefaultDocumentEvent chng) {
                try {
                    super.postRemoveUpdate(chng);
                    String text = getText(0, getLength());
                    //reset text
                    if (isNightMode) {
                        setCharacterAttributes(0, getLength(), attwhite, true);
                    } else {
                        setCharacterAttributes(0, getLength(), attblack, true);
                    }
                    //match palabras reservaadas
                    Pattern palabrasReservadas = Pattern.compile("\\b(main|if|IF|else|ELSE|end|END|do|DO|while|WHILE|then|THEN|repeat|REPEAT|until|UNTIL|cin|cout)\\b");
                    Matcher matcher = palabrasReservadas.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attblue, true);
                    }
                    //match NUMEROS
                    Pattern numerosPattern = Pattern.compile("\\b(-?\\d+(\\.\\d+)?)\\b");
                    matcher = numerosPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attred, true);
                    }
                    //match tipo de datos
                    Pattern tipoDeDatos = Pattern.compile("\\b(int|INT|real|REAL|boolean|float|FLOAT|BOOLEAN)\\b");
                    matcher = tipoDeDatos.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attgreen, true);
                    }
                    //MATCH VALORES BOOLEANOS
                    Pattern booleanPattern = Pattern.compile("\\b(true|TRUE|false|FALSE)\\b");
                    matcher = booleanPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attpink, true);
                    }
                    //MATCH OPERADORES
                    Pattern operatorsPattern = Pattern.compile("[-+*/=<>!]");
                    matcher = operatorsPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attOperadores, true);
                    }
                    //DETECTAR COMETARIOS
                    Pattern singleLinecommentsPattern = Pattern.compile("\\/\\/.*");
                    matcher = singleLinecommentsPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attgray, false);
                    }

                    Pattern multipleLinecommentsPattern = Pattern.compile("\\/\\*.*?\\*\\/",
                            Pattern.DOTALL);
                    matcher = multipleLinecommentsPattern.matcher(text);
                    while (matcher.find()) {
                        setCharacterAttributes(matcher.start(),
                                matcher.end() - matcher.start(), attgray, false);

                    }
                } catch (BadLocationException ex) {
                    Logger.getLogger(Ventana.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }

            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                //reset text
                if (isNightMode) {
                    setCharacterAttributes(0, getLength(), attwhite, true);
                } else {
                    setCharacterAttributes(0, getLength(), attblack, true);
                }
                //match palabras reservaadas
                Pattern palabrasReservadas = Pattern.compile("\\b(main|if|IF|else|ELSE|end|END|do|DO|while|then|THEN|WHILE|repeat|REPEAT|until|UNTIL|cin|cout)\\b");
                Matcher matcher = palabrasReservadas.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attblue, true);
                }
                //match NUMEROS
                Pattern numerosPattern = Pattern.compile("\\b(-?\\d+(\\.\\d+)?)\\b");
                matcher = numerosPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attred, true);
                }
                //match tipo de datos
                Pattern tipoDeDatos = Pattern.compile("\\b(int|INT|real|REAL|boolean|float|BOOLEAN)\\b");
                matcher = tipoDeDatos.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attgreen, true);
                }
                //MATCH VALORES BOOLEANOS
                Pattern booleanPattern = Pattern.compile("\\b(true|TRUE|false|FALSE)\\b");
                matcher = booleanPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attpink, true);
                }
                //MATCH OPERADORES
                Pattern operatorsPattern = Pattern.compile("[-+*/=<>!]");
                matcher = operatorsPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attOperadores, true);
                }
                //DETECTAR COMETARIOS
                Pattern singleLinecommentsPattern = Pattern.compile("\\/\\/.*");
                matcher = singleLinecommentsPattern.matcher(text);
                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attgray, false);
                }

                Pattern multipleLinecommentsPattern = Pattern.compile("\\/\\*.*?\\*\\/",
                        Pattern.DOTALL);
                matcher = multipleLinecommentsPattern.matcher(text);

                while (matcher.find()) {
                    setCharacterAttributes(matcher.start(),
                            matcher.end() - matcher.start(), attgray, false);
                }
            }

        };

        JTextPane txt = new JTextPane(doc);
        String temp = this.TextAreaCodigo.getText();
        this.TextAreaCodigo.setStyledDocument(txt.getStyledDocument());
        this.TextAreaCodigo.setText(temp);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabelLine = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaResultados = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextAreaTablaSimbolos = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaErrores = new javax.swing.JTextArea();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaLexico = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaSintacticp = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextAreaSemantico = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        TextAreaCodigo = new javax.swing.JTextPane();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextAreaTablaSimbolos2 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(21, 30, 33));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelLine.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelLine.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLine.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLine.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelLine, gridBagConstraints);

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(825, 163));

        jPanel2.setBackground(new java.awt.Color(21, 30, 33));

        jTextAreaResultados.setEditable(false);
        jTextAreaResultados.setBackground(new java.awt.Color(150, 150, 150));
        jTextAreaResultados.setColumns(20);
        jTextAreaResultados.setFont(new java.awt.Font("Myanmar Text", 1, 24)); // NOI18N
        jTextAreaResultados.setForeground(new java.awt.Color(0, 51, 51));
        jTextAreaResultados.setRows(5);
        jTextAreaResultados.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextAreaResultados.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        jScrollPane2.setViewportView(jTextAreaResultados);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1586, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1586, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Resultado", jPanel2);

        jPanel8.setBackground(new java.awt.Color(21, 30, 33));

        jTextAreaTablaSimbolos.setEditable(false);
        jTextAreaTablaSimbolos.setBackground(new java.awt.Color(150, 150, 150));
        jTextAreaTablaSimbolos.setColumns(20);
        jTextAreaTablaSimbolos.setFont(new java.awt.Font("Myanmar Text", 1, 12)); // NOI18N
        jTextAreaTablaSimbolos.setForeground(new java.awt.Color(0, 102, 102));
        jTextAreaTablaSimbolos.setRows(5);
        jTextAreaTablaSimbolos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextAreaTablaSimbolos.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jScrollPane9.setViewportView(jTextAreaTablaSimbolos);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1586, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 1586, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tabla de Simbolos", jPanel8);

        jPanel3.setBackground(new java.awt.Color(21, 30, 33));
        jPanel3.setAlignmentX(0.0F);
        jPanel3.setAlignmentY(0.0F);
        jPanel3.setAutoscrolls(true);

        jTextAreaErrores.setEditable(false);
        jTextAreaErrores.setBackground(new java.awt.Color(150, 150, 150));
        jTextAreaErrores.setColumns(20);
        jTextAreaErrores.setFont(new java.awt.Font("Myanmar Text", 1, 14)); // NOI18N
        jTextAreaErrores.setForeground(new java.awt.Color(153, 0, 0));
        jTextAreaErrores.setRows(5);
        jTextAreaErrores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane3.setViewportView(jTextAreaErrores);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1586, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1586, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Errores", jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1202;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 12, 13, 12);
        jPanel1.add(jTabbedPane1, gridBagConstraints);

        jTabbedPane7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTabbedPane7.setPreferredSize(new java.awt.Dimension(520, 473));

        jPanel4.setBackground(new java.awt.Color(21, 30, 33));

        jTextAreaLexico.setEditable(false);
        jTextAreaLexico.setBackground(new java.awt.Color(150, 150, 150));
        jTextAreaLexico.setColumns(20);
        jTextAreaLexico.setFont(new java.awt.Font("Myanmar Text", 1, 18)); // NOI18N
        jTextAreaLexico.setForeground(new java.awt.Color(0, 51, 51));
        jTextAreaLexico.setRows(5);
        jTextAreaLexico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane4.setViewportView(jTextAreaLexico);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 539, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
        );

        jTabbedPane7.addTab("Lexico", jPanel4);

        jPanel5.setBackground(new java.awt.Color(21, 30, 33));

        jTextAreaSintacticp.setEditable(false);
        jTextAreaSintacticp.setBackground(new java.awt.Color(150, 150, 150));
        jTextAreaSintacticp.setColumns(20);
        jTextAreaSintacticp.setFont(new java.awt.Font("Myanmar Text", 1, 18)); // NOI18N
        jTextAreaSintacticp.setForeground(new java.awt.Color(51, 0, 0));
        jTextAreaSintacticp.setLineWrap(true);
        jTextAreaSintacticp.setRows(5);
        jTextAreaSintacticp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane5.setViewportView(jTextAreaSintacticp);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 539, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
        );

        jTabbedPane7.addTab("Sintactico", jPanel5);

        jPanel6.setBackground(new java.awt.Color(21, 30, 33));

        jTextAreaSemantico.setEditable(false);
        jTextAreaSemantico.setBackground(new java.awt.Color(150, 150, 150));
        jTextAreaSemantico.setColumns(20);
        jTextAreaSemantico.setFont(new java.awt.Font("Myanmar Text", 1, 18)); // NOI18N
        jTextAreaSemantico.setForeground(new java.awt.Color(0, 51, 51));
        jTextAreaSemantico.setRows(5);
        jTextAreaSemantico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane6.setViewportView(jTextAreaSemantico);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 539, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE))
        );

        jTabbedPane7.addTab("Semantico", jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 417;
        gridBagConstraints.ipady = 416;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 12);
        jPanel1.add(jTabbedPane7, gridBagConstraints);

        TextAreaCodigo.setBackground(new java.awt.Color(150, 150, 150));
        TextAreaCodigo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TextAreaCodigo.setFont(new java.awt.Font("Myanmar Text", 1, 18)); // NOI18N
        TextAreaCodigo.setForeground(new java.awt.Color(0, 51, 102));
        TextAreaCodigo.setMinimumSize(new java.awt.Dimension(6, 41));
        TextAreaCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TextAreaCodigoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TextAreaCodigoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextAreaCodigoKeyTyped(evt);
            }
        });
        jScrollPane8.setViewportView(TextAreaCodigo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 699;
        gridBagConstraints.ipady = 508;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 0, 0);
        jPanel1.add(jScrollPane8, gridBagConstraints);

        jTextAreaTablaSimbolos2.setEditable(false);
        jTextAreaTablaSimbolos2.setColumns(20);
        jTextAreaTablaSimbolos2.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        jTextAreaTablaSimbolos2.setForeground(new java.awt.Color(102, 102, 102));
        jTextAreaTablaSimbolos2.setRows(5);
        jTextAreaTablaSimbolos2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane11.setViewportView(jTextAreaTablaSimbolos2);

        jPanel1.add(jScrollPane11, new java.awt.GridBagConstraints());

        jMenuBar1.setAlignmentX(0.0F);
        jMenuBar1.setPreferredSize(new java.awt.Dimension(241, 21));

        jMenu1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jMenu1.setText("Archivo");
        jMenu1.setAlignmentX(0.8F);
        jMenu1.setAlignmentY(0.8F);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Abrir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Guardar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setText("Guardar Como...");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Cerrar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu6.setText("Formato");
        jMenu6.setToolTipText("");
        jMenu6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu6ayudar(evt);
            }
        });
        jMenuBar1.add(jMenu6);

        jMenu4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu4.setText("Compilar");
        jMenu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu4MouseClicked(evt);
            }
        });
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu4);

        jMenu5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu5.setText("Ayuda");
        jMenu5.setToolTipText("");
        jMenu5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ayudar(evt);
            }
        });
        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1615, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 841, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JFileChooser selectorArchivos = new JFileChooser();
        selectorArchivos.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        selectorArchivos.showOpenDialog(this);
        AbrirTxt(selectorArchivos.getSelectedFile().getPath());
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public void AbrirTxt(String Ruta) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        RutaActual = Ruta;
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(Ruta);

            TextAreaCodigo.setText(getTextFile(archivo));
            setTitle(archivo.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        saveFile();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void saveFile() {
        if (RutaActual != "") {
            try {
                String ruta = RutaActual;

                File file = new File(ruta);
                // Si el archivo no existe es creado
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(TextAreaCodigo.getText());
                setTitle(file.getName());
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JFileChooser guardar = new JFileChooser();
            guardar.showSaveDialog(null);
            guardar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            File archivo = guardar.getSelectedFile();
            System.out.println(guardar.getSelectedFile().getPath());
            guardarFichero(TextAreaCodigo.getText(), archivo);
            RutaActual = guardar.getSelectedFile().getPath();
        }
    }

    public String getTextFile(File file) {
        String text = "";
        try {

            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            while (true) {
                int b = entrada.read();
                if (b != -1) {
                    text += (char) b;
                } else {
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
            return null;
        } catch (IOException ex) {
            System.out.println("Error al leer el archivo... " + ex.getMessage());
            return null;
        }
        return text;
    }


    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        JFileChooser guardar = new JFileChooser();
        guardar.showSaveDialog(null);
        guardar.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        File archivo = guardar.getSelectedFile();
        System.out.println(guardar.getSelectedFile().getPath());
        RutaActual = guardar.getSelectedFile().getPath();
        guardarFichero(TextAreaCodigo.getText(), archivo);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        TextAreaCodigo.setText("");
        RutaActual = "";
        setTitle("Nuevo archivo");
        jTextAreaErrores.setText("");
        jTextAreaLexico.setText("");
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void TextAreaCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextAreaCodigoKeyReleased
        this.tecla(evt);
    }//GEN-LAST:event_TextAreaCodigoKeyReleased

    private void TextAreaCodigoKeyTyped(java.awt.event.KeyEvent evt) {

    }

    private void TextAreaCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextAreaCodigoKeyPressed
    }//GEN-LAST:event_TextAreaCodigoKeyPressed

       

    
    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        saveFile();
        executeLexico();
   
    }//GEN-LAST:event_jMenu4ActionPerformed

    private void jMenu4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu4MouseClicked
        saveFile();
        executeLexico();
    }//GEN-LAST:event_jMenu4MouseClicked

    private void ayudar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ayudar
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_ayudar

    private void jMenu6ayudar(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu6ayudar
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu6ayudar

    private void executeLexico() {
        PySystemState state = new PySystemState();
        state.argv.append(new PyString("-f"));
        state.argv.append(new PyString(RutaActual));
        PythonInterpreter interpreter = new PythonInterpreter(null, state);

        interpreter.execfile(rutaActual+"\\AnalizadorLexico.py");

        //abrir archivo lexemas
        jTextAreaLexico.setText("");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(rutaActual+"\\lexico.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaLexico.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        //abrir archivo de errores lexemas
        jTextAreaErrores.setText("Errores Lexico:\n" );
        try {
            in = new BufferedReader(new FileReader(rutaActual+"\\errors.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaErrores.append("\t" + str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
            this.Sintactico();
    }

    public void Sintactico(){
        String rutaActual = System.getProperty("user.dir");
        System.out.println("Ruta actual: " + rutaActual);
        Path rutaNueva = Paths.get(rutaActual);
        System.out.println("Ruta nueva: " + rutaNueva.toString());
        //Path ruta = Paths.get(rutaNueva.toString(),"AnalizadorLexico");
        //System.out.println(ruta);
        Path rutaScript = Paths.get(rutaNueva.toString()).resolve("analizadorsintactico.py");
        
        try {
            String salidaPython = PythonRunner.ejecutarScriptPython(rutaScript.toString());
            this.jTextAreaSintacticp.setText(salidaPython);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //abrir archivo sintactico
        jTextAreaSintacticp.setText("");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\ArbolSintactico.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaSintacticp.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        //abrir archivo de errores sintactico
        try {
            jTextAreaErrores.append("\nErrores análisis sintactico: " + '\n');
            in = new BufferedReader(new FileReader(rutaActual + "\\erroresSintactico.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaErrores.append("\t"+str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {

            }

        }
        //abrir archivo analisis semantico
        this.jTextAreaSemantico.setText("");
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\ArbolAnotaciones.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                jTextAreaSemantico.append(str + '\n');
            }
        } catch (IOException e) {
            
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
         //abrir archivo tabla de simbolos
        this.jTextAreaTablaSimbolos.setText("");
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\TablaSimbolos.txt"));
            String str;
            jTextAreaTablaSimbolos.append("\n");
            while ((str = in.readLine()) != null) {
                jTextAreaTablaSimbolos.append(str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        
        //abrir archivo de errores semantico
        try {
            in = new BufferedReader(new FileReader(rutaActual + "\\ErroresSemantico.txt"));
            String str;
            jTextAreaErrores.append("\nErrores análisis semantico: " + '\n');
            while ((str = in.readLine()) != null) {
                jTextAreaErrores.append("\t"+str + '\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {

            }

        }
    }
    
    

    private void tecla(java.awt.event.KeyEvent evt) {
        int keyCode = evt.getKeyCode();
        if ((keyCode >= 65 && keyCode <= 90) || (keyCode >= 48 && keyCode <= 57)
                || (keyCode >= 97 && keyCode <= 122) || (keyCode != 27 && !(keyCode >= 37
                && keyCode <= 40) && !(keyCode >= 16
                && keyCode <= 18) && keyCode != 524
                && keyCode != 20)) {

            if (!getTitle().contains("*")) {
                setTitle(getTitle() + "*");
            }
        }

    }

    /**
     * @param args the command line arguments
     */
    public void guardarFichero(String cadena, File archivo) {

        FileWriter escribir;
        try {

            escribir = new FileWriter(archivo, true);
            escribir.write(cadena);
            escribir.close();
            setTitle(archivo.getName());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Saving Issues");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Saving Issues output");
        }
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Ventana.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Ventana().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane TextAreaCodigo;
    private javax.swing.JLabel jLabelLine;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane7;
    private javax.swing.JTextArea jTextAreaErrores;
    private javax.swing.JTextArea jTextAreaLexico;
    private javax.swing.JTextArea jTextAreaResultados;
    private javax.swing.JTextArea jTextAreaSemantico;
    private javax.swing.JTextArea jTextAreaSintacticp;
    private javax.swing.JTextArea jTextAreaTablaSimbolos;
    private javax.swing.JTextArea jTextAreaTablaSimbolos2;
    // End of variables declaration//GEN-END:variables
}
