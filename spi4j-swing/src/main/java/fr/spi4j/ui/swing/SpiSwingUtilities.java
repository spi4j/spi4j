/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.FocusManager;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe utilitaire.
 * 
 * @author MINARM
 */
public final class SpiSwingUtilities {

	private static Frame v_splash;

	/**
	 * Constructeur. (private : pas d'instance)
	 */
	private SpiSwingUtilities() {
		super();
	}

	/**
	 * Retourne l'instance courante de la classe componentClass contenant l'élément
	 * component.
	 * <p>
	 * Cette méthode peut-être très utile pour récupérer une référence à un parent
	 * éloigné (ancêtre), en l'absence de référence directe du type attribut. Ex :
	 * un composant panel désire une référence sur sa JFrame parente, alors
	 * l'instruction suivante suffit : getAncestorOfClass(JFrame.class, panel)
	 * 
	 * @return Component
	 * @param <TypeContainer> le type du composant recherché
	 * @param componentClass  Class
	 * @param component       Component
	 */
	@SuppressWarnings("unchecked")
	public static <TypeContainer> TypeContainer getAncestorOfClass(final Class<TypeContainer> componentClass,
			final Component component) {
		return (TypeContainer) SwingUtilities.getAncestorOfClass(componentClass, component);
	}

	/**
	 * Retourne le focusOwner permanent.
	 * <p>
	 * Le focusOwner permanent est défini comme le dernier Component à avoir reçu un
	 * événement FOCUS_GAINED permanent.
	 * <p>
	 * Le focusOwner et le focusOwner permanent sont équivalent sauf si un
	 * changement temporaire de focus est en cours. Si c'est le cas, le focusOwner
	 * permanent redeviendra &galement le focusOwner à la fin de ce changement de
	 * focus temporaire.
	 * 
	 * @return Component
	 */
	public static Component getPermanentFocusOwner() {
		// return new DefaultKeyboardFocusManager().getPermanentFocusOwner();
		return FocusManager.getCurrentManager().getPermanentFocusOwner();
	}

	/**
	 * Retourne la fenêtre possédant le focus.
	 * 
	 * @return Component
	 */
	public static Window getFocusedWindow() {
		// return new DefaultKeyboardFocusManager().getFocusedWindow();
		return FocusManager.getCurrentManager().getFocusedWindow();
	}

