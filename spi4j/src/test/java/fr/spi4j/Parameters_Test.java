/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletContext;

/**
 * Test unitaire de la classe Parameters.
 * @author MINARM
 */
public class Parameters_Test
{
   /**
    * Test de init_servletContext.
    */
   @Test
   public void testInitServletContext ()
   {
      final ServletContext v_servletContext = Mockito.mock(ServletContext.class);
      Parameters.initServletContext(v_servletContext);
   }

   /**
    * Test qu'un paramètre non défini a pour valeur null.
    */
   @Test
   public void testUndefinedGetParameter ()
   {
      assertNull(Parameters.getParameter("rien", null), "get_parameter");

      final ServletContext v_servletContext = Mockito.mock(ServletContext.class);
      Parameters.initServletContext(v_servletContext);
      try
      {
         assertNull(Parameters.getParameter("rien", null), "get_parameter");
      }
      finally
      {
         Parameters.initServletContext(null);
      }
   }

   /**
    * Test de la valeur d'un paramètre défini avec par priorité : propriétés systèmes, contexte de servlet puis valeur par défaut.
    */
   @Test
   public void testDefinedGetParameter ()
   {
      // récupération d'une valeur (PATH) qui a priori est non nulle dans les variables d'environnement de l'OS (en Windows et en Linux)
      assertNotNull(Parameters.getParameter("PATH", null), "get_parameter");

      assertEquals(Parameters.getParameter("parameter", "valeur par défaut"), "valeur par défaut", "get_parameter");

      final ServletContext v_servletContext = Mockito.mock(ServletContext.class);
      Mockito.when(v_servletContext.getInitParameter("parameter")).thenReturn("valeurDansContext");
      Parameters.initServletContext(v_servletContext);

      try
      {
         assertEquals(Parameters.getParameter("parameter", null), "valeurDansContext", "get_parameter");
      }
      finally
      {
         Parameters.initServletContext(null);
      }

      System.setProperty("parameter", "valeurDansSystemProperties");
      try
      {
         assertEquals(Parameters.getParameter("parameter", null), "valeurDansSystemProperties", "get_parameter");
      }
      finally
      {
         System.getProperties().remove("parameter");
      }
   }
}
