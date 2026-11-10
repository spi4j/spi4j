/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.admin;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Un exemple de servlet utilisée pour retourner les informations sur la version déployée de l'application.
 * @author MINARM
 */
public abstract class BuildInfoServlet extends HttpServlet
{

   private static final long serialVersionUID = 1L;

   @Override
   public void doGet (final HttpServletRequest p_request, final HttpServletResponse p_response)
            throws ServletException, IOException
   {
      p_response.setContentType("text/html");
      final OutputStream v_os = p_response.getOutputStream();
      v_os.write(getContent().getBytes(getInfoCharset()));
   }

   /**
    * @return le contenu de la page à afficher
    */
   protected String getContent ()
   {
      final StringBuilder v_stringContent = new StringBuilder();
      final BuildInfo_Abs v_buildInfo = getBuildInfo();
      v_stringContent.append("Application : <b>").append(v_buildInfo.getNomApplication()).append("</b>")
               .append("<br />");
      v_stringContent.append("Version : <b>").append(v_buildInfo.getVersion()).append("</b>").append("<br />");
      v_stringContent.append("URL : <b>").append(v_buildInfo.getUrl()).append("</b>").append("<br />");
      v_stringContent.append("Revision : <b>").append(v_buildInfo.getRevision()).append("</b>").append("<br />");
      v_stringContent.append("Version Spi4J : <b>").append(v_buildInfo.getSpi4jVersion()).append("</b>")
               .append("<br />");
      return v_stringContent.toString();
   }

   /**
    * @return l'instance de BuildInfo, qui contient les informations sur le build
    */
   protected abstract BuildInfo_Abs getBuildInfo ();

   /**
    * @return le charset de retour de la servlet
    */
   protected String getInfoCharset ()
   {
      // Lecture a priori d'un fichier properties, donc Charset prévu en ISO
      return "ISO-8859-1";
   }
}
