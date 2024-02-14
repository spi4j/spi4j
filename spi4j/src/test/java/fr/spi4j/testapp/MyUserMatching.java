/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

/**
 * Factory permettant de récupérer les instances de classes de matching.
 * @author MINARM
 */
public final class MyUserMatching
{
   /**
    * Constructeur privé.
    */
   private MyUserMatching ()
   {
      super();
   }

   /**
    * Obtenir la façade de services 'MyPersonneMatch_Itf'.
    * @return L'instance désirée.
    */
   public static MyPersonneMatch_Itf getInstanceOfPersonneMatch ()
   {
      return new MyPersonneMatch();
   }
}
