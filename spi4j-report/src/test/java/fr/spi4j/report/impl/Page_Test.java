/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.junit.jupiter.api.Test;

import bean.SampleBeanDataSet4Table;
import bean.SampleBeanDataSet4TableGroup;
import fr.spi4j.report.BirtEngine_Itf;
import fr.spi4j.report.Document_Itf;
import fr.spi4j.report.PageDesign_Itf;
import fr.spi4j.report.Page_Itf;
import fr.spi4j.report.Permission_Enum;
import fr.spi4j.report.ReportFactory;
import fr.spi4j.report.util.BirtUtils;

/**
 * Test de la classe Page. Attention, il faut des rapports au format
 * birt(rptdesign)!
 * 
 * @author MINARM
 */
public class Page_Test {
	private static final String c_target_directory = System.getProperty("user.dir") + "/target/";

	private static final String c_rptDesign_personne = "/fr/spi4j/report/Rapport_Personne.rptdesign";

	private static final String c_image = "/fr/spi4j/report/safran.png";

	private static final String c_rptDesign_personne_list = "/fr/spi4j/report/Rapport_Personne_List.rptdesign";

	private static final String c_rptDesign_personne_list_group = "/fr/spi4j/report/Rapport_Personne_List_Group.rptdesign";

	private static final String c_rptDesign_personne_chart = "/fr/spi4j/report/Rapport_Personne_Chart.rptdesign";

	/**
	 * Main.
	 * 
	 * @param p_args Paramètres du main.
	 * @throws IOException e
	 */
	public static void main(final String[] p_args) throws IOException {
		final BirtEngine_Itf v_outilsBIRT = BirtUtils.getInstance();
		final Page_Test v_test = new Page_Test();

		// System.out.println("test simple");
		v_test.test_rapportPersonne();
		// System.out.println("test numerotationPage");
		v_test.test_AllPagesBirt();

		v_outilsBIRT.stopPlatform();
	}

	/**
	 * Test création d'un PDF avec itext.
	 */
	@Test
	public void test_PageIText() {
		// page de type itext
		final Page_Itf v_Page = ReportFactory.createPageItext();
		assertNotNull(v_Page, "l'instance ne doit pas etre nulle");
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
		final BufferedOutputStream v_outputStream = new BufferedOutputStream(new FileOutputStream(p_fileName));
		try {
			v_documentPdf.writeDocumentWithPageNumbers(v_outputStream);
		} finally {
			v_outputStream.close();
		}
	}

	/**
	 * Test de génération d'un rapport basé sur 'c_rptDesign_personne'.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void test_rapportPersonne() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_rptDesign_personne);
		// Remplissage des pages par les beans
		v_Page.makeReportWithContainer("DataSourceTestRapportPersonne", "DataSetTestRapportPersonne",
				SampleBeanDataSet4Table.class, "table_personne");
		v_Page.makePictureElementEmbedded("SID.bmp", c_image, DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
		writeFile(v_Page, c_target_directory + "test_rapportPersonne.pdf");
	}

	/**
	 * @throws IOException e
	 */
	@Test
	public void test_rapportPersonneList() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_rptDesign_personne_list);
		// Remplissage des pages par les beans
		v_Page.makeReportWithContainer("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"list_personne");

		writeFile(v_Page, c_target_directory + "test_rapportPersonneList.pdf");
	}

	/**
	 * @throws IOException e
	 */
	@Test
	public void test_rapportPersonneListGroup() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_rptDesign_personne_list_group);
		// Remplissage des pages par les beans
		v_Page.makeReportWithListAndGroup("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4TableGroup.class,
				"list_personne", "_nomAndPrenom");

		writeFile(v_Page, c_target_directory + "test_rapportPersonneListGroup.pdf");
	}

	/**
	 * @throws IOException e
	 */
	@Test
	public void test_rapportPersonneListGroupBis() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_rptDesign_personne_list_group);
		// Remplissage des pages par les beans
		v_Page.createDataSet("DataSetPersonne", SampleBeanDataSet4TableGroup.class, "DataSourcePersonne");
		v_Page.makeListElementWithGroup("list_personne", "DataSetPersonne", "_nom");
		v_Page.hideVisibility("lab_id_personne");
		v_Page.createURLLink("text_lien_url", "http://www.sid.defense.gouv.fr/");

		// v_Page.createInternalLink("_nom", "LAMOTTE");

		writeFile(v_Page, c_target_directory + "test_rapportPersonneListGroupBis.pdf");
	}

	/**
	 * @throws IOException e
	 */
	@Test
	public void test_rapportPersonneChart() throws IOException {
		final PageDesign_Itf v_Page = ReportFactory.createPageBirt(c_rptDesign_personne_chart);
		// Remplissage des pages par les beans
		v_Page.makeReportWithChartWithAxes("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"chartAvecAxes_personne", "_nom", "_age");
		v_Page.makeReportWithChartWithoutAxes("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"chartSansAxes_personne", "_nom", "_age");

		writeFile(v_Page, c_target_directory + "test_rapportPersonneChart.pdf");
	}

	/**
	 * Test création d'un PDF avec toutes les pages birt et une permission.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void test_AllPagesBirt() throws IOException {
		// Creation des pages
		final PageDesign_Itf v_page1 = ReportFactory.createPageBirt(c_rptDesign_personne);
		final PageDesign_Itf v_page2 = ReportFactory.createPageBirt(c_rptDesign_personne_list);
		final PageDesign_Itf v_page3 = ReportFactory.createPageBirt(c_rptDesign_personne_list_group);
		final PageDesign_Itf v_page4 = ReportFactory.createPageBirt(c_rptDesign_personne_chart);
		final PageDesign_Itf v_page5 = ReportFactory.createPageBirt(c_rptDesign_personne_list_group);

		// Remplissage des pages par les beans
		v_page1.makeReportWithContainer("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"table_personne");
		v_page2.makeReportWithContainer("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"list_personne");
		v_page3.makeReportWithListAndGroup("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4TableGroup.class,
				"list_personne", "_nom");
		v_page4.makeReportWithChartWithAxes("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"chartAvecAxes_personne", "_nom", "_age");
		v_page4.makeReportWithChartWithoutAxes("DataSourcePersonne", "DataSetPersonne", SampleBeanDataSet4Table.class,
				"chartSansAxes_personne", "_nom", "_age");

		v_page5.createDataSet("DataSetPersonne", SampleBeanDataSet4TableGroup.class, "DataSourcePersonne");
		v_page5.makeListElementWithGroup("list_personne", "DataSetPersonne", "_nom");
		v_page5.hideVisibility("lab_id_personne");
		v_page5.createURLLink("text_lien_url", "http://www.sid.defense.gouv.fr/");

		// v_page5.createInternalLink("_nom", "LAMOTTE");

		// Creation d'un nouveau document PDF
		final Document_Itf v_documentPdf = ReportFactory.createDocument();
		// Ajout des pages au document PDF
		v_documentPdf.addPage(v_page1);
		v_documentPdf.addPage(v_page2);
		v_documentPdf.addPage(v_page3);
		v_documentPdf.addPage(v_page4);
		v_documentPdf.addPage(v_page5);
		v_documentPdf.setPermissionEnabled(Permission_Enum.ALLOW_COPY, false);

		// Edition et sauvegarde du document PDF
		final BufferedOutputStream v_outputStream = new BufferedOutputStream(
				new FileOutputStream(c_target_directory + "test_AllPagesBirt.pdf"));
		try {
			v_documentPdf.writeDocumentWithPageNumbers(v_outputStream);
		} finally {
			v_outputStream.close();
		}
	}
}
