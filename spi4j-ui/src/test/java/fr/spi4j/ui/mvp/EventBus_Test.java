/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.spi4j.ui.Sample.NoView;

/**
 * Classe de tests unitaires sur le EventBus.
 * 
 * @author MINARM
 */
public class EventBus_Test {

	private static final String c_ATTRIBUTE_IN_EVENT_INCREMENT = "attribute.increment";

	private int _number;

	/**
	 * Initialisation de la classe de tests.
	 */
	@BeforeAll
	public static void setUpBeforeClass() {
		MVPUtils.setViewManager(new ViewManager());
		MVPUtils.getInstance().getViewManager().setViewsAssociation(new ViewsAssociation() {
			@SuppressWarnings("unchecked")
			@Override
			public <TypeView extends View_Itf> TypeView getViewForPresenter(
					final Presenter_Abs<TypeView, ?> p_presenter) {
				return (TypeView) new NoView();
			}

			@Override
			public Presenter_Abs<? extends View_Itf, ?> getPresenterForAnnotatedView(final String p_userView) {
				return null;
			}
		});
	}

	/**
	 * Méthode d'initialisation des tests.
	 */
	@BeforeEach
	public void setUp() {
		_number = 0;
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWithoutReceivers() {
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		assertEquals(0, _number, "Mauvais nombre d'appels");
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWith1Receiver() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		v_receiver1.listen(EventTypeTest.EVENT1);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		assertEquals(EventReceiver.c_INCREMENT_1, _number, "Mauvais nombre d'appels");
		v_receiver1.close();
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWith2ReceiversOn2Events() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		final EventReceiver v_receiver2 = new EventReceiver(2);
		v_receiver1.listen(EventTypeTest.EVENT1);
		v_receiver2.listen(EventTypeTest.EVENT2);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT2, null));
		assertEquals(EventReceiver.c_INCREMENT_2, _number, "Mauvais nombre d'appels");
		v_receiver1.close();
		v_receiver2.close();
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWith2ReceiversOnSameEvent() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		final EventReceiver v_receiver2 = new EventReceiver(2);
		v_receiver1.listen(EventTypeTest.EVENT1);
		v_receiver2.listen(EventTypeTest.EVENT1);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		assertEquals(2 * EventReceiver.c_INCREMENT_1, _number, "Mauvais nombre d'appels");
		v_receiver1.close();
		v_receiver2.close();
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWith1ReceiverAnd2DifferentEvents() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		v_receiver1.listen(EventTypeTest.EVENT1);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT2, null));
		assertEquals(EventReceiver.c_INCREMENT_1, _number, "Mauvais nombre d'appels");
		v_receiver1.close();
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWith1ReceiverAnd2DifferentEventsIntercepter() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		v_receiver1.listen(EventTypeTest.EVENT1, EventTypeTest.EVENT2);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT2, null));
		assertEquals(EventReceiver.c_INCREMENT_1 + EventReceiver.c_INCREMENT_2, _number, "Mauvais nombre d'appels");
		MVPUtils.getInstance().getEventBus().stopListening(EventTypeTest.EVENT1, v_receiver1);
		v_receiver1.close();
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWith1ReceiverAnd2IdenticEvents() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		v_receiver1.listen(EventTypeTest.EVENT1);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT1, null));
		assertEquals(2 * EventReceiver.c_INCREMENT_1, _number, "Mauvais nombre d'appels");
		v_receiver1.close();
	}

	/**
	 * Test du bus.
	 */
	@Test
	public void testEventWithAttributes() {
		final int v_attributeIncrement = 13;
		final EventReceiver v_receiver1 = new EventReceiver(1);
		v_receiver1.listen(EventTypeTest.EVENT_WITH_ATTRIBUTES);
		final Event v_event = new Event(EventTypeTest.EVENT_WITH_ATTRIBUTES, null);
		v_event.addAttribute(c_ATTRIBUTE_IN_EVENT_INCREMENT, v_attributeIncrement);
		MVPUtils.getInstance().getEventBus().fireEvent(v_event);
		assertEquals(v_attributeIncrement, _number, "Mauvais nombre d'appels");
		v_receiver1.close();
	}

	/**
	 * Test d'un événement avec source.
	 */
	@Test
	public void testEventWithSource() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		final EventReceiver v_sender = new EventReceiver(2);
		v_receiver1.listen(EventTypeTest.EVENT_WITH_SOURCE);
		MVPUtils.getInstance().getEventBus().fireEvent(new Event(EventTypeTest.EVENT_WITH_SOURCE, v_sender));
		v_receiver1.close();
		v_sender.close();
	}

	/**
	 * Test d'un événement avec source.
	 */
	@Test
	public void testEventSentByPresenter() {
		final EventReceiver v_receiver1 = new EventReceiver(1);
		final EventReceiver v_sender = new EventReceiver(2);
		v_receiver1.listen(EventTypeTest.EVENT_WITH_SOURCE);
		assertTrue(v_receiver1.isListening(EventTypeTest.EVENT_WITH_SOURCE), "Le receveur devrait écouter l'événement");
		v_sender.fireEvent(new Event(EventTypeTest.EVENT_WITH_SOURCE, v_sender));
		v_receiver1.stopListening(EventTypeTest.EVENT_WITH_SOURCE);
		v_receiver1.close();
		v_sender.close();
	}

	/**
	 * Enumération des types d'événements.
	 * 
	 * @author MINARM
	 */
	private enum EventTypeTest implements EventType {
		EVENT1, EVENT2, EVENT_WITH_ATTRIBUTES, EVENT_WITH_SOURCE;
	}

	/**
	 * Receiver 1.
	 * 
	 * @author MINARM
	 */
	private class EventReceiver extends Presenter_Abs<View_Itf, Object> {

		private static final int c_INCREMENT_1 = 7;

		private static final int c_INCREMENT_2 = 11;

		private final int _id;

		/**
		 * Constructeur par défaut.
		 * 
		 * @param p_id l'id du receiver
		 */
		public EventReceiver(final int p_id) {
			super(null);
			_id = p_id;
			updateId();
		}

		@Override
		public void initView() {
			// aucune vue
		}

		@Override
		protected String doGenerateTitle() {
			return "event receiver";
		}

		@Override
		protected Object doGenerateId() {
			return _id;
		}

		@Override
		protected void onEvent(final Event p_event) {
			// ne fait rien
			super.onEvent(p_event);

			switch ((EventTypeTest) p_event.get_type()) {
			case EVENT1:
				_number += c_INCREMENT_1;
				break;
			case EVENT2:
				_number += c_INCREMENT_2;
				break;
			case EVENT_WITH_ATTRIBUTES:
				assertTrue(p_event.get_attributes().size() > 0, "L'événement devrait avoir des attributes");
				_number += (Integer) p_event.getAttribute(c_ATTRIBUTE_IN_EVENT_INCREMENT);
				break;
			case EVENT_WITH_SOURCE:
				assertNotNull(p_event.get_source(), "La source de l'événement devrait être définie");
				break;
			default:
				fail("Cas non prévu dans le EventReceiver");
			}
		}

	}
}
