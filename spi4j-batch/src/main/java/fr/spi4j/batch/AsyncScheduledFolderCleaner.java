/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.batch;

import java.io.File;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.batch.exception.Spi4jTaskExecutionException;

/**
 * Classe de nettoyage du dossier temporaire d'execution des processus
 * asynchrones.
 * 
 * @author MINARM
 * @deprecated
 */
@Deprecated
public class AsyncScheduledFolderCleaner implements Runnable {

	/** ILog for cache. */
	protected static Logger _log = LogManager.getLogger(AsyncScheduledFolderCleaner.class);

	protected final ScheduledThreadPoolExecutor _executor;

	/** Le dossier temporaire d'exécution des Threads (optionnel). */
	private String _folder;

	/** L'interval en minutes de nettoyage du dossier temporaire. */
	private int _cleanInterval;

	/** Délai d'expiration des fichiers. */
	private int _expirationDelay;

	protected AsyncScheduledFolderCleaner(ScheduledThreadPoolExecutor p_executor, String p_folder,
			int p_expirationDelay, int p_cleanInterval) {
//		ValidatorData.checkNotNullBloquant("executor", executor, "L'executor doit être renseigné.");
//		ValidatorData.checkNotEmptyBloquant("folder", folder,
//				"Impossible d'initialiser un nettoyeur planifié si le dossier temporaire n'est pas renseigné.");
//		ValidatorData.checkGreaterOrEqualsBloquant("cleanInterval", 1, cleanInterval,
//				"L'interval d'exécution du nettoyeur planifié ne doit pas être inférieur à 1 minute.");
//		ValidatorData.checkGreaterOrEqualsBloquant("expirationDelay", 1, expirationDelay,
//				"Le délai d'expiration des fichiers ne doit pas être inférieur à 1 minute.");

		_executor = p_executor;
		_folder = p_folder;
		_expirationDelay = p_expirationDelay;
		_cleanInterval = p_cleanInterval;
	}

	public void schedule() {
//		ValidatorData.checkNotNullBloquant("executor", _executor, "L'executor doit être renseigné.");

		final long v_milliUntilNext = 60 * 1000 * _cleanInterval;

		// TODO : gérer le changement d'heure en MArs et octobre. Pas très grave, mais à
		// prévoir tout de même.
		_log.info(
				"Prochain nettoyage planifié du dossier '" + _folder + "' prévu dans " + _cleanInterval + " minutes.");

		_executor.schedule(this, v_milliUntilNext, TimeUnit.MILLISECONDS);
	}

	@Override
	public final synchronized void run() {
		AsyncScheduledFolderCleaner.cleanFolder(_folder, _expirationDelay);

		schedule();
	}

	public static final synchronized void cleanFolder(final String p_folder, final int p_expirationDelay) {
//		ValidatorData.checkNotEmptyBloquant("folder", folder, "Le dossier à nettoyer doit être renseigné.");
//		ValidatorData.checkGreaterOrEqualsBloquant("expirationDelay", 0, expirationDelay,
//				"Le délai d'expiration doit être positif.");

		_log.info("Démarrage du nettoyage de dossier temporaire... {{{");
		try {
			// 1. Vérifier que le dossier existe => erreur (reschedule ?).
			File v_tempFolder = new File(p_folder);
			try {
				if (!v_tempFolder.exists())
					throw new Spi4jTaskExecutionException("L'emplacement '" + p_folder + "' n'existe pas",
							"Est-ce que l'existance du dossier a été testée au démarrage du serveur ?");

				if (!v_tempFolder.isDirectory())
					throw new Spi4jTaskExecutionException("L'emplacement '" + p_folder + "' n'est pas un dossier",
							null);

				// 2. Vérifier si le dossier est vide => fin.
				Set<File> v_files = new HashSet<>();
				for (File v_file : v_tempFolder.listFiles())
					if (v_file.isFile())
						v_files.add(v_file);
				if (!v_files.isEmpty()) {

					long v_now = Calendar.getInstance().getTimeInMillis();
					// 3. Clean les fichiers expirés dans files.
					for (File v_file : v_files)
						if ((v_now - v_file.lastModified()) > 60 * 1000 * p_expirationDelay)
							AsyncScheduledFolderCleaner.deleteFile(v_file);
				} else {
					_log.info("Le dossier '" + p_folder + "' est vide.");
				}
			} catch (SecurityException p_e) {
				throw new Spi4jTaskExecutionException(p_e, "Impossible d'accéder au répertoire '" + p_folder + "'",
						"Le répertoire " + p_folder + " est-il bien accessible en écriture ?");
			}

		} catch (Throwable t) {
			// le catch de TOUTES les erreurs empêche le blocage éternel du scheduler.
			_log.error("Erreur lors du nettoyage de dossier temporaire", t);
		} finally {
			_log.info("Fin du nettoyage de dossier temporaire.");
			_log.info("}}}");
		}
	}

	private static void deleteFile(File file) {
//		ValidatorData.checkNotNullBloquant("file", file, "Le fichier à supprimer doit être renseigné.");
		String filePath = file.getPath();

		try {
			_log.debug("Suppression du fichier '" + filePath + "'");
			file.delete();
		} catch (SecurityException p_e) {
			throw new Spi4jTaskExecutionException(p_e, "Impossible d'accéder au fichier '" + filePath + "'",
					"Le fichier " + filePath + " est-il bien accessible en écriture ?");
		}
	}

}
