/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import fr.spi4j.report.Document_Itf;
import fr.spi4j.report.Page_Itf;
import fr.spi4j.report.Permission_Enum;
import fr.spi4j.report.ReportException;

/**
 * Création/gestion d'un document.
 * 
 * @author MINARM
 */
public final class DocumentPdf implements Document_Itf {
	private final List<Page_Itf> _pages = new ArrayList<>();

	private final Set<Permission_Enum> _permissions = new HashSet<>();

	// login & mot de passe
	// si les valeurs sont nulles une valeur aléatoire sera écrite
	// les restrictions sur le pdf sur seront débloquées par la saisie de ces
	// informations
	private String _ownerPassword;

	private String _password;

	/**
	 * Constructeur.
	 */
	public DocumentPdf() {
		super();
	}

	@Override
	public List<Page_Itf> getPages() {
		return Collections.unmodifiableList(_pages);
	}

	@Override
	public void addPage(final Page_Itf p_pageToAdd) {
		_pages.add(p_pageToAdd);
	}

	@Override
	public void removePage(final int p_pageNumber) {
		_pages.remove(p_pageNumber);
	}

	@Override
	public Page_Itf get_page(final int p_pageNumber) {
		return _pages.get(p_pageNumber);
	}

	@Override
	public int get_size() {
		return _pages.size();
	}

	@Override
	public boolean isPermissionEnabled(final Permission_Enum p_permission) {
		return _permissions.contains(p_permission);
	}

	@Override
	public void setPermissionEnabled(final Permission_Enum p_permission, final boolean p_enabled) {
		if (p_enabled) {
			_permissions.add(p_permission);
		} else {
			_permissions.remove(p_permission);
		}
	}

	@Override
	public void setUserPassword(final String p_password) {
		_password = p_password;
	}

	@Override
	public String getUserPassword() {
		return _password;
	}

	@Override
	public void setOwnerPassword(final String p_ownerPassword) {
		_ownerPassword = p_ownerPassword;
	}

	@Override
	public String getOwnerPassword() {
		return _ownerPassword;
	}

	@Override
	public void writeDocumentWithPageNumbers(final OutputStream p_outputStream) {
		writeDocument(p_outputStream, true);
	}

	@Override
	public void writeDocumentWithoutPageNumbers(final OutputStream p_outputStream) {
		writeDocument(p_outputStream, false);
	}

	/**
	 * Ecrit le document.
	 * 
	 * @param p_outputStream   OutputStream
	 * @param p_addPageNumbers boolean
	 */
	private void writeDocument(final OutputStream p_outputStream, final boolean p_addPageNumbers) {
		final List<Page_Itf> v_pages = getPages();
		if (v_pages.isEmpty()) {
			throw new IllegalStateException("Aucune page à écrire dans le document");
		}
		int v_cptPages = 0;
		try {
			// Génère les pages
			for (final Page_Itf v_page : v_pages) {
				v_cptPages++;
				v_page.doReport();
			}

			if (p_addPageNumbers) {
				// Numérote les pages
				addPageNumbers();
			}

			final boolean v_hasPermission = !_permissions.isEmpty() || getUserPassword() != null
					|| getOwnerPassword() != null;

			if (!v_hasPermission) {
				// Ecrit le contenu des pages
				writePages(p_outputStream);
			} else {
				// Ecrit le contenu des pages et ajoute les permissions
				final ByteArrayOutputStream v_byteArrayOutputStream = new ByteArrayOutputStream();
				writePages(v_byteArrayOutputStream);
				final byte[] v_byte = v_byteArrayOutputStream.toByteArray();

				// PDFStamper permet d'ajouter des éléments en plus aux pages du PDF courant.
				// LE PDF courant ne perd pas ses éléments originaux.
				// Enclenche le processus d'ajout d'éléments externes aux pages du PDF courant.
				final PdfStamper v_pdfStamper = new PdfStamper(new PdfReader(v_byte), p_outputStream);
				try {
					addPermissionsAndPasswords(v_pdfStamper);
				} finally {
					v_pdfStamper.close();
				}
			}
		} catch (final DocumentException v_e) {
			throw new ReportException("Problème lors de l'écriture du document sur la page No" + v_cptPages, v_e);
		} catch (final IOException v_e) {
			throw new ReportException("Problème d'I/O lors de l'écriture du document sur la page No" + v_cptPages, v_e);
		}
	}

