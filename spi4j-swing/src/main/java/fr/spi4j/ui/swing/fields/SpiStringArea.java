/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;

import javax.swing.FocusManager;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import fr.spi4j.ui.HasMaxLength_Itf;
import fr.spi4j.ui.HasString_Itf;
import fr.spi4j.ui.swing.SpiSwingUtilities;

/**
 * Champs de saisie d'un texte long (type String avec \n).
 * @author MINARM
 */
public class SpiStringArea extends JScrollPane implements HasString_Itf, HasMaxLength_Itf
{
   private static final int STRING_AREA_DEFAULT_MAX_LENGTH = 4000;

   private static final long serialVersionUID = 1L;

   // ces gestionnaires d'événements sont statiques pour économie mémoire
   // l'instance est déduite à partir de la source de l'événement
   private static final FocusHandler FOCUS_HANDLER = new FocusHandler();

   private static final KeyHandler KEY_HANDLER = new KeyHandler();

   private JTextArea textArea;

   static
   {
      final String lookAndFeelName = UIManager.getLookAndFeel().getName();
      if (lookAndFeelName.startsWith("Windows") || lookAndFeelName.startsWith("Metal"))
      {
         // nécessaire pour Look and Feel Windows et Metal
         UIManager.put("TextArea.inactiveBackground", UIManager.get("TextField.inactiveBackground"));
      }
      else
      {
         // nécessaire pour Looks and Feel Nimbus
         UIManager.put("TextArea.inactiveBackground", new Color(240, 240, 240));
      }
   }

   /**
    * FocusListener.
    * @author MINARM
    */
   private static class FocusHandler implements FocusListener
   {
      /**
       * Constructeur.
       */
      FocusHandler ()
      {
         super();
      }

      @Override
      public void focusGained (final FocusEvent focusEvent)
      {
         final SpiStringArea instance = getInstance(focusEvent);
         if (instance != null)
         {
            // évènement de focus sur le textarea :
            // si focusGained on sélectionne le texte
            if (SpiTextField.TEXT_SELECTION_ON_FOCUS_GAINED && instance.isEditable())
            {
               instance.getTextArea().selectAll();
            }

            // dans tous les cas on relance un focusEvent sur le SpiStringArea
            // pour ceux qui écoutent sur celui-ci
            instance.processFocusEvent(new FocusEvent(instance, focusEvent.getID(), focusEvent.isTemporary()));
         }
      }

      @Override
      public void focusLost (final FocusEvent focusEvent)
      {
         final SpiStringArea instance = getInstance(focusEvent);
         if (instance != null)
         {
            // dans tous les cas on relance un focusEvent sur le SpiStringArea
            // pour ceux qui écoutent sur celui-ci
            instance.processFocusEvent(new FocusEvent(instance, focusEvent.getID(), focusEvent.isTemporary()));
         }
      }
   }

   /**
    * KeyListener.
    * @author MINARM
    */
   private static class KeyHandler implements KeyListener
   {
      /**
       * Constructeur.
       */
      KeyHandler ()
      {
         super();
      }

      @Override
      public void keyPressed (final KeyEvent keyEvent)
      {
         final SpiStringArea instance = getInstance(keyEvent);
         if (instance != null)
         {
            instance.keyEvent(keyEvent);
         }
      }

      @Override
      public void keyReleased (final KeyEvent keyEvent)
      {
         final SpiStringArea instance = getInstance(keyEvent);
         if (instance != null)
         {
            instance.keyEvent(keyEvent);
         }
      }

      @Override
      public void keyTyped (final KeyEvent keyEvent)
      {
         final SpiStringArea instance = getInstance(keyEvent);
         if (instance != null)
         {
            instance.keyEvent(keyEvent);
         }
      }
   }

   /**
    * Constructeur.
    */
   public SpiStringArea ()
   {
      super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      if (getVerticalScrollBar() != null)
      {
         getVerticalScrollBar().setFocusable(false);
      }
      if (getHorizontalScrollBar() != null)
      {
         getHorizontalScrollBar().setFocusable(false);
      }
      setViewportView(getTextArea());
      setSize(300, 65); // taille par défaut
      getTextArea().addFocusListener(FOCUS_HANDLER);
      getTextArea().addKeyListener(KEY_HANDLER);
   }

   /**
    * Retourne l'instance de SpiStringArea père du composant source de l'événement spécifié.
    * @return SpiStringArea
    * @param event
    *           EventObject
    */
   protected static SpiStringArea getInstance (final EventObject event)
   {
      if (event.getSource() instanceof Component)
      {
         return SpiSwingUtilities.getAncestorOfClass(SpiStringArea.class, (Component) event.getSource());
      }
      return null;
   }

   @Override
   public String getValue ()
   {
      String text = getTextArea().getText();
      if (text != null)
      {
         // on (re)trime pour enlever les espaces
         text = text.trim();
         // si la chaine est vide alors elle devient null
         if (text.length() == 0)
         {
            text = null;
         }
      }
      return text;
   }

   @Override
   public void setValue (final String newString)
   {
      // si le texte est le même, on évite de lancer un DocumentEvent inutile
      if (!getTextArea().getText().equals(newString))
      {
         final JTextArea myTextArea = getTextArea();
         final SpiTextDocument document = getTextDocument();
         final boolean oldEditable = document.isEditable();
         document.setEditable(true);
         myTextArea.setText(newString);
         myTextArea.setCaretPosition(0); // on scrolle au début
         document.setEditable(oldEditable);
      }
   }

