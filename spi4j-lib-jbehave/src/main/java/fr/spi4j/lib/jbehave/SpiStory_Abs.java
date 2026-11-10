/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbehave.core.embedder.Embedder.EmbedderFailureStrategy;
import org.jbehave.core.embedder.Embedder.RunningStoriesFailed;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PendingStepsFound;
import org.jbehave.core.io.StoryResourceNotFound;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.ReportsCount;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.requirement.Requirement_Itf;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.ViewsAssociation;
import fr.spi4j.ui.mvp.rda.MVPUtilsMultiThread;

/**
 * Classe de tests initialisant les storys JBehave.
 * 
 * @author MINARM
 */
//@RunWith(AnnotatedEmbedderRunner.class)
public abstract class SpiStory_Abs extends JUnitStory {

	static {
		// possibilité de gérer plusieurs threads sur les stories pour tests de charge
		MVPUtils.setInstance(new MVPUtilsMultiThread());
	}

	private static final Logger c_log = LogManager.getLogger(SpiStory_Abs.class);

	private Throwable _error;

	private int _threadNum = -1;

	private int _numIteration; // = 0 par défaut

	private final Map<String, Object> _data = new HashMap<>();

	/**
	 * File d'élements (FIFO)
	 */
	private final Deque<NextTypeAndIdentifiant> _file = new ArrayDeque<>();


	/**
	 * Constructeur avec ajustement de la configuration.
	 */
	public SpiStory_Abs() {
		super();
		// configuration custom de depart.
		useConfiguration(new SpiStoryConfiguration(getClass()));

		// injection de la gestion des erreurs au niveau de la classe 'Embedder'.
		configuredEmbedder().useEmbedderFailureStrategy(new EmbedderFailureStrategy() {

			public void handleFailures(ReportsCount p_count) {
//				if (null != p_count && !p_count.isEmpty())
//					manageError(p_count.elements().nextElement());
			}

			// on s'arrete à la première erreur.
			public void handleFailures(BatchFailures p_failures) {
				if (null != p_failures && !p_failures.isEmpty())
					manageError(p_failures.elements().nextElement());
			}
		});

		// strategie pour la gestion des steps non trouves.
		configuredEmbedder().configuration().usePendingStepStrategy(new FailingUponPendingStep());

		// stratégie pour l'écriture des rapports + gestion des échecs, etc...
		configuredEmbedder().embedderControls().doGenerateViewAfterStories(shouldGenerateViewAfterStories())
				.doIgnoreFailureInStories(false).doIgnoreFailureInView(false).doVerboseFailures(true)
				.useThreads(getThreadsToLaunch());
	}

	/**
	 * Méthode d'initialisation des tests JBehave. Cette méthode peut être redéfinie
	 * pour rajouter une transaction autour de la story par exemple.
	 */
	public void setUp() {
		SpiStoryContextHandler.start(this);
		if (isLoadTesting()) {
			MVPUtils.setInstance(new MVPUtilsMultiThread());
		} else {
			MVPUtils.reinit();
		}
		MVPUtils.getInstance().getViewManager().setViewsAssociation(getViewsAssociation());
	}

	/**
	 * Méthode de finalisation des tests JBehave. Cette méthode peut être redéfinie
	 * pour rajouter une transaction autour de la story par exemple.
	 */
	public void tearDown() {
		SpiStoryContextHandler.destroy();
		Collection<Presenter_Abs<? extends View_Itf, ?>> v_presenters = MVPUtils.getInstance().getViewManager()
				.getActivePresenters();
		while (!v_presenters.isEmpty()) {
			v_presenters.iterator().next().close();
			v_presenters = MVPUtils.getInstance().getViewManager().getActivePresenters();
		}
		_data.clear();
		// on passe à l'itération suivante
		_numIteration++;
	}

	/**
	 * Injection des steps prédéfinis.
	 */
	@Override
	public InjectableStepsFactory stepsFactory() {
		List<Object> v_stepsInstances = getStepsClassesInstances();
		if (v_stepsInstances == null) {
			v_stepsInstances = new ArrayList<>();
		}
		return new InstanceStepsFactory(configuration(), v_stepsInstances);
	}

