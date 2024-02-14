/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.Parameters;
import fr.spi4j.ReflectUtil;
import fr.spi4j.business.UserBusiness_Abs;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.remoting.ServiceRemotingProxy.ExceptionResult;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet pour le remoting des services. <br/>
 * Côté serveur, la servlet doit être déclarée dans le fichier web.xml de l'application ainsi avec une balise servlet et une balise servlet-mapping et un url-pattern "/remoting/*" par exemple. <br/>
 * Côté client, le remoting des services doit être initialisé en appelant UserBusinessGen.getSingleton().initImplClient("http://localhost:8080/myapp/remoting") par exemple.
 * @author MINARM
 */
public class RemotingServlet extends HttpServlet
{
   private static final long serialVersionUID = -678338120447835264L;

   private static String versionApplication;

   private static UserBusiness_Abs userBusiness;

   private static final Map<String, Class<?>> c_map_primitiveTypes = new HashMap<>();

   private static SpiRemotingSerializer_Itf serializer = new JavaSerializer();

   static
   {
      c_map_primitiveTypes.put(Boolean.TYPE.getName(), Boolean.TYPE);
      c_map_primitiveTypes.put(Character.TYPE.getName(), Character.TYPE);
      c_map_primitiveTypes.put(Byte.TYPE.getName(), Byte.TYPE);
      c_map_primitiveTypes.put(Short.TYPE.getName(), Short.TYPE);
      c_map_primitiveTypes.put(Integer.TYPE.getName(), Integer.TYPE);
      c_map_primitiveTypes.put(Long.TYPE.getName(), Long.TYPE);
      c_map_primitiveTypes.put(Float.TYPE.getName(), Float.TYPE);
      c_map_primitiveTypes.put(Double.TYPE.getName(), Double.TYPE);
   }

   @Override
   public void init (final ServletConfig p_config) throws ServletException
   {
      super.init(p_config);
      // initialisation servletContext pour que la méthode Parameters.getParameter lise aussi les paramètres dans le contexte
      Parameters.initServletContext(p_config.getServletContext());
   }

   @Override
   protected void service (final HttpServletRequest p_request, final HttpServletResponse p_response)
            throws ServletException, IOException
   {
      if (!"POST".equals(p_request.getMethod()))
      {
         // seul un appel en POST (depuis un client Java) est autorisé pour cette servlet
         p_response.sendError(HttpServletResponse.SC_FORBIDDEN, "Direct access forbidden");
      }
      else
      {
         super.service(p_request, p_response);
      }
   }

   @Override
   public void doPost (final HttpServletRequest p_request, final HttpServletResponse p_response)
            throws ServletException, IOException
   {
      Object v_result;

      final String v_versionClient = p_request.getHeader(ServiceRemotingProxy.c_versionKey);
      if (v_versionClient != null && !v_versionClient.equals(versionApplication))
      {
         final IllegalVersionException v_exception = new IllegalVersionException(versionApplication, v_versionClient);
         v_result = new ExceptionResult(v_exception);
         p_response.setHeader(ServiceRemotingProxy.c_exceptionMessageKey, v_exception.toString());
      }
      else
      {

         // lit l'objet Request venant du client (ServiceRemotingProxy envoie toujours un objet de type Request)
         final RemotingRequest v_request = serverRead(p_request);

         try
         {
            // trouve l'instance du service et invoque la méthode du service côté serveur
            v_result = invoke(v_request);
         }
         catch (final Throwable v_ex)
         {
            // si le service a lancé une exception, alors on encapsule l'exception pour la transmettre à l'application cliente
            v_result = new ExceptionResult(v_ex);
            final String v_messageWithType = String.valueOf(v_ex);
            // on enlève '\n' et '\r' pour éviter http://en.wikipedia.org/wiki/HTTP_response_splitting
            p_response.setHeader(ServiceRemotingProxy.c_exceptionMessageKey, v_messageWithType.replace('\n', ' ')
                     .replace('\r', ' '));
         }
         if (v_result == null)
         {
            v_result = ServiceRemotingProxy.c_nullResult;
         }
      }

      // écrit le résultat du service (ou l'exception) vers l'application cliente
      // (l'objet doit être sérialisable pour pouvoir l'écrire en sérialisation java)
      serverWrite((Serializable) v_result, p_request, p_response);
   }

   /**
    * Dispatching de la requête vers le service demandé.
    * @param p_request
    *           Objet request décrivant le service demandé et les arguments
    * @return Résultat du service
    * @throws Throwable
    *            si exception
    */
   private Object invoke (final RemotingRequest p_request) throws Throwable
   {
      final String v_interfaceServiceName = p_request.getInterfaceServiceName();
      final Class<?> v_interfaceService = Class.forName(v_interfaceServiceName);
      final String v_methodName = p_request.getMethodName();
      final String[] v_parameterTypes = p_request.getParameterTypes();
      final Object[] v_args = p_request.getArgs();

      final UserBusiness_Abs v_userBusiness = getUserBusiness();
      final Object v_service = v_userBusiness.getBinding(v_interfaceService);
      final Method v_method = getServiceMethod(v_service, v_methodName, v_parameterTypes);
      try
      {
         return ReflectUtil.invokeMethod(v_method, v_service, v_args);
      }
      catch (final Throwable v_ex)
      {
         // si une exception (fonctionnelle par exemple) survient dans l'implémentation du service
         // alors elle est catchée ici
         // et on logue sur le serveur l'exception du service avec le même nom de logger et le même niveau de log que ServiceLogProxy
         // ainsi toutes les exceptions envoyées aux clients par le serveur sont loguées ici
         LogManager.getLogger(v_interfaceService.getSimpleName()).info(v_ex.toString(), v_ex);
         throw v_ex;
      }
   }

