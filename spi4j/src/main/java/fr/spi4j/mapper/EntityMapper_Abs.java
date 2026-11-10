/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.spi4j.ReflectUtil;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Classe abstraite de services de conversion entre un objet métier et un objet xml.
 * @author MINARM
 * @param <TypeEntity>
 *           Le type d'objet métier.
 * @param <TypeXto>
 *           Le type d'objet xml.
 */
public abstract class EntityMapper_Abs<TypeEntity extends Entity_Itf<?>, TypeXto extends Xto_Itf<?>> implements
         EntityMapper_Itf<TypeEntity, TypeXto>
{

   @Override
   public TypeEntity convertXtoToEntity (final TypeXto p_xto)
   {
      // instanciation du Entity
      final TypeEntity v_retour = getInstanceOfEntity();
      // copie des champs
      copyFields(p_xto, v_retour);
      return v_retour;
   }

   @Override
   public TypeXto convertEntityToXto (final TypeEntity p_entity)
   {
      // instanciation du XTO
      final TypeXto v_retour = getInstanceOfXto();
      // copie des champs
      copyFields(p_entity, v_retour);
      return v_retour;
   }


   @Override
   @SuppressWarnings("unchecked")
   public List<TypeEntity> convertListXtoToListEntity (final List<TypeXto> p_tab_xto)
   {
      if (p_tab_xto == null)
      {
         return null;
      }
      final List<TypeEntity> v_retour = (List<TypeEntity>) createCollectionOfSameType(p_tab_xto);
      for (final TypeXto v_xto : p_tab_xto)
      {
         v_retour.add(convertXtoToEntity(v_xto));
      }
      return v_retour;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<TypeXto> convertListEntityToListXto (final List<TypeEntity> p_tab_entity)
   {
      if (p_tab_entity == null)
      {
         return null;
      }
      final List<TypeXto> v_retour = (List<TypeXto>) createCollectionOfSameType(p_tab_entity);
      for (final TypeEntity v_entity : p_tab_entity)
      {
         v_retour.add(convertEntityToXto(v_entity));
      }
      return v_retour;
   }

   @Override
   @SuppressWarnings("unchecked")
   public final TypeEntity convertXtoItfToEntity (final Xto_Itf<?> p_xto)
   {
      return convertXtoToEntity((TypeXto) p_xto);
   }

   @Override
   @SuppressWarnings("unchecked")
   public final TypeXto convertEntityItfToXto (final Entity_Itf<?> p_entity)
   {
      return convertEntityToXto((TypeEntity) p_entity);
   }

   /**
    * Copie champ par champ d'un objet source vers un objet destination.
    * @param p_from
    *           Objet source
    * @param p_to
    *           Objet destination
    */
   private void copyFields (final Object p_from, final Object p_to)
   {
      // récupération des attributs de l'objet source, et vérification avec ceux de l'objet destination
      final List<String> v_fields = listFields(p_from, p_to);

      // parcours de chaque attribut (v_fields ne peut pas être null)
      for (final String v_fieldName : v_fields)
      {
         try
         {
            copyField(p_from, p_to, v_fieldName);
         }
         catch (final Exception v_e)
         {
            throw new Spi4jRuntimeException(v_e, "Impossible de trouver le champ " + v_fieldName + " dans l'objet : "
                     + p_from.getClass(), "Vérifier la configuration du Entity et XTO pour " + p_from.getClass()
                     + " / "
                     + p_to.getClass());
         }
      }
   }

   /**
    * Copie d'un champ d'un objet source vers un objet destination.
    * @param p_from
    *           Objet source
    * @param p_to
    *           Objet destination
    * @param p_fieldName
    *           Nom du champ
    */
   private void copyField (final Object p_from, final Object p_to, final String p_fieldName)
   {
      // récupération de la valeur de l'objet source
      final Object v_fieldValue = getFieldValue(p_from, p_fieldName);

      // si le champ contient un Entity ou un XTO, il faut le convertir également
      if (v_fieldValue instanceof Entity_Itf<?>)
      {
         // conversion du Entity en XTO
         convertAndSetEntity(p_to, p_fieldName, (Entity_Itf<?>) v_fieldValue);
      }
      else if (v_fieldValue instanceof Xto_Itf<?>)
      {
         // conversion du XTO en Entity
         convertAndSetXto(p_to, p_fieldName, (Xto_Itf<?>) v_fieldValue);
      }

      // si le champ contient une liste de Entities ou XTOs, il faut les convertir également
      else if (v_fieldValue instanceof Collection<?>)
      {
         // c'est une collection
         final Collection<?> v_coll = (Collection<?>) v_fieldValue;
         setCollectionFieldValue(p_to, p_fieldName, v_coll);
      }

      // si le champ contient une map de Entities ou XTOs, il faut les convertir également
      else if (v_fieldValue instanceof Map<?, ?>)
      {
         // c'est une map
         final Map<?, ?> v_map = (Map<?, ?>) v_fieldValue;
         setMapFieldValue(p_to, p_fieldName, v_map);
      }
      else
      {
         // mise à jour de la valeur dans l'objet destination
         setFieldValue(p_to, p_fieldName, v_fieldValue);
      }
   }

   /**
    * Modifie la valeur d'un champ de type Collection dans un objet.
    * @param p_instance
    *           l'instance de l'objet
    * @param p_fieldName
    *           le nom du champ
    * @param p_coll
    *           la nouvelle valeur du champ
    */
   @SuppressWarnings("unchecked")
   private void setCollectionFieldValue (final Object p_instance, final String p_fieldName, final Collection<?> p_coll)
   {
      if (!p_coll.isEmpty())
      {
         // récupération du premier élément non null pour tester son type
         final Object v_collElementTest = getFirstNonNull(p_coll);
         if (v_collElementTest instanceof Entity_Itf<?>)
         {
            // conversion de la collection de Entity en collection de XTO
            convertAndSetCollectionEntity(p_instance, p_fieldName, (Collection<Entity_Itf<?>>) p_coll);
         }
         else if (v_collElementTest instanceof Xto_Itf<?>)
         {
            // conversion de la collection de XTO en collection de Entity
            convertAndSetCollectionXto(p_instance, p_fieldName, (Collection<Xto_Itf<?>>) p_coll);
         }
         else
         {
            // mise à jour simple de la valeur dans l'objet destination
            setFieldValue(p_instance, p_fieldName, p_coll);
         }
      }
      else
      {
         // mise à jour simple de la valeur dans l'objet destination
         setFieldValue(p_instance, p_fieldName, p_coll);
      }
   }

   /**
    * Modifie la valeur d'un champ de type Map dans un objet.
    * @param p_instance
    *           l'instance de l'objet
    * @param p_fieldName
    *           le nom du champ
    * @param p_map
    *           la nouvelle valeur du champ
    */
   @SuppressWarnings("unchecked")
   private void setMapFieldValue (final Object p_instance, final String p_fieldName, final Map<?, ?> p_map)
   {
      // création d'une map qui pourra être transformée
      Map<?, ?> v_mapTransformee = p_map;
      if (!p_map.isEmpty())
      {
         // récupération de la première clé et de la première valeur non nulls pour tester le type
         final Object v_keyValueTest = getFirstNonNull(p_map.keySet());
         final Object v_valueValueTest = getFirstNonNull(p_map.values());
         // conversion des clés de la map
         if (v_keyValueTest instanceof Entity_Itf<?>)
         {
            v_mapTransformee = convertMapEntityAsKey((Map<Entity_Itf<?>, Object>) v_mapTransformee, p_fieldName);
         }
         else if (v_keyValueTest instanceof Xto_Itf<?>)
         {
            v_mapTransformee = convertMapXtoAsKey((Map<Xto_Itf<?>, Object>) v_mapTransformee, p_fieldName);
         }
         // conversion des valeurs de la map
         if (v_valueValueTest instanceof Entity_Itf<?>)
         {
            v_mapTransformee = convertMapEntityAsValue((Map<Object, Entity_Itf<?>>) v_mapTransformee, p_fieldName);
         }
         else if (v_valueValueTest instanceof Xto_Itf<?>)
         {
            v_mapTransformee = convertMapXtoAsValue((Map<Object, Xto_Itf<?>>) v_mapTransformee, p_fieldName);
         }
      }

      // mise à jour de la map convertie dans l'objet destination
      setFieldValue(p_instance, p_fieldName, v_mapTransformee);
   }

   /**
    * Convertit une map dans laquelle les clés sont des Entity.
    * @param p_map
    *           la map de Entity initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de XTO convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapEntityAsKey (final Map<Entity_Itf<?>, Object> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Xto_Itf<?>, Object> v_newMap = (Map<Xto_Itf<?>, Object>) createMapOfSameType(p_map);
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Entry<Entity_Itf<?>, ?> v_mapEntry : p_map.entrySet())
         {
            if (v_mapEntry.getKey() == null)
            {
               v_newMap.put(null, v_mapEntry.getValue());
            }
            else
            {
               final Entity_Itf<?> v_entityElement = v_mapEntry.getKey();
               final Xto_Itf<?> v_xtoElement = v_specificMapper.convertEntityItfToXto(v_entityElement);
               v_newMap.put(v_xtoElement, v_mapEntry.getValue());
            }
         }
      }
      return v_newMap;
   }

   /**
    * Convertit une map dans laquelle les clés sont des XTO.
    * @param p_map
    *           la map de XTO initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de Entity convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapXtoAsKey (final Map<Xto_Itf<?>, Object> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Entity_Itf<?>, Object> v_newMap = (Map<Entity_Itf<?>, Object>) createMapOfSameType(p_map);
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Entry<Xto_Itf<?>, ?> v_mapEntry : p_map.entrySet())
         {
            if (v_mapEntry.getKey() == null)
            {
               v_newMap.put(null, v_mapEntry.getValue());
            }
            else
            {
               final Xto_Itf<?> v_xtoElement = v_mapEntry.getKey();
               final Entity_Itf<?> v_entityElement = v_specificMapper.convertXtoItfToEntity(v_xtoElement);
               v_newMap.put(v_entityElement, v_mapEntry.getValue());
            }
         }
      }
      return v_newMap;
   }

   /**
    * Convertit la map de Entity p_map en map de XTO.
    * @param p_map
    *           la map de Entity initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de XTO convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapEntityAsValue (final Map<Object, Entity_Itf<?>> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Object, Xto_Itf<?>> v_newMap = (Map<Object, Xto_Itf<?>>) createMapOfSameType(p_map);
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Entry<?, Entity_Itf<?>> v_mapEntry : p_map.entrySet())
         {
            if (v_mapEntry.getValue() == null)
            {
               v_newMap.put(v_mapEntry.getKey(), null);
            }
            else
            {
               final Entity_Itf<?> v_entityElement = v_mapEntry.getValue();
               final Xto_Itf<?> v_xtoElement = v_specificMapper.convertEntityItfToXto(v_entityElement);
               v_newMap.put(v_mapEntry.getKey(), v_xtoElement);
            }
         }
      }
      return v_newMap;
   }

   /**
    * Convertit la map de XTO p_map en map de Entity et l'insère dans le champ p_fieldName de p_to.
    * @param p_map
    *           la map de XTO initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de Entity convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapXtoAsValue (final Map<Object, Xto_Itf<?>> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Object, Entity_Itf<?>> v_newMap = (Map<Object, Entity_Itf<?>>) createMapOfSameType(p_map);
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Entry<?, Xto_Itf<?>> v_mapEntry : p_map.entrySet())
         {
            if (v_mapEntry.getValue() == null)
            {
               v_newMap.put(v_mapEntry.getKey(), null);
            }
            else
            {
               final Xto_Itf<?> v_xtoElement = v_mapEntry.getValue();
               final Entity_Itf<?> v_entityElement = v_specificMapper.convertXtoItfToEntity(v_xtoElement);
               v_newMap.put(v_mapEntry.getKey(), v_entityElement);
            }
         }
      }
      return v_newMap;
   }

   /**
    * Retourne le premier élément non null de la collection en paramètre, ou null sinon.
    * @param p_collection
    *           Collection
    * @return Object
    */
   private static Object getFirstNonNull (final Collection<?> p_collection)
   {
      Object v_retour = null;
      for (final Object v_value : p_collection)
      {
         if (v_value != null)
         {
            v_retour = v_value;
            break;
         }
      }
      return v_retour;
   }

   /**
    * @param p_collection
    *           Collection
    * @return Collection
    */
   private static Collection<?> createCollectionOfSameType (final Collection<?> p_collection)
   {
      Collection<?> v_retour;
      try
      {
         v_retour = p_collection.getClass().getDeclaredConstructor().newInstance();
      }
      catch (final Exception v_e)
      {
         v_retour = new ArrayList<>(p_collection.size());
      }
      return v_retour;
   }

   /**
    * Crée une map de même type (HashMap ou LinkedHashMap ou TreeMap ...)
    * @param p_map
    *           Map
    * @return Map
    */
   private static Map<?, ?> createMapOfSameType (final Map<?, ?> p_map)
   {
      Map<?, ?> v_newMap;
      try
      {
         v_newMap = p_map.getClass().getDeclaredConstructor().newInstance();
      }
      catch (final Throwable v_e)
      {
         v_newMap = new HashMap<>();
      }
      return v_newMap;
   }

   /**
    * Convertit le Entity p_fieldValue en XTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra le XTO converti
    * @param p_fieldValue
    *           le Entity initial qui sera converti
    */
   private void convertAndSetEntity (final Object p_to, final String p_fieldName, final Entity_Itf<?> p_fieldValue)
   {
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         final Xto_Itf<?> v_specificXto = v_specificMapper.convertEntityItfToXto(p_fieldValue);
         setFieldValue(p_to, p_fieldName, v_specificXto);
      }
   }

   /**
    * Convertit le XTO p_fieldValue en Entity et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra le Entity converti
    * @param p_fieldValue
    *           le XTO initial qui sera converti
    */
   private void convertAndSetXto (final Object p_to, final String p_fieldName, final Xto_Itf<?> p_fieldValue)
   {
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         final Entity_Itf<?> v_specificEntity = v_specificMapper.convertXtoItfToEntity(p_fieldValue);
         setFieldValue(p_to, p_fieldName, v_specificEntity);
      }
   }

   /**
    * Convertit la collection de Entity p_coll en collection de XTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la collection de XTO convertie
    * @param p_coll
    *           la collection de Entity initiale qui sera convertie
    */
   @SuppressWarnings("unchecked")
   private void convertAndSetCollectionEntity (final Object p_to, final String p_fieldName,
            final Collection<Entity_Itf<?>> p_coll)
   {
      // instanciation d'une nouvelle collection
      final Collection<Xto_Itf<?>> v_newColl = (Collection<Xto_Itf<?>>) createCollectionOfSameType(p_coll);
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Object v_collElement : p_coll)
         {
            if (v_collElement == null)
            {
               v_newColl.add(null);
            }
            else
            {
               final Entity_Itf<?> v_entityElement = (Entity_Itf<?>) v_collElement;
               final Xto_Itf<?> v_xtoElement = v_specificMapper.convertEntityItfToXto(v_entityElement);
               v_newColl.add(v_xtoElement);
            }
         }
         setFieldValue(p_to, p_fieldName, v_newColl);
      }
   }

   /**
    * Convertit la collection de XTO p_coll en collection de Entity et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la collection de Entity convertie
    * @param p_coll
    *           la collection de XTO initiale qui sera convertie
    */
   @SuppressWarnings("unchecked")
   private void convertAndSetCollectionXto (final Object p_to, final String p_fieldName,
            final Collection<Xto_Itf<?>> p_coll)
   {
      // instanciation d'une nouvelle collection
      final Collection<Entity_Itf<?>> v_newColl = (Collection<Entity_Itf<?>>) createCollectionOfSameType(p_coll);
      final EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Object v_collElement : p_coll)
         {
            if (v_collElement == null)
            {
               v_newColl.add(null);
            }
            else
            {
               final Xto_Itf<?> v_xtoElement = (Xto_Itf<?>) v_collElement;
               final Entity_Itf<?> v_entityElement = v_specificMapper.convertXtoItfToEntity(v_xtoElement);
               v_newColl.add(v_entityElement);
            }
         }
         setFieldValue(p_to, p_fieldName, v_newColl);
      }
   }

   /**
    * Récupère la valeur d'un champ pour un objet.
    * @param p_instance
    *           l'instance de l'objet
    * @param p_fieldName
    *           le nom du champ
    * @return la valeur de ce champ pour cette instance d'objet
    */
   private static Object getFieldValue (final Object p_instance, final String p_fieldName)
   {
      try
      {
         // On n'appelle pas le getter pour ne pas faire appel au lazy loading dans le Entity.
         // On ne veut certainement pas charger toutes les références de chacun des objets qu'on convertit alors qu'on n'utilisera pas ces données dans le XTO.
         // Si ces données sont réellement nécessaires dans le XTO, alors elles auront dû être chargées spécifiquement par les services dans le Entity.
         final Class<? extends Object> v_class = p_instance.getClass();
         final Field v_field = v_class.getDeclaredField(p_fieldName);
         v_field.setAccessible(true);
         return v_field.get(p_instance);
      }
      catch (final Throwable v_e)
      {
         throw new Spi4jRuntimeException(v_e, v_e.getMessage(), "???");
      }
   }

   /**
    * Modifie la valeur d'un champ dans un objet.
    * @param p_instance
    *           l'instance de l'objet
    * @param p_fieldName
    *           le nom du champ
    * @param p_value
    *           la nouvelle valeur du champ
    */
   @SuppressWarnings("deprecation")
   private static void setFieldValue (final Object p_instance, final String p_fieldName, final Object p_value)
   {
      try
      {
         final Class<? extends Object> v_class = p_instance.getClass();
         try
         {
            // en passant par le setter
            final Method v_setter;
            if (p_value == null)
            {
               v_setter = v_class.getDeclaredMethod("set" + p_fieldName, Object.class);
            }
            else
            {
               v_setter = v_class.getDeclaredMethod("set" + p_fieldName, p_value.getClass());
            }
            ReflectUtil.invokeMethod(v_setter, p_instance, p_value);
         }
         catch (final NoSuchMethodException v_e)
         {
            // le setter n'a pas été trouvé dans la classe, utilisation de l'attribut directement
            final Field v_field = v_class.getDeclaredField(p_fieldName);
            final boolean v_accessible = v_field.isAccessible();
            v_field.setAccessible(true);
            v_field.set(p_instance, p_value);
            v_field.setAccessible(v_accessible);
         }
      }
      catch (final Throwable v_e)
      {
         throw new Spi4jRuntimeException(v_e, v_e.getMessage(), "???");
      }
   }

   /**
    * Retourne la liste des champs pour ce couple d'objets métier / xml.
    * @param p_obj1
    *           (In)(*) L'objet métier de ce mapper.
    * @param p_obj2
    *           (In)(*) L'objet xml de ce mapper.
    * @return la liste des champs pour ce couple d'objets métier / xml.
    */
   private static List<String> listFields (final Object p_obj1, final Object p_obj2)
   {
      final List<String> v_retour = new ArrayList<>();

      // liste les champs du entity
      for (final Field v_field : p_obj1.getClass().getDeclaredFields())
      {
         // on exclut les champs static final comme serialVersionUID car ils ne nous intéressent pas (et en plus on ne peut pas changer la valeur)
         // on exclut également le champ qui contient la valeur de la version (@JdbcVersion ou @Version)
         if (isFieldCopiable(v_field))
         {
            v_retour.add(v_field.getName());
         }
      }

      // vérifie que les champs du xto sont identiques
      final List<String> v_verif = new ArrayList<>();
      for (final Field v_field : p_obj2.getClass().getDeclaredFields())
      {
         // on exclut les champs static final comme serialVersionUID car ils ne nous intéressent pas (et en plus on ne peut pas changer la valeur)
         // on exclut également le champ qui contient la valeur de la version (@JdbcVersion ou @Version)
         if (isFieldCopiable(v_field))
         {
            v_verif.add(v_field.getName());
         }
      }

      if (!v_retour.containsAll(v_verif) || !v_verif.containsAll(v_retour))
      {
         final StringBuilder v_erreurs = new StringBuilder();
         final List<String> v_verifNotInRetour = new ArrayList<>(v_verif);
         v_verifNotInRetour.removeAll(v_retour);
         final List<String> v_retourNotInVerif = new ArrayList<>(v_retour);
         v_retourNotInVerif.removeAll(v_verif);

         String v_separator = "";
         for (final String v_str1 : v_retourNotInVerif)
         {
            v_erreurs.append(v_separator);
            v_erreurs.append(v_str1).append(" est présent dans ").append(p_obj1.getClass())
                     .append(" mais absent dans ").append(p_obj2.getClass());
            v_separator = ", ";
         }
         for (final String v_str2 : v_verifNotInRetour)
         {
            v_erreurs.append(v_separator);
            v_erreurs.append(v_str2).append(" est présent dans ").append(p_obj2.getClass())
                     .append(" mais absent dans ").append(p_obj1.getClass());
            v_separator = ", ";
         }
         throw new Spi4jRuntimeException("Problème de configuration des attributs entre le Entity et le XTO de "
                  + p_obj1.getClass() + " / " + p_obj2.getClass() + " : " + v_erreurs,
                  "Vérifier la configuration des attributs pour le Entity et le XTO de " + p_obj1.getClass() + " / "
                           + p_obj2.getClass());
      }

      return v_retour;
   }

   /**
    * Est-ce que cet attribut est copiable
    * @param p_field
    *           Field
    * @return boolean
    */
   private static boolean isFieldCopiable (final Field p_field)
   {
      boolean v_copiable = true;
      // si le champ est static et final on ne le copie pas
      final int v_modifiers = p_field.getModifiers();
      v_copiable &= !Modifier.isStatic(v_modifiers) && !Modifier.isFinal(v_modifiers);

      // si le champ est le champ qui contient la valeur de la version (@JdbcVersion / @Version) on le copie car on en a besoin dans GWT
      // v_copiable &= !p_field.getName().equals(MatchHelper.c_entityVersionValueFieldName);

      return v_copiable;
   }

   /**
    * Retourne une instance de l'objet métier.
    * @return une nouvelle instance d'objet métier.
    */
   abstract protected TypeEntity getInstanceOfEntity ();

   /**
    * Retourne une instance de l'objet xml.
    * @return une nouvelle instance d'objet xml.
    */
   abstract protected TypeXto getInstanceOfXto ();

   /**
    * Retourne un mapper spécifique pour le champ p_fieldName.
    * @param p_fieldName
    *           le champ qui nécessite un mapper spécifique
    * @return un mapper spécifique pour le champ p_fieldName
    */
   protected EntityMapper_Itf<? extends Entity_Itf<?>, ? extends Xto_Itf<?>> getSpecificMapper (final String p_fieldName)
   {
      throw new Spi4jRuntimeException("Un mapper a été demandé pour le champ " + p_fieldName
               + " mais aucun n'a été renseigné dans " + getClass(), "Renseigner un mapper spécifique pour le champ "
               + p_fieldName + " dans " + getClass());
   }

}
