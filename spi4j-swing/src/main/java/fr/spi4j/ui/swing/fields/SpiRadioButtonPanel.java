/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.fields;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.LayoutFocusTraversalPolicy;

import fr.spi4j.ui.swing.SpiPanel;

/**
 * Permet de regrouper automatiquement les JRadioButtons qu'on ajoute dans ce panel. <br/>
 * Arrow key support by Alexander Potochkin (http://weblogs.java.net/blog/alexfromsun/archive/2006/11/kiss_principle.html, LGPL)
 * @author MINARM, Alexander Potochkin
 */
public class SpiRadioButtonPanel extends SpiPanel
{
   private static final long serialVersionUID = 1L;

   private static final ContainerHandler CONTAINER_HANDLER = new ContainerHandler();

   // ce layout par défaut est une constante statique (design pattern poids-mouche / fly-weight),
   // son état ne doit pas être modifié
   private static final FlowLayout DEFAULT_FLOW_LAYOUT = new FlowLayout(FlowLayout.LEFT, 0, 0)
   {
      private static final long serialVersionUID = 1L;

      @Override
      public void setAlignment (final int align)
      {
         if (align == FlowLayout.LEFT)
         {
            // setAlignment est appelée dans le constructeur de FlowLayout
            super.setAlignment(align);
         }
         else
         {
            throw new UnsupportedOperationException();
         }
      }

      @Override
      public void setHgap (final int hgap)
      {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setVgap (final int vgap)
      {
         throw new UnsupportedOperationException();
      }
   };

   private final ButtonGroup buttonGroup = new ButtonGroup();

   /**
    * ContainerListener.
    * @author MINARM
    */
   private static class ContainerHandler implements ContainerListener
   {
      /**
       * Constructeur.
       */
      ContainerHandler ()
      {
         super();
      }

      @Override
      public void componentAdded (final ContainerEvent containerEvent)
      {
         if (containerEvent.getChild() instanceof JRadioButton)
         {
            final JRadioButton radioButton = (JRadioButton) containerEvent.getChild();
            final SpiRadioButtonPanel radioButtonPanel = (SpiRadioButtonPanel) containerEvent.getContainer();
            // groupe automatiquement les JRadioButtons de ce panel.
            radioButtonPanel.getButtonGroup().add(radioButton);
         }
      }

      @Override
      public void componentRemoved (final ContainerEvent containerEvent)
      {
         if (containerEvent.getChild() instanceof JRadioButton)
         {
            final JRadioButton radioButton = (JRadioButton) containerEvent.getChild();
            final SpiRadioButtonPanel radioButtonPanel = (SpiRadioButtonPanel) containerEvent.getContainer();
            radioButtonPanel.getButtonGroup().remove(radioButton);
         }
      }
   }

   /**
    * ActionListener.
    * @author MINARM
    */
   private class ActionHandler implements ActionListener
   {
      private static final String FORWARD = "moveSelectionForward";

      private static final String BACKWARD = "moveSelectionBackward";

      /**
       * Constructeur.
       */
      ActionHandler ()
      {
         super();
      }

      @Override
      public void actionPerformed (final ActionEvent e)
      {
         final FocusTraversalPolicy ftp = getFocusTraversalPolicy();

         if (ftp instanceof RadioButtonPanelFocusTraversalPolicy)
         {
            final RadioButtonPanelFocusTraversalPolicy xftp = (RadioButtonPanelFocusTraversalPolicy) ftp;

            final String actionCommand = e.getActionCommand();
            final Component fo = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Component next;

            xftp.setAlternativeFocusMode(true);

            if (FORWARD.equals(actionCommand))
            {
               next = xftp.getComponentAfter(SpiRadioButtonPanel.this, fo);
            }
            else if (BACKWARD.equals(actionCommand))
            {
               next = xftp.getComponentBefore(SpiRadioButtonPanel.this, fo);
            }
            else
            {
               throw new AssertionError("Unexpected action command: " + actionCommand);
            }

            xftp.setAlternativeFocusMode(false);

            if (fo instanceof AbstractButton)
            {
               final AbstractButton b = (AbstractButton) fo;
               b.getModel().setPressed(false);
            }
            if (fo instanceof AbstractButton && next instanceof AbstractButton)
            {
               final ButtonGroup group = getButtonGroup();
               final AbstractButton nextButton = (AbstractButton) next;
               if (group != null && group.getSelection() != null && !nextButton.isSelected())
               {
                  nextButton.setSelected(true);
               }
               next.requestFocusInWindow();
            }
         }
      }
   }

   /**
    * LayoutFocusTraversalPolicy.
    * @author MINARM
    */
   private class RadioButtonPanelFocusTraversalPolicy extends LayoutFocusTraversalPolicy
   {
      private static final long serialVersionUID = 1L;

      private boolean alternativeFocusMode;

      /**
       * Constructeur.
       */
      RadioButtonPanelFocusTraversalPolicy ()
      {
         super();
      }

      /**
       * Retourne la valeur de alternativeFocusMode.
       * @return boolean
       */
      boolean isAlternativeFocusMode ()
      {
         return alternativeFocusMode;
      }

      /**
       * Définit la valeur de alternativeFocusMode.
       * @param newAlternativeFocusMode
       *           boolean
       */
      void setAlternativeFocusMode (final boolean newAlternativeFocusMode)
      {
         this.alternativeFocusMode = newAlternativeFocusMode;
      }

      @Override
      protected boolean accept (final Component c)
      {
         if (!isAlternativeFocusMode() && c instanceof AbstractButton)
         {
            final AbstractButton button = (AbstractButton) c;
            final ButtonGroup group = getButtonGroup();
            if (group != null && group.getSelection() != null && !button.isSelected())
            {
               return false;
            }
         }
         return super.accept(c);
      }

      @Override
      public Component getComponentAfter (final Container aContainer, final Component aComponent)
      {
         final Component componentAfter = super.getComponentAfter(aContainer, aComponent);
         if (!isAlternativeFocusMode())
         {
            return componentAfter;
         }
         return componentAfter == null ? getFirstComponent(aContainer) : componentAfter;
      }

      @Override
      public Component getComponentBefore (final Container aContainer, final Component aComponent)
      {
         final Component componentBefore = super.getComponentBefore(aContainer, aComponent);
         if (!isAlternativeFocusMode())
         {
            return componentBefore;
         }
         return componentBefore == null ? getLastComponent(aContainer) : componentBefore;
      }
   }

   /**
    * Constructeur.
    */
   public SpiRadioButtonPanel ()
   {
      super(DEFAULT_FLOW_LAYOUT); // flowLayout par défaut (poids mouche) pour un RadioButtonPanel
      setOpaque(false);
      addContainerListener(CONTAINER_HANDLER);
      initArrowKeySupport();
   }

   /**
    * Initialise la possibilité d'utiliser les flèches du clavier pour passer d'un bouton à l'autre (comme Windows).
    */
   private void initArrowKeySupport ()
   {
      setFocusTraversalPolicyProvider(true);
      setFocusTraversalPolicy(new RadioButtonPanelFocusTraversalPolicy());
      final ActionListener actionHandler = new ActionHandler();
      registerKeyboardAction(actionHandler, ActionHandler.FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
               JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      registerKeyboardAction(actionHandler, ActionHandler.FORWARD, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
               JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      registerKeyboardAction(actionHandler, ActionHandler.BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
               JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      registerKeyboardAction(actionHandler, ActionHandler.BACKWARD, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
               JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
   }

   /**
    * Retourne la valeur de la propriété buttonGroup.
    * @return ButtonGroup
    */
   protected ButtonGroup getButtonGroup ()
   {
      return buttonGroup;
   }

   /**
    * Clears the selection such that none of the buttons in the <code>ButtonGroup</code> are selected.
    */
   public void clearSelection ()
   {
      getButtonGroup().clearSelection();
   }
}