	/**
	 * Ajoute les numéros de pages I / N en bas de chaque page.
	 */
	private void addPageNumbers() {
		int v_nbTotalPage = 0;

		final List<Page_Itf> v_pages = getPages();
		for (final Page_Itf v_page : v_pages) {
			v_nbTotalPage += v_page.getNumberOfPages();
		}

		int v_startPage = 1;
		// Ajouter les numéros de page
		for (final Page_Itf v_Page : v_pages) {
			v_startPage = v_Page.addPageNumbers(v_startPage, v_nbTotalPage);
		}
	}

	/**
	 * Ecrit le contenu des pages dans le flux.
	 * 
	 * @param p_outputStream OutputStream
	 * @throws DocumentException e
	 * @throws IOException       e
	 */
	private void writePages(final OutputStream p_outputStream) throws DocumentException, IOException {
		final List<Page_Itf> v_pages = getPages();
		if (v_pages.size() > 1) {
			final PdfCopyFields v_pdfCopyFields = new PdfCopyFields(p_outputStream);
			// écrit en concaténant les pages
			for (final Page_Itf v_Page : v_pages) {
				final PdfReader v_reader = new PdfReader(v_Page.get_tab_byte());
				try {
					v_pdfCopyFields.addDocument(v_reader);
				} finally {
					v_reader.close();
				}
			}
			v_pdfCopyFields.close();
		} else {
			// une seule page, alors pas besoin de concaténer
			final Page_Itf v_page = get_page(0);
			p_outputStream.write(v_page.get_tab_byte());
		}
	}

	/**
	 * Ajoute les permissions choisies au PDF. Si on ne positionne rien, les valeurs
	 * par défaut sont :
	 * <p>
	 * <ul>
	 * <li>Pas de mot de passe utilisateur</li>
	 * <li>Pas de mot de passe 'administrateur'</li>
	 * <li>Impression : autorisée</li>
	 * <li>Assemblage du document : Non autorisé</li>
	 * <li>Copie du contenu : Autorisée</li>
	 * <li>Copie du contenu pour accessibilité: Autorisée</li>
	 * <li>Extraction de pages : Autorisée</li>
	 * <li>Commentaires : Autorisés</li>
	 * <li>Remplissage de champ de formulaires : Autorisé</li>
	 * <li>Apposition de signature : non autorisée</li>
	 * <li>Création de modèle de page : non autorisée</li>
	 * </ul>
	 * Dès que l'on positionne une valeur, le mot de passe administrateur est
	 * automatiquement positionné.
	 * <p>
	 * S'il est 'null', une valeur aléatoire sera créée. Si seul le mot de passe
	 * administrateur est positionné, ou si les deux mots de passes sont
	 * positionnés, les autorisations sont:
	 * <p>
	 * <ul>
	 * <li>Pas de mot de passe utilisateur</li>
	 * <li>Mot de passe 'administrateur'</li>
	 * <li>Impression : non autorisée</li>
	 * <li>Assemblage du document : Non autorisé</li>
	 * <li>Copie du contenu : non autorisée</li>
	 * <li>Copie du contenu pour accessibilité: non Autorisée</li>
	 * <li>Extraction de pages : non Autorisée</li>
	 * <li>Commentaires : non Autorisés</li>
	 * <li>Remplissage de champ de formulaires : non Autorisé</li>
	 * <li>Apposition de signature : non autorisée</li>
	 * <li>Création de modèle de page : non autorisée</li>
	 * </ul>
	 * 
	 * @param p_pdfStamper Le PDF.
	 */
	@SuppressWarnings("deprecation")
	private void addPermissionsAndPasswords(final PdfStamper p_pdfStamper) {
		// Initialisation des permissions
		int v_permissionAll = 0;
		// tant qu'il y a des permission dans la map
		for (final Permission_Enum v_permission : _permissions) {
			// Ajout des permissions a la variable
			v_permissionAll += v_permission.getPdfPermission();
		}

		// Recuperation du PdfContentByte d'une page du stamper
		final PdfContentByte v_over = p_pdfStamper.getOverContent(1);
		// Recuperation du writer
		final PdfWriter v_writer = v_over.getPdfWriter();

		// Setter de permission au writer
		// ATTENTION IL FAUT AJOUTER LE PACKAGE : bouncycastle.jar
		try {
			v_writer.setEncryption(false, getUserPassword(), getOwnerPassword(), v_permissionAll);
		} catch (final DocumentException v_e) {
			throw new ReportException("Problème pour ajouter permission et MdP avec p_pdfStamper=" + p_pdfStamper, v_e);
		}
	}
}
