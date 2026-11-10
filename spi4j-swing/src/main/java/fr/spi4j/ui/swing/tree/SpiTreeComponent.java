/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.tree;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.spi4j.ui.HasMultipleSelection_Itf;
import fr.spi4j.ui.Node_Itf;
import fr.spi4j.ui.swing.SpiSwingUtilities;

/**
 * Composant basique d'arbre
 * @author thillica, adamar (DCSID)
 * @param <TypeValue>
 *           Type de la valeur
 */
public class SpiTreeComponent<TypeValue extends Node_Itf<TypeValue>> extends JTree implements
         HasMultipleSelection_Itf<TypeValue>
{
   private static final long serialVersionUID = 1L;

   /** Noeud racine de l'arbre */
   private final DefaultMutableTreeNode _rootTreeNode = new DefaultMutableTreeNode("Root");

   /** La map de tous les noeuds */
   private final Map<TypeValue, DefaultMutableTreeNode> _mapTreeNode = new HashMap<TypeValue, DefaultMutableTreeNode>();

   /** Liste des éléments de l'arbre */
   private List<TypeValue> _listNode;

   /**
    * Constructeur
    */
   public SpiTreeComponent ()
   {
      super(new DefaultTreeModel(new DefaultMutableTreeNode("Vide")));
      // Masquer le noeud racine
      setRootVisible(false);
      // Déplier les noeuds sur sélection
      setExpandsSelectedPaths(true);
      // Afficher le picto des noeuds
      setShowsRootHandles(true);
      // Enregistrer l'arbre pour afficher les tooltips
      ToolTipManager.sharedInstance().registerComponent(this);
   }

   @Override
   public String getToolTipText (final MouseEvent p_event)
   {
      if (p_event == null)
      {
         return null;
      }
      else
      {
         final int v_row = getRowForLocation(p_event.getX(), p_event.getY());
         final TreePath v_path = getPathForRow(v_row);
         if (v_path != null)
         {
            final DefaultMutableTreeNode v_treeNode = (DefaultMutableTreeNode) v_path.getLastPathComponent();
            if (v_treeNode != null)
            {
               final TreeCellRenderer v_renderer = getCellRenderer();
               final Component v_component = v_renderer.getTreeCellRendererComponent(this, v_treeNode,
                        isRowSelected(v_row), isExpanded(v_row), getModel().isLeaf(v_treeNode), v_row, true);
               if (v_component instanceof JComponent)
               {
                  final String v_toolTipText = ((JComponent) v_component).getToolTipText();
                  if (v_toolTipText != null)
                  {
                     // le tool-tip a été défini spécifiquement sur le renderer, alors on le prend
                     return v_toolTipText;
                  }
                  // pas de tool-tip défini spécifiquement sur le renderer, alors on prend par défaut le text
                  String v_text = SpiSwingUtilities.getTextFromRendererComponent(v_component);
                  if (v_text == null)
                  {
                     // text par défaut si ce n'est pas un composant textuel connu
                     v_text = v_treeNode.toString();
                  }
                  return v_text;
               }
            }
         }
         return null;
      }
   }

   /**
    * Permet de lancer la construction complète de l'arbre.
    * <p>
    * La liste contient la racine.
    */
   private void runBuildTree ()
   {
      final TreePath v_racinePath = new TreePath(_rootTreeNode.getPath());
      // Récupérer la liste des noeuds dépliés
      final Enumeration<TreePath> v_enumExpandPath = getExpandedDescendants(v_racinePath);

      // Reinitialiser l'arbre
      _mapTreeNode.clear();
      _rootTreeNode.removeAllChildren();
      // Construit l'arbre
      for (final TypeValue v_value : getList())
      {
         final DefaultMutableTreeNode v_treeNode = new DefaultMutableTreeNode(v_value);
         _mapTreeNode.put(v_value, v_treeNode);
         _rootTreeNode.add(v_treeNode);
         buildTreeNode(v_treeNode, v_value);
      }
      // Passer le noeud racine au model d'arbre par défaut
      if (getModel() instanceof DefaultTreeModel)
      {
         final DefaultTreeModel v_model = (DefaultTreeModel) getModel();
         v_model.setRoot(_rootTreeNode);
      }

      // Déplier les noeuds
      while (v_enumExpandPath != null && v_enumExpandPath.hasMoreElements())
      {
         final TreePath v_expandPath = v_enumExpandPath.nextElement();
         final DefaultMutableTreeNode v_expandNode = (DefaultMutableTreeNode) v_expandPath.getLastPathComponent();
         final Object v_expandObject = v_expandNode.getUserObject();
         if (v_expandObject instanceof Node_Itf)
         {
            @SuppressWarnings("unchecked")
            final TypeValue v_expandValue = (TypeValue) v_expandObject;
            for (final TypeValue v_key : _mapTreeNode.keySet())
            {
               if (v_key.equals(v_expandValue))
               {
                  final TreePath v_treePath = new TreePath(_mapTreeNode.get(v_key).getPath());
                  expandPath(v_treePath);
                  break;
               }
            }
         }
      }
   }

   /**
    * Construit l'arbre récursivement
    * @param p_treeNode
    *           Le noeud
    * @param p_value
    *           La valeur
    */
   private void buildTreeNode (final DefaultMutableTreeNode p_treeNode, final TypeValue p_value)
   {
      // Vérifier que la valeur n'est pas null
      if (p_value != null && p_value.getListNode() != null)
      {
         for (final TypeValue v_value : p_value.getListNode())
         {
            if (v_value.getParentNode() != p_value)
            {
               throw new IllegalStateException("\nL'arborescence n'est pas respectée.");
            }
            final DefaultMutableTreeNode v_treeNode = new DefaultMutableTreeNode(v_value);
            _mapTreeNode.put(v_value, v_treeNode);
            p_treeNode.add(v_treeNode);
            buildTreeNode(v_treeNode, v_value);
         }
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<TypeValue> getValue ()
   {
      final List<TypeValue> v_ret = new ArrayList<TypeValue>();

      final TreePath[] v_treePaths = getSelectionPaths();
      if (v_treePaths != null && v_treePaths.length > 0)
      {
         // final DefaultMutableTreeNode v_treeNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
         for (final TreePath v_treePath : v_treePaths)
         {
            final DefaultMutableTreeNode v_treeNode = (DefaultMutableTreeNode) v_treePath.getLastPathComponent();
            if (v_treeNode != null)
            {
               final Object v_value = v_treeNode.getUserObject();
               if (v_value instanceof Node_Itf)
               {
                  v_ret.add((TypeValue) v_value);
               }
               else
               {
                  throw new IllegalStateException("\nL'objet du noeud doit être une instance de '"
                           + Node_Itf.class.getName() + "'");
               }
            }
         }
      }
      return v_ret;
   }

   @Override
   public void setValue (final List<TypeValue> p_value)
   {
      if (p_value != null && !p_value.isEmpty())
      {
         final List<TreePath> v_treePaths = new ArrayList<TreePath>();

         for (final TypeValue v_value : p_value)
         {
            final DefaultMutableTreeNode v_treeNode = _mapTreeNode.get(v_value);

            if (v_treeNode != null)
            {
               final TreePath v_treePath = new TreePath(v_treeNode.getPath());
               v_treePaths.add(v_treePath);
            }
         }
         final TreePath[] v_tmpPaths = new TreePath[v_treePaths.size()];
         setSelectionPaths(v_treePaths.toArray(v_tmpPaths));

         // mono-sélection : garder la position
         if (v_treePaths.size() == 1)
         {
            final Rectangle v_bounds = getPathBounds(v_treePaths.get(0));
            if (v_bounds != null)
            {
               // Pour éviter le scroll horizontal
               v_bounds.x = 0;
               scrollRectToVisible(v_bounds);
            }
         }
      }
      else
      {
         clearSelection();
      }
   }

   @Override
   public List<TypeValue> getList ()
   {
      return _listNode;
   }

   @Override
   public void setList (final List<TypeValue> p_list)
   {
      _listNode = p_list;
      runBuildTree();
   }
}
