/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.remoting;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.testapp.MyPersonneDto;
import fr.spi4j.testapp.MyPersonneService_Itf;
import fr.spi4j.testapp.MyUserBusinessGen;
import fr.spi4j.testapp.test.DatabaseInitialization;
import jakarta.servlet.ServletException;

/**
 * Classe de test du remoting.
 * @author MINARM
 */
public class Remoting_Test
{
   private static final String c_contextName = "/testwebapp";

   private static final int c_socket_port = 8123;

   /**
    * Test du remoting.
    * @throws Spi4jValidationException
    *            si erreur de validation d'une entity.
    * @throws IOException
    *            si erreur de lecture / écriture
    * @throws ServletException
    *            si erreur de servlet
    */
   @Test
   public void remoting () throws Spi4jValidationException, IOException, ServletException
   {
      DatabaseInitialization.initDatabase();
      // initialisation de l'implémentation "serveur" des services
      final MyUserBusinessGen v_userBusiness = MyUserBusinessGen.getSingleton();
      v_userBusiness.initBindings();
      RemotingServlet.setUserBusiness(v_userBusiness);

      // initialisation de l'url du serveur http à appeler dans le proxy de remoting
      ServiceRemotingProxy.setServerUrl("http://localhost:" + c_socket_port + c_contextName);

      // démarrage d'un serveur http sur le port 8123 (port peu courant qui doit être libre)
      final InetSocketAddress v_port = new InetSocketAddress(c_socket_port);
      final HttpServer v_server = HttpServer.create(v_port, 0);
      final HttpServerHandler v_myHandler = new HttpServerHandler();
      v_server.createContext(c_contextName, v_myHandler);
      final ExecutorService v_threadPool = Executors.newFixedThreadPool(1);
      try
      {
         v_server.setExecutor(v_threadPool);
         try
         {
            v_server.start();

            // test des proxys de remoting en tant qu'application cliente
            runTest();
         }
         finally
         {
            v_server.stop(0);
         }
      }
      finally
      {
         v_threadPool.shutdownNow();
         v_myHandler.destroy();
      }
   }

   /**
    * Test d'appel des services en remoting.
    * @throws Spi4jValidationException
    *            si erreur de création de l'entity.
    */
   private void runTest () throws Spi4jValidationException
   {
      final MyPersonneService_Itf v_clientService = ServiceRemotingProxy.createProxy(MyPersonneService_Itf.class);
      assertNotNull(v_clientService, "createProxy");
      MyPersonneDto v_personne = new MyPersonneDto();
      v_personne.setNom("Dupond");
      v_personne.setPrenom("Jean");
      v_personne.setCivil(true);

      // création
      v_personne = v_clientService.save(v_personne);
      assertNotNull(v_personne, "save");

      // mise à jour
      v_personne = v_clientService.save(v_personne);
      assertNotNull(v_personne, "save");

      // liste complète (la liste n'est pas vide puisque l'on a créé une personne ci-dessus)
      assertNotNull(v_clientService.findAll(), "findAll");

      // find unitaire
      v_personne = v_clientService.findById(v_personne.getId());
      assertNotNull(v_personne, "findById");

      // tests version application
      v_personne = runTestVersionApplication(v_clientService, v_personne);

      // delete (résultat void et l'on vérifie que le remoting ne fait pas de NullPointerException)
      v_clientService.delete(v_personne);

      // deuxième delete sur la même donnée: il doit y avoir une exception
      // et l'on vérifie que cette exception est bien remontée et relancée par le remoting
      Exception v_deleteException = null;
      try
      {
         v_clientService.delete(v_personne);
      }
      catch (final Exception v_ex)
      {
         v_deleteException = v_ex;
      }
      assertTrue(
               v_deleteException != null && v_deleteException.getMessage() != null
                        && v_deleteException.getMessage().startsWith("Objet déjà modifié ou supprimé"),
               "l'exception attendue dans remoting du delete n'a pas été lancée");

      // Remarque : ce test unitaire laisse éventuellement dans la base de données une personne supprimée logiquement après chaque exécution
      // (pas de rollback de transaction possible puisqu'elle est déjà terminée)
      // mais cette personne supprimée logiquement n'apparaîtra pas dans l'ihm de l'application
      // et ce n'est pas bien grave car le nombre de personnes n'augmente pas vite
   }

   /**
    * Test de la comparaison de versions.
    * @param p_clientService
    *           MyPersonneService_Itf
    * @param p_personne
    *           MyPersonneDto
    * @return MyPersonneDto
    */
   private MyPersonneDto runTestVersionApplication (final MyPersonneService_Itf p_clientService,
            final MyPersonneDto p_personne)
   {
      RemotingServlet.setVersionApplication("123");
      ServiceRemotingProxy.setVersionApplication("123");

      MyPersonneDto v_personne = p_clientService.findById(p_personne.getId());
      assertNotNull(v_personne, "findById");

      RemotingServlet.setVersionApplication("123");
      ServiceRemotingProxy.setVersionApplication("456");
      try
      {
         v_personne = p_clientService.findById(v_personne.getId());
         fail();
      }
      catch (final Exception v_e)
      {
         // ok
         assertTrue(v_e instanceof IllegalVersionException);
      }
      finally
      {

         RemotingServlet.setVersionApplication(null);
         ServiceRemotingProxy.setVersionApplication(null);
      }
      return p_personne;
   }
}
