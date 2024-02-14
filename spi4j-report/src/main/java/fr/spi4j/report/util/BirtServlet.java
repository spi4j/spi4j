/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.util;

import java.io.IOException;

import fr.spi4j.report.PageDesign_Itf;
import fr.spi4j.report.ReportFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet servant d'exemple pour utilisation de BIRT dans une webapp. <br/>
 * @author MINARM
 */
public class BirtServlet extends HttpServlet
{
   // Cette servlet peut être déclarée dans le fichier web.xml d'une webapp ainsi : *
   // <servlet>
   // <servlet-name>BirtServlet</servlet-name>
   // <servlet-class>fr.spi4j.report.BirtServlet</servlet-class>
   // </servlet>
   // <servlet-mapping>
   // <servlet-name>BirtServlet</servlet-name>
   // <url-pattern>/report/*</url-pattern>
   // </servlet-mapping>

   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet (final HttpServletRequest p_req, final HttpServletResponse p_resp) throws ServletException,
            IOException
   {
      // ceci n'est qu'un exemple, et dans la vraie vie il faudrait avoir un "contrôleur" qui prépare les données pour ce report en particulier
      // et en fonction de paramètres fournis par le client
      final String v_report = p_req.getParameter("report");
      final PageDesign_Itf v_pageBirt = ReportFactory.createPageBirt(v_report);
      final ServletOutputStream v_outputStream = p_resp.getOutputStream();
      p_resp.setContentType("application/pdf");

      // final Document_Itf v_document = ReportFactory.createDocument();
      // v_document.addPage(v_pageBirt);
      // v_document.writeDocumentWithoutPageNumbers(v_outputStream);

      // dans le cas simple indiqué en commentaire ci-dessus, il est équivalent et plus efficace de faire directement comme ceci :
      v_pageBirt.writeReport(v_outputStream);
   }

   @Override
   public void init (final ServletConfig p_config) throws ServletException
   {
      // On n'initialise pas la plateforme Birt lors du démarrage de la webapp, car c'est trop long en tests : elle sera initialisée au besoin à la première utilisation
      // BirtUtils.getInstance().startPlatform();

      super.init(p_config);
   }

   @Override
   public void destroy ()
   {
      // lors de l'arrêt de la webapp, on arrête la plateforme Birt
      BirtUtils.getInstance().stopPlatform();
      super.destroy();
   }
}
