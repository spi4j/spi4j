/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.util;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;

import fr.spi4j.report.BirtEngine_Itf;
import fr.spi4j.report.ReportException;

/**
 * @author MINARM
 */
public final class BirtUtils implements BirtEngine_Itf
{
   /** L'instance singleton */
   private static final BirtEngine_Itf c_instance = new BirtUtils();

   private IReportEngine _reportEngine;

   private IDesignEngine _designEngine;

   /**
    * Constructeur : private cas une seule instance.
    */
   private BirtUtils ()
   {
      super();
   }

   /**
    * Permet d'obtenir une instance de ReportEngine_Itf.
    * @return L'intance de ReportEngine_Itf.
    */
   public static BirtEngine_Itf getInstance ()
   {
      return c_instance;
   }

   @Override
   public void startPlatform ()
   {
      try
      {
         // Demarrage du moteur
         final EngineConfig v_config = new EngineConfig();
         Platform.startup(v_config);
         final IReportEngineFactory v_reportFactory = (IReportEngineFactory) Platform
                  .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
         _reportEngine = v_reportFactory.createReportEngine(v_config);

         final DesignConfig v_designConfig = new DesignConfig();
         final IDesignEngineFactory v_designFactory = (IDesignEngineFactory) Platform
                  .createFactoryObject(IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY);
         _designEngine = v_designFactory.createDesignEngine(v_designConfig);

      }
      catch (final BirtException v_erreur)
      {
         throw new ReportException("Problème pour démarrer le moteur", v_erreur);
      }
   }

   @Override
   public void stopPlatform ()
   {
      // Fermeture du moteur
      if (_reportEngine != null)
      {
         _reportEngine.destroy();
      }
      Platform.shutdown();
   }

   @Override
   public IReportEngine getReportEngine ()
   {
      if (_reportEngine == null || _designEngine == null)
      {
         startPlatform();
      }
      return _reportEngine;
   }

   @Override
   public IDesignEngine getDesignEngine ()
   {
      if (_reportEngine == null || _designEngine == null)
      {
         startPlatform();
      }
      return _designEngine;
   }
}
