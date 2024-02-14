/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * Dialogue d'affichage d'exception.
 * @author MINARM
 */
public class SpiErrorDialog extends JDialog
{
   private static final long serialVersionUID = 1L;

   private static final int c_width = 500;

   private static final int c_minHeight = 120;

   private static JLabel lbMessage;

   private static JButton btDetails;

   private static JButton btClose;

   private static JTextArea txtStackTrace;

   private static JButton btCopy;

   private static JPanel panelCopy;

   private static JScrollPane stackTraceScroll;

   private static SpiErrorDialog dialogInstance;

   private static Container contentPaneInstance;

   // initialisé à false
   private boolean _detailsOpened;

   private int _openedHeight = 500;

   private int _closedHeight = 120;

   /**
    * Construction privé d'une dialogue d'affichage d'erreur.
    */
   protected SpiErrorDialog ()
   {
      super();
      setTitle("Erreur");
      setModal(true);
      setMinimumSize(new Dimension(c_width, c_minHeight));
      // pas besoin car c'est mieux avec le pack: setPreferredSize(new Dimension(c_width, _closedHeight));
   }

   /**
    * @return l'instance du content pane (static) d'une {@link SpiErrorDialog}
    */
   private static synchronized Container getContentPaneInstance ()
   {
      if (contentPaneInstance == null)
      {
         final JPanel v_contentPane = new JPanel();
         v_contentPane.setLayout(new BorderLayout(5, 5));
         final Icon _icon = UIManager.getIcon("OptionPane.errorIcon"); // DefaultLookup.get((JComponent) getContentPane(), (ComponentUI) null, "OptionPane.errorIcon");
         final JLabel v_lbIcon = new JLabel(_icon);
         v_lbIcon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
         final JPanel v_panelIcon = new JPanel();
         v_panelIcon.setAlignmentY(TOP_ALIGNMENT);
         v_panelIcon.add(v_lbIcon);
         v_contentPane.add(v_panelIcon, BorderLayout.WEST);

         final JPanel v_content = new JPanel(new BorderLayout(5, 5));

         lbMessage = new JLabel();
         final JPanel v_panelMessage = new JPanel();
         v_panelMessage.add(lbMessage);
         v_panelMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
         v_content.add(v_panelMessage, BorderLayout.NORTH);

         final JPanel v_panelDetails = new JPanel();
         v_panelDetails.setLayout(new BorderLayout());
         btClose = new JButton("Fermer");
         btDetails = new JButton("Détails >>");
         final JPanel v_panelBtDetails = new JPanel();
         v_panelBtDetails.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
         v_panelBtDetails.add(btClose);
         v_panelBtDetails.add(btDetails);
         v_panelDetails.add(v_panelBtDetails, BorderLayout.NORTH);
         txtStackTrace = new JTextArea();
         txtStackTrace.setFont(txtStackTrace.getFont().deriveFont(11f));
         txtStackTrace.setEditable(false);
         stackTraceScroll = new JScrollPane(txtStackTrace);
         // v_stackTraceScroll.setMinimumSize(new Dimension(c_width - 10, c_stackHeight));
         stackTraceScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
         v_panelDetails.add(stackTraceScroll);

         panelCopy = new JPanel();
         panelCopy.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
         btCopy = new JButton("Copier dans le presse papier");
         panelCopy.add(btCopy);
         v_panelDetails.add(panelCopy, BorderLayout.SOUTH);

         v_content.add(v_panelDetails);

         v_contentPane.add(v_content);

         btDetails.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed (final ActionEvent p_event)
            {
               dialogInstance._detailsOpened = !dialogInstance._detailsOpened;
               if (dialogInstance._detailsOpened)
               {
                  openDetails();
               }
               else
               {
                  closeDetails();
               }
            }
         });

         btClose.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed (final ActionEvent p_event)
            {
               dialogInstance.dispose();
            }
         });

         btCopy.addActionListener(new ActionListener()
         {
            @Override
            public void actionPerformed (final ActionEvent p_event)
            {
               final int v_caretPosition = txtStackTrace.getCaretPosition();
               txtStackTrace.selectAll();
               txtStackTrace.copy();
               txtStackTrace.setCaretPosition(v_caretPosition);
            }
         });

         v_contentPane.addKeyListener(new KeyAdapter()
         {
            @Override
            public void keyReleased (final KeyEvent e)
            {
               if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
               {
                  dialogInstance.dispose();
               }
            }
         });

         contentPaneInstance = v_contentPane;
      }
      dialogInstance._detailsOpened = false;
      btDetails.setText("Détails >>");
      stackTraceScroll.setVisible(false);
      panelCopy.setVisible(false);
      return contentPaneInstance;
   }

   /**
    * Ouverture des détails.
    */
   private static void openDetails ()
   {
      dialogInstance._closedHeight = dialogInstance.getHeight();
      btDetails.setText("Détails <<");
      stackTraceScroll.setVisible(true);
      panelCopy.setVisible(true);
      dialogInstance.setSize(new Dimension(dialogInstance.getWidth(), dialogInstance._openedHeight));
   }

   /**
    * Fermeture des détails.
    */
   private static void closeDetails ()
   {
      dialogInstance._openedHeight = dialogInstance.getHeight();
      btDetails.setText("Détails >>");
      stackTraceScroll.setVisible(false);
      panelCopy.setVisible(false);
      dialogInstance.setSize(new Dimension(dialogInstance.getWidth(), dialogInstance._closedHeight));
   }

   /**
    * Affichage de la stacktrace dans le text area.
    * @param p_e
    *           l'exception
    */
   private void fillDetails (final Throwable p_e)
   {
      final StringWriter v_stringWriter = new StringWriter();
      p_e.printStackTrace(new PrintWriter(v_stringWriter));
      final String v_openingStackTrace = v_stringWriter.toString().replaceAll("\t", "      ");

      final StringBuilder v_builder = new StringBuilder(v_openingStackTrace);
      v_builder.append("\n\nErreur intervenue ");
      final SimpleDateFormat v_sdf = new SimpleDateFormat("'le 'dd/MM/yyyy' à 'HH:mm:ss");
      v_builder.append(v_sdf.format(new Date()));
      v_builder.append(" pour ").append(System.getProperty("user.name"));

      txtStackTrace.setText(v_builder.toString());

      txtStackTrace.setCaretPosition(0);

   }

   /**
    * Affiche la dialogue d'erreur.
    */
   public void display ()
   {
      setLocationRelativeTo(null);
      pack();
      setVisible(true);
   }

   /**
    * Affiche une boite de dialogue modale indiquant en détails la trace de l'erreur.
    * @param p_e
    *           l'erreur qui a été interceptée
    */
   public static void showError (final Throwable p_e)
   {
      final SpiErrorDialog v_dialog = new SpiErrorDialog();
      dialogInstance = v_dialog;
      v_dialog.setContentPane(getContentPaneInstance());
      v_dialog.fillDetails(p_e);
      if (p_e.getMessage() != null)
      {
         lbMessage.setText("<html>" + p_e.getMessage().replace("\n", "<br/>") + "</html>");
      }
      else
      {
         lbMessage.setText(p_e.toString());
      }
      v_dialog.display();
   }

}