	/**
	 * La méthode principale pour le lancement des tests.
	 */
	@Override
	public void run() {
		try {

			// On évite les annotations et on lance les trois méthodes à la suite sans
			// utiliser @BeforeEach et @AfterEach.
			setUp();
			super.run();
			tearDown();

		} catch (final Throwable v_t) {
			final Throwable v_cause = v_t.getCause();
			if (v_cause != null) {
				// dans jbehave, c'est en général l'exception dans la cause qui contient
				// l'information intéressante pour le développeur
				c_log.error(v_cause.toString(), v_cause);
				// gestion de l'erreur si ce n'est pas déjà un assert qui a échoué
				if (v_cause instanceof AssertionError) {
					throw new SpiStoryException(v_cause.getMessage());
				} else {
					errorAction(v_cause);
				}
			} else {
				// erreur de génération des rapports
				if (v_t instanceof RunningStoriesFailed
						&& v_t.getMessage().startsWith("Failures in running stories: ReportsCount")) {
					c_log.warn("Des stories ont échoué, problème de génération des rapports : " + v_t.toString());
				} else {
					// gestion de l'erreur
					if (_error != null) {
						// gestion de l'erreur si ce n'est pas déjà un assert qui a échoué
						if (_error instanceof AssertionError) {
							throw new SpiStoryException(_error.getMessage());
						} else {
							errorAction(_error);
						}
					}
					// gestion de l'erreur
					errorAction(v_t);
				}
			}
		}
	}

	/**
	 * Gestion d'une erreur suite à step qui a échoué ou qui n'a pas été trouvé
	 * 
	 * @param p_throw l'erreur source
	 */
	private void manageError(final Throwable p_throw) {
		final Throwable v_cause = p_throw.getCause();
		if (v_cause != null) {
			_error = v_cause;
			c_log.error(v_cause.getMessage(), v_cause);
			fail(v_cause.getMessage());
		} else {
			_error = p_throw;
			c_log.error(p_throw.getMessage(), p_throw);
			fail(p_throw.getMessage());
		}
	}

	/**
	 * Gestion d'une erreur.
	 * 
	 * @param p_throwable l'erreur interceptée
	 */
	protected void errorAction(final Throwable p_throwable) {
		// si la cause est une PendingStepFound : indiquer au testeur que le step n'a
		// pas été trouvé
		if (p_throwable instanceof PendingStepsFound) {
			throw new SpiStoryException("Step non trouvé : " + p_throwable.getMessage());
		} else if (p_throwable instanceof StoryResourceNotFound) {
			throw new SpiStoryException(
					"Impossible de trouver la Story JBehave. Vérifier le nom du fichier Java : XXX_Story.java et de la story : XXX.story");
		} else {
			c_log.error(p_throwable.toString(), p_throwable);
			// on limite à 512 caractères pour ne pas surcharger la failure JUnit
			if (null == p_throwable.getMessage() || p_throwable.getMessage().isEmpty()) {
				throw new SpiStoryException(p_throwable.toString() + " : "
						+ Arrays.toString(p_throwable.getStackTrace()).substring(0, 512) + " ...");
			} else {
				throw new SpiStoryException(p_throwable.getMessage());
			}
		}
	}

	/**
	 * Cette méthode permet de définir quelles sont les classes à charger pour
	 * renseigner le référentiel de steps disponibles. Elle retourne par défaut les
	 * classes de steps de Spi4J et peut être surchargée.
	 * 
	 * Il est conseillé de l'appeler dans la méthode la surchargeant avec
	 * super.getStepsClassesInstances.
	 * 
	 * @return la liste des classes contenant les steps JBehave
	 */
	public List<Object> getStepsClassesInstances() {

		final List<Object> v_classes = new ArrayList<>();
		v_classes.add(new SpiStepsGiven(this));
		v_classes.add(new SpiStepsWhen(this));
		v_classes.add(new SpiStepsThen(this));
		return v_classes;
	}
	
	/**
	 * Retourne la stratégie d'écriture des rapports.
	 * 
	 * @return 'true' si le dévelopeur désire générer un rapport.
	 */
	abstract public boolean shouldGenerateViewAfterStories();	

	/**
	 * Cette méthode indique quelles sont les vues à utiliser lors du déroulement
	 * des scenarii (vues réelles ou vues mockées ).
	 * 
	 * @return l'association présenteur <-> vue
	 */
	abstract public ViewsAssociation getViewsAssociation();

	/**
	 * Retrouve une exigence selon son nom.
	 * 
	 * @param p_req le nom de l'exigence cherchée
	 * @return l'exigence trouvée
	 */
	abstract public Requirement_Itf findRequirement(String p_req);

	/**
	 * Cette méthode indique si la story doit vérifier l’exécution des exigences.
	 * 
	 * @return true si la story doit vérifier les exigences, false sinon
	 */
	abstract public boolean shouldCheckRequirements();

	/**
	 * Cette méthode retourne le nombre de processus à activer pour la story.
	 * 
	 * @return le nombre de processus demandés pour le test.
	 */
	abstract public int getThreadsToLaunch();

	/**
	 * @return numero de thread
	 */
	public int getThreadNum() {
		return _threadNum;
	}

	/**
	 * @param p_threadNum le numero de thread
	 */
	public void setThreadNum(final int p_threadNum) {
		_threadNum = p_threadNum;
	}

	/**
	 * @return le numéro d'itération de cette story pour ce thread
	 */
	public int getNumIteration() {
		return _numIteration;
	}

