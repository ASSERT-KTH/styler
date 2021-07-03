package com.asynch.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.asynch.common.Article;
import com.asynch.common.Result;
import com.asynch.common.Tuple;
import com.asynch.util.CommonUtils;


public class FutureScrapper extends CommonScrapper {

	private final List<String> urlList;
	private final ExecutorService executor;

	public FutureScrapper(final String urlFile, final ExecutorService executor)
			throws IOException {
		this.urlList = CommonUtils.getLinks(urlFile);
		this.executor = executor;
	}

	@Override
	public void process() {
		final List<Future<Result>> futureList = new ArrayList<>(10);
		for (final String url : urlList) {
			futureList.add(executor.submit(invokeCallable(url)));
		}

		futureList.stream().map(future -> {
			try {
				return future.get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}).forEach(System.out::println);
	}

	private Callable<Result> invokeCallable(final String url) {
		return () -> {
			final Tuple tuple = getPageSource(url);
			final Article article = fetchArticle(tuple);
			return getResult(article);
		};
	}
}