	/**
	 * Démarre un composant dans une Frame (utile pour écrire des méthodes main sur
	 * des panels en développement).
	 * 
	 * @param component JComponent
	 * @return la Frame créée
	 */
	public static JFrame run(final JComponent component) {
		final JFrame frame = new JFrame();
		try {
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			component.setOpaque(true); // si opaque false, il y a des pbs de paint
			frame.setContentPane(component);
			frame.setTitle(component.getClass().getName());
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (final Exception exception) {
			exception.printStackTrace();
		}
		return frame;
	}

	/**
	 * Démarre un composant dans une Frame sans pack() (utile pour écrire des
	 * méthodes main sur des panels en développement).
	 * 
	 * @param component JComponent
	 * @return la Frame créée
	 */
	public static JFrame runUnpacked(final JComponent component) {
		component.setPreferredSize(component.getSize());
		return run(component);
	}

	/**
	 * Méthode utilisée par SpiBasicTable et SpiComboBox pour essayer d'extraire un
	 * texte d'un composant de rendu.
	 * 
	 * @param rendererComponent Component
	 * @return String ou null si texte non trouvé
	 */
	public static String getTextFromRendererComponent(final Component rendererComponent) {
		String text;
		if (rendererComponent instanceof JLabel) {
			text = ((JLabel) rendererComponent).getText();
			if (text == null || text.length() == 0) {
				text = ((JLabel) rendererComponent).getToolTipText();
				if (text == null) {
					text = "";
				}
			}
		} else if (rendererComponent instanceof JTextComponent) {
			text = ((JTextComponent) rendererComponent).getText();
			if (text == null || text.length() == 0) {
				text = ((JTextComponent) rendererComponent).getToolTipText();
				if (text == null) {
					text = "";
				}
			}
		} else if (rendererComponent instanceof JCheckBox) {
			text = String.valueOf(((JCheckBox) rendererComponent).isSelected() ? "vrai" : "faux");
		} else {
			// texte non trouvé
			text = null;
		}
		return text;
	}

	/**
	 * Initialisation d'événements sur la touche Escape pour fermer les dialogues.
	 */
	public static void initEscapeClosesDialogs() {
		final AWTEventListener awtEventListener = new AWTEventListener() {
			/** {@inheritDoc} */
			@Override
			public void eventDispatched(final AWTEvent event) {
				if (event instanceof KeyEvent && ((KeyEvent) event).getKeyCode() == KeyEvent.VK_ESCAPE
						&& event.getID() == KeyEvent.KEY_PRESSED) {
					escapePressed();
				}
			}
		};
		Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * La touche Esc a été pressée : fermer la dialogue modale ouverte.
	 */
	private static void escapePressed() {
		final Component focusOwner = SpiSwingUtilities.getPermanentFocusOwner();
		final Window focusedWindow = SwingUtilities.getWindowAncestor(focusOwner);
		if (focusedWindow instanceof Dialog && ((Dialog) focusedWindow).isModal()) {
			try {
				final Robot robot = new Robot();
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_ALT);
			} catch (final AWTException v_e) {
				throw new Spi4jRuntimeException("Impossible de fermer la fenêtre de dialogue avec la touche Esc.",
						"???");
			}
		}
	}

	/**
	 * Affiche un splash screen lors du lancement de l'application (ne pas oublier
	 * d'appeler {@link #hideSplashScreen()} à la fin de l'initialisation, juste
	 * après l'affichage du premier écran)
	 * 
	 * @param p_titreApp le titre de la fenêtre du splash screen
	 * @param p_splash   le chemin vers l'image
	 */
	public static void showSplashScreen(final String p_titreApp, final String p_splash) {
		// affichage du splash
		try {
			final BufferedImage v_image = ImageIO.read(SpiSwingUtilities.class.getResourceAsStream(p_splash));
			v_splash = new Frame(p_titreApp) {
				private static final long serialVersionUID = 1L;

				@Override
				public void paint(final Graphics p_g) {
					p_g.drawImage(v_image, 0, 0, null);
				}
			};
			v_splash.setUndecorated(true);
			v_splash.setSize(v_image.getWidth(), v_image.getHeight());
			// afficher la fenêtre centrée
			v_splash.setLocationRelativeTo(null);
			v_splash.setVisible(true);
		} catch (final IOException v_e) {
			LogManager.getLogger(SpiSwingUtilities.class).warn("Splash non trouvé : " + p_splash, v_e);
		}
	}

	/**
	 * Masquage du splash screen (si celui-ci a été affiché précédemment)
	 */
	public static void hideSplashScreen() {
		// suppression du splash
		if (v_splash != null) {
			v_splash.dispose();
		}
	}

	/**
	 * Permet d'exécuter du code sur l'EDT de manière sûre.
	 * 
	 * @param p_runnable le code à exécuter Exemple d'appel:
	 *
	 *                   <pre>
	 *                   SpiSwingUtilities.invokeLaterIfNeeded(new Runnable() {
	 *                   	public void run() {
	 *                   		System.out.println(&quot;invokeLaterIfNeeded() ==&gt; isDispatchThread=&quot; + EventQueue.isDispatchThread());
	 *                   		_pgBar_avancementTrt.setValue(p_valProgression);
	 *                   	}
	 *                   });
	 *                   </pre>
	 */
	public static void invokeLaterIfNeeded(final Runnable p_runnable) {
		if (SwingUtilities.isEventDispatchThread() == true) {
			p_runnable.run();
		} else {
			SwingUtilities.invokeLater(p_runnable);
		}
	}

}
