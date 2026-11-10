/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dbpopulate;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compteur utilisé pour la montée en charge
 * @author MINARM
 */
public class Repartition1NCounter implements Counter_Itf<Integer>
{

   private static final Logger c_log = LogManager.getLogger(Repartition1NCounter.class);

   private static final Random c_random = new Random();

   private final int _min;

   private final int _max;

   private long _availableVolumetry;

   private long _inputVolumetry;

   private final boolean _01Nullable;

   /**
    * Constructeur du compteur de répartition pour la montée en charge
    * @param p_min
    *           le nombre minimum d'éléments pour cette relation (>= 0)
    * @param p_max
    *           le nombre maximum d'éléments pour cette relation (>= p_min)
    * @param p_maxVolumetry
    *           le nombre total d'éléments à affecter (ex : le nombre d'adresses)
    * @param p_inputVolumetry
    *           le nombre d'éléments en entrée de la relation (ex : le nombre de personnes)
    * @param p_01Nullable
    *           flag indiquant si la relation est nullable (0..1 ou 1..1) (ex : une adresse peut ne pas avoir de propriétaire, une personne peut ne pas avoir de grade)
    */
   public Repartition1NCounter (final int p_min, final int p_max, final long p_maxVolumetry,
            final long p_inputVolumetry, final boolean p_01Nullable)
   {
      if (p_min < 0)
      {
         throw new IllegalArgumentException(
                  "Le nombre minimum d'éléments pour la relation doit être positif ou null : " + p_min);
      }
      if (p_max < p_min)
      {
         throw new IllegalArgumentException(
                  "Le nombre maximum d'éléments pour la relation doit être au moins égal au nombre minimum d'éléments : "
                           + p_max);
      }
      if (p_inputVolumetry * p_min > p_maxVolumetry)
      {
         throw new IllegalArgumentException(
                  "La volumétrie n'est pas calculable car il n'y a pas assez d'éléments dans la volumétrie disponible ou bien la relation a un minimum trop élevé : "
                           + p_inputVolumetry + " x " + p_min + " > " + p_maxVolumetry);
      }
      if (!p_01Nullable && p_inputVolumetry * p_max < p_maxVolumetry)
      {
         throw new IllegalArgumentException(
                  "La volumétrie n'est pas calculable car il y a trop d'éléments dans la volumétrie disponible ou bien la relation a un maximum trop faible : "
                           + p_inputVolumetry + " x " + p_max + " < " + p_maxVolumetry);
      }
      _min = p_min;
      _max = p_max;
      _inputVolumetry = p_inputVolumetry;
      _01Nullable = p_01Nullable;
      _availableVolumetry = p_maxVolumetry;
   }

   @Override
   public Integer getNextValue ()
   {
      int v_nextValue;
      if (_inputVolumetry == 0)
      {
         v_nextValue = -1;
         if (_availableVolumetry > 0)
         {
            c_log.warn("Il reste des entités disponibles à affecter (" + _availableVolumetry
                     + "), pourtant la volumétrie d'entrée est épuisée");
         }
      }
      else
      {
         final int v_realMin = (int) Math.max(_min, _availableVolumetry - _max * (_inputVolumetry - 1));
         final int v_realMax;
         if (_01Nullable)
         {
            // Si la relation côté 01 est nullable, alors ce n'est pas grave s'il reste des données à la fin
            v_realMax = _max;
         }
         else
         {
            v_realMax = (int) Math.min(_max, _availableVolumetry - _min * (_inputVolumetry - 1));
         }

         c_log.debug("realMin = " + v_realMin + " / realMax = " + v_realMax + " (entités restantes : "
                  + _availableVolumetry + " / itérations restantes : " + _inputVolumetry + ")");

         // Calcul aléatoire d'une valeur
         v_nextValue = c_random.nextInt(v_realMax + 1 - v_realMin) + v_realMin;
      }

      // on a utilisé 'v_nextValue' de la volumétrie disponible en plus (il ne faut pas que _usedVolumetry dépasse _maxVolumetry)
      if (v_nextValue > 0)
      {
         _availableVolumetry -= v_nextValue;
      }

      // on a calculé une nouvelle valeur, la volumétrie à affecter décrémente
      // --> on va affecter 'v_nextValue' adresses à la personne courante,
      // on a une personne en moins à prendre en compte car on vient de la traiter
      _inputVolumetry--;

      c_log.debug("Compteur montée en charge : " + v_nextValue);
      return v_nextValue;
   }

   @Override
   public Integer getNextValue (final Integer p_maxValue)
   {
      int v_nextValue = getNextValue();
      // Si la valeur calculée dépasse la valeur maximum souhaitée
      if (v_nextValue > p_maxValue)
      {
         // On n'a pas utilisé autant de valeurs disponibles que prévu, il faut réajuster la volumétrie utilisée
         _availableVolumetry = _availableVolumetry + v_nextValue - p_maxValue;
         v_nextValue = p_maxValue;
      }
      return v_nextValue;
   }

   @Override
   public Integer getMax ()
   {
      return _max;
   }

}
