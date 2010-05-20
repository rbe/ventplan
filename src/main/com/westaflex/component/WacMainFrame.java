/*
 * WacMainFrame.java
 *
 * Created on 11. Juli 2006, 20:20
 */
package com.westaflex.component;

import com.bensmann.superswing.component.util.JDesktopUtil;
import com.bensmann.superswing.component.util.LookAndFeelUtil;
import com.bensmann.superswing.model.XmlDataModel;
import com.bensmann.superswing.observer.ShutdownObservable;
import com.bensmann.superswing.observer.ShutdownObserver;
import com.bensmann.superswing.ooo.OOoInternalFrame;
import com.bensmann.superswing.thread.SwingWorker3;
import com.seebass.tools.Tools;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.beans.OfficeDocument;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.DateTime;
import com.sun.star.util.XRefreshable;
import com.sun.star.view.XPrintable;
import com.westaflex.component.classes.SeeDocBridge;
import com.westaflex.database.WestaDB;
import com.westaflex.dialogs.OOoBeanViewer;
import com.westaflex.dialogs.PrintDialog;
import com.westaflex.dialogs.SettingsDialog;
import com.westaflex.resource.Strings.Strings;
import com.westaflex.util.OOoBridge;
import com.westaflex.util.OOoBridgeSwingWorker;
import com.westaflex.util.WestaWacApplHelper;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 *
 * @author  rb
 */
