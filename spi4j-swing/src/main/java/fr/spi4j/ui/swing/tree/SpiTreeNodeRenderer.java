/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renderer pour le composant {@link SpiTreeComponent}
 * @author thillica, adamar (DCSID)
 */
public class SpiTreeNodeRenderer extends DefaultTreeCellRenderer
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur
    */
   public SpiTreeNodeRenderer ()
   {
      super();
   }

   /**
    * Méthode à surcharger pour changer le rendu via setText(String), setIcon(Icon), setToolTipText(String) en fonction de la valeur
    * @param p_value
    *           La valeur
    */
   protected void setValue (final Object p_value)
   {
      if (p_value != null)
      {
         setText(p_value.toString());
      }
      else
      {
         setText("");
      }
      // Remarque : Le toolTipText affiché par défaut, s'il n'est pas défini sur le renderer,
      // est le même que le text affiché, selon le code de TreeComponent.getToolTipText(MouseEvent).
   }

   @Override
   public Component getTreeCellRendererComponent (final JTree p_tree, final Object p_value, final boolean p_sel,
            final boolean p_expanded, final boolean p_leaf, final int p_row, final boolean p_hasFocus)
   {
      // réaliser le super
      super.getTreeCellRendererComponent(p_tree, p_value, p_sel, p_expanded, p_leaf, p_row, p_hasFocus);

      // Obtenir la valeur
      if (p_value instanceof DefaultMutableTreeNode)
      {
         final DefaultMutableTreeNode v_node = (DefaultMutableTreeNode) p_value;
         // Obtenir l'objet utilisateur
         final Object v_userObject = v_node.getUserObject();
         setValue(v_userObject);
      }
      return this;
   }
}
