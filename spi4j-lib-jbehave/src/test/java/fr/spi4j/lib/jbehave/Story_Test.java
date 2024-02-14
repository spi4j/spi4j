/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import java.util.ArrayList;
import java.util.List;

import fr.spi4j.lib.jbehave.requirement.Requirement_EnumForTest;
import fr.spi4j.requirement.Requirement_Itf;
import fr.spi4j.ui.mvp.MVPUtils;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.ViewsAssociation;

/**
 * Test de story.
 * 
 * @author MINARM
 */
public class Story_Test extends SpiStory_Abs {

	@Override
	public void setUp() {
		MVPUtils.reinit();
		super.setUp();
	}

	@Override
	public List<Object> getStepsClassesInstances() {
		final List<Object> v_classes = new ArrayList<>();
		v_classes.add(new StorySteps(this));
		return v_classes;
	}

	@Override
	public ViewsAssociation getViewsAssociation() {
		return new ViewsAssociation() {
			@SuppressWarnings("unchecked")
			@Override
			public <TypeView extends View_Itf> TypeView getViewForPresenter(
					final Presenter_Abs<TypeView, ?> p_presenter) {
				return (TypeView) new View_Itf() {
					@Override
					public void setTitle(final String p_title) {
						// RAS
					}

					@Override
					public void restoreView(final View_Itf p_view) {
						// RAS
					}

					@Override
					public void removeView(final View_Itf p_view) {
						// RAS
					}

					@Override
					public boolean isModal() {
						return false;
					}

					@Override
					public String getTitle() {
						return "";
					}

					@Override
					public void addView(final View_Itf p_view) {
						// RAS
					}

					@Override
					public void beforeClose() {
						// Aucun traitement par défaut.
						// Méthode à surcharger si besoin
					}

					@Override
					public Presenter_Abs<? extends View_Itf, ?> getPresenter() {
						return null;
					}
				};
			}

			@Override
			public Presenter_Abs<? extends View_Itf, ?> getPresenterForAnnotatedView(final String p_userView) {
				return null;
			}
		};
	}

	@Override
	public Requirement_Itf findRequirement(final String p_req) {
		return Requirement_EnumForTest.valueOf(p_req);
	}

	@Override
	public boolean shouldCheckRequirements() {
		return true;
	}

	@Override
	public boolean shouldGenerateViewAfterStories() {
		return false;
	}

	@Override
	public int getThreadsToLaunch() {
		return 1;
	}
}
