/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.util.List;

import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.EntityUtil;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.swing.fields.SpiComboBox;

/**
 * TableModel de SpiTable, qui utilise les AttributesNames_Itf (enum) pour déterminer les valeurs dans les colonnes.
 * @param <TypeValue>
 *           Type des valeurs de la liste
 * @author MINARM
 */
public class SpiTableModel<TypeValue> extends SpiListTableModel<TypeValue>
{
   private static final long serialVersionUID = 1L;

   private static final Logger c_log = LogManager.getLogger(SpiTableModel.class);

   /**
    * Constructeur.
    * @param p_table
    *           JTable
    */
   public SpiTableModel (final JTable p_table)
   {
      super(p_table);
   }

   @Override
   public int getColumnCount ()
   {
      return getTable().getColumnCount();
   }

   @Override
   public Object getValueAt (final int rowIndex, final int columnIndex)
   {

      final TypeValue v_object = getObjectAt(rowIndex);
      // final List<AttributesNames_Itf> v_identifier = getIdentifier(columnIndex);
      final List<Object> v_identifier = getIdentifier(columnIndex);
      if (v_identifier == null || v_object == null)
      {
         // cas particulier: si cette colonne n'a pas d'identifier, alors on suppose qu'il y a un renderer spécifique sur la colonne
         // qui utilisera comme valeur l'objet correspondant à cette ligne dans le tableau
         return v_object;
      }
      // la valeur affichée dans la cellule est le résultat de l'appel
      // à la méthode "get" + Identifier sur l'objet
      try
      {
         Object v_value = v_object;
         // s'il n'y a qu'un attribut alors il s'agit d'un simple appel à un getter,
         // et s'il y a plusieurs attributs alors il s'agit d'une suite d'appel aux getters
         // (par exemple, v_personneDto.get_grade().get_trigramme() pour le tableau new AttributesNames_Itf[] {PersonneAttributes_Enum.grade, GradeAttributes_Enum.trigramme} )
         for (final Object v_attribute : v_identifier)
         {
            if (v_value instanceof Dto_Itf)
            {
               v_value = DtoUtil.getAttributeValue((Dto_Itf<?>) v_value, (AttributesNames_Itf) v_attribute);
            }
            else if (v_value instanceof Entity_Itf)
            {
               // v_value = ((ColumnsNames_Itf) v_attribute).getLogicalColumnName();
               v_value = EntityUtil.getAttributeValue((Entity_Itf<?>) v_value, (ColumnsNames_Itf) v_attribute);
            }
            if (v_value == null)
            {
               break;
            }
         }
         return v_value;
      }
      catch (final Exception v_e)
      {
         // s'il y a une erreur dans le getter, alors il ne faut surtout pas afficher de popup d'erreur
         // car tous les affichages de cellules vont en permanence afficher des popups ce qui rendrait l'application bloquée,
         // donc on se contente de logguer le message de l'exception dans un warning (sans trace de l'exception pour ne pas saturer le log non plus)
         // et on affiche "??" dans la cellule
         c_log.warn(v_e.toString());
         return "??";
      }



   }

