/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import fr.spi4j.report.BirtEngine_Itf;
import fr.spi4j.report.Document_Itf;
import fr.spi4j.report.Page_Itf;
import fr.spi4j.report.Permission_Enum;
import fr.spi4j.report.ReportFactory;
import fr.spi4j.report.util.BirtUtils;

/**
 * Test de la classe DocumentPdf Attention, il faut des rapports au format
 * birt(rptdesign)!
 * 
 * @author MINARM
 */
public class DocumentPdf_Test {
	/**
	 * Répertoire de génération.
	 */
	public static final String c_target_directory = System.getProperty("user.dir") + "/target/";

	/**
	 * 1er rptdesign de test.
	 */
	public static final String c_rptDesign1 = "/fr/spi4j/report/Rapport_Personne.rptdesign";

	private static final String c_rptDesign2 = "/fr/spi4j/report/Rapport_Personne_List.rptdesign";

	/**
	 * Classe main.
	 * 
	 * @param p_args Argument du main.
	 * @throws IOException e
	 */
	public static void main(final String[] p_args) throws IOException {
		final BirtEngine_Itf v_outilsBIRT = BirtUtils.getInstance();
		final DocumentPdf_Test v_test = new DocumentPdf_Test();

		// occupation mémoire avant lancement
		// System.out.print(Runtime.getRuntime().freeMemory());

		// System.out.println("test simple");
		v_test.testSimple();
		// System.out.println("test actions page");
		v_test.testActionsPages();
		// System.out.println("test actions permissions");
		v_test.testActionsPermissions();
		// System.out.println("test ecriture fichier");
		v_test.testWritePagePaysage();

		// occupation memoire apres lancement
		// System.out.print(Runtime.getRuntime().freeMemory());

		v_outilsBIRT.stopPlatform();
	}

	/**
	 * test de l'instance simple
	 */
	@Test
	public void testSimple() {
		final Document_Itf v_DocumentPdf = new DocumentPdf();
		assertNotNull(v_DocumentPdf, "l'instance ne doit pas etre nulle");
		assertNotNull(v_DocumentPdf.getPages(), "l'objet liste des pages ne doit pas etre nulle");
	}

	/**
	 * test sur les méthodes liées aux pages du document
	 */
	@Test
	public void testActionsPages() {
		final Document_Itf v_DocumentPdf = ReportFactory.createDocument();
		assertEquals(0, v_DocumentPdf.get_size(), "le nombre de page doit être 0");
		final Page_Itf v_page1 = ReportFactory.createPageBirt(c_rptDesign1);
		v_DocumentPdf.addPage(v_page1);
		assertEquals(1, v_DocumentPdf.get_size(), "le nombre de page doit être 1");
		assertEquals(v_page1, v_DocumentPdf.get_page(0), "on doit récupérer la page envoyée");
		final Page_Itf v_page2 = ReportFactory.createPageBirt(c_rptDesign2);
		v_DocumentPdf.addPage(v_page2);

		final Document_Itf v_DocumentPdf2 = ReportFactory.createDocument();
		for (final Page_Itf v_page : v_DocumentPdf.getPages()) {
			v_DocumentPdf2.addPage(v_page);
		}
		assertEquals(2, v_DocumentPdf2.get_size(), "le nombre de page doit être 2");

		assertEquals(v_page1, v_DocumentPdf2.get_page(0), "la page 1 doit être page1");
		assertEquals(v_page2, v_DocumentPdf2.get_page(1), "la page 2 doit être page2");
	}

