/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package tutorial;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.junit.jupiter.api.Test;

import bean.SampleBeanDataSet4Chart;
import bean.SampleBeanDataSet4Table;
import bean.SampleBeanDataSet4TableGroup;
import fr.spi4j.report.Document_Itf;
import fr.spi4j.report.PageDesign_Itf;
import fr.spi4j.report.Page_Itf;
import fr.spi4j.report.ReportException;
import fr.spi4j.report.ReportFactory;
import fr.spi4j.report.impl.PageOrientation_Enum;
import fr.spi4j.report.util.ReportUtils;

/**
 * Test de la classe Page. Attention, il faut des rapports au format
 * birt(rptdesign)!
 * 
 * @author MINARM
 */
public class TutoReport_Test {
	private static final String c_target_directory = "target/";

	private static final String c_rapport_tuto1 = "/tutorial/TutoReport1.pdf";

	private static final String c_rapport_tuto2 = "/tutorial/TutoReport2.pdf";

	private static final String c_rapport_TutoReportTable = "/tutorial/TutoReportTable.pdf";

	private static final String c_rapport_TutoReportTableGroup = "/tutorial/TutoReportTableGroup.pdf";

	// private static final String c_rapport_TutoReportTableImbriquee =
	// "/tutorial/TutoReportTableImbriquee.pdf";

	private static final String c_rapport_TutoReportChart = "/tutorial/TutoReportChart.pdf";

	private static final String c_rapport_TutoReportImageStatiqueIncorporee = "/tutorial/TutoReportImageStatiqueIncorporee.pdf";

	private static final String c_rapport_TutoReportImageDynamiquementIncorporee = "/tutorial/TutoReportImageDynamiquementIncorporee.pdf";

	private static final String c_rapport_TutoReportAffectationVariable = "/tutorial/TutoReportAffectationVariable.pdf";

	private static final String c_rapport_TutoReportOrientationPage = "/tutorial/TutoReportOrientationPage.pdf";

	// private static final String c_rapport_TutoReportInternalLink =
	// "/tutorial/TutoReportInternalLink.pdf";

	private static final String c_txt_lienUrl = "txt_lienUrl";

	private static final String c_image = "/fr/spi4j/report/safran.png";

	private static final String c_design_TutoReportPremiereGeneration = "/tutorial/TutoReportPremiereGeneration.rptdesign";

	private static final String c_design_TutoReportTable = "/tutorial/TutoReportTable.rptdesign";

	private static final String c_design_TutoReportTableGroup = "/tutorial/TutoReportTableGroup.rptdesign";

	// private static final String c_design_TutoReportTableImbriquee =
	// "/tutorial/TutoReportTableImbriquee.rptdesign";

	private static final String c_design_TutoReportChart = "/tutorial/TutoReportChart.rptdesign";

	private static final String c_design_TutoReportImageStatiqueIncorporee = "/tutorial/TutoReportImageStatiqueIncorporee.rptdesign";

	private static final String c_design_TutoReportImageDynamiquementIncorporee = "/tutorial/TutoReportImageDynamiquementIncorporee.rptdesign";

	private static final String c_design_TutoReportAffectationVariable = "/tutorial/TutoReportAffectationVariable.rptdesign";

	private static final String c_design_TutoReportOrientationPage = "/tutorial/TutoReportOrientationPage.rptdesign";

	// private static final String c_design_TutoReportInternalLink =
	// "/tutorial/TutoReportInternalLink.rptdesign";

