/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing.table;

import java.awt.Color;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.swing.fields.SpiCheckBox;
import fr.spi4j.ui.swing.fields.SpiDateField;
import fr.spi4j.ui.swing.fields.SpiDoubleField;
import fr.spi4j.ui.swing.fields.SpiIntegerField;
import fr.spi4j.ui.swing.fields.SpiLongField;
import fr.spi4j.ui.swing.fields.SpiStringField;

/**
 * Editor par défaut pour les cellules des JTables.
 * @author MINARM
 */
public class SpiDefaultCellEditor extends DefaultCellEditor
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructeur.
    * @param checkBox
    *           JCheckBox
    */
   public SpiDefaultCellEditor (final JCheckBox checkBox)
   {
      super(checkBox);
      checkBox.setBackground(Color.WHITE);
      checkBox.setHorizontalAlignment(SwingConstants.CENTER);
   }

   /**
    * Constructeur.
    * @param comboBox
    *           JComboBox
    */
   @SuppressWarnings("rawtypes")
   public SpiDefaultCellEditor (final JComboBox comboBox)
   {
      super(comboBox);
   }

   /**
    * Constructeur.
    * @param textField
    *           JTextField
    */
   public SpiDefaultCellEditor (final JTextField textField)
   {
      super(textField);
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -1;

         @Override
         public void setValue (final Object value)
         {
            textField.setText(value != null ? value.toString() : "");
         }

         @Override
         public Object getCellEditorValue ()
         {
            return textField.getText();
         }
      };
   }

   /**
    * Constructeur.
    * @param checkBox
    *           SpiCheckBox
    */
   public SpiDefaultCellEditor (final SpiCheckBox checkBox)
   {
      this((JCheckBox) checkBox);
   }

   /**
    * Constructeur.
    * @param dateField
    *           SpiDateField
    */
   public SpiDefaultCellEditor (final SpiDateField dateField)
   {
      super(dateField);
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -2;

         @Override
         public void setValue (final Object value)
         {
            dateField.setValue((Date) value);
         }

         @Override
         public Object getCellEditorValue ()
         {
            return dateField.getValue();
         }
      };
   }

   /**
    * Constructeur.
    * @param doubleField
    *           SpiDoubleField
    */
   public SpiDefaultCellEditor (final SpiDoubleField doubleField)
   {
      super(doubleField);
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -3;

         @Override
         public void setValue (final Object value)
         {
            doubleField.setValue((Double) value);
         }

         @Override
         public Object getCellEditorValue ()
         {
            return doubleField.getValue();
         }
      };
   }

   /**
    * Constructeur.
    * @param integerField
    *           SpiIntegerField
    */
   public SpiDefaultCellEditor (final SpiIntegerField integerField)
   {
      super(integerField);
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -4;

         @Override
         public void setValue (final Object value)
         {
            integerField.setValue((Integer) value);
         }

         @Override
         public Object getCellEditorValue ()
         {
            return integerField.getValue();
         }
      };
   }

   /**
    * Constructeur.
    * @param longField
    *           SpiLongField
    */
   public SpiDefaultCellEditor (final SpiLongField longField)
   {
      super(longField);
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -4;

         @Override
         public void setValue (final Object value)
         {
            longField.setValue((Long) value);
         }

         @Override
         public Object getCellEditorValue ()
         {
            return longField.getValue();
         }
      };
   }

   /**
    * Constructeur.
    * @param stringField
    *           SpiStringField
    */
   public SpiDefaultCellEditor (final SpiStringField stringField)
   {
      super(stringField);
      stringField.setBorder(null);
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -5;

         @Override
         public void setValue (final Object value)
         {
            stringField.setValue(value != null ? value.toString() : null);
         }

         @Override
         public Object getCellEditorValue ()
         {
            return stringField.getValue();
         }
      };
   }

   /**
    * Constructeur.
    * @param <T>
    *           Type de valeur
    * @param hasValue
    *           HasValue_Itf
    */
   @SuppressWarnings("unchecked")
   public <T> SpiDefaultCellEditor (final HasValue_Itf<T> hasValue)
   {
      super(new JTextField());
      editorComponent = (JComponent) hasValue;
      setClickCountToStart(2);
      delegate = new DefaultCellEditor.EditorDelegate()
      {
         private static final long serialVersionUID = -5;

         @Override
         public void setValue (final Object value)
         {
            hasValue.setValue(value != null ? (T) value : null);
         }

         @Override
         public Object getCellEditorValue ()
         {
            return hasValue.getValue();
         }
      };
   }
}