public class WacMainFrame extends javax.swing.JFrame
        implements ShutdownObservable {

    /**
     *
     */
    class NeuesProjektSwingWorker extends SwingWorker3 {

        private ProjectInternalFrame p;

        @Override
        public Object construct() {

            setStatusText(Strings.CREATE_NEW_PROJECT, true);
            // Initialisiere neues Projekt und speichere Referenz
            // in der Liste für Projekte
            p = new ProjectInternalFrame(WacMainFrame.this);

            Rectangle r = p.getBounds();
            r.setLocation(projects.size() * 5, projects.size() * 5);
            p.setBounds(r);
            p.setVisible(true);
            // Projekt dem Desktop hinzufügen
            wacDesktopPane.add(p, 0);
            projects.add(p);

            // Kindfenster aktivieren
            try {
                //
                p.setSelected(true);
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            }

            // Menu "Fenster"
            updateFensterMenu();

            return null;

        }

        @Override
        public void finished() {
            setStatusText(Strings.PREPARED, false);
        }

    }

    /**
     *
     */
    class ProjektLadenSwingWorker extends SwingWorker {

        private File wpxFile;

        private ProjectInternalFrame p;

        private Exception exception;

        public ProjektLadenSwingWorker(File file) {
            wpxFile = file;
        }

        @Override
        protected Object doInBackground() throws Exception {

            if (wpxFile != null) {
                setStatusText(Strings.LOADING_PROJECT_FROM + wpxFile.getAbsolutePath(), true);

                // Speichere aktuelles Verzeichnis zum Laden der Projekte
                applHelper.setPreference(WestaWacApplHelper.PROJECT_LOAD_DIRECTORY, wpxFile.getParent());

                // Initialisiere neues Projekt
                p = new ProjectInternalFrame(WacMainFrame.this);

                // Setze Dateihandle in Project
                p.setWpxFile(wpxFile);

                try {
                    // Werte aus XML in Swing-Komponenten injizieren
                    XmlDataModel.load(p, wpxFile);
                    // Tabellenspalten anpassen
                    p.initLayout();
                } catch (XPathExpressionException ex) {
                    ex.printStackTrace();
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = ex;
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = ex;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = ex;
                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = ex;
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = ex;
                } catch (SAXException ex) {
                    ex.printStackTrace();
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = ex;
                } finally {

                    if (exception == null) {

                        p.getRaeume().fillFromTableModel(p.getSeebassTabelle().getModel());
                        // Speichere Referenz in der Liste für Projekte
                        projects.add(p);
                        // Set location to upper left corner of JDesktopPane
                        Rectangle r = p.getBounds();
                        r.setLocation(projects.size() * 5, projects.size() * 5);
                        p.setBounds(r);
                        p.setVisible(true);
                        // Projekt dem Desktop hinzufügen
                        wacDesktopPane.add(p, 0);
                        // InternalFrame auswählen
                        wacDesktopPane.setSelectedFrame(p);
                        wacDesktopPane.getDesktopManager().activateFrame(p);

                        // Menu "Fenster"
                        updateFensterMenu();
                        // Werte anzeigen
                        p.updateAllComponents();
                        p.initLayout();
                    } else {
                        Tools.errbox(Strings.CANNOT_LOAD_PROJECT);
                    }
                    wacMainProgressBar.setIndeterminate(false);
                    setStatusText(Strings.PREPARED, false);
                }
            }
            setStatusText();
            return null;
        }

    }

    /**
     *
     */
    class ProjektSpeichernSwingWorker extends SwingWorker3 {

        private File wpxFile;

        private ProjectInternalFrame projectInternalFrame;

        private Exception exception;

        public ProjektSpeichernSwingWorker(ProjectInternalFrame projectInternalFrame) {
            this.projectInternalFrame = projectInternalFrame;
        }

        public void setFile(File file) {
            wpxFile = file;
        }

        @Override
        public Object construct() {

            String statusText = null;

            // Filechooser nur anzeigen wenn kein Filehandle bekannt
            if (wpxFile == null) {
                wpxFile = projectInternalFrame.getWpxFile();
            }
            // Setze Dateihandle in Projekt
            projectInternalFrame.setWpxFile(wpxFile);

            if (wpxFile != null) {

                statusText = String.format(Strings.SAVE_PROJECT_AS,
                        projectInternalFrame.getTitle(),
                        wpxFile.getPath());

                wacMainStatusLabel.setText(statusText);
                wacMainProgressBar.setIndeterminate(true);

                // Speichere aktuelles Verzeichnis zum Speichern der Projekte
                // in den Preferences
                applHelper.setPreference(
                        WestaWacApplHelper.PROJECT_SAVE_DIRECTORY,
                        wpxFile.getParent());
                try {
                    // Werte aus Swing-Komponenten in XML-Dokument speichern
                    XmlDataModel.save(
                            projectInternalFrame,
                            projectInternalFrame.getWpxFile());

                } catch (ParserConfigurationException e) {
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = e;
                } catch (TransformerException e) {
                    // Save exception for displaying in ShowErrorInternalFrame
                    exception = e;
                } finally {
                    if (exception == null) {

                        wacMainStatusLabel.setText(statusText + Strings.READY);

                    } else {
                        wacMainStatusLabel.setText(statusText + Strings.ERROR);
                        Tools.errbox(Strings.CANNOT_SAVE_PROJECT);
                    }
                }
            }
            return null;
        }

        @Override
        public void finished() {
            wacMainProgressBar.setIndeterminate(false);
        }

    }

    /**
     *
     */
    class ProjektSeitenansicht extends SwingWorker {

        private OOoBeanViewer aViewer;

        private JDialog seitenansichtFrame;

        private ProjectInternalFrame projectInternalFrame;

        ProjektSeitenansicht() {
            seitenansichtFrame = new JDialog(WacMainFrame.this, false);
            seitenansichtFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            aViewer = new OOoBeanViewer();
            aViewer.setParent(seitenansichtFrame);
            aViewer.init();
            aViewer.start();
            seitenansichtFrame.setLayout(new java.awt.BorderLayout());
            seitenansichtFrame.add(aViewer);
            seitenansichtFrame.setLocation(0, 0);
            seitenansichtFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            seitenansichtFrame.setVisible(true);
        }

        @Override
        protected Object doInBackground() throws Exception {

            InputStream stream = null;
            PropertyValue[] prop = new PropertyValue[1];
            // Aktuelles Projekt holen

            projectInternalFrame =
                    (ProjectInternalFrame) wacDesktopPane.getSelectedFrame();
            // Titel des OOoInternalFrame setzen
            seitenansichtFrame.setTitle(Strings.PAGE_PREVIEW_FOR
                    + projectInternalFrame.getTitle());
            prop[0] = new PropertyValue();
            prop[0].Name = "UpdateDocMode";
            prop[0].Value = com.sun.star.document.UpdateDocMode.NO_UPDATE;
            stream = getClass().getResourceAsStream("/com/westaflex/resource/WestaWAC.ott");
            aViewer.loadDocumentFromStream(stream, prop, "Auslegungsdokument");
            // Dokument anzeigen
            projectInternalFrame.toDocument(aViewer.getDocument());
            // Statusbar und Progressbar
            wacMainStatusLabel.setText(String.format(Strings.LOADING_PAGE_PREVIEW_FOR,
                    projectInternalFrame.getTitle())
                    + Strings.READY);
            wacMainProgressBar.setIndeterminate(false);
            return null;
        }

    }

    /**
     * Creates new form WacMainFrame
     */
    public WacMainFrame() {

        // Liste für ShutdownObserver initialisieren
        shutdownObserverList = new LinkedList<ShutdownObserver>();

        // ApplHelper holen und XML-Konfiguration lesen
        applHelper = WestaWacApplHelper.getInstance();

        // Liste für Projekte anlegen
        projects = new LinkedList<ProjectInternalFrame>();

        // Komponenten initialisieren
        initComponents();

        // Actions zuweisen
        initActions();

        // Fullscreen
        setExtendedState(MAXIMIZED_BOTH);

        // DesktopPane an ApplHelper übergeben
        applHelper.setDesktopPane(wacDesktopPane);

        //
        desktopUtil = JDesktopUtil.getInstance(wacDesktopPane);

        // Set look and feel
        LookAndFeelUtil.setWindowsLookAndFeel(this);

        // Druckdialog für Auslegung
        printDialog = new PrintDialog(this, true);

        addPropertyChangeListener("Changed", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                wacDesktopPane.getSelectedFrame().firePropertyChange(
                        evt.getPropertyName(),
                        (Long) evt.getOldValue(),
                        (Long) evt.getNewValue());
//                System.out.println("changed has happened!");
            }

        });
    }

    private void initActions() {
        applyAction(pbAuslegung, wacDateiNeuMenuItem, "Auslegung", "Auslegung");
        applyAction(pbAngebot, wacAngebotNeu, "Angebot", "Angebot");
        tbNeuButton.setAction(wacDateiNeuMenuItem.getAction());
    }

    private void applyAction(JButton pb, JMenuItem mi, String name, final String suffix) {

        Action a = null;
        ImageIcon icon = null, icon_small = null;

        icon = new ImageIcon(getClass().getResource("/com/westaflex/resource/command/" + name + ".png"));
        icon_small = new ImageIcon(getClass().getResource("/com/westaflex/resource/command/" + name + "_small.png"));
        a = new AbstractAction(name, icon) {

            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    WacMainFrame.this.getClass().getDeclaredMethod("perform" + suffix, ActionEvent.class).invoke(WacMainFrame.this, e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        };
        mi.setAction(a);
        mi.setIcon(icon_small);
        pb.setAction(a);
    }

    public void performAuslegung(ActionEvent evt) {
        new NeuesProjektSwingWorker().start();
    }

    public void performAngebot(ActionEvent evt) {

        int res = -1;
        Object[] doc = null;
        String macroPath = "";
        if (wacDesktopPane.getSelectedFrame() instanceof ProjectInternalFrame) {
            res = Tools.askbox(Strings.CREATE_NEW_PROPOSAL_FROM_LAYOUT, Strings.PROPOSAL);
            if (res == Tools.OPTION_OK) {
                macroPath = "Westa.Main.silentMode";
//                doc = new Object[1];
                String[] s = new String[]{"Oliver", "Seebass"};
//                doc[0] = s;
                doc = ((ProjectInternalFrame) wacDesktopPane.getSelectedFrame()).collectTransferData();
                //doc[0] = ((ProjectInternalFrame)wacDesktopPane.getSelectedFrame()).getOfficeDocument();
            }
        }
        if (res != JOptionPane.YES_OPTION) {
            res = JOptionPane.showConfirmDialog(this, Strings.CREATE_NEW_PROPOSAL, Strings.PROPOSAL, JOptionPane.YES_NO_OPTION);
            macroPath = "Westa.Main.silentMain";
        }
        if (res == JOptionPane.YES_OPTION) {
            setStatusText(Strings.CREATING_NEW_PROPOSAL, true);
            update(getGraphics());
            new OOoBridgeSwingWorker().executeMacro(macroPath, OOoBridge.LOCATION_APPLICATION, doc);
            requestFocus();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        wacToolBar = new javax.swing.JToolBar();
        tbNeuButton = new javax.swing.JButton();
        tbOeffnenButton = new javax.swing.JButton();
        tbSpeichernButton = new javax.swing.JButton();
        tbAnsichtButton = new javax.swing.JButton();
        tbDruckenButton = new javax.swing.JButton();
        wacMainStatusLabel = new javax.swing.JLabel();
        wacMainProgressBar = new javax.swing.JProgressBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        wacDesktopPane = new javax.swing.JDesktopPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        pbAuslegung = new javax.swing.JButton();
        pbAngebot = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        wacMenuBar = new javax.swing.JMenuBar();
        wacDateiMenu = new javax.swing.JMenu();
        wacDateiNeuMenuItem = new javax.swing.JMenuItem();
        wacDateiOeffnenMenuItem = new javax.swing.JMenuItem();
        wacDateiSchliessenMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        wacDateiSpeichernMenuItem = new javax.swing.JMenuItem();
        wacDateiSpeichernUnterMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        wacDateiSeitenansichtMenuItem = new javax.swing.JMenuItem();
        wacDateiDruckenMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        wacDateiBeendenMenuItem = new javax.swing.JMenuItem();
        wacAngebotMenu = new javax.swing.JMenu();
        wacAngebotNeu = new javax.swing.JMenuItem();
        wacExtrasMenu = new javax.swing.JMenu();
        wacExtrasEinstellungen = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        ExtrasSync = new javax.swing.JMenuItem();
        wacFensterMenu = new javax.swing.JMenu();
        wacHilfeMenu = new javax.swing.JMenu();
        wacHilfeUeberMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("WestaWAC Technische Auslegung");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(java.awt.Color.white);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/WestaWAC.ico.png")).getImage());
        setLocationByPlatform(true);
        setName("wacMainFrame");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        wacToolBar.setFloatable(false);
        wacToolBar.setAlignmentX(0.0F);
        wacToolBar.setBorderPainted(false);
        wacToolBar.setOpaque(false);
        tbNeuButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Neu.png")));
        tbNeuButton.setToolTipText("Neues Projekt");
        tbNeuButton.setBorderPainted(false);
        tbNeuButton.setFocusPainted(false);
        tbNeuButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        wacToolBar.add(tbNeuButton);

        tbOeffnenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Oeffnen.png")));
        tbOeffnenButton.setToolTipText("Projekt \u00f6ffnen");
        tbOeffnenButton.setBorderPainted(false);
        tbOeffnenButton.setFocusPainted(false);
        tbOeffnenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        tbOeffnenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projektLadenAction(evt);
            }
        });

        wacToolBar.add(tbOeffnenButton);

        tbSpeichernButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Speichern.png")));
        tbSpeichernButton.setToolTipText("Projekt speichern");
        tbSpeichernButton.setBorderPainted(false);
        tbSpeichernButton.setEnabled(false);
        tbSpeichernButton.setFocusPainted(false);
        tbSpeichernButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        tbSpeichernButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projektSpeichernAction(evt);
            }
        });

        wacToolBar.add(tbSpeichernButton);

        tbAnsichtButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Ansicht.png")));
        tbAnsichtButton.setToolTipText("Seitenansicht");
        tbAnsichtButton.setBorderPainted(false);
        tbAnsichtButton.setEnabled(false);
        tbAnsichtButton.setFocusPainted(false);
        tbAnsichtButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        tbAnsichtButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbAnsichtButtonActionPerformed(evt);
            }
        });

        wacToolBar.add(tbAnsichtButton);

        tbDruckenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Drucken.png")));
        tbDruckenButton.setToolTipText("Drucken");
        tbDruckenButton.setBorderPainted(false);
        tbDruckenButton.setEnabled(false);
        tbDruckenButton.setFocusPainted(false);
        tbDruckenButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        tbDruckenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbDruckenButtonActionPerformed(evt);
            }
        });

        wacToolBar.add(tbDruckenButton);

        wacMainStatusLabel.setText(" ");

        jSplitPane1.setDividerSize(8);
        jSplitPane1.setOneTouchExpandable(true);
        jScrollPane1.setViewportView(wacDesktopPane);

        jSplitPane1.setRightComponent(jScrollPane1);

        jSplitPane2.setDividerLocation(350);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        pbAuslegung.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbAuslegung.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        pbAngebot.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pbAngebot.setPreferredSize(new java.awt.Dimension(60, 60));
        pbAngebot.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pbAuslegung)
                    .add(pbAngebot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {pbAngebot, pbAuslegung}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(pbAuslegung)
                .add(17, 17, 17)
                .add(pbAngebot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(201, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {pbAngebot, pbAuslegung}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jSplitPane2.setLeftComponent(jPanel1);

        jTextArea1.setEnabled(false);
        jScrollPane3.setViewportView(jTextArea1);

        jSplitPane2.setRightComponent(jScrollPane3);

        jSplitPane1.setLeftComponent(jSplitPane2);

        wacMenuBar.setOpaque(false);
        wacDateiMenu.setMnemonic('l');
        wacDateiMenu.setText("Auslegung");
        wacDateiNeuMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        wacDateiNeuMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Neu.png")));
        wacDateiNeuMenuItem.setMnemonic('N');
        wacDateiNeuMenuItem.setText("Neu");
        wacDateiNeuMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                neuesProjektActionHandler(evt);
            }
        });

        wacDateiMenu.add(wacDateiNeuMenuItem);

        wacDateiOeffnenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        wacDateiOeffnenMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Oeffnen.png")));
        wacDateiOeffnenMenuItem.setMnemonic('f');
        wacDateiOeffnenMenuItem.setText("\u00d6ffnen...");
        wacDateiOeffnenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projektLadenAction(evt);
            }
        });

        wacDateiMenu.add(wacDateiOeffnenMenuItem);

        wacDateiSchliessenMenuItem.setMnemonic('l');
        wacDateiSchliessenMenuItem.setText("Schlie\u00dfen");
        wacDateiSchliessenMenuItem.setEnabled(false);
        wacDateiSchliessenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projektSchliessenAction(evt);
            }
        });

        wacDateiMenu.add(wacDateiSchliessenMenuItem);

        wacDateiMenu.add(jSeparator1);

        wacDateiSpeichernMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        wacDateiSpeichernMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Speichern.png")));
        wacDateiSpeichernMenuItem.setText("Speichern");
        wacDateiSpeichernMenuItem.setEnabled(false);
        wacDateiSpeichernMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projektSpeichernAction(evt);
            }
        });

        wacDateiMenu.add(wacDateiSpeichernMenuItem);

        wacDateiSpeichernUnterMenuItem.setText("Speichern unter...");
        wacDateiSpeichernUnterMenuItem.setEnabled(false);
        wacDateiSpeichernUnterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projektSpeichernUnterAction(evt);
            }
        });

        wacDateiMenu.add(wacDateiSpeichernUnterMenuItem);

        wacDateiMenu.add(jSeparator2);

        wacDateiSeitenansichtMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Ansicht.png")));
        wacDateiSeitenansichtMenuItem.setText("Seitenansicht");
        wacDateiSeitenansichtMenuItem.setEnabled(false);
        wacDateiSeitenansichtMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbAnsichtButtonActionPerformed(evt);
            }
        });

        wacDateiMenu.add(wacDateiSeitenansichtMenuItem);

        wacDateiDruckenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        wacDateiDruckenMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/westaflex/resource/menu/Menu_Drucken.png")));
        wacDateiDruckenMenuItem.setText("Drucken...");
        wacDateiDruckenMenuItem.setEnabled(false);
        wacDateiDruckenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wacDateiDruckenMenuItemActionPerformed(evt);
            }
        });

        wacDateiMenu.add(wacDateiDruckenMenuItem);

        wacDateiMenu.add(jSeparator3);

        wacDateiBeendenMenuItem.setText("Beenden");
        wacDateiBeendenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wacDateiBeendenMenuItemActionPerformed(evt);
            }
        });

        wacDateiMenu.add(wacDateiBeendenMenuItem);

        wacMenuBar.add(wacDateiMenu);

        wacAngebotMenu.setMnemonic('a');
        wacAngebotMenu.setText("Angebot");
        wacAngebotNeu.setText("Item");
        wacAngebotMenu.add(wacAngebotNeu);

        wacMenuBar.add(wacAngebotMenu);

        wacExtrasMenu.setMnemonic('x');
        wacExtrasMenu.setText("Extras");
        wacExtrasEinstellungen.setText("Einstellungen");
        wacExtrasEinstellungen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wacExtrasEinstellungenActionPerformed(evt);
            }
        });

        wacExtrasMenu.add(wacExtrasEinstellungen);

        wacExtrasMenu.add(jSeparator4);

        ExtrasSync.setText("Textserver synchronisieren...");
        ExtrasSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExtrasSyncActionPerformed(evt);
            }
        });

        wacExtrasMenu.add(ExtrasSync);

        wacMenuBar.add(wacExtrasMenu);

        wacFensterMenu.setMnemonic('F');
        wacFensterMenu.setText("Fenster");
        wacMenuBar.add(wacFensterMenu);

        wacHilfeMenu.setMnemonic('H');
        wacHilfeMenu.setText("Hilfe");
        wacHilfeUeberMenuItem.setText("\u00dcber...");
        wacHilfeUeberMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeigeUeberPanelAction(evt);
            }
        });

        wacHilfeMenu.add(wacHilfeUeberMenuItem);

        wacMenuBar.add(wacHilfeMenu);

        setJMenuBar(wacMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(wacToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1085, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(wacMainStatusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wacMainProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1065, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(wacToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(wacMainProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(wacMainStatusLabel))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ExtrasSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExtrasSyncActionPerformed

        if (Tools.askbox(Strings.KILL_ALL_OOFFICE) == Tools.OPTION_OK) {
            try {
                if (System.getProperty("os.name").contains("Windows")) {
                    Runtime.getRuntime().exec("taskkill /F /S localhost /U " + System.getProperty("user.name") + " /IM soffice*");
                } else {
                    Runtime.getRuntime().exec("killall -u " + System.getProperty("user.name") + " soffice");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_ExtrasSyncActionPerformed

    private void wacDateiDruckenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wacDateiDruckenMenuItemActionPerformed
        tbDruckenButtonActionPerformed(evt);
    }//GEN-LAST:event_wacDateiDruckenMenuItemActionPerformed

    private void wacExtrasEinstellungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wacExtrasEinstellungenActionPerformed
        new SettingsDialog(this, true).setVisible(true);
    }//GEN-LAST:event_wacExtrasEinstellungenActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        shutdown();
    }//GEN-LAST:event_formWindowClosing

    private void tbDruckenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbDruckenButtonActionPerformed

        JInternalFrame f = null;
        PropertyValue[] loadProps = new PropertyValue[1];

        f = wacDesktopPane.getSelectedFrame();

        if (f instanceof ProjectInternalFrame) {
            printDialog.setVisible(true);
            if (printDialog.getRC() == true) {
                XComponentContext xContext;
                try {
                    xContext = Bootstrap.bootstrap();

                    Object d = xContext.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
                    XComponentLoader xLoadable = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, d);
                    loadProps[0] = new PropertyValue();
                    loadProps[0].Name = "Hidden";
                    loadProps[0].Value = new Boolean(true);
                    XComponent comp = xLoadable.loadComponentFromURL(getClass().getResource("/com/westaflex/resource/WestaWAC.ott").toString().replaceFirst(":/", ":///"), "_blank", 0, loadProps);

                    OfficeDocument oDoc = new OfficeDocument(
                            (com.sun.star.frame.XModel) UnoRuntime.queryInterface(
                            com.sun.star.frame.XModel.class, comp));
                    ((ProjectInternalFrame) wacDesktopPane.getSelectedFrame()).setOfficeDocument(oDoc);
                    ((ProjectInternalFrame) wacDesktopPane.getSelectedFrame()).toDocument(oDoc);

                    XDocumentInfoSupplier xds = (XDocumentInfoSupplier) UnoRuntime.queryInterface(XDocumentInfoSupplier.class, oDoc);
                    XDocumentInfo xp = xds.getDocumentInfo();
                    XPropertySet xps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xp);
                    DateTime dt = new DateTime();
                    Calendar c = Calendar.getInstance();
                    dt.Day = (short) c.get(Calendar.DAY_OF_MONTH);
                    dt.Month = (short) ((c.get(Calendar.MONTH) + 1));
                    dt.Year = (short) c.get(Calendar.YEAR);
                    System.out.println("dt" + dt);
                    xps.setPropertyValue("CreationDate", dt);
                    XRefreshable xr = (XRefreshable) UnoRuntime.queryInterface(XRefreshable.class, oDoc);
                    xr.refresh();

                    printDialog.setControlSwitches(oDoc);

//                    PropertyValue[] printProps = new PropertyValue[1];
                    XPrintable xPrintable = (XPrintable) UnoRuntime.queryInterface(XPrintable.class, oDoc);
                    PropertyValue[] printerDesc = new PropertyValue[1];
                    printerDesc[0] = new PropertyValue();
                    printerDesc[0].Name = "Name";
                    printerDesc[0].Value = printDialog.getPrinterName();
                    xPrintable.setPrinter(printerDesc);

                    printerDesc[0].Name = "CopyCount";
                    printerDesc[0].Value = printDialog.getCopyCount();
                    oDoc.print(printerDesc);

                } catch (BootstrapException ex) {
                    ex.printStackTrace();
                } catch (com.sun.star.uno.Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else if (f instanceof OOoInternalFrame) {
            new SeeDocBridge().print(((OOoInternalFrame) f).getDocument());
        } else {
            Tools.msgbox(Strings.NOTHING_TO_PRINT);
        }

    }//GEN-LAST:event_tbDruckenButtonActionPerformed

    private void wacDateiBeendenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wacDateiBeendenMenuItemActionPerformed
        shutdown();
    }//GEN-LAST:event_wacDateiBeendenMenuItemActionPerformed

    private void tbAnsichtButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbAnsichtButtonActionPerformed
        // Statusbar und Progressbar
        wacMainStatusLabel.setText(String.format(
                Strings.LOADING_PAGE_PREVIEW_FOR,
                wacDesktopPane.getSelectedFrame().getTitle()));
        wacMainProgressBar.setIndeterminate(true);
//        new ProjektSeitenansichtSwingWorker().start();
        new ProjektSeitenansicht().execute();
    }//GEN-LAST:event_tbAnsichtButtonActionPerformed

    private void projektSchliessenAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projektSchliessenAction

        ProjectInternalFrame p =
                (ProjectInternalFrame) wacDesktopPane.getSelectedFrame();

        if (p != null) {
            wacDesktopPane.getDesktopManager().closeFrame(p);
            removeProject(p);
        }

    }//GEN-LAST:event_projektSchliessenAction

    private void projektSpeichernUnterAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projektSpeichernUnterAction

        ProjectInternalFrame p =
                (ProjectInternalFrame) wacDesktopPane.getSelectedFrame();

        if (p != null) {
            p.setWpxFile(null);
            p.saveProject();
        }

    }//GEN-LAST:event_projektSpeichernUnterAction

    private void projektSpeichernAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projektSpeichernAction

        ProjectInternalFrame p =
                (ProjectInternalFrame) wacDesktopPane.getSelectedFrame();

        if (p != null) {
            p.saveProject();
        }

    }//GEN-LAST:event_projektSpeichernAction

    private void projektLadenAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projektLadenAction
        String projectLoadDirectory = applHelper.getPreference(WestaWacApplHelper.PROJECT_LOAD_DIRECTORY);

        File f = Tools.getSingleFile(this, projectLoadDirectory);
        if (f != null) {
//            System.out.println("vor 1");
            new ProjektLadenSwingWorker(f).execute();
//            System.out.println("nach 1");
        }
    }//GEN-LAST:event_projektLadenAction

    private void zeigeUeberPanelAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeigeUeberPanelAction

        // About
        if (aboutPanel == null) {

            aboutPanel = new JDialog(this, Strings.PROGRAM_TITLE, true);
            aboutPanel.setLayout(new BorderLayout());
            aboutPanel.add(new JLabel(Strings.VERSIONSTRING), BorderLayout.NORTH);
            aboutPanel.add(new JLabel("(C) 2007 - 2009 Oliver Seebass"), BorderLayout.SOUTH);
            aboutPanel.add(new JLabel("ooc: " + System.getenv("ooc")), BorderLayout.SOUTH);
            aboutPanel.add(new JLabel(new ImageIcon(getClass().getResource("/com/westaflex/resource/WestaWAC.png"))), BorderLayout.CENTER);
            aboutPanel.pack();
            Tools.centerComponent(aboutPanel);
        }
        aboutPanel.setVisible(true);

    }//GEN-LAST:event_zeigeUeberPanelAction

    /**
     *
     * @param evt
     */
    private void neuesProjektActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_neuesProjektActionHandler
        new NeuesProjektSwingWorker().start();
    }//GEN-LAST:event_neuesProjektActionHandler

    /**
     *
     */
    public void updateFensterMenu() {
        // Menu "Fenster" neu erstellen
        wacFensterMenu = desktopUtil.generateInternalFrameMenu(
                wacMenuBar, wacFensterMenu);
        updateMenuState();
    }

    public void updateMenuState() {
        boolean b;

        b = !projects.isEmpty();
        wacDateiSchliessenMenuItem.setEnabled(b);
        wacDateiSpeichernMenuItem.setEnabled(b);
        wacDateiSpeichernUnterMenuItem.setEnabled(b);
        wacDateiSeitenansichtMenuItem.setEnabled(b);
        wacDateiDruckenMenuItem.setEnabled(b);
        tbSpeichernButton.setEnabled(b);
        tbAnsichtButton.setEnabled(b);
        tbDruckenButton.setEnabled(b);
    }

    /**
     * Projekt aus der Liste aller Projekte entfernen
     *
     * @param projectInternalFrame
     */
    public void removeProject(ProjectInternalFrame projectInternalFrame) {
        projects.remove(projectInternalFrame);
        updateFensterMenu();
    }

    /**
     *
     * @param projectInternalFrame
     * @return
     */
    public ProjektSpeichernSwingWorker getProjektSpeichernSwingWorker(
            ProjectInternalFrame projectInternalFrame) {

        return new ProjektSpeichernSwingWorker(projectInternalFrame);

    }

    /**
     *
     */
    private void shutdown() {

//        closeAllInternalFrames();

//        // ShutdownObserver informieren
        fireShutdownEvent();

        if (wacDesktopPane.getAllFrames().length == 0) {

            // Datenbank schließen
            WestaDB.getInstance().close();
            try {

                OOoBridge.getInstance().close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Hauptfenster schliessen
            dispose();

            // JVM beenden
            System.exit(0);
        } else {
            Tools.msgbox(Strings.CANNOT_CLOSE_DUE_TO_OPEN_WINDOWS);
        }
    }

    /**
     *
     * @param shutdownObserver
     */
    @Override
    public void registerShutdownObserver(ShutdownObserver shutdownObserver) {
        shutdownObserverList.add(shutdownObserver);
    }

    /**
     *
     * @param shutdownObserver
     */
    @Override
    public void unregisterShutdownObserver(ShutdownObserver shutdownObserver) {
        shutdownObserverList.remove(shutdownObserver);
    }

    /**
     *
     */
    @Override
    public void fireShutdownEvent() {
        Object so[] = null;

        so = shutdownObserverList.toArray();
        for (Object s : so) {
            ((ShutdownObserver) s).processShutdown(this);
        }
    }

    private PrintDialog printDialog = null;

    private WestaWacApplHelper applHelper;

    private List<ProjectInternalFrame> projects;

    private JDesktopUtil desktopUtil;

    private JDialog aboutPanel;

    private List<ShutdownObserver> shutdownObserverList;

    public void setStatusText(Object... action) {
        Stack textStack = new Stack();
        Object text;

        if (action.length > 0) {
            text = action[0];
            textStack.push(text);
            if (action.length > 1) {
                setProgressBar((Boolean) action[1]);
            }
        } else {
            if (!textStack.empty()) {
                text = textStack.pop();
            } else {
                text = Strings.READY;
            }
            setProgressBar(false);
        }
        wacMainStatusLabel.setText((String) text);
    }

    public void setProgressBar(boolean on) {
        wacMainProgressBar.setIndeterminate(on);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ExtrasSync;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton pbAngebot;
    private javax.swing.JButton pbAuslegung;
    private javax.swing.JButton tbAnsichtButton;
    private javax.swing.JButton tbDruckenButton;
    private javax.swing.JButton tbNeuButton;
    private javax.swing.JButton tbOeffnenButton;
    private javax.swing.JButton tbSpeichernButton;
    private javax.swing.JMenu wacAngebotMenu;
    private javax.swing.JMenuItem wacAngebotNeu;
    private javax.swing.JMenuItem wacDateiBeendenMenuItem;
    private javax.swing.JMenuItem wacDateiDruckenMenuItem;
    private javax.swing.JMenu wacDateiMenu;
    private javax.swing.JMenuItem wacDateiNeuMenuItem;
    private javax.swing.JMenuItem wacDateiOeffnenMenuItem;
    private javax.swing.JMenuItem wacDateiSchliessenMenuItem;
    private javax.swing.JMenuItem wacDateiSeitenansichtMenuItem;
    private javax.swing.JMenuItem wacDateiSpeichernMenuItem;
    private javax.swing.JMenuItem wacDateiSpeichernUnterMenuItem;
    private javax.swing.JDesktopPane wacDesktopPane;
    private javax.swing.JMenuItem wacExtrasEinstellungen;
    private javax.swing.JMenu wacExtrasMenu;
    private javax.swing.JMenu wacFensterMenu;
    private javax.swing.JMenu wacHilfeMenu;
    private javax.swing.JMenuItem wacHilfeUeberMenuItem;
    protected javax.swing.JProgressBar wacMainProgressBar;
    private javax.swing.JLabel wacMainStatusLabel;
    private javax.swing.JMenuBar wacMenuBar;
    private javax.swing.JToolBar wacToolBar;
    // End of variables declaration//GEN-END:variables
}
