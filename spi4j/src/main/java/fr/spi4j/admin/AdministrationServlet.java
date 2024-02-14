/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.business.ServiceCacheProxy;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.UserPersistence_Abs;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet pour l'administration de l'application. <br/>
 * Côté serveur, la servlet doit être déclarée dans le fichier web.xml de l'application avec une balise servlet et une balise servlet-mapping et un url-pattern "/admin/*" par exemple. <br/>
 * Il faut s'assurer que cette servlet soit sécurisée dans le web.xml afin que n'importe qui ne puisse pas y accéder.
 * @author MINARM
 */
public class AdministrationServlet extends HttpServlet
{
   private static final long serialVersionUID = -3889117139237978524L;

   private static final Map<String, byte[]> c_servletContents = new HashMap<>();

   private static final Logger c_log = LogManager.getLogger(AdministrationServlet.class);

   @Override
   protected void service (final HttpServletRequest p_request, final HttpServletResponse p_response)
            throws ServletException, IOException
   {
      if (!"GET".equals(p_request.getMethod()))
      {
         // seul un appel en GET (depuis un navigateur) est autorisé pour cette servlet
         p_response.sendError(HttpServletResponse.SC_FORBIDDEN, "Servlet appelable par le navigateur");
      }
      else
      {
         super.service(p_request, p_response);
      }
   }

   @Override
   public void doGet (final HttpServletRequest p_request, final HttpServletResponse p_response)
            throws ServletException, IOException
   {
      final String v_path = p_request.getPathInfo();
      final AdministrationService_Itf v_service = findService(v_path);
      if (v_service == null)
      {
         throw new ServletException("Service Inconnu : " + v_path);
      }
      if (!isServiceAvailable(v_service))
      {
         throw new ServletException("Service non disponible : " + v_path);
      }
      executeService(v_service, p_request.getParameterMap());

      p_response.setContentType("text/html;charset=UTF-8");
      final OutputStream v_os = p_response.getOutputStream();
      v_os.write(getResourceAsBytes(v_service.getServletContentPath()));
      v_os.close();

   }

   /**
    * Cherche un service selon son url (commençant par un /)
    * @param p_path
    *           l'url du service
    * @return le service trouvé ou null si aucun service trouvé à cette url
    */
   protected AdministrationService_Itf findService (final String p_path)
   {
      return AdministrationServiceEnum.getServiceFromPath(p_path);
   }

   /**
    * Cette méthode est surchargeable pour rendre indisponible certains services dans l'application.
    * @param p_service
    *           le service à tester
    * @return true si le service est disponible (par défaut), false sinon
    */
   protected boolean isServiceAvailable (final AdministrationService_Itf p_service)
   {
      return true;
   }

   /**
    * Exécute le service d'administration
    * @param p_service
    *           le service demandé
    * @param p_parameters
    *           les paramètres de la requête
    */
   @SuppressWarnings("rawtypes")
   protected void executeService (final AdministrationService_Itf p_service, final Map p_parameters)
   {
      if (p_service instanceof AdministrationServiceEnum)
      {
         switch ((AdministrationServiceEnum) p_service)
         {
         case INDEX:
            break;
         case CLEAR_CACHES:
            c_log.info("Purge du cache");
            ServiceCacheProxy.clearCaches();
            break;
         case CHECK_DATABASE:
            final StringBuilder v_sb = new StringBuilder();
            for (final UserPersistence_Abs v_userPersistence : getUserPersistences())
            {
               try
               {
                  v_userPersistence.checkDatabase();
               }
               catch (final Exception v_e)
               {
                  v_sb.append(v_e.getMessage()).append('\\');
               }
            }
            if (v_sb.length() != 0)
            {
               throw new Spi4jRuntimeException(v_sb.toString(),
                        "Contactez l'administrateur pour vérifier la cohérence des tables dans la base de données");
            }
         default:
         }
      }
   }

   /**
    * Surcharger cette méthode avec : <code>return Arrays.asList(fr.monappli.persistence.ParamPersistenceGen_Abs.getUserPersistence());</code>
    * @return les instances de UserPersistence de l'application
    */
   protected List<UserPersistence_Abs> getUserPersistences ()
   {
      throw new Spi4jRuntimeException("Impossible de vérifier la cohérence de la base de données",
               "Surcharger la méthode AdministrationServlet.getUserPersistences()");
   }

   /**
    * Retourne le contenu d'une page
    * @param p_servletContent
    *           la page à afficher
    * @return le contenu de la page (mis en cache)
    * @throws ServletException
    *            si impossible de lire le contenu de la page
    */
   private byte[] getResourceAsBytes (final String p_servletContent) throws ServletException
   {
      if (!c_servletContents.containsKey(p_servletContent))
      {
         // Il faut rechercher le contenu dans la classe concrète ainsi que dans les classes parentes.
         Class<?> v_clazz = getClass();
         InputStream v_is = null;
         do
         {
            v_is = v_clazz.getResourceAsStream(p_servletContent);
            v_clazz = v_clazz.getSuperclass();
         }
         while (v_is == null && v_clazz != null);

         if (v_is == null)
         {
            throw new ServletException("Contenu introuvable : " + p_servletContent);
         }

         int v_read;
         final byte[] v_buffer = new byte[1024];
         final ByteArrayOutputStream v_outputStream = new ByteArrayOutputStream();
         try
         {
            while (v_is.available() > 0)
            {
               v_read = v_is.read(v_buffer);
               v_outputStream.write(v_buffer, 0, v_read);
            }
            c_servletContents.put(p_servletContent, v_outputStream.toByteArray());
         }
         catch (final IOException v_e)
         {
            throw new ServletException("Impossible de lire le contenu de " + p_servletContent, v_e);
         }
         finally
         {
            try
            {
               v_is.close();
            }
            catch (final IOException v_e)
            {
               c_log.warn("Impossible de fermer le flux pour le fichier " + p_servletContent);
            }
         }
      }
      return c_servletContents.get(p_servletContent);
   }
}
