/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.plaf.basic.CalendarHeaderHandler;
import org.jdesktop.swingx.plaf.basic.SpinningCalendarHeaderHandler;

import fr.spi4j.ui.HasDate_Itf;
import fr.spi4j.ui.HasMandatory_Itf;
import fr.spi4j.ui.swing.SpiPanel;

/**
 * Champs de saisie d'une date (type java.util.Date) avec calendrier.
 * @author MINARM
 */
public class SpiDatePanel extends SpiPanel implements HasDate_Itf, HasMandatory_Itf
{
   private static final long serialVersionUID = 1L;

   private static final int c_marginToField = 20;

   private static final ImageIcon c_calendarIcon = new ImageIcon(
            SpiDatePanel.class.getResource("/icons/Calendar16.gif"));

   // on utilise pour l'instant une monthView statique pour économiser la mémoire,
   // en conséquence ce monthView n'est pas personnalisable pour mettre flaggedDates, selectionMode, lowerBound, upperBound ou autres propriétés
   // (sinon il faudrait instancier un monthView pour chaque datePanel, et à la demande et non dans le constructeur du datePanel)
   static
   {
      // activation du spinner sur les années dans JXMonthView (doit être fait avant l'instanciation du JXMonthView et nécessite zoomable=true)
      UIManager.put(CalendarHeaderHandler.uiControllerID, SpinningCalendarHeaderHandler.class.getName());
      UIManager.put(SpinningCalendarHeaderHandler.ARROWS_SURROUND_MONTH, Boolean.TRUE);
   }

