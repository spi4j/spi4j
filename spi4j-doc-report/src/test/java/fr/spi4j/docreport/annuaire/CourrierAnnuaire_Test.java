/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.docreport.annuaire;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.spi4j.docreport.DocReport;
import fr.spi4j.docreport.annuaire.dto.CompetenceDto;
import fr.spi4j.docreport.annuaire.dto.GradeDto;
import fr.spi4j.docreport.annuaire.dto.PersonneDto;

/**
 * Test de SpiDocReport.
 * 
 * @author MINARM
 */
public class CourrierAnnuaire_Test {
	private static final DocReport c_DOC_REPORT_INSTANCE = DocReport.c_INSTANCE;

	/**
	 * Test unitaire de SpiDocReport avec template ODT.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuaireODT() throws IOException {
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final String v_filePathInClassPath = "/templates/courrier_annuaire.odt";
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_Out.odt");
		try {
			c_DOC_REPORT_INSTANCE.process(v_filePathInClassPath, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Test unitaire de SpiDocReport avec template DocX.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuaireDocX() throws IOException {
		// Create context Java model
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final String v_filePathInClassPath = "/templates/courrier_annuaire.docx";
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_Out.docx");
		try {
			c_DOC_REPORT_INSTANCE.process(v_filePathInClassPath, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Test unitaire de SpiDocReport avec template ODT, en chargeant le template au
	 * préalable depuis un InputStream.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuaireODTFromStream() throws IOException {
		// préchargement (une seule fois) du template en l'identifiant avec un id unique
		// et à partir d'un InputStream
		// (peut être utile quand il faut charger le template à partir d'un fichier ou
		// d'une base de données)
		final String v_templateUniqueId = "courrier_annuaire";
		final InputStream v_input = getClass().getResourceAsStream("/templates/courrier_annuaire.docx");
		try {
			c_DOC_REPORT_INSTANCE.loadTemplate(v_input, v_templateUniqueId);
		} finally {
			v_input.close();
		}

		// génération de l'édition, en indiquant le template à utiliser avec ce même id
		// unique
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_from_stream_Out.docx");
		try {
			c_DOC_REPORT_INSTANCE.process(v_templateUniqueId, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Test unitaire de SpiDocReport avec template ODT.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuairePdfFromODT() throws IOException {
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final String v_filePathInClassPath = "/templates/courrier_annuaire.odt";
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_Out_from_ODT.pdf");
		try {
			c_DOC_REPORT_INSTANCE.processToPdf(v_filePathInClassPath, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Test unitaire de SpiDocReport avec template DocX.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuairePdfFromDocX() throws IOException {
		// Create context Java model
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final String v_filePathInClassPath = "/templates/courrier_annuaire.docx";
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_Out_from_DOCX.pdf");
		try {
			c_DOC_REPORT_INSTANCE.processToPdf(v_filePathInClassPath, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Test unitaire de SpiDocReport avec template ODT.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuaireHtmlFromODT() throws IOException {
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final String v_filePathInClassPath = "/templates/courrier_annuaire.odt";
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_Out_from_ODT.html");
		try {
			c_DOC_REPORT_INSTANCE.processToHtml(v_filePathInClassPath, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Test unitaire de SpiDocReport avec template DocX.
	 * 
	 * @throws IOException e
	 */
	@Test
	public void genererCourrierAnnuaireHtmlFromDocX() throws IOException {
		// Create context Java model
		final PersonneDto v_personne = createMockPersonne();

		final Map<String, Object> v_context = new HashMap<>();
		v_context.put("personne", v_personne);

		// Generate report by merging Java model with the ODT
		final String v_filePathInClassPath = "/templates/courrier_annuaire.docx";
		final OutputStream v_output = new FileOutputStream("target/courrier_annuaire_Out_from_DOCX.html");
		try {
			// When testing, ignore : Property 'http://javax.xml.XMLConstants/property/accessExternalSchema' is not recognized.
			c_DOC_REPORT_INSTANCE.processToHtml(v_filePathInClassPath, v_context, v_output);
		} finally {
			v_output.close();
		}
	}

	/**
	 * Création d'un objet bouchon PersonneDto.
	 * 
	 * @return PersonneDto
	 */
	private PersonneDto createMockPersonne() {
		final PersonneDto v_personne = new PersonneDto();
		v_personne.set_nom("DURAND");
		v_personne.set_prenom("Benjamin");
		v_personne.set_grade(new GradeDto(0L, "Lieutenant-Colonel", "LCL"));
		v_personne.set_civil(Boolean.FALSE);
		v_personne.set_salaire(1000000d);
		v_personne.set_dateNaissance(new Date());
		final List<CompetenceDto> v_competences = new ArrayList<>();
		v_competences.add(new CompetenceDto(0L, "Français"));
		v_competences.add(new CompetenceDto(0L, "Anglais"));
		v_competences.add(new CompetenceDto(0L, "Pacman"));
		v_personne.set_tab_competences(v_competences);
		return v_personne;
	}
}