   @Override
   public void setValueAt (final Object value, final int rowIndex, final int columnIndex)
   {
      final TypeValue v_object = getObjectAt(rowIndex);
      final List<Object> v_identifier = getIdentifier(columnIndex);
      if (v_identifier == null)
      {
         throw new IllegalStateException("Aucun identifier sur cette colonne");
      }
      if (v_object == null)
      {
         throw new IllegalStateException("Aucun objet sur cette ligne");
      }
      // la valeur se met à jour via la méthode "set_" + Identifier sur l'objet
      try
      {
         Identifiable_Itf<?> v_currentObject = (Identifiable_Itf<?>) v_object;
         int i = 0;
         // s'il n'y a qu'un attribut alors il s'agit ici d'un simple appel à un setter,
         // et s'il y a plusieurs attributs alors il s'agit d'une suite d'appel aux getters et un appel au setter pour le dernier attribut
         // (par exemple, v_personneDto.get_grade().set_trigramme(String) pour le tableau new AttributesNames_Itf[] {PersonneAttributes_Enum.grade, GradeAttributes_Enum.trigramme} )
         for (final Object v_attribute : v_identifier)
         {
            if (i < v_identifier.size() - 1)
            {
               if (v_currentObject instanceof Dto_Itf)
               {
                  v_currentObject = (Dto_Itf<?>) DtoUtil.getAttributeValue((Dto_Itf<?>) v_currentObject,
                           (AttributesNames_Itf) v_attribute);
               }
               else if (v_currentObject instanceof Entity_Itf)
               {
                  v_currentObject = (Entity_Itf<?>) EntityUtil.getAttributeValue((Entity_Itf<?>) v_currentObject,
                           (ColumnsNames_Itf) v_attribute);
               }
               if (v_currentObject == null)
               {
                  break;
               }
            }
            else
            {
               // c'est le dernier attribut du tableau,
               // si la valeur est nulle (peut être la valeur nulle par défaut de la combobox)
               if (SpiComboBox.isNullValue(value))
               {
                  if (v_currentObject instanceof Dto_Itf)
                  {
                     DtoUtil.setAttributeValue((Dto_Itf<?>) v_currentObject, (AttributesNames_Itf) v_attribute, null);
                  }
                  else if (v_currentObject instanceof Entity_Itf)
                  {
                     EntityUtil.setAttributeValue((Entity_Itf<?>) v_currentObject, (ColumnsNames_Itf) v_attribute,
                              null);
                  }
               }
               else
               {
                  if (v_currentObject instanceof Dto_Itf)
                  {
                     DtoUtil.setAttributeValue((Dto_Itf<?>) v_currentObject, (AttributesNames_Itf) v_attribute, value);
                  }
                  else if (v_currentObject instanceof Entity_Itf)
                  {
                     EntityUtil.setAttributeValue((Entity_Itf<?>) v_currentObject, (ColumnsNames_Itf) v_attribute,
                              value);
                  }
               }
            }
            i++;
         }
      }
      catch (final Exception v_e)
      {
         // s'il y a une erreur dans le setter, alors il ne faut pas afficher de popup d'erreur
         // car sinon l'ihm resterait tout le temps en édition ce qui rendrait l'application bloquée,
         // donc on se contente de logguer le message de l'exception dans un warning (sans trace de l'exception pour ne pas saturer le log non plus)
         c_log.warn(v_e.toString());
      }
   }

   @Override
   public Class<?> getColumnClass (final int columnIndex)
   {
      final Class<?> v_class = super.getColumnClass(columnIndex);
      if (Object.class != v_class)
      {
         // cas classique, on prend le type à partir de la première valeur non null dans la colonne
         return v_class;
      }
      final List<Object> v_identifier = getIdentifier(columnIndex);
      if (v_identifier == null)
      {
         return v_class;
      }
      // sinon, s'il n'y a pas de valeurs dans la colonne, on prend le type déclaré dans l'énumération des attributs
      if ((v_identifier.get(v_identifier.size() - 1)) instanceof AttributesNames_Itf)
      {
         final AttributesNames_Itf v_lastAttribute = (AttributesNames_Itf) v_identifier.get(v_identifier.size() - 1);
         return v_lastAttribute.getType();
      }
      if ((v_identifier.get(v_identifier.size() - 1)) instanceof ColumnsNames_Itf)
      {
         final ColumnsNames_Itf v_lastAttribute = (ColumnsNames_Itf) v_identifier.get(v_identifier.size() - 1);
         return v_lastAttribute.getTypeColumn();
      }
      return null;

   }

   // note: cette méthode getColumnName n'est normalement jamais appelée, car le libellé de la colonne est déjà défini quand on appelle SpiTable.addColumn(AttributesNames_Itf, String)
   @Override
   public String getColumnName (final int columnIndex)
   {
      final List<Object> v_identifier = getIdentifier(columnIndex);
      if (v_identifier == null)
      {
         return super.getColumnName(columnIndex);
      }
      return v_identifier.toString();
   }

   /**
    * Retourne la chaîne d'attributs correspondant à un numéro de colonne (le plus souvent, il n'y a qu'un seul attribut dans le résultat).
    * @param columnIndex
    *           int
    * @return List<AttributesNames_Itf>
    */
   @SuppressWarnings("unchecked")
   private List<Object> getIdentifier (final int columnIndex)
   {
      final int v_index = getTable().convertColumnIndexToView(columnIndex);
      // selon SpiTable.addColumn, l'identifier est de type AttributesNames_Itf[]
      final Object v_identifier = getTable().getColumnModel().getColumn(v_index).getIdentifier();
      if (v_identifier instanceof List)
      {
         // si l'identifier est de type AttributesNames_Itf[], alors on va l'utiliser dans getValueAt, getColumnClass ou getColumnName
         return (List<Object>) v_identifier;
      }
      // mais sinon on ignore l'identifier et ces 3 méthodes auront un comportement par défaut
      return null;
   }
}
