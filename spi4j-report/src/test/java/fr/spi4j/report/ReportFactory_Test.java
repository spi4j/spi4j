package fr.spi4j.report;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import fr.spi4j.report.impl.DocumentPdf_Test;
import fr.spi4j.report.impl.PageOrientation_Enum;

/**
 * Classe de tests unitaires associée "ReportFactory".
 */
public class ReportFactory_Test {
	private static final String c_target_directory = DocumentPdf_Test.c_target_directory;

	private static final String c_rapport1 = DocumentPdf_Test.c_rptDesign1;

	/**
	 * Test de 'createPageBirt' avec un nom de MasterPage qui n'existe pas.
	 * 
	 * @throws IOException Si une erreur d'I/O se produit.
	 */
	@Test
	public void test_createPageBirt_CE1() throws IOException {

		assertThrows(ReportException.class, () -> {
			final Document_Itf v_DocumentPdf = ReportFactory.createDocument();
			try {
				final Page_Itf v_page1 = ReportFactory.createPageBirt(c_rapport1, "MasterPageInconnu",
						PageOrientation_Enum.PageOrientationPortrait);
				v_DocumentPdf.addPage(v_page1);
				final BufferedOutputStream v_outputStream1Avec2 = new BufferedOutputStream(
						new FileOutputStream(c_target_directory + "test_createPageBirt_CE1.pdf"));
				try {
					v_DocumentPdf.writeDocumentWithPageNumbers(v_outputStream1Avec2);
				} finally {
					v_outputStream1Avec2.close();
				}
			} catch (final ReportException v_e) {
				assertTrue(

						v_e.getMessage().contains(
								"Problème pour trouver la MasterPage \"MasterPageInconnu\" dans le report \"") == true,
						"Le message d'erreur est incorrect");
				throw v_e;
			}
		});

	}
}
