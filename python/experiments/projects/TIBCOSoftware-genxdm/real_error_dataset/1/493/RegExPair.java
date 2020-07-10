/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.processor.w3c.xs.regex.nfa;

/**
 * Pair is an immutable class transparently
 * holding an ordered pair of objects.  "Transparent" because
 * the equals() method is solely based on the contents
 * not the container.
 * <p/>
 * Useful for using two objects together as a single
 * key for a hash map.
 * <p/>
 * Updated March 1, 2002 to support null members.
 * <p/>
 * {@link Comparable} is supported by assuming members support it too;
 * the first member has priority over the second.
 */
final class RegExPair<L,R> implements Comparable<RegExPair<L,R>>
{
    protected final L m_first;
    protected final R m_second;

    /**
     * Creates a Pair containing the two given objects.
     *
     * @param first  the first object of the pair
     * @param second the second object of the pair
     */
    public RegExPair(final L first, final R second)
    {
        m_first = first;
        m_second = second;
    }

    /**
     * Returns the first object of the pair.
     *
     * @return an Object
     */
    public L getFirst()
    {
        return m_first;
    }

    /**
     * Returns the second object of the pair.
     *
     * @return an Object
     */
    public R getSecond()
    {
        return m_second;
    }

    /**
     * Computes a hash code solely based on the contents.
     *
     * @return int hashcode
     */
    @Override
    public int hashCode()
    {
        final int hash;
        if (m_first != null)
            hash = m_first.hashCode();
        else
            hash = 499;//a nice prime number for {null, *}
        if (m_second != null)
            return (hash << 1) ^ m_second.hashCode();//assymetric
        else
            return hash;
    }

    /**
     * Returns true if the given object is a Pair
     * and its component are equal to the corresponding
     * components of this Pair.
     *
     * @param obj
     * @return true is components are equals
     */
	@Override
    public boolean equals(Object obj)
    {
        if (obj instanceof RegExPair)
        {
            RegExPair<?,?> other = (RegExPair<?,?>)obj;
            return (m_first == other.m_first
                    || (m_first != null && m_first.equals(other.m_first)))
                    && (m_second == other.m_second
                    || (m_second != null && m_second.equals(other.m_second)));
        }
        else
            return false;
    }

    public int compareTo(final RegExPair<L, R> o)
    {
        if (o == this)
        {
            return 0;
        }
        else
        {
            final int compare = nullSafeCompare(m_first, o.m_first);
            if (0 != compare)
            {
                return compare;
            }
            else
            {
                return nullSafeCompare(m_second, o.m_second);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected static <L,R> int nullSafeCompare(final L o1, R o2)
    {
        if (o1 == o2)
        {
            return 0;
        }
        if (null == o1)
        {
            return -1;//o2 is not null
        }
        else
        {
            return null == o2 ? 1 : ((Comparable)o1).compareTo(o2);
        }
    }
}
