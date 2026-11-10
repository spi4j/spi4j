/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dbpopulate;

/**
 * Compteur tournant entre 'min' et 'max' par pas de 1. Par exemple entre : 0 et 5
 * @author MINARM
 */
public class RoundCounter implements Counter_Itf<Integer>
{
   /** Le minimum */
   private final Integer _min;

   /** Le maximum */
   private final Integer _max;

   /** La valeur courante (la dernière retournée) */
   private Integer _value;

   /**
    * Constructeur max.
    * @param p_min
    *           Le minimum.
    * @param p_max
    *           Le maximum.
    */
   public RoundCounter (final Integer p_min, final Integer p_max)
   {
      _min = p_min;
      _max = p_max;
      // Initialiser par défaut - max ==> prendra le min :-)
      _value = _max;
   }

   @Override
   public Integer getNextValue ()
   {
      // Si prochaine valeur en dehors du max
      if (_value >= _max)
      {
         _value = _min;
      }
      // Sinon : on incrémente
      else
      {
         _value++;
      }

      return _value;
   }

   @Override
   public Integer getNextValue (final Integer p_maxValue)
   {
      return Math.min(getNextValue(), p_maxValue);
   }

   @Override
   public Integer getMax ()
   {
      return _max;
   }

}