   private static final JXMonthView c_monthView = new JXMonthView();
   static
   {
      // initialisation statique du monthView
      // c_monthView.setTraversable(true);
      // zoomable et pas uniquement traversable pour avoir le spinner sur les années
      c_monthView.setZoomable(true);
      c_monthView.setComponentInputMapEnabled(true);
      c_monthView.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
               BorderFactory.createEmptyBorder(5, 5, 5, 5)));
      c_monthView.setSelectionModel(new SingleDaySelectionModel());
      c_monthView.setTodayBackground(Color.BLUE.darker());
      c_monthView.setDayForeground(Calendar.SUNDAY, Color.GRAY);
      c_monthView.setDayForeground(Calendar.SATURDAY, Color.GRAY);
   }

   private static final ActionListener c_actionHandler = new ActionListener()
   {
      @Override
      public void actionPerformed (final ActionEvent actionEvent)
      {
         final Object source = actionEvent.getSource();
         if (source instanceof JButton && ((JButton) source).getParent() instanceof SpiDatePanel)
         {

            final SpiDatePanel datePanel = (SpiDatePanel) ((JButton) source).getParent();
            datePanel.chooseDate();
         }
      }
   };

   private final SpiDateField _dateField = new SpiDateField();

   private final JButton _calendarButton = new JButton(null, c_calendarIcon);

   private Date lowerCalendarBound;

   private Date upperCalendarBound;

   /**
    * Constructeur.
    */
   public SpiDatePanel ()
   {
      super(null); // layout : aucun

      _calendarButton.setBorder(null);
      _calendarButton.setContentAreaFilled(false);
      _calendarButton.setFocusable(false);
      _calendarButton.addActionListener(c_actionHandler);

      setOpaque(false);
      setSize(130, 20);
      // taille par défaut pour un DatePanel
      // note : setSize appelle reshape (surchargé)
      add(_dateField);
      add(_calendarButton);
   }

   @Override
   public Date getValue ()
   {
      return _dateField.getValue();
   }

   @Override
   public void setValue (final Date p_newDate)
   {
      _dateField.setValue(p_newDate);
   }

   /**
    * Définit la valeur de la propriété date avec la date du jour.
    * @see #setValue
    */
   public void setNow ()
   {
      _dateField.setNow();
   }

   /**
    * Retourne la valeur de la propriété endOfDay.
    * @return boolean
    * @see #setEndOfDay
    */
   public boolean isEndOfDay ()
   {
      return _dateField.isEndOfDay();
   }

   /**
    * Définit la valeur de la propriété endOfDay.
    * @param newEndOfDay
    *           boolean
    * @see #isEndOfDay
    */
   public void setEndOfDay (final boolean newEndOfDay)
   {
      _dateField.setEndOfDay(newEndOfDay);
   }

   @Override
   public void setEnabled (final boolean newEnabled)
   {
      super.setEnabled(newEnabled);
      _dateField.setEditable(newEnabled);
      _calendarButton.setEnabled(newEnabled);
   }

   /**
    * Appelle setEnabled.
    * @param newEditable
    *           boolean
    */
   public void setEditable (final boolean newEditable)
   {
      setEnabled(newEditable);
   }

   /**
    * Retourne la date minimum de la popup calendrier.
    * @return Date
    */
   public Date getLowerCalendarBound ()
   {
      return lowerCalendarBound;
   }

   /**
    * Définit la date minimum de la popup calendrier.
    * @param p_lowerCalendarBound
    *           Date
    */
   public void setLowerCalendarBound (final Date p_lowerCalendarBound)
   {
      this.lowerCalendarBound = p_lowerCalendarBound;
   }

   /**
    * Retourne la date maximum de la popup calendrier.
    * @return Date
    */
   public Date getUpperCalendarBound ()
   {
      return upperCalendarBound;
   }

   /**
    * Définit la date maximum de la popup calendrier.
    * @param p_upperCalendarBound
    *           Date
    */
   public void setUpperCalendarBound (final Date p_upperCalendarBound)
   {
      this.upperCalendarBound = p_upperCalendarBound;
   }

   /**
    * @return Composant SpiDateField interne à ce SpiDatePanel
    */
   public SpiDateField getDateField ()
   {
      return _dateField;
   }

   /**
    * @return Composant calendarButton interne à ce SpiDatePanel
    */
   public JButton getCalendarButton ()
   {
      return _calendarButton;
   }

   /**
    * @return Instance statique du JXMonthView de SwingX.
    */
   protected static JXMonthView getMonthView ()
   {
      return c_monthView;
   }

   /**
    * Retourne la liste des jours flaggés dans la popup calendrier (par exemple les jours fériés).
    * @return Set
    */
   public static Set<Date> getFlaggedDates ()
   {
      return c_monthView.getFlaggedDates();
   }

   /**
    * Définit la liste des jours flaggés dans la popup calendrier (par exemple les jours fériés).
    * @param flaggedDates
    *           Set
    */
   public static void setFlaggedDates (final Set<Date> flaggedDates)
   {
      c_monthView.setFlaggedDates(flaggedDates.toArray(new Date[flaggedDates.size()]));
   }

   /**
    * {@inheritDoc}
    * @deprecated Ne pas appeler cette méthode directement, appeler si besoin: <code>setBounds(int, int, int, int)</code>.
    */
   @Deprecated
   @Override
   public void reshape (final int x, final int y, final int width, final int height)
   {
      // Surcharge de reshape pour définir les positions et tailles du field et du bouton (toujours alignés à droite quelle que soit la taille).
      // Note : un componentListener ne sert pas avant que
      // le composant ait un parent, cela ne conviendrait donc pas ici.
      super.reshape(x, y, width, height);
      _dateField.setBounds(0, 0, width - c_marginToField, height);
      _calendarButton.setBounds(width - c_marginToField + 2, 2, c_marginToField - 2, height - 2);
   }

   /**
    * Ouvre la popup du calendrier (la dialog en retour de méthode est déjà affichée et il est donc en général inutile d'utiliser le résultat de cette méthode).
    * @return JDialog
    */
   public JDialog chooseDate ()
   {
      // positionne la date dans le calendrier
      final Date v_date = this.getValue();
      c_monthView.setSelectionDate(v_date);
      if (v_date != null)
      {
         c_monthView.ensureDateVisible(v_date);
      }
      else
      {
         c_monthView.ensureDateVisible(new Date());
      }
      c_monthView.setLowerBound(lowerCalendarBound);
      c_monthView.setUpperBound(upperCalendarBound);

      // crée la boîte de dialogue non modale, non décorée, en y ajoutant le monthView et en mettant la boîte de dialogue à la bonne position et à la bonne taille
      final JDialog v_dialog = createCalendarDialog();

      // ajoute un listener pour enregister la valeur si l'utilisateur sélectionne un jour dans le calendrier puis pour fermer la boîte de dialogue
      final ActionListener v_actionListener = new ActionListener()
      {
         @Override
         public void actionPerformed (final ActionEvent e)
         {
            try
            {
               if (JXMonthView.COMMIT_KEY.equals(e.getActionCommand()))
               {
                  setValueUsingCalendar(c_monthView.getSelectionDate());
               }
               // else if (JXMonthView.CANCEL_KEY.equals(e.getActionCommand())) { }
            }
            finally
            {
               c_monthView.removeActionListener(this);
               v_dialog.dispose();
               v_dialog.getContentPane().removeAll();
            }
         }
      };
      c_monthView.addActionListener(v_actionListener);

      // ajoute un listener pour fermer la boîte de dialogue si elle perd le focus (en cliquant à côté avec la souris par exemple)
      final WindowAdapter windowAdapter = new WindowAdapter()
      {
         @Override
         public void windowLostFocus (final WindowEvent evt)
         {
            c_monthView.removeActionListener(v_actionListener);
            v_dialog.dispose();
         }
      };
      v_dialog.addWindowFocusListener(windowAdapter);

      // affiche la boîte de dialogue
      v_dialog.setVisible(true);
      return v_dialog;
   }

   /**
    * Crée la boîte de dialogue pour le calendrier.
    * @return JDialog
    */
   private JDialog createCalendarDialog ()
   {
      final Window v_window = SwingUtilities.windowForComponent(this);
      final JDialog v_dialog;
      if (v_window instanceof Frame)
      {
         v_dialog = new JDialog((Frame) v_window);
      }
      else if (v_window instanceof Dialog)
      {
         v_dialog = new JDialog((Dialog) v_window);
      }
      else
      {
         v_dialog = new JDialog((Frame) null);
      }
      v_dialog.setModal(false);
      v_dialog.setUndecorated(true);
      v_dialog.getContentPane().removeAll();
      v_dialog.getContentPane().add(c_monthView);
      v_dialog.pack(); // ce pack fait un setLocationRelativeTo(getOwner()) ce qui n'est pas terrible
      v_dialog.setResizable(false);
      if (this.isShowing())
      {
         Point location = this.getLocationOnScreen();
         // anciennement: location.translate(this.getWidth() - v_dialog.getWidth(), this.getHeight() + 1);
         location.translate(this.getWidth(), 1);
         location = adjustPopupLocationToFitScreen(location.x, location.y, v_dialog);
         v_dialog.setLocation(location);
      }
      else
      {
         v_dialog.setLocationRelativeTo(this);
      }
      return v_dialog;
   }

   /**
    * Returns an point which has been adjusted to take into account of the desktop bounds, taskbar and multi-monitor configuration. (from JPopupMenu)
    * @param xposition
    *           int
    * @param yposition
    *           int
    * @param p_window
    *           boîte de dialogue
    * @return Point
    */
   private Point adjustPopupLocationToFitScreen (final int xposition, final int yposition, final Window p_window)
   {
      final Point p = new Point(xposition, yposition);

      if (GraphicsEnvironment.isHeadless())
      {
         return p;
      }

      final Toolkit toolkit = Toolkit.getDefaultToolkit();
      Rectangle screenBounds;
      Insets screenInsets;
      GraphicsConfiguration gc = null;
      // Try to find GraphicsConfiguration, that includes mouse
      // pointer position
      final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      final GraphicsDevice[] gd = ge.getScreenDevices();
      for (final GraphicsDevice element : gd)
      {
         if (element.getType() == GraphicsDevice.TYPE_RASTER_SCREEN)
         {
            final GraphicsConfiguration dgc = element.getDefaultConfiguration();
            if (dgc.getBounds().contains(p))
            {
               gc = dgc;
               break;
            }
         }
      }

      // If not found and we have an owner, ask owner about his gc
      if (gc == null && p_window.getOwner() != null)
      {
         gc = p_window.getOwner().getGraphicsConfiguration();
      }

      if (gc != null)
      {
         // If we have GraphicsConfiguration use it to get
         // screen bounds and insets
         screenInsets = toolkit.getScreenInsets(gc);
         screenBounds = gc.getBounds();
      }
      else
      {
         // If we don't have GraphicsConfiguration use primary screen
         // and empty insets
         screenInsets = new Insets(0, 0, 0, 0);
         screenBounds = new Rectangle(toolkit.getScreenSize());
      }

      final int scrWidth = screenBounds.width - Math.abs(screenInsets.left + screenInsets.right);
      final int scrHeight = screenBounds.height - Math.abs(screenInsets.top + screenInsets.bottom);

      Dimension size;

      size = p_window.getPreferredSize();

      if (p.x + size.width > screenBounds.x + scrWidth)
      {
         p.x = screenBounds.x + scrWidth - size.width;
      }

      if (p.y + size.height > screenBounds.y + scrHeight)
      {
         p.y = screenBounds.y + scrHeight - size.height;
      }

      /*
       * Change is made to the desired (X,Y) values, when the Popup is too tall OR too wide for the screen
       */
      if (p.x < screenBounds.x)
      {
         p.x = screenBounds.x;
      }
      if (p.y < screenBounds.y)
      {
         p.y = screenBounds.y;
      }

      return p;
   }

   /**
    * Positionnement de la valeur à partir du calendrier.
    * @param p_newDate
    *           Date
    */
   protected void setValueUsingCalendar (final Date p_newDate)
   {
      setValue(p_newDate);
   }

   @Override
   public boolean isMandatory ()
   {
      return _dateField.isMandatory();
   }

   @Override
   public void setMandatory (final boolean p_mandatory)
   {
      _dateField.setMandatory(p_mandatory);
   }

   @Override
   public void setName (final String name)
   {
      super.setName(name);
      getDateField().setName(name + ".dateField");
      getCalendarButton().setName(name + ".calendarButton");
   }
}
