/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import java.util.List;
import java.util.Map;

import fr.spi4j.entity.Service_Abs;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.Dao_Itf;
import fr.spi4j.persistence.dao.TableCriteria;
import fr.spi4j.requirement.RequirementException;

/**
 * Implémentation du contrat de services spécifiques pour un type 'Personne'. <br/>
 * Pour rappel, les services sont sans état.
 */
public class MyPersonneEntityService extends Service_Abs<Long, MyPersonneEntity_Itf, MyPersonneColumns_Enum>
         implements MyPersonneEntityService_Itf
{

   @Override
   public int methode ()
   {
      return 0;
   }

   @Override
   public int methode2 ()
   {
      return 0;
   }

   @Override
   public void methodeJetantException ()
   {
      throw new Spi4jRuntimeException("?", "?");
   }

   @Override
   public void methodeJetantExceptionExigenceFonctionnelle ()
   {
      throw new RequirementException(MyRequirement_Enum.REQ_FCT_PERS_01, "exigence fonctionnelle");
   }

   @Override
   public void methodeJetantExceptionExigenceTechnique ()
   {
      throw new RequirementException(MyRequirement_Enum.REQ_TEC_PERS_02, "exigence technique",
               new NullPointerException());
   }

   @Override
   public void changeNom (final MyPersonneEntity p_personne)
   {
      p_personne.setNom("n'importe quoi");
   }

   @Override
   public List<MyPersonneEntity_Itf> findByCriteria (final TableCriteria<MyPersonneColumns_Enum> p_tableCriteria)
   {
      return super.findByCriteria(p_tableCriteria);
   }

   @Override
   public List<MyPersonneEntity_Itf> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name)
   {
      return super.findByCriteria(p_queryCriteria, p_map_value_by_name);
   }

   @Override
   public List<MyPersonneEntity_Itf> findByCriteria (final String p_queryCriteria,
            final Map<String, ? extends Object> p_map_value_by_name, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      return super.findByCriteria(p_queryCriteria, p_map_value_by_name, p_nbLignesMax, p_nbLignesStart);
   }

   @Override
   public int deleteByCriteria (final TableCriteria<MyPersonneColumns_Enum> p_tableCriteria)
   {
      return super.deleteByCriteria(p_tableCriteria);
   }

   @Override
   public List<MyPersonneEntity_Itf> findByColumn (final MyPersonneColumns_Enum p_column, final Object p_value)
   {
      return super.findByColumn(p_column, p_value);
   }

   @Override
   public List<MyPersonneEntity_Itf> findAll (final MyPersonneColumns_Enum p_orderByColumn)
   {
      return super.findAll(p_orderByColumn);
   }

   @Override
   protected Dao_Itf<Long, MyPersonneEntity_Itf, MyPersonneColumns_Enum> getDao ()
   {
      final MyUserPersistence v_userPersistence = MyParamPersistence.getUserPersistence();

      final MyPersonneDao_Itf v_PersonneDao_Itf = v_userPersistence.getInstanceOfPersonneDao();

      return v_PersonneDao_Itf;
   }

}
