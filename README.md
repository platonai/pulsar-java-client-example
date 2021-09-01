Pulsar client example in java

Submit an X-SQL to scrape:

    String sql = "...";
    String id = driver.submit(sql, true);

Check the status of a specified scrape task:

    ScrapeResponse status = driver.findById(id);

Check our dashboard:

    Dashboard dashboard = driver.dashboard();

Download all scrape results page by page:

    Page<CompactedScrapeResponse> results = driver.download(0, 10);
