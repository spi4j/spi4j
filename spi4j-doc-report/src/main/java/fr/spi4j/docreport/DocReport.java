/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import fr.spi4j.docreport.impl.SpiDocReport;

/**
 * Interface principale de l'api du projet spi-doc-report : ce projet simplifie
 * l'utilisation de XDocReport, pour un usage simple.
 * <p>
 * Interface implémentée par SpiDocReport.
 * 
 * @author MINARM
 */
public interface DocReport {
	/**
	 * Instance de l'implémentation (par défaut) de cette interface.
	 */
	DocReport c_INSTANCE = new SpiDocReport();

	/**
	 * @return Moteur de template global ("Freemarker" par défaut, ou alors
	 *         "Velocity").
	 */
	String getTemplateEngineName();

	/**
	 * Définit le moteur de template global ("Freemarker" par défaut, ou alors
	 * "Velocity").
	 * 
	 * @param p_templateEngineName String
	 */
	void setTemplateEngineName(final String p_templateEngineName);

	/**
	 * Génère un document à partir d'un modèle (template) et de valeurs (soit ODT
	 * vers ODT, soit DOCX vers DOCX).
	 * 
	 * @param p_filePathInClassPathOrTemplateId Chemin du template ODT ou DOCX dans
	 *                                          le classpath (par exemple :
	 *                                          /templates/courrier_annuaire.odt) ou
	 *                                          bien templateId après un
	 *                                          {@link #loadTemplate(InputStream, String)}
	 * @param p_context                         Valeurs identifiées par des clés ;
	 *                                          les valeurs peuvent être des objets
	 *                                          avec des getters.
	 * @param p_output                          Flux de sortie où le document est
	 *                                          généré (par exemple un
	 *                                          FileOutputStream, un
	 *                                          ServletOutputStream ou un
	 *                                          ByteArrayOutputStream)
	 * @throws IOException e
	 */
	void process(final String p_filePathInClassPathOrTemplateId, final Map<String, Object> p_context,
			final OutputStream p_output) throws IOException;

	/**
	 * Génère un document à partir d'un modèle (template) et de valeurs, puis le
	 * convertit en document PDF.
	 * 
	 * @param p_filePathInClassPathOrTemplateId Chemin du template ODT ou DOCX dans
	 *                                          le classpath (par exemple :
	 *                                          /templates/courrier_annuaire.odt) ou
	 *                                          bien templateId après un
	 *                                          {@link #loadTemplate(InputStream, String)}
	 * @param p_context                         Valeurs identifiées par des clés ;
	 *                                          les valeurs peuvent être des objets
	 *                                          avec des getters.
	 * @param p_output                          Flux de sortie où le document PDF
	 *                                          est généré (par exemple un
	 *                                          FileOutputStream, un
	 *                                          ServletOutputStream ou un
	 *                                          ByteArrayOutputStream)
	 * @throws IOException e
	 */
	void processToPdf(final String p_filePathInClassPathOrTemplateId, final Map<String, Object> p_context,
			final OutputStream p_output) throws IOException;

	/**
	 * Génère un document à partir d'un modèle (template) et de valeurs, puis le
	 * convertit en document HTML.
	 * 
	 * @param p_filePathInClassPathOrTemplateId Chemin du template ODT ou DOCX dans
	 *                                          le classpath (par exemple :
	 *                                          /templates/courrier_annuaire.odt) ou
	 *                                          bien templateId après un
	 *                                          {@link #loadTemplate(InputStream, String)}
	 * @param p_context                         Valeurs identifiées par des clés ;
	 *                                          les valeurs peuvent être des objets
	 *                                          avec des getters.
	 * @param p_output                          Flux de sortie où le document HTML
	 *                                          est généré (par exemple un
	 *                                          FileOutputStream, un
	 *                                          ServletOutputStream ou un
	 *                                          ByteArrayOutputStream)
	 * @throws IOException e
	 */
	void processToHtml(final String p_filePathInClassPathOrTemplateId, final Map<String, Object> p_context,
			final OutputStream p_output) throws IOException;

	/**
	 * Charge un template à partir d'un flux InputStream : utile quand le template
	 * n'est pas dans les ressources du classpath, mais dans un fichier à part ou
	 * dans une base de données.
	 * 
	 * @param p_inputStream InputStream d'un template, par exemple un
	 *                      FileInputStream ou un ByteArrayInputStream
	 * @param p_templateId  Clé unique pour identifier ensuite ce template lors de
	 *                      l'appel à {@link #process(String, Map, OutputStream)}
	 * @throws IOException e
	 */
	void loadTemplate(final InputStream p_inputStream, String p_templateId) throws IOException;

	/**
	 * Retourne l'instance de convertisseur adapté au format du fichier en paramètre
	 * (soit ODT, soit DOCX).
	 * 
	 * @param p_fileName Nom ou chemin du fichier avec extension .odt ou .docx (par
	 *                   exemple : /templates/courrier_annuaire.odt)
	 * @return DocConverter
	 */
	DocConverter getDocConverterForFile(final String p_fileName);
}