	/**
	 * @return true si on est en mode test de charge, false sinon
	 */
	public boolean isLoadTesting() {
		return _threadNum >= 0;
	}

	/**
	 * Stocke des données dans la story.
	 * 
	 * @param p_identifiant l'identifiant de la donnée
	 * @param p_data        la donnée
	 */
	public void putData(final String p_identifiant, final Object p_data) {
		_data.put(p_identifiant, p_data);
		c_log.debug("Stockage de " + p_identifiant + " ==> " + p_data);
	}

	/**
	 * Récupère des données stockées dans la story.
	 * 
	 * @param p_identifiant l'identifiant de la donnée
	 * @return la donnée
	 */
	public Object getData(final String p_identifiant) {
		final Object v_data = _data.get(p_identifiant);
		c_log.debug("Utilisation de la valeur stockée : " + p_identifiant + " ==> " + v_data);
		return v_data;
	}

	/**
	 * Vider la file d'attente d'enregistrement de données.
	 */
	public void clearDataFile() {
		_file.clear();
	}

	/**
	 * Sauvegarde l'objet Dto dans la story
	 * 
	 * @param p_obj l'objet à sauvegarder
	 */
	public void saveDto(final Object p_obj) {
		final NextTypeAndIdentifiant v_next = _file.pollLast();
		if (v_next == null) {
			throw new Spi4jRuntimeException(
					"Le DTO demande à être enregistré mais on ne sait pas dans quel identifiant",
					"Vérifier que les données de la file sont bien valides");
		}
		putData(v_next._identifiant, p_obj);
	}

	/**
	 * Sauvegarde l'objet Entity dans la story
	 * 
	 * @param p_obj l'objet à sauvegarder
	 */
	public void saveEntity(final Object p_obj) {
		final NextTypeAndIdentifiant v_next = _file.pollLast();
		if (v_next == null) {
			throw new Spi4jRuntimeException(
					"L'entity demande à être enregistré mais on ne sait pas dans quel identifiant",
					"Vérifier que les données de la file sont bien valides");
		}
		putData(v_next._identifiant, p_obj);
	}

	/**
	 * Initialise le prochain identifiant et le prochain type pour l'objet à stocker
	 * l'objet dans la story.
	 * 
	 * @param p_nextIdentifiant le prochain identifiant
	 * @param p_nextType        le prochain type
	 */
	public void setNextTypeToSave(final String p_nextIdentifiant, final String p_nextType) {
		_file.push(NextTypeAndIdentifiant.create(p_nextIdentifiant, p_nextType));
	}

	/**
	 * Vérifie si l'objet retourné par cette fonction doit être stocké dans la
	 * story.
	 * 
	 * @param p_proxy  the proxy instance that the method was invoked on
	 * @param p_method the Method instance corresponding to the interface method
	 *                 invoked on the proxy instance. The declaring class of the
	 *                 Method object will be the interface that the method was
	 *                 declared in, which may be a superinterface of the proxy
	 *                 interface that the proxy class inherits the method through.
	 * @param p_args   an array of objects containing the values of the arguments
	 *                 passed in the method invocation on the proxy instance, or
	 *                 null if interface method takes no arguments. Arguments of
	 *                 primitive types are wrapped in instances of the appropriate
	 *                 primitive wrapper class, such as java.lang.Integer or
	 *                 java.lang.Boolean.
	 * @return true si l'objet retourné par le service doit être stocké dans la
	 *         story, false sinon
	 */
	public boolean hasToBeSaved(final Object p_proxy, final Method p_method, final Object[] p_args) {
		if (p_method.getName().startsWith("save")) {
			final Class<?> v_typeMethod = p_args[0].getClass();
			if (!_file.isEmpty()) {
				final String v_typeDemande = _file.peekLast()._type;
				return v_typeMethod.getSimpleName().equalsIgnoreCase(v_typeDemande);
			}
		}
		return false;
	}

	/**
	 * Les type et identifiant d'un objet à sauvegarder dans la story.
	 * 
	 * @author MINARM
	 */
	private static final class NextTypeAndIdentifiant {
		private final String _identifiant;

		private final String _type;

		/**
		 * Constructeur.
		 * 
		 * @param p_identifiant l'identifiant
		 * @param p_type        le type
		 */
		private NextTypeAndIdentifiant(final String p_identifiant, final String p_type) {
			super();
			this._identifiant = p_identifiant;
			this._type = p_type;
		}

		/**
		 * Méthode de construction.
		 * 
		 * @param p_identifiant l'identifiant
		 * @param p_type        le type
		 * @return la nouvelle instance
		 */
		private static NextTypeAndIdentifiant create(final String p_identifiant, final String p_type) {
			return new NextTypeAndIdentifiant(p_identifiant, p_type);
		}
	}
}
