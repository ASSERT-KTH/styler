package com.bakdata.conquery.models.query.concept.specific;

import c10n.C10N;
import com.bakdata.conquery.internationalization.CQElementC10n;
import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.io.jackson.InternalOnly;
import com.bakdata.conquery.models.externalservice.ResultType;
import com.bakdata.conquery.models.identifiable.ids.specific.ManagedExecutionId;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.QueryResolveContext;
import com.bakdata.conquery.models.query.Visitable;
import com.bakdata.conquery.models.query.concept.CQElement;
import com.bakdata.conquery.models.query.queryplan.ConceptQueryPlan;
import com.bakdata.conquery.models.query.queryplan.DateAggregationAction;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.ExistsAggregator;
import com.bakdata.conquery.models.query.queryplan.specific.AndNode;
import com.bakdata.conquery.models.query.resultinfo.LocalizedSimpleResultInfo;
import com.bakdata.conquery.models.query.resultinfo.ResultInfoCollector;
import com.bakdata.conquery.util.QueryUtils;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

@CPSType(id = "AND", base = CQElement.class)
public class CQAnd extends CQElement implements ForcedExists{

	@Getter
	@Setter
	@NotEmpty
	@Valid
	private List<CQElement> children;

	@Getter @Setter
	boolean createExists = false;

	@InternalOnly
	@Getter @Setter
	private DateAggregationAction dateAction;

	@Override
	public QPNode createQueryPlan(QueryPlanContext context, ConceptQueryPlan plan) {
		Preconditions.checkNotNull(dateAction);

		QPNode[] nodes = new QPNode[children.size()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = children.get(i).createQueryPlan(context, plan);
		}


		final QPNode node = AndNode.of(Arrays.asList(nodes), dateAction);

		if (createExists) {
			final ExistsAggregator existsAggregator = new ExistsAggregator(node.collectRequiredTables());
			existsAggregator.setReference(node);
			plan.addAggregator(existsAggregator);
		}


		return node;
	}

	@Override
	public void collectRequiredQueries(Set<ManagedExecutionId> requiredQueries) {
		for (CQElement c : children) {
			c.collectRequiredQueries(requiredQueries);
		}
	}

	@Override
	public void resolve(QueryResolveContext context) {
		Preconditions.checkNotNull(context.getDateAggregationMode());

		switch(context.getDateAggregationMode()) {
			case NONE:
				dateAction = DateAggregationAction.BLOCK;
				break;
			case MERGE:
				dateAction = DateAggregationAction.MERGE;
				break;
			case LOGICAL:
			case INTERSECT:
				dateAction = DateAggregationAction.INTERSECT;
				break;
			default:
				throw new IllegalStateException("Cannot handle mode " + context.getDateAggregationMode());
		}
		children.forEach(c->c.resolve(context));
	}

	@Override
	public void collectResultInfos(ResultInfoCollector collector) {
		for (CQElement c : children) {
			c.collectResultInfos(collector);
		}

		if(createExists){
			collector.add(new LocalizedSimpleResultInfo(this::getLabel, ResultType.BooleanT.INSTANCE));
		}
	}

	@Override
	public String getLabel(Locale locale) {
		String label = super.getLabel(locale);
		if (label != null) {
			return label;
		}

		return QueryUtils.createDefaultMultiLabel(children, " " + C10N.get(CQElementC10n.class, locale).and() + " ", locale);
	}

	@Override
	public void visit(Consumer<Visitable> visitor) {
		super.visit(visitor);
		for (CQElement c : children) {
			c.visit(visitor);
		}
	}
}
