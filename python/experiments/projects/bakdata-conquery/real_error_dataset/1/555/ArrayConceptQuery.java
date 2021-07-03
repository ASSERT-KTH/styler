package com.bakdata.conquery.models.query.concept;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.bakdata.conquery.ConqueryConstants;
import com.bakdata.conquery.apiv1.QueryDescription;
import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.io.jackson.InternalOnly;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.query.IQuery;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.QueryResolveContext;
import com.bakdata.conquery.models.query.Visitable;
import com.bakdata.conquery.models.query.queryplan.ArrayConceptQueryPlan;
import com.bakdata.conquery.models.query.queryplan.ConceptQueryPlan;
import com.bakdata.conquery.models.query.resultinfo.ResultInfo;
import com.bakdata.conquery.models.query.resultinfo.ResultInfoCollector;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;

/**
 * Query type that combines a set of {@link ConceptQuery}s which are separately evaluated
 * and whose results are merged. If a SpecialDateUnion is required, the result will hold
 * the union of all dates from the separate queries.
 */
@NoArgsConstructor
@Getter
@Setter
@CPSType(id = "ARRAY_CONCEPT_QUERY", base = QueryDescription.class)
@Slf4j
public class ArrayConceptQuery extends IQuery {

	@NotEmpty
	private List<ConceptQuery> childQueries = new ArrayList<>();
	@NotNull
	protected ConceptQueryPlan.DateAggregationMode dateAggregationMode;


	@InternalOnly
	protected ConceptQueryPlan.DateAggregationMode resolvedDateAggregationMode;

	public ArrayConceptQuery(@NonNull List<ConceptQuery> queries, ConceptQueryPlan.DateAggregationMode dateAggregationMode) {
		this.childQueries = queries;
		this.dateAggregationMode = dateAggregationMode;
	}
	
	public ArrayConceptQuery( List<ConceptQuery> queries) {
		this(queries, ConceptQueryPlan.DateAggregationMode.NONE);
		if(queries == null) {
			throw new IllegalArgumentException("No sub query list provided.");
		}
		this.childQueries = queries;
	}

	@Override
	public void resolve(QueryResolveContext context) {
		resolvedDateAggregationMode = dateAggregationMode;
		if(context.getDateAggregationMode() != null) {
			log.trace("Overriding date aggregation mode ({}) with mode from context ({})", dateAggregationMode, context.getDateAggregationMode());
			resolvedDateAggregationMode = context.getDateAggregationMode();
		}

		if (resolvedDateAggregationMode == null) {
			log.trace("No date aggregation mode was availiable. Falling back to MERGE");
			resolvedDateAggregationMode = ConceptQueryPlan.DateAggregationMode.MERGE;

		}
		childQueries.forEach(c -> c.resolve(context.withDateAggregationMode(resolvedDateAggregationMode)));
	}

	@Override
	public ArrayConceptQueryPlan createQueryPlan(QueryPlanContext context) {
		// Make sure the constructor and the adding is called with the same context.
		ArrayConceptQueryPlan aq = new ArrayConceptQueryPlan(!Objects.equals(resolvedDateAggregationMode, ConceptQueryPlan.DateAggregationMode.NONE));
		aq.addChildPlans(childQueries, context);
		return aq;
	}

	@Override
	public void collectRequiredQueries(Set<ManagedExecutionId> requiredQueries) {
		childQueries.forEach(q -> q.collectRequiredQueries(requiredQueries));
	}

	@Override
	public void collectResultInfos(ResultInfoCollector collector) {
		List<ResultInfo> infos = collector.getInfos();
		int lastIndex = Math.max(0,infos.size()-1);
		childQueries.forEach(q -> q.collectResultInfos(collector));
		ResultInfo dateInfo = ConqueryConstants.DATES_INFO;
		
		if(!infos.isEmpty()) {
			// Remove DateInfo from each childQuery			
			infos.subList(lastIndex, infos.size()).removeAll(List.of(dateInfo));
		}
		// Add one DateInfo for the whole Query
		collector.getInfos().add(0, dateInfo);
	}

	@Override
	public void visit(Consumer<Visitable> visitor) {
		childQueries.forEach(q -> q.visit(visitor));
	}
}
