/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.typed;

import org.genxdm.Model;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.nodes.TypedNodeInformer;
import org.genxdm.typed.io.SequenceHandler;

/**
 * Aspect of the data model interface providing typed access to the XQuery Data Model tree.
 * 
 * Implementation note: implementations are encouraged to override their
 * inherited (from Model) stream() method with a cast narrowing the ContentHandler
 * parameter to SequenceHandler<A>. This can permit transmission of type annotations
 * and typed values without revalidation in some cases.
 * 
 * @param <N>
 *            The node handle
 * @param <A>
 *            The atom handle
 */
public interface TypedModel<N, A> 
    extends Model<N>, TypedNodeInformer<N, A>
{
	void stream(N node, SequenceHandler<A> handler, boolean bogus)
		throws GenXDMException;
}
