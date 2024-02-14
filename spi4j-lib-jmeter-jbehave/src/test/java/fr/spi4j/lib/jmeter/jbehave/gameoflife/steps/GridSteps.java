/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jmeter.jbehave.gameoflife.steps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbehave.core.annotations.Aliases;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import fr.spi4j.lib.jmeter.jbehave.gameoflife.domain.Game;
import fr.spi4j.lib.jmeter.jbehave.gameoflife.view.string.StringRenderer;

/**
 * Classe de steps pour les tests.
 * @author MINARM
 */
public class GridSteps
{

   private static final Logger c_log = LogManager.getLogger(GridSteps.class);

   private Game _game;

   private StringRenderer _renderer;

   /**
    * Démarrage du jeu.
    * @param p_width
    *           largeur du jeu
    * @param p_height
    *           hauteur du jeu
    */
   @Given("a $width by $height game")
   @Aliases(values =
   {"a new game: $width by $height" })
   public void theGameIsRunning (final int p_width, final int p_height)
   {
      _game = new Game(p_width, p_height);
      _renderer = new StringRenderer();
      _game.setObserver(_renderer);
   }

   /**
    * Cocher / Décocher une cellule.
    * @param p_column
    *           la colonne
    * @param p_row
    *           la ligne
    */
   @When("I toggle the cell at ($column, $row)")
   public void iToggleTheCellAt (final int p_column, final int p_row)
   {
      _game.toggleCellAt(p_column, p_row);
   }

   /**
    * Attendre un peu.
    * @param p_ms
    *           la durée en ms
    */
   @When("I wait $ms ms")
   public void iWaitXms (final int p_ms)
   {
      try
      {
         c_log.info("Waiting for " + p_ms + " ms");
         Thread.sleep(p_ms);
      }
      catch (final InterruptedException v_e)
      {
         c_log.error("Problem while waiting", v_e);
      }
   }

   /**
    * Retourne une exception.
    * @param p_msg
    *           le message de l'exception
    */
   @When("fail with \"$msg\"")
   public void fail (final String p_msg)
   {
      throw new RuntimeException(p_msg);
   }

   /**
    * Vérification de l'état de la grille.
    * @param p_grid
    *           la grille attendue
    */
   @Then("the grid should look like $grid")
   @Aliases(values =
   {"the grid should be $grid" })
   public void theGridShouldLookLike (final String p_grid)
   {
      assertThat(_renderer.asString(), equalTo(p_grid));
   }
}
