/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import fr.spi4j.business.fetching.FetchingStrategy_Abs;

/**
 * FetchingStrategy 'MyPersonne'.
 * @author MINARM
 */
public class MyPersonneFetchingStrategy extends FetchingStrategy_Abs<Long, MyPersonneDto>
{
   private static final long serialVersionUID = 1L;
}