   /**
    * Retourne la valeur de la propriété maxLength.
    * @return int
    * @see #setMaxLength
    */
   @Override
   public int getMaxLength ()
   {
      return getTextDocument().getMaxLength();
   }

   /**
    * Définit la valeur de la propriété maxLength.
    * @param newMaxLength
    *           int
    * @see #getMaxLength
    */
   @Override
   public void setMaxLength (final int newMaxLength)
   {
      getTextDocument().setMaxLength(newMaxLength);
   }

   /**
    * Retourne la valeur de la propriété textDocument.
    * @return SpiTextDocument
    */
   protected SpiTextDocument getTextDocument ()
   {
      return (SpiTextDocument) getTextArea().getDocument();
   }

   /**
    * Retourne la valeur de la propriété textArea interne.
    * @return JTextArea
    */
   public JTextArea getTextArea ()
   {
      if (textArea == null)
      {
         final Document document = new SpiTextDocument(STRING_AREA_DEFAULT_MAX_LENGTH);
         final JTextArea myTextArea = new JTextArea(document);
         myTextArea.setLineWrap(true);
         myTextArea.setWrapStyleWord(true);
         // textArea.setDragEnabled(true);

         this.textArea = myTextArea;
      }

      return textArea;
   }

   /**
    * Gestion des événements claviers sur le textArea (touche TAB).
    * @param event
    *           KeyEvent
    */
   protected void keyEvent (final KeyEvent event)
   {
      // La touche tabulation transfert le focus.
      if (event.getID() == KeyEvent.KEY_PRESSED && event.getKeyChar() == KeyEvent.VK_TAB)
      {
         event.consume();
         if (event.isShiftDown())
         {
            FocusManager.getCurrentManager().focusPreviousComponent(getTextArea());
         }
         else
         {
            FocusManager.getCurrentManager().focusNextComponent(getTextArea());
         }
      }
   }

   /**
    * Retourne la valeur de la propriété editable.
    * @return boolean
    * @see #setEditable
    */
   public boolean isEditable ()
   {
      return getTextArea().isEditable();
   }

   /**
    * Définit la valeur de la propriété editable.
    * @param newEditable
    *           boolean
    * @see #isEditable
    */
   public void setEditable (final boolean newEditable)
   {
      final JTextArea myTextArea = getTextArea();
      // le document du textArea doit être un MTextDocument
      getTextDocument().setEditable(newEditable);
      myTextArea.setEditable(newEditable);
      // appel de updateBackground car sans cela la couleur de fond du JTextArea non éditable reste blanche et non grisée
      updateBackground(myTextArea);
   }

   /**
    * Méthode inspirée directement de {@link javax.swing.plaf.basic.BasicTextUI#updateBackground}, <br/>
    * mais contrairement à celle-ci nous forçons son exécution si Nimbus (à base de Synth) et si c'est un JTextArea <br/>
    * (BasicTextUI#updateBackground se désactive malheureusement dans ces 2 cas) <br/>
    * <br/>
    * Updates the background of the text component based on whether the text component is editable and/or enabled.
    * @param c
    *           the JTextComponent that needs its background color updated
    */
   static void updateBackground (final JTextComponent c)
   {
      final Color background = c.getBackground();
      if (background instanceof UIResource)
      {
         final String propertyPrefix;
         if (c instanceof JTextArea)
         {
            propertyPrefix = "TextArea";
         }
         else
         {
            propertyPrefix = "TextField";
         }
         final Color disabledBG = UIManager.getColor(propertyPrefix + ".disabledBackground");
         final Color inactiveBG = UIManager.getColor(propertyPrefix + ".inactiveBackground");
         final Color bg = UIManager.getColor(propertyPrefix + ".background");

         // final In an final ideal situation, the following final check would not final be necessary and final we would replace final the color any final time the previous final color was a UIResouce.
         // However, it turns final out that there final is existing code final that uses the final following inadvisable pattern final to turn a final text area into final what appears to final be a multi-line label:
         //
         // JLabel label = new JLabel();
         // final JTextArea area = new JTextArea();
         // area.setBackground(label.getBackground());
         // area.setEditable(false);
         //
         // JLabel's default background is a UIResource. As such, just checking for UIResource would have us always changing the background away from what the developer wanted.
         // Therefore, for JTextArea/JEditorPane, we'll additionally check that the color we're about to replace matches one that was installed by us from the UIDefaults.

         if (!background.equals(disabledBG) && !background.equals(inactiveBG) && !background.equals(bg))
         {
            return;
         }

         Color newColor = null;
         if (!c.isEnabled())
         {
            newColor = disabledBG;
         }
         if (newColor == null && !c.isEditable())
         {
            newColor = inactiveBG;
         }
         if (newColor == null)
         {
            newColor = bg;
         }
         if (newColor != null && !newColor.equals(background))
         {
            c.setBackground(newColor);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @deprecated Utiliser setEditable
    */
   @Deprecated
   @Override
   public void setEnabled (final boolean newEnabled)
   {
      // Surcharge de setEnabled pour appeler setEditable.
      setEditable(newEnabled);
   }

   @Override
   public void setName (final String name)
   {
      super.setName(name);
      getTextArea().setName(name + ".textArea");
   }
}
