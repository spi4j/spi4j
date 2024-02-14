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
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Classe abstraite de services de conversion entre un objet métier et un objet xml.
 * @author MINARM
 * @param <TypeDto>
 *           Le type d'objet métier.
 * @param <TypeXto>
 *           Le type d'objet xml.
 */
public abstract class Mapper_Abs<TypeDto extends Dto_Itf<?>, TypeXto extends Xto_Itf<?>>
         implements Mapper_Itf<TypeDto, TypeXto>
{

   @Override
   public TypeDto convertXtoToDto (final TypeXto p_xto)
   {
      // instanciation du DTO
      final TypeDto v_retour = getInstanceOfDto();
      // copie des champs
      copyFields(p_xto, v_retour);
      return v_retour;
   }

   @Override
   public TypeXto convertDtoToXto (final TypeDto p_dto)
   {
      // instanciation du XTO
      final TypeXto v_retour = getInstanceOfXto();
      // copie des champs
      copyFields(p_dto, v_retour);
      return v_retour;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<TypeDto> convertListXtoToListDto (final List<TypeXto> p_tab_xto)
   {
      if (p_tab_xto == null)
      {
         return null;
      }
      final List<TypeDto> v_retour = (List<TypeDto>) createCollectionOfSameType(p_tab_xto);
      for (final TypeXto v_xto : p_tab_xto)
      {
         v_retour.add(convertXtoToDto(v_xto));
      }
      return v_retour;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<TypeXto> convertListDtoToListXto (final List<TypeDto> p_tab_dto)
   {
      if (p_tab_dto == null)
      {
         return null;
      }
      final List<TypeXto> v_retour = (List<TypeXto>) createCollectionOfSameType(p_tab_dto);
      for (final TypeDto v_dto : p_tab_dto)
      {
         v_retour.add(convertDtoToXto(v_dto));
      }
      return v_retour;
   }

   @Override
   @SuppressWarnings("unchecked")
   public final TypeDto convertXtoItfToDto (final Xto_Itf<?> p_xto)
   {
      return convertXtoToDto((TypeXto) p_xto);
   }

   @Override
   @SuppressWarnings("unchecked")
   public final TypeXto convertDtoItfToXto (final Dto_Itf<?> p_dto)
   {
      return convertDtoToXto((TypeDto) p_dto);
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
                     + p_from.getClass(), "Vérifier la configuration du DTO et XTO pour " + p_from.getClass() + " / "
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

      // si le champ contient un DTO ou un XTO, il faut le convertir également
      if (v_fieldValue instanceof Dto_Itf<?>)
      {
         // conversion du DTO en XTO
         convertAndSetDto(p_to, p_fieldName, (Dto_Itf<?>) v_fieldValue);
      }
      else if (v_fieldValue instanceof Xto_Itf<?>)
      {
         // conversion du XTO en DTO
         convertAndSetXto(p_to, p_fieldName, (Xto_Itf<?>) v_fieldValue);
      }

      // si le champ contient une liste de DTOs ou XTOs, il faut les convertir également
      else if (v_fieldValue instanceof Collection<?>)
      {
         // c'est une collection
         final Collection<?> v_coll = (Collection<?>) v_fieldValue;
         setCollectionFieldValue(p_to, p_fieldName, v_coll);
      }

      // si le champ contient une map de DTOs ou XTOs, il faut les convertir également
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
         if (v_collElementTest instanceof Dto_Itf<?>)
         {
            // conversion de la collection de DTO en collection de XTO
            convertAndSetCollectionDto(p_instance, p_fieldName, (Collection<Dto_Itf<?>>) p_coll);
         }
         else if (v_collElementTest instanceof Xto_Itf<?>)
         {
            // conversion de la collection de XTO en collection de DTO
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
         if (v_keyValueTest instanceof Dto_Itf<?>)
         {
            v_mapTransformee = convertMapDtoAsKey((Map<Dto_Itf<?>, Object>) v_mapTransformee, p_fieldName);
         }
         else if (v_keyValueTest instanceof Xto_Itf<?>)
         {
            v_mapTransformee = convertMapXtoAsKey((Map<Xto_Itf<?>, Object>) v_mapTransformee, p_fieldName);
         }
         // conversion des valeurs de la map
         if (v_valueValueTest instanceof Dto_Itf<?>)
         {
            v_mapTransformee = convertMapDtoAsValue((Map<Object, Dto_Itf<?>>) v_mapTransformee, p_fieldName);
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
    * Convertit une map dans laquelle les clés sont des DTO.
    * @param p_map
    *           la map de DTO initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de XTO convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapDtoAsKey (final Map<Dto_Itf<?>, Object> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Xto_Itf<?>, Object> v_newMap = (Map<Xto_Itf<?>, Object>) createMapOfSameType(p_map);
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Entry<Dto_Itf<?>, ?> v_mapEntry : p_map.entrySet())
         {
            if (v_mapEntry.getKey() == null)
            {
               v_newMap.put(null, v_mapEntry.getValue());
            }
            else
            {
               final Dto_Itf<?> v_dtoElement = v_mapEntry.getKey();
               final Xto_Itf<?> v_xtoElement = v_specificMapper.convertDtoItfToXto(v_dtoElement);
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
    *           Le champ de l'instance qui recevra la map de DTO convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapXtoAsKey (final Map<Xto_Itf<?>, Object> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Dto_Itf<?>, Object> v_newMap = (Map<Dto_Itf<?>, Object>) createMapOfSameType(p_map);
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
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
               final Dto_Itf<?> v_dtoElement = v_specificMapper.convertXtoItfToDto(v_xtoElement);
               v_newMap.put(v_dtoElement, v_mapEntry.getValue());
            }
         }
      }
      return v_newMap;
   }

   /**
    * Convertit la map de DTO p_map en map de XTO.
    * @param p_map
    *           la map de DTO initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de XTO convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapDtoAsValue (final Map<Object, Dto_Itf<?>> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Object, Xto_Itf<?>> v_newMap = (Map<Object, Xto_Itf<?>>) createMapOfSameType(p_map);
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         for (final Entry<?, Dto_Itf<?>> v_mapEntry : p_map.entrySet())
         {
            if (v_mapEntry.getValue() == null)
            {
               v_newMap.put(v_mapEntry.getKey(), null);
            }
            else
            {
               final Dto_Itf<?> v_dtoElement = v_mapEntry.getValue();
               final Xto_Itf<?> v_xtoElement = v_specificMapper.convertDtoItfToXto(v_dtoElement);
               v_newMap.put(v_mapEntry.getKey(), v_xtoElement);
            }
         }
      }
      return v_newMap;
   }

   /**
    * Convertit la map de XTO p_map en map de DTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_map
    *           la map de XTO initiale qui sera convertie
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la map de DTO convertie
    * @return la map convertie
    */
   @SuppressWarnings("unchecked")
   private Map<?, ?> convertMapXtoAsValue (final Map<Object, Xto_Itf<?>> p_map, final String p_fieldName)
   {
      // instanciation d'une nouvelle map
      final Map<Object, Dto_Itf<?>> v_newMap = (Map<Object, Dto_Itf<?>>) createMapOfSameType(p_map);
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
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
               final Dto_Itf<?> v_dtoElement = v_specificMapper.convertXtoItfToDto(v_xtoElement);
               v_newMap.put(v_mapEntry.getKey(), v_dtoElement);
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
    * Convertit le DTO p_fieldValue en XTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra le XTO converti
    * @param p_fieldValue
    *           le DTO initial qui sera converti
    */
   private void convertAndSetDto (final Object p_to, final String p_fieldName, final Dto_Itf<?> p_fieldValue)
   {
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         final Xto_Itf<?> v_specificXto = v_specificMapper.convertDtoItfToXto(p_fieldValue);
         setFieldValue(p_to, p_fieldName, v_specificXto);
      }
   }

   /**
    * Convertit le XTO p_fieldValue en DTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra le DTO converti
    * @param p_fieldValue
    *           le XTO initial qui sera converti
    */
   private void convertAndSetXto (final Object p_to, final String p_fieldName, final Xto_Itf<?> p_fieldValue)
   {
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
      if (v_specificMapper != null)
      {
         final Dto_Itf<?> v_specificDto = v_specificMapper.convertXtoItfToDto(p_fieldValue);
         setFieldValue(p_to, p_fieldName, v_specificDto);
      }
   }

   /**
    * Convertit la collection de DTO p_coll en collection de XTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la collection de XTO convertie
    * @param p_coll
    *           la collection de DTO initiale qui sera convertie
    */
   @SuppressWarnings("unchecked")
   private void convertAndSetCollectionDto (final Object p_to, final String p_fieldName,
            final Collection<Dto_Itf<?>> p_coll)
   {
      // instanciation d'une nouvelle collection
      final Collection<Xto_Itf<?>> v_newColl = (Collection<Xto_Itf<?>>) createCollectionOfSameType(p_coll);
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
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
               final Dto_Itf<?> v_dtoElement = (Dto_Itf<?>) v_collElement;
               final Xto_Itf<?> v_xtoElement = v_specificMapper.convertDtoItfToXto(v_dtoElement);
               v_newColl.add(v_xtoElement);
            }
         }
         setFieldValue(p_to, p_fieldName, v_newColl);
      }
   }

   /**
    * Convertit la collection de XTO p_coll en collection de DTO et l'insère dans le champ p_fieldName de p_to.
    * @param p_to
    *           L'instance destinataire
    * @param p_fieldName
    *           Le champ de l'instance qui recevra la collection de DTO convertie
    * @param p_coll
    *           la collection de XTO initiale qui sera convertie
    */
   @SuppressWarnings("unchecked")
   private void convertAndSetCollectionXto (final Object p_to, final String p_fieldName,
            final Collection<Xto_Itf<?>> p_coll)
   {
      // instanciation d'une nouvelle collection
      final Collection<Dto_Itf<?>> v_newColl = (Collection<Dto_Itf<?>>) createCollectionOfSameType(p_coll);
      final Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> v_specificMapper = getSpecificMapper(p_fieldName);
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
               final Dto_Itf<?> v_dtoElement = v_specificMapper.convertXtoItfToDto(v_xtoElement);
               v_newColl.add(v_dtoElement);
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
         // On n'appelle pas le getter pour ne pas faire appel au lazy loading dans le DTO.
         // On ne veut certainement pas charger toutes les références de chacun des objets qu'on convertit alors qu'on n'utilisera pas ces données dans le XTO.
         // Si ces données sont réellement nécessaires dans le XTO, alors elles auront dû être chargées spécifiquement par les services dans le DTO.
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

      // liste les champs du dto
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
         throw new Spi4jRuntimeException("Problème de configuration des attributs entre le DTO et le XTO de "
                  + p_obj1.getClass() + " / " + p_obj2.getClass() + " : " + v_erreurs,
                  "Vérifier la configuration des attributs pour le DTO et le XTO de " + p_obj1.getClass() + " / "
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
      // v_copiable &= !p_field.getName().equals(MatchHelper.c_dtoVersionValueFieldName);

      return v_copiable;
   }

   /**
    * Retourne une instance de l'objet métier.
    * @return une nouvelle instance d'objet métier.
    */
   abstract protected TypeDto getInstanceOfDto ();

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
   protected Mapper_Itf<? extends Dto_Itf<?>, ? extends Xto_Itf<?>> getSpecificMapper (final String p_fieldName)
   {
      throw new Spi4jRuntimeException("Un mapper a été demandé pour le champ " + p_fieldName
               + " mais aucun n'a été renseigné dans " + getClass(), "Renseigner un mapper spécifique pour le champ "
               + p_fieldName + " dans " + getClass());
   }

}