	/**
	 * Première génération d'édition.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testPremiereGeneration() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportPremiereGeneration);

		v_Page.createURLLink(c_txt_lienUrl, "http://www.intranet.defense.gouv.fr/index.html");

		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		// Edition et sauvegarde du document PDF
		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		v_documentPdf.writeDocumentWithoutPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_tuto1);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec une table simple.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testTableSimple() throws IOException {
		// Obtenir une page
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportTable);
		// Créer un document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		// Remplissage des pages par les beans
		v_Page.makeReportWithContainer("DataSourceSampleBeanDataSet4Table", "DataSetSampleBeanDataSet4Table",
				SampleBeanDataSet4Table.class, "tbl_SampleBeanDataSet4Table");

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportTable);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec une table groupée.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testTableGroupee() throws IOException {
		// Obtenir une page
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportTableGroup);
		// Créer un document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		// Remplissage des pages par les beans en groupant sur la colonne 'nom'
		v_Page.makeReportWithListAndGroup("DataSource4TableGroupee", "DataSetSampleBean4TableGroupee",
				SampleBeanDataSet4TableGroup.class, "tbl_SampleBeanDataSet4TableGroup", "_nom");

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportTableGroup);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec une Table imbriquee.
	 * 
	 * @throws IOException Si erreur.
	 */
	// TODO à corriger et à réactiver
	// @Test
	// public void testTableImbriquee () throws IOException
	// {
	// // Obtenir une page
	// final PageDesign_Itf v_Page =
	// ReportFactory.createPageBirt(c_design_TutoReportTableImbriquee);
	// // Créer un document PDF
	// final Document_Itf v_documentPdf = ReportFactory.createDocument();
	// // Ajout des pages au document PDF
	// v_documentPdf.addPage(v_Page);
	//
	// // Remplissage des pages par les beans
	// v_Page.makeReportWithContainer("DataSourceSampleBeanDataSet4TableImbriquee",
	// "DataSetSampleBeanDataSet4TableImbriquee",
	// SampleBeanDataSet4TableImbriquee.class,
	// "tbl_SampleBeanDataSet4TableImbriquee");
	//
	// final ByteArrayOutputStream v_ByteArrayOutputStream = new
	// ByteArrayOutputStream(1024);
	// // Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
	// v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);
	//
	// // Les octets représentant le PDF
	// final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
	// assertNotNull("Le PDF ne devrait pas être vide", v_tab_result);
	//
	// // Définir le fichier pour écrire les octets du PDF
	// final File v_fichierPdf = new File(c_target_directory +
	// c_rapport_TutoReportTableImbriquee);
	// v_fichierPdf.getParentFile().mkdirs();
	// final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new
	// FileOutputStream(v_fichierPdf));
	// try
	// {
	// // Ecrire les données du PDF
	// v_pdfOutputStream.write(v_tab_result);
	// }
	// finally
	// {
	// // Fermer le fichier
	// v_pdfOutputStream.close();
	// }
	// }