   /**
    * Retourne la méthode java correspondant au service demandé.
    * @param p_service
    *           Instance du service
    * @param p_methodName
    *           Nom de la méthode
    * @param p_parameterTypes
    *           Types des paramètres
    * @return Méthode java
    * @throws ClassNotFoundException
    *            si une classe est non trouvée
    * @throws NoSuchMethodException
    *            si la méthode est non trouvée
    */
   private Method getServiceMethod (final Object p_service, final String p_methodName, final String[] p_parameterTypes)
            throws ClassNotFoundException, NoSuchMethodException
   {
      // cette méthode n'est pas censée lancée d'exception en exécution puisque la méthode recherchée existe forcément (par génération de code ou implémentation de l'interface)
      // en public et retourne forcément le bon type (par génération de code ou implémentation de l'interface), mais au cas où une éventuelle exception serait bien transmise
      final Class<?>[] v_parameterClasses = new Class<?>[p_parameterTypes.length];
      for (int v_i = 0; v_i < p_parameterTypes.length; v_i++)
      {
         final String v_parameterType = p_parameterTypes[v_i];
         if (v_parameterType.indexOf('.') == -1 && c_map_primitiveTypes.containsKey(v_parameterType))
         {
            // si le nom de la classe du paramètre ne contient pas de '.' et qu'il est dans c_map_primitiveTypes, c'est qu'il s'agit d'un type primitif
            v_parameterClasses[v_i] = c_map_primitiveTypes.get(v_parameterType);
         }
         else
         {
            v_parameterClasses[v_i] = Class.forName(v_parameterType);
         }
      }
      return p_service.getClass().getMethod(p_methodName, v_parameterClasses);
   }

   /**
    * Lit dans le flux d'entrée de la requête http l'objet request envoyé par le client.
    * @param p_request
    *           Requête http
    * @return Objet request
    * @throws IOException
    *            Si exception d'entrée/sortie
    */
   protected RemotingRequest serverRead (final HttpServletRequest p_request) throws IOException
   {
      InputStream v_input = p_request.getInputStream();
      if ("gzip".equals(p_request.getHeader("Content-Encoding")))
      {
         // si la requête du client est compressée en gzip, alors on la décompresse
         v_input = new GZIPInputStream(v_input);
      }
      try
      {
         try
         {
            return (RemotingRequest) serializer.read(v_input);
         }
         catch (final ClassNotFoundException v_ex)
         {
            throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
         }
      }
      finally
      {
         // ce close doit être fait en finally
         // (http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html)
         v_input.close();
      }
   }

   /**
    * Ecrit dans le flux de sortie de la réponse http le résultat du service.
    * @param p_result
    *           Résultat du service (éventuellement de type NullResult ou ExceptionResult)
    * @param p_request
    *           Requête http
    * @param p_response
    *           Réponse http
    * @throws IOException
    *            Si erreur d'entrée/sortie
    */
   protected void serverWrite (final Serializable p_result, final HttpServletRequest p_request,
            final HttpServletResponse p_response) throws IOException
   {
      OutputStream v_output = new BufferedOutputStream(p_response.getOutputStream());
      final String v_acceptEncoding = p_request.getHeader("Accept-Encoding");
      if (v_acceptEncoding != null && v_acceptEncoding.contains("gzip"))
      {
         // on compresse le flux envoyé en gzip
         p_response.setHeader("Content-Encoding", "gzip");
         p_response.setHeader("Vary", "Accept-Encoding");
         v_output = new GZIPOutputStream(v_output);
      }
      serializer.write(p_result, v_output);
   }

   /**
    * Retourne la factory UserBusiness associée à cette servlet par {@link #setUserBusiness(UserBusiness_Abs)}.
    * @return UserBusiness_Abs
    */
   public static UserBusiness_Abs getUserBusiness ()
   {
      if (userBusiness == null)
      {
         throw new IllegalStateException(
                  "Le paramètre 'userBusiness' n'a pas été défini. Avez-vous appelé RemotingServlet.setUserBusiness(...) (et potentiellement myUserBusiness.importBindings(...) si besoin) lors de l'initialisation du serveur ?");
      }
      return userBusiness;
   }

   /**
    * Associe l'instance de la factory UserBusiness à cette servlet : cette méthode est appelée dans l'implémentation de UserBusinessGen.initServerBindings.
    * @param p_userBusiness
    *           UserBusiness_Abs
    */
   public static void setUserBusiness (final UserBusiness_Abs p_userBusiness)
   {
      if (p_userBusiness == null)
      {
         throw new IllegalArgumentException("Le paramètre 'userBusiness' est obligatoire, il ne peut pas être 'null'\n");
      }
      userBusiness = p_userBusiness;
   }

   /**
    * Définit la version de l'application.
    * @param p_versionApplication
    *           Version de l'application
    */
   public static void setVersionApplication (final String p_versionApplication)
   {
      versionApplication = p_versionApplication;
   }

   /**
    * Utilise un serializer pour transférer les données via HTTP (Défaut : JavaSerializer. Alternative : XmlSerializer)
    * @param p_serializer
    *           le serializer
    */
   public static void setSerializer (final SpiRemotingSerializer_Itf p_serializer)
   {
      serializer = p_serializer;
   }
}
