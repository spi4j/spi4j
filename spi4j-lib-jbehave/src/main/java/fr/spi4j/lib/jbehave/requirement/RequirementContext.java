/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.requirement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.requirement.Requirement_Itf;

/**
 * Permet de suivre les méthodes portant une annotation RequirementAnnotationForTest qui ont été invoquées.<br/>
 * Permet aussi de demander quelles méthodes ont été invoquées pour un RequirementAnnotationForTest donné.<br/>
 * Permet donc d'en déduire si un RequirementAnnotationForTest a été utilisé pendant l'execution d'un programme.
 * @author MINARM
 */
public class RequirementContext
{

   private final Map<Requirement_Itf, List<Method>> _usages = new HashMap<>();

   /**
    * Une exigence est tirée.
    * @param p_req
    *           l'exigence tirée
    * @param p_method
    *           la méthode dans laquelle l'exigence est tirée
    */
   public void used (final Requirement_Itf p_req, final Method p_method)
   {
      LogManager.getLogger(getClass()).debug("Passage de l'exigence : " + p_req.getName());
      List<Method> v_methods = _usages.get(p_req);
      if (v_methods == null)
      {
         v_methods = new ArrayList<>();
         _usages.put(p_req, v_methods);
      }
      v_methods.add(p_method);
   }

   /**
    * Cherche les utilisations d'une exigence.
    * @param p_requirement
    *           l'exigence
    * @return les méthodes qui tirent l'exigence
    */
   public List<Method> usagesFor (final Requirement_Itf p_requirement)
   {
      final List<Method> v_list = _usages.get(p_requirement);
      if (v_list != null)
      {
         return v_list;
      }
      else
      {
         return Collections.emptyList();
      }
   }

   /**
    * Cherche si une exigence a été tirée.
    * @param p_requirement
    *           l'exigence
    * @return true si l'exigence a été tirée, false sinon
    */
   public boolean hasBeenUsed (final Requirement_Itf p_requirement)
   {
      return !usagesFor(p_requirement).isEmpty();
   }

   /**
    * Réinitialise le context d'exigences.
    */
   public void clear ()
   {
      _usages.clear();
   }
}
