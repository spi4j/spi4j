/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package bean.data;

/**
 * @author MINARM
 */
public class Produit
{
   /** Le nom du produit */
   private String _nom;

   /** La quantité du produit */
   private Integer _quantite;

   /**
    * Constructeur max.
    * @param p_nom
    *           Le nom du produit
    * @param p_quantite
    *           La quantité du produit
    */
   public Produit (final String p_nom, final Integer p_quantite)
   {
      super();
      _nom = p_nom;
      _quantite = p_quantite;
   }

   /**
    * Obtenir le nom du produit.
    * @return Le nom.
    */
   public String get_nom ()
   {
      return _nom;
   }

   /**
    * Affecter le nom du produit.
    * @param p_nom
    *           Le nom.
    */
   public void set_nom (final String p_nom)
   {
      _nom = p_nom;
   }

   /**
    * Obtenir la quantité du produit.
    * @return La quantité.
    */
   public Integer get_quantite ()
   {
      return _quantite;
   }

   /**
    * Affecter la quantité du produit.
    * @param p_quantite
    *           La quantité.
    */
   public void set_quantite (final Integer p_quantite)
   {
      _quantite = p_quantite;
   }

}
