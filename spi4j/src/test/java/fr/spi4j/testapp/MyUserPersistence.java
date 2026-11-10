/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import fr.spi4j.persistence.ParamPersistence_Abs;
import fr.spi4j.persistence.UserPersistence_Abs;

/**
 * Classe abstraite permettant de centraliser les traitements de persistance de l'application.
 * @author MINARM
 */
public class MyUserPersistence extends UserPersistence_Abs
{
   /**
    * Constructeur.
    * @param p_ParamPersistence
    *           (In)(*) le paramétrage de la persistance
    */
   protected MyUserPersistence (final ParamPersistence_Abs p_ParamPersistence)
   {
      super(p_ParamPersistence);
   }

   /**
    * Obtenir l'entité 'MyPersonneEntity_Itf'.
    * @return L'instance désirée.
    */
   public MyPersonneEntity_Itf getInstanceOfPersonneEntity ()
   {
      // Obtenir l'instance représentant l'entité
      final MyPersonneEntity_Itf v_PersonneEntity = (MyPersonneEntity_Itf) getEntity(MyPersonneEntity_Itf.class);
      // Retourner l'instance obtenue
      return v_PersonneEntity;
   }

   /**
    * Obtenir le DAO 'MyPersonneDao_Itf'.
    * @return L'instance désirée.
    */
   public MyPersonneDao_Itf getInstanceOfPersonneDao ()
   {
      // Obtenir l'instance représentant le DAO
      final MyPersonneDao_Itf v_PersonneDao = (MyPersonneDao_Itf) getDao(MyPersonneEntity_Itf.class);
      // Retourner l'instance obtenue
      return v_PersonneDao;
   }

   /**
    * Obtenir l'entité 'MyPersonneEntity_Itf'.
    * @return L'instance désirée.
    */
   public MyGradeEntity_Itf getInstanceOfGradeEntity ()
   {
      // Obtenir l'instance représentant l'entité
      final MyGradeEntity_Itf v_GradeEntity = (MyGradeEntity_Itf) getEntity(MyGradeEntity_Itf.class);
      // Retourner l'instance obtenue
      return v_GradeEntity;
   }

   /**
    * Obtenir le DAO 'MyGradeDao_Itf'.
    * @return L'instance désirée.
    */
   public MyGradeDao_Itf getInstanceOfGradeDao ()
   {
      // Obtenir l'instance représentant le DAO
      final MyGradeDao_Itf v_GradeDao = (MyGradeDao_Itf) getDao(MyGradeEntity_Itf.class);
      // Retourner l'instance obtenue
      return v_GradeDao;
   }

   /**
    * Obtenir l'entité 'MyPersonneEntity_Itf'.
    * @return L'instance désirée.
    */
   public MyGradeEntity_Itf getInstanceOfWrongGradeEntity ()
   {
      // Obtenir l'instance représentant l'entité
      final MyGradeEntity_Itf v_WrongGradeEntity = (MyGradeEntity_Itf) getEntity(MyWrongGradeEntity_Itf.class);
      // Retourner l'instance obtenue
      return v_WrongGradeEntity;
   }

   /**
    * Obtenir le DAO 'MyWrongGradeDao_Itf'.
    * @return L'instance désirée.
    */
   public MyWrongGradeDao_Itf getInstanceOfWrongGradeDao ()
   {
      // Obtenir l'instance représentant le DAO
      final MyWrongGradeDao_Itf v_WrongGradeDao = (MyWrongGradeDao_Itf) getDao(MyWrongGradeEntity_Itf.class);
      // Retourner l'instance obtenue
      return v_WrongGradeDao;
   }
}