	/**
	 * Génération d'édition avec un chart (cas nominal).
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testChart_CN() throws IOException {
		// Obtenir une page
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportChart);
		// Créer un document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		// Remplissage des pages par les beans en spécifiant :
		// - la légende (ex : nom)
		// - et la valeur à représenter (ex : age)
		// Remarque : S'il y a plusieurs valeurs pour la même légende, alors les valeurs
		// sont sommées
		v_Page.makeReportWithChartWithAxes("DataSourceSampleBean_histo", "DataSetSampleBean_histo",
				SampleBeanDataSet4Chart.class, "chart_histo", "_nom", "_age");
		v_Page.makeReportWithChartWithoutAxes("DataSourceSampleBean_camembert1", "DataSetSampleBean_camembert1",
				SampleBeanDataSet4Chart.class, "chart_camembert1", "_nom", "_age");
		v_Page.makeReportWithChartWithoutAxes("DataSourceSampleBean_camembert2", "DataSetSampleBean_camembert2",
				SampleBeanDataSet4Chart.class, "chart_camembert2", "_nomAndPrenom", "_age");

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportChart);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec un chart (cas d'exception No1) - Provoquer une
	 * erreur en fournissant un DataSet ne possedant pas la colonne spécifiée.
	 */
	@Test
	public void testChart_CE1() {
		assertThrows(ReportException.class, () -> {
			// Obtenir une page
			final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportChart);
			// Créer un document PDF
			final Document_Itf v_documentPdf = ReportFactory.createDocument();
			// Ajout des pages au document PDF
			v_documentPdf.addPage(v_Page);

			// Remplissage des pages par les beans avec 'SampleBeanDataSet4Table' au lieu de
			// 'SampleBeanDataSet4Chart'
			// --> Pas de colonne '_nomAndPrenom'
			v_Page.makeReportWithChartWithoutAxes("DataSourceSampleBean_camembert2", "DataSetSampleBean_camembert2",
					SampleBeanDataSet4Table.class, "chart_camembert2", "_nomAndPrenom", "_age");
		});
	}

	/**
	 * Génération d'édition avec une image statique incorporée dans le rptdesign.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testImageStatiqueIncorporee() throws IOException {
		// Obtenir une page
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportImageStatiqueIncorporee);
		// Créer un document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportImageStatiqueIncorporee);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec un flux d'image incorporée à la volée dans le
	 * rptdesign.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testImageDynamiquementIncorporee() throws IOException {
		// Obtenir une page
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportImageDynamiquementIncorporee);
		// Lire une image
		final byte[] v_fluxImageToAdd = ReportUtils.transformPictureToByte(c_image);
		// Affecter le flux de l'image dynamiquement (via le nom dans le rptDesign
		// "image_vide.png") au report
		v_Page.makePictureElementEmbedded("image_vide.png", v_fluxImageToAdd,
				DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
		// Créer un document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportImageDynamiquementIncorporee);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec affectation de la valeur d'une variable à la volée.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testAffectationVariable_CN() throws IOException {
		// Obtenir une page
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportAffectationVariable);
		// Affecter le Label 'lbl_sampleAffectation' défini dans le rptDesign
		v_Page.setText("lbl_sampleAffectation", "Le texte sur une seule ligne\naffecté par le programme Java");
		v_Page.setText("txt_sampleAffectation", "Le texte sur \nplusieurs lignes affecté\npar le programme Java");
		// Créer un document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_Page);

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportAffectationVariable);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec affectation de la valeur d'une variable à la volée
	 * avec un élément invalide (autre qu'un 'Label'). Si erreur.
	 */
	@Test
	public void testAffectationVariable_CE1() {
		assertThrows(IllegalArgumentException.class, () -> {
			// Obtenir une page
			final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportAffectationVariable);
			// Elément non 'Label
			final String v_elementNameNonLabel = "lst_sample";
			try {
				// Affecter le Label 'lbl_sampleAffectation' défini dans le rptDesign
				v_Page.setText(v_elementNameNonLabel, "Un texte qui ne sera pas écrit");
			} catch (final IllegalArgumentException v_e) {
				assertTrue(

						v_e.getMessage().startsWith("Problème pour affecter la valeur dans l'élément \""
								+ v_elementNameNonLabel + "\" (de type "),
						"Le message d'erreur est incorrect");
				throw v_e;
			}
		});
	}

	/**
	 * Génération d'édition avec des pages orientées différemments.
	 * 
	 * @throws IOException Si erreur.
	 */
	@Test
	public void testOrientationPage() throws IOException {
		final Document_Itf v_DocumentPdf = ReportFactory.createDocument();
		final Page_Itf v_page1 = ReportFactory.createPageBirt(c_design_TutoReportOrientationPage,
				PageOrientation_Enum.PageOrientationPortrait);
		v_DocumentPdf.addPage(v_page1);
		final Page_Itf v_page2 = ReportFactory.createPageBirt(c_design_TutoReportOrientationPage,
				PageOrientation_Enum.PageOrientationLandScape);
		v_DocumentPdf.addPage(v_page2);
		final Page_Itf v_page3 = ReportFactory.createPageBirt(c_design_TutoReportOrientationPage,
				PageOrientation_Enum.PageOrientationPortrait);
		// FIXME : Problème le MasterPage ne semble pas pris en compte par : PageBIRT -
		// v_MasterPageHandle.setOrientation(_PageOrientation_Enum.get_nomOrientationBirt())
		// final Page_Itf v_page3 =
		// ReportFactory.createPageBirt(c_design_TutoReportOrientationPage,
		// "MasterPage2",
		// PageOrientation_Enum.PageOrientationPortrait);
		v_DocumentPdf.addPage(v_page3);

		final ByteArrayOutputStream v_ByteArrayOutputStream = new ByteArrayOutputStream(1024);
		// Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
		v_DocumentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);

		// Les octets représentant le PDF
		final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
		assertNotNull(v_tab_result, "Le PDF ne devrait pas être vide");

		// Définir le fichier pour écrire les octets du PDF
		final File v_fichierPdf = new File(c_target_directory + c_rapport_TutoReportOrientationPage);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			// Ecrire les données du PDF
			v_pdfOutputStream.write(v_tab_result);
		} finally {
			// Fermer le fichier
			v_pdfOutputStream.close();
		}
	}

	/**
	 * Génération d'édition avec un lien interne.
	 * 
	 * @throws IOException Si erreur.
	 */
	// TODO à corriger et à réactiver
	// FIXME : java.lang.IllegalArgumentException: Pas d'ActionHandle trouvé sur
	// l'élément "label_lienInterne" - réalisation du bookmark "bookmark1"
	// impossible
	// @Test
	// public void testInternalLink () throws IOException
	// {
	// // Obtenir une page No1
	// final PageDesign_Itf v_Page1 =
	// ReportFactory.createPageBirt(c_design_TutoReportInternalLink);
	// // Lire une image
	// final byte[] v_fluxImageToAdd = ReportUtils.transformPictureToByte(c_image);
	// // Affecter le flux de l'image dynamiquement (via le nom dans le rptDesign
	// "image_vide.png") au report
	// v_Page1.makePictureElementEmbedded("image_vide.png", v_fluxImageToAdd,
	// DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
	// // Créer un lien interne
	// v_Page1.createInternalLink("label_lienInterne", "bookmark1");
	// // Créer un document PDF
	// final Document_Itf v_documentPdf = ReportFactory.createDocument();
	// // Ajout de la page au document PDF
	// v_documentPdf.addPage(v_Page1);
	//
	// // Obtenir une page No2
	// final PageDesign_Itf v_Page2 =
	// ReportFactory.createPageBirt(c_design_TutoReportInternalLink);
	// // Affecter le flux de l'image dynamiquement (via le nom dans le rptDesign
	// "image_vide.png") au report
	// v_Page2.makePictureElementEmbedded("image_vide.png", v_fluxImageToAdd,
	// DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
	// // Ajout de la page au document PDF
	// v_documentPdf.addPage(v_Page2);
	//
	// final ByteArrayOutputStream v_ByteArrayOutputStream = new
	// ByteArrayOutputStream(1024);
	// // Ecrire le document PDF dans le flux 'v_ByteArrayOutputStream'
	// v_documentPdf.writeDocumentWithPageNumbers(v_ByteArrayOutputStream);
	//
	// // Les octets représentant le PDF
	// final byte[] v_tab_result = v_ByteArrayOutputStream.toByteArray();
	// assertNotNull("Le PDF ne devrait pas être vide", v_tab_result);
	//
	// // Définir le fichier pour écrire les octets du PDF
	// final File v_fichierPdf = new File(c_target_directory +
	// c_rapport_TutoReportInternalLink);
	// v_fichierPdf.getParentFile().mkdirs();
	// final BufferedOutputStream v_pdfOutputStream = new BufferedOutputStream(new
	// FileOutputStream(v_fichierPdf));
	// try
	// {
	// // Ecrire les données du PDF
	// v_pdfOutputStream.write(v_tab_result);
	// }
	// finally
	// {
	// // Fermer le fichier
	// v_pdfOutputStream.close();
	// }
	// }

	/**
	 * Test de report basic.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void testRapportTuto() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_design_TutoReportPremiereGeneration);

		writeFile(v_Page, c_target_directory + c_rapport_tuto2);
	}

	/**
	 * Génère un fichier pdf à partir d'une page BIRT.
	 * 
	 * @param p_Page     Page
	 * @param p_fileName Nom et chemin du fichier pdf
	 * @throws IOException e
	 */
	private void writeFile(final PageDesign_Itf p_Page, final String p_fileName) throws IOException {
		// Sauvegarde du rptdesign tel que modifié par les méthodes sur PageDesign_Itf :
		// utile en test pour comparer le résultat de ces méthodes avec ce qui serait
		// fait avec le designer BIRT
		// (Cette sauvegarde ne devrait pas avoir d'incidence sur la génération pdf dans
		// la suite de cette méthode)
		final String v_rptDesignFileName = p_fileName.replace(".pdf", ".rptdesign");
		final BufferedOutputStream v_rptDesignOutputStream = new BufferedOutputStream(
				new FileOutputStream(v_rptDesignFileName));
		try {
			p_Page.save(v_rptDesignOutputStream);
		} finally {
			v_rptDesignOutputStream.close();
		}

		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(p_Page);

		// Edition et sauvegarde du document PDF
		final File v_fichierPdf = new File(p_fileName);
		v_fichierPdf.getParentFile().mkdirs();
		final BufferedOutputStream v_outputStream = new BufferedOutputStream(new FileOutputStream(v_fichierPdf));
		try {
			v_documentPdf.writeDocumentWithPageNumbers(v_outputStream);
		} finally {
			v_outputStream.close();
		}
	}
}
