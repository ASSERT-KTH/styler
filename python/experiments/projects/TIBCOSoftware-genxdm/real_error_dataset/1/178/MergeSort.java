/**
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.processor.xpath.v10;

import java.util.Comparator;

public class MergeSort
{
	private MergeSort()
	{
	}

	public static <T> void sort(final Comparator<T> cmp, final T[] src, final int off, final int len, final T[] temp, final int tempOff)
	{
		if (len <= 1)
		{
			return;
		}
		int halfLen = len / 2;
		sortCopy(cmp, src, off, halfLen, temp, tempOff);
		sortCopy(cmp, src, off + halfLen, len - halfLen, temp, tempOff + halfLen);
		merge(cmp, temp, tempOff, halfLen, len - halfLen, src, off);
	}

	private static <T> void sortCopy(final Comparator<T> cmp, final T[] src, final int off, final int len, final T[] dest, final int destOff)
	{
		if (len <= 1)
		{
			if (len != 0)
			{
				dest[destOff] = src[off];
			}
			return;
		}
		int halfLen = len / 2;
		sort(cmp, src, off, halfLen, dest, destOff);
		sort(cmp, src, off + halfLen, len - halfLen, dest, destOff + halfLen);
		merge(cmp, src, off, halfLen, len - halfLen, dest, destOff);
	}

	private static <T> void merge(Comparator<T> cmp, T[] src, int off1, int len1, int len2, T[] dest, int destOff)
	{
		int off2 = off1 + len1;
		if (len1 != 0 && len2 != 0)
		{
			for (;;)
			{
				if (cmp.compare(src[off1], src[off2]) <= 0)
				{
					dest[destOff++] = src[off1++];
					if (--len1 == 0)
						break;
				}
				else
				{
					dest[destOff++] = src[off2++];
					if (--len2 == 0)
						break;
				}
			}
		}
		for (; len1 > 0; --len1)
			dest[destOff++] = src[off1++];
		for (; len2 > 0; --len2)
			dest[destOff++] = src[off2++];
	}
}