	/**
	 * test sur les méthodes liées aux permissions du document
	 * 
	 * @throws IOException e
	 */
	@Test
	public void testActionsPermissions() throws IOException {
		final Document_Itf v_DocumentPdf = ReportFactory.createDocument();
		final Page_Itf v_page1 = ReportFactory.createPageBirt(c_rptDesign1);
		v_DocumentPdf.addPage(v_page1);
		final Page_Itf v_page2 = ReportFactory.createPageBirt(c_rptDesign2);
		v_DocumentPdf.addPage(v_page2);

		for (final Permission_Enum v_permission : Permission_Enum.values()) {
			v_DocumentPdf.setPermissionEnabled(v_permission, true);
			assertTrue(v_DocumentPdf.isPermissionEnabled(v_permission));
			v_DocumentPdf.setPermissionEnabled(v_permission, false);
			assertFalse(v_DocumentPdf.isPermissionEnabled(v_permission));
		}

		v_DocumentPdf.setPermissionEnabled(Permission_Enum.ALLOW_MODIFY_CONTENTS, true);
		v_DocumentPdf.setOwnerPassword("admin1");
		assertEquals("admin1", v_DocumentPdf.getOwnerPassword(),
				"le mot de passe administrateur du pdf doit être 'admin1'");
		v_DocumentPdf.setUserPassword("mdp");
		assertEquals("mdp", v_DocumentPdf.getUserPassword(), "le mot de passe d'ouverture du pdf doit être 'mdp'");

		final BufferedOutputStream v_outputStream = new BufferedOutputStream(
				new FileOutputStream(c_target_directory + "testActionsPermissions.pdf"));
		try {
			v_DocumentPdf.writeDocumentWithPageNumbers(v_outputStream);
		} finally {
			v_outputStream.close();
		}
	}

	/**
	 * test sur la méthode public void writeDocument(String p_fileName)
	 * 
	 * @throws IOException e
	 */
	@Test
	public void testWritePagePaysage() throws IOException {
		final Document_Itf v_DocumentPdf = ReportFactory.createDocument();
		// Cf. 'c_rptDesign1' dans la vue "OutLine", puis regarder la propriété
		// "Orientation" qui doit être en "Auto"
		final Page_Itf v_page1 = ReportFactory.createPageBirt(c_rptDesign1,
				PageOrientation_Enum.PageOrientationPortrait);
		v_DocumentPdf.addPage(v_page1);
		final Page_Itf v_page2 = ReportFactory.createPageBirt(c_rptDesign1,
				PageOrientation_Enum.PageOrientationLandScape);
		v_DocumentPdf.addPage(v_page2);
		final Page_Itf v_page3 = ReportFactory.createPageBirt(c_rptDesign2,
				PageOrientation_Enum.PageOrientationPortrait);
		v_DocumentPdf.addPage(v_page3);

		final BufferedOutputStream v_outputStream1Avec2 = new BufferedOutputStream(
				new FileOutputStream(c_target_directory + "testWritePagePaysage_avecNumPage.pdf"));
		try {
			v_DocumentPdf.writeDocumentWithPageNumbers(v_outputStream1Avec2);
		} finally {
			v_outputStream1Avec2.close();
		}

		final Document_Itf v_DocumentPdf2 = ReportFactory.createDocument();
		for (final Page_Itf v_page : v_DocumentPdf.getPages()) {
			v_DocumentPdf2.addPage(v_page);
		}

		assertEquals(v_DocumentPdf.get_size(), v_DocumentPdf2.get_size(),
				"le nombre de page du document2 doit être " + v_DocumentPdf.get_size());
		v_DocumentPdf2.removePage(v_DocumentPdf2.get_size() - 1);
		assertEquals(v_DocumentPdf.get_size() - 1, v_DocumentPdf2.get_size(),
				"le nombre de page du document2 doit être " + (v_DocumentPdf.get_size() - 1));

		final BufferedOutputStream v_outputStream1Sans2 = new BufferedOutputStream(
				new FileOutputStream(c_target_directory + "testWritePagePaysage_sansNumPage.pdf"));
		try {
			v_DocumentPdf2.writeDocumentWithoutPageNumbers(v_outputStream1Sans2);
		} finally {
			v_outputStream1Sans2.close();
		}
	}
}
