/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package bean.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MINARM
 */
public class Personne implements java.io.Serializable
{
   /** L'id pour la Serialisation */
   private static final long serialVersionUID = 1L;

   private String _id;

   private String _nom;

   private String _prenom;

   private Integer _age;

   private List<Produit> _lstProduit = new ArrayList<>();

   /**
    * Constructeur par defaut
    */
   public Personne ()
   {
      super();
   }

   /**
    * Constructeur de la Class Bean
    * @param p_id
    *           L'identifiant de la personne.
    * @param p_nom
    *           Le nom de la personne.
    * @param p_prenom
    *           Le prénom de la personne.
    * @param p_age
    *           L'âge de la personne.
    */
   public Personne (final String p_id, final String p_nom, final String p_prenom, final Integer p_age)
   {
      this(p_id, p_nom, p_prenom, p_age, null);
   }

   /**
    * Constructeur de la Class Bean
    * @param p_id
    *           L'identifiant de la personne.
    * @param p_nom
    *           Le nom de la personne.
    * @param p_prenom
    *           Le prénom de la personne.
    * @param p_age
    *           L'âge de la personne.
    * @param p_lstProduit
    *           Les produits de la personne.
    */
   public Personne (final String p_id, final String p_nom, final String p_prenom, final Integer p_age,
            final List<Produit> p_lstProduit)
   {
      set_id(p_id);
      set_nom(p_nom);
      set_prenom(p_prenom);
      set_age(p_age);
      _lstProduit = p_lstProduit;
   }

   /**
    * Affecter l'identifiant de la personne.
    * @param p_id
    *           L'identifiant de la personne.
    */
   public void set_id (final String p_id)
   {
      _id = p_id;
   }

   /**
    * Obtenir l'identifiant de la personne.
    * @return l'identifiant de la personne.
    */
   public String get_id ()
   {
      return _id;
   }

   /**
    * Affecter le nom de la personne.
    * @param p_nom
    *           Le nom de la personne.
    */
   public void set_nom (final String p_nom)
   {
      _nom = p_nom;
   }

   /**
    * Obtenir nom de la personne.
    * @return le nom de la personne.
    */
   public String get_nom ()
   {
      return _nom;
   }

   /**
    * Affecter le prénom de la personne.
    * @param p_prenom
    *           Le prénom de la personne.
    */
   public void set_prenom (final String p_prenom)
   {
      _prenom = p_prenom;
   }

   /**
    * Obtenir le prénom de la personne.
    * @return prénom de la personne.
    */
   public String get_prenom ()
   {
      return _prenom;
   }

   /**
    * Affecter l'âge de la personne.
    * @param p_age
    *           L'âge de la personne.
    */
   public void set_age (final Integer p_age)
   {
      _age = p_age;
   }

   /**
    * Obtenir l'âge de la personne.
    * @return l'âge de la personne.
    */
   public Integer get_age ()
   {
      return _age;
   }

   /**
    * Obtenir la liste des produits.
    * @return La liste.
    */
   public List<Produit> get_lstProduit ()
   {
      return _lstProduit;
   }

   /**
    * Ajouter un produit à la personne.
    * @param p_Produit
    *           Le produit à ajouter.
    */
   public void addProduit (final Produit p_Produit)
   {
      _lstProduit.add(p_Produit);
   }

   /**
    * Le nom et le prénom pour réaliser un chart "camembert" avec cette colonne en discriminent.
    * @return Le nom et le prénom concaténés.
    */
   public String get_nomAndPrenom ()
   {
      return _nom + " " + _prenom;
   }

}
