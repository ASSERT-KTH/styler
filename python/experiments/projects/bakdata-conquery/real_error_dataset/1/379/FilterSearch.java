package com.bakdata.conquery.apiv1;

import com.bakdata.conquery.models.concepts.filters.specific.AbstractSelectFilter;
import com.bakdata.conquery.models.datasets.Dataset;
import com.bakdata.conquery.models.worker.Namespaces;
import com.github.powerlibraries.io.In;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.csv.CsvParser;
import com.zigurs.karlis.utils.search.QuickSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zigurs.karlis.utils.search.QuickSearch.MergePolicy.INTERSECTION;
import static com.zigurs.karlis.utils.search.QuickSearch.UnmatchedPolicy.IGNORE;

@Slf4j
public class FilterSearch {

	/**
	 * Enum to specify a scorer function in {@link QuickSearch}. Used for resolvers in {@link AbstractSelectFilter}.
	 */
	@AllArgsConstructor
	@Getter
	public enum FilterSearchType {
		/**
		 * String must start with search-String.
		 */
		PREFIX {
			@Override
			public double score(String candidate, String keyword) {
				/* Sort ascending by length of match */
				if (candidate.startsWith(keyword))
					return 1d / (double) candidate.length();
				else
					return -1d;
			}
		},
		/**
		 * Search is contained somewhere in string.
		 */
		CONTAINS {
			@Override
			public double score(String candidate, String keyword) {
				/* 0...1 depending on the length ratio */
				double matchScore = (double) candidate.length() / (double) keyword.length();

				/* boost by 1 if matches start of keyword */
				if (keyword.startsWith(candidate))
					return matchScore + 1.0;

				return matchScore;
			}
		},
		/**
		 * Values must be exactly the same.
		 */
		EXACT {
			@Override
			public double score(String candidate, String keyword) {
				/* Only allow exact matches through (returning < 0.0 means skip this candidate) */
				return candidate.equals(keyword) ? 1.0 : -1.0;
			}
		};

		/**
		 * Search function. See {@link QuickSearch}.
		 * @param candidate potential match.
		 * @param match search String.
		 * @return Value > 0 increases relevance of string (also used for ordering results). < 0 removes string.
		 */
		public abstract double score(String candidate, String match);
	}

	private static Map<String, QuickSearch<FilterSearchItem>> search = new HashMap<>();

	public static ExecutorService init(Namespaces namespaces, Collection<Dataset> datasets) {
		ExecutorService executor = Executors.newSingleThreadExecutor();

		datasets.stream()
				.flatMap(ds -> namespaces.get(ds.getId()).getStorage().getAllConcepts().stream())
				.flatMap(c -> c.getConnectors().stream())
				.flatMap(co -> co.collectAllFilters().stream())
				.filter(f -> f instanceof AbstractSelectFilter && f.getTemplate() != null)
				.forEach(f -> executor.submit(() -> createSourceSearch(((AbstractSelectFilter<?>) f))));

		executor.shutdown();
		return executor;
	}

	public static void createSourceSearch(AbstractSelectFilter<?> filter) {
		FilterTemplate template = filter.getTemplate();

		List<String> templateColumns = new ArrayList<>(template.getColumns());
		templateColumns.add(template.getColumnValue());

		File file = new File(template.getFilePath());
		String key = String.join("_", templateColumns) + "_" + file.getName();

		QuickSearch<FilterSearchItem> quick = search.get(key);
		if (quick != null) {
			log.info("Reference list '{}' already exists ...", file.getAbsolutePath());
			filter.setSourceSearch(quick);
			return;
		}

		log.info("Processing reference list '{}' ...", file.getAbsolutePath());
		final long time = System.currentTimeMillis();

		quick = new QuickSearch.QuickSearchBuilder()
			.withUnmatchedPolicy(IGNORE)
			.withMergePolicy(INTERSECTION)
			.withKeywordMatchScorer(FilterSearchType.CONTAINS::score)
			.build();

		try {
			CsvParser parser = CsvParsing.createParser();
			IterableResult<String[], ParsingContext> it = parser.iterate(In.file(file).withUTF8().asReader());
			String[] header = it.getContext().parsedHeaders();

			for (String[] row : it) {
				FilterSearchItem item = new FilterSearchItem();

				for (int i = 0; i < header.length; i++) {
					String column = header[i];

					if (!templateColumns.contains(column)) {
						continue;
					}

					item.setLabel(template.getValue());
					item.setOptionValue(template.getOptionValue());
					item.getTemplateValues().put(column, row[i]);

					quick.addItem(item, row[i]);

					if (column.equals(template.getColumnValue())) {
						item.setValue(row[i]);
					}
				}
			}

			filter.setSourceSearch(quick);
			search.put(key, quick);
			log.info("Processed reference list '{}' in {} ms", file.getAbsolutePath(), System.currentTimeMillis() - time);
		} catch (Exception e) {
			log.error("Failed to process reference list '"+file.getAbsolutePath()+"'", e);
		}
	}
}
