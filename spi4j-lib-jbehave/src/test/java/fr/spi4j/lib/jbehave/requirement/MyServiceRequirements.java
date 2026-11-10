/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

/**
 * Exigences du service.
 * @author MINARM
 */
public class MyServiceRequirements
{

   /**
    * Test de l'exigence 1.
    */
   @Requirement_AnnotationForTest(Requirement_EnumForTest.REQ_FCT_PERS_01)
   public void exigence1 ()
   {
      // RAS
   }

   /**
    * Test de l'exigence 2.
    */
   @Requirement_AnnotationForTest(Requirement_EnumForTest.REQ_TEC_PERS_02)
   public void exigence2 ()
   {
      // RAS
   }

}
