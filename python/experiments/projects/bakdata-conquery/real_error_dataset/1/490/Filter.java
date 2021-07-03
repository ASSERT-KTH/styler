package com.bakdata.conquery.models.concepts.filters;

import com.bakdata.conquery.io.cps.CPSBase;
import com.bakdata.conquery.models.api.description.FEFilter;
import com.bakdata.conquery.models.concepts.Connector;
import com.bakdata.conquery.models.datasets.Column;
import com.bakdata.conquery.models.datasets.Import;
import com.bakdata.conquery.models.exceptions.ConceptConfigurationException;
import com.bakdata.conquery.models.identifiable.Labeled;
import com.bakdata.conquery.models.identifiable.ids.specific.FilterId;
import com.bakdata.conquery.models.query.queryplan.filter.FilterNode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class is the abstract superclass for all filters.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
@CPSBase
public abstract class Filter<FE_TYPE> extends Labeled<FilterId> {

	private String unit;
	private String description;
	@JsonBackReference
	private Connector connector;
	private String pattern;
	private Boolean allowDropFile;

	public abstract void configureFrontend(FEFilter f) throws ConceptConfigurationException;

	@JsonIgnore
	public abstract Column[] getRequiredColumns();

	public final boolean requiresColumn(Column c) {
		return ArrayUtils.contains(getRequiredColumns(), c);
	}

	public abstract FilterNode createAggregator(FE_TYPE filterValue);//, QueryPlanContext ctx);

	@Override
	public FilterId createId() {
		return new FilterId(connector.getId(), getName());
	}

	/**
	 * This method is called once at startup or if the dataset changes for each new import that
	 * concerns this filter. Use this to collect metadata from the import. It is not guaranteed that
	 * any blocks or cBlocks exist at this time. Any data created by this method should be volatile
	 * and @JsonIgnore.
	 * @param imp the import added
	 */
	public void addImport(Import imp) {}
}
